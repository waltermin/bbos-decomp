package com.fourthpass.wmls;

final class FunctionName {
   private int _index;
   private String _name;

   FunctionName(WMLInputStream stream) {
      this._index = stream.readUInt8();
      byte[] bytes = new byte[stream.readUInt8()];
      stream.readBytes(bytes);
      this._name = (String)(new Object(bytes));
   }

   final int getIndex() {
      return this._index;
   }

   @Override
   public final String toString() {
      return this._name;
   }
}
