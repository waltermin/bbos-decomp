package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class StackMap extends CodfileItemRelative {
   private CodfileLabel _label;
   private TypeList _typeList;

   public StackMap(CodfileLabel label, DataSection dataSection, TypeList typeList) {
      this._label = label;
      this._typeList = dataSection.getTypeLists().getTypeList(typeList, dataSection, true);
   }

   @Override
   public final void writeRelative(StructuredOutputStream out, int relative) {
      out.writeShort(this._label.getOffset() + relative);
      this._typeList.writeOffset(out);
   }

   public static final int getSize() {
      return 4;
   }
}
