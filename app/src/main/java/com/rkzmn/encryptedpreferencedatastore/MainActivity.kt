package com.rkzmn.encryptedpreferencedatastore

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rkzmn.encrypted_preference_datastore.encryptedPreferenceDataStore
import com.rkzmn.encrypted_preference_datastore.watchValueWithDefault
import com.rkzmn.encryptedpreferencedatastore.ui.theme.EncryptedPreferenceDataStoreTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val Context.dataStore by encryptedPreferenceDataStore(
        fileName = "user-settings"
    )

    private val userNameKey = stringPreferencesKey("name")
    private val passwordKey = stringPreferencesKey("password")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EncryptedPreferenceDataStoreTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EncryptedPreferenceDatastoreDemoUI()
                }
            }
        }
    }

    @Composable
    fun EncryptedPreferenceDatastoreDemoUI() {
        var username by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }

        val savedUsername by userNameKey.watchValueWithDefault(
            dataStore = dataStore,
            defaultValue = ""
        ).collectAsState(initial = "")

        val savedPassword by passwordKey.watchValueWithDefault(
            dataStore = dataStore,
            defaultValue = ""
        ).collectAsState(initial = "")

        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Username") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Password") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Button(onClick = {
                    scope.launch {
                        dataStore.edit {
                            it[userNameKey] = username
                            it[passwordKey] = password
                        }
                    }
                }) {
                    Text(text = "Save")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    scope.launch {
                        val currentValue = dataStore.data.first()
                        username = currentValue[userNameKey].orEmpty()
                        password = currentValue[passwordKey].orEmpty()
                    }
                }) {
                    Text(text = "Load")
                }
            }
            Text(text = "Saved $savedUsername : $savedPassword")
        }
    }
}

