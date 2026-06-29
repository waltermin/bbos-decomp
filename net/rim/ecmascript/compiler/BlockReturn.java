package net.rim.ecmascript.compiler;

class BlockReturn extends Block {
   private boolean _haveValue;

   BlockReturn(boolean haveValue, Label l) {
      super(l);
      this._haveValue = haveValue;
   }

   @Override
   int getNumTargets() {
      return 0;
   }

   @Override
   Label getTarget(int i) {
      return null;
   }

   @Override
   void setTarget(int i, Label l) {
   }

   @Override
   boolean redirectLabels() {
      return false;
   }

   @Override
   int generateJumps(Label next, byte[] buffer, int index) {
      return GenerateCode.genByte(this._haveValue ? 125 : 126, buffer, index);
   }

   @Override
   void dump(Function f, Compiler c, int indent) {
      super.dump(f, c, indent);
      c.println("Return", indent);
   }
}
