package net.rim.ecmascript.compiler;

class BlockGoto extends Block {
   private boolean _short;
   private int _jmpOffset;
   private Label _target;

   BlockGoto(Block b, Label t) {
      super(b);
      this._target = t;
   }

   BlockGoto(Label l, Label t) {
      super(l);
      this._target = t;
   }

   @Override
   Label getLabel() {
      return super._label;
   }

   Label getTarget() {
      return this._target;
   }

   void setTarget(Label target) {
      this._target = target;
   }

   @Override
   int getNumTargets() {
      return 1;
   }

   @Override
   Label getTarget(int i) {
      return this._target;
   }

   @Override
   void setTarget(int i, Label l) {
      this._target = l;
   }

   @Override
   boolean redirectLabels() {
      Label redirect = this._target.getRedirect();
      if (redirect != null) {
         this._target = redirect;
         return true;
      } else {
         return false;
      }
   }

   @Override
   int generateJumps(Label next, byte[] buffer, int index) {
      if (this._target != next) {
         this._jmpOffset = index;
         return GenerateCode.genLabelOp(84, this._short, this._target, buffer, index);
      } else {
         this._short = true;
         return index;
      }
   }

   @Override
   boolean shortenBranch() {
      if (this._short) {
         return false;
      }

      if (!this.byteReachable(this._jmpOffset, this._target)) {
         return false;
      }

      this._short = true;
      return true;
   }

   @Override
   void dump(Function f, Compiler c, int indent) {
      super.dump(f, c, indent);
      c.print("GOTO ");
      this._target.dump(c);
      c.println("", indent);
   }
}
