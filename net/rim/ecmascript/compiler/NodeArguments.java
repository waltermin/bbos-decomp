package net.rim.ecmascript.compiler;

class NodeArguments extends NodeId {
   NodeArguments(int id) {
      super(id);
   }

   @Override
   void generate(Function f) {
      f.addCode(36, this.getNameOrArguments(f));
   }

   @Override
   int getNameOrArguments(Function f) {
      f.addCode(32);
      return super.getName();
   }
}
