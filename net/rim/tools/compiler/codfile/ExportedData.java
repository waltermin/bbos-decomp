package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class ExportedData extends CodfileItem {
   private Identifier _name;
   private int _length;
   private Bytes _bytes;

   public ExportedData(DataSection dataSection, Bytes bytes, String name) {
      DataBytes dataBytes = dataSection.getDataBytes();
      this._name = dataBytes.getIdentifier(name);
      this._bytes = bytes;
      this._length = bytes.length();
   }

   @Override
   public final void write(StructuredOutputStream out) {
      this.setOffset(out);
      this._name.writeOffset(out);
      out.writeShort(this._length);
      this._bytes.writeOffset(out);
      this.setExtent(out);
   }

   @Override
   public final int compareTo(Object o) {
      ExportedData other = (ExportedData)o;
      return this._name.compareTo(other._name);
   }
}
