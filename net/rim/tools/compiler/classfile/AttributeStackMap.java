package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;
import net.rim.tools.compiler.types.Type;

public final class AttributeStackMap {
   private int _codeOffset;
   private AttributeStackMapType[] _localTypes;
   private AttributeStackMapType[] _stackTypes;
   private Type[] _types;

   public AttributeStackMap(StructuredInputStream in, ConstantPool constantPool) {
      this._codeOffset = in.readUnsignedShort();
      int num = in.readUnsignedShort();
      if (num > 0) {
         this._localTypes = new AttributeStackMapType[num];

         for (int i = 0; i < num; i++) {
            this._localTypes[i] = AttributeStackMapType.read(in, constantPool);
         }
      }

      num = in.readUnsignedShort();
      if (num > 0) {
         this._stackTypes = new AttributeStackMapType[num];

         for (int i = 0; i < num; i++) {
            this._stackTypes[i] = AttributeStackMapType.read(in, constantPool);
         }
      }
   }

   public final int getCodeOffset() {
      return this._codeOffset;
   }

   public final AttributeStackMapType[] getLocalTypes() {
      return this._localTypes;
   }

   public final int getLocalSize() {
      int size = 0;
      AttributeStackMapType[] items = this._localTypes;
      int num = items == null ? 0 : items.length;

      for (int i = 0; i < num; i++) {
         size += items[i].getSize();
      }

      return size;
   }

   public final AttributeStackMapType[] getStackTypes() {
      return this._stackTypes;
   }

   public final int getStackSize() {
      int size = 0;
      AttributeStackMapType[] items = this._stackTypes;
      int num = items == null ? 0 : items.length;

      for (int i = 0; i < num; i++) {
         size += items[i].getSize();
      }

      return size;
   }

   public final void setTypes(Type[] types) {
      this._localTypes = null;
      this._stackTypes = null;
      this._types = types;
   }

   public final Type[] getTypes() {
      return this._types;
   }
}
