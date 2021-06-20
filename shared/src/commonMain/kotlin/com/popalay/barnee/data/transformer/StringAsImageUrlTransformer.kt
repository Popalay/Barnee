package com.popalay.barnee.data.transformer

import com.popalay.barnee.data.model.EmptyImageUrl
import com.popalay.barnee.data.model.ImageUrl
import com.popalay.barnee.data.model.toImageUrl
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object StringAsImageUrlTransformer : KSerializer<ImageUrl> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("StringAsImageUrlTransformer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ImageUrl) {
        throw UnsupportedOperationException()
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder) =
        if (decoder.decodeNotNullMark()) {
            decoder.decodeString().toImageUrl()
        } else {
            EmptyImageUrl
        }
}
