package com.popalay.barnee.data.repository

sealed class DrinksRequest {
    data class RelatedTo(val alias: String) : DrinksRequest()
    data class ForTags(val tags: Set<String>) : DrinksRequest()
    data class ByAliases(val aliases: Set<String>) : DrinksRequest()
    data class ForQuery(val query: String) : DrinksRequest()
    data class Collection(val name: String) : DrinksRequest()
    data class Search(
        val query: String,
        val filters: Map<String, List<String>>
    ) : DrinksRequest()

    object Random : DrinksRequest()
}
