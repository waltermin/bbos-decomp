package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class InterfaceMethodRef extends CodfileItem {
   protected Member _member;
   protected ClassDef _classDef;
   protected Identifier _name;
   protected TypeList _protoTypeList;
   protected TypeList _typeList;

   public InterfaceMethodRef(DataSection dataSection, Member member) {
      this._member = member;
      this._classDef = member.getClassDef();
      this._name = member.getName();
      if (!(member instanceof RoutineNull)) {
         Routine routine = (Routine)member;
         routine.makeSymbolic(dataSection, true);
         this._protoTypeList = routine.getProtoTypeList();
         this._typeList = routine.getTypeList();
      }
   }

   @Override
   public final void write(StructuredOutputStream out) {
      this.setOffset(out);
      this._classDef.writeAbsoluteClassDef(out);
      this._name.writeOffset(out);
      this._protoTypeList.writeOffset(out);
      this._typeList.writeOffset(out);
      this.setExtent(out);
   }
}
