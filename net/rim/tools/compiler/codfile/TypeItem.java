package net.rim.tools.compiler.codfile;

import java.io.IOException;
import net.rim.tools.compiler.io.StructuredOutputStream;
import net.rim.tools.compiler.vm.Constants;

public final class TypeItem implements Constants {
   private int _bits;
   private ClassDef _classDef;
   private static final int ID_SHIFT = 24;
   private static final int NEST_SHIFT = 16;
   private static final int BASEID_SHIFT = 8;
   private static TypeItem[] _cache = new TypeItem[]{
      null,
      new TypeItem(1),
      new TypeItem(2),
      new TypeItem(3),
      new TypeItem(4),
      new TypeItem(5),
      new TypeItem(6),
      null,
      null,
      null,
      new TypeItem(10),
      new TypeItem(11),
      new TypeItem(12)
   };

   public static final TypeItem makeTypeItem(int id) {
      return _cache[id];
   }

   private TypeItem(int id) {
      this._bits = (id & 0xFF) << 24 | 1;
   }

   public TypeItem(int nesting, int baseId) {
      this._bits = 134217728 | (nesting & 0xFF) << 16 | (baseId & 0xFF) << 8 | 3;
   }

   public TypeItem(ClassDef classDef, int id) {
      this._classDef = classDef;
      this._bits = (id & 0xFF) << 24 | 3;
   }

   public TypeItem(ClassDef classDef) {
      this._classDef = classDef;
      this._bits = 117440515;
   }

   public TypeItem(int nesting, ClassDef classDef) {
      this._classDef = classDef;
      this._bits = 134217728 | (nesting & 0xFF) << 16 | 1792 | 5;
   }

   public final void makeSymbolic(DataSection dataSection) {
      if (this._classDef != null) {
         this._classDef.makeSymbolic(dataSection);
      }
   }

   public final void write(StructuredOutputStream out, int nibble) throws IOException {
      int bits = this._bits;
      int id = bits >> 24 & 0xFF;
      out.writeByte((nibble & 15) << 4 | id);
      switch (id) {
         case 0:
            throw new IOException("unexpected type id: 0x" + Integer.toHexString(id));
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 10:
         case 11:
         case 12:
            return;
         case 8:
         default:
            int nest = bits >> 16 & 0xFF;
            out.writeByte(nest);
            int bid = bits >> 8 & 0xFF;
            out.writeByte(bid);
            if (bid != 7) {
               return;
            }
         case 7:
         case 9:
            this._classDef.writeAbsoluteClassDef(out);
      }
   }

   public final int getId() {
      return this._bits >> 24 & 0xFF;
   }

   public final int getExtent() {
      return this._bits & 0xFF;
   }

   public final int compareTo(Object o) {
      TypeItem other = (TypeItem)o;
      if (this == other) {
         return 0;
      } else if (this._bits < other._bits) {
         return -1;
      } else if (this._bits > other._bits) {
         return 1;
      } else if (this._classDef != null) {
         return this._classDef.compareTo(other._classDef);
      } else {
         return other._classDef != null ? -1 : 0;
      }
   }

   @Override
   public final boolean equals(Object o) {
      if (!(o instanceof TypeItem)) {
         return false;
      } else {
         TypeItem other = (TypeItem)o;
         if (this == other) {
            return true;
         } else if (this._bits != other._bits) {
            return false;
         } else {
            return this._classDef != null ? this._classDef.equals(other._classDef) : other._classDef == null;
         }
      }
   }

   @Override
   public final int hashCode() {
      int hash = this._bits;
      if (this._classDef != null) {
         hash = hash * 31 + this._classDef.hashCode();
      }

      return hash;
   }
}
