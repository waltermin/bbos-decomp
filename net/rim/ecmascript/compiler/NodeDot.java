package net.rim.ecmascript.compiler;

class NodeDot extends NodeUnary {
   private int _name;

   NodeDot(Function f, Node lhs, String qualifier, String name, boolean isAttribute) {
      super(lhs);
      if (qualifier != null) {
         StringBuffer b = new StringBuffer();
         b.append((char)(isAttribute ? '1' : '0'));
         b.append((char)(48 + qualifier.length()));
         b.append((char)(48 + name.length()));
         b.append(qualifier);
         b.append(name);
         name = b.toString();
      } else if (isAttribute) {
         name = "@" + name;
      }

      this._name = f.addId(name);
   }

   @Override
   void generate(Function f) {
      super._lhs.generate(f);
      f.addCode(34, this._name);
   }

   int getName() {
      return this._name;
   }
}
