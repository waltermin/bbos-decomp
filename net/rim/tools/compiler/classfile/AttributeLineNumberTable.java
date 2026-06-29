package net.rim.tools.compiler.classfile;

import java.io.IOException;
import net.rim.tools.compiler.io.StructuredInputStream;

public final class AttributeLineNumberTable extends Attribute {
   public AttributeLineNumberTable(StructuredInputStream in, int iName, String name) throws IOException {
      super(in, iName, name);
      int offset = in.getOffset();
      int num = in.readUnsignedShort();
      in.skipBytes(num * 4);
      if (offset + super._length != in.getOffset()) {
         throw new IOException("incorrect line number table attribute length");
      }
   }
}
