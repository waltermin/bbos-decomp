package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredInputStream;
import net.rim.tools.compiler.io.StructuredOutputStream;

public class CodfileItemRelative extends CodfileItem {
   public CodfileItemRelative() {
   }

   public CodfileItemRelative(int offset) {
      super(offset);
   }

   public CodfileItemRelative(StructuredInputStream in) {
      super(in);
   }

   @Override
   public void write(StructuredOutputStream out) {
      this.setOffset(out);
      this.writeRelative(out, 0);
      this.setExtent(out);
   }

   public void writeRelative(StructuredOutputStream _1, int _2) {
      throw null;
   }
}
