package net.rim.tools.compiler.codfile;

import java.io.IOException;
import java.util.Vector;
import net.rim.tools.compiler.io.StructuredOutputStream;

public final class TypeList extends CodfileItem {
   private boolean _compressable = true;
   private TypeItem[] _types;

   private final void init(Vector items) {
      int num = items.size();
      this._types = new TypeItem[num];

      for (int i = 0; i < num; i++) {
         this._types[i] = (TypeItem)items.elementAt(i);
      }
   }

   public TypeList(int offset) {
      super(offset);
   }

   public TypeList(TypeItem item) {
      this._types = new TypeItem[1];
      this._types[0] = item;
   }

   public TypeList(Vector items) {
      this.init(items);
   }

   public final void makeSymbolic(DataSection dataSection) {
      int num = this.length();

      for (int i = 0; i < num; i++) {
         this._types[i].makeSymbolic(dataSection);
      }
   }

   @Override
   public final void write(StructuredOutputStream out) throws IOException {
      this.setOffset(out);
      int num = this.length();
      if (num == 0) {
         out.writeByte(0);
      } else {
         int len = this._types[0].getExtent();
         if (num > 1) {
            int count = 0;
            TypeItem curr = this._types[1];

            for (int i = 1; i < num; i++) {
               TypeItem next = i < num - 1 ? this._types[i + 1] : null;
               if (this._compressable && count < 15 && curr.equals(next)) {
                  count++;
               } else {
                  len += curr.getExtent();
                  count = 0;
                  curr = next;
               }
            }
         }

         if (len > 7) {
            if (++len < 64) {
               out.writeByte(128 + len);
               len = 0;
            } else {
               out.writeByte(192 + (len >> 4));
               len &= 15;
            }
         }

         this._types[0].write(out, len);
         if (num > 1) {
            int count = 0;
            TypeItem curr = this._types[1];

            for (int i = 1; i < num; i++) {
               TypeItem next = i < num - 1 ? this._types[i + 1] : null;
               if (this._compressable && count < 15 && curr != null && curr.equals(next)) {
                  count++;
               } else {
                  if (curr == null) {
                     throw new IOException("null typelist entry");
                  }

                  curr.write(out, count);
                  count = 0;
                  curr = next;
               }
            }
         }
      }

      this.setExtent(out);
   }

   public final void setCompressable(boolean compressable) {
      this._compressable = this._compressable && compressable;
   }

   public final int length() {
      return this._types == null ? 0 : this._types.length;
   }

   public final int getLocalCount() {
      int num = this.length();
      int count = num;

      for (int i = 0; i < num; i++) {
         int id = this._types[i].getId();
         if (id == 6 || id == 12) {
            count++;
         }
      }

      return count;
   }

   @Override
   public final int compareTo(Object o) {
      TypeList other = (TypeList)o;
      if (this == other) {
         return 0;
      }

      int num = this.length();
      int otherNum = other.length();
      if (num < otherNum) {
         return -1;
      }

      if (num > otherNum) {
         return 1;
      }

      for (int i = 0; i < num; i++) {
         int result = this._types[i].compareTo(other._types[i]);
         if (result != 0) {
            return result;
         }
      }

      return 0;
   }

   @Override
   public final boolean equals(Object o) {
      if (!(o instanceof TypeList)) {
         return false;
      }

      TypeList other = (TypeList)o;
      if (this == other) {
         return true;
      }

      int num = this.length();
      if (num != other.length()) {
         return false;
      }

      for (int i = 0; i < num; i++) {
         if (!this._types[i].equals(other._types[i])) {
            return false;
         }
      }

      return true;
   }

   public final boolean equalsSkip(TypeList other) {
      if (this == other) {
         return true;
      }

      int num = this.length();
      if (num != other.length()) {
         return false;
      }

      for (int i = 1; i < num; i++) {
         if (!this._types[i].equals(other._types[i])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public final int hashCode() {
      int hash = 305419896;
      int num = this.length();

      for (int i = 0; i < num; i++) {
         hash = hash * 31 + this._types[i].hashCode();
      }

      return hash;
   }
}
