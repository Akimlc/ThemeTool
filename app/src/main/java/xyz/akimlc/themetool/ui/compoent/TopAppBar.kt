package xyz.akimlc.themetool.ui.compoent

import android.annotation.SuppressLint
import android.app.Activity
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@SuppressLint("ContextCastToActivity")
@Composable
fun BackTopAppBar(
    title: String,
    scrollBehavior: ScrollBehavior? = null,
    navController: NavController
) {
    val context = LocalContext.current

    TopAppBar(
        title = title,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            TopButton(
                modifier = Modifier.padding(start = 18.dp),
                imageVector = MiuixIcons.Useful.Back,
                contentDescription = null,
                onClick = {
                    navController.popBackStack()
                }
            )
        }
    )
}

@Composable
fun TopButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String?,
    tint: Color = colorScheme.onBackground,
    onClick: () -> Unit,

    ){
    val view = LocalView.current
    Box(
        modifier
            .size(35.dp)
            .clip(RoundedCornerShape(50))
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onClick()

            },
        contentAlignment = Alignment.Center
    ){
        Icon(
            imageVector,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}