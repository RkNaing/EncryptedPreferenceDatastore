package com.rkzmn.encryptedpreferencedatastore.datastore

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PreferenceValues(
    @SerialName("integers") val integers: Map<String, Int> = mapOf(),
    @SerialName("doubles") val doubles: Map<String, Double> = mapOf(),
    @SerialName("strings") val strings: Map<String, String> = mapOf(),
    @SerialName("booleans") val booleans: Map<String, Boolean> = mapOf(),
    @SerialName("floats") val floats: Map<String, Float> = mapOf(),
    @SerialName("longs") val longs: Map<String, Long> = mapOf(),
    @SerialName("stringSets") val stringSets: Map<String, Set<String>> = mapOf(),
    @SerialName("byteArrays") val byteArrays: Map<String, ByteArray> = mapOf()
) {

    class Builder {

        private val integers: MutableMap<String, Int> = mutableMapOf()
        private val doubles: MutableMap<String, Double> = mutableMapOf()
        private val strings: MutableMap<String, String> = mutableMapOf()
        private val booleans: MutableMap<String, Boolean> = mutableMapOf()
        private val floats: MutableMap<String, Float> = mutableMapOf()
        private val longs: MutableMap<String, Long> = mutableMapOf()
        private val stringSets: MutableMap<String, Set<String>> = mutableMapOf()
        private val byteArrays: MutableMap<String, ByteArray> = mutableMapOf()

        fun putInt(key: String, value: Int) {
            integers[key] = value
        }

        fun putDouble(key: String, value: Double) {
            doubles[key] = value
        }

        fun putString(key: String, value: String) {
            strings[key] = value
        }

        fun putBoolean(key: String, value: Boolean) {
            booleans[key] = value
        }

        fun putFloat(key: String, value: Float) {
            floats[key] = value
        }

        fun putLong(key: String, value: Long) {
            longs[key] = value
        }

        fun putStringSet(key: String, value: Set<String>) {
            stringSets[key] = value
        }

        fun putByteArray(key: String, value: ByteArray) {
            byteArrays[key] = value
        }

        fun build(): PreferenceValues {
            return PreferenceValues(
                integers = integers.toMap(),
                doubles = doubles.toMap(),
                strings = strings.toMap(),
                booleans = booleans.toMap(),
                floats = floats.toMap(),
                longs = longs.toMap(),
                stringSets = stringSets.toMap(),
                byteArrays = byteArrays.toMap()
            )
        }

    }

}
