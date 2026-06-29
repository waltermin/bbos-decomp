package net.rim.tools.compiler.codfile;

import java.io.IOException;
import net.rim.tools.compiler.io.StructuredOutputStream;

public final class ClassDefDomestic extends ClassDef {
   private ClassDefLocal _sibling;
   private String _actualPackageName;
   private String _actualClassName;

   public ClassDefDomestic(DataSection dataSection, String packageName, String className) {
      super(dataSection, -1);
      this.setActualName(packageName, className);
   }

   private final void setActualName(String packageName, String className) {
      this._actualPackageName = packageName == null ? "" : packageName;
      this._actualClassName = className;
   }

   @Override
   public final String getPackageNameString() {
      return this._actualPackageName;
   }

   @Override
   public final String getClassNameString() {
      return this._actualClassName;
   }

   public final void setSibling(ClassDefLocal sibling) {
      this._sibling = sibling;
   }

   @Override
   public final int getOrdinal() {
      if (this._sibling != null) {
         super._ordinal = this._sibling.getOrdinal();
      }

      return super._ordinal;
   }

   @Override
   public final void write(StructuredOutputStream out) throws IOException {
      throw new IOException("unable to write domestic class def");
   }

   @Override
   public final void writeOrdinal(StructuredOutputStream out) {
      this.writeAbsoluteOrdinal(out);
   }

   @Override
   public final void writeRelativeOrdinal(StructuredOutputStream out) throws IOException {
      throw new IOException("cannot write ordinal for non-local class");
   }

   public final void writeAbsoluteOrdinal(StructuredOutputStream out) {
      if (this._sibling != null) {
         super._ordinal = this._sibling.getOrdinal();
      }

      this.writeModuleOrdinal(out);
      out.writeByte(super._ordinal);
   }

   @Override
   public final void writeAbsoluteClassDef(StructuredOutputStream out) {
      if (super._siblingFormat) {
         out.writeByte(super._module.getOrdinal());
         if (this._sibling != null) {
            super._ordinal = this._sibling.getOrdinal();
         }

         out.writeByte(super._ordinal);
      } else {
         this.writeAbsoluteOrdinal(out);
      }
   }

   @Override
   public final ClassRef getClassRef(DataSection dataSection) {
      DataBytes dataBytes = dataSection.getDataBytes();
      this.setPackageName(dataBytes.getIdentifier(this._actualPackageName));
      this.setClassName(dataBytes.getIdentifier(this._actualClassName));
      return super.getClassRef(dataSection);
   }

   @Override
   public final FieldDef createFieldDef(Identifier name, TypeList typeList, boolean isStatic) {
      return new FieldDefDomestic(this, name, typeList, isStatic);
   }

   @Override
   public final FieldDef makeFieldDef(DataSection dataSection, String name, boolean suppress, TypeList typeList, boolean isStatic) {
      DataBytes dataBytes = dataSection.getDataBytes();
      FieldDefDomestic fieldDef = (FieldDefDomestic)this.createFieldDef(dataBytes.getNullIdentifier(), typeList, isStatic);
      fieldDef.setActualName(name);
      this.addFieldDef(fieldDef, isStatic);
      return fieldDef;
   }

   @Override
   public final Routine createRoutine(Identifier name, TypeList typeList, TypeList protoTypeList) {
      return new RoutineDomestic(this, name, typeList, protoTypeList);
   }

   @Override
   public final Routine makeRoutine(DataSection dataSection, String name, boolean suppress, TypeList typeList, TypeList protoTypeList) {
      DataBytes dataBytes = dataSection.getDataBytes();
      RoutineDomestic routine = (RoutineDomestic)this.createRoutine(dataBytes.getNullIdentifier(), typeList, protoTypeList);
      routine.setActualName(name);
      routine.setOffset(super._module.getNumRoutines() + 65536);
      super._module.addRoutine(routine);
      return routine;
   }

   @Override
   public final int compareTo(Object o) {
      ClassDef other = (ClassDef)o;
      int classCompare = this._actualClassName.compareTo(other.getClassNameString());
      return classCompare != 0 ? classCompare : this._actualPackageName.compareTo(other.getPackageNameString());
   }

   @Override
   public final boolean equals(Object o) {
      if (!(o instanceof ClassDefDomestic)) {
         return false;
      }

      ClassDefDomestic other = (ClassDefDomestic)o;
      return this == other ? true : this._actualClassName.equals(other._actualClassName) && this._actualPackageName.equals(other._actualPackageName);
   }

   @Override
   public final int hashCode() {
      return this._actualClassName.hashCode() * 31 + this._actualPackageName.hashCode();
   }
}
