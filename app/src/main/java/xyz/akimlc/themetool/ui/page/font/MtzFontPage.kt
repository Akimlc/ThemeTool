package xyz.akimlc.themetool.ui.page.font

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.ui.compoent.AppScaffold
import xyz.akimlc.themetool.ui.compoent.LabeledTextField
import xyz.akimlc.themetool.ui.compoent.WarningNotice
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

    AppScaffold(
        title = stringResource(R.string.title_ttf_to_mtz),
        navController = navController
    ) {
        item {
            WarningNotice("导入的字体需要主题破解才能正常应用，请确保已破解主题后再使用")
        }
        item {
            LabeledTextField(
                modifier = Modifier.padding(top = 8.dp),
                label = stringResource(R.string.label_font_name),
                value = fontName,
                onValueChange = { fontName = it },
            )
            LabeledTextField(
                label = stringResource(R.string.label_font_author),
                value = fontAuthor,
                onValueChange = {
                    fontAuthor = it
                }
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
                    text = stringResource(R.string.button_import),
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
                    text = stringResource(R.string.button_generate),
                    onClick = {
                        // 校验输入
                        when {
                            importFont.isEmpty() -> {
                                showToast(context.getString(R.string.toast_please_import_font))
                                return@TextButton
                            }

                            fontName.isEmpty() -> {
                                showToast(context.getString(R.string.toast_please_enter_font_name))
                                return@TextButton
                            }

                            fontAuthor.isEmpty() -> {
                                showToast(context.getString(R.string.toast_please_enter_font_author))
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

        //字体预览区域
        item {
            SmallTitle("字体预览区域")
            FontPreview(fontUri)
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
        title = stringResource(R.string.dialog_title_converting),
        onDismissRequest = {
            showDialog.value = false
        }
    ) {
        LinearProgressIndicator(progress)
    }

}

@Composable
fun FontPreview(fontUri: Uri?) {
    val context = LocalContext.current
    var fontFamily by remember { mutableStateOf<FontFamily?>(null) }
    LaunchedEffect(fontUri) {
        fontUri?.let {
            val loadTypefaceFromUri = loadTypefaceFromUri(context, it)
            fontFamily = loadTypefaceFromUri?.let { FontFamily(it) }
        }
    }
    if (fontFamily!=null) {
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "简体 你 我 猫 狗 鱼 葱 兔 花 爱 心 的 一\n" +
                        "每一杯普通的水，和喜欢的人在一起就会变成雪碧；他的胳膊和你轻轻碰到，你心里的气泡就会滋儿一下的跑到嗓子；甜甜的呀，还亮晶晶的，好像刚才喝下了整条星河。……\n" +
                        "繁體 \n" +
                        "每一杯普通的水，和喜歡的人在一起就會變成雪碧；他的胳膊和妳輕輕碰到，妳心裏的氣泡就會滋兒一下的跑到嗓子；甜甜的呀，還亮晶晶的，好像剛才喝下了整條星河。……\n" +
                        "魃 魈 魁 魑 魅 魍 魉 爨 齁 （常规字库）\n" +
                        "顛 萬 畝 長 遠 會 連 誇 稱 讚 過 麗 （1w）\n" +
                        "屄 嚟 醃 歩 恆 姮 厷 痾 娿 幹 亜 燊 毐 掱（1.5w）\n" +
                        "槑 烎 圐 圙 嫑 勥 奣 巭 謽 鬾 嘂 嚻 靐 龖 龘 齾 爩 龗 灪 龖 厵 鱻 犇 羴 驫 灥  嚞 孨 朤 讟 纞 竑 涐 烝 俢 婳（2w）\n" +
                        "㙓 㵘 㐂 䲜 㥮 㑳 㤘 㑇 䋺 䀚（三万）\n" +
                        "字母\n" +
                        "abcdefghijklmnopqrstuvwxyz\n" +
                        "ABCDEFGHIJKLMNOPQRSTUVWXYZ\n" +
                        "数字\n" +
                        "1 2 3 4 5 6 7 8 9 0\n" +
                        "符号 \n" +
                        "逗号，顿号、句号。冒号：问号？ 感叹号！ \n" +
                        "艾特@百分号% 井号# 括号( )省略号……",
                fontFamily = fontFamily,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


fun loadTypefaceFromUri(context: Context, uri: Uri): Typeface? {
    return try {
        context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
            Typeface.Builder(pfd.fileDescriptor).build()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}