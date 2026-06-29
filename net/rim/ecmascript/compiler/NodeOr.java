package net.rim.ecmascript.compiler;

class NodeOr extends NodeBinary {
   Compiler _c;

   NodeOr(Compiler c, Node lhs, Node rhs) {
      super(lhs, rhs);
      this._c = c;
   }

   @Override
   void genIf(Function f, Label trueLabel, Label falseLabel) {
      Label nextLabel = new Label(this._c);
      super._lhs.genIf(f, trueLabel, nextLabel);
      f.genLabel(nextLabel);
      super._rhs.genIf(f, trueLabel, falseLabel);
   }

   @Override
   void generate(Function f) {
      Label endLabel = new Label(this._c);
      Label falseLabel = new Label(this._c);
      super._lhs.generate(f);
      f.addCode(25);
      f.addCode(-123);
      f.genIf(endLabel, falseLabel);
      f.genLabel(falseLabel);
      f.addCode(92);
      super._rhs.generate(f);
      f.genGoto(endLabel);
      f.genLabel(endLabel);
   }
}
