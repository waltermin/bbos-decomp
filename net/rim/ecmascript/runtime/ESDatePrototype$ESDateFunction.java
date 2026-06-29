package net.rim.ecmascript.runtime;

class ESDatePrototype$ESDateFunction extends HostFunction {
   protected ESDate _date;
   protected long _t;
   protected boolean _isNaN;

   ESDatePrototype$ESDateFunction(String name, int length) {
      super("Date", name, length);
   }

   ESDatePrototype$ESDateFunction(String name) {
      this(name, 0);
   }
}
