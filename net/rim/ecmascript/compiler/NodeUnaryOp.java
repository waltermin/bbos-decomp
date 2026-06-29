package net.rim.ecmascript.compiler;

class NodeUnaryOp extends NodeUnary {
   private int _op;

   NodeUnaryOp(int op, Node lhs) {
      super(lhs);
      this._op = op;
   }

   @Override
   void generate(Function f) {
      super._lhs.generate(f);
      f.addCode(this._op);
   }

   @Override
   Node fold(Function f) {
      Node folded = ConstFold.foldUnary(f, this._op, super._lhs);
      return folded != null ? folded : this;
   }
}
