package net.rim.tools.compiler.types;

import net.rim.tools.compiler.codfile.TypeItem;

public final class BaseType extends Type {
   private int _size;
   private int _typeId;

   public BaseType(String name, int size, int typeId) {
      super(name);
      this._size = size;
      this._typeId = typeId;
   }

   @Override
   public final int getSize() {
      return this._size;
   }

   @Override
   public final int getTypeId() {
      return this._typeId;
   }

   @Override
   final TypeItem makeTypeItem(TypeModule typeModule) {
      return TypeItem.makeTypeItem(this.getTypeId());
   }
}
