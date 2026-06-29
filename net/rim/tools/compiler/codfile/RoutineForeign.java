package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class RoutineForeign extends Routine {
   private FixupTableEntry _fixups;
   private String _actualName;

   public RoutineForeign(ClassDef classDef, Identifier name, TypeList typeList, TypeList protoTypeList) {
      super(classDef, name, typeList, protoTypeList);
      super._offset = -1;
      super._address = -1;
      this._actualName = name.getString();
   }

   @Override
   public final void makeSymbolic(DataSection dataSection, boolean includeReturnType) {
      this.makeSymbolic(dataSection, includeReturnType, this._actualName);
   }

   public final void setActualName(String name) {
      this._actualName = name;
   }

   private final int addFixup(StructuredOutputStream out) {
      Object obj = out.getCookie();
      if (obj != null) {
         if (this._fixups == null) {
            DataSection dataSection = (DataSection)obj;
            this.makeSymbolic(dataSection, false);
            this._fixups = new FixupTableEntry(2);
            this._fixups.setRef(this.getFixupRef(dataSection));
         }

         this._fixups.addFixup(out.getOffset());
      }

      int ordinal = -1;
      if (this._fixups != null) {
         ordinal = this._fixups.getOrdinal();
      }

      return ordinal;
   }

   @Override
   public final void write(StructuredOutputStream out) {
      throw new Object("unable to write foreign routine");
   }

   @Override
   public final void writeOffset(StructuredOutputStream out) {
      int ordinal = -1;
      if (!(super._classDef instanceof ClassDefNull)) {
         ordinal = this.addFixup(out);
      }

      super._classDef.writeModuleOrdinal(out);
      out.writeShort(ordinal);
   }

   @Override
   public final void writeStaticOffset(StructuredOutputStream out, ClassDef classDef) {
      int ordinal = -1;
      if (!(super._classDef instanceof ClassDefNull)) {
         ordinal = this.addStaticFixup(out, classDef);
      }

      out.writeShort(-1);
      out.writeShort(ordinal);
   }

   @Override
   public final void writeFixups(DataSection dataSection) {
      if (this._fixups != null) {
         dataSection.addMethodFixup(this._fixups);
      }

      super.writeFixups(dataSection);
   }
}
