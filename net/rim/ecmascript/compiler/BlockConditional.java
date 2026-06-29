package net.rim.ecmascript.compiler;

class BlockConditional extends Block implements OpcodeConstants {
   private Label _ifTrue;
   private Label _ifFalse;
   private int _ifTrueOffset;
   private int _ifFalseOffset;
   private boolean _shortTrueBranch;
   private boolean _shortFalseBranch;

   BlockConditional(Label l, Label ifTrue, Label ifFalse) {
      super(l);
      this._ifTrue = ifTrue;
      this._ifFalse = ifFalse;
   }

   @Override
   Label getLabel() {
      return super._label;
   }

   Label getIfTrue() {
      return this._ifTrue;
   }

   Label getIfFalse() {
      return this._ifFalse;
   }

   @Override
   int getNumTargets() {
      return 2;
   }

   @Override
   Label getTarget(int i) {
      return i == 0 ? this._ifTrue : this._ifFalse;
   }

   @Override
   void setTarget(int i, Label l) {
      if (i == 0) {
         this._ifTrue = l;
      } else {
         this._ifFalse = l;
      }
   }

   @Override
   boolean redirectLabels() {
      boolean change = false;
      Label redirect = this._ifTrue.getRedirect();
      if (redirect != null) {
         this._ifTrue = redirect;
         change = true;
      }

      redirect = this._ifFalse.getRedirect();
      if (redirect != null) {
         this._ifFalse = redirect;
         change = true;
      }

      return change;
   }

   @Override
   int generateJumps(Label next, byte[] buffer, int index) {
      return this.generateJumps(next, buffer, index, super._code.lastOp());
   }

   int generateJumps(Label next, byte[] buffer, int index, byte lastOp) {
      byte op;
      byte reverseOp;
      switch (lastOp) {
         case 10:
            op = 50;
            reverseOp = 48;
            break;
         case 11:
         default:
            op = 52;
            reverseOp = 70;
            index--;
            break;
         case 12:
            op = 54;
            reverseOp = 68;
            index--;
            break;
         case 13:
            op = 58;
            reverseOp = 64;
            index--;
            break;
         case 14:
            op = 62;
            reverseOp = 60;
            index--;
            break;
         case 15:
            op = 66;
            reverseOp = 56;
            index--;
            break;
         case 16:
            op = 70;
            reverseOp = 52;
            index--;
            break;
         case 17:
            op = 72;
            reverseOp = 74;
            index--;
            break;
         case 18:
            op = 74;
            reverseOp = 72;
            index--;
      }

      if (this._ifTrue == next) {
         this._shortTrueBranch = true;
         this._ifTrueOffset = this._ifFalseOffset = index;
         return GenerateCode.genLabelOp(reverseOp, this._shortFalseBranch, this._ifFalse, buffer, index);
      } else if (this._ifFalse == next) {
         this._shortFalseBranch = true;
         this._ifTrueOffset = this._ifFalseOffset = index;
         return GenerateCode.genLabelOp(op, this._shortTrueBranch, this._ifTrue, buffer, index);
      } else {
         this._ifFalseOffset = index;
         index = GenerateCode.genLabelOp(reverseOp, this._shortFalseBranch, this._ifFalse, buffer, index);
         this._ifTrueOffset = index;
         return GenerateCode.genLabelOp(84, this._shortTrueBranch, this._ifTrue, buffer, index);
      }
   }

   @Override
   boolean shortenBranch() {
      boolean rc = false;
      if (!this._shortTrueBranch && this.byteReachable(this._ifTrueOffset, this._ifTrue)) {
         this._shortTrueBranch = true;
         rc = true;
      }

      if (!this._shortFalseBranch && this.byteReachable(this._ifFalseOffset, this._ifFalse)) {
         this._shortFalseBranch = true;
         rc = true;
      }

      return rc;
   }

   @Override
   void dump(Function f, Compiler c, int indent) {
      super.dump(f, c, indent);
      c.print("true:");
      this._ifTrue.dump(c);
      c.print(", false:");
      this._ifFalse.dump(c);
      c.println("", indent);
   }
}
