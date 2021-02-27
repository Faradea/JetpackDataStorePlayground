package com.example.jetpackdatastoreplayground

import androidx.datastore.preferences.core.booleanPreferencesKey

object DataStoreSettings {

    object PreferencesKeys {
        val IS_CHECKED = booleanPreferencesKey("is_checked")
    }

    data class UserPreferences(val isChecked: Boolean)

}