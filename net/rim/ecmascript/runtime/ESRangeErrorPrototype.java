package net.rim.ecmascript.runtime;

class ESRangeErrorPrototype extends ESErrorPrototype {
   ESRangeErrorPrototype() {
      super("RangeError", GlobalObject.getInstance().errorPrototype);
   }

   @Override
   void populate() {
      this.populate(GlobalObject.getInstance().rangeErrorConstructor);
   }
}
