package com.github.better.android.tools.folder

import com.github.better.android.tools.ResToolsConfig
import com.github.better.android.tools.base.BaseFolderResReplace
import com.github.better.android.tools.debug
import java.io.FilenameFilter
import java.util.*

/**
 * raw
 * @raw/xxx 暂时没有，所以res先不处理
 * Created by better On 2019-07-07.
 */
class RawReplace(config: ResToolsConfig) : BaseFolderResReplace(config) {

    /**
     * 资源名称
     */
    private val RES_TYPE_NAME = "raw"
    /**
     * 需要过滤掉 animator
     */
    private val DIR_FILTER = FilenameFilter { file, s -> file.isDirectory && s.startsWith(RES_TYPE_NAME) && !s.startsWith("animator") }

    override val resTypeName: String = RES_TYPE_NAME
    override val javaRegex: String = """(R(\s*?)\.(\s*?)raw(\s*?)\.(\s*?))(\w+)"""
    override val xmlRegex: String = """(@raw/)(\w+)"""

    /**
     * 过滤掉 animator 的文件夹
     * 获取 res/anim/下的所有文件，并将文件转换成集合
     * 如：[res/anim/better.xml,res/anim/cz.xml] 返回 [better, cz]
     */
    override fun getResNameSet(): Set<String> {
        val layoutNameSet = HashSet<String>()
        // 1.获取所有layout开头的文件夹，并获取下面的所有的文件
        resDir.listFiles(DIR_FILTER)?.forEach { dir ->
            dir.listFiles().forEach { file ->
                layoutNameSet.add(file.name.substring(0, file.name.lastIndexOf(".")))
            }
        }
        return layoutNameSet
    }

    override fun replaceSrc(resNameSet: Set<String>, regex: String) {
        debug("---------- $RES_TYPE_NAME ----- replace source folder start...")
        replaceSrcDir(srcDir, resNameSet, javaRegex)
        debug("---------- $RES_TYPE_NAME ----- replace source folder end")
    }

    override fun replaceRes(resNameSet: Set<String>, regex: String) {
        // 1.替换文件内容
        debug("---------- $RES_TYPE_NAME ----- $RES_TYPE_NAME not used in the res folder now, so replace res not implemented.")

        // 2.修改文件名
        debug("---------- $RES_TYPE_NAME ----- rename start...")
        renameFile(resDir, resNameSet, DIR_FILTER, RES_TYPE_NAME)
        debug("---------- $RES_TYPE_NAME ----- rename end")
    }

}