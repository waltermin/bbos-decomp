package net.rim.ecmascript.compiler;

import net.rim.ecmascript.runtime.Names;

class NodeNew extends NodeBinary {
   NodeNew(Node lhs, Node args) {
      super(lhs, args);
   }

   @Override
   void generate(Function f) {
      int args = 0;
      if (super._rhs != null) {
         args = ((NodeArgList)super._rhs).getNumChildren();
      }

      boolean needNew = true;
      if (!f.isEvalCalled() && !f.isNested() && super._lhs instanceof NodeId) {
         int id = ((NodeId)super._lhs).getName();
         int localIndex = f.getLocalIndex(id);
         if (localIndex == -1) {
            String name = (String)Names.constructors.get(f.getId(((NodeId)super._lhs).getName()));
            if (name == "Object") {
               if (super._rhs == null) {
                  f.addCode(107);
                  return;
               }
            } else if (name == "Array") {
               if (args <= 1 && f.getCompiler().getVersion() != 120) {
                  if (args == 0) {
                     f.addCode(100);
                  } else {
                     super._rhs.generate(f);
                  }

                  f.addCode(108);
                  return;
               }

               needNew = false;
            } else if (name != null) {
               needNew = false;
            }
         }
      }

      if (needNew) {
         f.addCode(107);
         super._lhs.generate(f);
         f.addCode(-127);
      } else {
         f.addCode(103);
         super._lhs.generate(f);
      }

      if (super._rhs != null) {
         super._rhs.generate(f);
      }

      f.addCode(8, args);
   }
}
