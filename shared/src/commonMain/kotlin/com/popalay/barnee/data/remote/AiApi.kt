/*
 * Copyright (c) 2023 Denys Nykyforov
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

package com.popalay.barnee.data.remote

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionMode
import com.aallam.openai.api.chat.Parameters
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.util.Logger
import kotlinx.serialization.json.Json

class AiApi(
    private val openAi: OpenAI,
    private val json: Json,
    private val logger: Logger
) {

    @OptIn(BetaOpenAI::class)
    suspend fun getDrinkByPrompt(prompt: String): Drink {
        val response = openAi.chatCompletion(createChatCompletionRequest(prompt))
        val result = response.choices[0].message?.functionCall?.arguments?.let {
            json.decodeFromString(Drink.serializer(), it)
        }

        logger.debug("AiAPI", result.toString())

        return result ?: error("Drink is null")
    }
}

@OptIn(BetaOpenAI::class)
private fun createChatCompletionRequest(prompt: String) = chatCompletionRequest {
    model = ModelId("gpt-3.5-turbo-0613")
    n = 1
    temperature = 1.5
    messages = listOf(
        ChatMessage(
            role = ChatRole.System,
            content = "You are a helpful cocktails recipe assistant."
        ),
        ChatMessage(
            role = ChatRole.User,
            content = "Suggest a cocktail based on the following wishlist: $prompt. Be creative and imaginative! " +
                    "The name of the cocktail should be unique and imaginative e.g. The Fluffy Unicorn, Stellar Swirl, Mystical Mingle."
        )
    )
    functions {
        function {
            name = "setRecipe"
            description = "Set the recipe for the cocktail"
            parameters = Parameters.fromJsonString(responseScheme)
        }
    }
    functionCall = FunctionMode.Named("setRecipe")
}

private val responseScheme = """
                            {
                              "type": "object",
                              "properties": {
                                "howToMix": {
                                  "type": "object",
                                  "properties": {
                                    "summary": {
                                      "type": "string",
                                      "description": "Summary of the drink recipe"
                                    },
                                    "stepByStep": {
                                      "type": "array",
                                      "items": {
                                        "type": "object",
                                        "properties": {
                                          "text": {
                                            "type": "string",
                                            "description": "Text describing the step"
                                          }
                                        }
                                      }
                                    }
                                  }
                                },
                                "id": {
                                  "type": "string",
                                  "description": "Unique Id of the drink, always starts with 'generated_'"
                                },
                                "ingredients": {
                                  "type": "array",
                                  "items": {
                                    "type": "object",
                                    "properties": {
                                      "text": {
                                        "type": "string",
                                        "description": "Text describing the ingredient"
                                      },
                                      "nutrition": {
                                        "type": "integer",
                                        "description": "Nutrition value of the ingredient"
                                      }
                                    }
                                  },
                                  "required": [
                                    "text",
                                    "nutrition"
                                  ]
                                },
                                "name": {
                                  "type": "string",
                                  "description": "The name of the drink"
                                },
                                "nutritions": {
                                  "type": "object",
                                  "properties": {
                                    "totalCalories": {
                                      "type": "integer",
                                      "description": "Total calories of the drink"
                                    }
                                  }
                                },
                                "story": {
                                  "type": "string",
                                  "description": "Story of the drink"
                                }
                              },
                              "required": [
                                "id",
                                "name",
                                "story",
                                "howToMix",
                                "nutritions"
                              ]
                            }
                        """.trimIndent()
