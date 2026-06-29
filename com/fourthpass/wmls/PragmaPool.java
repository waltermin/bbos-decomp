package com.fourthpass.wmls;

final class PragmaPool {
   private Pragma[] pragma;

   PragmaPool(WMLInputStream stream) {
      int count = stream.readMBInt();
      this.pragma = new Pragma[count];

      for (int i = 0; i < count; i++) {
         int j = stream.readUInt8();
         if (j < 0 || j > 3) {
            throw new Object("Incorrect Compiled Code");
         }

         this.pragma[i] = new Pragma(stream, j);
      }
   }

   public final String getAccessDomain(ConstantPool cp) {
      try {
         for (int i = 0; i < this.pragma.length; i++) {
            if (this.pragma[i].getType() == 0) {
               return cp.getConstant(this.pragma[i].getIndex()).toString();
            }
         }
      } finally {
         return null;
      }

      return null;
   }

   public final String getAccessPath(ConstantPool cp) {
      try {
         for (int i = 0; i < this.pragma.length; i++) {
            if (this.pragma[i].getType() == 1) {
               return cp.getConstant(this.pragma[i].getIndex()).toString();
            }
         }
      } finally {
         return null;
      }

      return null;
   }
}
