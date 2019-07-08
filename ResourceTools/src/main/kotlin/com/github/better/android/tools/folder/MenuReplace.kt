package com.github.better.android.tools.folder

import com.github.better.android.tools.ResToolsConfig
import com.github.better.android.tools.base.BaseFolderResReplace
import com.github.better.android.tools.debug
import java.io.FilenameFilter
import java.util.*

/**
 * Menu 文件夹
 * @menu/xxx 暂时没有，所以 res 先不处理
 * Created by better On 2019-07-07.
 */
class MenuReplace(config: ResToolsConfig) : BaseFolderResReplace(config) {

    /**
     * 资源名称
     */
    private val RES_TYPE_NAME = "menu"
    private val DIR_FILTER = FilenameFilter { file, s -> file.isDirectory && s.startsWith(RES_TYPE_NAME) }

    override val resTypeName: String = RES_TYPE_NAME
    override val javaRegex: String = """(R(\s*?)\.(\s*?)menu(\s*?)\.(\s*?))(\w+)"""
    override val xmlRegex: String = """(@menu/)(\w+)"""

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