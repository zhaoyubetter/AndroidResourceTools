package com.github.better.restools.base

import com.github.better.restools.ResToolsConfiguration

import java.util.regex.Matcher

abstract class BaseReplace {
    /**
     * 源代码路径
     */
    protected File srcDir
    /**
     * 资源文件路径
     */
    protected File resDir

    /**
     * AndroidManifest
     */
    protected File manifestFile

    protected ResToolsConfiguration config

    BaseReplace(ResToolsConfiguration config) {
        this.config = config
        this.srcDir = new File(config.srcFolderPath)
        this.resDir = new File(config.resFolderPath)
        this.manifestFile = new File(config.manifestFilePath)
    }

    // ====== 源代码中的部分  start =============================================
    /**
     * 替换 src 源代码目录中的资源名
     * @param file 源代码目录
     * @param set 当前module下所有资源名 set
     * @param java_regx 匹配正则
     */
    protected void replaceSrcDir(File file, Set<String> set, java_regx) {
        if (file.exists()) {
            if (file.isDirectory()) {
                file.eachFile { it -> replaceSrcDir(it, set, java_regx) }
            } else {
                if (file.name.endsWith(".java") || file.name.endsWith(".kt")) {  // only .java or .kt files
                    handleSrcFile(file, set, java_regx)
                }
            }
        }
    }

    private void handleSrcFile(File file, Set<String> set, regex) {
        String fileContent = file.getText()              // every file is a text file
        StringBuffer sb = new StringBuffer()             // result content
        Matcher matcher = fileContent =~ regex
        while (matcher.find()) {
            String oldResName = matcher.group(6)   // the old res name
            if (set.contains(oldResName)) {               // 本模块中包含的资源名，才替换
                String newResName = config.new_prefix + oldResName
                if (oldResName.startsWith(config.old_prefix)) {     // 替换掉旧的前缀
                    newResName = config.new_prefix + oldResName.substring(config.old_prefix.length())
                }
                matcher.appendReplacement(sb, "\$1$newResName") // 拼接 保留$1分组,替换$6分组
            } else {
                matcher.find()              // 继续下一次查找，避免死循环
            }
        }
        // 修改了文件时，才写入文件
        if (sb.length() > 0) {
            matcher.appendTail(sb)              // 添加结尾
            file.write(sb.toString())           // 写回文件
        }
    }
    // ====== 源代码中的部分  end =============================================

    // ====== 资源文件部分公用方法  start =====================
    // def xml_regx = ~/(@XXX\/)(w+)"/      xxx 表示各种资源，如：layout、drawable 等
    /**
     * 操作资源
     * @param file
     * @param set
     * @param regex
     * @param valuesType 是否是 values 类型资源（values-xx, values-en这种）
     *        values 类型资源时，需要保留 $3 分组
     * @return
     */
    def handleResFile(File file, Set<String> set, regex, valuesType = false) {
        boolean hasUpdate = false                 // 是否有修改
        StringBuilder sb = new StringBuilder()    // 文件内容
        file.each { line ->
            Matcher matcher = line =~ regex
            StringBuffer tSb = new StringBuffer()
            while (matcher.find()) {
                String oldResName = matcher.group(2)
                if (set.contains(oldResName)) {
                    String newResName = config.new_prefix + oldResName
                    if (oldResName.startsWith(config.old_prefix)) {
                        newResName = config.new_prefix + oldResName.substring(config.old_prefix.length())
                    }

                    if (valuesType) {
                        matcher.appendReplacement(tSb, "\$1$newResName\$3") // 拼接 保留$1分组,替换组2,保留组3
                    } else {
                        matcher.appendReplacement(tSb, "\$1$newResName") // 拼接 保留$1分组,替换组2
                    }
                } else {
                    matcher.find()                          // 继续下一次查找，避免死循环
                }
            }
            if (tSb.length() > 0) {                         // 如果包含了，则重新赋值line，并拼接余下部分
                matcher.appendTail(tSb)
                hasUpdate = true
                line = tSb.toString()
            }

            sb.append(line).append("\r\n")
        }

        // 有修改了，才重新写入文件
        if (hasUpdate) {
            file.write(sb.toString())
        }
    }
    // ====== 资源文件部分公用方法  end =====================

    /**
     *  res目录下的资源 - 替换名称
     * @param file
     * @param set
     * @param regx
     * @param dir_filter null no filter
     * @param valuesType 是否是 values 文件夹 类型(如：values，values-en)
     */
    protected def replaceResDir(File file, Set<String> set, regx, dir_filter, valuesType = false) {
        File[] dirs = file.listFiles(dir_filter as FilenameFilter)
        dirs?.each { dir ->
            if (dir != null && dir.isDirectory()) {
                dir.eachFile { it ->
                    if (it.name.endsWith(".xml")) {     // 只在xml有引用
                        handleResFile(it, set, regx, valuesType)
                    }
                }
            }
        }

        // 清单文件 manifest file
        if (manifestFile != null) {
            handleResFile(manifestFile, set, regx)
        }
    }
}
