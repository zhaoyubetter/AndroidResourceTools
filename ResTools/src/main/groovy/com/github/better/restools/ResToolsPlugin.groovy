package com.github.better.restools

import com.github.better.restools.folder.AnimReplace
import com.github.better.restools.folder.ColorReplace
import com.github.better.restools.folder.DrawableReplace
import com.github.better.restools.folder.LayoutReplace
import com.github.better.restools.folder.MenuReplace
import com.github.better.restools.folder.MipmapReplace
import com.github.better.restools.folder.RawReplace
import com.github.better.restools.folder.XmlReplace
import com.github.better.restools.values.ValuesReplace
import com.github.better.restools.ResToolsConfiguration
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * zhaoyu1
 */
class ResToolsPlugin implements Plugin<Project> {

    static final String APP = "com.android.application"
    static final String LIBRARY = "com.android.library"

    @Override
    void apply(Project project) {
        if (!(project.plugins.hasPlugin(APP) || project.plugins.hasPlugin(LIBRARY))) {
            throw new IllegalArgumentException(
                    'ResTools gradle plugin can only be applied to android projects.')
        }

        // config
        project.extensions.create('resConfig', ResToolsConfiguration.class)

        // === Create Task
        project.tasks.create(["name": "replaceResName", "group": "resourceTools"]) << {
            if (!project.android) {
                throw new IllegalStateException('Must apply \'com.android.application\' or \'com.android.library\' first!')
            }

            if (project.resConfig == null) {       // check config
                throw new IllegalArgumentException(
                        'ResTools gradle plugin "resConfig DSL" config can not be null.')
            }

            // === System default
            String sourceFolder = project.android.sourceSets.main.java.srcDirs[0].getAbsolutePath()
            String resFolder =    project.android.sourceSets.main.res.srcDirs[0].getAbsolutePath()
            String manifestFilePath = project.android.sourceSets.main.manifest.srcFile.getAbsolutePath()

            long startTime = System.currentTimeMillis()     // startTime

            // === User settings
            def config = project.resConfig
            if (config.new_prefix == null || config.new_prefix.trim().length() == 0) {
                throw new IllegalArgumentException(
                        'the [new_prefix] can not be null (必须配置新的前缀)')
            }
            if (config.srcFolderPath != null && config.srcFolderPath.trim().length() > 0) {
                sourceFolder = config.srcFolderPath
            }
            if (config.resFolderPath != null && config.resFolderPath.trim().length() > 0) {
                resFolder = config.resFolderPath
            }
            if (config.manifestFilePath != null && config.manifestFilePath.trim().length() > 0) {
                manifestFilePath = config.manifestFilePath
            }

            // === print all settings

            println(">>>>>> old_prefix: ${config.old_prefix}")
            println(">>>>>> new_prefix: ${config.new_prefix}")
            println(">>>>>> srcFolder : ${sourceFolder}")
            println(">>>>>> resFolder : ${resFolder}")
            println(">>>>>> AndroidManifest.xml file path : ${manifestFilePath}")

            // === do work
            println "++++++++++++++++++++++ Start replace Android resources..."

            ResToolsConfiguration workConfig = new ResToolsConfiguration(
                    config.new_prefix,
                    config.old_prefix,
                    sourceFolder,
                    resFolder,
                    manifestFilePath
            )
            doWork(workConfig)

            println("++++++++++++++++++++++ Finish replace resouces name, Total time: ${(System.currentTimeMillis() - startTime) / 1000} ")
        }
    }

    private def doWork(ResToolsConfiguration config) {
        // 1. layout
        LayoutReplace layoutReplace = new LayoutReplace(config);
        layoutReplace.replaceThis();

//        // 2. drawable
        DrawableReplace drawableReplace = new DrawableReplace(config);
        drawableReplace.replaceThis();
//
//
//        // 3.  color
        ColorReplace colorReplace = new ColorReplace(config);
        colorReplace.replaceThis();
//
//
//        // 4.  Anim
        AnimReplace anim = new AnimReplace(config);
        anim.replaceThis();
//
//        // 5.  menu
        MenuReplace menuReplace = new MenuReplace(config);
        menuReplace.replaceThis();
//
//        // 6.  mipmap
        MipmapReplace mipmapReplace = new MipmapReplace(config);
        mipmapReplace.replaceThis();
//
//        // 7. raw
        RawReplace rawReplace = new RawReplace(config);
        rawReplace.replaceThis();
//
//        // 8. xml
        XmlReplace xmlReplace = new XmlReplace(config);
        xmlReplace.replaceThis();

        ////////////// all values  types ////////////////////
        // === 9. values test not support attrs
        ValuesReplace valuesReplace = new ValuesReplace(config);
        valuesReplace.replaceValues(ValuesReplace.ALL_VALUES_TYPES);
    }
}