package com.example.openlibraryandroidassessment.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Sealed class representing descriptions for a book.
 * This allows for polymorphic deserialization of the book description
 * returned by the Open Library API.
 */
@Serializable
sealed class Description {
    @Serializable
    data class Simple(val value: String) : Description()

    @Serializable
    data class Complex(val value: String) : Description()
}

/**
 * Data class representing the response for a book's work information from the Open Library API.
 *
 * @property description The description of the book, which can be of type [Description].
 */
@Serializable
data class WorkResponse(
    val description: Description?
) {
    companion object {
        fun fromJsonElement(jsonElement: JsonElement): WorkResponse {
            val jsonObject = jsonElement.jsonObject
            val descriptionElement = jsonObject["description"]

            val description = when {
                // if description is a string, return that
                descriptionElement is JsonPrimitive && descriptionElement.isString ->
                    Description.Simple(descriptionElement.content)
                // if description is a struct with text in the "value" field, grab the value
                descriptionElement is JsonObject ->
                    Description.Complex(descriptionElement["value"]?.jsonPrimitive?.content ?: "")
                else -> null
            }

            return WorkResponse(description)
        }
    }
}