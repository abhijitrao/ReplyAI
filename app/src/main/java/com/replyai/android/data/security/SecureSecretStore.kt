package com.replyai.android.data.security

import android.content.Context
import android.util.Base64
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties

class SecureSecretStore(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun get(key: String): String? {
        val encodedValue = preferences.getString(key, null) ?: return null
        return runCatching {
            decrypt(encodedValue)
        }.getOrNull()
    }

    fun put(key: String, value: String) {
        val encryptedValue = encrypt(value)
        preferences.edit().putString(key, encryptedValue).apply()
    }

    fun remove(key: String) {
        preferences.edit().remove(key).apply()
    }

    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())

        val encrypted = cipher.doFinal(value.toByteArray(StandardCharsets.UTF_8))
        val iv = cipher.iv

        return Base64.encodeToString(
            iv + encrypted,
            Base64.NO_WRAP
        )
    }

    private fun decrypt(encodedValue: String): String {
        val payload = Base64.decode(encodedValue, Base64.NO_WRAP)
        require(payload.size > GCM_IV_LENGTH_BYTES) { "Invalid encrypted value" }

        val iv = payload.copyOfRange(0, GCM_IV_LENGTH_BYTES)
        val encrypted = payload.copyOfRange(GCM_IV_LENGTH_BYTES, payload.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateSecretKey(),
            GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
        )

        return String(cipher.doFinal(encrypted), StandardCharsets.UTF_8)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = java.security.KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }

        keyStore.getKey(KEY_ALIAS, null)?.let { return it as SecretKey }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )

        return keyGenerator.generateKey()
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "replyai_secure_secrets"
        const val PREFERENCES_NAME = "replyai_secure_secrets"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val GCM_IV_LENGTH_BYTES = 12
        const val GCM_TAG_LENGTH_BITS = 128
    }
}
