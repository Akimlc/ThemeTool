package xyz.akimlc.themetool.ui.compoent

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.dismissDialog
import xyz.akimlc.themetool.data.model.Info
import xyz.akimlc.themetool.data.model.Info.ThemeInfo
import xyz.akimlc.themetool.repository.ThemeRepository
import xyz.akimlc.themetool.viewmodel.SearchFontViewModel
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel.ProductData

@Composable
fun ThemeInfoDialog(isShow: MutableState<Boolean>, product: ProductData, themeInfo: ThemeInfo?) {
    val context = LocalContext.current
    val themeInfoState = remember { mutableStateOf<ThemeInfo?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // 监听弹窗打开事件，触发解析
    LaunchedEffect(isShow.value) {
        if (isShow.value) {
            isLoading.value = true
            coroutineScope.launch {
                themeInfoState.value = ThemeRepository().parseTheme(product.uuid) // 解析主题信息
                isLoading.value = false
            }
        }
    }
    SuperDialog(
        show = isShow,
        title = "主题信息",
        onDismissRequest = {
            dismissDialog(isShow)
        }
    ) {
        Column {
            Text("主题名字：${product.name}")
            if (isLoading.value) {
                Text("正在解析，请稍候...")
            } else {
                Text("主题链接：${themeInfoState.value?.themeUrl ?: "解析失败"}")
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                "复制",
                onClick = {
                    val themeInfo = themeInfoState.value
                    if (themeInfo==null) {
                        Toast.makeText(context, "正在解析，请稍候...", Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }
                    val clipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("Theme URL", themeInfo.themeUrl)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                "下载",
                onClick = {
                    val themeInfo = themeInfoState.value
                    if (themeInfo==null) {
                        Toast.makeText(context, "正在解析，请稍候...", Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(themeInfo.themeUrl))
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.textButtonColorsPrimary(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun FontInfoDialog(
    isShow: MutableState<Boolean>,
    product: SearchFontViewModel.ProductData,
    fontInfo: Info.FontInfo?
) {
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(true) }
    val fontInfoState = remember { mutableStateOf<Info.FontInfo?>(null) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(isShow.value) {
        if (isShow.value) {
            isLoading.value = true
            coroutineScope.launch {
                fontInfoState.value = ThemeRepository().parseFont(product.uuid) // 解析主题信息
                isLoading.value = false
            }

        }
    }
    SuperDialog(
        show = isShow,
        title = "字体信息",
        onDismissRequest = {
            dismissDialog(isShow)
        }
    ) {
        Column {
            Text("字体名字：${product.name}")
            if (isLoading.value) {
                Text("正在解析，请稍候...")
            } else {
                Text("主题链接：${fontInfoState.value?.fontUrl ?: "解析失败"}")
            }


            Row(
                modifier = Modifier
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    "复制",
                    onClick = {
                        val fontState = fontInfoState.value
                        if (fontState==null) {
                            Toast.makeText(context, "正在解析，请稍候...", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        val clipboardManager =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = ClipData.newPlainText("Theme URL", fontInfo?.fontUrl)
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                TextButton(
                    "下载", onClick = {
                        val fontInfo = fontInfoState.value
                        if (fontInfo==null) {
                            Toast.makeText(context, "正在解析，请稍候...", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fontInfo.fontUrl))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}