package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class InitializedStaticData extends CodfileItem {
   private int _value;
   private Literal _literal;

   public InitializedStaticData(int address, int value) {
      super._address = address;
      this._value = value;
   }

   public InitializedStaticData(int address, Literal literal) {
      super._address = address;
      this._literal = literal;
   }

   @Override
   public final void write(StructuredOutputStream out) {
      this.setOffset(out);
      this.writeAddress(out);
      if (this._literal != null) {
         this._value = this._literal.getOffset();
      }

      out.writeInt(this._value);
      this.setExtent(out);
   }
}
