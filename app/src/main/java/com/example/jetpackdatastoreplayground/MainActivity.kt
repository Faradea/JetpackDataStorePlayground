package com.example.jetpackdatastoreplayground

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
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

    private var count: Int = 0

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
                val isChecked = preferences[DataStoreSettings.PreferencesKeys.IS_CHECKED]?: false
                val count = preferences[DataStoreSettings.PreferencesKeys.COUNT]?: 0
                DataStoreSettings.UserPreferences(isChecked = isChecked, count = count)
            }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val checkBox = findViewById<CheckBox>(R.id.checkBox)
        val button = findViewById<Button>(R.id.button)
        val countText = findViewById<TextView>(R.id.textView)

        userPreferencesFlow.asLiveData().observe(this, {
            checkBox.isChecked = it.isChecked
            count = it.count
            countText.text = count.toString()
        })

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                updateIsChecked(isChecked)
            }
        }

        button.setOnClickListener {
            lifecycleScope.launch {
                updateCount(count + 1)
            }
        }
    }

    private suspend fun updateIsChecked(isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[DataStoreSettings.PreferencesKeys.IS_CHECKED] = isChecked
        }
    }

    private suspend fun updateCount(newValue: Int) {
        dataStore.edit { preferences ->
            preferences[DataStoreSettings.PreferencesKeys.COUNT] = newValue
        }
    }
}