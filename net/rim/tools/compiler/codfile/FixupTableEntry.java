package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.exec.MyArrays;
import net.rim.tools.compiler.io.StructuredOutputStream;

public final class FixupTableEntry extends CodfileItem {
   private int _align;
   private boolean _implied;
   private CodfileItem _ref;
   private int _offsetLength;
   private int[] _offsets;
   private static final int FIXUP_INDENT;

   public FixupTableEntry(int align) {
      this._align = align;
      this.setOrdinal(-1);
   }

   private final void writeOldFmt(StructuredOutputStream out) {
      if (this._offsets != null) {
         out.writeSlack(this._align);
         this._ref.write(out);
         int num = this._offsetLength;
         out.writeMultiByteShort(super._extent);
         this.setOffset(out);
         int last = 0;

         for (int i = 0; i < num; i++) {
            out.writeMultiByteShort(this._offsets[i] - last);
            last = this._offsets[i];
         }

         if (super._extent == 0) {
            this.setExtent(out);
            if ((super._extent & 49152) != 0) {
               out.writeShort(0);
               super._offset += 2;
               return;
            }

            if ((super._extent & 16256) != 0) {
               out.writeByte(0);
               super._offset++;
               return;
            }
         } else {
            this.setExtent(out);
         }
      }
   }

   @Override
   public final void write(StructuredOutputStream out) {
      if (!this._implied) {
         this.writeOldFmt(out);
      } else {
         if (this._offsets != null) {
            out.writeSlack(this._align);
            this._ref.write(out);
         }
      }
   }

   public final CodfileItem getRef() {
      return this._ref;
   }

   public final void setRef(CodfileItem ref) {
      this._ref = ref;
   }

   public final void setImplied(boolean implied) {
      this._implied = implied;
   }

   public final void addFixup(int offset) {
      if (this._offsets == null) {
         this._offsets = new int[8];
      }

      if (this._offsetLength == this._offsets.length) {
         this._offsets = MyArrays.resize(this._offsets, this._offsetLength * 2);
      }

      this._offsets[this._offsetLength++] = offset;
   }

   @Override
   public final int compareTo(Object o) {
      FixupTableEntry other = (FixupTableEntry)o;
      int off1 = this._offsets[0];
      int off2 = other._offsets[0];
      return off1 - off2;
   }
}
