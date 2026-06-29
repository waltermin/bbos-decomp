package net.rim.ecmascript.compiler;

class NodeBinaryOp extends NodeBinary {
   private int _op;

   NodeBinaryOp(int op, Node lhs, Node rhs) {
      super(lhs, rhs);
      this._op = op;
   }

   @Override
   void generate(Function f) {
      super._lhs.generate(f);
      super._rhs.generate(f);
      f.addCode(this._op);
   }

   @Override
   Node fold(Function f) {
      Node folded = ConstFold.foldBinary(f, this._op, super._lhs, super._rhs);
      return folded != null ? folded : this;
   }
}
