package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;

public final class ConstantPoolString extends ConstantPoolIndex {
   private ConstantPoolUTF8 _string;

   public ConstantPoolString(int tag, StructuredInputStream in) {
      super(tag, in);
   }

   @Override
   public final void resolve(ConstantPool constantPool) {
      if (this._string == null) {
         this._string = (ConstantPoolUTF8)constantPool.getEntry(super._index);
      }
   }

   public final String getString() {
      return this._string.getString();
   }
}
