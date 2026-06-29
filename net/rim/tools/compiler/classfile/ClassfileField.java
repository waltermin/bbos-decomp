package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;

public final class ClassfileField {
   private int _accessFlags;
   private int _iName;
   private String _name;
   private int _iDescriptor;
   private TypeDescriptor _descriptor;
   private AttributeList _attributes;

   public ClassfileField(StructuredInputStream in, ConstantPool constantPool, boolean shallow) {
      this._accessFlags = in.readUnsignedShort();
      this._iName = in.readUnsignedShort();
      this._name = constantPool.getString(this._iName);
      this._iDescriptor = in.readUnsignedShort();
      this._descriptor = new TypeDescriptor(constantPool.getString(this._iDescriptor));
      this._attributes = new AttributeList(in, constantPool, 2, shallow);
   }

   public final int getAccessFlags() {
      return this._accessFlags;
   }

   public final String getName() {
      return this._name;
   }

   public final TypeDescriptor getDescriptor() {
      return this._descriptor;
   }

   public final boolean hasAttribute(String attributeName) {
      return this.getAttribute(attributeName) != null;
   }

   public final Attribute getAttribute(String attributeName) {
      return this._attributes.getAttribute(attributeName);
   }
}
