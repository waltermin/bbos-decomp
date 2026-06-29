package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;
import net.rim.tools.compiler.vm.Constants;

public class ConstantPoolEntry implements Constants {
   public static final byte _UTF8;
   public static final byte _Integer;
   public static final byte _Float;
   public static final byte _Long;
   public static final byte _Double;
   public static final byte _Class;
   public static final byte _AString;
   public static final byte _FieldRef;
   public static final byte _MethodRef;
   public static final byte _InterfaceMethodRef;
   public static final byte _NameAndType;
   static final byte _Manufactured;

   public ConstantPoolEntry(int tag) {
   }

   public ConstantPoolEntry() {
      this(-1);
   }

   public static ConstantPoolEntry read(StructuredInputStream in) {
      int tag = in.readByte();
      switch (tag) {
         case 0:
         case 2:
            invalid();
            return null;
         case 1:
         default:
            return new ConstantPoolUTF8(tag, in);
         case 3:
         case 4:
            return new ConstantPoolInteger(tag, in, tag == 4);
         case 5:
         case 6:
            return new ConstantPoolLong(tag, in, tag == 6);
         case 7:
            return new ConstantPoolClass(tag, in);
         case 8:
            return new ConstantPoolString(tag, in);
         case 9:
            return new ConstantPoolFieldRef(tag, in);
         case 10:
            return new ConstantPoolMethodRef(tag, in);
         case 11:
            return new ConstantPoolInterfaceMethodRef(tag, in);
         case 12:
            return new ConstantPoolNameAndType(tag, in);
      }
   }

   public void resolve(ConstantPool constantPool) {
   }

   public void verify() {
   }

   public static void invalid() {
      throw new Object("Invalid constant pool entry.");
   }
}
