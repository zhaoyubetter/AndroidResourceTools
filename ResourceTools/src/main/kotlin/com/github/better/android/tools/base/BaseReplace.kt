package com.github.better.android.tools.base

import com.github.better.android.tools.ResToolsConfig
import com.github.better.android.tools.getNewName
import java.io.File
import java.io.FileFilter
import java.io.FilenameFilter
import java.lang.StringBuilder

/**
 * Base
 */
abstract class BaseReplace(config: ResToolsConfig) {
    /**
     * src Dir
     */
    protected val srcDir: File
    /**
     * resources Dir
     */
    protected val resDir: File

    /**
     * android's manifestFile
     */
    protected val manifestFile: File

    /**
     * config
     */
    protected val config: ResToolsConfig = config

    init {
        this.srcDir = File(config.srcFolderPath)
        this.resDir = File(config.resFolderPath)
        this.manifestFile = File(config.manifestFilePath)
    }

    // ====== 源代码中的部分  start =============================================
    /**
     * 替换 src 源代码目录中的资源名 （ java & kt ）
     * @param file 源代码目录
     * @param set 当前 module 下的所有资源名
     * @param regex regex
     */
    protected fun replaceSrcDir(file: File, set: Set<String>, regex: String) {
        if (!file.exists()) return

        if (file.isDirectory) {
            file.listFiles().forEach { replaceSrcDir(it, set, regex) }
        } else {
            if (file.name.endsWith(".java") || file.name.endsWith(".kt")) {
                handleSrcFile(file, set, regex)
            }
        }
    }

    private fun handleSrcFile(file: File, set: Set<String>, regex: String) {
        val fileContent = file.readText()
        val sb = StringBuffer()
        val matcher = Regex(regex).toPattern().matcher(fileContent)
        while (matcher.find()) {
            val oldName = matcher.group(6)   // oldName
            if (set.contains(oldName)) {      // 本模块包含的资源名才替换
                val newName = config.getNewName(oldName)
                matcher.appendReplacement(sb, "\$1$newName")  // 拼接： 保留$!分组，替换$6分组
            }
        }

        // sb 长度大于0时，才覆盖文件
        if (sb.isNotEmpty()) {
            matcher.appendTail(sb)          // 添加结尾
            file.writeText(sb.toString())   // 写回文件
        }
    }
    // ====== 源代码中的部分  end =============================================


    // ====== 资源文件部分公用方法  start =====================
    // regex = ~/(@XXX\/)(w+)"/      xxx 表示各种资源，如：layout、drawable 等
    /**
     * @param isValuesDir 是否是 values 文件夹类型资源（如：values-xx, values-en这种）
     *        values 类型资源时，需要保留 $3 分组
     */
    fun handleResFile(file: File, set: Set<String>, regex: String, isValuesDir: Boolean = false) {
        var hasUpdate = false
        val sb = StringBuilder()
        file.forEachLine { line ->
            var line = line
            val matcher = Regex(regex).toPattern().matcher(line)
            val tSb = StringBuffer()
            while (matcher.find()) {
                val oldName = matcher.group(2)
                if (set.contains(oldName)) {
                    val newName = config.getNewName(oldName)

                    if (isValuesDir) {
                        matcher.appendReplacement(tSb, "\$1$newName\$3") // 拼接 保留$1分组,替换组2,保留组3
                    } else {
                        matcher.appendReplacement(tSb, "\$1$newName") // 拼接 保留$1分组,替换组2
                    }
                }
            }

            if (tSb.isNotEmpty()) {
                matcher.appendTail(tSb)
                hasUpdate = true
                line = tSb.toString()
            }

            sb.append(line).append(System.lineSeparator())
        }

        // 有修改了，才重新写入文件
        if (hasUpdate) {
            file.writeText(sb.toString())
        }
    }
    // ====== 资源文件部分公用方法  end =====================

    /**
     *  res目录下的资源 - 替换名称
     * @param file
     * @param set
     * @param regex
     * @param dir_filter null no filter
     * @param isValuesDir 是否是 values 文件夹 类型(如：values，values-en)
     */
    protected fun replaceResDir(file: File, set: Set<String>, regex: String,
                                dir_filter: FilenameFilter? = null, isValuesDir: Boolean = false) {
        val dirs = file.listFiles(dir_filter)
        dirs?.forEach { it ->
            if (it.isDirectory) {
                it.listFiles(FileFilter {
                    it.name.endsWith(".xml")
                }).forEach { file ->
                    handleResFile(file, set, regex, isValuesDir)
                }
            }
        }

        // 清单文件 manifest file
        if (manifestFile != null) {
            handleResFile(manifestFile, set, regex)
        }
    }
}