package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;

public final class AttributeStackMapType {
   private int _type;
   private String _typeName;
   private int _newOffset;
   private ConstantPool _constantPool;
   private ConstantPoolClass _constantPoolClass;
   public static final int ITEM_Bogus;
   public static final int ITEM_Integer;
   public static final int ITEM_Float;
   public static final int ITEM_Double;
   public static final int ITEM_Long;
   public static final int ITEM_Null;
   public static final int ITEM_InitObject;
   public static final int ITEM_Object;
   public static final int ITEM_NewObject;
   private static final int CACHE_SIZE;
   private static AttributeStackMapType[] _cache = new AttributeStackMapType[7];

   private AttributeStackMapType(int type) {
      this._type = type;
   }

   private AttributeStackMapType(int type, int value, ConstantPool constantPool) {
      this(type);
      if (type == 7) {
         this._typeName = constantPool.getClassName(value);
      } else {
         if (type == 8) {
            this._newOffset = value;
            this._constantPool = constantPool;
         }
      }
   }

   public static final AttributeStackMapType read(StructuredInputStream in, ConstantPool constantPool) {
      AttributeStackMapType result = null;
      int type = in.readUnsignedByte();
      if (type != 7 && type != 8) {
         synchronized (_cache) {
            result = _cache[type];
            if (result == null) {
               result = _cache[type] = new AttributeStackMapType(type);
            }

            return result;
         }
      } else {
         return new AttributeStackMapType(type, in.readUnsignedShort(), constantPool);
      }
   }

   public final int getType() {
      return this._type;
   }

   public final int getSize() {
      int size = 1;
      switch (this._type) {
         case 3:
         case 4:
         default:
            size++;
         case 2:
            return size;
      }
   }

   public final String getTypeName() {
      return this._typeName;
   }

   public final int getNewOffset() {
      return this._newOffset;
   }

   public final ConstantPoolClass getNewClass(int index) {
      if (this._constantPoolClass == null) {
         this._constantPoolClass = (ConstantPoolClass)this._constantPool.getEntry(index);
         this._constantPool = null;
      }

      return this._constantPoolClass;
   }
}
