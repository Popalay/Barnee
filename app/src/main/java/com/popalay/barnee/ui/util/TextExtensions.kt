package com.popalay.barnee.ui.util

import java.util.Locale

fun String.capitalizeFirstChar(locale: Locale = Locale.getDefault()) =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
