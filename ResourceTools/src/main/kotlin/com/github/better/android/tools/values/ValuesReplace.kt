package com.github.better.android.tools.values

import com.github.better.android.tools.ResToolsConfig
import com.github.better.android.tools.base.BaseReplace
import com.github.better.android.tools.debug
import java.io.FilenameFilter
import java.util.LinkedHashSet

/**
 * values (包括 values-xx)下的
- string
- string_arrays
- color
- dimens
- bool
- integer
- attr not support
- style not support well need repaired
 * Created by better On 2019-07-07.
 */
class ValuesReplace(config: ResToolsConfig) : BaseReplace(config) {
    private val DIR_FILTER = FilenameFilter { file, s -> file.isDirectory && s.startsWith("values") }

    companion object {
        val ALL_VALUES_TYPES = LinkedHashSet<ValuesType>().apply {
            this.add(ValuesType.string)
            this.add(ValuesType.string_arrays)
            this.add(ValuesType.color)
            this.add(ValuesType.bool)
            this.add(ValuesType.integer)
            this.add(ValuesType.dimens)

            // not support now
            this.add(ValuesType.attr)
            this.add(ValuesType.style)
        }
    }

    /**
     * 替换values下的资源名称
     */
    fun replaceValues(set: Set<ValuesType>) {
        set.forEach {
            when (it) {
                ValuesType.string -> strings(it)
                ValuesType.string_arrays -> arrays(it)
                ValuesType.color -> color(it)
                ValuesType.dimens -> dimens(it)
                ValuesType.bool -> bool(it)
                ValuesType.integer -> integer(it)

                ValuesType.style -> style(it)
                ValuesType.attr -> attr(it)
            }
        }
    }

    // 字符串
    private fun strings(valueType: ValuesType) {
        debug("------------ replace strings resource (处理strings资源)")
        val stringNameSet = getValueNameSet(valueType.xml_Regx)
        // 修改源码中的
        replaceSrcDir(srcDir, stringNameSet, valueType.java_Regx)
        // 修改xml中的名称
        replaceResDir(resDir, stringNameSet, valueType.xml_Regx, null, true)
        // 修改xml中的引用
//        replaceResDir(resDir, stringNameSet, valueType.xml_ref_regex, null)
    }

    // string-array
    private fun arrays(valueType: ValuesType) {
        debug("------------ replace string-array resource (处理 string-array 资源)")
        val nameSet = getValueNameSet(valueType.xml_Regx)
        // 修改源码中的
        replaceSrcDir(srcDir, nameSet, valueType.java_Regx)
        // 修改xml中的名称
        replaceResDir(resDir, nameSet, valueType.xml_Regx, null, true)
    }

    // color
    private fun color(valueType: ValuesType) {
        debug("------------ replace color resource (处理 color 资源)")
        val nameSet = getValueNameSet(valueType.xml_Regx)
        replaceSrcDir(srcDir, nameSet, valueType.java_Regx)
        // 修改xml中的名称
        replaceResDir(resDir, nameSet, valueType.xml_Regx, null, true)
        // 修改xml中的引用
        replaceResDir(resDir, nameSet, valueType.xml_ref_regex, null)
    }

    // dimens
    private fun dimens(valueType: ValuesType) {
        debug("------------ replace dimens resource (处理 dimens 资源")
        val nameSet = getValueNameSet(valueType.xml_Regx)

        replaceSrcDir(srcDir, nameSet, valueType.java_Regx)
        // 修改xml中的名称
        replaceResDir(resDir, nameSet, valueType.xml_Regx, null, true)
        // 修改xml中的引用
        replaceResDir(resDir, nameSet, valueType.xml_ref_regex, null)
    }

    // bool
    private fun bool(valueType: ValuesType) {
        debug("------------ replace bool resource (处理 bool 资源)")
        val nameSet = getValueNameSet(valueType.xml_Regx)

        replaceSrcDir(srcDir, nameSet, valueType.java_Regx)
        // 修改xml中的名称
        replaceResDir(resDir, nameSet, valueType.xml_Regx, null, true)
        // 修改xml中的引用
        replaceResDir(resDir, nameSet, valueType.xml_ref_regex, null)
    }

    // integer
    private fun integer(valueType: ValuesType) {
        debug("------------ replace integer resource (处理 integer 资源)")
        val nameSet = getValueNameSet(valueType.xml_Regx)

        replaceSrcDir(srcDir, nameSet, valueType.java_Regx)
        // 修改xml中的名称
        replaceResDir(resDir, nameSet, valueType.xml_Regx, null, true)
        // 修改xml中的引用
        replaceResDir(resDir, nameSet, valueType.xml_ref_regex, null)
    }

    private fun style(valueType: ValuesType) {
        debug("----> sorry style replace is not implement now ( 暂没有实现)")
    }

    private fun attr(valueType: ValuesType) {
        debug("----> sorry attr  replace is not implements now (暂没有实现)")
    }


    /**
     * 读取xml资源，获取values资源名称 set
     * @param xmlRegex
     */
    private fun getValueNameSet(xmlRegex: String): Set<String> {
        val nameSet = HashSet<String>()     // 字符串
        // 1.获取所有values开头的文件夹
        resDir.listFiles(DIR_FILTER)?.forEach { dir ->
            val pattern = Regex(xmlRegex).toPattern()
            // 2.遍历文件夹下各个资源文件xml后缀，获取资源名称
            dir.listFiles().forEach { file ->
                if (file.name.endsWith(".xml")) {
                    file.readLines().forEach { line ->
                        val matcher = pattern.matcher(line)
                        while (matcher.find()) {
                            nameSet.add(matcher.group(2))
                        }
                    }
                }
            }
        }

        return nameSet
    }


    /** values 文件下的资料类型
     */
    enum class ValuesType(xmlRegex: String, javaRegex: String, xml_ref_regex: String) {

        /*
         <string name="better_me_app_name>appName</string>
         <string name="better_me_app_name ">   appName</string>
         <string name="better_me_app_name">appName</string>
         */

        string("(<string\\s+name\\s*=\\s*[\\\"'])(\\w+)(\\s*.*[\\\"']\\s*>)",
                "(R(\\s*?)\\.(\\s*?)string(\\s*?)\\.(\\s*?))(\\w+)",
                "(@string/)(\\w+)"
        ),

        string_arrays("(<string-array\\s+name\\s*=\\s*[\\\"'])(\\w+)(\\s*.*[\\\"']\\s*>)",
                "(R(\\s*?)\\.(\\s*?)array(\\s*?)\\.(\\s*?))(\\w+)",
                "(<string-array\\s+name\\s*=\\s*\\\")(.+?)(\\\">)"
        ),

        color("(<color\\s+name\\s*=\\s*[\\\"'])(\\w+)(\\s*.*[\\\"']\\s*>)",
                "(R(\\s*?)\\.(\\s*?)color(\\s*?)\\.(\\s*?))(\\w+)",
                "(@color/)(\\w+)"
        ),

        dimens("(<dimen\\s+name\\s*=\\s*[\\\"'])(\\w+)(\\s*.*[\\\"']\\s*>)",
                "(R(\\s*?)\\.(\\s*?)dimen(\\s*?)\\.(\\s*?))(\\w+)",
                "(@dimen/)(\\w+)"
        ),

        bool("(<bool\\s+name\\s*=\\s*[\\\"'])(\\w+)(\\s*.*[\\\"']\\s*>)",
                "(R(\\s*?)\\.(\\s*?)bool(\\s*?)\\.(\\s*?))(\\w+)",
                "(@bool/)(\\w+)"
        ),

        integer("(<integer\\s+name\\s*=\\s*[\\\"'])(\\w+)(\\s*.*[\\\"']\\s*>)",
                "(R(\\s*?)\\.(\\s*?)integer(\\s*?)\\.(\\s*?))(\\w+)",
                "(@integer/)(\\w+)"
        ),

        attr("",
                "",
                ""
        ),

        style("(<style\\s+name\\s*=\\s*\\\")(.+?\\\")(.*>)",
                "(R(\\s*?)\\.(\\s*?)style(\\s*?)\\.(\\s*?))(\\w+)",
                "(@style/)(.+?\\\")"
        );

        /**
         * 源代码中
         * like: R.string.XXX
         */
        val java_Regx: String
        /**
         * xml 中
         * like: <string name="empty_message">XXX</string>
         */
        val xml_Regx: String
        /**
         * xml 引用中
         * like:  <string name="empty_message">@string/empty_message</string>
         */
        val xml_ref_regex: String

        init {
            this.java_Regx = javaRegex
            this.xml_Regx = xmlRegex
            this.xml_ref_regex = xml_ref_regex
        }
    }
}

