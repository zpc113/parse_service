package junit.test;

import javax.tools.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;



public class JavaToolsTest {
    public static void executeMain(String name, String content) throws IllegalAccessException, InstantiationException {
        content = class_begin + content + class_end;
        Class<?> claszz = compile(name,content);
        ZPC zpc = (ZPC) claszz.newInstance();
        zpc.main(new String[]{});
        try {
            Method method = claszz.getMethod("main", String[].class);
            System.out.println(method.getName());
            method.invoke(null, new Object[] { new String[] {} });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public static void main(String[] args) throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append("  public static void main(String[] args){");
        sb.append("JSONObject jsonObject = (JSONObject) JSONObject.parse(\"{\\\"aaa\\\":\\\"bbb\\\"}\");\n" +
                "        System.out.println(jsonObject.toString());    System.out.println(\"周鹏程.\");");
        sb.append("  }");

        executeMain(class_name_complete, sb.toString());
    }
    private static final String class_name_complete = "junit.test.ZPC1";
    private static final String class_name = "ZPC1";
    private static final String class_begin = "package junit.test; " +
            "import com.alibaba.fastjson.JSONObject;" +
            "public class " + class_name + " extends ZPC {";
    private static final String class_end = "}";
}

