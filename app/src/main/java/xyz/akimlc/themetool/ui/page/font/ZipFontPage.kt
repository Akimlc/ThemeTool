package xyz.akimlc.themetool.ui.page.font

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.ui.compoent.BackTopAppBar
import xyz.akimlc.themetool.ui.compoent.ErrorNotice
import xyz.akimlc.themetool.ui.compoent.LabeledTextField
import xyz.akimlc.themetool.utils.FileUtils
import xyz.akimlc.themetool.utils.font.TTF2ZIP.Companion.convert

@Composable
fun ZipFontPage(navController: NavController) {
    var importFont by remember { mutableStateOf("") }
    var zipName by remember { mutableStateOf("") }
    var fontUri by remember { mutableStateOf<Uri?>(null) }

    var showConvertDialog = remember { mutableStateOf(false) }
    val isShow = remember { mutableStateOf(false) }
    var convertProgress by remember { mutableStateOf(0f) }
    val scrollBehavior = MiuixScrollBehavior()

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val selectFontLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode!=RESULT_OK) {
            return@rememberLauncherForActivityResult
        }
        val data = result.data ?: return@rememberLauncherForActivityResult
        val uri = data.data ?: return@rememberLauncherForActivityResult
        fontUri = uri
        importFont = FileUtils().getFileNameFromUri(context, uri) ?: "未知字体文件"
        zipName = importFont.substringBeforeLast(".")
        focusManager.clearFocus()
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) {
        isShow.value = true
    }
    Scaffold(
        topBar = {
            BackTopAppBar(
                title = stringResource(R.string.title_ttf_to_mtz),
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(top = 12.dp),
            contentPadding = padding
        ) {
            item {
                ShowWaringDialog(isShow, navController)
                ErrorNotice(
                    text = stringResource(R.string.error_experimental_feature)
                )
                LabeledTextField(
                    label = stringResource(R.string.label_font_name),
                    value = zipName,
                    onValueChange = { zipName = it },
                )

                LabeledTextField(
                    label = stringResource(R.string.label_imported_font),
                    value = importFont,
                    onValueChange = { importFont = it },
                )
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        text = "导入",
                        onClick = {
                            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                                type = "font/ttf"
                            }
                            selectFontLauncher.launch(intent)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(12.dp))
                    TextButton(
                        text = "生成",
                        onClick = {
                            // 校验输入
                            when {
                                importFont.isEmpty() -> {
                                    showToast(context.getString(R.string.toast_please_import_font))
                                    return@TextButton
                                }

                                zipName.isEmpty() -> {
                                    showToast(context.getString(R.string.toast_please_enter_font_name))
                                    return@TextButton
                                }

                                else -> {
                                    // 显示进度对话框
                                    showConvertDialog.value = true
                                    // 调用转换并传入进度更新
                                    convert(
                                        context,
                                        fontUri,
                                        importFont,
                                        zipName,
                                        onProgressUpdate = { progress ->
                                            convertProgress = progress
                                        },
                                        onFinish = {
                                            showConvertDialog.value = false
                                        }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                }
                ZipProgressDialog(showConvertDialog, convertProgress)
            }
        }
    }
}

@Composable
private fun ZipProgressDialog(
    showDialog: MutableState<Boolean>,
    progress: Float,
) {
    SuperDialog(
        show = showDialog,
        title = stringResource(R.string.progress_title_converting),
        onDismissRequest = {
            showDialog.value = false
        }
    ) {
        LinearProgressIndicator(progress)
    }
}


@Composable
private fun ShowWaringDialog(
    isShow: MutableState<Boolean>,
    navController: NavController
) {
    SuperDialog(
        title = "警告",
        show = isShow,
        onDismissRequest = {
            isShow.value = true
        },
        content = {
            Text(
                text = stringResource(R.string.warning_line1),
                modifier = Modifier
                    .fillMaxWidth(),
                style = MiuixTheme.textStyles.main,
            )
            Text(
                text = stringResource(R.string.warning_line2),
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fillMaxWidth(),
                style = MiuixTheme.textStyles.main,
            )
            Text(
                text = stringResource(R.string.warning_line3),
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fillMaxWidth(),
                style = MiuixTheme.textStyles.main,
            )

            Row(
                modifier = Modifier
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    text = stringResource(R.string.button_cancel),
                    onClick = {
                        isShow.value = false
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                TextButton(
                    text = stringResource(R.string.button_confirm),
                    onClick = {
                        isShow.value = false
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }
        }
    )
}