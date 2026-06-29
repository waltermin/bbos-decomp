package net.rim.ecmascript.compiler;

import java.util.Vector;

class NodeNary extends Node {
   protected Vector _children = new Vector();

   void addExpr(Node n) {
      this._children.addElement(n);
   }

   Node getChild(int i) {
      return (Node)this._children.elementAt(i);
   }

   int getNumChildren() {
      return this._children.size();
   }

   @Override
   void generate(Function f) {
      int numChildren = this.getNumChildren();

      for (int i = 0; i < numChildren; i++) {
         Node n = this.getChild(i);
         n.generate(f);
      }
   }
}
