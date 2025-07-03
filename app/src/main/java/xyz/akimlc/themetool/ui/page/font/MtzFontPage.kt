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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.ui.compoent.BackTopAppBar
import xyz.akimlc.themetool.ui.compoent.LabeledTextField
import xyz.akimlc.themetool.utils.FileUtils
import xyz.akimlc.themetool.utils.font.TTF2MTZ.Companion.convert

@Composable
fun MtzFontPage(navController: NavController) {
    var importFont by remember { mutableStateOf("") }
    var fontName by remember { mutableStateOf("") }
    var fontAuthor by remember { mutableStateOf("ThemeTool") }
    var fontUri by remember { mutableStateOf<Uri?>(null) }


    var showConvertDialog = remember { mutableStateOf(false) }
    var showConvertProgress by remember { mutableStateOf(false) }
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
        //删除后缀
        fontName = importFont.substringBeforeLast(".")
        focusManager.clearFocus()
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            BackTopAppBar(
                title = "TTF转MTZ",
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
                LabeledTextField(
                    label = "字体名称",
                    value = fontName,
                    onValueChange = { fontName = it },
                )
                LabeledTextField(
                    label = "字体作者",
                    value = fontAuthor,
                    onValueChange = {
                        fontAuthor = it
                    }
                )
                LabeledTextField(
                    label = "导入的字体",
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
                                    showToast("请先导入字体")
                                    return@TextButton
                                }

                                fontName.isEmpty() -> {
                                    showToast("请输入字体名称")
                                    return@TextButton
                                }

                                fontAuthor.isEmpty() -> {
                                    showToast("请输入字体作者")
                                    return@TextButton
                                }

                                else -> {
                                    // 显示进度对话框
                                    showConvertProgress = true
                                    showConvertDialog.value = true
                                    // 调用转换并传入进度更新
                                    convert(
                                        context,
                                        fontUri,
                                        importFont,
                                        fontName,
                                        fontAuthor,
                                        onProgressUpdate = { progress ->
                                            convertProgress = progress
                                        },
                                        onFinish = {
                                            showConvertProgress = false
                                            showConvertDialog.value = false
                                        }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                    ProgressDialog(showConvertDialog, convertProgress)

                }
            }
        }
    }
}


@Composable
fun ProgressDialog(
    showDialog: MutableState<Boolean>,
    progress: Float,
) {

    SuperDialog(
        show = showDialog,
        title = "正在转换",
        onDismissRequest = {
            showDialog.value = false
        }
    ) {
        LinearProgressIndicator(progress)
    }

}



