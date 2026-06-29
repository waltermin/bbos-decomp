package net.rim.ecmascript.compiler;

class NodeId extends Node {
   protected int _id;

   NodeId(int id) {
      this._id = id;
   }

   @Override
   void generateAndDiscard(Function f) {
   }

   @Override
   void generate(Function f) {
      f.addCode(36, this._id);
   }

   int getName() {
      return this._id;
   }

   int getNameOrArguments(Function f) {
      return this._id;
   }
}
