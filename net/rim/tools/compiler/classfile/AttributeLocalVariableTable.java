package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;

public final class AttributeLocalVariableTable extends Attribute {
   public AttributeLocalVariableTable(StructuredInputStream in, ConstantPool constantPool, int iName, String name) {
      super(in, iName, name);
      int offset = in.getOffset();
      int num = in.readUnsignedShort();
      if (num > 0) {
         in.skipBytes(num * 10);
      }

      if (offset + super._length != in.getOffset()) {
         throw new Object("incorrect local variable table attribute length");
      }
   }
}
