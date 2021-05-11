package com.example.jetpackdatastoreplayground.proto

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.jetpackdatastoreplayground.UserPreferences
import com.example.jetpackdatastoreplayground.UserPreferencesOrBuilder
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.*
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer: Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences {
            try {
                return UserPreferences.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            }
    }

    override suspend fun writeTo(
        t: UserPreferences,
        output: OutputStream
    ) = t.writeTo(output)
}

