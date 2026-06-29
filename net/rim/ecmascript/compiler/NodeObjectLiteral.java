package net.rim.ecmascript.compiler;

import net.rim.ecmascript.util.IntVector;

class NodeObjectLiteral extends NodeNary {
   private IntVector _properties = new IntVector();

   void addProperty(Function f, String name, Node expr) {
      this.addExpr(expr);
      this._properties.addElement(f.addId(name));
   }

   @Override
   void generate(Function f) {
      f.addCode(107);
      int numChildren = this.getNumChildren();

      for (int i = 0; i < numChildren; i++) {
         Node n = this.getChild(i);
         if (n != null) {
            f.addCode(25);
            n.generate(f);
            f.addCode(114, this._properties.elementAt(i));
         }
      }
   }
}
