package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;

public final class ConstantPoolInteger extends ConstantPoolEntry {
   private int _value;
   private boolean _isFloat;

   public ConstantPoolInteger(int tag, StructuredInputStream in, boolean isFloat) {
      super(tag);
      this._value = in.readInt();
      this._isFloat = isFloat;
   }

   public ConstantPoolInteger(int value) {
      super(-1);
      this._value = value;
   }

   public final int getValue() {
      return this._value;
   }

   public final boolean isFloat() {
      return this._isFloat;
   }
}
