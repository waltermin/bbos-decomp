package net.rim.ecmascript.compiler;

import net.rim.ecmascript.runtime.Value;

class NodeString extends Node {
   protected String _value;

   NodeString(String value) {
      this._value = value;
   }

   @Override
   void generate(Function f) {
      f.addCode(104, f.addString(this._value));
   }

   @Override
   long getFoldableValue() {
      return Value.makeStringValue(this._value);
   }
}
