package com.popalay.barnee.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.LightOn: ImageVector
    get() {
        if (_lightOn != null) {
            return _lightOn!!
        }
        _lightOn = Builder(
            name = "LightOn",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1.5f,
                strokeLineCap = Round,
                strokeLineJoin = StrokeJoin.Companion.Round,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(3.0f, 12.0f)
                horizontalLineToRelative(1.0f)
                moveToRelative(8.0f, -9.0f)
                verticalLineToRelative(1.0f)
                moveToRelative(8.0f, 8.0f)
                horizontalLineToRelative(1.0f)
                moveToRelative(-15.4f, -6.4f)
                lineToRelative(0.7f, 0.7f)
                moveToRelative(12.1f, -0.7f)
                lineToRelative(-0.7f, 0.7f)
            }
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1.5f,
                strokeLineCap = Round,
                strokeLineJoin = StrokeJoin.Companion.Round,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(9.0f, 16.0f)
                arcToRelative(5.0f, 5.0f, 0.0f, true, true, 6.0f, 0.0f)
                arcToRelative(3.5f, 3.5f, 0.0f, false, false, -1.0f, 3.0f)
                arcToRelative(2.0f, 2.0f, 0.0f, false, true, -4.0f, 0.0f)
                arcToRelative(3.5f, 3.5f, 0.0f, false, false, -1.0f, -3.0f)
            }
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1.5f,
                strokeLineCap = Round,
                strokeLineJoin = StrokeJoin.Companion.Round,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(9.7f, 17.0f)
                lineTo(14.3f, 17.0f)
            }
        }
            .build()
        return _lightOn!!
    }

private var _lightOn: ImageVector? = null
