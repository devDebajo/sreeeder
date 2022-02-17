package ru.debajo.reader.rss.ui.common

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import ru.debajo.reader.rss.R

object FontFamilies {
    val robotoSerif = FontFamily(
        Font(R.font.roboto_serif_thin, FontWeight.W100),
        Font(R.font.roboto_serif_extra_light, FontWeight.W200),
        Font(R.font.roboto_serif_light, FontWeight.W300),
        Font(R.font.roboto_serif_regular, FontWeight.W400),
        Font(R.font.roboto_serif_medium, FontWeight.W500),
        Font(R.font.roboto_serif_semi_bold, FontWeight.W600),
        Font(R.font.roboto_serif_bold, FontWeight.W700),
        Font(R.font.roboto_serif_extra_bold, FontWeight.W800),
        Font(R.font.roboto_serif_black, FontWeight.W900),

        Font(R.font.roboto_serif_thin_italic, FontWeight.W100, FontStyle.Italic),
        Font(R.font.roboto_serif_extra_light_italic, FontWeight.W200, FontStyle.Italic),
        Font(R.font.roboto_serif_light_italic, FontWeight.W300, FontStyle.Italic),
        Font(R.font.roboto_serif_italic, FontWeight.W400, FontStyle.Italic),
        Font(R.font.roboto_serif_medium_italic, FontWeight.W500, FontStyle.Italic),
        Font(R.font.roboto_serif_semi_bold_italic, FontWeight.W600, FontStyle.Italic),
        Font(R.font.roboto_serif_bold_italic, FontWeight.W700, FontStyle.Italic),
        Font(R.font.roboto_serif_extra_bold_italic, FontWeight.W800, FontStyle.Italic),
        Font(R.font.roboto_serif_black_italic, FontWeight.W900, FontStyle.Italic),
    )
}