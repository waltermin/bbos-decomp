package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class ClassDefForeign extends ClassDef {
   private CodfileOffset _fixupRef;
   private FixupTableEntry _codeFixups;
   private String _actualPackageName;
   private String _actualClassName;

   public ClassDefForeign(DataSection dataSection, String packageName, String className) {
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

   @Override
   public final void makeSymbolic(DataSection dataSection) {
      this.getClassRef(dataSection);
   }

   @Override
   protected final CodfileOffset getFixupRef(DataSection dataSection) {
      if (this._fixupRef == null) {
         this._fixupRef = new CodfileOffset(this.getClassRef(dataSection));
      }

      return this._fixupRef;
   }

   @Override
   public final void write(StructuredOutputStream out) {
      throw new Object("unable to write foreign class def");
   }

   @Override
   public final void writeOrdinal(StructuredOutputStream out) {
      this.writeAbsoluteOrdinal(out);
   }

   @Override
   public final void writeRelativeOrdinal(StructuredOutputStream out) {
      throw new Object("cannot write ordinal for non-local class");
   }

   public final void writeAbsoluteOrdinal(StructuredOutputStream out) {
      Object obj = out.getCookie();
      if (obj != null) {
         int offset = out.getOffset();
         if (out.writingCode()) {
            if (this._codeFixups == null) {
               this._codeFixups = new FixupTableEntry(2);
               this._codeFixups.setRef(this.getFixupRef((DataSection)obj));
            }

            this._codeFixups.addFixup(offset);
         }
      }

      this.writeModuleOrdinal(out);
      out.writeByte(-1);
   }

   @Override
   public final void writeAbsoluteClassDef(StructuredOutputStream out) {
      if (!super._siblingFormat) {
         this.writeAbsoluteOrdinal(out);
      } else {
         int offset = out.getOffset();
         if (out.writingCode() && !super._implied_clsref_fixups) {
            this.writeModuleOrdinal(out);
         } else {
            out.writeByte(super._module.getOrdinal());
         }

         Object obj = out.getCookie();
         if (obj != null) {
            this.getClassRef((DataSection)obj);
            if (out.writingCode()) {
               if (this._codeFixups == null) {
                  this._codeFixups = new FixupTableEntry(2);
                  this._codeFixups.setRef(this.getFixupRef((DataSection)obj));
               }

               this._codeFixups.addFixup(offset);
            }

            out.writeByte(-1);
         } else if (out.writingCode() && !super._implied_clsref_fixups) {
            out.writeByte(-1);
         } else {
            super._classRef.writeOrdinal(out);
         }
      }
   }

   @Override
   public final void writeFixups(DataSection dataSection) {
      if (this._codeFixups != null) {
         dataSection.addClassDefCodeFixup(this._codeFixups);
      }

      super.writeFixups(dataSection);
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
      return new FieldDefForeign(this, name, typeList, isStatic);
   }

   @Override
   public final FieldDef makeFieldDef(DataSection dataSection, String name, boolean suppress, TypeList typeList, boolean isStatic) {
      DataBytes dataBytes = dataSection.getDataBytes();
      FieldDefForeign fieldDef = (FieldDefForeign)this.createFieldDef(dataBytes.getNullIdentifier(), typeList, isStatic);
      fieldDef.setActualName(name);
      this.addFieldDef(fieldDef, isStatic);
      return fieldDef;
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
      if (!(o instanceof ClassDefForeign)) {
         return false;
      }

      ClassDefForeign other = (ClassDefForeign)o;
      return this == other ? true : this._actualClassName.equals(other._actualClassName) && this._actualPackageName.equals(other._actualPackageName);
   }

   @Override
   public final int hashCode() {
      return this._actualClassName.hashCode() * 31 + this._actualPackageName.hashCode();
   }
}
