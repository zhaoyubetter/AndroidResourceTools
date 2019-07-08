package resTools

import com.github.better.android.tools.ResToolsConfig
import com.github.better.android.tools.folder.*
import com.github.better.android.tools.values.ValuesReplace
import org.junit.Test


/**
 * @author zhaoyu1  2019-07-08
 **/
class ResToolsTest {

    private fun doWork(config: ResToolsConfig) {
        // 1. layout
        val layoutReplace = LayoutReplace(config)
        layoutReplace.replaceThis()
//
//        // 2. drawable
        val drawableReplace = DrawableReplace(config)
        drawableReplace.replaceThis()
//        // 3.  color
        val colorReplace = ColorReplace(config)
        colorReplace.replaceThis()
//        // 4.  Anim
        val anim = AnimReplace(config)
        anim.replaceThis()
//        // 5.  menu
        val menuReplace = MenuReplace(config)
        menuReplace.replaceThis()
//        // 6.  mipmap
        val mipmapReplace = MipmapReplace(config)
        mipmapReplace.replaceThis()
//        // 7. raw
        val rawReplace = RawReplace(config)
        rawReplace.replaceThis()
//        // 8. xml
        val xmlReplace = XmlReplace(config)
        xmlReplace.replaceThis()

        ////////////// all values  types ////////////////////
        // === 9. values test not support attrs
        val valuesReplace = ValuesReplace(config)
        valuesReplace.replaceValues(ValuesReplace.ALL_VALUES_TYPES)
    }

    /**
     * 这里是测试类
     */
    @Test
    fun test() {
        val config = ResToolsConfig(
                srcFolderPath = "/Users/zhaoyu1/Documents/github/AndroidResourceTools/app/src/main/java",
                resFolderPath = "/Users/zhaoyu1/Documents/github/AndroidResourceTools/app/src/main/res",
                manifestFilePath = "/Users/zhaoyu1/Documents/github/AndroidResourceTools/app/src/main/AndroidManifest.xml",
                oldPrefix = "better",
                newPrefix = "bb_")

        doWork(config)
    }

}