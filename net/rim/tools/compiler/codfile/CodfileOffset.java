package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class CodfileOffset extends CodfileItem {
   private CodfileItem _item;

   public CodfileOffset(int address, boolean writeByte) {
      super._address = address;
   }

   public CodfileOffset(int address) {
      this(address, false);
   }

   public CodfileOffset(CodfileItem item) {
      this._item = item;
   }

   @Override
   public final void write(StructuredOutputStream out) {
      this.setOffset(out);
      if (this._item == null) {
         this.writeAddress(out);
      } else {
         this._item.writeOffset(out);
      }

      this.setExtent(out);
   }
}
