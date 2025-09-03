package xyz.akimlc.themetool.ui.page.font

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import xyz.akimlc.themetool.ui.FontPageList
import xyz.akimlc.themetool.ui.compoent.AppScaffold
import xyz.akimlc.themetool.viewmodel.DesignerViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FontDesignerPage(
    navController: NavController,
    designerId: String
) {
    val viewModel: DesignerViewModel = viewModel()
    val designerInfo by viewModel.designerInfo.collectAsState()
    val designerProducts by viewModel.designerProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val backgroundColor = MiuixTheme.colorScheme.background
    val isDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(designerId) {
        if (designerInfo==null) {
            viewModel.loadDesignerInfoData(designerId)
        }
        if (designerProducts.isEmpty()) {
            viewModel.designerProducts(designerId, 0)
        }
    }

    //进来的时候，显示进度条加载数据
    if (designerInfo==null && designerProducts.isEmpty() && isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    AppScaffold(
        title = "作者详情",
        navController = navController
    ) {
        item {
            DesignerInfoCard(designerInfo)
        }
        designerProducts.forEachIndexed { index, product ->
            item {
                DesignerProductItem(
                    product = product,
                    isDark = isDarkTheme,
                    index = index,
                    navController = navController
                )
            }

            // 滑动到底部自动加载更多
            if (index==designerProducts.lastIndex && hasMore && !isLoading) {
                viewModel.loadMore()
            }
        }

        // 加载状态提示
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}


@Composable
fun DesignerInfoCard(
    info: DesignerViewModel.DesignerInfoData?
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像
        AsyncImage(
            model = info?.designerIcon
                ?: "https://q2.qlogo.cn/headimg_dl?dst_uin=1908459261&spec=100",
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.width(16.dp))

        // 信息
        Column {
            Text(
                text = info?.name ?: "加载中..."
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "粉丝数：${info?.fansCount ?: 0}  ·  作品数：${info?.productCount ?: 0}"
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}


@Composable
fun DesignerProductItem(
    product: DesignerViewModel.DesignerProductData,
    isDark: Boolean,
    index: Int,
    navController: NavController
) {
    val backgroundColors = if (isDark) {
        listOf(
            Color(0xFF6B6B6A),
            Color(0xFF858581),
            Color(0xFF8B8C86),
            Color(0xFF8F8E83),
            Color(0xFF90927E)
        )
    } else {
        listOf(
            Color(0xFFF4F2F1),
            Color(0xFFEBEBE7),
            Color(0xFFF1F2EC),
            Color(0xFFF5F4E9),
            Color(0xFFF6F8E4)
        )
    }
    val cardColor = backgroundColors[index % backgroundColors.size]
    Card(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    navController.navigate(FontPageList.detail(product.uuid))
                }
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .height(70.dp)
            .fillMaxWidth(),
        colors = CardDefaults.defaultColors(
            color = cardColor
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(width = 240.dp, height = 30.dp)
            )
        }
    }

}