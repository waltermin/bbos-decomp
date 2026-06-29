package net.rim.ecmascript.compiler;

class NodeTemp extends Node {
   protected int _id;

   NodeTemp(int id) {
      this._id = id;
   }

   @Override
   void generateAndDiscard(Function f) {
   }

   @Override
   void generate(Function f) {
      f.addCode(46, this._id);
   }

   int getId() {
      return this._id;
   }
}
