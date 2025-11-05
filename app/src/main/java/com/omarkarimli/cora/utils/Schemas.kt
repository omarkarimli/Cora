package com.omarkarimli.cora.utils

import com.google.firebase.ai.type.Schema

object Schemas {
    const val AI_MODEL_1 = "gemini-2.5-flash"
    const val AI_MODEL_2 = "gemini-2.5-flash-image-preview"
    const val JSON_MIME_TYPE = "application/json"

    val imageModelSchema = Schema.obj(
        mapOf(
            "imageUrl" to Schema.string(description = "The URL or file path to the image resource."),
            "sourceUrl" to Schema.string(description = "The URL of the original source, like an e-commerce page."),
            "gender" to Schema.string(description = "The associated gender (e.g., men, women, unisex).")
        )
    )

    val clothModelSchema = Schema.obj(
        mapOf(
            "type" to Schema.string(description = "The category of the item (e.g., shirt, jeans, dress)."),
            "material" to Schema.string(description = "The fabric composition (e.g., cotton, wool)."),
            "color" to Schema.string(description = "The dominant color."),
            "size" to Schema.string(description = "The size of the item (e.g., M, L, 32x30)."),
            "price" to Schema.string(description = "The price of the item, including currency symbols.")
        )
    )

    val itemAnalysisModelSchema = Schema.obj(
        mapOf(
            "title" to Schema.string(description = "A short, descriptive title for the analysis."),
            "description" to Schema.string(description = "A detailed description of the analysis or item."),
            "gender" to Schema.enumeration(
                listOf("men", "women", "unisex"),
                description = "The overall gender category of the analysis."
            ),
            "parts" to Schema.array(
                clothModelSchema,
                description = "A list of ClothModel objects representing individual articles of clothing detected."
            )
        )
    )
}