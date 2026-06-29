package net.rim.ecmascript.compiler;

class NodeVoid extends Node {
   protected Node _lhs;

   NodeVoid(Node lhs) {
      this._lhs = lhs;
   }

   @Override
   void generateAndDiscard(Function f) {
      this._lhs.generateAndDiscard(f);
   }

   @Override
   void generate(Function f) {
      this._lhs.generateAndDiscard(f);
      f.addCode(111);
   }
}
