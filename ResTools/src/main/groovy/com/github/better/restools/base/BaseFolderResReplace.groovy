package com.github.better.restools.base

import com.github.better.restools.ResToolsConfiguration

/**
 * 资源Base
 * @auther better
 */
abstract class BaseFolderResReplace extends BaseReplace {

    BaseFolderResReplace(ResToolsConfiguration config) {
        super(config)
    }

    /**
     * 获取资源类型名称
     * @return
     */
    abstract String getResTypeName()

    /**
     * 匹配源代码的正则表达式
     * @return
     */
    abstract String getJavaRegex()

    /**
     * 匹配xml中的正则表达式
     * @return
     */
    abstract String getXmlRegex()

    /**
     * 获取特定类型的资源名集合,比如：
     * drawable,需要注意，必须包含 drawable-xx的
     * @return
     */
    abstract Set<String> getResNameSet()

    /**
     * 模板方法
     * @throws IOException
     */
    final void replaceThis() throws IOException {
        println("***** $resTypeName ***** do $resTypeName start...")

        def java_regx = ~getJavaRegex()
        def xml_regx = ~getXmlRegex()
        Set<String> resNameSet = getResNameSet()

        // 1.源代码目录部分
        replaceSrc(resNameSet, java_regx)
        // 2.资源目录部分
        replaceRes(resNameSet, xml_regx)

        println("***** $resTypeName ***** do $resTypeName finish")
    }

    abstract void replaceSrc(Set<String> resNameSet, java_regx) throws IOException

    abstract void replaceRes(Set<String> resNameSet, xml_regx) throws IOException


    /**
     * 文件重命名，处理各种文件的重命名
     * @param file
     * @param resNameSet 资源名
     * @param dir_filter
     * @param resTypeName 资源类型名
     */
    protected void renameFile(File file, Set<String> resNameSet, dir_filter, resTypeName) {
        File[] dirs = file.listFiles(dir_filter)
        dirs?.each { dir ->
            dir?.eachFile { it ->
                String fileName = it.name
                if (it.name.lastIndexOf('.' ) > -1) {
                    // 可能是 png .9 图片
                    fileName = it.name.substring(0, it.name.lastIndexOf("."))
                    if (fileName.endsWith(".9")) {   // 可能有.9
                        fileName = fileName.substring(0, fileName.lastIndexOf("."))
                    }
                }

                // 只替换指定的资源
                if (resNameSet.contains(fileName)) {
                    String oldName = it.name
                    String newName = config.new_prefix + oldName
                    if (oldName.startsWith(config.old_prefix)) {
                        newName = config.new_prefix + oldName.substring(config.old_prefix.length())
                    }

                    File newFile = new File(it.getParent(), newName)
                    if (newFile.exists()) {
                        newFile.delete()
                    }

                    if (!it.renameTo(newFile)) {
                        println("--------------- $resTypeName ${it.name} 重命名失败！，请手动修改成：${newFile.name}")
                    }
                }
            }
        }
    }

    /*
    void export(String moduleFolder, String appResFolder) {
        Tools.checkDir(moduleFolder, appResFolder)

        File moduleDirFile = new File(moduleFolder)
        // === 1.layout name map from module.  (XXX to R.xxx.XXX)
        Map<String, String> moduleMap = new HashMap<>()
        moduleMap(moduleDirFile, moduleDirFile, moduleMap)
        // === 2. layout name map from 'MainGo App Res'
        File appResDirFile = new File(appResFolder)
        // folderName to layout filename list
        Map<String, List<String>> dirFileMap = getAppLayoutMap(appResDirFile, moduleLayoutMap)
        // === 3.according to 'moduleLayoutMap' key, find all layout from 'main app res' and copy layout.xml file from dirs
        export(dirFileMap, appResDir)
    }*/
}
