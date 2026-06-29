package net.rim.ecmascript.compiler;

import net.rim.ecmascript.runtime.Value;

class NodeTrue extends Node {
   @Override
   void generate(Function f) {
      f.addCode(109);
   }

   @Override
   long getFoldableValue() {
      return Value.TRUE;
   }
}
