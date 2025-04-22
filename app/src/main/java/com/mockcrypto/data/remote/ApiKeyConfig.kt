package com.mockcrypto.data.remote

import android.content.Context
import java.util.Properties
import java.io.FileInputStream
import java.io.IOException

object ApiKeyConfig {
    private const val LOCAL_PROPERTIES_FILENAME = "local.properties"
    private const val COINGECKO_API_KEY = "coingecko.api.key"
    
    /**
     * Gets the CoinGecko API key from local.properties
     * Add the following line to your local.properties file:
     * coingecko.api.key=YOUR-ACTUAL-API-KEY
     */
    fun getCoinGeckoApiKey(context: Context): String {
        return try {
            // Look for the properties file in the app's files directory
            val properties = Properties()
            val inputStream = FileInputStream(context.filesDir.resolve(LOCAL_PROPERTIES_FILENAME))
            properties.load(inputStream)
            properties.getProperty(COINGECKO_API_KEY) ?: ""
        } catch (e: IOException) {
            // If not found in app files, try to find it in the project root (development environment)
            try {
                val properties = Properties()
                val inputStream = context.assets.open(LOCAL_PROPERTIES_FILENAME)
                properties.load(inputStream)
                properties.getProperty(COINGECKO_API_KEY) ?: ""
            } catch (e: IOException) {
                ""
            }
        }
    }
} 