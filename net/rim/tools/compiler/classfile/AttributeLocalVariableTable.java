package net.rim.tools.compiler.classfile;

import java.io.IOException;
import net.rim.tools.compiler.io.StructuredInputStream;

public final class AttributeLocalVariableTable extends Attribute {
   public AttributeLocalVariableTable(StructuredInputStream in, ConstantPool constantPool, int iName, String name) throws IOException {
      super(in, iName, name);
      int offset = in.getOffset();
      int num = in.readUnsignedShort();
      if (num > 0) {
         in.skipBytes(num * 10);
      }

      if (offset + super._length != in.getOffset()) {
         throw new IOException("incorrect local variable table attribute length");
      }
   }
}
