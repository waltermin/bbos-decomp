package net.rim.ecmascript.compiler;

class BlockTry extends Block {
   private Label _try;
   private Label _catch;

   BlockTry(Label l, Label ifTrue, Label ifFalse) {
      super(l);
      this._try = ifTrue;
      this._catch = ifFalse;
   }

   @Override
   Label getLabel() {
      return super._label;
   }

   Label getIfTrue() {
      return this._try;
   }

   Label getIfFalse() {
      return this._catch;
   }

   @Override
   int getNumTargets() {
      return 2;
   }

   @Override
   Label getTarget(int i) {
      return i == 0 ? this._try : this._catch;
   }

   @Override
   void setTarget(int i, Label l) {
      if (i == 0) {
         this._try = l;
      } else {
         this._catch = l;
      }
   }

   @Override
   boolean redirectLabels() {
      return false;
   }

   @Override
   int generateJumps(Label next, byte[] buffer, int index) {
      return GenerateCode.genLabelOp(110, false, this._catch, buffer, index);
   }

   @Override
   void dump(Function f, Compiler c, int indent) {
      super.dump(f, c, indent);
      c.print("try:");
      this._try.dump(c);
      c.print(", catch:");
      this._catch.dump(c);
      c.println("", indent);
   }

   @Override
   boolean needsScope() {
      return ByteCode.requiresScope(110);
   }
}
