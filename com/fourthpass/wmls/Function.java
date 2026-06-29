package com.fourthpass.wmls;

final class Function {
   private int _numArgs;
   private int _numLocals;
   private int _size;
   private byte[] _instructions;

   Function(WMLInputStream stream) {
      this._numArgs = stream.readUInt8();
      this._numLocals = stream.readUInt8();
      this._size = stream.readMBInt();
      this._instructions = new byte[this._size];
      stream.readBytes(this._instructions);
   }

   final byte[] getInstructions() {
      return this._instructions;
   }

   final int getNumArgs() {
      return this._numArgs;
   }

   final int getNumLocals() {
      return this._numLocals;
   }
}
