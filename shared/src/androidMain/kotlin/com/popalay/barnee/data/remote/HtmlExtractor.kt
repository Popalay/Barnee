package com.popalay.barnee.data.remote

import org.jsoup.Jsoup

actual class HtmlExtractor actual constructor() {
    actual fun extract(url: String, selector: String): String {
        val document = Jsoup.connect(url).get()
        val scripts = document.head().select(selector)
        return scripts[0].childNode(0).toString()
    }
}