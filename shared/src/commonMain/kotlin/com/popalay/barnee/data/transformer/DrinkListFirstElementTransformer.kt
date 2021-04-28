package com.popalay.barnee.data.transformer

import com.popalay.barnee.data.model.Drink
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer

object DrinkListFirstElementTransformer : JsonTransformingSerializer<Drink>(Drink.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement =
        if (element is JsonArray) element.first() else element
}