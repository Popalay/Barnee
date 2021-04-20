package com.popalay.barnee.ui.util

import androidx.compose.ui.unit.IntSize
import coil.request.ImageRequest

fun ImageRequest.Builder.applyForExtarnalImage(data: String, size: IntSize) =
    data(data + "?imwidth=${size.width}")

fun ImageRequest.Builder.applyForInternalImage(data: String, size: IntSize) =
    data("https://res.cloudinary.com/barnee/image/upload/c_scale,w_${size.width}/$data")