package com.github.better.android.tools

/**
 * Created by better On 2019-07-07.
 */
data class ResToolsConfig(
        /**
         * new prefix
         */
        val newPrefix: String = "",
        /**
         * old prefix
         */
        val oldPrefix: String = "",
        /**
         * src folder
         */
        val srcFolderPath: String = "",
        /**
         * resource folder
         */
        val resFolderPath: String = "",
        /**
         *
         */
        val manifestFilePath: String = ""
)