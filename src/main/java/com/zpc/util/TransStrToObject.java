package com.zpc.util;

import com.zpc.dtcrawler.DtcrawlerScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by 和谐社会人人有责 on 2017/12/11.
 * 将字符串转换为java类
 */
public class TransStrToObject {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 类的全限定名
    private static final String class_name_complete = "com.zpc.dtcrawler.DtcrawlerScript_";
    private static final String class_name = "DtcrawlerScript_";
    private static final String package_str = "package zpc.script; ";
    private static final String head_str = "public class ";
    private static final String implements_str = "implements DtcrawlerScript {";
    private static final String class_end = "}";

    /**
     * 将字符串转换为java对象
     *
     * @param objectStr
     * @return
     */
    public Object trans(String objectStr) {
        // 生成类末尾需要拼上的32位随机字符串
        String randomEnd = getEndClassNameEnd();
        String importPackages = ImportPackages.packages();
        // 处理脚本为一个完整的java类
        objectStr = package_str + importPackages + head_str + class_name + randomEnd + implements_str + objectStr + class_end;

        Class<?> claszz = compile(class_name_complete + randomEnd , objectStr);
        DtcrawlerScript dtcrawlerScript = null;
        try {
            dtcrawlerScript = (DtcrawlerScript) claszz.newInstance();
        } catch (InstantiationException e) {
            logger.error(e.getMessage());
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
        }

        return dtcrawlerScript;
    }

    private final static Class<?> compile(String name, String content) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        StrSrcJavaObject srcObject = new StrSrcJavaObject(name, content);
        Iterable<? extends JavaFileObject> fileObjects = Arrays.asList(srcObject);
        String flag = "-d";
        String outDir = "";
        try {
            File classPath = new File(Thread.currentThread().getContextClassLoader().getResource("").toURI());
            outDir = classPath.getAbsolutePath() + File.separator;
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        Iterable<String> options = Arrays.asList(flag, outDir);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, fileObjects);
        boolean result = task.call();
        if (result == true) {
            System.out.println("Compile it successfully.");
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private static class StrSrcJavaObject extends SimpleJavaFileObject {
        private String content;
        public StrSrcJavaObject(String name, String content) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.content = content;
        }
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }

    /**
     * 获取32位随机字符串
     * @return
     */
    public static String getEndClassNameEnd() {
        String classNameEnd = "";
        for (int i = 0 ; i < 32 ; i ++) {
            Random random = new Random();
            char c = (char) (97 + random.nextInt(26));
            classNameEnd += c;
        }
        return classNameEnd;
    }

    public static void main(String[] args) {
        System.out.println(getEndClassNameEnd());
    }
}
