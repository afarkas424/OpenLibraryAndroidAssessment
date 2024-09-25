package com.example.openlibraryandroidassessment.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Sealed Class to accounbt for polymorphic deserialization of /work request to get book descriptions
 */
@Serializable
sealed class Description {
    @Serializable
    data class Simple(val value: String) : Description()

    @Serializable
    data class Complex(val value: String) : Description()
}

@Serializable
data class WorkResponse(
    val description: Description?
) {
    companion object {
        fun fromJsonElement(jsonElement: JsonElement): WorkResponse {
            val jsonObject = jsonElement.jsonObject
            val descriptionElement = jsonObject["description"]

            val description = when {
                descriptionElement is JsonPrimitive && descriptionElement.isString ->
                    Description.Simple(descriptionElement.content)
                descriptionElement is JsonObject ->
                    Description.Complex(descriptionElement["value"]?.jsonPrimitive?.content ?: "")
                else -> null
            }

            return WorkResponse(description)
        }
    }
}