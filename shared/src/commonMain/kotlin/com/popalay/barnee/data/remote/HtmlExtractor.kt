package com.popalay.barnee.data.remote

expect object HtmlExtractor {
    fun extract(url: String, selector: String): String
}