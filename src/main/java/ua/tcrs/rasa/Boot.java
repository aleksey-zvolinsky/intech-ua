package ua.tcrs.rasa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Boot
{
  private static PrintStream debug = null;
  
  public static void debug(String message)
  {
    if (debug != null) {
      debug.println(message);
    }
  }
  
  public static void error(String message)
  {
    System.err.println(message);
  }
  
  public static void error(String message, Throwable t)
  {
    System.err.println(message);
    t.printStackTrace(System.err);
  }
  
  public static void main(String[] args)
  {
    if (System.getProperty("rasa.debug") != null) {
      if (System.getProperty("rasa.debug").equals("out")) {
        debug = System.out;
      } else {
        debug = System.err;
      }
    }
    CodeSource codeSource = Boot.class.getProtectionDomain().getCodeSource();
    
    String startJarName = codeSource.getLocation().getPath();
    System.setProperty("rasa.start.jar", startJarName);
    debug("Start jar: " + startJarName);
    try
    {
      JarFile startJarFile = new JarFile(startJarName);
      Manifest manifest = startJarFile.getManifest();
      Attributes attr = manifest.getMainAttributes();
      if (attr == null)
      {
        error("Failed to get main attributes for " + startJarName);
      }
      else
      {
        String rasaMainClass = attr.getValue("Rasa-Main-Class");
        if (rasaMainClass == null) {
          error("Failed to load Main-Class manifest attribute from " + startJarName);
        } else {
          try
          {
            List<URL> urls = new ArrayList();
            
            String rasaClassPath = attr.getValue("Rasa-Class-Path");
            if (rasaClassPath != null)
            {
              String[] paths = rasaClassPath.split(" ");
              for (String path : paths) {
                if (path.endsWith("/*"))
                {
                  File dir = new File(path.substring(0, path.length() - 2));
                  if (dir.exists())
                  {
                    File[] files = dir.listFiles(new FilenameFilter()
                    {
                      public boolean accept(File dir, String name)
                      {
                        return name.endsWith(".jar");
                      }
                    });
                    if (files != null) {
                      for (File file : files)
                      {
                        urls.add(file.toURI().toURL());
                        debug("Added to classpath: " + file.getPath());
                      }
                    }
                  }
                }
                else
                {
                  File file = new File(path);
                  urls.add(file.toURI().toURL());
                  debug("Added to classpath: " + file.getPath());
                }
              }
            }
            Enumeration<JarEntry> jee = startJarFile.entries();
            while (jee.hasMoreElements())
            {
              JarEntry je = jee.nextElement();
              String jeName = je.getName();
              if ((jeName.startsWith("lib/")) && (jeName.endsWith(".jar")))
              {
                InputStream is = startJarFile.getInputStream(je);
                if (is != null)
                {
                  File tempJar = File.createTempFile(jeName.replace('/', '-').substring(0, jeName.length() - 4) + '-', ".jar");
                  tempJar.deleteOnExit();
                  OutputStream os = new FileOutputStream(tempJar);
                  copy(is, os);
                  os.close();
                  is.close();
                  urls.add(tempJar.toURI().toURL());
                  debug("Added to classpath: " + je.getName());
                }
              }
            }
            URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
            if (urls.size() > 0)
            {
              classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), classLoader);
              Thread.currentThread().setContextClassLoader(classLoader);
            }
            Class<?> cls = classLoader.loadClass(rasaMainClass);
            Method clsMainMethod = cls.getMethod("main", new Class[] { java.lang.String.class });
            clsMainMethod.invoke(null, new Object[] { args });
          }
          catch (ClassNotFoundException e)
          {
            error("", e);
          }
          catch (NoSuchMethodException e)
          {
            error("", e);
          }
          catch (IllegalAccessException e)
          {
            error("", e);
          }
          catch (InvocationTargetException e)
          {
            error("", e);
          }
        }
      }
    }
    catch (IOException e)
    {
      error("", e);
    }
  }
  
  protected static void copy(InputStream in, OutputStream out)
    throws IOException
  {
    byte[] buf = new byte[1024];
    for (;;)
    {
      int len = in.read(buf);
      if (len < 0) {
        break;
      }
      out.write(buf, 0, len);
    }
  }
}
