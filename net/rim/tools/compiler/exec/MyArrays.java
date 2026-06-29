package net.rim.tools.compiler.exec;

import net.rim.tools.compiler.analysis.Instruction;
import net.rim.tools.compiler.analysis.InstructionTarget;
import net.rim.tools.compiler.codfile.CodfileItem;
import net.rim.tools.compiler.codfile.CodfileLabel;
import net.rim.tools.compiler.types.Field;
import net.rim.tools.compiler.types.Method;
import net.rim.vm.Array;

public class MyArrays {
   private MyArrays() {
   }

   public static byte[] resize(byte[] a, int size) {
      Array.resize(a, size);
      return a;
   }

   public static char[] resize(char[] a, int size) {
      Array.resize(a, size);
      return a;
   }

   public static int[] resize(int[] a, int size) {
      Array.resize(a, size);
      return a;
   }

   public static Object[] resize(Object[] a, int size) {
      Array.resize(a, size);
      return a;
   }

   public static Method[] resize(Method[] a, int size) {
      Array.resize(a, size);
      return a;
   }

   public static Field[] resize(Field[] a, int size) {
      Array.resize(a, size);
      return a;
   }

   public static Instruction[] resize(Instruction[] a, int size) {
      Array.resize(a, size);
      return a;
   }

   public static InstructionTarget[] resize(InstructionTarget[] a, int size) {
      Array.resize(a, size);
      return a;
   }

   public static CodfileItem[] resize(CodfileItem[] a, int size) {
      Array.resize(a, size);
      return a;
   }

   public static CodfileLabel[] resize(CodfileLabel[] a, int size) {
      Array.resize(a, size);
      return a;
   }
}
