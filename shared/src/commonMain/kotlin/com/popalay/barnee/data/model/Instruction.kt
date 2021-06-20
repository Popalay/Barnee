package com.popalay.barnee.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Instruction(
    @SerialName("stepByStep") val steps: List<InstructionStep>
)
