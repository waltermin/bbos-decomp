package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class ClassDefNull extends ClassDef {
   public ClassDefNull(DataSection dataSection, Identifier packageName, Identifier className) {
      super(dataSection, -1);
      super._packageName = packageName;
      super._className = className;
   }

   @Override
   public final void write(StructuredOutputStream out) {
      throw new Object("cannot write null classDef");
   }

   @Override
   public final void writeModuleOrdinal(StructuredOutputStream out) {
      out.writeByte(-1);
   }

   @Override
   public final void writeOrdinal(StructuredOutputStream out) {
      this.writeAbsoluteOrdinal(out);
   }

   @Override
   public final void writeRelativeOrdinal(StructuredOutputStream out) {
      out.writeByte(-1);
   }

   public final void writeAbsoluteOrdinal(StructuredOutputStream out) {
      this.writeModuleOrdinal(out);
      out.writeByte(-1);
   }

   @Override
   public final void writeAbsoluteClassDef(StructuredOutputStream out) {
      this.writeAbsoluteOrdinal(out);
   }

   @Override
   public final FieldDef createFieldDef(Identifier name, TypeList typeList, boolean isStatic) {
      return new FieldDefForeign(this, name, typeList, isStatic);
   }

   @Override
   public final FieldDef makeFieldDef(DataSection dataSection, String name, boolean suppress, TypeList typeList, boolean isStatic) {
      DataBytes dataBytes = dataSection.getDataBytes();
      FieldDefForeign fieldDef = (FieldDefForeign)this.createFieldDef(dataBytes.getNullIdentifier(), typeList, isStatic);
      fieldDef.setActualName(name);
      return fieldDef;
   }

   @Override
   public final ClassRef getClassRef(DataSection dataSection) {
      if (super._classRef == null) {
         super._classRef = dataSection.getNullClassRef();
      }

      return super._classRef;
   }

   @Override
   public final Routine createRoutine(Identifier name, TypeList typeList, TypeList protoTypeList) {
      return new RoutineForeign(this, name, typeList, protoTypeList);
   }

   @Override
   public final Routine makeRoutine(DataSection dataSection, String name, boolean suppress, TypeList typeList, TypeList protoTypeList) {
      DataBytes dataBytes = dataSection.getDataBytes();
      RoutineForeign routine = (RoutineForeign)this.createRoutine(dataBytes.getNullIdentifier(), typeList, protoTypeList);
      routine.setActualName(name);
      return routine;
   }
}
