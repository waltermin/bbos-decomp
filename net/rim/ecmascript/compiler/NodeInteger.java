package net.rim.ecmascript.compiler;

import net.rim.ecmascript.runtime.Value;

class NodeInteger extends Node {
   protected int _value;

   NodeInteger(int value) {
      this._value = value;
   }

   @Override
   void generate(Function f) {
      f.pushIntValue(this._value);
   }

   int getValue() {
      return this._value;
   }

   @Override
   long getFoldableValue() {
      return Value.makeIntegerValue(this._value);
   }
}
