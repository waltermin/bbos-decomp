package net.rim.ecmascript.runtime;

class ESEvalErrorPrototype$Constructor extends ESErrorPrototype$Constructor {
   ESEvalErrorPrototype$Constructor() {
      super("EvalError", GlobalObject.getInstance().evalErrorPrototype);
   }
}
