package net.rim.device.api.xml.jaxp;

import net.rim.vm.Array;

class ArrayResize {
   static int roundToSectionSize(int length, int sectionSize) {
      return length + (sectionSize - 1) & ~(sectionSize - 1);
   }

   static String[] stringArrayResize(String[] oldArray, int newSize) {
      Array.resize(oldArray, newSize);
      return oldArray;
   }

   static int getSectionSize(Object array) {
      return Array.getSectionSize(array);
   }

   static char[][][] charArrayArrayResize(char[][][] oldArray, int newSize) {
      Array.resize(oldArray, newSize);
      return oldArray;
   }

   static char[] charArrayResize(char[] oldArray, int newSize) {
      Array.resize(oldArray, newSize);
      return oldArray;
   }

   static byte[] byteArrayResize(byte[] oldArray, int newSize) {
      Array.resize(oldArray, newSize);
      return oldArray;
   }

   static byte[][][] byteArrayArrayResize(byte[][][] oldArray, int newSize) {
      Array.resize(oldArray, newSize);
      return oldArray;
   }

   static int[] intArrayResize(int[] oldArray, int newSize) {
      Array.resize(oldArray, newSize);
      return oldArray;
   }

   static Object[] objectArrayResize(Object[] oldArray, int newSize) {
      Array.resize(oldArray, newSize);
      return oldArray;
   }
}
