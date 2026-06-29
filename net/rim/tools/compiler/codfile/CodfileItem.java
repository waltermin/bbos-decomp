package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredInputStream;
import net.rim.tools.compiler.io.StructuredOutputStream;
import net.rim.tools.compiler.vm.Constants;

public class CodfileItem implements Constants {
   protected int _offset;
   protected int _extent;
   protected int _ordinal;
   protected int _address;

   public CodfileItem(int offset) {
      this._offset = offset;
   }

   public CodfileItem() {
      this(-1);
   }

   public CodfileItem(StructuredInputStream in) {
      this(in.getOffset());
   }

   public void writeLocalOffset(StructuredOutputStream out) {
      out.writeShort(this._offset);
   }

   public void writeOffset(StructuredOutputStream out) {
      out.writeShort(this._offset);
   }

   public void writeOrdinal(StructuredOutputStream out) {
      out.writeByte(this._ordinal);
   }

   public void writeAddress(StructuredOutputStream out) {
      out.writeShort(this._address);
   }

   public void writeAddressByte(StructuredOutputStream out) {
      out.writeByte(this._address);
   }

   public void write(StructuredOutputStream _1) {
      throw null;
   }

   public void setOffset(StructuredOutputStream out) {
      this._offset = out.getOffset();
   }

   public void setOffset(int offset) {
      this._offset = offset;
   }

   public int getOffset() {
      return this._offset;
   }

   public void setExtent(StructuredOutputStream out) {
      this._extent = out.getOffset() - this._offset;
   }

   public void setExtent(int extent) {
      this._extent = extent;
   }

   public int getExtent() {
      return this._extent;
   }

   public void setOrdinal(int ordinal) {
      this._ordinal = ordinal;
   }

   public int getOrdinal() {
      return this._ordinal;
   }

   public void setAddress(int address) {
      this._address = address;
   }

   public int getAddress() {
      return this._address;
   }

   public int compareTo(Object o) {
      CodfileItem other = (CodfileItem)o;
      return this._offset - other._offset;
   }
}
