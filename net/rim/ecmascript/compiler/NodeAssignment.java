package net.rim.ecmascript.compiler;

import net.rim.ecmascript.util.Resources;

class NodeAssignment extends NodeBinary {
   private String _notLValue = Resources.getString(15);
   protected static final int LHS_NAME;
   protected static final int LHS_DOT;
   protected static final int LHS_INDEX;

   NodeAssignment(Node lhs, Node rhs) {
      super(lhs, rhs);
   }

   NodeAssignment(Node lhs, Node rhs, String notLValue) {
      super(lhs, rhs);
      this._notLValue = notLValue;
   }

   int genLeft(Function f) {
      if (super._lhs instanceof NodeId) {
         return 0;
      }

      if (!(super._lhs instanceof NodeDot)) {
         if (!(super._lhs instanceof NodeIndex)) {
            throw new CompileError(this._notLValue);
         }

         NodeIndex index = (NodeIndex)super._lhs;
         index._lhs.generate(f);
         index._rhs.generate(f);
         return 2;
      } else {
         NodeDot dot = (NodeDot)super._lhs;
         dot._lhs.generate(f);
         return 1;
      }
   }

   void genGets(Function f, int lhsType) {
      switch (lhsType) {
         case -1:
            throw new CompileError(this._notLValue);
         case 0:
         default:
            f.addCode(117, ((NodeId)super._lhs).getNameOrArguments(f));
            return;
         case 1:
            if (super._rhs == null) {
               f.addCode(-125);
            }

            f.addCode(114, ((NodeDot)super._lhs).getName());
            return;
         case 2:
            if (super._rhs == null) {
               f.addCode(27);
               f.addCode(92);
               f.addCode(27);
               f.addCode(92);
            }

            f.addCode(113);
      }
   }

   void genRight(Function f) {
      if (super._rhs != null) {
         super._rhs.generate(f);
      }
   }

   @Override
   void generate(Function f) {
      int lhsType = this.genLeft(f);
      this.genRight(f);
      switch (lhsType) {
         case -1:
            break;
         case 0:
         default:
            f.addCode(25);
            break;
         case 1:
            f.addCode(26);
            break;
         case 2:
            f.addCode(27);
      }

      this.genGets(f, lhsType);
   }

   @Override
   void generateAndDiscard(Function f) {
      int lhsType = this.genLeft(f);
      this.genRight(f);
      this.genGets(f, lhsType);
   }

   @Override
   void generateAndSave(Function f) {
      int lhsType = this.genLeft(f);
      this.genRight(f);
      f.saveGlobalReturn();
      this.genGets(f, lhsType);
   }
}
