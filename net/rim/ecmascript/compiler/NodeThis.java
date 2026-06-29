package net.rim.ecmascript.compiler;

class NodeThis extends Node {
   @Override
   void generate(Function f) {
      f.addCode(106);
   }
}
