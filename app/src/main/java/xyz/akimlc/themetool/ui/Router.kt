package xyz.akimlc.themetool.ui

object Route {
    const val MAIN = "main"
    const val HOME = "$MAIN/home"
    const val SETTINGS = "$MAIN/settings"
    const val ABOUT = "$MAIN/about"
    const val DOWNLOAD = "$MAIN/download"
    const val FONT_DETAIL = "$MAIN/font/{uuid}"
}

object ThemePageList {
    // 主题主页（比如主题搜索、推荐等）
    const val HOME = "theme/home"

    // 主题搜索页
    const val SEARCH = "theme/search"

    // 主题解析页（如解析 MTZ/ZIP）
    const val PARSE = "theme/parse"

    // 主题详情页（带参数 UUID）
    const val DETAIL = "theme/detail/{uuid}"

    // 构造带参数的详情页跳转路径
}

object FontPageList {
    // 字体搜索页
    const val SEARCH = "font/search"

    // 字体详情页（带 uuid 参数）
    const val DETAIL = "font/detail/{uuid}"

    fun detail(uuid: String) = "font/detail/$uuid"

    // MTZ 字体打包页
    const val MTZ = "font/mtz"

    // ZIP 字体打包页
    const val ZIP = "font/zip"

    //设计师界面
    const val DESIGNER = "font/designer"
}

object AboutPageList {
    //引用页面
    const val REFERENCES = "about/references"

    //感谢页面
    const val THANKS = "about/thanks"

    //捐赠页面
    const val DONATION = "about/donation"
}