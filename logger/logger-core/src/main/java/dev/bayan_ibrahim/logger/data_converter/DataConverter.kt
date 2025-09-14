package dev.bayan_ibrahim.logger.data_converter

/**
 * [convert] and [unconvert] data like compression, encryption, and other stuff
 */
interface DataConverter {
    fun convert(rawData: String): String
    fun unconvert(convertedData: String): String
}