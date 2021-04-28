package com.popalay.barnee.data.model

import com.popalay.barnee.data.transformer.InstructionStepTransformer
import kotlinx.serialization.Serializable

@Serializable(with = InstructionStepTransformer::class)
data class InstructionStep(val text: String)