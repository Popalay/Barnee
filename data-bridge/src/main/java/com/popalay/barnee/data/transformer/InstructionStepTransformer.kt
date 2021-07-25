/*
 * Copyright (c) 2021 Denys Nykyforov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.popalay.barnee.data.transformer

import com.popalay.barnee.data.model.InstructionStep
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure

internal object InstructionStepTransformer : KSerializer<InstructionStep> {
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

fun Number.toIntIfInt(): Number = if (toInt() - toDouble() == 0.0) toInt() else this
