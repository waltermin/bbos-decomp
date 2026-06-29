package net.rim.ecmascript.compiler;

class NodeJavaAdapter extends NodeBinary {
   Node _overrides;

   NodeJavaAdapter(Node lhs, Node overrides, Node args) {
      super(lhs, args);
      this._overrides = overrides;
   }

   @Override
   void generate(Function f) {
      f.addCode(103);
      super._lhs.generate(f);
      int args = 0;
      if (super._rhs != null) {
         super._rhs.generate(f);
         args = ((NodeArgList)super._rhs).getNumChildren();
      }

      this._overrides.generate(f);
      f.addCode(-113, args);
   }

   @Override
   void generateAndDiscard(Function f) {
   }
}
