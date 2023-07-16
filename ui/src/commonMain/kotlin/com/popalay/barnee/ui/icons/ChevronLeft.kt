package com.popalay.barnee.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.unit.dp

val Icons.ChevronLeft: ImageVector
    get() {
        if (_chevronLeft != null) {
            return _chevronLeft!!
        }
        _chevronLeft = Builder(
            name = "ChevronLeft",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 19.0f,
            viewportHeight = 19.0f
        ).apply {
            materialPath {
                moveTo(12.9f, 17.269f)
                arcToRelative(1.026f, 1.026f, 0.0f, false, true, -0.727f, -0.302f)
                lineToRelative(-6.801f, -6.8f)
                arcToRelative(1.03f, 1.03f, 0.0f, false, true, 0.0f, -1.456f)
                lineToRelative(6.8f, -6.8f)
                arcToRelative(1.03f, 1.03f, 0.0f, false, true, 1.456f, 1.455f)
                lineTo(7.555f, 9.439f)
                lineToRelative(6.073f, 6.073f)
                arcTo(1.03f, 1.03f, 0.0f, false, true, 12.9f, 17.27f)
                close()
            }
        }
            .build()
        return _chevronLeft!!
    }

private var _chevronLeft: ImageVector? = null
