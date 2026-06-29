package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public class MemberRef extends CodfileItem {
   protected ClassRef _classRef;
   protected Identifier _name;
   protected TypeList _typeList;
   protected ClassDef _classDef;
   protected Member _member;

   public MemberRef(ClassDef classDef, ClassRef classRef, Identifier name, TypeList typeList) {
      this._classDef = classDef;
      this._classRef = classRef;
      this._name = name;
      this._typeList = typeList;
   }

   protected MemberRef(ClassDef classDef, Member member, Identifier name, TypeList typeList) {
      this._classDef = classDef;
      this._member = member;
      this._name = name;
      this._typeList = typeList;
   }

   protected MemberRef() {
   }

   @Override
   public void write(StructuredOutputStream out) {
      this.setOffset(out);
      this._classRef.writeOffset(out);
      this._name.writeOffset(out);
      this._typeList.writeOffset(out);
      this.setExtent(out);
   }

   public ClassDef getClassDef() {
      return this._classDef;
   }

   public Identifier getName() {
      return this._name;
   }

   public boolean compareSignatures(MemberRef ref, boolean skipthis) {
      if (ref._name == this._name) {
         if (ref._typeList == this._typeList) {
            return true;
         }

         if (skipthis) {
            return this._typeList.equalsSkip(ref._typeList);
         }
      }

      return false;
   }
}
