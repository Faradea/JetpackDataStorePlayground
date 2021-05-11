package com.example.jetpackdatastoreplayground

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.datastore.core.DataStore
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.example.jetpackdatastoreplayground.proto.UserPreferencesSerializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.datastore.dataStore

class MainActivity : AppCompatActivity() {

    private var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val checkBox = findViewById<CheckBox>(R.id.checkBox)
        val button = findViewById<Button>(R.id.button)
        val countText = findViewById<TextView>(R.id.textView)

        val protoIsCheckedFlow: Flow<Boolean> = this.applicationContext.protoDataStore.data
            .map { it.isCheckedProto }

        val protoCountFlow: Flow<Int> = this.applicationContext.protoDataStore.data
                .map { it.count }

        protoIsCheckedFlow.asLiveData().observe(this) {
            checkBox.isChecked = it
        }

        protoCountFlow.asLiveData().observe(this) {
            count = it
            countText.text = it.toString()
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                protoDataStore.updateData { currentUserPreferences ->
                    currentUserPreferences
                        .toBuilder()
                        .setIsCheckedProto(isChecked)
                        .build()
                }
            }
        }

        button.setOnClickListener {
            count++
            lifecycleScope.launch {
                protoDataStore.updateData { currentUserPreferences ->
                    currentUserPreferences
                            .toBuilder()
                            .setCount(count)
                            .build()
                }
            }
        }
    }
}

private val Context.protoDataStore: DataStore<UserPreferences> by dataStore(
        fileName = "user_preferences.pb",
        serializer = UserPreferencesSerializer,
)
