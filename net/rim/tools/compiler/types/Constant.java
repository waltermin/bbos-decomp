package net.rim.tools.compiler.types;

public final class Constant {
   private long _value;
   private String _string;

   public Constant(long value) {
      this._value = value;
   }

   public Constant(String string) {
      this._string = string;
   }

   public final long getValue() {
      return this._value;
   }

   public final String getString() {
      return this._string;
   }

   public final boolean isString() {
      return this._string != null;
   }
}
