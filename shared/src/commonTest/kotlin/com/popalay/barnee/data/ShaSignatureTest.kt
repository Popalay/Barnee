package com.popalay.barnee.data

import io.ktor.util.sha1
import io.ktor.utils.io.core.toByteArray
import kotlin.test.Test
import kotlin.test.assertEquals

class ShaSignatureTest {

    @Test
    fun testSignatureGeneration() {
        val input = "eager=w_400,h_300,c_pad|w_260,h_200,c_crop&public_id=sample_image&timestamp=1315060510abcd"
        val expectedSignature = "bfd09f95f331f558cbd1320e67aa8d488770583e"
        val signature = input.toSha1()

        assertEquals(expectedSignature, signature)
    }
}

fun String.toSha1(): String = sha1(this.toByteArray()).joinToString(separator = "") {
    val hexChars = "0123456789abcdef"
    val highNibble = (it.toInt() shr 4) and 0x0F
    val lowNibble = it.toInt() and 0x0F
    "${hexChars[highNibble]}${hexChars[lowNibble]}"
}
