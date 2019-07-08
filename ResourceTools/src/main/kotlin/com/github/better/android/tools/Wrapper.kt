package com.github.better.android.tools

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Created by better On 2019-07-07.
 */
inline fun <reified T> T.debug(content: String) {
    val log = Logger.getLogger(T::class.simpleName)
    log.log(Level.INFO, content)
}

inline fun <reified T> T.warn(content: String) {
    val log = Logger.getLogger(T::class.simpleName)
    log.log(Level.WARNING, content)
}

/**
 * 获取新名称
 * @param oldName 老资源名
 * return 新的名称
 */
fun ResToolsConfig.getNewName(oldName: String): String {
    // 需求：就是如果当前资源是以最新的前序开头就不要进行重命名
    if (oldName.startsWith(this.newPrefix)) {
        return oldName
    }
    // 替换掉老的前缀
    return this.newPrefix + if (oldName.startsWith(oldPrefix)) {
        oldName.substring(oldPrefix.length)
    } else {
        oldName
    }
}