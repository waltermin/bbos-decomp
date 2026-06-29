package net.rim.ecmascript.compiler;

class NodeAssignmentOp extends NodeAssignment {
   private int _op;

   NodeAssignmentOp(int op, Node lhs, Node rhs) {
      super(lhs, rhs);
      this._op = op;
   }

   @Override
   void generate(Function f) {
      int lhsType = this.genLeft(f);
      switch (lhsType) {
         case 0:
         default:
            f.addCode(36, ((NodeId)super._lhs).getNameOrArguments(f));
            super._rhs.generate(f);
            f.addCode(this._op);
            f.addCode(25);
            f.addCode(117, ((NodeId)super._lhs).getName());
            return;
         case 1:
            f.addCode(25);
            f.addCode(34, ((NodeDot)super._lhs).getName());
            super._rhs.generate(f);
            f.addCode(this._op);
            f.addCode(26);
            f.addCode(114, ((NodeDot)super._lhs).getName());
            return;
         case 2:
            f.addCode(28);
            f.addCode(33);
            super._rhs.generate(f);
            f.addCode(this._op);
            f.addCode(27);
            f.addCode(113);
         case -1:
      }
   }

   @Override
   void generateAndSave(Function f) {
      int lhsType = this.genLeft(f);
      switch (lhsType) {
         case 0:
         default:
            f.addCode(36, ((NodeId)super._lhs).getNameOrArguments(f));
            super._rhs.generate(f);
            f.addCode(this._op);
            f.saveGlobalReturn();
            f.addCode(117, ((NodeId)super._lhs).getName());
            return;
         case 1:
            f.addCode(25);
            f.addCode(34, ((NodeDot)super._lhs).getName());
            super._rhs.generate(f);
            f.addCode(this._op);
            f.saveGlobalReturn();
            f.addCode(114, ((NodeDot)super._lhs).getName());
            return;
         case 2:
            f.addCode(28);
            f.addCode(33);
            super._rhs.generate(f);
            f.addCode(this._op);
            f.saveGlobalReturn();
            f.addCode(113);
         case -1:
      }
   }

   @Override
   void generateAndDiscard(Function f) {
      int lhsType = this.genLeft(f);
      switch (lhsType) {
         case 0:
         default:
            switch (this._op) {
               case -126:
               case 0:
                  if (super._rhs instanceof NodeInteger) {
                     int value = ((NodeInteger)super._rhs).getValue();
                     if (this._op == -126) {
                        value = -value;
                     }

                     if (value <= 127 && value >= -128) {
                        f.addCode(78, value << 24 | ((NodeId)super._lhs).getNameOrArguments(f));
                        return;
                     }
                  }
               default:
                  f.addCode(36, ((NodeId)super._lhs).getNameOrArguments(f));
                  super._rhs.generate(f);
                  f.addCode(this._op);
                  f.addCode(117, ((NodeId)super._lhs).getName());
                  return;
            }
         case 1:
            f.addCode(25);
            f.addCode(34, ((NodeDot)super._lhs).getName());
            super._rhs.generate(f);
            f.addCode(this._op);
            f.addCode(114, ((NodeDot)super._lhs).getName());
            return;
         case 2:
            f.addCode(28);
            f.addCode(33);
            super._rhs.generate(f);
            f.addCode(this._op);
            f.addCode(113);
         case -1:
      }
   }
}
