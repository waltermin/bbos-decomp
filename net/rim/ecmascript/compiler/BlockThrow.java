package net.rim.ecmascript.compiler;

class BlockThrow extends Block {
   BlockThrow(Label l) {
      super(l);
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
      return GenerateCode.genByte(-121, buffer, index);
   }

   @Override
   void dump(Function f, Compiler c, int indent) {
      super.dump(f, c, indent);
      c.println("THROWS", indent);
   }
}
