package me.d4rk.fracassadobot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;

public class JavaEval {

    private static String imports = "package dontuse;\n";

    static {
        addImport("sun.misc.*");

        addImport("java.lang.invoke.*");
        addImport("java.lang.management.*");
        addImport("java.util.*");
        addImport("java.net.*");
        addImport("java.math.*");
        addImport("java.util.function.*");
        addImport("java.util.stream.*");
        addImport("java.lang.reflect.*");

        addImport("net.dv8tion.jda.api.*");
        addImport("net.dv8tion.jda.api.entities.*");
        addImport("net.dv8tion.jda.api.requests.*");
        addImport("net.dv8tion.jda.api.managers.*");
        addImport("net.dv8tion.jda.api.utils.*");
        addImport("net.dv8tion.jda.api.events.*");
        addImport("net.dv8tion.jda.api.events.message.*");
        addImport("net.dv8tion.jda.api.events.message.guild.*");

        addImport("me.d4rk.fracassadobot.commands.*");
        addImport("me.d4rk.fracassadobot.handlers.*");
        addImport("me.d4rk.fracassadobot.listeners.*");
        addImport("me.d4rk.fracassadobot.utils.*");
        addImport("me.d4rk.fracassadobot.utils.command.*");
        addImport("me.d4rk.fracassadobot.*");
    }

    public static void addImport(String name) {
        imports += "import " + name + ";\n";
    }

    public static void eval(GuildMessageReceivedEvent event, String input) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        String code = imports +
                "public class Source {\n" +
                "public static Object run(GuildMessageReceivedEvent event) throws Throwable {\n" +
                "try {\n" +
                "return null;\n" +
                "} finally {\n" +
                input.replaceAll(";{2,}", ";") + "//*/\n" +
                "}\n" +
                "}\n" +
                "}";
        File root = new File(".dynamic");
        File source = new File(root, "dontuse/Source.java");
        source.getParentFile().mkdirs();
        try {
            try(FileOutputStream fos = new FileOutputStream(source)) {
                copyData(new ByteArrayInputStream(code.getBytes(Charset.defaultCharset())), fos);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int errcode = compiler.run(null, out, out, source.getPath());
            if(errcode != 0) {
                String err = new String(out.toByteArray(), Charset.defaultCharset());
                if(err.length() > 1000) {
                    err = Paste.toHastebin(err);
                }
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error: ", null)
                        .setColor(new Color(16711680))
                        .setDescription("Failed to compile with the following error:"+"```java\n"+err+"```")
                        .build()
                ).queue();
                return;
            }
            DummyClassLoader loader = new DummyClassLoader(JavaEval.class.getClassLoader());
            File[] files = new File(root, "dontuse").listFiles();
            if(files == null) files = new File[0];
            for(File f : files) {
                if(!f.getName().endsWith(".class")) continue;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try(FileInputStream fis = new FileInputStream(f)) {
                    copyData(fis, baos);
                }
                f.delete();
                String clname = f.getAbsolutePath().substring(root.getAbsolutePath().length()+1).replace('/', '.').replace('\\', '.');

                loader.define(
                        clname.substring(0, clname.length()-6),
                        baos.toByteArray()
                );
            }
            Object ret = loader.loadClass("dontuse.Source").getMethod("run", GuildMessageReceivedEvent.class).invoke(null, event);
            if(ret == null) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Success: ", null)
                        .setColor(new Color(65280))
                        .setDescription("Executed without error and without return.")
                        .build()
                ).queue();
                return;
            }
            String v;
            if(ret instanceof Object[]) v = Arrays.toString((Object[])ret);
            else if(ret instanceof boolean[]) v = Arrays.toString((boolean[])ret);
            else if(ret instanceof byte[]) v = Arrays.toString((byte[])ret);
            else if(ret instanceof short[]) v = Arrays.toString((short[])ret);
            else if(ret instanceof char[]) v = new String((char[])ret);
            else if(ret instanceof int[]) v = Arrays.toString((int[])ret);
            else if(ret instanceof float[]) v = Arrays.toString((float[])ret);
            else if(ret instanceof long[]) v = Arrays.toString((long[])ret);
            else if(ret instanceof double[]) v = Arrays.toString((double[])ret);
            else v = String.valueOf(ret);

            if(v.length() > 1000) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Success: ", null)
                        .setColor(new Color(65280))
                        .setDescription("Executed without error and with the following returns:"+"```\n"+Paste.toHastebinAsync(v)+"```")
                        .build()
                ).queue();
                return;
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success: ", null)
                    .setColor(new Color(65280))
                    .setDescription("Executed without error and with the following returns:"+"```java\n"+v+"```")
                    .build()
            ).queue();
        } catch(Exception e) {
            String stack = "";
            stack = stack + e.getClass().getName();
            for(StackTraceElement st : e.getStackTrace()) {
                stack = stack + "\n    at " + st.toString();
            }
            stack = stack + "\nCaused by: " + e;
            for(StackTraceElement st : e.getCause().getStackTrace()) {
                stack = stack + "\n    at " + st.toString();
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error: ", null)
                    .setColor(new Color(16711680))
                    .setDescription("Executed with the following error:"+
                            "```java\nMessage: "+e.getMessage()+
                            "\nClass: "+e.getClass().getName()+
                            "\nCaused by: "+e.getCause().getClass().getName()+
                            "\nFull StackTrace: "+Paste.toHastebin(stack)+
                            "```")
                    .build()
            ).queue();

        }
    }

    public static void copyData(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = is.read(buffer)) != -1)
            os.write(buffer, 0, read);
    }

    public static class DummyClassLoader extends ClassLoader {
        public DummyClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> define(String name, byte[] bytes) {
            return super.defineClass(name, bytes, 0, bytes.length);
        }
    }
}