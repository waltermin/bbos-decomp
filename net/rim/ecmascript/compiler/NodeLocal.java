package net.rim.ecmascript.compiler;

class NodeLocal extends Node {
   protected int _id;

   NodeLocal(int id) {
      this._id = id;
   }

   @Override
   void generateAndDiscard(Function f) {
   }

   @Override
   void generate(Function f) {
      f.addCode(37, this._id);
   }

   int getId() {
      return this._id;
   }
}
