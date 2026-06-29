package net.rim.ecmascript.compiler;

class NodeCall extends NodeBinary {
   NodeCall(Node lhs, Node args) {
      super(lhs, args);
   }

   @Override
   void generate(Function f) {
      int op = 6;
      if (!(super._lhs instanceof NodeId)) {
         if (!(super._lhs instanceof NodeDot)) {
            if (!(super._lhs instanceof NodeIndex)) {
               f.addCode(103);
               super._lhs.generate(f);
            } else {
               NodeIndex index = (NodeIndex)super._lhs;
               index._lhs.generate(f);
               f.addCode(25);
               index._rhs.generate(f);
               f.addCode(33);
            }
         } else {
            NodeDot dot = (NodeDot)super._lhs;
            dot._lhs.generate(f);
            f.addCode(25);
            f.addCode(34, ((NodeDot)super._lhs).getName());
         }
      } else {
         int id = ((NodeId)super._lhs).getNameOrArguments(f);
         if (id == f.getCompiler().getIdEval()) {
            f.setEvalCalled();
            op = 5;
         }

         f.addCode(41, ((NodeId)super._lhs).getNameOrArguments(f));
      }

      int args = 0;
      if (super._rhs != null) {
         super._rhs.generate(f);
         args = ((NodeArgList)super._rhs).getNumChildren();
      }

      f.addCode(op, args);
   }
}
