package net.rim.ecmascript.compiler;

class NodePostfix extends NodeAssignment {
   private int _op;

   NodePostfix(int op, Node lhs) {
      super(lhs, null);
      this._op = op;
   }

   @Override
   void generate(Function f) {
      int lhsType = this.genLeft(f);
      switch (lhsType) {
         case 0:
         default:
            f.addCode(36, ((NodeId)super._lhs).getNameOrArguments(f));
            f.addCode(-122);
            f.addCode(25);
            f.addCode(this._op);
            f.addCode(117, ((NodeId)super._lhs).getName());
            return;
         case 1:
            f.addCode(25);
            f.addCode(34, ((NodeDot)super._lhs).getName());
            f.addCode(-122);
            f.addCode(26);
            f.addCode(this._op);
            f.addCode(114, ((NodeDot)super._lhs).getName());
            return;
         case 2:
            f.addCode(28);
            f.addCode(33);
            f.addCode(-122);
            f.addCode(27);
            f.addCode(this._op);
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
            f.addCode(78, (this._op == 77 ? 1 : -1) << 24 | ((NodeId)super._lhs).getNameOrArguments(f));
            return;
         case 1:
            f.addCode(25);
            f.addCode(34, ((NodeDot)super._lhs).getName());
            f.addCode(this._op);
            f.addCode(114, ((NodeDot)super._lhs).getName());
            return;
         case 2:
            f.addCode(28);
            f.addCode(33);
            f.addCode(this._op);
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
            f.addCode(-122);
            f.saveGlobalReturn();
            f.addCode(this._op);
            f.addCode(117, ((NodeId)super._lhs).getName());
            return;
         case 1:
            f.addCode(25);
            f.addCode(34, ((NodeDot)super._lhs).getName());
            f.addCode(-122);
            f.saveGlobalReturn();
            f.addCode(this._op);
            f.addCode(114, ((NodeDot)super._lhs).getName());
            return;
         case 2:
            f.addCode(28);
            f.addCode(33);
            f.addCode(-122);
            f.saveGlobalReturn();
            f.addCode(this._op);
            f.addCode(113);
         case -1:
      }
   }
}
