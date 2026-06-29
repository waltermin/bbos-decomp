package net.rim.ecmascript.util;

import java.util.TimeZone;
import net.rim.device.api.system.DeviceInfo;
import net.rim.ecmascript.runtime.GlobalObject;
import net.rim.vm.Array;
import net.rim.vm.Memory;

public class Misc {
   public static final int PLATFORM_DESKTOPVM;
   public static final int PLATFORM_SIMULATOR;
   public static final int PLATFORM_J2SE;
   public static final int PLATFORM_DEVICE;

   public static int separatedArraySize(long len, int lenSep) {
      long estimate = len * lenSep + 1;
      if (estimate > Integer.MAX_VALUE) {
         estimate = Integer.MAX_VALUE;
      } else if (estimate < 1) {
         estimate = 1;
      }

      return (int)estimate;
   }

   public static String replaceString(String str, Object oldObj, Object newObj) {
      if (str == null) {
         return str;
      }

      if (oldObj == null) {
         return str;
      }

      if (newObj == null) {
         return str;
      }

      String oldStr = oldObj.toString();
      String newStr = newObj == null ? "" : newObj.toString();
      int oldIndex = 0;
      int newLength = newStr.length();

      while (true) {
         oldIndex = str.indexOf(oldStr, oldIndex);
         if (oldIndex == -1) {
            return str;
         }

         str = ((StringBuffer)(new Object())).append(str.substring(0, oldIndex)).append(newStr).append(str.substring(oldIndex + oldStr.length())).toString();
         oldIndex += newLength;
      }
   }

   public static String replace(String str, Object o1) {
      return replaceString(str, "%1", o1);
   }

   public static String replace(String str, Object o1, Object o2) {
      return replaceString(replace(str, o1), "%2", o2);
   }

   public static String replace(String str, Object o1, Object o2, Object o3) {
      return replaceString(replace(str, o1, o2), "%3", o3);
   }

   public static String tzName(TimeZone tz, boolean inDaylightTime) {
      return tz == null ? "EST" : tz.getID();
   }

   public static long getLocalTZA(TimeZone tz) {
      return tz == null ? -18000000 : tz.getRawOffset();
   }

   public static native int toInt(float var0);

   public static native int toInt(Object var0);

   public static native float toFloat(int var0);

   public static native long toLong(double var0);

   public static native double toDouble(long var0);

   public static native Object toObject(int var0);

   public static int compareDouble(double d1, double d2) {
      if (d1 < d2) {
         return -1;
      } else if (d1 > d2) {
         return 1;
      } else {
         long thisBits = toLong(d1);
         long anotherBits = toLong(d2);
         if (thisBits == anotherBits) {
            return 0;
         } else {
            return thisBits < anotherBits ? -1 : 1;
         }
      }
   }

   public static void freeMixedArray(long[] a) {
   }

   public static long[] newMixedArray(int length) {
      long[] la = new long[length];
      Array.markAsMixed(la);
      return la;
   }

   public static long[] longArrayResize(long[] a, int newLength) {
      Array.resize(a, newLength);
      return a;
   }

   public static byte[] byteArrayResize(byte[] a, int newLength) {
      Array.resize(a, newLength);
      return a;
   }

   public static char[] charArrayResize(char[] a, int newLength) {
      Array.resize(a, newLength);
      return a;
   }

   public static int[] intArrayResize(int[] a, int newLength) {
      Array.resize(a, newLength);
      return a;
   }

   public static String[] stringArrayResize(String[] a, int newLength) {
      Array.resize(a, newLength);
      return a;
   }

   public static GlobalObject[] globalObjectArrayResize(GlobalObject[] a, int newLength) {
      Array.resize(a, newLength);
      return a;
   }

   public static String stringIntern(String str) {
      return Memory.stringIntern(str);
   }

   public static void GC() {
   }

   public static void cleanup() {
   }

   public static void forceGC() {
   }

   public static void objTableGC() {
   }

   public static int getPlatform() {
      try {
         return DeviceInfo.isSimulator() ? 1 : 3;
      } finally {
         ;
      }
   }
}
