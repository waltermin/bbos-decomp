package net.rim.ecmascript.compiler;

class RetBlockJsr extends Block {
   private int _temp;

   RetBlockJsr(Label l, int temp) {
      super(l);
      this._temp = temp;
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
      index = GenerateCode.genByte(124, buffer, index);
      return GenerateCode.genShort(this._temp, buffer, index);
   }

   @Override
   void dump(Function f, Compiler c, int indent) {
      super.dump(f, c, indent);
      c.print("ret: ");
      c.print4(this._temp);
      c.println("", indent);
   }
}
