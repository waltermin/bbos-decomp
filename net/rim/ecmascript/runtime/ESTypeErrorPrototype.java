package net.rim.ecmascript.runtime;

class ESTypeErrorPrototype extends ESErrorPrototype {
   ESTypeErrorPrototype() {
      super("TypeError", GlobalObject.getInstance().errorPrototype);
   }

   @Override
   void populate() {
      this.populate(GlobalObject.getInstance().typeErrorConstructor);
   }
}
