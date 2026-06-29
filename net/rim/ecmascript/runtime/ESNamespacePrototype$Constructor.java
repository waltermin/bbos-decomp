package net.rim.ecmascript.runtime;

class ESNamespacePrototype$Constructor extends Constructor {
   ESNamespacePrototype$Constructor() {
      super("Namespace", GlobalObject.getInstance().namespacePrototype);
   }

   @Override
   public long run() {
      long uriValue = this.getParm(0);
      String uri;
      if (Value.getType(uriValue) == 2) {
         uri = "";
      } else {
         uri = Convert.toString(uriValue);
      }

      long prefixValue = this.getParm(1);
      String prefix;
      if (Value.getType(prefixValue) == 2) {
         prefix = null;
      } else {
         prefix = Convert.toString(prefixValue);
      }

      return Value.makeObjectValue(new ESNamespace(uri, prefix));
   }
}
