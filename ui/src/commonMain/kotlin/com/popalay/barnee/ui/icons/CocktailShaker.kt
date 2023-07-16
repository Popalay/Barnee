package com.popalay.barnee.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.unit.dp

val Icons.CocktailShaker: ImageVector
    get() {
        if (_cocktailShaker != null) {
            return _cocktailShaker!!
        }
        _cocktailShaker = Builder(
            name = "CocktailShaker",
            defaultWidth = 18.0.dp,
            defaultHeight = 18.0.dp,
            viewportWidth = 487.78f,
            viewportHeight = 487.78f
        ).apply {
            materialPath {
                moveTo(209.71f, 0.0f)
                horizontalLineToRelative(68.0f)
                verticalLineToRelative(49.91f)
                horizontalLineToRelative(-68.0f)
                close()
            }
            materialPath {
                moveTo(363.95f, 158.21f)
                curveToRelative(-8.0f, -43.85f, -42.24f, -79.23f, -86.93f, -91.17f)
                horizontalLineToRelative(-66.63f)
                curveToRelative(-44.69f, 11.94f, -78.93f, 47.32f, -86.93f, 91.17f)
                horizontalLineTo(363.95f)
                close()
            }
            materialPath {
                moveTo(119.91f, 178.21f)
                lineToRelative(42.37f, 302.23f)
                curveToRelative(0.59f, 4.21f, 4.19f, 7.34f, 8.44f, 7.34f)
                horizontalLineTo(323.84f)
                curveToRelative(4.32f, 0.0f, 7.95f, -3.23f, 8.46f, -7.52f)
                lineToRelative(35.56f, -302.04f)
                horizontalLineTo(119.91f)
                close()
            }
        }
            .build()
        return _cocktailShaker!!
    }

private var _cocktailShaker: ImageVector? = null
