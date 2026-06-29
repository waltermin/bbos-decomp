package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public class ClassDef extends CodfileItem {
   protected boolean _siblingFormat;
   protected boolean _implied_clsref_fixups;
   protected Module _module;
   protected Identifier _packageName;
   protected Identifier _className;
   protected CodfileArray _fieldDefs;
   protected CodfileArray _staticFieldDefs;
   protected ClassRef _classRef;
   protected Routine _nullRoutine;

   private void init(DataSection dataSection) {
      this._nullRoutine = new RoutineNull(this, dataSection);
      this._siblingFormat = true;
      this._implied_clsref_fixups = dataSection.getImpliedClsrefFixups();
   }

   protected ClassDef(DataSection dataSection, String packageName, String className) {
      this.init(dataSection);
      DataBytes dataBytes = dataSection.getDataBytes();
      this._packageName = dataBytes.getIdentifier(packageName);
      this._className = dataBytes.getIdentifier(className);
   }

   public ClassDef(DataSection dataSection, int offset) {
      super(offset);
      this.init(dataSection);
   }

   public void makeSymbolic(DataSection dataSection) {
   }

   protected CodfileOffset getFixupRef(DataSection dataSection) {
      return null;
   }

   public void writeModuleOrdinal(StructuredOutputStream out) {
      this._module.writeOrdinal(out);
   }

   @Override
   public void writeOrdinal(StructuredOutputStream _1) {
      throw null;
   }

   public void writeRelativeOrdinal(StructuredOutputStream _1) {
      throw null;
   }

   public void writeAbsoluteClassDef(StructuredOutputStream _1) {
      throw null;
   }

   public void writeFixups(DataSection dataSection) {
      int num = this.getNumFieldDefs(false);

      for (int i = 0; i < num; i++) {
         FieldDef fieldDef = this.getFieldDef(i, false);
         fieldDef.writeFixups(dataSection);
      }

      num = this.getNumFieldDefs(true);

      for (int i = 0; i < num; i++) {
         FieldDef fieldDef = this.getFieldDef(i, true);
         fieldDef.writeFixups(dataSection);
      }
   }

   public void setModule(Module module) {
      this._module = module;
   }

   public Module getModule() {
      return this._module;
   }

   public void setPackageName(Identifier packageName) {
      this._packageName = packageName;
   }

   public Identifier getPackageName() {
      return this._packageName;
   }

   public String getPackageNameString() {
      return this._packageName.getString();
   }

   public void setClassName(Identifier className) {
      this._className = className;
   }

   public Identifier getClassName() {
      return this._className;
   }

   public String getClassNameString() {
      return this._className.getString();
   }

   public int getLibOff() {
      return 1;
   }

   public ClassRef getClassRef(DataSection dataSection) {
      if (this._classRef == null) {
         this._classRef = dataSection.makeClassRef(this);
      }

      return this._classRef;
   }

   public Routine createRoutine(Identifier _1, TypeList _2, TypeList _3) {
      throw null;
   }

   public Routine makeRoutine(DataSection _1, String _2, boolean _3, TypeList _4, TypeList _5) {
      throw null;
   }

   public Routine getNullRoutine() {
      return this._nullRoutine;
   }

   public void allocateFieldDefs(int num, boolean isStatic) {
      if (isStatic) {
         this._staticFieldDefs = new CodfileArray(num);
      } else {
         this._fieldDefs = new CodfileArray(num);
      }
   }

   public void addFieldDef(FieldDef fieldDef, boolean isStatic) {
      CodfileArray a = null;
      if (isStatic) {
         if (this._staticFieldDefs == null) {
            this.allocateFieldDefs(1, isStatic);
         }

         a = this._staticFieldDefs;
      } else {
         if (this._fieldDefs == null) {
            this.allocateFieldDefs(1, isStatic);
         }

         a = this._fieldDefs;
      }

      int idx = a.size();
      fieldDef.setOrdinal(idx);
      a.addElement(fieldDef);
   }

   public int getNumFieldDefs(boolean isStatic) {
      CodfileArray a = null;
      if (isStatic) {
         a = this._staticFieldDefs;
      } else {
         a = this._fieldDefs;
      }

      return a != null ? a.size() : 0;
   }

   public FieldDef getFieldDef(int index, boolean isStatic) {
      CodfileArray a = null;
      if (isStatic) {
         a = this._staticFieldDefs;
      } else {
         a = this._fieldDefs;
      }

      return (FieldDef)a.elementAt(index);
   }

   public FieldDef createFieldDef(Identifier _1, TypeList _2, boolean _3) {
      throw null;
   }

   public FieldDef makeFieldDef(DataSection _1, String _2, boolean _3, TypeList _4, boolean _5) {
      throw null;
   }

   public String getFullName() {
      String className = this._className.getString();
      int packageLen = this._packageName.length();
      if (packageLen > 0) {
         String packageName = this._packageName.getString();
         StringBuffer buffer = (StringBuffer)(new Object(packageLen + 1 + className.length()));
         buffer.append(packageName);
         buffer.append(".");
         buffer.append(className);
         return buffer.toString();
      } else {
         return className;
      }
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof ClassDef)) {
         return false;
      }

      ClassDef other = (ClassDef)o;
      return this == other
         ? true
         : this.getClassNameString().equals(other.getClassNameString()) && this.getPackageNameString().equals(other.getPackageNameString());
   }

   @Override
   public int hashCode() {
      return this._className.hashCode() * 31 + this._packageName.hashCode();
   }
}
