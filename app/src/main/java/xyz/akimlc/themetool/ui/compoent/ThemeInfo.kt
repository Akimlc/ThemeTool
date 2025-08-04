package xyz.akimlc.themetool.ui.compoent

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.data.model.Info
import xyz.akimlc.themetool.data.model.Info.ThemeInfo
import xyz.akimlc.themetool.repository.Parse
import xyz.akimlc.themetool.viewmodel.DownloadViewModel
import xyz.akimlc.themetool.viewmodel.SearchFontViewModel
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel.GlobalProductData
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel.ProductData

@Composable
fun ThemeInfoDialog(
    isShow: MutableState<Boolean>,
    product: ProductData,
    themeInfo: ThemeInfo?,
    downloadViewModel: DownloadViewModel
) {
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
        title = stringResource(R.string.title_theme_info),
        onDismissRequest = {
            isShow.value = false
        }
    ) {
        Column {
            Text(stringResource(R.string.label_theme_name, product.name))
            if (isLoading.value) {
                Text(stringResource(R.string.parsing))
            } else {
                Text(
                    stringResource(
                        R.string.label_theme_name,
                        themeInfoState.value?.themeUrl ?: stringResource(R.string.parse_failed)
                    )
                )
            }
            TextButton(
                text = stringResource(R.string.copy),
                onClick = {
                    val themeInfo = themeInfoState.value
                    if (themeInfo==null) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.parsing),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TextButton
                    }
                    val clipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("Theme URL", themeInfo.themeUrl)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(
                        context,
                        context.getString(R.string.toast_copy_success),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            Spacer(Modifier.height(12.dp))
            TextButton(
                text = stringResource(R.string.download),
                onClick = {
                    val themeInfo = themeInfoState.value
                    if (themeInfo==null) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.parsing),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TextButton
                    }
                    downloadViewModel.fetchDownloadInfo(themeInfo.themeUrl, context) // 启动下载任务
                    Toast.makeText(
                        context,
                        context.getString(R.string.added_to_download_list),
                        Toast.LENGTH_SHORT
                    ).show()
                    isShow.value = false
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
    downloadViewModel: DownloadViewModel
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
        title = stringResource(R.string.title_theme_info),
        onDismissRequest = {
            isShow.value = false
        }
    ) {
        Column {
            Text(stringResource(R.string.label_theme_name, product.name))
            if (isLoading.value) {
                Text(stringResource(R.string.parsing))
            } else {
                Text(
                    stringResource(
                        R.string.label_theme_url,
                        themeState.value?.downloadUrl ?: stringResource(R.string.parse_failed)
                    )
                )
            }

            TextButton(
                text = stringResource(R.string.copy),
                onClick = {
                    val themeState = themeState.value
                    if (themeState==null) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.parsing),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TextButton
                    }
                    val clipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("Theme URL", themeState.downloadUrl)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(
                        context,
                        context.getString(R.string.copy_success),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            TextButton(
                text = stringResource(R.string.download),
                onClick = {
                    val themeState = themeState.value
                    if (themeState==null) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.parsing),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TextButton
                    }
                    downloadViewModel.fetchDownloadInfo(themeState.downloadUrl, context)
                    Toast.makeText(
                        context,
                        context.getString(R.string.added_to_download_list),
                        Toast.LENGTH_SHORT
                    ).show()
                    isShow.value = false
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
    product: SearchFontViewModel.ProductData,
    downloadViewModel: DownloadViewModel
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
        title = stringResource(R.string.title_theme_info),
        show = isShow,
        onDismissRequest = {
            isShow.value = false
        }
    ) {
        Column {
            Text(stringResource(R.string.label_font_name, product.name))
            if (isLoading.value) {
                Text(stringResource(R.string.parsing))
            } else {
                Text(
                    stringResource(
                        R.string.label_font_url,
                        themeState.value?.downloadUrl ?: stringResource(R.string.parse_failed)
                    )
                )
            }
            TextButton(
                text = stringResource(R.string.copy),
                onClick = {
                    val themeState = themeState.value
                    if (themeState==null) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.copy_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TextButton
                    }
                    val clipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("Theme URL", themeState.downloadUrl)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(
                        context,
                        context.getString(R.string.copy_success),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            TextButton(
                text = stringResource(R.string.download),
                onClick = {
                    val themeState = themeState.value
                    if (themeState==null) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.parse_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TextButton
                    }
                    downloadViewModel.fetchDownloadInfo(themeState.downloadUrl, context)
                    Toast.makeText(
                        context,
                        context.getString(R.string.added_to_download_list),
                        Toast.LENGTH_SHORT
                    ).show()
                    isShow.value = false
                },
                colors = ButtonDefaults.textButtonColorsPrimary(),
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}