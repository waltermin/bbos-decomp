package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class Bytes extends CodfileData {
   public Bytes(byte[] bytes, int arrayType, boolean needsHeader) {
      super(bytes, arrayType, needsHeader, false);
   }

   public final boolean matches(byte[] bytes, int arrayType) {
      if (super._arrayType != arrayType && arrayType != -1) {
         return false;
      }

      if (super._bytes.length != bytes.length) {
         return false;
      }

      int num = bytes.length;

      for (int i = 0; i < num; i++) {
         if (super._bytes[i] != bytes[i]) {
            return false;
         }
      }

      return true;
   }

   @Override
   public final void writeTerminator(StructuredOutputStream out) {
   }
}
