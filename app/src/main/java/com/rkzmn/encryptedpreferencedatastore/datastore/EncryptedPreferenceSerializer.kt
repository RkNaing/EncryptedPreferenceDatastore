package com.rkzmn.encryptedpreferencedatastore.datastore

import android.util.Log
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.rkzmn.encryptedpreferencedatastore.encryption.CryptoManager
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object EncryptedPreferenceSerializer : Serializer<Preferences> {

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

    private fun Preferences.asPreferenceValues(): PreferenceValues {
        val builder = PreferenceValues.Builder()
        asMap().forEach { (key, value) ->
            val keyName = key.name
            when (value) {
                is Int -> builder.putInt(key = keyName, value = value)
                is Double -> builder.putDouble(key = keyName, value = value)
                is String -> builder.putString(key = keyName, value = value)
                is Boolean -> builder.putBoolean(key = keyName, value = value)
                is Float -> builder.putFloat(key = keyName, value = value)
                is Long -> builder.putLong(key = keyName, value = value)
                is ByteArray -> builder.putByteArray(key = keyName, value = value)
                is Set<*> -> {
                    if (value.all { it is String }) {
                        @Suppress("UNCHECKED_CAST")
                        builder.putStringSet(key = keyName, value = value as Set<String>)
                    } else {
                        throw IllegalArgumentException("Set $keyName contains non-String elements.")
                    }
                }

                else -> {
                    val type = value.javaClass.name
                    throw IllegalArgumentException(
                        "EncryptedPreferenceSerializer does not support type: $type"
                    )
                }
            }
        }

        return builder.build()
    }

    private fun PreferenceValues.asPreference(): Preferences {
        val preferences = mutablePreferencesOf()

        integers.forEach { (key, value) ->
            preferences[intPreferencesKey(key)] = value
        }

        doubles.forEach { (key, value) ->
            preferences[doublePreferencesKey(key)] = value
        }

        strings.forEach { (key, value) ->
            preferences[stringPreferencesKey(key)] = value
        }

        booleans.forEach { (key, value) ->
            preferences[booleanPreferencesKey(key)] = value
        }

        floats.forEach { (key, value) ->
            preferences[floatPreferencesKey(key)] = value
        }

        longs.forEach { (key, value) ->
            preferences[longPreferencesKey(key)] = value
        }

        stringSets.forEach { (key, value) ->
            preferences[stringSetPreferencesKey(key)] = value
        }

        byteArrays.forEach { (key, value) ->
            preferences[byteArrayPreferencesKey(key)] = value
        }

        return preferences.toPreferences()
    }
}