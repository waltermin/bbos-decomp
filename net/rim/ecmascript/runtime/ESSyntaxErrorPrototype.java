package net.rim.ecmascript.runtime;

class ESSyntaxErrorPrototype extends ESErrorPrototype {
   ESSyntaxErrorPrototype() {
      super("SyntaxError", GlobalObject.getInstance().errorPrototype);
   }

   @Override
   void populate() {
      this.populate(GlobalObject.getInstance().syntaxErrorConstructor);
   }
}
