package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;

public final class AttributeCode extends Attribute {
   private int _maxStack;
   private int _maxLocals;
   private byte[] _code;
   private ClassfileExceptionHandler[] _handlers;
   private AttributeList _attributes;

   public AttributeCode(StructuredInputStream in, ConstantPool constantPool, int iName, String name) {
      super(in, iName, name);
      int offset = in.getOffset();
      this._maxStack = in.readUnsignedShort();
      this._maxLocals = in.readUnsignedShort();
      int codeLen = in.readInt();
      if (codeLen > 0) {
         this._code = new byte[codeLen];
         in.read(this._code);
      }

      int num = in.readUnsignedShort();
      if (num > 0) {
         this._handlers = new ClassfileExceptionHandler[num];

         for (int i = 0; i < num; i++) {
            this._handlers[i] = new ClassfileExceptionHandler(in, constantPool);
         }
      }

      this._attributes = new AttributeList(in, constantPool, 4, false);
      if (offset + super._length != in.getOffset()) {
         throw new Object("incorrect code attribute length");
      }
   }

   public final byte[] getCode() {
      return this._code;
   }

   public final int getMaxStack() {
      return this._maxStack;
   }

   public final int getMaxLocals() {
      return this._maxLocals;
   }

   public final int getNumHandlers() {
      return this._handlers == null ? 0 : this._handlers.length;
   }

   public final ClassfileExceptionHandler[] getHandlers() {
      return this._handlers;
   }

   public final boolean hasAttribute(String attributeName) {
      return this.getAttribute(attributeName) != null;
   }

   public final Attribute getAttribute(String attributeName) {
      return this._attributes.getAttribute(attributeName);
   }
}
