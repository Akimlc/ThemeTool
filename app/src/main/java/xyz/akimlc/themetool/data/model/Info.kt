package xyz.akimlc.themetool.data.model


class Info {
    data class ThemeInfo(
        val themeName: String,
        val themeUrl: String,
        val themeSize: Int // 单位：字节
    )

    data class FontInfo(
        val fontUrl: String,
        val fontSize: Int // 单位：字节
    )

    data class GlobalTheme(
        val name : String,
        val fileSize: String,
        val downloadUrl: String
    )

    data class DomesticFontInfo(
        val downloadUrl : String
    )
}
