package dev.bayan_ibrahim.logger.data_converter

/**
 * composite data converter apply [baseConverter] **BEFORE** the current converter
 */
abstract class CompositeDataConverter : DataConverter {
    protected open val baseConverter: DataConverter? = null
    final override fun convert(rawData: String): String {
        return innerConvert(
            rawData = baseConverter?.convert(rawData) ?: rawData
        )
    }

    final override fun unconvert(convertedData: String): String {
        return innerUnConvert(
            rawData = baseConverter?.unconvert(convertedData) ?: convertedData
        )
    }

    protected abstract fun innerConvert(rawData: String): String
    protected abstract fun innerUnConvert(rawData: String): String
}

