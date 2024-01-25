## Encrypted Preference Data Store

The encrypted version preference data store with exact same API and usage as the
preference datastore, just the way as SharedPreferences and EncryptedSharedPreferences.

~~~
    private val Context.dataStore by encryptedPreferenceDataStore(
        fileName = "user-settings"
    )
~~~

[PreferenceDataStoreExt](/app/src/main/java/com/rkzmn/encryptedpreferencedatastore/datastore/PreferenceDataStoreExt.kt)
contains shorthand data store preference extensions to ease observing, getting and updating the preference.  
