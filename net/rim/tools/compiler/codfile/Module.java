package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;
import net.rim.tools.compiler.vm.Constants;

public class Module extends CodfileItem implements Constants {
   protected Literal _name;
   protected Literal _version;
   protected CodfileVector _classDefs;
   protected CodfileVector _routines;
   protected ClassDef _nullClassDef;
   protected Routine _nullRoutine;

   protected Module(DataSection dataSection) {
      super._ordinal = 255;
      Identifier empty = dataSection.getDataBytes().getNullIdentifier();
      this._nullClassDef = new ClassDefNull(dataSection, empty, empty);
      this._nullClassDef.setModule(this);
      this._nullRoutine = new RoutineNull(this._nullClassDef, dataSection);
   }

   private void init(DataSection dataSection) {
      this._nullClassDef = dataSection.getNullClassDef();
      this._nullRoutine = new RoutineNull(this._nullClassDef, dataSection);
   }

   protected Module(DataSection dataSection, String name, String version, CodfileVector classDefs, CodfileVector routines) {
      this._classDefs = classDefs;
      this._routines = routines;
      DataBytes dataBytes = dataSection.getDataBytes();
      this._name = dataBytes.getLiteral(name, false, false);
      this._version = dataBytes.getLiteral(version, false, false);
      this.init(dataSection);
   }

   @Override
   public void write(StructuredOutputStream out) {
      this.setOffset(out);
      this._name.writeOffset(out);
      this.setExtent(out);
   }

   public void writeVersion(StructuredOutputStream out) {
      this._version.writeOffset(out);
   }

   public void writeFixups(DataSection dataSection) {
      int num = this.getNumClassDefs();

      for (int i = 0; i < num; i++) {
         this.getClassDef(i).writeFixups(dataSection);
      }

      num = this.getNumRoutines();

      for (int i = 0; i < num; i++) {
         this.getRoutine(i).writeFixups(dataSection);
      }
   }

   public void addClassDef(ClassDef classDef) {
      classDef.setModule(this);
      classDef.setOrdinal(this._classDefs.size());
      this._classDefs.addElement(classDef);
   }

   public int getNumClassDefs() {
      return this._classDefs.size();
   }

   public ClassDef getClassDef(int ordinal) {
      return ordinal >= this._classDefs.size() ? this._nullClassDef : (ClassDef)this._classDefs.elementAt(ordinal);
   }

   public ClassDef getNullClassDef() {
      return this._nullClassDef;
   }

   public ClassDef makeClassDef(DataSection _1, String _2, String _3) {
      throw null;
   }

   public void addRoutine(Routine routine) {
      this._routines.addItemOffset(routine);
   }

   public int getNumRoutines() {
      return this._routines.size();
   }

   public Routine getRoutine(int ordinal) {
      return (Routine)this._routines.elementAt(ordinal);
   }
}
