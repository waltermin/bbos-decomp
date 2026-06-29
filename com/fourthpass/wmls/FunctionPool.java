package com.fourthpass.wmls;

final class FunctionPool {
   private NameTable _nameTable;
   private Function[] _functions;

   FunctionPool(WMLInputStream stream) {
      int count = stream.readUInt8();
      this._nameTable = new NameTable(stream);
      this._functions = new Function[count];

      for (int i = 0; i < count; i++) {
         this._functions[i] = new Function(stream);
      }
   }

   final Function getFunction(int i) {
      return this._functions[i];
   }

   final int getIndex(String functionName) {
      return this._nameTable.getIndex(functionName);
   }
}
