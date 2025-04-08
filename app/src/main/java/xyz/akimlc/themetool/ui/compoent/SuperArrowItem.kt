package xyz.akimlc.themetool.ui.compoent

import android.graphics.drawable.Drawable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.extra.SuperArrow

@Composable
fun SuperArrowItem(
    title: String,
    summary: String? = null,
    icon: Int,
    onClick: () -> Unit
) {
    SuperArrow(
        title = title,
        summary = summary,
        onClick = onClick,
        leftAction = {
            Icon(
                modifier = Modifier.padding(end = 18.dp)
                    .size(24.dp),
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = getAdaptiveBlackWhite()
            )
        }
    )
}
@Composable
fun SuperArrowItem1(
    title: String,
    summary: String? = null,
    icon: ImageVector,
    onClick: () -> Unit
) {
    SuperArrow(
        title = title,
        summary = summary,
        onClick = onClick,
        leftAction = {
            Icon(
                modifier = Modifier.padding(end = 18.dp),
                imageVector = icon,
                contentDescription = null
            )
        }
    )
}

@Composable
fun getAdaptiveBlackWhite(): Color {
    return if (isSystemInDarkTheme()) Color.White else Color.Black
}