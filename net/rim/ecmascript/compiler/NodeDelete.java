package net.rim.ecmascript.compiler;

class NodeDelete extends Node {
   protected Node _lhs;

   NodeDelete(Node lhs) {
      this._lhs = lhs;
   }

   @Override
   void generate(Function f) {
      if (this._lhs instanceof NodeArguments) {
         f.addCode(20);
      } else if (this._lhs instanceof NodeId) {
         f.addCode(21, ((NodeId)this._lhs).getName());
      } else if (!(this._lhs instanceof NodeDot)) {
         if (!(this._lhs instanceof NodeIndex)) {
            this._lhs.generateAndDiscard(f);
            f.addCode(109);
         } else {
            NodeIndex index = (NodeIndex)this._lhs;
            index._lhs.generate(f);
            index._rhs.generate(f);
            f.addCode(23);
         }
      } else {
         NodeDot dot = (NodeDot)this._lhs;
         dot._lhs.generate(f);
         f.addCode(22, ((NodeDot)this._lhs).getName());
      }
   }
}
