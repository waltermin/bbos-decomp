package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;
import net.rim.tools.compiler.vm.IdEncode;

public final class Identifier extends CodfileData {
   private String _string;

   private static final byte[] makeBytes(String str) {
      str = IdEncode.encode(str);
      int num = str.length();
      byte[] bytes = new byte[num];

      for (int i = 0; i < num; i++) {
         bytes[i] = (byte)str.charAt(i);
      }

      return bytes;
   }

   public Identifier(String str) {
      super(makeBytes(str), 2, false, false);
      this._string = str;
   }

   public Identifier() {
      super._offset = 0;
      this._string = "";
   }

   @Override
   public final void writeTerminator(StructuredOutputStream out) {
      out.writeByte(0);
   }

   public final String getString() {
      return this._string;
   }

   @Override
   public final boolean equals(Object o) {
      if (!(o instanceof Identifier)) {
         if (!(o instanceof Object)) {
            return false;
         }

         String string = (String)o;
         return this._string == null ? false : this._string.equals(string);
      } else {
         Identifier other = (Identifier)o;
         if (this == other) {
            return true;
         } else if (this._string == null && other._string == null) {
            return true;
         } else {
            return this._string != null && other._string != null ? this._string.equals(other._string) : false;
         }
      }
   }

   @Override
   public final int hashCode() {
      return this._string != null ? this._string.hashCode() : super.hashCode();
   }
}
