package com.popalay.barnee.util

fun Number.toIntIfInt(): Number = if (toInt() - toDouble() == 0.0) toInt() else this
