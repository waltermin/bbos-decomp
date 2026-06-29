package net.rim.ecmascript.compiler;

class NodeAnd extends NodeBinary {
   Compiler _c;

   NodeAnd(Compiler c, Node lhs, Node rhs) {
      super(lhs, rhs);
      this._c = c;
   }

   @Override
   void genIf(Function f, Label trueLabel, Label falseLabel) {
      Label nextLabel = new Label(this._c);
      super._lhs.genIf(f, nextLabel, falseLabel);
      f.genLabel(nextLabel);
      super._rhs.genIf(f, trueLabel, falseLabel);
   }

   @Override
   void generate(Function f) {
      Label endLabel = new Label(this._c);
      Label trueLabel = new Label(this._c);
      super._lhs.generate(f);
      f.addCode(25);
      f.addCode(-124);
      f.genIf(endLabel, trueLabel);
      f.genLabel(trueLabel);
      f.addCode(92);
      super._rhs.generate(f);
      f.genGoto(endLabel);
      f.genLabel(endLabel);
   }
}
