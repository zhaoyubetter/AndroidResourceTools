package com.github.better.restools;

public class ResToolsConfiguration {
    /**
     * 新的资源前缀
     */
    public String new_prefix = "";
    /**
     * 老的资源前缀，取值说明如下：
     * <p>
     * 1. 使用空字符串：
     * 比如：new_prefix 为 'mae_',  module_main_activity.xml 会被替换成 mae_module_main_activity.xml
     * 2. 取值字符串时：
     * 比如：old_prefix='module_', new_prefix='mae_', module_main_activity.xml 会被替换成 mae_main_activity.xml
     */
    public String old_prefix = "";

    /**
     * 源代码目录
     */
    public String srcFolderPath = "";

    /**
     * 资源文件目录
     */
    public String resFolderPath = "";

    /**
     * 清单文件路径
     */
    public String manifestFilePath = "";


    public ResToolsConfiguration(String new_prefix, String old_prefix, String srcFolderPath, String resFolderPath, String manifestFilePath) {
        this.new_prefix = new_prefix;
        this.old_prefix = old_prefix;
        this.srcFolderPath = srcFolderPath;
        this.resFolderPath = resFolderPath;
        this.manifestFilePath = manifestFilePath;
    }

    public ResToolsConfiguration() {

    }
}
