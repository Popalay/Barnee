package com.popalay.barnee.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.unit.dp

val Icons.Search: ImageVector
    get() {
        if (_search != null) {
            return _search!!
        }
        _search = Builder(
            name = "Search",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 50.0f,
            viewportHeight = 50.0f
        ).apply {
            materialPath {
                moveTo(21f, 3f)
                curveTo(11.6f, 3f, 4f, 10.6f, 4f, 20f)
                curveTo(4f, 29.4f, 11.6f, 37f, 21f, 37f)
                curveTo(24.3546f, 37f, 27.471f, 36.0198f, 30.1035f, 34.3477f)
                lineTo(42.378906f, 46.621094f)
                lineTo(46.621094f, 42.378906f)
                lineTo(34.523438f, 30.279297f)
                curveTo(36.6957f, 27.424f, 38f, 23.8706f, 38f, 20f)
                curveTo(38f, 10.6f, 30.4f, 3f, 21f, 3f)
                close()
                moveTo(21f, 7f)
                curveTo(28.2f, 7f, 34f, 12.8f, 34f, 20f)
                curveTo(34f, 27.2f, 28.2f, 33f, 21f, 33f)
                curveTo(13.8f, 33f, 8f, 27.2f, 8f, 20f)
                curveTo(8f, 12.8f, 13.8f, 7f, 21f, 7f)
                close()
            }
        }
            .build()
        return _search!!
    }

private var _search: ImageVector? = null
