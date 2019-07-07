package better

import org.junit.Test

/**
 * Created by better On 2019-07-07.
 */
class Test1 {
    @Test
    fun test1() {
        val fileName = "better.9.png"
        println(fileName.substring(0, fileName.lastIndexOf(".")))
        println(fileName.substringBeforeLast("."))
    }
}