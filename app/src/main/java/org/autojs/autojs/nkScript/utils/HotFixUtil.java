package org.autojs.autojs.nkScript.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

public class HotFixUtil {

    private final String TAG = "HotFixUtil";
    private final String FIELD_DEX_ELEMENTS = "dexElements";
    private final String FIELD_PATH_LIST = "pathList";
    private final String CLASS_NAME = "dalvik.system.BaseDexClassLoader";
    Context myApplication;

    private final String DEX_SUFFIX = ".dex";
    private final String JAR_SUFFIX = ".jar";
    private final String APK_SUFFIX = ".apk";
    private final String SOURCE_DIR = "patch";
    private final String OPTIMIZE_DIR = "odex";

/*    ————————————————
    版权声明：本文为CSDN博主「zhuliyuan丶」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
    原文链接：https://blog.csdn.net/zly921112/article/details/83547750
    */
    public void startFix() throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        // 默认补丁目录  /storage/emulated/0/Android/data/rocketly.hotfixdemo/files/patch
        // SDCard/Android/data/
        Log.i(TAG, "startFix: 检查更新.");

        
        File sourceFile = myApplication.getExternalFilesDir(SOURCE_DIR);
       // File sourceFile =new File(Environment.getExternalStorageDirectory()+"/"+SOURCE_DIR);
        if (!sourceFile.exists()) {
            Log.i(TAG, sourceFile+"-补丁目录不存在");
            return;
        }
        // 默认 dex优化存放目录  /data/data/rocketly.hotfixdemo/app_odex
        File optFile = myApplication.getDir(OPTIMIZE_DIR, Context.MODE_PRIVATE);
        if (!optFile.exists()) {
            optFile.mkdir();
        }
        StringBuilder sb = new StringBuilder();
        File[] listFiles = sourceFile.listFiles();
        for (int i = 0; i < listFiles.length; i++) {//遍历查找文件中patch开头, .dex .jar .apk结尾的文件
            Log.i(TAG, "startFix: listFiles"+listFiles[i] );
            File file = listFiles[i];
            if (file.getName().startsWith("patch") && file.getName().endsWith(DEX_SUFFIX)//这里我默认的补丁文件名是patch
                    || file.getName().endsWith(JAR_SUFFIX)
                    || file.getName().endsWith(APK_SUFFIX)) {
                if (i != 0) {
                    sb.append(File.pathSeparator);//多个dex路径 添加默认分隔符 :
                }
                sb.append(file.getAbsolutePath());
            }
        }
        String dexPath = sb.toString();
        String optPath = optFile.getAbsolutePath();

        ClassLoader pathClassLoader = myApplication.getClassLoader();//拿到系统默认的PathClassLoader加载器
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, optPath, null, myApplication.getClassLoader());//加载我们自己的补丁dex
        Object pathElements = getElements(pathClassLoader);//获取PathClassLoader Element[]
        Object dexElements = getElements(dexClassLoader);//获取DexClassLoader Element[]
        Object combineArray = combineArray(pathElements, dexElements);//合并数组
        setDexElements(pathClassLoader, combineArray);//将合并后Element[]数组设置回PathClassLoader pathList变量

    }

    public HotFixUtil( Context myApplication ) {
        this.myApplication = myApplication;
    }

    /**
     * 获取Element[]数组
     */
    private Object getElements(ClassLoader classLoader) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> BaseDexClassLoaderClazz = Class.forName(CLASS_NAME);//拿到BaseDexClassLoader Class
        Field pathListField = BaseDexClassLoaderClazz.getDeclaredField(FIELD_PATH_LIST);//拿到pathList字段
        pathListField.setAccessible(true);
        Object DexPathList = pathListField.get(classLoader);//拿到DexPathList对象
        Field dexElementsField = DexPathList.getClass().getDeclaredField(FIELD_DEX_ELEMENTS);//拿到dexElements字段
        dexElementsField.setAccessible(true);
        return dexElementsField.get(DexPathList);//拿到Element[]数组
    }

    /**
     * 合并Element[]数组 将补丁的放在前面
     */
    private Object combineArray(Object pathElements, Object dexElements) {
        Class<?> componentType = pathElements.getClass().getComponentType();
        int i = Array.getLength(pathElements);
        int j = Array.getLength(dexElements);
        int k = i + j;
        Object result = Array.newInstance(componentType, k);// 创建一个类型为componentType，长度为k的新数组
        System.arraycopy(dexElements, 0, result, 0, j);
        System.arraycopy(pathElements, 0, result, j, i);
        return result;
    }

    /**
     * 将Element[]数组 设置回PathClassLoader
     */
    private void setDexElements(ClassLoader classLoader, Object value) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> BaseDexClassLoaderClazz = Class.forName(CLASS_NAME);
        Field pathListField = BaseDexClassLoaderClazz.getDeclaredField(FIELD_PATH_LIST);
        pathListField.setAccessible(true);
        Object dexPathList = pathListField.get(classLoader);
        Field dexElementsField = dexPathList.getClass().getDeclaredField(FIELD_DEX_ELEMENTS);
        dexElementsField.setAccessible(true);
        dexElementsField.set(dexPathList, value);
        Log.i(TAG, "setDexElements: finish");
        
    }
}
