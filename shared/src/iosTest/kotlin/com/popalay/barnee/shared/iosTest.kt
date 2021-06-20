package com.popalay.barnee.shared

import kotlin.test.Test
import kotlin.test.assertTrue

class IosGreetingTest {

    @Test
    fun testExample() {
        assertTrue(Greeting().greetingText().contains("iOS"), "Check iOS is mentioned")
    }
}
