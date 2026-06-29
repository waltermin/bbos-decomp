package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.io.StructuredInputStream;

public final class ConstantPoolLong extends ConstantPoolEntry {
   private long _value;
   private boolean _isDouble;

   public ConstantPoolLong(int tag, StructuredInputStream in, boolean isDouble) {
      super(tag);
      this._value = in.readLong();
      this._isDouble = isDouble;
   }

   public ConstantPoolLong(long value) {
      super(-1);
      this._value = value;
   }

   public final long getValue() {
      return this._value;
   }

   public final boolean isDouble() {
      return this._isDouble;
   }
}
