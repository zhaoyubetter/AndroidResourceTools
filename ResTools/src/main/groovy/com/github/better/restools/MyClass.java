package com.github.better.restools;

/*
import com.github.better.restools.folder.AnimReplace;
import com.github.better.restools.folder.ColorReplace;
import com.github.better.restools.folder.DrawableReplace;
import com.github.better.restools.folder.LayoutReplace;
import com.github.better.restools.folder.MenuReplace;
import com.github.better.restools.folder.MipmapReplace;
import com.github.better.restools.folder.RawReplace;
import com.github.better.restools.folder.XmlReplace;
import com.github.better.restools.values.ValuesReplace;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class MyClass {
    public static void main(String[] args) throws IOException {
        String sourcePath = "/Users/zhaoyu1/Documents/app名称/app/src/main/java";
        String resPath = "//Users/zhaoyu1/Documents/app名称/app/src/main/res";
        String manifestFilePath = "/Users/zhaoyu1/Documents/app名称/app/src/main/AndroidManifest.xml";

        final ResToolsConfiguration config = new ResToolsConfiguration("better_", "", sourcePath, resPath, manifestFilePath);

//        // 1.test layout
        LayoutReplace layoutReplace = new LayoutReplace(config);
        layoutReplace.replaceThis();
//
//        // 2.test drawable
        DrawableReplace drawableReplace = new DrawableReplace(config);
        drawableReplace.replaceThis();
//
//
//        // 3. test color
        ColorReplace colorReplace = new ColorReplace(config);
        colorReplace.replaceThis();
//
//
//        // 4. test Anim
        AnimReplace anim = new AnimReplace(config);
        anim.replaceThis();
//
//        // 5. test menu
        MenuReplace menuReplace = new MenuReplace(config);
        menuReplace.replaceThis();
//
//        // 6. test mipmap   == need test more time
        MipmapReplace mipmapReplace = new MipmapReplace(config);
        mipmapReplace.replaceThis();
//
//        // 7.test raw
        RawReplace rawReplace = new RawReplace(config);
        rawReplace.replaceThis();
//
//        // 8.test xml
        XmlReplace xmlReplace = new XmlReplace(config);
        xmlReplace.replaceThis();


        // ===9.values test not support attrs
        ValuesReplace valuesReplace = new ValuesReplace(config);
        Set<ValuesReplace.ValuesType> set = new HashSet<>();
        set.add(ValuesReplace.ValuesType.string);
        valuesReplace.replaceValues(ValuesReplace.ALL_VALUES_TYPES);


        testString();

    }


    private static void testString() {
//        String str = "<string name=\"me_didi_car_searching\" translatable=\"false\" formatted=\"true\"> 正在为您寻找车辆</string>";
////        String str = "<string name=\"me_didi_car_searching\"> 正在为您寻找车辆</string>";
//        String regex ="(<string\\s+name\\s*=\\s*[\\\"'])(\\w+)(\\s*.*[\\\"']\\s*>)";
//
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(str);
//        while(matcher.find()) {
//
//            System.out.println(matcher.group(1));
//            System.out.println(matcher.group(2));
//            System.out.println(matcher.group(3));
//        }


    }
}
*/