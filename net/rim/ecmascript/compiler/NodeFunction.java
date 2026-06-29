package net.rim.ecmascript.compiler;

class NodeFunction extends Node {
   private Function _functionExpression;
   private String _id;

   NodeFunction(Function f, boolean exposeName) {
      this._functionExpression = f;
      if (exposeName) {
         this._id = this._functionExpression.getId();
      }
   }

   @Override
   void generate(Function f) {
      f.addCode(91, this._functionExpression.getIndex());
      if (this._id != null) {
         f.setNeedsScope();
         f.addCode(25);
         f.addCode(117, f.addId(this._id));
      }
   }

   @Override
   void generateAndDiscard(Function f) {
      if (this._id != null) {
         f.setNeedsScope();
         f.addCode(91, this._functionExpression.getIndex());
         f.addCode(117, f.addId(this._id));
      }
   }

   int getName() {
      return this._functionExpression.getIndex();
   }
}
