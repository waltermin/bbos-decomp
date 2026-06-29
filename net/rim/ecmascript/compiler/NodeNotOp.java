package net.rim.ecmascript.compiler;

class NodeNotOp extends NodeUnary {
   NodeNotOp(Node lhs) {
      super(lhs);
   }

   @Override
   void genIf(Function f, Label trueLabel, Label falseLabel) {
      super._lhs.genIf(f, falseLabel, trueLabel);
   }

   @Override
   void generate(Function f) {
      super._lhs.generate(f);
      f.addCode(-124);
   }
}
