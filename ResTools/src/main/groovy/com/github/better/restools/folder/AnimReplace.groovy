package com.github.better.restools.folder

import com.github.better.restools.ResToolsConfiguration
import com.github.better.restools.Tools
import com.github.better.restools.base.BaseFolderResReplace

/**
 * anim
 */
class AnimReplace extends BaseFolderResReplace {

    private def final DIR_FILTER = new Tools.DirNamePrefixFilter("anim")
    private def final RES_TYPE_NAME = "anim"

    AnimReplace(ResToolsConfiguration config) {
        super(config)
    }

    @Override
    String getResTypeName() {
        return RES_TYPE_NAME
    }

    @Override
    String getJavaRegex() {
        return "(R(\\s*?)\\.(\\s*?)anim(\\s*?)\\.(\\s*?))(\\w+)"
    }

    @Override
    String getXmlRegex() {
        return "(@anim/)(\\w+)"
    }

    @Override
    Set<String> getResNameSet() {
        Set<String> resNameSet = new HashSet<>()
        // 1.获取所有drawable开头的文件夹
        File[] dirs = resDir.listFiles(DIR_FILTER)
        // 2.获取drawable名字并存储
        dirs?.each { dir ->
            dir.eachFile { it ->
                String fileName = it.name.substring(0, it.name.lastIndexOf("."))
                resNameSet.add(fileName)
            }
        }
        return resNameSet
    }

    @Override
    void replaceSrc(Set<String> resNameSet, Object java_regx) throws IOException {
        println("---------- $RES_TYPE_NAME ----- replace source folder start...")
        replaceSrcDir(srcDir, resNameSet, java_regx)
        println("---------- $RES_TYPE_NAME ----- replace source folder end")
    }

    @Override
    void replaceRes(Set<String> resNameSet, Object xml_regx) throws IOException {
        // 1.替换文件内容
        println("---------- $RES_TYPE_NAME ----- replace res folder start...")
        replaceResDir(resDir, resNameSet, xml_regx, null)
        println("---------- $RES_TYPE_NAME ----- replace res folder end")

        // 2.修改文件名
        println("---------- $RES_TYPE_NAME ----- rename start...")
        renameFile(resDir, resNameSet, DIR_FILTER, RES_TYPE_NAME)
        println("---------- $RES_TYPE_NAME ----- rename end")
    }
}
