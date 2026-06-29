package net.rim.tools.compiler.types;

import java.util.Vector;
import net.rim.tools.compiler.Compiler;
import net.rim.tools.compiler.classfile.AttributeStackMap;
import net.rim.tools.compiler.classfile.AttributeStackMapType;
import net.rim.tools.compiler.classfile.ConstantPoolClass;
import net.rim.tools.compiler.classfile.TypeDescriptor;
import net.rim.tools.compiler.codfile.TypeItem;
import net.rim.tools.compiler.codfile.TypeList;
import net.rim.tools.compiler.util.CompileException;
import net.rim.tools.compiler.vm.Constants;

public class Type implements Constants {
   String _typeName;
   ArrayType _arrayType;
   private TypeList[] _typeLists;
   public static final char DESC_BYTE = 'B';
   public static final char DESC_CHAR = 'C';
   public static final char DESC_DOUBLE = 'D';
   public static final char DESC_FLOAT = 'F';
   public static final char DESC_INT = 'I';
   public static final char DESC_LONG = 'J';
   public static final char DESC_SHORT = 'S';
   public static final char DESC_BOOLEAN = 'Z';
   public static final char DESC_ARRAY = '[';
   public static final char DESC_OBJECT = 'L';
   public static final char DESC_OBJECT_SUFFIX = ';';
   public static final char DESC_VOID = 'V';
   public static final char DESC_OPEN_PAREN = '(';
   public static final char DESC_CLOSE_PAREN = ')';
   private static Vector _items = (Vector)(new Object());

   public Type(String typeName) {
      this._typeName = typeName;
   }

   public final String getName() {
      return this._typeName;
   }

   public String getFullName() {
      return this._typeName;
   }

   public final int getLocalCount() {
      return (this.getSize() + 3) / 4;
   }

   public final boolean isTwoWord() {
      return this.getSize() == 8;
   }

   public ArrayType getArrayType() {
      if (this._arrayType == null) {
         this._arrayType = new ArrayType(this, 1);
      }

      return this._arrayType;
   }

   public int getSize() {
      throw null;
   }

   public int getTypeId() {
      throw null;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (!(o instanceof Type)) {
         return false;
      }

      Type other = (Type)o;
      return this.getFullName().equals(other.getFullName());
   }

   @Override
   public int hashCode() {
      return this.getFullName().hashCode();
   }

   TypeItem makeTypeItem(TypeModule _1) {
      throw null;
   }

   final TypeList getTypeList(int ordinal, int count) {
      if (this._typeLists == null) {
         this._typeLists = new TypeList[count];
      }

      return this._typeLists[ordinal];
   }

   final void setTypeList(TypeList typeList, int ordinal) {
      this._typeLists[ordinal] = typeList;
   }

   TypeList getTypeList(TypeModule typeModule) {
      int ordinal = typeModule.getOrdinal();
      TypeList typeList = this.getTypeList(ordinal, typeModule.getCount());
      if (typeList == null) {
         typeList = new TypeList(this.makeTypeItem(typeModule));
         this.setTypeList(typeList, ordinal);
      }

      return typeList;
   }

   public static final TypeList getTypeList(TypeModule typeModule, Type type) {
      return type == null ? typeModule.getDataSection().getTypeLists().getEmptyTypeList() : type.getTypeList(typeModule);
   }

   public static final TypeList getTypeList(TypeModule typeModule, Type thisType, NameAndType[] types) {
      TypeList typeList = null;
      synchronized (_items) {
         _items.setSize(0);
         if (thisType != null) {
            _items.addElement(thisType.makeTypeItem(typeModule));
         }

         if (types != null) {
            int num = types.length;

            for (int i = 0; i < num; i++) {
               Type type = types[i].getType();
               TypeItem typeItem = type.makeTypeItem(typeModule);
               _items.addElement(typeItem);
               if (type.isTwoWord() && i < num - 1) {
                  type = types[i + 1].getType();
                  if (type.getTypeId() == 10) {
                     i++;
                  }
               }
            }
         }

         int num = _items.size();

         for (int i = num - 1; i >= 0; i--) {
            TypeItem typeItem = (TypeItem)_items.elementAt(i);
            if (typeItem.getId() != 10) {
               break;
            }

            _items.setSize(i);
         }

         typeList = new TypeList(_items);
         _items.setSize(0);
         return typeList;
      }
   }

   public static final TypeList getTypeList(TypeModule typeModule, Type thisType, Type[] types, int numTypes, boolean trimVoid) {
      TypeList typeList = null;
      synchronized (_items) {
         _items.setSize(0);
         if (thisType != null) {
            _items.addElement(thisType.makeTypeItem(typeModule));
         }

         for (int i = 0; i < numTypes; i++) {
            Type type = types[i];
            TypeItem typeItem = type.makeTypeItem(typeModule);
            _items.addElement(typeItem);
            if (type.isTwoWord() && i < numTypes - 1) {
               type = types[i + 1];
               if (type.getTypeId() == 10) {
                  i++;
               }
            }
         }

         if (trimVoid) {
            int num = _items.size();

            for (int i = num - 1; i >= 0; i--) {
               TypeItem typeItem = (TypeItem)_items.elementAt(i);
               if (typeItem.getId() != 10) {
                  break;
               }

               _items.setSize(i);
            }
         }

         typeList = new TypeList(_items);
         _items.setSize(0);
         return typeList;
      }
   }

   public static final Type translateType(Compiler compiler, TypeDescriptor descriptor) throws CompileException {
      Type type = null;
      int arrayDepth = 0;

      while (descriptor.matches('[')) {
         arrayDepth++;
      }

      char ch = descriptor.getNextChar();
      switch (ch) {
         case 'B':
            type = compiler.getByteType();
            break;
         case 'C':
            type = compiler.getCharType();
            break;
         case 'D':
            type = compiler.getDoubleType();
            break;
         case 'F':
            type = compiler.getFloatType();
            break;
         case 'I':
            type = compiler.getIntType();
            break;
         case 'J':
            type = compiler.getLongType();
            break;
         case 'L':
            type = compiler.findClassType(descriptor.getClassName());
            break;
         case 'S':
            type = compiler.getShortType();
         case 'V':
            break;
         case 'Z':
            type = compiler.getBooleanType();
            break;
         default:
            throw new CompileException(
               ((StringBuffer)(new Object("bad TypeDescriptor parse: '"))).append(ch).append("' in ").append(descriptor.getString()).toString()
            );
      }

      if (type != null) {
         while (arrayDepth > 0) {
            type = type.getArrayType();
            arrayDepth--;
         }
      }

      return type;
   }

   public static final void translateTypes(Compiler compiler, TypeDescriptor descriptor, Vector types) {
      if (descriptor.matches('(')) {
         while (!descriptor.matches(')')) {
            types.addElement(translateType(compiler, descriptor));
         }
      }
   }

   public final String encodeType() throws CompileException {
      Type type = this;
      StringBuffer buf = (StringBuffer)(new Object());

      while (true) {
         int typeId = type.getTypeId();
         switch (typeId) {
            case 0:
               throw new CompileException(((StringBuffer)(new Object("unexpected type id: 0x"))).append(Integer.toHexString(typeId)).toString());
            case 1:
            default:
               buf.append('Z');
               break;
            case 2:
               buf.append('B');
               break;
            case 3:
               buf.append('C');
               break;
            case 4:
               buf.append('S');
               break;
            case 5:
               buf.append('I');
               break;
            case 6:
               buf.append('J');
               break;
            case 7:
            case 9:
               buf.append('L');
               buf.append(this.getFullName());
               buf.append(';');
               break;
            case 8:
               buf.append('[');
               type = ((ArrayType)type).getBaseType();
               continue;
            case 10:
               buf.append('V');
               break;
            case 11:
               buf.append('F');
               break;
            case 12:
               buf.append('D');
         }

         return buf.toString();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final Type translateStackMapType(Compiler compiler, ClassType classType, AttributeStackMapType smt, byte[] bytes) throws CompileException {
      Type type = null;
      String typeName = null;
      switch (smt.getType()) {
         case -1:
            break;
         case 0:
         default:
            type = compiler.getVoidType();
            break;
         case 1:
            type = compiler.getIntType();
            break;
         case 2:
            type = compiler.getFloatType();
            break;
         case 3:
            type = compiler.getDoubleType();
            break;
         case 4:
            type = compiler.getLongType();
            break;
         case 5:
            type = compiler.getNullType();
            break;
         case 6:
            classType.setReachable(compiler, true);
            type = new ClassUninitializedType(classType, 0);
            break;
         case 7:
            typeName = smt.getTypeName();
            if (typeName.charAt(0) == '[') {
               type = translateType(compiler, new TypeDescriptor(typeName));
            } else {
               classType = compiler.findClassType(typeName.replace('/', '.'));
               classType.setReachable(compiler, true);
               type = classType;
            }
            break;
         case 8:
            int offset = smt.getNewOffset();
            int index = bytes[offset + 1] << 8 | bytes[offset + 2] & 255;

            try {
               ConstantPoolClass cpc = smt.getNewClass(index);
               typeName = cpc.getName();
            } catch (Throwable var10) {
               throw new CompileException(ioe.getMessage());
            }

            classType = compiler.findClassType(typeName);
            classType.setReachable(compiler, true);
            type = new ClassUninitializedType(classType, offset, true);
      }

      return type;
   }

   public static final void translateStackMap(Compiler compiler, ClassType classType, int maxLocals, AttributeStackMap sme, byte[] bytes) {
      Type voidType = compiler.getVoidType();
      AttributeStackMapType[] locals = sme.getLocalTypes();
      int nLocals = locals == null ? 0 : locals.length;
      AttributeStackMapType[] stacks = sme.getStackTypes();
      int nStacks = stacks == null ? 0 : stacks.length;
      int num = 0;

      for (int i = 0; i < nLocals; i++) {
         num++;
         int attr = locals[i].getType();
         if (attr == 4 || attr == 3) {
            num++;
         }
      }

      if (num < maxLocals) {
         num = maxLocals;
      }

      for (int var15 = 0; var15 < nStacks; var15++) {
         num++;
         int attr = stacks[var15].getType();
         if (attr == 4 || attr == 3) {
            num++;
         }
      }

      Type[] result = new Type[num];
      int count = 0;

      for (int var16 = 0; var16 < nLocals; var16++) {
         Type type = translateStackMapType(compiler, classType, locals[var16], bytes);
         result[count++] = type;
         if (type != null && type.isTwoWord()) {
            result[count++] = voidType;
         }
      }

      while (count < maxLocals) {
         result[count++] = voidType;
      }

      for (int var17 = 0; var17 < nStacks; var17++) {
         Type type = translateStackMapType(compiler, classType, stacks[var17], bytes);
         result[count++] = type;
         if (type != null && type.isTwoWord()) {
            result[count++] = voidType;
         }
      }

      sme.setTypes(result);
   }
}
