package net.rim.ecmascript.compiler;

class NodeTypeof extends Node {
   protected Node _lhs;

   NodeTypeof(Node lhs) {
      this._lhs = lhs;
   }

   @Override
   void generate(Function f) {
      if (this._lhs instanceof NodeId) {
         f.addCode(-120, ((NodeId)this._lhs).getNameOrArguments(f));
      } else {
         this._lhs.generate(f);
         f.addCode(-119);
      }
   }
}
