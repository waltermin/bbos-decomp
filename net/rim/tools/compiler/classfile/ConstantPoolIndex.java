package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;

public class ConstantPoolIndex extends ConstantPoolEntry {
   int _index;

   public ConstantPoolIndex(int tag, StructuredInputStream in) {
      super(tag);
      this._index = in.readUnsignedShort();
   }
}
