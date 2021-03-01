package com.example.jetpackdatastoreplayground

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object MyDataStore {

    object PreferencesKeys {
        val IS_CHECKED = booleanPreferencesKey("is_checked")
        val COUNT = intPreferencesKey("count")
    }

    data class UserPreferences(val isChecked: Boolean, val count: Int)

}