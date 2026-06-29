package net.rim.ecmascript.compiler;

class NodeConst extends NodeAssignment {
   NodeConst(Node lhs, Node rhs) {
      super(lhs, rhs);
   }

   @Override
   void genGets(Function f, int lhsType) {
      super.genGets(f, lhsType);
      f.addCode(116, ((NodeId)super._lhs).getName());
   }
}
