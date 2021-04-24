package com.popalay.barnee.data.repository

sealed class DrinksRequest {
    data class RelatedTo(val alias: String) : DrinksRequest()
    data class ForTags(val tags: Set<String>) : DrinksRequest()
    data class ForQuery(val query: String) : DrinksRequest()
    data class Random(val count: Int) : DrinksRequest()
    object Favorites : DrinksRequest()
}