package net.rim.tools.compiler.codfile;

import java.io.IOException;
import net.rim.tools.compiler.io.StructuredOutputStream;

public final class FieldDefDomestic extends FieldDef {
   private FieldDefLocal _sibling;
   private String _actualName;

   public FieldDefDomestic(ClassDef classDef, Identifier name, TypeList typeList, boolean isStatic) {
      super(classDef, name, typeList, isStatic);
      this._actualName = name.getString();
   }

   public final void setActualName(String name) {
      this._actualName = name;
   }

   public final void setSibling(FieldDefLocal sibling) {
      this._sibling = sibling;
   }

   @Override
   public final void makeSymbolic(DataSection dataSection) {
      super._classDef.getClassRef(dataSection);
      super._name = dataSection.getDataBytes().getIdentifier(this._actualName);
      super._typeList = dataSection.getTypeLists().getTypeList(super._typeList, dataSection, false);
   }

   @Override
   public final void write(StructuredOutputStream out) throws IOException {
      throw new IOException("cannot write non-local member fields");
   }

   @Override
   public final void writeStaticOffset(StructuredOutputStream out, ClassDef classDef) {
      if (this._sibling != null) {
         super._address = this._sibling.getAddress();
      }

      if (!super._isStatic) {
         super.writeStaticOffset(out, classDef);
      } else if (classDef == super._classDef) {
         classDef.writeAbsoluteClassDef(out);
         this.writeAddress(out);
      } else {
         int ordinal = this.addFixup(out, classDef);
         out.writeShort(-1);
         out.writeShort(ordinal);
      }
   }

   @Override
   public final void writeMemberAddress(StructuredOutputStream out, boolean nullRef) {
      if (this._sibling != null) {
         super._address = this._sibling.getAddress();
      }

      super.writeMemberAddress(out, nullRef);
   }
}
