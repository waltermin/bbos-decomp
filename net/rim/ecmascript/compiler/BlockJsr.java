package net.rim.ecmascript.compiler;

class BlockJsr extends Block {
   private Label _jsr;
   private Label _next;
   private int _temp;
   private Compiler _c;

   BlockJsr(Compiler c, Label l, Label jsr, Label next, int temp) {
      super(l);
      this._jsr = jsr;
      this._next = next;
      this._temp = temp;
      this._c = c;
   }

   @Override
   Label getLabel() {
      return super._label;
   }

   Label getJsr() {
      return this._jsr;
   }

   Label getNext() {
      return this._next;
   }

   @Override
   int getNumTargets() {
      return 2;
   }

   @Override
   Label getTarget(int i) {
      return i == 0 ? this._jsr : this._next;
   }

   @Override
   void setTarget(int i, Label l) {
      if (i == 0) {
         this._jsr = l;
      } else {
         this._next = l;
      }
   }

   @Override
   boolean redirectLabels() {
      Label redirect = this._jsr.getRedirect();
      if (redirect != null) {
         this._jsr = redirect;
         return true;
      } else {
         return false;
      }
   }

   @Override
   int generateJumps(Label next, byte[] buffer, int index) {
      int op = this._jsr.getBlock().getOffset() + (this._temp << 16);
      return GenerateCode.genIntOp(86, op, buffer, index);
   }

   @Override
   void dump(Function f, Compiler c, int indent) {
      super.dump(f, c, indent);
      c.print("jsr:");
      this._jsr.dump(c);
      c.print(", next:");
      this._next.dump(c);
      c.print(", temp:");
      c.print4(this._temp);
      c.println("", indent);
   }

   BlockGoto makeGoto() {
      BlockGoto BlockGoto = new BlockGoto(this, this._next);
      this._c.replaceBlock(this, BlockGoto);
      return BlockGoto;
   }
}
