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
