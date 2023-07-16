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

package com.popalay.barnee.ui.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.popalay.barnee.ui.SharedRes
import dev.icerock.moko.resources.compose.asFont

@Composable
private fun Roboto() = FontFamily(
    listOfNotNull(
        SharedRes.fonts.Roboto.regular.asFont(FontWeight.Normal),
        SharedRes.fonts.Roboto.medium.asFont(FontWeight.Medium),
        SharedRes.fonts.Roboto.bold.asFont(FontWeight.Bold)
    )
)

@Composable
fun Typography() = Typography(
    h1 = TextStyle(
        fontFamily = Roboto(),
        fontWeight = FontWeight.Bold,
        fontSize = 50.sp
    ),
    h2 = TextStyle(
        fontFamily = Roboto(),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    h3 = TextStyle(
        fontFamily = Roboto(),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    h4 = TextStyle(
        fontFamily = Roboto(),
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = Roboto(),
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = Roboto(),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    body1 = TextStyle(
        fontFamily = Roboto(),
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = Roboto(),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    button = TextStyle(
        fontFamily = Roboto(),
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    caption = TextStyle(
        fontFamily = Roboto(),
        fontWeight = FontWeight.Normal,
        letterSpacing = 2.sp,
        fontSize = 13.sp
    ),
)
