package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;

public final class ConstantPool {
   private ConstantPoolEntry[] _entries;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public ConstantPool(StructuredInputStream in, boolean shallow) {
      int numEntries = in.readUnsignedShort();
      if (numEntries > 1) {
         this._entries = new ConstantPoolEntry[numEntries];
         this._entries[0] = new ConstantPoolEntry();

         for (int i = 1; i < numEntries; i++) {
            ConstantPoolEntry entry = ConstantPoolEntry.read(in);
            this._entries[i] = entry;
            if (entry instanceof ConstantPoolLong) {
               this._entries[++i] = this._entries[0];
               if (i >= numEntries) {
                  throw new Object("invalid constant pool in class file");
               }
            }
         }
      }

      if (!shallow) {
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;

            for (int var9 = 1; var9 < numEntries; var9++) {
               this._entries[var9].resolve(this);
            }

            for (int i = 1; i < numEntries; i++) {
               this._entries[i].verify();
            }

            var7 = false;
         } finally {
            if (var7) {
               throw new Object("invalid constant pool in class file");
            }
         }
      }
   }

   public final int getNumEntries() {
      return this._entries == null ? 1 : this._entries.length;
   }

   public final ConstantPoolEntry getEntry(int index) {
      if (index != 0 && index < this.getNumEntries()) {
         return this._entries[index];
      } else {
         throw new Object(((StringBuffer)(new Object("invalid constant pool index: "))).append(index).toString());
      }
   }

   public final String getString(int index) {
      ConstantPoolEntry entry = this.getEntry(index);
      if (!(entry instanceof ConstantPoolUTF8)) {
         ConstantPoolEntry.invalid();
         return null;
      } else {
         ConstantPoolUTF8 cpu = (ConstantPoolUTF8)entry;
         return cpu.getString();
      }
   }

   public final String getClassName(int index) {
      ConstantPoolEntry entry = this.getEntry(index);
      if (!(entry instanceof ConstantPoolClass)) {
         ConstantPoolEntry.invalid();
         return null;
      } else {
         ConstantPoolClass cpc = (ConstantPoolClass)entry;
         return cpc.getName();
      }
   }

   public final String getStringValue(int index) {
      ConstantPoolEntry entry = this.getEntry(index);
      if (!(entry instanceof ConstantPoolString)) {
         ConstantPoolEntry.invalid();
         return null;
      } else {
         ConstantPoolString cps = (ConstantPoolString)entry;
         return cps.getString();
      }
   }

   public final int getValue(int index, boolean isFloat) {
      ConstantPoolEntry entry = this.getEntry(index);
      if (!(entry instanceof ConstantPoolInteger)) {
         ConstantPoolEntry.invalid();
         return 0;
      }

      ConstantPoolInteger value = (ConstantPoolInteger)entry;
      if (isFloat ^ value.isFloat()) {
         ConstantPoolEntry.invalid();
      }

      return value.getValue();
   }

   public final long getLongValue(int index, boolean isDouble) {
      ConstantPoolEntry entry = this.getEntry(index);
      if (!(entry instanceof ConstantPoolLong)) {
         ConstantPoolEntry.invalid();
         return 0;
      }

      ConstantPoolLong value = (ConstantPoolLong)entry;
      if (isDouble ^ value.isDouble()) {
         ConstantPoolEntry.invalid();
      }

      return value.getValue();
   }
}
