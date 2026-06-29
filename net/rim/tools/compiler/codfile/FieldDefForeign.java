package net.rim.tools.compiler.codfile;

import java.io.IOException;
import net.rim.tools.compiler.io.StructuredOutputStream;

public final class FieldDefForeign extends FieldDef {
   private String _actualName;

   public FieldDefForeign(ClassDef classDef, Identifier name, TypeList typeList, boolean isStatic) {
      super(classDef, name, typeList, isStatic);
      this.setAddress(-1);
      this._actualName = name.getString();
   }

   @Override
   public final void makeSymbolic(DataSection dataSection) {
      super._name = dataSection.getDataBytes().getIdentifier(this._actualName);
      super._typeList = dataSection.getTypeLists().getTypeList(super._typeList, dataSection, false);
   }

   public final void setActualName(String name) {
      this._actualName = name;
   }

   @Override
   public final void write(StructuredOutputStream out) throws IOException {
      throw new IOException("cannot write non-local member fields");
   }

   @Override
   public final void writeStaticOffset(StructuredOutputStream out, ClassDef classDef) {
      if (!super._isStatic) {
         super.writeStaticOffset(out, classDef);
      } else {
         int ordinal = this.addFixup(out, classDef);
         out.writeShort(-1);
         out.writeShort(ordinal);
      }
   }
}
