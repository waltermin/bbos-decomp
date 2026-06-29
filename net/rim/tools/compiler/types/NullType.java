package net.rim.tools.compiler.types;

import net.rim.tools.compiler.codfile.ClassDef;
import net.rim.tools.compiler.codfile.TypeItem;

public final class NullType extends ReferenceType {
   public NullType() {
      super("NullType");
   }

   @Override
   public final int getTypeId() {
      return 7;
   }

   @Override
   public final ClassDef getClassDef(TypeModule typeModule) {
      int ordinal = typeModule.getOrdinal();
      ClassDef classDef = this.getClassDef(ordinal, typeModule.getCount());
      if (classDef == null) {
         classDef = super._typeModule.getNullClassDef(typeModule);
         this.setClassDef(classDef, ordinal);
      }

      return classDef;
   }

   @Override
   final TypeItem makeTypeItem(TypeModule typeModule) {
      int ordinal = typeModule.getOrdinal();
      TypeItem typeItem = this.getTypeItem(ordinal, typeModule.getCount());
      if (typeItem == null) {
         typeItem = new TypeItem(this.getClassDef(typeModule));
         this.setTypeItem(typeItem, ordinal);
      }

      return typeItem;
   }
}
