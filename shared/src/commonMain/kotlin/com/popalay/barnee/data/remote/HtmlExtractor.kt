package com.popalay.barnee.data.remote

expect class HtmlExtractor {
    fun extract(url: String, selector: String): String
}