package com.rkzmn.encrypted_preference_datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

internal fun Preferences.asPreferenceValues(): PreferenceValues {
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

internal fun PreferenceValues.asPreference(): Preferences {
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