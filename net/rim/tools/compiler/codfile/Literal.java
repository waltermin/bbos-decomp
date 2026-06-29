package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class Literal extends CodfileData {
   private String _string;
   private boolean _unicode;

   private static final byte[] makeBytes(String str, boolean unicode) {
      int num = str.length();
      byte[] bytes = new byte[unicode ? num * 2 : num];
      int n = 0;

      for (int i = 0; i < num; i++) {
         char c = str.charAt(i);
         bytes[n++] = (byte)(c & 0xFF);
         if (unicode) {
            bytes[n++] = (byte)(c >> '\b' & 0xFF);
         }
      }

      return bytes;
   }

   public Literal(String str, boolean unicode, boolean needsHeader) {
      super(makeBytes(str, unicode), unicode ? 3 : 2, needsHeader, true);
      this._string = str;
      this._unicode = unicode;
   }

   @Override
   public final void writeTerminator(StructuredOutputStream out) {
      if (this._unicode) {
         out.writeChar(0);
      } else {
         out.writeByte(0);
      }
   }

   @Override
   public final boolean equals(Object o) {
      if (!(o instanceof Literal)) {
         return false;
      }

      Literal other = (Literal)o;
      return this == other ? true : this._string.equals(other._string);
   }

   @Override
   public final int hashCode() {
      return this._string.hashCode();
   }
}
