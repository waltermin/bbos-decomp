package net.rim.device.apps.internal.browser.xml;

import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;

public final class XMLConverterRegistry {
   private Hashtable _contentTypes = (Hashtable)(new Object());
   private static final long APP_REGISTRY_KEY;

   private XMLConverterRegistry() {
      this._contentTypes.put("html", "text/html");
   }

   public static final XMLConverterRegistry getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      XMLConverterRegistry instance = (XMLConverterRegistry)ar.getOrWaitFor(3348499767394966251L);
      if (instance == null) {
         instance = new XMLConverterRegistry();
         ar.put(3348499767394966251L, instance);
      }

      return instance;
   }

   public final void register(String type, String contentType) {
      if (!this._contentTypes.contains(type)) {
         this._contentTypes.put(type, contentType);
      }
   }

   public final String getContentType(String type) {
      return (String)(type == null ? null : this._contentTypes.get(type));
   }
}
