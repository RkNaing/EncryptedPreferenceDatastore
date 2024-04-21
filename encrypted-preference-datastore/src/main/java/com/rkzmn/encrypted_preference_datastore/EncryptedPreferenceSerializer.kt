package com.rkzmn.encrypted_preference_datastore

import androidx.datastore.core.Serializer
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.rkzmn.encrypted_preference_datastore.crypto.CryptoManager
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

internal class EncryptedPreferenceSerializer(
    private val cryptoManager: CryptoManager
) : Serializer<Preferences> {

    override val defaultValue: Preferences = emptyPreferences()

    override suspend fun readFrom(input: InputStream): Preferences {
        val decryptedBytes = cryptoManager.decrypt(input)
        val decryptedJsonString = decryptedBytes.decodeToString()
        return try {
            Json.decodeFromString<PreferenceValues>(decryptedJsonString).asPreference()
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: Preferences, output: OutputStream) {
        val jsonString = Json.encodeToString(value = t.asPreferenceValues())
        val bytes = jsonString.encodeToByteArray()
        cryptoManager.encrypt(bytes, output)
    }

}