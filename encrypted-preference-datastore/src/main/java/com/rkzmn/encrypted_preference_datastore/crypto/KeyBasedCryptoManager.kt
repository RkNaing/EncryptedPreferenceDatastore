package com.rkzmn.encrypted_preference_datastore.crypto

import java.io.InputStream
import java.io.OutputStream
import javax.crypto.spec.SecretKeySpec

internal class KeyBasedCryptoManager(key: ByteArray) : CipherCryptoManager() {

    private val keySpec = SecretKeySpec(
        key.take(KEY_SIZE).toByteArray(),
        ALGORITHM
    )

    override fun encrypt(bytes: ByteArray, outputStream: OutputStream) {
        encrypt(
            outputStream = outputStream,
            bytes = bytes,
            transformation = TRANSFORMATION,
            key = keySpec
        )
    }

    override fun decrypt(inputStream: InputStream): ByteArray {
        return decrypt(
            inputStream = inputStream,
            transformation = TRANSFORMATION,
            key = keySpec
        )
    }

    companion object {
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
        private const val KEY_SIZE = 16
    }
}