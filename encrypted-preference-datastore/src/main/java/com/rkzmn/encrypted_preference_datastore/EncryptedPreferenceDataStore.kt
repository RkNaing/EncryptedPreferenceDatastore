package com.rkzmn.encrypted_preference_datastore

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.GuardedBy
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.rkzmn.encrypted_preference_datastore.crypto.AndroidKeyStoreCryptoManager
import com.rkzmn.encrypted_preference_datastore.crypto.KeyBasedCryptoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toPath
import java.security.MessageDigest
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Create and returns an encrypted version of [preferencesDataStore]
 */
fun encryptedPreferenceDataStore(
    fileName: String,
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>? = null,
    produceMigrations: (Context) -> List<DataMigration<Preferences>> = { listOf() },
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
): ReadOnlyProperty<Context, DataStore<Preferences>> = EncryptedDataStoreSingletonDelegate(
    fileName = "${fileName}.json",
    corruptionHandler = corruptionHandler,
    produceMigrations = produceMigrations,
    scope = scope
)

private class EncryptedDataStoreSingletonDelegate(
    private val fileName: String,
    private val corruptionHandler: ReplaceFileCorruptionHandler<Preferences>?,
    private val produceMigrations: (Context) -> List<DataMigration<Preferences>>,
    private val scope: CoroutineScope
) : ReadOnlyProperty<Context, DataStore<Preferences>> {

    private val lock = Any()

    @GuardedBy("lock")
    @Volatile
    private var instance: DataStore<Preferences>? = null

    /**
     * Gets the instance of the DataStore.
     *
     * @param thisRef must be an instance of [Context]
     * @param property not used
     */
    override fun getValue(thisRef: Context, property: KProperty<*>): DataStore<Preferences> {
        return instance ?: synchronized(lock) {
            if (instance == null) {
                val applicationContext = thisRef.applicationContext
                val serializer = createSerializer(context = applicationContext)
                instance = DataStoreFactory.create(
                    storage = OkioStorage(FileSystem.SYSTEM, OkioSerializerWrapper(serializer)) {
                        applicationContext.dataStoreFile(fileName).absolutePath.toPath()
                    },
                    corruptionHandler = corruptionHandler,
                    migrations = produceMigrations(applicationContext),
                    scope = scope
                )
            }
            instance!!
        }
    }

    private fun createSerializer(context: Context): Serializer<Preferences> {
        val cryptoManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AndroidKeyStoreCryptoManager()
        } else {
            KeyBasedCryptoManager(key = getSignature(context))
        }
        return EncryptedPreferenceSerializer(cryptoManager = cryptoManager)
    }

    @Suppress("DEPRECATION")
    private fun getSignature(context: Context): ByteArray = with(context) {
        val md: MessageDigest = MessageDigest.getInstance("SHA")

        val rawBytes = runCatching {
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            val signatures = packageInfo?.signatures
            when {
                packageInfo == null -> packageName.toByteArray()
                signatures.isNullOrEmpty() -> packageInfo.packageName.toByteArray()
                else -> signatures.first().toByteArray()
            }
        }.getOrElse { packageName.toByteArray() }

        md.digest(rawBytes)
    }
}

private class OkioSerializerWrapper<T>(
    private val delegate: Serializer<T>
) : OkioSerializer<T> {
    override val defaultValue: T
        get() = delegate.defaultValue

    override suspend fun readFrom(source: BufferedSource): T {
        return delegate.readFrom(source.inputStream())
    }

    override suspend fun writeTo(t: T, sink: BufferedSink) {
        delegate.writeTo(t, sink.outputStream())
    }
}