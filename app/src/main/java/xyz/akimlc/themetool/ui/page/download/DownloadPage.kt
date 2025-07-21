package xyz.akimlc.themetool.ui.page.download

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Info
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.data.db.DownloadEntity
import xyz.akimlc.themetool.data.model.DownloadStatus
import xyz.akimlc.themetool.ui.compoent.InfoNotice
import xyz.akimlc.themetool.ui.compoent.getAdaptiveBlackWhite
import xyz.akimlc.themetool.viewmodel.DownloadViewModel

@Composable
fun DownloadPage(
    navController: NavController,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    viewModel: DownloadViewModel
) {
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    Log.d("DownloadViewModelCheck", "DownloadPage ViewModel: $viewModel")
    val TAG = "DownloadPage"
    val downloadList by viewModel.downloads.collectAsState()
    //Log.d("DownloadPage", "下载任务数=${downloadList.size}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.download),
                scrollBehavior = topAppBarScrollBehavior,
                actions = {
                    IconButton(
                        modifier = Modifier
                            .padding(end = 18.dp)
                            .size(40.dp),
                        onClick = {
                            showDialog.value = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_clear),
                            contentDescription = null,
                            tint = getAdaptiveBlackWhite()
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (downloadList.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(48.dp))
                    Icon(
                        imageVector = MiuixIcons.Useful.Info,
                        contentDescription = "No downloads",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.no_downloads),
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(48.dp)) // 可选：占点空间使得可滚动
                }
            } else {
                item{
                    InfoNotice(
                        text = "下载的路径为：Download/ThemeTool"
                    )
                }
                items(downloadList, key = { it.id }) { item ->
                    DownloadItem(item)
                }
            }
        }
    }

    if (showDialog.value) {
        SuperDialog(
            show = showDialog,
            title = stringResource(R.string.clear_confirm_title),
            onDismissRequest = { showDialog.value = false },
        ) {
            Text(
                stringResource(R.string.clear_confirm_message),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Row(
                modifier = Modifier
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    text = stringResource(R.string.button_cancel),
                    onClick = {
                        showDialog.value = false
                    },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                TextButton(
                    text = stringResource(R.string.button_confirm),
                    onClick = {
                        viewModel.clearDownloads()
                        showDialog.value = false
                        Toast.makeText(
                            context,
                            context.getString(R.string.clear_success),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale", "InvalidColorHexValue")
@Composable
fun DownloadItem(item: DownloadEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_download_icon),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.name,
                    fontSize = 17.sp,
                    softWrap = false,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        String.format("%.2f MB", item.size * item.progress) +
                                " / " + String.format("%.2f MB", item.size),
                        style = MiuixTheme.textStyles.subtitle,
                        color = MiuixTheme.colorScheme.onSurfaceContainerVariant,
                        fontSize = 11.sp
                    )
                    Text(
                        text = when (item.status) {
                            DownloadStatus.READY -> "准备中"
                            DownloadStatus.FAILED -> "失败"
                            DownloadStatus.FINISHED -> "已完成"
                            DownloadStatus.DOWNLOADING -> "下载中"
                            DownloadStatus.STOP -> "已暂停"
                        },
                        color = if (isSystemInDarkTheme()) Color(0xFF8F8F8F) else Color(0x73737373),
                        fontSize = 11.sp
                    )
                }
                LinearProgressIndicator(
                    progress = item.progress,
                    modifier = Modifier.fillMaxWidth()
                )
            }
//            Box(
//                modifier = Modifier
//                    .height(32.dp)
//                    .defaultMinSize(minWidth = 64.dp)
//                    .background(Color(0x1A0D84FF), shape = RoundedCornerShape(200.dp)),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = if (item.status==DownloadStatus.DOWNLOADING) {
//                        "暂停"
//                    } else {
//                        "开始"
//                    },
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFF0D84FF),
//                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//                )
//            }
        }
    }
}
