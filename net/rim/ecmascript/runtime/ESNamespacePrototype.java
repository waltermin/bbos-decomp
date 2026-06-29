package net.rim.ecmascript.runtime;

class ESNamespacePrototype extends ESNamespace {
   ESNamespacePrototype() {
      super("", "", true);
      this.setPrototype(GlobalObject.getInstance().objectPrototype);
   }

   void populate() {
      this.addField("constructor", 2, Value.makeObjectValue(GlobalObject.getInstance().namespaceConstructor));
   }
}
