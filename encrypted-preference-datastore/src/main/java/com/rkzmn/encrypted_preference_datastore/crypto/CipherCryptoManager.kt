package com.rkzmn.encrypted_preference_datastore.crypto

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

abstract class CipherCryptoManager : CryptoManager {
    protected fun encrypt(
        outputStream: OutputStream,
        bytes: ByteArray,
        transformation: String,
        key: Key
    ) = outputStream.use { stream ->
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val blockSize = cipher.blockSize
        val iv = cipher.iv

        stream.write(iv.size)
        stream.write(iv)

        var offset = 0
        while (offset < bytes.size) {
            val blockSizeToWrite = blockSize.coerceAtMost(bytes.size - offset)
            val encryptedBytes = cipher.update(bytes, offset, blockSizeToWrite)
            if (encryptedBytes != null) {
                stream.write(encryptedBytes)
            }
            offset += blockSizeToWrite
        }

        val finalEncryptedBytes = cipher.doFinal()
        stream.write(finalEncryptedBytes)
    }

    protected fun decrypt(
        inputStream: InputStream,
        transformation: String,
        key: Key
    ): ByteArray = inputStream.use { stream ->
        val ivSize = stream.read()
        val iv = if (ivSize > 0) ByteArray(ivSize) else byteArrayOf()
        stream.read(iv)

        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))

        val blockSize = cipher.blockSize
        val buffer = ByteArray(blockSize)
        val byteArrayOutputStream = ByteArrayOutputStream()

        byteArrayOutputStream.use { outputStream ->
            var bytesRead = stream.read(buffer)
            while (bytesRead != -1) {
                val decryptedBytes = cipher.update(buffer, 0, bytesRead)
                if (decryptedBytes != null) {
                    outputStream.write(decryptedBytes)
                }
                bytesRead = stream.read(buffer)
            }

            val finalDecryptedBytes = cipher.doFinal()
            outputStream.write(finalDecryptedBytes)
            outputStream.toByteArray()
        }
    }
}