package com.github.better.android.tools.base

import com.github.better.android.tools.ResToolsConfig
import com.github.better.android.tools.debug
import com.github.better.android.tools.warn
import java.io.File
import java.io.FileFilter
import java.io.FilenameFilter
import java.io.IOException

/**
 * Created by better On 2019-07-07.
 * 资源 Base
 */
abstract class BaseFolderResReplace(config: ResToolsConfig) : BaseReplace(config) {

    /**
     * 资源类型名称
     */
    abstract val resTypeName: String

    /**
     * 匹配源代码的正则表达式
     */
    abstract val javaRegex: String

    /**
     * 匹配xml中的正则表达式
     */
    abstract val xmlRegex: String

    /**
     * 获取特定类型的资源名集合,比如：
     * drawable，需要注意，必须包含 drawable-xx
     * @return
     */
    abstract fun getResNameSet(): Set<String>

    /**
     * 替换源代码部分
     */
    abstract fun replaceSrc(resNameSet: Set<String>, regex: String)

    /**
     * 替换资源部分
     */
    abstract fun replaceRes(resNameSet: Set<String>, regex: String)

    /**
     * 模板方法
     */
    @Throws(IOException::class)
    fun replaceThis() {
        debug("***** $resTypeName ***** do $resTypeName start...")
        getResNameSet().apply {
            if (this.isNotEmpty()) {
                // 1. 源代码部分
                replaceSrc(this, javaRegex)
                // 2. 资源目录部分
                replaceRes(this, xmlRegex)
            }
        }
        debug("***** $resTypeName ***** do $resTypeName finish...")
    }

    /**
     * 文件重命名，处理各种文件的重命名
     * @param file
     * @param resNameSet 资源名
     * @param dirFilter
     * @param resTypeName 资源类型名
     */
    protected fun renameFile(file: File,
                             resNameSet: Set<String>,
                             dirFilter: FilenameFilter,
                             resTypeName: String) {
        val dirs = file.listFiles(dirFilter)
        dirs?.forEach { dir ->
            dir.listFiles().forEach { file ->
                var fileName = file.name.let {
                    var fileName = it
                    // 含有后缀名的文件
                    if (it.lastIndexOf('.') > -1) {
                        fileName = fileName.substringBeforeLast('.')
                        // 是否 .9 文件
                        if (fileName.endsWith(".9")) {
                            fileName = fileName.substringBeforeLast('.')
                        }
                    }
                    fileName
                }

                // 只替换指定的资源
                if (resNameSet.contains(fileName)) {
                    val oldName = file.name
                    val newName = config.newPrefix + if (oldName.startsWith(config.oldPrefix)) {
                        oldName.substring(config.oldPrefix.length)
                    } else {
                        oldName
                    }

                    // 重命名文件
                    File(file.parent, newName).apply {
                        if (!this.exists()) {
                            this.delete()
                        }

                        if (!file.renameTo(this)) {
                            warn("--------------- $resTypeName ${file.name} 重命名失败！，请手动修改成：${this.name}")
                        }
                    }
                }
            }
        }
    }
}