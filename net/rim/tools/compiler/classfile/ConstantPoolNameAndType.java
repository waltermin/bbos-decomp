package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;

public final class ConstantPoolNameAndType extends ConstantPoolTwoIndex {
   private ConstantPoolUTF8 _name;
   private ConstantPoolUTF8 _type;

   public ConstantPoolNameAndType(int tag, StructuredInputStream in) {
      super(tag, in);
   }

   @Override
   public final void resolve(ConstantPool constantPool) {
      if (this._name == null) {
         this._name = (ConstantPoolUTF8)constantPool.getEntry(super._index1);
         this._type = (ConstantPoolUTF8)constantPool.getEntry(super._index2);
      }
   }

   public final String getName() {
      return this._name.getString();
   }

   public final String getType() {
      return this._type.getString();
   }

   public final void checkFieldType() {
      String type = this._type.getString();
      if (type.charAt(0) == '(') {
         throw new Object(((StringBuffer)(new Object("bad field type: "))).append(type).toString());
      }
   }

   public final void checkMethodType() {
      String type = this._type.getString();
      if (type.charAt(0) != '(') {
         throw new Object(((StringBuffer)(new Object("bad method type: "))).append(type).toString());
      }

      if (this._name.getString().equals("<init>") && !type.endsWith(")V")) {
         throw new Object(((StringBuffer)(new Object("bad <init> method return type: "))).append(type).toString());
      }
   }

   public final void checkMethodName() {
      String name = this._name.getString();
      if (name.charAt(0) == '<' && !name.equals("<init>")) {
         throw new Object(((StringBuffer)(new Object("bad method name: "))).append(name).toString());
      }
   }
}
