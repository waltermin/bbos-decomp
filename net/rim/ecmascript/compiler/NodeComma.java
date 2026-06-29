package net.rim.ecmascript.compiler;

class NodeComma extends NodeBinary {
   NodeComma(Node lhs, Node rhs) {
      super(lhs, rhs);
   }

   @Override
   void generate(Function f) {
      super._lhs.generateAndDiscard(f);
      super._rhs.generate(f);
   }
}
