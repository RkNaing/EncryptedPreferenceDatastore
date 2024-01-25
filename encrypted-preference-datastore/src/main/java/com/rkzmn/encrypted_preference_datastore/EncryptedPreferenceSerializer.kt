package com.rkzmn.encrypted_preference_datastore

import android.util.Log
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

internal object EncryptedPreferenceSerializer : Serializer<Preferences> {

    private val cryptoManager: CryptoManager = CryptoManager()

    override val defaultValue: Preferences = emptyPreferences()

    override suspend fun readFrom(input: InputStream): Preferences {
        val decryptedBytes = cryptoManager.decrypt(input)
        val decryptedJsonString = decryptedBytes.decodeToString()
        Log.d("RK", "readFrom: $decryptedJsonString")
        return try {
            Json.decodeFromString<PreferenceValues>(decryptedJsonString).asPreference()
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: Preferences, output: OutputStream) {
        val jsonString = Json.encodeToString(value = t.asPreferenceValues())
        Log.d("RK", "writeTo: $jsonString")
        val bytes = jsonString.encodeToByteArray()
        cryptoManager.encrypt(bytes, output)
    }


}