## Encrypted Preference Data Store

[![Release](https://jitpack.io/v/RkNaing/EncryptedPreferenceDatastore.svg)]
(https://jitpack.io/#rknaing/EncryptedPreferenceDatastore)


The encrypted version preference data store with exact same API and usage as the
preference datastore, just the way as SharedPreferences and EncryptedSharedPreferences.

~~~
    private val Context.dataStore by encryptedPreferenceDataStore(
        fileName = "user-settings"
    )
~~~

[PreferenceDataStoreExt](/app/src/main/java/com/rkzmn/encryptedpreferencedatastore/datastore/PreferenceDataStoreExt.kt)
contains shorthand data store preference extensions to ease observing, getting and updating the preference.

### Install

**Step 1** : Register jipack maven into project's root `setting.gradle.kts`.

~~~
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
~~~

**Step 2** : Add dependency to the module's `build.gradle.kts`. Replace version with the [latest verion number](https://jitpack.io/p/rknaing/encryptedpreferencedatastore).

~~~
dependencies {

    implementation("com.github.rknaing:encryptedpreferencedatastore:version")

}
~~~

