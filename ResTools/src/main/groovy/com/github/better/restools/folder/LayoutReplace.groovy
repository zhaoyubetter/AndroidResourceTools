package com.github.better.restools.folder

import com.github.better.restools.ResToolsConfiguration
import com.github.better.restools.Tools
import com.github.better.restools.base.BaseFolderResReplace


/**
 * layout 布局资源
 */
public class LayoutReplace extends BaseFolderResReplace {

    private def final DIR_FILTER = new Tools.DirNamePrefixFilter("layout")
    private def final RES_TYPE_NAME = "layout"

    LayoutReplace(ResToolsConfiguration config) {
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
}
