package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public class Member extends CodfileItem {
   protected ClassDef _classDef;
   protected Identifier _name;
   protected TypeList _typeList;

   public Member() {
   }

   public Member(ClassDef classDef, Identifier name, TypeList typeList) {
      this._classDef = classDef;
      this._name = name;
      this._typeList = typeList;
   }

   public Member(ClassDef classDef, int offset) {
      super(offset);
      this._classDef = classDef;
   }

   public void writeStaticOffset(StructuredOutputStream _1, ClassDef _2) {
      throw null;
   }

   public void writeMemberAddress(StructuredOutputStream _1, boolean _2) {
      throw null;
   }

   public void writeFixups(DataSection _1) {
      throw null;
   }

   public ClassDef getClassDef() {
      return this._classDef;
   }

   public Identifier getName() {
      return this._name;
   }

   public TypeList getTypeList() {
      return this._typeList;
   }
}
