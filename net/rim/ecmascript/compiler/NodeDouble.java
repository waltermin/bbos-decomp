package net.rim.ecmascript.compiler;

import net.rim.ecmascript.runtime.Value;

class NodeDouble extends Node {
   protected int _value;
   protected double _dvalue;

   NodeDouble(Function f, double value) {
      this._dvalue = value;
      this._value = f.addDouble((Double)(new Object(value)));
   }

   @Override
   void generate(Function f) {
      f.addCode(96, this._value);
   }

   void negate() {
      this._value = -this._value;
   }

   @Override
   long getFoldableValue() {
      return Value.makeDoubleValue(this._dvalue);
   }
}
