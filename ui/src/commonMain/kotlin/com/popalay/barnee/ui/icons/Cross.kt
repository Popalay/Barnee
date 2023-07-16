package com.popalay.barnee.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.unit.dp

val Icons.Cross: ImageVector
    get() {
        if (_cross != null) {
            return _cross!!
        }
        _cross = Builder(
            name = "Cross",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            materialPath {
                moveTo(13.46f, 12.0f)
                lineTo(19.0f, 17.54f)
                verticalLineTo(19.0f)
                horizontalLineTo(17.54f)
                lineTo(12.0f, 13.46f)
                lineTo(6.46f, 19.0f)
                horizontalLineTo(5.0f)
                verticalLineTo(17.54f)
                lineTo(10.54f, 12.0f)
                lineTo(5.0f, 6.46f)
                verticalLineTo(5.0f)
                horizontalLineTo(6.46f)
                lineTo(12.0f, 10.54f)
                lineTo(17.54f, 5.0f)
                horizontalLineTo(19.0f)
                verticalLineTo(6.46f)
                lineTo(13.46f, 12.0f)
                close()
            }
        }
            .build()
        return _cross!!
    }

private var _cross: ImageVector? = null
