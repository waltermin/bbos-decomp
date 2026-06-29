package net.rim.tools.compiler.types;

import net.rim.tools.compiler.codfile.ClassDef;
import net.rim.tools.compiler.codfile.TypeItem;
import net.rim.tools.compiler.codfile.TypeList;

public final class ClassUninitializedType extends ReferenceType {
   private ClassType _classType;
   private int _offset;
   private boolean _preverified;

   public ClassUninitializedType(ClassType classType, int offset) {
      super("<" + classType.getName() + ">");
      this._classType = classType;
      this._offset = offset;
      this._preverified = false;
   }

   public ClassUninitializedType(ClassType classType, int offset, boolean preverified) {
      super("<" + classType.getName() + ">");
      this._classType = classType;
      this._offset = offset;
      this._preverified = preverified;
   }

   @Override
   public final int getTypeId() {
      return 9;
   }

   public final void fixupOffset(int offset, int ordinal) {
      if (this._preverified && this._offset == offset) {
         this._offset = ordinal;
         this._preverified = false;
      }
   }

   public final boolean isPreverified() {
      return this._preverified;
   }

   @Override
   public final boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (!(o instanceof ClassUninitializedType)) {
         return false;
      }

      ClassUninitializedType other = (ClassUninitializedType)o;
      return this._offset != other._offset ? false : this._classType.equals(other._classType);
   }

   @Override
   public final int hashCode() {
      return this._offset * 31 + this._classType.hashCode();
   }

   @Override
   public final ClassDef getClassDef(TypeModule typeModule) {
      int ordinal = typeModule.getOrdinal();
      ClassDef classDef = this.getClassDef(ordinal, typeModule.getCount());
      if (classDef == null) {
         classDef = this._classType.getClassDef(typeModule);
         this.setClassDef(classDef, ordinal);
      }

      return classDef;
   }

   @Override
   final TypeItem makeTypeItem(TypeModule typeModule) {
      int ordinal = typeModule.getOrdinal();
      TypeItem typeItem = this.getTypeItem(ordinal, typeModule.getCount());
      if (typeItem == null) {
         typeItem = new TypeItem(this.getClassDef(typeModule), 9);
         this.setTypeItem(typeItem, ordinal);
      }

      return typeItem;
   }

   @Override
   final TypeList getTypeList(TypeModule typeModule) {
      int ordinal = typeModule.getOrdinal();
      TypeList typeList = this.getTypeList(ordinal, typeModule.getCount());
      if (typeList == null) {
         if (!this._classType.isDefined()) {
            typeList = new TypeList(-1);
         } else {
            typeList = new TypeList(this.makeTypeItem(typeModule));
         }

         this.setTypeList(typeList, ordinal);
      }

      return typeList;
   }
}
