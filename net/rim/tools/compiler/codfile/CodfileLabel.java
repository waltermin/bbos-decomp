package net.rim.tools.compiler.codfile;

public final class CodfileLabel {
   private int _ordinal;
   private int _offset;

   public CodfileLabel(int offset) {
      this._offset = offset;
   }

   public CodfileLabel(int ordinal, int offset) {
      this._ordinal = ordinal;
      this._offset = offset;
   }

   public final int getOrdinal() {
      return this._ordinal;
   }

   public final void setOffset(int offset) {
      this._offset = offset;
   }

   public final int getOffset() {
      return this._offset;
   }
}
