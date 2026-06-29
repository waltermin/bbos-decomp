package net.rim.wica.runtime.script.internal.appenv;

import java.util.Hashtable;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.persistence.Resource;
import net.rim.wica.runtime.script.internal.PropertiesConverter;
import net.rim.wica.runtime.script.internal.WicaAppContext;

final class ESStringBundle extends ESObject {
   private Hashtable _strings;

   ESStringBundle(String name, WicaAppContext context) {
      this.setPrototype(context.getStringBundlePrototype());
      Resource resource = (Resource)context.getResource(name);
      if (resource != null) {
         this._strings = PropertiesConverter.convert(resource.getData());
      }

      this.addField("name", 5, Value.makeStringValue(name));
   }

   final String getString(String key) {
      return (String)(this._strings != null ? this._strings.get(key) : null);
   }
}
