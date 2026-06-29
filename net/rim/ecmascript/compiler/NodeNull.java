package net.rim.ecmascript.compiler;

class NodeNull extends Node {
   @Override
   void generate(Function f) {
      f.addCode(102);
   }
}
