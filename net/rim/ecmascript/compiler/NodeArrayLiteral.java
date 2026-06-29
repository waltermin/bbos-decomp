package net.rim.ecmascript.compiler;

class NodeArrayLiteral extends NodeNary {
   @Override
   void generate(Function f) {
      Node newArray = new NodeNew(new NodeId(f.addId("Array")), null);
      int numChildren = this.getNumChildren();
      newArray.generate(f);
      int lastPushed = -1;

      for (int i = 0; i < numChildren; i++) {
         Node n = this.getChild(i);
         if (n != null) {
            f.addCode(25);
            f.pushIntValue(i);
            n.generate(f);
            f.addCode(113);
            lastPushed = i;
         }
      }

      if (lastPushed + 1 < numChildren) {
         f.addCode(25);
         f.pushIntValue(numChildren);
         f.addCode(114, f.addId("length"));
      }
   }
}
