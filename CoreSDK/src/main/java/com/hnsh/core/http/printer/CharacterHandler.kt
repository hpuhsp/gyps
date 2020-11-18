package com.hnsh.core.http.printer

import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import okhttp3.internal.and
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.StringReader
import java.io.StringWriter
import java.util.regex.Pattern
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/29 15:10
 * @UpdateRemark:   更新说明：
 */
class CharacterHandler {
    companion object {
        val emojiFilter: InputFilter = object : InputFilter {
            // emoji过滤器
            var emoji = Pattern.compile(
                "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE or Pattern.CASE_INSENSITIVE
            )

            override fun filter(
                source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int
            ): CharSequence? {
                val emojiMatcher = emoji.matcher(source)
                return if (emojiMatcher.find()) {
                    ""
                } else null
            }
        }

        /**
         * 字符串转换成十六进制字符串
         *
         * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
         */
        fun str2HexStr(str: String): String {
            val chars = "0123456789ABCDEF".toCharArray()
            val sb = StringBuilder("")
            val bs = str.toByteArray()
            var bit: Int
            for (i in bs.indices) {
                bit = bs[i] and 0x0f0 shr 4
                sb.append(chars[bit])
                bit = bs[i] and 0x0f
                sb.append(chars[bit])
            }
            return sb.toString().trim { it <= ' ' }
        }

        /**
         * json 格式化
         *
         * @param json
         * @return
         */
        fun jsonFormat(json: String?): String? {
            if (json.isNullOrEmpty()) {
                return "Empty/Null json content"
            }
            var jsonStr = json
            var message: String? = null
            try {
                jsonStr = jsonStr.trim { it <= ' ' }
                message = if (jsonStr.startsWith("{")) {
                    val jsonObject = JSONObject(jsonStr)
                    jsonObject.toString(4)
                } else if (json.startsWith("[")) {
                    val jsonArray = JSONArray(jsonStr)
                    jsonArray.toString(4)
                } else {
                    jsonStr
                }
            } catch (e: JSONException) {
                message = jsonStr
            } catch (error: OutOfMemoryError) {
                message = "Output omitted because of Object size"
            }
            return message
        }

        /**
         * xml 格式化
         *
         * @param xml
         * @return
         */
        fun xmlFormat(xml: String?): String? {
            if (xml.isNullOrEmpty()) {
                return "Empty/Null xml content"
            }
            val message: String
            message = try {
                val xmlInput: Source =
                    StreamSource(StringReader(xml))
                val xmlOutput =
                    StreamResult(StringWriter())
                val transformer =
                    TransformerFactory.newInstance().newTransformer()
                transformer.setOutputProperty(OutputKeys.INDENT, "yes")
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
                transformer.transform(xmlInput, xmlOutput)
                xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n")
            } catch (e: TransformerException) {
                xml
            }
            return message
        }
    }
}