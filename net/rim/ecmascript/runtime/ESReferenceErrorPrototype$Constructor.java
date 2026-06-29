package net.rim.ecmascript.runtime;

class ESReferenceErrorPrototype$Constructor extends ESErrorPrototype$Constructor {
   ESReferenceErrorPrototype$Constructor() {
      super("ReferenceError", GlobalObject.getInstance().referenceErrorPrototype);
   }
}
