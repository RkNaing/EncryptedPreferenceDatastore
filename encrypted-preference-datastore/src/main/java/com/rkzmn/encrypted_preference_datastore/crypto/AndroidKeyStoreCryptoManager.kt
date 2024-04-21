package com.rkzmn.encrypted_preference_datastore.crypto

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

// Ref: http://tinyurl.com/crypto-manager
@RequiresApi(Build.VERSION_CODES.M)
internal class AndroidKeyStoreCryptoManager : CipherCryptoManager() {
    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
        load(null)
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        val spec = KeyGenParameterSpec.Builder(KEY_ALIAS, purposes)
            .setBlockModes(BLOCK_MODE)
            .setEncryptionPaddings(PADDING)
            .setUserAuthenticationRequired(false)
            .setRandomizedEncryptionRequired(true)
            .build()


        return KeyGenerator.getInstance(ALGORITHM).apply { init(spec) }.generateKey()
    }

    override fun encrypt(bytes: ByteArray, outputStream: OutputStream) {
        encrypt(
            outputStream = outputStream,
            bytes = bytes,
            transformation = TRANSFORMATION,
            key = getKey()
        )
    }

    override fun decrypt(inputStream: InputStream): ByteArray {
        return decrypt(
            inputStream = inputStream,
            transformation = TRANSFORMATION,
            key = getKey()
        )
    }

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }
}
