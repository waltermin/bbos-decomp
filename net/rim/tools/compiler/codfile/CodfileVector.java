package net.rim.tools.compiler.codfile;

import java.util.Vector;
import net.rim.tools.compiler.io.StructuredOutputStream;
import net.rim.tools.compiler.vm.Constants;

public class CodfileVector extends Vector implements Constants {
   protected int _align;
   protected boolean _sizePrefix;
   protected boolean _sizePrefixNegative;
   protected int _offset;
   protected int _extent;

   public CodfileVector(int align, boolean sizePrefix) {
      this._align = align;
      this._sizePrefix = sizePrefix;
   }

   public CodfileVector(boolean sizePrefix) {
      this(1, sizePrefix);
   }

   public CodfileVector(int align) {
      this(align, false);
   }

   public CodfileVector() {
      this(1, false);
   }

   public void negatePrefix() {
      this._sizePrefixNegative = true;
   }

   public void writeOffset(StructuredOutputStream out) {
      out.writeShort(this._offset);
   }

   private int writePrefix(StructuredOutputStream out, boolean includeHeading) {
      out.writeSlack(this._align);
      this.setOffset(out);
      int num = this.size();
      if (this._sizePrefix) {
         int n = num;
         if (this._sizePrefixNegative) {
            n = -num;
         }

         out.writeShort(n);
      }

      return num;
   }

   public void write(StructuredOutputStream out, boolean includeHeading) {
      int num = this.writePrefix(out, includeHeading);

      for (int i = 0; i < num; i++) {
         CodfileItem item = (CodfileItem)this.elementAt(i);
         item.write(out);
      }

      this.setExtent(out);
   }

   public void write(StructuredOutputStream out) {
      this.write(out, false);
   }

   public void writeOffsets(StructuredOutputStream out) {
      int num = this.writePrefix(out, false);

      for (int i = 0; i < num; i++) {
         CodfileItem cfi = (CodfileItem)this.elementAt(i);
         int offset = cfi.getOffset();
         out.writeShort(offset);
      }
   }

   public void setOffset(StructuredOutputStream out) {
      this._offset = out.getOffset();
   }

   public void setExtent(StructuredOutputStream out) {
      this._extent = out.getOffset() - this._offset;
   }

   public int getExtent() {
      return this._extent;
   }

   public int addElementOrdered(CodfileItem item) {
      int num = this.size();
      int r = num - 1;
      int l = 0;

      while (l <= r) {
         int m = (l + r) / 2;
         CodfileItem curr = (CodfileItem)this.elementAt(m);
         if (item.compareTo(curr) < 0) {
            r = m - 1;
         } else {
            l = m + 1;
         }
      }

      this.insertElementAt(item, l);
      return l;
   }

   public void addItemOffset(CodfileItem item) {
      int offset = item.getOffset();
      if (offset <= 0) {
         throw new IllegalArgumentException("item has no offset");
      }

      int num = this.size();
      int r = num - 1;
      int l = 0;
      if (num > 0) {
         CodfileItem curr = (CodfileItem)this.elementAt(r);
         if (offset > curr.getOffset()) {
            this.addElement(item);
            return;
         }
      }

      while (l <= r) {
         int m = (l + r) / 2;
         CodfileItem curr = (CodfileItem)this.elementAt(m);
         if (offset < curr.getOffset()) {
            r = m - 1;
         } else {
            l = m + 1;
         }
      }

      this.insertElementAt(item, l);
   }
}
