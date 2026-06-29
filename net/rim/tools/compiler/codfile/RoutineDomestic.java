package net.rim.tools.compiler.codfile;

import java.io.IOException;
import net.rim.tools.compiler.io.StructuredOutputStream;

public final class RoutineDomestic extends Routine {
   private RoutineLocal _sibling;
   private String _actualName;

   public RoutineDomestic(ClassDef classDef, Identifier name, TypeList typeList, TypeList protoTypeList) {
      super(classDef, name, typeList, protoTypeList);
      this._actualName = name.getString();
   }

   public final void setActualName(String name) {
      this._actualName = name;
   }

   public final void setSibling(RoutineLocal sibling) {
      this._sibling = sibling;
   }

   @Override
   public final void makeSymbolic(DataSection dataSection, boolean includeReturnType) {
      this.makeSymbolic(dataSection, includeReturnType, this._actualName);
   }

   @Override
   public final void write(StructuredOutputStream out) throws IOException {
      throw new IOException("unable to write domestic routine");
   }

   @Override
   public final void writeOffset(StructuredOutputStream out) {
      if (this._sibling != null) {
         super._offset = this._sibling.getOffset();
      }

      super._classDef.writeModuleOrdinal(out);
      out.writeShort(super._offset);
   }

   @Override
   public final void writeStaticOffset(StructuredOutputStream out, ClassDef classDef) {
      if (this._sibling != null) {
         super._offset = this._sibling.getOffset();
      }

      if (classDef == super._classDef) {
         classDef.writeAbsoluteClassDef(out);
         out.writeShort(super._offset);
      } else {
         int ordinal = this.addStaticFixup(out, classDef);
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

   @Override
   public final int getVTableOffset(boolean nullRef) {
      if (this._sibling != null) {
         super._address = this._sibling.getAddress();
      }

      return super.getVTableOffset(nullRef);
   }
}
