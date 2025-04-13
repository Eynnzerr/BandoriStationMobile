package com.eynnzerr.bandoristation.utils

import com.eynnzerr.bandoristation.model.ApiResponseContent
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object ResponseContentSerializer : KSerializer<ApiResponseContent> {
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor("ApiResponseContent", SerialKind.CONTEXTUAL)

    override fun deserialize(decoder: Decoder): ApiResponseContent {
        val jsonDecoder = decoder as? JsonDecoder ?: throw SerializationException("Expected JsonDecoder")
        val element = jsonDecoder.decodeJsonElement()

        return when {
            element is JsonPrimitive && element.isString -> ApiResponseContent.StringContent(element.content)
            element is JsonObject -> ApiResponseContent.ObjectContent(element)
            else -> throw SerializationException("Unknown response format")
        }
    }

    override fun serialize(encoder: Encoder, value: ApiResponseContent) {
        val jsonEncoder = encoder as? JsonEncoder ?: throw SerializationException("Expected JsonEncoder")

        when (value) {
            is ApiResponseContent.StringContent -> jsonEncoder.encodeString(value.text)
            is ApiResponseContent.ObjectContent -> jsonEncoder.encodeJsonElement(value.data)
        }
    }
}
