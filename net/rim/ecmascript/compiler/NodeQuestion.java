package net.rim.ecmascript.compiler;

class NodeQuestion extends NodeBinary {
   private Node _cond;
   private Compiler _c;

   NodeQuestion(Compiler c, Node cond, Node lhs, Node rhs) {
      super(lhs, rhs);
      this._cond = cond;
      this._c = c;
   }

   @Override
   void generate(Function f) {
      Label endLabel = new Label(this._c);
      Label falseLabel = new Label(this._c);
      Label trueLabel = new Label(this._c);
      this._cond.genIf(f, trueLabel, falseLabel);
      f.genLabel(trueLabel);
      super._lhs.generate(f);
      f.genGoto(endLabel);
      f.genLabel(falseLabel);
      super._rhs.generate(f);
      f.genLabel(endLabel);
   }
}
