package com.github.better.restools.folder

import com.github.better.restools.ResToolsConfiguration
import com.github.better.restools.Tools
import com.github.better.restools.base.BaseFolderResReplace

import java.util.regex.Matcher


/**
 * layout 布局资源
 */
public class LayoutReplace extends BaseFolderResReplace {

    private def final DIR_FILTER = new Tools.DirNamePrefixFilter("layout")
    private def final RES_TYPE_NAME = "layout"

    LayoutReplace(ResToolsConfig config) {
        super(config)
    }

    @Override
    String getResTypeName() {
        return RES_TYPE_NAME
    }

    @Override
    String getJavaRegex() {
        // group 6 为名字
        return "(R(\\s*?)\\.(\\s*?)layout(\\s*?)\\.(\\s*?))(\\w+)"
    }

    @Override
    String getXmlRegex() {
        // group 2为名字
        return "(@layout/)(\\w+)"
    }

    @Override
    Set<String> getResNameSet() {
        Set<String> layoutNameSet = new HashSet<>()
        // 1.获取所有layout开头的文件夹
        File[] layoutDirs = resDir.listFiles(DIR_FILTER)
        // 2.获取layout名字并存储
        layoutDirs?.each { layoutDir ->
            layoutDir.eachFile { it ->
                layoutNameSet.add(it.name.substring(0, it.name.lastIndexOf(".")))
            }
        }
        return layoutNameSet
    }

    @Override
    void replaceSrc(Set<String> resNameSet, java_regx) throws IOException {
        println("---------- layout ----- replace source folder start...")
        replaceSrcDir(srcDir, resNameSet, java_regx)

        // 替换kt源代码文件 layout 文件
        replaceKtSrcFile(srcDir, resNameSet)

        println("---------- layout ----- replace source folder end")
    }


    @Override
    void replaceRes(Set<String> resNameSet, xml_regx) throws IOException {
        println("---------- layout ----- replace res folder start...")
        // 1.替换文件内容，经验证，xml文件夹的小部分布局，可能引用到了layout文件，所以filter 传 null
        replaceResDir(resDir, resNameSet, xml_regx, null)
        println("---------- layout ----- replace res folder end")

        // 2.修改文件名
        println("---------- layout ----- rename start...")
        renameFile(resDir, resNameSet, DIR_FILTER, RES_TYPE_NAME)
        println("---------- layout ----- rename end")
    }


    /**
     * kt 文件特殊处理，layout 导入包时，需要重新替换，如：
     *     import  kotlinx.android.synthetic.main.activity_main.*
     * 替换成：
     *     import  kotlinx.android.synthetic.main.new_prefix_activity_main.*
     * @param file
     * @param set
     */
    private void replaceKtSrcFile(File file, Set<String> set) {
        if (file.exists()) {
            if (file.isDirectory()) {
                file.eachFile { it -> replaceKtSrcFile(it, set) }
            } else {
                if (file.name.endsWith(".kt")) {  // kt
                    handleKtSrcFileLayout(file, set)
                }
            }
        }
    }

    private void handleKtSrcFileLayout(file, set) {
        def regex = "(synthetic.main\\.)(\\w+)(\\.)"
        String fileContent = file.getText()              // every file is a text file
        StringBuffer sb = new StringBuffer()             // result content
        Matcher matcher = fileContent =~ regex
        while (matcher.find()) {
            String oldResName = matcher.group(2)   // the old res name
            if (set.contains(oldResName)) {               // 本模块中包含的资源名，才替换
                String newResName = config.new_prefix + oldResName
                if (oldResName.startsWith(config.old_prefix)) {     // 替换掉旧的前缀
                    newResName = config.new_prefix + oldResName.substring(config.old_prefix.length())
                }
                matcher.appendReplacement(sb, "\$1$newResName\$3") // 拼接 保留$1$3分组,替换$2分组
            }
        }
        // 修改了文件时，才写入文件
        if (sb.length() > 0) {
            matcher.appendTail(sb)              // 添加结尾
            file.write(sb.toString())           // 写回文件
        }
    }
}
