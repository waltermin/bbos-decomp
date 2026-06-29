package net.rim.tools.compiler.classfile;

import java.io.IOException;
import net.rim.tools.compiler.io.StructuredInputStream;
import net.rim.tools.compiler.vm.Constants;

public class Attribute implements Constants {
   ConstantPool _constantPool;
   int _iName;
   String _name;
   byte[] _bytes;
   int _start;
   int _length;

   Attribute(StructuredInputStream in, int iName, String name) {
      this._iName = iName;
      this._name = name;
      this._length = in.readInt();
   }

   public Attribute(StructuredInputStream in, ConstantPool constantPool, int iName, String name) throws IOException {
      this(in, iName, name);
      this._constantPool = constantPool;
      this._start = in.getOffset();
      this._bytes = in.getBytes();
      in.skipBytes(this._length);
      if (this._start + this._length != in.getOffset()) {
         throw new IOException("incorrect attribute length");
      }
   }

   public static Attribute read(StructuredInputStream in, ConstantPool constantPool, int list, boolean shallow) throws IOException {
      Attribute att = null;
      int iName = in.readUnsignedShort();
      String name = constantPool.getString(iName);
      if (shallow) {
         att = new Attribute(in, constantPool, iName, name);
      } else if (name.equals(AttributeList.NAME_CODE)) {
         if (list != 3) {
            throw new IOException("Code attribute found outside of method");
         }

         att = new AttributeCode(in, constantPool, iName, name);
      } else if (name.equals(AttributeList.NAME_LINENUMBERTABLE)) {
         if (list != 4) {
            throw new IOException("LineNumberTable attribute found outside of Code attribute");
         }

         att = new AttributeLineNumberTable(in, iName, name);
      } else if (name.equals(AttributeList.NAME_STACKMAP)) {
         if (list != 4) {
            throw new IOException("StackMap attribute found outside of Code attribute");
         }

         att = new AttributeStackMapTable(in, constantPool, iName, name);
      } else if (name.equals(AttributeList.NAME_LOCALVARIABLETABLE)) {
         if (list != 4) {
            throw new IOException("LocalVariableTable attribute found outside of Code attribute");
         }

         att = new AttributeLocalVariableTable(in, constantPool, iName, name);
      } else {
         att = new Attribute(in, constantPool, iName, name);
         if (!name.equals(AttributeList.NAME_DEPRECATED) && !name.equals(AttributeList.NAME_SYNTHETIC)) {
            if (name.regionMatches(true, 0, AttributeList.NAME_STACKMAP, 0, name.length()) && list == 4) {
               throw new IOException("StackMap attribute found with incorrect case");
            }
         } else if (att._length != 0) {
            throw new IOException("incorrect attribute length for: " + name);
         }
      }

      return att;
   }

   private int getShort(int offset) {
      offset += this._start;
      int value = (this._bytes[offset] & 255) << 8;
      return value | this._bytes[offset + 1] & 0xFF;
   }

   public String getName() {
      return this._name;
   }

   public int getConstantValue(boolean isFloat) {
      int index = this.getShort(0);
      return this._constantPool.getValue(index, isFloat);
   }

   public long getConstantValueLong(boolean isDouble) {
      int index = this.getShort(0);
      return this._constantPool.getLongValue(index, isDouble);
   }

   public String getConstantString() {
      int index = this.getShort(0);
      return this._constantPool.getStringValue(index);
   }

   public int getNumExceptions() {
      return this.getShort(0);
   }

   public String getExceptionClassName(int index) {
      index = (index + 1) * 2;
      int num = this.getShort(index);
      return this._constantPool.getClassName(num);
   }

   public String getSourceFileName() {
      int index = this.getShort(0);
      return this._constantPool.getString(index);
   }
}
