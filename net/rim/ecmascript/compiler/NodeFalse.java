package net.rim.ecmascript.compiler;

import net.rim.ecmascript.runtime.Value;

class NodeFalse extends Node {
   @Override
   void generate(Function f) {
      f.addCode(95);
   }

   @Override
   long getFoldableValue() {
      return Value.FALSE;
   }
}
