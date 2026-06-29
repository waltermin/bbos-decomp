package net.rim.tools.compiler.classfile;

import java.io.IOException;
import net.rim.tools.compiler.io.StructuredInputStream;

public final class AttributeStackMapTable extends Attribute {
   private AttributeStackMap[] _stackMaps;

   public AttributeStackMapTable(StructuredInputStream in, ConstantPool constantPool, int iName, String name) throws IOException {
      super(in, iName, name);
      int offset = in.getOffset();
      int num = in.readUnsignedShort();
      if (num > 0) {
         this._stackMaps = new AttributeStackMap[num];

         for (int i = 0; i < num; i++) {
            AttributeStackMap stackMap = new AttributeStackMap(in, constantPool);
            if (i > 0 && stackMap.getCodeOffset() <= this._stackMaps[i - 1].getCodeOffset()) {
               throw new IOException("incorrect stack map table ordering");
            }

            this._stackMaps[i] = stackMap;
         }
      }

      if (offset + super._length != in.getOffset()) {
         throw new IOException("incorrect stack map table attribute length");
      }
   }

   public final int getNumStackMaps() {
      return this._stackMaps == null ? 0 : this._stackMaps.length;
   }

   public final AttributeStackMap getStackMap(int index) {
      return this._stackMaps[index];
   }
}
