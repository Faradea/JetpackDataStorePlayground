package com.example.jetpackdatastoreplayground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val dataStore: DataStore<Preferences> =
            this.createDataStore(name = "userPrefs")

    private val userPreferencesFlow: Flow<DataStoreSettings.UserPreferences> = dataStore.data
            .catch { exception ->
                // dataStore.data throws an IOException when an error is encountered when reading data
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                // Get our show completed value, defaulting to false if not set:
                val showCompleted = preferences[DataStoreSettings.PreferencesKeys.IS_CHECKED]?: false
                DataStoreSettings.UserPreferences(showCompleted)
            }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val checkBox = findViewById<CheckBox>(R.id.checkBox)

        userPreferencesFlow.asLiveData().observe(this, {
            checkBox.isChecked = it.isChecked
        })

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                updateIsChecked(isChecked)
            }
        }
    }

    private suspend fun updateIsChecked(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[DataStoreSettings.PreferencesKeys.IS_CHECKED] = isChecked
        }
    }
}