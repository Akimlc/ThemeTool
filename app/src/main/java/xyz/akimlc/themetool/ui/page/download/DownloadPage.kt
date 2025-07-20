package xyz.akimlc.themetool.ui.page.download

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Info
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.data.db.DownloadEntity
import xyz.akimlc.themetool.data.model.DownloadStatus
import xyz.akimlc.themetool.viewmodel.DownloadViewModel


//val downloadItems = listOf(
//    DownloadModel(
//        id = "1",
//        name = "测试",
//        url = "www.www",
//        size = 1f,
//        progress = 0f,
//        status = DownloadStatus.FAILED
//    ),
//    DownloadModel(
//        id = "2",
//        name = "测试",
//        url = "www.www",
//        size = 1f,
//        progress = 0f,
//        status = DownloadStatus.FAILED
//    ),
//    DownloadModel(
//        id = "3",
//        name = "测试",
//        url = "www.www",
//        size = 1f,
//        progress = 0f,
//        status = DownloadStatus.FAILED
//    ),
//)

@Composable
fun DownloadPage(
    navController: NavController,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    viewModel: DownloadViewModel
) {

    Log.d("DownloadViewModelCheck", "DownloadPage ViewModel: $viewModel")
    val TAG = "DownloadPage"
    val downloadList by viewModel.downloads.collectAsState()
    //Log.d("DownloadPage", "下载任务数=${downloadList.size}")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
    ) {
        if (downloadList.isEmpty()) {
            //没有下载列表的时候
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .overScrollVertical()
                    .padding(top = 12.dp),
                contentPadding = padding,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Icon(
                        imageVector = MiuixIcons.Useful.Info,
                        contentDescription = "No downloads",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "暂无下载任务",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            // 下载列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .overScrollVertical()
                    .padding(top = 12.dp),
                contentPadding = padding
            ) {
                items(downloadList, key = { it.id }) { item ->
                    Log.d(TAG, "DownloadPage: 下载项：${item.name}, 大小：${item.size}MB")
                    DownloadItem(item)
                }
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
            .padding(horizontal = 12.dp, vertical = 6.dp)
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
//                    progress = if (item.status==DownloadStatus.READY) 1f else item.progress,
                    progress = item.progress,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Box(
                modifier = Modifier
                    .height(32.dp)
                    .defaultMinSize(minWidth = 64.dp)
                    .background(Color(0x1A0D84FF), shape = RoundedCornerShape(200.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (item.status==DownloadStatus.DOWNLOADING) {
                        "暂停"
                    } else {
                        "开始"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D84FF),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}