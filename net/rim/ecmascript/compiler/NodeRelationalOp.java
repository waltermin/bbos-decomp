package net.rim.ecmascript.compiler;

class NodeRelationalOp extends NodeBinary {
   private int _op;

   NodeRelationalOp(int op, Node lhs, Node rhs) {
      super(lhs, rhs);
      this._op = op;
   }

   @Override
   void genIf(Function f, Label trueLabel, Label falseLabel) {
      this.generate(f);
      f.genIf(trueLabel, falseLabel);
   }

   @Override
   void generate(Function f) {
      super._lhs.generate(f);
      super._rhs.generate(f);
      f.addCode(this._op);
   }

   @Override
   Node fold(Function f) {
      Node folded = ConstFold.foldRelational(f, this._op, super._lhs, super._rhs);
      return folded != null ? folded : this;
   }
}
