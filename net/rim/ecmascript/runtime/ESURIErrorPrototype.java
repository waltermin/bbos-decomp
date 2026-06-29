package net.rim.ecmascript.runtime;

class ESURIErrorPrototype extends ESErrorPrototype {
   ESURIErrorPrototype() {
      super("URIError", GlobalObject.getInstance().errorPrototype);
   }

   @Override
   void populate() {
      this.populate(GlobalObject.getInstance().URIErrorConstructor);
   }
}
