package net.rim.tools.compiler.types;

import net.rim.tools.compiler.codfile.ClassDef;
import net.rim.tools.compiler.codfile.TypeItem;

public class ReferenceType extends Type {
   TypeModule _typeModule;
   private ClassDef[] _classDefs;
   private TypeItem[] _typeItems;

   public ReferenceType(String typeName) {
      super(typeName);
   }

   @Override
   public final int getSize() {
      return 4;
   }

   public void setTypeModule(TypeModule typeModule) {
      this._typeModule = typeModule;
   }

   public final TypeModule getTypeModule() {
      return this._typeModule;
   }

   final TypeItem getTypeItem(int ordinal, int count) {
      if (this._typeItems == null) {
         this._typeItems = new TypeItem[count];
      }

      return this._typeItems[ordinal];
   }

   final void setTypeItem(TypeItem typeItem, int ordinal) {
      this._typeItems[ordinal] = typeItem;
   }

   final ClassDef getClassDef(int ordinal, int count) {
      if (this._classDefs == null) {
         this._classDefs = new ClassDef[count];
      }

      return this._classDefs[ordinal];
   }

   final void setClassDef(ClassDef classDef, int ordinal) {
      this._classDefs[ordinal] = classDef;
   }

   public ClassDef getClassDef(TypeModule _1) {
      throw null;
   }
}
