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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.dismissDialog
import xyz.akimlc.themetool.repository.font.TTF2ZIP.Companion.convert
import xyz.akimlc.themetool.utils.FileUtils

@Composable
fun ZipFontPage(navController: NavController) {
    var importFont by remember { mutableStateOf("") }
    var fontName by remember { mutableStateOf("") }
    var zipName by remember { mutableStateOf("") }
    var fontUri by remember { mutableStateOf<Uri?>(null) }

    var showConvertDialog = remember { mutableStateOf(false) }
    var convertProgress by remember { mutableStateOf(0f) }
    val scrollBehavior = MiuixScrollBehavior()

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val selectFontLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            return@rememberLauncherForActivityResult
        }
        val data = result.data ?: return@rememberLauncherForActivityResult
        val uri = data.data ?: return@rememberLauncherForActivityResult
        fontUri = uri
        importFont = FileUtils().getFileNameFromUri(context, uri) ?: "未知字体文件"
        // 删除后缀
        fontName = importFont.substringBeforeLast(".")
        // 默认ZIP文件名与字体名相同
        zipName = fontName
        focusManager.clearFocus()
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "TTF转ZIP",
                scrollBehavior = scrollBehavior
            )
        }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 12.dp),
            topAppBarScrollBehavior = scrollBehavior,
            contentPadding = padding
        ) {
            item {
                TextField(
                    label = "字体名称",
                    value = fontName,
                    onValueChange = { fontName = it },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                
                // 添加ZIP文件名输入框
                TextField(
                    label = "ZIP文件名",
                    value = zipName,
                    onValueChange = { zipName = it },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                
                TextField(
                    label = "导入的字体",
                    value = importFont,
                    enabled = false,
                    onValueChange = { importFont = it },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
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
                                    showToast("请先导入字体")
                                    return@TextButton
                                }

                                fontName.isEmpty() -> {
                                    showToast("请输入字体名称")
                                    return@TextButton
                                }
                                
                                zipName.isEmpty() -> {
                                    showToast("请输入ZIP文件名")
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
                                        zipName, // 使用用户输入的ZIP文件名
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
        title = "正在转换",
        onDismissRequest = { dismissDialog(showDialog) }
    ) {
        LinearProgressIndicator(progress)
    }
}