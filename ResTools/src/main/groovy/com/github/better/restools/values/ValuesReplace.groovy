package com.github.better.restools.values

import com.github.better.restools.ResToolsConfiguration
import com.github.better.restools.Tools
import com.github.better.restools.base.BaseReplace

import java.util.regex.Matcher

/**
 - string
 - string_arrays
 - color
 - dimens
 - bool
 - integer
 - attr not support
 - style not support well need repaired
 */
class ValuesReplace extends BaseReplace {

    private def final DIR_FILTER = new Tools.DirNamePrefixFilter("values")
    public static final Set<ValuesType> ALL_VALUES_TYPES

    static {
        ALL_VALUES_TYPES = new LinkedHashSet<>()
        ALL_VALUES_TYPES.add(ValuesType.string)
        ALL_VALUES_TYPES.add(ValuesType.string_arrays)
        ALL_VALUES_TYPES.add(ValuesType.color)
        ALL_VALUES_TYPES.add(ValuesType.bool)
        ALL_VALUES_TYPES.add(ValuesType.integer)
        ALL_VALUES_TYPES.add(ValuesType.dimens)

        // not support now
        ALL_VALUES_TYPES.add(ValuesType.attr)
        ALL_VALUES_TYPES.add(ValuesType.style)
    }

    public ValuesReplace(ResToolsConfiguration config) {
        super(config)
    }

    /**
     * values 类型
     */
    public static enum ValuesType {
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
        )

        /**
         * 源代码中
         * like: R.string.XXX
         */
        String java_Regx
        /**
         * xml 中
         * like: <string name="empty_message">XXX</string>
         */
        String xml_Regx
        /**
         * xml 引用中
         * like:  <string name="empty_message">@string/empty_message</string>
         */
        String xml_ref_regex

        ValuesType(String xmlRegx, String javaRegx, String xml_ref_regex) {
            this.xml_Regx = xmlRegx
            this.java_Regx = javaRegx
            this.xml_ref_regex = xml_ref_regex
        }
    }

    void replaceValues(Set<ValuesType> set) {
        set.forEach {
            switch (it) {
                case ValuesType.string:
                    strings(it)
                    break
                case ValuesType.string_arrays:
                    arrays(it)
                    break
                case ValuesType.color:
                    color(it)
                    break
                case ValuesType.dimens:
                    dimens(it)
                    break
                case ValuesType.bool:
                    bool(it)
                    break
                case ValuesType.integer:
                    integer(it)
                    break
                case ValuesType.style:      // need implement
                    style(it)
                    break
                case ValuesType.attr:       // need implement
                    attr(it)
                    break
            }
        }
    }

    // 字符串
    private def strings(ValuesType valueType) {
        println("------------ replace strings resource (处理strings资源)")
        def stringNameSet = getValueNameSet(valueType.xml_Regx)
        def java_regx = ~valueType.java_Regx
        def xml_regx = ~valueType.xml_Regx
        def xml_ref_regx = ~valueType.xml_ref_regex
        // 修改源码中的
        replaceSrcDir(srcDir, stringNameSet, java_regx)
        // 修改xml中的名称
        replaceResDir(resDir, stringNameSet, xml_regx, null, true)
        // 修改xml中的引用
        replaceResDir(resDir, stringNameSet, xml_ref_regx, null)
    }

    // string-array
    private def arrays(ValuesType valueType) {
        println("------------ replace string-array resource (处理 string-array 资源)")
        def nameSet = getValueNameSet(valueType.xml_Regx)
        def java_regx = ~valueType.java_Regx
        def xml_regx = ~valueType.xml_Regx
        def xml_ref_regx = ~valueType.xml_ref_regex  // array 引用没有实现

        // 修改源码中的
        replaceSrcDir(srcDir, nameSet, java_regx)
        // 修改xml中的名称
        replaceResDir(resDir, nameSet, xml_regx, null, true)
    }

    // color
    private def color(ValuesType valueType) {
        println("------------ replace color resource (处理 color 资源)")
        def nameSet = getValueNameSet(valueType.xml_Regx)
        def java_regx = ~valueType.java_Regx
        def xml_regx = ~valueType.xml_Regx
        def xml_ref_regx = ~valueType.xml_ref_regex

        replaceSrcDir(srcDir, nameSet, java_regx)
        // 修改xml中的名称
        replaceResDir(resDir, nameSet, xml_regx, null, true)
        // 修改xml中的引用
        replaceResDir(resDir, nameSet, xml_ref_regx, null)
    }

    // dimens
    private def dimens(ValuesType valueType) {
        println("------------ replace dimens resource (处理 dimens 资源")
        def nameSet = getValueNameSet(valueType.xml_Regx)
        def java_regx = ~valueType.java_Regx
        def xml_regx = ~valueType.xml_Regx
        def xml_ref_regx = ~valueType.xml_ref_regex

        replaceSrcDir(srcDir, nameSet, java_regx)
        // 修改xml中的名称
        replaceResDir(resDir, nameSet, xml_regx, null, true)
        // 修改xml中的引用
        replaceResDir(resDir, nameSet, xml_ref_regx, null)
    }

    // bool
    private def bool(ValuesType valueType) {
        println("------------ replace bool resource (处理 bool 资源)")

        def nameSet = getValueNameSet(valueType.xml_Regx)
        def java_regx = ~valueType.java_Regx
        def xml_regx = ~valueType.xml_Regx
        def xml_ref_regx = ~valueType.xml_ref_regex

        replaceSrcDir(srcDir, nameSet, java_regx)
        // 修改xml中的名称
        replaceResDir(resDir, nameSet, xml_regx, null, true)
        // 修改xml中的引用
        replaceResDir(resDir, nameSet, xml_ref_regx, null)
    }

    // integer
    private def integer(ValuesType valueType) {
        println("------------ replace integer resource (处理 integer 资源)")

        def nameSet = getValueNameSet(valueType.xml_Regx)
        def java_regx = ~valueType.java_Regx
        def xml_regx = ~valueType.xml_Regx
        def xml_ref_regx = ~valueType.xml_ref_regex

        replaceSrcDir(srcDir, nameSet, java_regx)
        // 修改xml中的名称
        replaceResDir(resDir, nameSet, xml_regx, null, true)
        // 修改xml中的引用
        replaceResDir(resDir, nameSet, xml_ref_regx, null)
    }

    private def style(ValuesType valueType) {
        // println("------------ 处理 style 资源")
        println("----> sorry style replace is not implement now ( 暂没有实现)")
    }

    private def attr(ValuesType valueType) {
        // println("------------ 处理 attr 资源")
        println("----> sorry attr  replace is not implements now (暂没有实现)")
    }

    // ==========================================================

    /**
     * 读取xml资源，获取values资源名称 set
     * @param xmlRegex
     */
    private def getValueNameSet(xmlRegex) {
        Set<String> nameSet = new HashSet<>()     // 字符串
        // 1.获取所有values开头的文件夹
        File[] dirs = resDir.listFiles(DIR_FILTER)
        // 2.遍历文件夹下各个资源文件xml后缀，获取资源名称
        dirs?.each { dir ->
            dir.eachFile { it ->
                if (it.name.endsWith(".xml")) {
                    it.eachLine { line ->
                        Matcher matcher = line =~ xmlRegex
                        while (matcher.find()) {
                            nameSet.add(matcher.group(2))
                        }
                    }
                }
            }
        }
        return nameSet
    }
}
