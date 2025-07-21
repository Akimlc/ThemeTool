package xyz.akimlc.themetool.ui.compoent

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Cancel
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme


@Composable
fun WarningNotice(
    text: String,
) {
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) Color(0xFF4D3313) else Color(0xFFFFF6EB)
    val textColor = Color(0xFFFFA83F)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .padding(horizontal = 12.dp),
        insideMargin = PaddingValues(vertical = 12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            Image(
                modifier = Modifier
                    .padding(end = 24.dp)
                    .size(10.dp, 14.dp),
                imageVector = MiuixIcons.Useful.Cancel,
                contentDescription = null,
                colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariantActions)
            )
        }
    }
}


@Composable
fun ErrorNotice(
    text: String
) {
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) Color(0xFF4D1C1C) else Color(0xFFFFEBEB)
    val textColor = Color(0xFFFF4C4C)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .padding(horizontal = 12.dp),
        insideMargin = PaddingValues(vertical = 12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            Image(
                modifier = Modifier
                    .padding(end = 24.dp)
                    .size(10.dp, 14.dp),
                imageVector = MiuixIcons.Useful.Cancel,
                contentDescription = null,
                colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariantActions)
            )
        }
    }
}

@Composable
fun InfoNotice(
    text: String
){
    val isDark = isSystemInDarkTheme()

    val backgroundColor = if (isDark) Color(0xFF0F274B) else Color(0xFFE1EBF7)

    val textColor = Color(0xFF0D84ff)


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .padding(horizontal = 12.dp),
        insideMargin = PaddingValues(vertical = 12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            Image(
                modifier = Modifier
                    .padding(end = 24.dp)
                    .size(10.dp, 14.dp),
                imageVector = MiuixIcons.Useful.Cancel,
                contentDescription = null,
                colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariantActions)
            )
        }
    }
}