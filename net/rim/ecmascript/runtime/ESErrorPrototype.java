package net.rim.ecmascript.runtime;

class ESErrorPrototype extends ESError {
   ESErrorPrototype() {
      this("Error", GlobalObject.getInstance().objectPrototype);
      this.addHostFunction(new ESErrorPrototype$1(this, "Error", "toString"));
   }

   ESErrorPrototype(String name, ESObject prototype) {
      super(name, "", prototype, true);
      this.setPrototype(prototype);
   }

   void populate() {
      this.populate(GlobalObject.getInstance().errorConstructor);
   }

   void populate(ESObject constructor) {
      this.addField("constructor", 2, Value.makeObjectValue(constructor));
      this.addField("name", 2, Value.makeStringValue(super._type));
      this.addField("message", 2, Value.makeStringValue(""));
   }
}
