package net.rim.tools.compiler.types;

import net.rim.tools.compiler.codfile.ClassDef;
import net.rim.tools.compiler.codfile.TypeItem;
import net.rim.tools.compiler.util.CompileException;

public final class ArrayType extends ReferenceType {
   private int _nesting;
   private Type _baseType;

   public ArrayType(Type baseType, int nesting) throws CompileException {
      super(((StringBuffer)(new Object(baseType._typeName))).append("[]").toString());
      this._nesting = nesting;
      this._baseType = baseType;
      if (this._nesting > 255) {
         throw new CompileException("Error!: Array nesting is too deep");
      }
   }

   @Override
   public final ArrayType getArrayType() {
      if (super._arrayType == null) {
         super._arrayType = new ArrayType(this, this._nesting + 1);
      }

      return super._arrayType;
   }

   @Override
   public final String getFullName() {
      Type type = this.getMostBaseType();
      if (type instanceof BaseType) {
         return super._typeName;
      }

      StringBuffer name = (StringBuffer)(new Object(type.getFullName()));

      for (int i = 0; i < this._nesting; i++) {
         name.append("[]");
      }

      return name.toString();
   }

   @Override
   public final int getTypeId() {
      return 8;
   }

   public final int getNesting() {
      return this._nesting;
   }

   public final Type getBaseType() {
      return this._baseType;
   }

   public final Type getMostBaseType() {
      Type elementType = this._baseType;

      while (elementType instanceof ArrayType) {
         ArrayType arrayType = (ArrayType)elementType;
         elementType = arrayType.getBaseType();
      }

      return elementType;
   }

   @Override
   public final ClassDef getClassDef(TypeModule typeModule) throws CompileException {
      throw new CompileException("cannot get class def for array type");
   }

   @Override
   final TypeItem makeTypeItem(TypeModule typeModule) {
      int ordinal = typeModule.getOrdinal();
      TypeItem typeItem = this.getTypeItem(ordinal, typeModule.getCount());
      if (typeItem == null) {
         Type mostBase = this.getMostBaseType();
         if (!(mostBase instanceof ReferenceType)) {
            typeItem = new TypeItem(this._nesting, mostBase.getTypeId());
         } else {
            ReferenceType refType = (ReferenceType)mostBase;
            typeItem = new TypeItem(this._nesting, refType.getClassDef(typeModule));
         }

         this.setTypeItem(typeItem, ordinal);
      }

      return typeItem;
   }
}
