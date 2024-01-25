package com.rkzmn.encryptedpreferencedatastore.datastore

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.properties.ReadOnlyProperty

/**
 * Create and returns an encrypted version of [preferencesDataStore]
 */
fun encryptedPreferenceDataStore(
    fileName: String,
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>? = null,
    produceMigrations: (Context) -> List<DataMigration<Preferences>> = { listOf() },
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
): ReadOnlyProperty<Context, DataStore<Preferences>> = dataStore(
    fileName = "${fileName}.json",
    corruptionHandler = corruptionHandler,
    serializer = EncryptedPreferenceSerializer,
    produceMigrations = produceMigrations,
    scope = scope
)