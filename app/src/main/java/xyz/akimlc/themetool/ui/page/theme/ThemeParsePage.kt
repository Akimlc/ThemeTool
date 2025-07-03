package xyz.akimlc.themetool.ui.page.theme

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.data.model.Info.ThemeInfo
import xyz.akimlc.themetool.ui.compoent.BackTopAppBar
import xyz.akimlc.themetool.ui.compoent.LabeledTextField
import xyz.akimlc.themetool.viewmodel.DownloadViewModel
import xyz.akimlc.themetool.viewmodel.ParseViewModel

@Composable
fun ThemeParsePage(
    navController: NavController,
    viewModel: ParseViewModel = viewModel(),
    downloadViewmodel: DownloadViewModel
) {
    val context = LocalContext.current
    val scroll = MiuixScrollBehavior(rememberTopAppBarState())
    var shareLink by remember { mutableStateOf("") }
    val themeInfo by viewModel.themeInfoState
    val errorMessage by viewModel.errorMessage

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            BackTopAppBar(
                title = "主题解析",
                scrollBehavior = scroll,
                navController = navController
            )
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .overScrollVertical()
                .padding(top = 12.dp)
                .nestedScroll(scroll.nestedScrollConnection),
            contentPadding = paddingValues
        ) {
            item {
                LabeledTextField(
                    value = shareLink,
                    onValueChange = { shareLink = it },
                    label = "请输入主题分享链接",
                )
                TextButton(
                    text = "解析",
                    onClick = {
                        viewModel.parseTheme(shareLink.toString())
                    },
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 6.dp)
                        .padding(bottom = 8.dp),
                )
            }
            item {
                ThemeInfoCard(
                    themeInfo,
                    downloadViewmodel
                )
            }
        }
    }
}


@Composable
fun ThemeInfoCard(themeInfo: ThemeInfo?, downloadViewModel: DownloadViewModel) {
    val context = LocalContext.current
    if (themeInfo==null) {
        return
    }
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Text("主题名字:${themeInfo.themeName}")
            Text("主题链接：${themeInfo.themeUrl}")
            Spacer(modifier = Modifier.height(6.dp))
            Text("主题大小： ${themeInfo.themeSize / (1024 * 1024)} MB")
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = {
                        // 复制链接到剪贴板
                        val clipboardManager =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = ClipData.newPlainText("Theme URL", themeInfo.themeUrl)
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    text = "复制"
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = {
                        // 跳转到浏览器进行下载
                        val intent = Intent(Intent.ACTION_VIEW, themeInfo.themeUrl.toUri())
                        context.startActivity(intent)
                        //Go to 下载管理界面
                        //downloadViewModel.startDownload(themeInfo)
                    },
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    modifier = Modifier.weight(1f),
                    text = "下载"
                )
            }
        }
    }
}