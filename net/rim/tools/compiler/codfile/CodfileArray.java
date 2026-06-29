package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.exec.MyArrays;
import net.rim.tools.compiler.io.StructuredOutputStream;
import net.rim.tools.compiler.vm.Constants;

public final class CodfileArray extends CodfileItem implements Constants {
   private CodfileItem[] _items;
   private int _numItems;
   private int _align;
   private boolean _sizePrefix;

   public CodfileArray(int capacity, int align, boolean sizePrefix) {
      if (capacity > 0) {
         this._items = new CodfileItem[capacity];
      }

      this._align = align;
      this._sizePrefix = sizePrefix;
   }

   public CodfileArray(int capacity, boolean sizePrefix) {
      this(capacity, 1, sizePrefix);
   }

   public CodfileArray(int capacity, int align) {
      this(capacity, align, false);
   }

   public CodfileArray(int capacity) {
      this(capacity, 1, false);
   }

   public CodfileArray() {
      this(0, 1, false);
   }

   private final int writePrefix(StructuredOutputStream out, boolean includeHeading) {
      out.writeSlack(this._align);
      this.setOffset(out);
      int num = this._numItems;
      if (this._sizePrefix) {
         out.writeShort(num);
      }

      return num;
   }

   public final void write(StructuredOutputStream out, boolean includeHeading) {
      int num = this.writePrefix(out, includeHeading);

      for (int i = 0; i < num; i++) {
         this._items[i].write(out);
      }

      this.setExtent(out);
   }

   @Override
   public final void write(StructuredOutputStream out) {
      this.write(out, false);
   }

   public final void writeRelative(StructuredOutputStream out, int relative) {
      int num = this.writePrefix(out, false);

      for (int i = 0; i < num; i++) {
         CodfileItem item = this._items[i];
         if (!(item instanceof CodfileItemRelative)) {
            throw new Object("cannot write relative offset for non-relative item");
         }

         CodfileItemRelative cfr = (CodfileItemRelative)item;
         cfr.writeRelative(out, relative);
      }

      this.setExtent(out);
   }

   public final void writeAbsoluteOrdinals(StructuredOutputStream out) {
      int num = this.writePrefix(out, false);

      for (int i = 0; i < num; i++) {
         CodfileItem item = this._items[i];
         if (!(item instanceof ClassDef)) {
            throw new Object("cannot write absolute ordinals for non-classdef item");
         }

         ClassDef classDef = (ClassDef)item;
         classDef.writeAbsoluteClassDef(out);
      }

      this.setExtent(out);
   }

   public final void writeOffsets(StructuredOutputStream out) {
      int num = this.writePrefix(out, false);

      for (int i = 0; i < num; i++) {
         this._items[i].writeLocalOffset(out);
      }
   }

   public final int size() {
      return this._numItems;
   }

   public final CodfileItem elementAt(int index) {
      return this._items[index];
   }

   public final void addElement(CodfileItem item) {
      if (this._items == null) {
         this._items = new CodfileItem[1];
      } else if (this._numItems == this._items.length) {
         this._items = MyArrays.resize(this._items, this._items.length * 2);
      }

      this._items[this._numItems++] = item;
   }
}
