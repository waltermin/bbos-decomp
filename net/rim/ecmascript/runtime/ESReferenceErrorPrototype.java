package net.rim.ecmascript.runtime;

class ESReferenceErrorPrototype extends ESErrorPrototype {
   ESReferenceErrorPrototype() {
      super("ReferenceError", GlobalObject.getInstance().errorPrototype);
   }

   @Override
   void populate() {
      this.populate(GlobalObject.getInstance().referenceErrorConstructor);
   }
}
