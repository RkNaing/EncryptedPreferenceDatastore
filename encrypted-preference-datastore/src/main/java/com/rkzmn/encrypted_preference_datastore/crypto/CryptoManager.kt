package com.rkzmn.encrypted_preference_datastore.crypto

import java.io.InputStream
import java.io.OutputStream

internal interface CryptoManager {
    fun encrypt(bytes: ByteArray, outputStream: OutputStream)

    fun decrypt(inputStream: InputStream): ByteArray
}