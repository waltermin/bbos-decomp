package net.rim.wica.runtime.messaging.internal.util;

import java.util.Hashtable;
import net.rim.wica.runtime.service.DefaultContainer;
import net.rim.wica.runtime.service.ServiceProvider;

public class Provider extends DefaultContainer {
   private Hashtable _componentTable = (Hashtable)(new Object());
   public static final String SCHEDULER;
   public static final String BACKGROUND_SCHEDULER;

   public Provider(ServiceProvider parent) {
      super(parent);
   }

   public Object getService(String name) {
      return this._componentTable.get(name);
   }

   public void registerComponent(String name, Object componentImpl) {
      if (name == null) {
         throw new Object("Name cannot be null");
      }

      if (this._componentTable.containsKey(name)) {
         throw new Object(((StringBuffer)(new Object("The component "))).append(name).append("already exists in the container").toString());
      }

      this._componentTable.put(name, componentImpl);
   }
}
