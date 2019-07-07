package com.github.better.android.tools.folder

import com.github.better.android.tools.ResToolsConfig
import com.github.better.android.tools.base.BaseFolderResReplace
import com.github.better.android.tools.debug
import java.io.FilenameFilter
import java.util.*

/**
 * mipmap 文件夹
 * Created by better On 2019-07-07.
 */
class MipmapReplace(config: ResToolsConfig) : BaseFolderResReplace(config) {

    /**
     * 资源名称
     */
    private val RES_TYPE_NAME = "mipmap"
    private val DIR_FILTER = FilenameFilter { file, s -> file.isDirectory && s.startsWith(RES_TYPE_NAME) }

    override val resTypeName: String = RES_TYPE_NAME
    override val javaRegex: String = """(R(\s*?)\.(\s*?)mipmap(\s*?)\.(\s*?))(\w+)"""
    override val xmlRegex: String = """(@mipmap/)(\w+)"""

    override fun getResNameSet(): Set<String> {
        return resDir.listFiles(DIR_FILTER)?.map {
            var suffixName = it.name.substringBeforeLast(".")
            if (suffixName.endsWith(".9")) {
                suffixName = suffixName.substringBeforeLast(".")
            }
            suffixName
        }?.toSet() ?: Collections.emptySet()
    }

    override fun replaceSrc(resNameSet: Set<String>, regex: String) {
        debug("---------- $RES_TYPE_NAME ----- replace source folder start...")
        replaceSrcDir(srcDir, resNameSet, javaRegex)
        debug("---------- $RES_TYPE_NAME ----- replace source folder end")
    }

    override fun replaceRes(resNameSet: Set<String>, regex: String) {
        // 1.替换文件内容
        debug("---------- $RES_TYPE_NAME ----- replace res folder start...")
        replaceResDir(resDir, resNameSet, xmlRegex, null)
        debug("---------- $RES_TYPE_NAME ----- replace res folder end")

        // 2.修改文件名
        debug("---------- $RES_TYPE_NAME ----- rename start...")
        renameFile(resDir, resNameSet, DIR_FILTER, RES_TYPE_NAME)
        debug("---------- $RES_TYPE_NAME ----- rename end")
    }

}