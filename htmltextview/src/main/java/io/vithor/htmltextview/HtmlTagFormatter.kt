package io.vithor.htmltextview

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.Layout
import android.text.Spanned
import android.text.style.*
import android.util.Log
import org.xml.sax.Attributes
import java.util.*
import java.util.regex.Pattern

class HtmlTagFormatter {
    private val mListParents = mutableListOf<String>()
    private var mListItemCount = 0

    internal var mTagStyle = HashMap<String, String>()
    internal var mTagStartIndex = HashMap<String, Int>()

    private var hrefValue: String? = null

    @Throws(NumberFormatException::class)
    fun handlerHtmlContent(context: Context, htmlContent: String): Spanned {
        return HtmlParser.buildSpannedText(htmlContent) { opening, tag, output, attributes ->
            when (tag) {
                Tag.SPAN.name, Tag.P.name -> if (opening) {
                    mTagStartIndex.put(tag, output.length)

                    var styleContent = HtmlParser.getValue(attributes, Attribute.STYLE.name)
                    styleContent = handleAlignAttribute(attributes, styleContent)
                    styleContent = handleColorAttribute(attributes, styleContent)

                    mTagStyle.put(tag, styleContent)
                } else {
                    handleStyleAttribute(output, tag, context)
                    mTagStyle.put(tag, "")
                }
                Tag.A.name -> if (opening) {
                    mTagStartIndex.put(tag, output.length)

                    var styleContent = HtmlParser.getValue(attributes, Attribute.STYLE.name)
                    styleContent = handleAlignAttribute(attributes, styleContent)
                    styleContent = handleColorAttribute(attributes, styleContent)

                    hrefValue = HtmlParser.getValue(attributes, Attribute.HREF.name)

                    mTagStyle.put(tag, styleContent)
                } else {
                    handleHrefAttribute(output, tag, hrefValue!!)
                    handleStyleAttribute(output, tag, context)
                    mTagStyle.put(tag, "")
                }
                Tag.UL.name, Tag.OL.name, Tag.DD.name -> {
                    if (opening) {
                        mListParents.add(tag)
                    } else
                        mListParents.remove(tag)

                    mListItemCount = 0
                }
                Tag.LI.name -> if (!opening) {
                    handleListTag(output)
                }
            }
            false
        }
    }

    private fun handleHrefAttribute(output: Editable, tag: String, link: String) {
        val startIndex = mTagStartIndex[tag]!!
        val stopIndex = output.length

//        output.removeSpan(output.getSpans(startIndex, stopIndex, URLSpan::class.java).first())
        output.setSpan(URLSpan(link), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun handleColorAttribute(attributes: Attributes?, styleContent: String?): String? {
        return handleAttribute(Attribute.COLOR.name, Style.FontColor.name,
                attributes, styleContent)
    }

    private fun handleAlignAttribute(attributes: Attributes?, styleContent: String?): String? {
        return handleAttribute(Attribute.ALIGN.name, Style.TextAlign.name,
                attributes, styleContent)
    }

    private fun handleAttribute(name: String, styleName: String, attributes: Attributes?, styleContent: String?): String? {
        return HtmlParser.getValue(attributes, name)?.let { alignContent ->
            if (styleContent.isNullOrBlank())
                return@let styleName + ":" + alignContent

            var newStyle = styleContent!!

            if (!newStyle.endsWith(";")) newStyle += ";"

            newStyle += styleName + ":" + alignContent

            return@let newStyle
        } ?: styleContent
    }

    private fun handleListTag(output: Editable) {
        if (mListParents.last() == "ul") {
            output.append("\n")
            val split = output.toString().split("\n".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()

            val lastIndex = split.size - 1
            val start = output.length - split[lastIndex].length - 1
            output.setSpan(BulletSpan(15), start, output.length, 0)
        } else if (mListParents.last() == "ol") {
            mListItemCount++
            output.append("\n")
            val split = output.toString().split("\n".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()

            val lastIndex = split.size - 1
            val start = output.length - split[lastIndex].length - 1
            output.insert(start, "$mListItemCount. ")
            output.setSpan(LeadingMarginSpan.Standard(15 * mListParents.size), start, output.length, 0)
        }
    }

    private fun handleAlignStyle(output: Editable, parentTag: String, alignTag: String) {
        val startIndex = mTagStartIndex[parentTag]
        val stopIndex = output.length

        val alinspan: AlignmentSpan = when (alignTag) {
            "center" -> AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER)
            "right" -> AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE)
            "left" -> AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL)
            else -> AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL)
        }
        output.setSpan(alinspan, startIndex!!, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun handleStyleAttribute(output: Editable, tag: String, context: Context) {
        val styleContent = mTagStyle[tag]
        val startIndex = mTagStartIndex[tag]!!
        val stopIndex = output.length

        if (!styleContent.isNullOrBlank()) {
            styleContent!!.split(';').map { styles -> styles.split(':') }.forEach { pair ->

                val (styleName: String?, value: String?) = pair

                when (styleName) {
                    Style.FontSize.name -> {
                        val size = Integer.valueOf(getAllNumbers(value))!!
                        Log.i("tag", "$size")
                        output.setSpan(AbsoluteSizeSpan(sp2px(context, size.toFloat())), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    Style.BackgroundColor.name -> {
                        output.setSpan(BackgroundColorSpan(Color.parseColor(value)), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    Style.FontColor.name -> {
                        val str = output.toString()
                        output.setSpan(ForegroundColorSpan(Color.parseColor(value)), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    Style.TextAlign.name -> {
                        handleAlignStyle(output, tag, value)
                    }
                }
            }
        }
    }

    enum class Tag(name: String) {
        A("a"),
        P("p"),
        UL("ul"),
        OL("ol"),
        LI("li"),
        DD("dd"),
        SPAN("span")
    }

    enum class Attribute(name: String) {
        HREF("href"),
        STYLE("style"),
        ALIGN("align"),
        COLOR("color"),
    }

    enum class Style(name: String) {
        TextAlign("text-align"),
        FontColor("color"),
        BackgroundColor("background-color"),
        FontSize("font-size"),
    }

    companion object {
        private fun getAllNumbers(body: String): String {
            val pattern = Pattern.compile("\\d+")
            val matcher = pattern.matcher(body)
            while (matcher.find()) {
                return matcher.group(0)
            }
            return ""
        }

        fun sp2px(context: Context, spValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }
    }
}
