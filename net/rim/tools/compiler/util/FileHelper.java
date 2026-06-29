package net.rim.tools.compiler.util;

import net.rim.tools.compiler.vm.Constants;

public final class FileHelper implements Constants {
   public static final char pathSeparatorChar = ';';
   public static final String pathSeparator = ";";
   public static final char separatorChar = '\\';
   public static final String separator = "\\";
   public static final long oldEnough = 1800000L;
   public static String ext_cod = ".cod";
   public static String ext_tmp = ".tmp";
   public static String ext_jar = ".jar";
   public static String ext_jad = ".jad";
   public static String ext_rapc = ".rapc";
   public static String ext_class = ".class";
   public static String[] ext_images = new String[]{".gif", ".png", ".jpg", ".jpeg"};
   public static String pfx_rapc = "rapc_";

   public static final String fixSlashes(String name) {
      return name.replace('\\', '/');
   }

   public static final int findSeparator(String name) {
      int lastSeparator = name.lastIndexOf(92);
      int lastAlternate = name.lastIndexOf(47);
      if (lastAlternate > lastSeparator) {
         lastSeparator = lastAlternate;
      }

      return lastSeparator;
   }

   public static final String removePathPrefix(String name) {
      int lastSeparator = findSeparator(name);
      return lastSeparator != -1 ? name.substring(lastSeparator + 1) : name;
   }

   public static final int checkExtension(String name, String extension) {
      return name.endsWith(extension) ? name.length() - extension.length() : -1;
   }

   public static final int checkExtensions(String name, String[] extensions) {
      int num = extensions.length;

      for (int i = 0; i < num; i++) {
         if (checkExtension(name, extensions[i]) != -1) {
            return i;
         }
      }

      return -1;
   }

   public static final String removeExtension(String name, String extension) {
      int ext = checkExtension(name, extension);
      return ext != -1 ? name.substring(0, ext) : name;
   }

   public static final String extractExtension(String name) {
      if (name != null) {
         int ext = name.lastIndexOf(46);
         if (ext != -1) {
            return name.substring(ext);
         }
      }

      return null;
   }

   public static final String makeMultiName(String base, int ordinal, String extension) {
      StringBuffer out = (StringBuffer)(new Object(256));
      String result = null;
      if (ordinal == 0) {
         if (extension != null) {
            out.append(base);
            out.append(extension);
            result = out.toString();
         } else {
            result = base;
         }
      } else {
         out.append(base);
         out.append("-");
         out.append(ordinal);
         if (extension != null) {
            out.append(extension);
         }

         result = out.toString();
      }

      return result;
   }
}
