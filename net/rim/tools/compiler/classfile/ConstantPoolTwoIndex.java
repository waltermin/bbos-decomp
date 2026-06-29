package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;

public class ConstantPoolTwoIndex extends ConstantPoolEntry {
   int _index1;
   int _index2;

   public ConstantPoolTwoIndex(int tag, StructuredInputStream in) {
      super(tag);
      this._index1 = in.readUnsignedShort();
      this._index2 = in.readUnsignedShort();
   }
}
