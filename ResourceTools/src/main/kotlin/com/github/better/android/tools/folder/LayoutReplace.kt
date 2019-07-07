package com.github.better.android.tools.folder

import com.github.better.android.tools.ResToolsConfig
import com.github.better.android.tools.base.BaseFolderResReplace
import com.github.better.android.tools.debug
import java.io.File
import java.io.FilenameFilter
import java.util.*

/**
 * Layout
 * Created by better On 2019-07-07.
 */
class LayoutReplace(config: ResToolsConfig) : BaseFolderResReplace(config) {

    /**
     * 资源名称
     */
    private val RES_TYPE_NAME = "layout"
    private val DIR_FILTER = FilenameFilter { file, s -> file.isDirectory && s.startsWith(RES_TYPE_NAME) }

    override val resTypeName: String = RES_TYPE_NAME
    override val javaRegex: String = """(R(\s*?)\.(\s*?)layout(\s*?)\.(\s*?))(\w+)"""
    override val xmlRegex: String = """(@layout/)(\w+)"""

    override fun getResNameSet(): Set<String> {
        return resDir.listFiles(DIR_FILTER)?.map { it.name.substringBeforeLast(".") }?.toSet()
                ?: Collections.emptySet()
    }

    override fun replaceSrc(resNameSet: Set<String>, regex: String) {
        debug("---------- $RES_TYPE_NAME ----- replace source folder start...")
        replaceSrcDir(srcDir, resNameSet, javaRegex)

        // 替换kt源代码文件 layout 文件
        replaceKtSrcFile(srcDir, resNameSet)
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

    /**
     * kt 文件特殊处理，layout 导入包时，需要重新替换，如：
     *     import  kotlinx.android.synthetic.main.activity_main.*
     * 替换成：
     *     import  kotlinx.android.synthetic.main.new_prefix_activity_main.*
     * @param file
     * @param set
     */
    private fun replaceKtSrcFile(file: File, set: Set<String>) {
        if (file.exists()) {
            if (file.isDirectory) {
                file.listFiles().forEach {
                    replaceKtSrcFile(it, set)
                }
            } else {
                if (file.name.endsWith(".kt")) {  // kt
                    handleKtSrcFileLayout(file, set)
                }
            }
        }
    }

    private fun handleKtSrcFileLayout(file: File, set: Set<String>) {
        val regex = "(synthetic.main\\.)(\\w+)(\\.)"
        val fileContent = file.readText()
        val sb = StringBuffer()
        val matcher = Regex(regex).toPattern().matcher(fileContent)
        while (matcher.find()) {
            val oldName = matcher.group(2)
            if (set.contains(oldName)) {
                val newName = config.newPrefix + if (oldName.startsWith(config.oldPrefix)) {
                    oldName.substring(config.oldPrefix.length)
                } else {
                    oldName
                }
                matcher.appendReplacement(sb, "\$1$newName\$3") // 拼接 保留$1$3分组,替换$2分组
            }
        }

        // 修改了文件时，才写入文件
        if (sb.isNotEmpty()) {
            matcher.appendTail(sb)              // 添加结尾
            file.writeText(sb.toString())       // 写回文件
        }

    }

}