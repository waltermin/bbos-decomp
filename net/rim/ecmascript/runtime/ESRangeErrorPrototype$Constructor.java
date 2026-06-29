package net.rim.ecmascript.runtime;

class ESRangeErrorPrototype$Constructor extends ESErrorPrototype$Constructor {
   ESRangeErrorPrototype$Constructor() {
      super("RangeError", GlobalObject.getInstance().rangeErrorPrototype);
   }
}
