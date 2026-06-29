package net.rim.ecmascript.runtime;

class ESObjectPrototype extends ESObject {
   ESObjectPrototype() {
      super("Object", null, true);
   }

   void populate() {
      this.addField("constructor", 2, Value.makeObjectValue(GlobalObject.getInstance().objectConstructor));
      this.addHostFunction(new ESObjectPrototype$ToString("toString"));
      this.addHostFunction(new ESObjectPrototype$ToString("toLocaleString"));
      this.addHostFunction(new ESObjectPrototype$1(this, "Object", "valueOf"));
      this.addHostFunction(new ESObjectPrototype$2(this, "Object", "hasOwnProperty", 1));
      this.addHostFunction(new ESObjectPrototype$3(this, "Object", "isPrototypeOf", 1));
      this.addHostFunction(new ESObjectPrototype$4(this, "Object", "propertyIsEnumerable", 1));
   }
}
