package net.rim.ecmascript.runtime;

class ESObjectPrototype$Constructor extends Constructor {
   ESObjectPrototype$Constructor() {
      super("Object", GlobalObject.getInstance().objectPrototype);
   }

   @Override
   public long run() {
      long value = this.getParm(0);
      ESObject obj;
      switch (Value.getType(value)) {
         case 1:
            obj = Convert.toObject(value);
            break;
         case 2:
         case 3:
         default:
            obj = new ESObject();
      }

      return Value.makeObjectValue(obj);
   }
}
