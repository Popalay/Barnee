package com.popalay.barnee.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AggregationResponse(
    val metaData: AggregationMetadata
)

@Serializable
data class AggregationMetadata(
    val aggregations: Aggregation
)

@Serializable
data class Aggregation(
    val tasting: AggregationGroup,
    val skill: AggregationGroup,
    val servedIn: AggregationGroup,
    val colored: AggregationGroup,
    val withType: AggregationGroup
)

@Serializable
data class AggregationGroup(
    private val values: Map<String, Int>
) {
    @Transient
    val displayNames = values.keys.map { it.replace(' ', '-') to it.lowercase().replace('-', ' ') }
}