package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public class CodfileData extends CodfileItem {
   protected boolean _needsHeader;
   protected boolean _isString;
   protected int _arrayType;
   protected int _length;
   protected byte[] _bytes;
   public static final int HEADER_SIZE = 4;

   protected CodfileData() {
   }

   protected CodfileData(byte[] bytes, int arrayType, boolean needsHeader, boolean isString) {
      this._bytes = bytes;
      this._length = bytes.length;
      this._arrayType = arrayType;
      this._needsHeader = needsHeader;
      this._isString = isString;
   }

   static int elementSize(int arrayType) {
      switch (arrayType) {
         case 0:
            return 1;
         case 1:
         default:
            return 1;
         case 2:
            return 1;
         case 3:
            return 2;
         case 4:
            return 2;
         case 5:
            return 4;
         case 6:
            return 8;
      }
   }

   @Override
   public void write(StructuredOutputStream out) {
      int eltSize = elementSize(this._arrayType);
      if (this._needsHeader) {
         out.writeSlack(4);
         if (eltSize == 8 && (out.getOffset() & 7) == 0) {
            out.writeInt(0);
         }

         int mask = -805306368;
         if (this._isString) {
            mask |= 136314880;
         }

         mask |= this._arrayType << 17 & 1966080;
         int length = this._length / eltSize;
         mask |= length << 0 & 131071;
         out.writeInt(mask);
      }

      out.writeSlack(eltSize);
      this.setOffset(out);
      out.write(this._bytes);
      this.writeTerminator(out);
      this.setExtent(out);
   }

   public void writeTerminator(StructuredOutputStream _1) {
      throw null;
   }

   public void setNeedsHeader() {
      this._needsHeader = true;
   }

   public boolean getNeedsHeader() {
      return this._needsHeader;
   }

   public void setArrayType(int arrayType) {
      this._arrayType = arrayType;
   }

   public int length() {
      return this._length;
   }

   @Override
   public int compareTo(Object o) {
      CodfileData other = (CodfileData)o;
      if (this == other) {
         return 0;
      }

      byte[] obytes = other._bytes;
      if (this._bytes == null) {
         return 1;
      }

      int len = this._bytes.length;
      if (len > obytes.length) {
         len = obytes.length;
      }

      for (int i = 0; i < len; i++) {
         int l = this._bytes[i] & 255;
         int r = obytes[i] & 255;
         if (l != r) {
            return l - r;
         }
      }

      if (len < obytes.length) {
         return -1;
      } else {
         return len < this._bytes.length ? 1 : 0;
      }
   }
}
