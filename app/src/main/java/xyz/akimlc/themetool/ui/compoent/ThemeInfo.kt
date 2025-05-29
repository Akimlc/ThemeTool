package xyz.akimlc.themetool.ui.compoent

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog
import xyz.akimlc.themetool.data.model.Info
import xyz.akimlc.themetool.data.model.Info.ThemeInfo
import xyz.akimlc.themetool.repository.Parse
import xyz.akimlc.themetool.viewmodel.SearchFontViewModel
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel.GlobalProductData
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
                themeInfoState.value = Parse().parseTheme(product.uuid) // 解析主题信息
                isLoading.value = false
            }
        }
    }
    SuperDialog(
        show = isShow,
        title = "主题信息",
        onDismissRequest = {
            isShow.value = false
        }
    ) {
        Column {
            Text("主题名字：${product.name}")
            if (isLoading.value) {
                Text("正在解析，请稍候...")
            } else {
                Text("主题链接：${themeInfoState.value?.themeUrl ?: "解析失败"}")
            }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            Spacer(Modifier.height(12.dp))
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
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun GlobalThemeInfoDialog(
    isShow: MutableState<Boolean>,
    product: GlobalProductData,
) {
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(true) }
    val themeState = remember { mutableStateOf<Info.GlobalTheme?>(null) }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(isShow.value) {
        if (isShow.value) {
            isLoading.value = true
            coroutineScope.launch {
                themeState.value = Parse().parseGlobalTheme(product.uuid)
                isLoading.value = false
            }
        }
    }
    SuperDialog(
        show = isShow,
        title = "主题信息",
        onDismissRequest = {
            isShow.value = false
        }
    ) {
        Column {
            Text("主题名字：${product.name}")
            if (isLoading.value) {
                Text("正在解析,请稍后....")
            } else {
                Text("主题链接：${themeState.value?.downloadUrl ?: "解析失败..."}")
            }

            TextButton(
                "复制",
                onClick = {
                    val themeState = themeState.value
                    if (themeState==null) {
                        Toast.makeText(context, "复制失败", Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }
                    val clipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("Theme URL", themeState.downloadUrl)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            TextButton(
                "下载",
                onClick = {
                    val themeState = themeState.value
                    if (themeState==null) {
                        Toast.makeText(context, "解析失败...", Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }
                    val intent = Intent(Intent.ACTION_VIEW, themeState.downloadUrl.toUri())
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.textButtonColorsPrimary(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DomesticFontInfoDialog(
    isShow: MutableState<Boolean>,
    product: SearchFontViewModel.ProductData
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(true) }
    val themeState = remember { mutableStateOf<Info.DomesticFontInfo?>(null) }

    LaunchedEffect(isShow.value) {
        if (isShow.value) {
            isLoading.value = false
            coroutineScope.launch {
                themeState.value = Parse().parseDomesticFont(product.uuid, product.name)
                isLoading.value = false
            }
        }
    }
    SuperDialog(
        title = "字体信息",
        show = isShow,
        onDismissRequest = {
        }
    ) {
        Column {
            Text("主题名字：${product.name}")
            if (isLoading.value) {
                Text("正在解析中...")
            } else {
                Text("下载链接：${themeState.value?.downloadUrl ?: "解析失败..."}")
            }
            TextButton(
                "复制",
                onClick = {
                    val themeState = themeState.value
                    if (themeState==null) {
                        Toast.makeText(context, "复制失败", Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }
                    val clipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("Theme URL", themeState.downloadUrl)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            TextButton(
                "下载",
                onClick = {
                    val themeState = themeState.value
                    if (themeState==null) {
                        Toast.makeText(context, "解析失败...", Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }
                    val intent = Intent(Intent.ACTION_VIEW, themeState.downloadUrl.toUri())
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.textButtonColorsPrimary(),
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}