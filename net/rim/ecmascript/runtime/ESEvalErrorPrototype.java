package net.rim.ecmascript.runtime;

class ESEvalErrorPrototype extends ESErrorPrototype {
   ESEvalErrorPrototype() {
      super("EvalError", GlobalObject.getInstance().errorPrototype);
   }

   @Override
   void populate() {
      this.populate(GlobalObject.getInstance().evalErrorConstructor);
   }
}
