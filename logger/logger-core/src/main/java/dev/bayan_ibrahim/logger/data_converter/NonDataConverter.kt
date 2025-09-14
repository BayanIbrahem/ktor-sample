package dev.bayan_ibrahim.logger.data_converter

/**
 * don't convert the data, just keep the same data
 */
object NonDataConverter : DataConverter {
    override fun convert(rawData: String): String = rawData
    override fun unconvert(convertedData: String): String = convertedData
}