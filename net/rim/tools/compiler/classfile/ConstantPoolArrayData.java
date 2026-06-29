package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.vm.Constants;

public final class ConstantPoolArrayData extends ConstantPoolEntry implements Constants {
   private int _arrayType;
   private byte[] _bytes;
   private ConstantPoolFieldRef _fieldRef;

   public ConstantPoolArrayData(int arrayType, long[] arrayData, ConstantPoolFieldRef fieldRef) {
      super(-1);
      this._arrayType = arrayType;
      this._fieldRef = fieldRef;
      switch (this._arrayType) {
         case 1:
         case 2:
         default:
            this._bytes = this.byteToByte(arrayData);
            return;
         case 3:
         case 4:
            this._bytes = this.charToByte(arrayData);
            return;
         case 5:
            this._bytes = this.intToByte(arrayData);
            return;
         case 6:
            this._bytes = this.longToByte(arrayData);
         case 0:
      }
   }

   public final int getArrayType() {
      return this._arrayType;
   }

   public final byte[] getBytes() {
      return this._bytes;
   }

   public final ConstantPoolFieldRef getFieldRef() {
      return this._fieldRef;
   }

   private final byte[] byteToByte(long[] values) {
      byte[] data = new byte[values.length];

      for (int i = 0; i < values.length; i++) {
         data[i] = (byte)values[i];
      }

      return data;
   }

   private final byte[] charToByte(long[] values) {
      byte[] data = new byte[2 * values.length];

      for (int i = 0; i < values.length; i++) {
         long value = values[i];
         byte b1 = (byte)(value & 255);
         byte b2 = (byte)(value >> 8 & 255);
         data[2 * i + 0] = b1;
         data[2 * i + 1] = b2;
      }

      return data;
   }

   private final byte[] intToByte(long[] values) {
      byte[] data = new byte[4 * values.length];

      for (int i = 0; i < values.length; i++) {
         long value = values[i];
         byte b1 = (byte)(value & 255);
         byte b2 = (byte)(value >> 8 & 255);
         byte b3 = (byte)(value >> 16 & 255);
         byte b4 = (byte)(value >> 24 & 255);
         data[4 * i + 0] = b1;
         data[4 * i + 1] = b2;
         data[4 * i + 2] = b3;
         data[4 * i + 3] = b4;
      }

      return data;
   }

   private final byte[] longToByte(long[] values) {
      byte[] data = new byte[8 * values.length];

      for (int i = 0; i < values.length; i++) {
         long value = values[i];
         byte b1 = (byte)(value & 255);
         byte b2 = (byte)(value >> 8 & 255);
         byte b3 = (byte)(value >> 16 & 255);
         byte b4 = (byte)(value >> 24 & 255);
         byte b5 = (byte)(value >> 32 & 255);
         byte b6 = (byte)(value >> 40 & 255);
         byte b7 = (byte)(value >> 48 & 255);
         byte b8 = (byte)(value >> 56 & 255);
         data[8 * i + 0] = b1;
         data[8 * i + 1] = b2;
         data[8 * i + 2] = b3;
         data[8 * i + 3] = b4;
         data[8 * i + 4] = b5;
         data[8 * i + 5] = b6;
         data[8 * i + 6] = b7;
         data[8 * i + 7] = b8;
      }

      return data;
   }
}
