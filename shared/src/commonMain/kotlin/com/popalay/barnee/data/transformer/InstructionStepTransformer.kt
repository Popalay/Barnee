package com.popalay.barnee.data.transformer

import com.popalay.barnee.data.model.InstructionStep
import com.popalay.barnee.util.toIntIfInt
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure

object InstructionStepTransformer : KSerializer<InstructionStep> {
    private val regex by lazy { "[\\[][\\w ,'-]+[|][\\w]+[|][\\w\\W]{8}-[\\w\\W]{4}-[\\w\\W]{4}-[\\w\\W]{4}-[\\w\\W]{12}+[]]".toRegex() }

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("InstructionStep") {
        element("text", String.serializer().descriptor)
        element("milliliters", Double.serializer().descriptor)
    }

    override fun deserialize(decoder: Decoder): InstructionStep = decoder.decodeStructure(descriptor) {
        var text = ""
        var milliliters = 0.0
        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> text = decodeStringElement(descriptor, 0)
                1 -> milliliters = decodeDoubleElement(descriptor, 1)
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }
        val displayText = text.replace(regex) {
            it.groups.firstOrNull()?.value.orEmpty().substringAfter("[").substringBefore("|")
        }.removeSuffix(".") + milliliters.takeIf { it > 0 }?.let { " (${it.toIntIfInt()}ml)" }.orEmpty()

        InstructionStep(displayText)
    }

    override fun serialize(encoder: Encoder, value: InstructionStep) {
        error("Serialization is not supported")
    }
}