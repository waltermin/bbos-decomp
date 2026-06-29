package net.rim.ecmascript.compiler;

class NodeIndex extends NodeBinary {
   NodeIndex(Node lhs, Node args) {
      super(lhs, args);
   }

   @Override
   void generate(Function f) {
      super._lhs.generate(f);
      super._rhs.generate(f);
      f.addCode(33);
   }
}
