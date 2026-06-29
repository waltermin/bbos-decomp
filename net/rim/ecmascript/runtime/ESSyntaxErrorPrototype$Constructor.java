package net.rim.ecmascript.runtime;

class ESSyntaxErrorPrototype$Constructor extends ESErrorPrototype$Constructor {
   ESSyntaxErrorPrototype$Constructor() {
      super("SyntaxError", GlobalObject.getInstance().syntaxErrorPrototype);
   }
}
