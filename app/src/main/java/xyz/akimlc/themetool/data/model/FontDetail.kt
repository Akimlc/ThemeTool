package xyz.akimlc.themetool.data.model

data class FontDetail(
    val fontName: String,   //字体名字
    val fontAuthor: String, //字体作者
    val fontAuthorIcon: String, //作者头像
    val previewUrl: List<String>,    //预览图
    val fontDownloadUrl: String //下载链接
)