package net.rim.ecmascript.runtime;

class ESTypeErrorPrototype$Constructor extends ESErrorPrototype$Constructor {
   ESTypeErrorPrototype$Constructor() {
      super("TypeError", GlobalObject.getInstance().typeErrorPrototype);
   }
}
