package net.rim.device.internal.system;

import net.rim.device.api.util.NumberUtilities;

public class DebugUtilities {
   public static void printArrayContents(byte[] array) {
      printArrayContents(array, 0, array == null ? 0 : array.length);
   }

   public static void printArrayContents(byte[] array, int offset, int length) {
      if (array != null && array.length >= offset + length) {
         int k = 0;

         for (int i = offset; i < offset + length; i++) {
            if (k != 0 && k % 6 == 0) {
               System.out.println();
            }

            System.out.print("0x" + NumberUtilities.intToHexDigit(array[i] >> 4) + NumberUtilities.intToHexDigit(array[i]) + ", ");
            k++;
         }

         System.out.println();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static long logStart(String className, String m) {
      long start = System.currentTimeMillis();
      StringBuffer buffer = new StringBuffer(className);
      buffer.append(' ');
      buffer.append(m);
      buffer.append(' ');
      buffer.append('S');
      buffer.append(' ');
      buffer.append(start);
      buffer.append(' ');
      System.out.println(buffer.toString());
      return start;
   }

   public static void logFinish(String className, String m, long start) {
      long end = System.currentTimeMillis();
      StringBuffer buffer = new StringBuffer(className);
      buffer.append(' ');
      buffer.append(m);
      buffer.append(' ');
      buffer.append('F');
      buffer.append(' ');
      buffer.append(end);
      buffer.append(' ');
      System.out.println(buffer.toString());
      buffer.setLength(0);
      buffer.append(' ');
      buffer.append(m);
      buffer.append(' ');
      buffer.append('D');
      buffer.append(' ');
      buffer.append(end - start);
      buffer.append(' ');
      System.out.println(buffer.toString());
   }
}
