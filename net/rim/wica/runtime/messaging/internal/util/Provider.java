package net.rim.wica.runtime.messaging.internal.util;

import java.util.Hashtable;
import net.rim.wica.runtime.service.DefaultContainer;
import net.rim.wica.runtime.service.ServiceProvider;

public class Provider extends DefaultContainer {
   private Hashtable _componentTable = new Hashtable();
   public static final String SCHEDULER = "Scheduler";
   public static final String BACKGROUND_SCHEDULER = "BackgroundScheduler";

   public Provider(ServiceProvider parent) {
      super(parent);
   }

   public Object getService(String name) {
      return this._componentTable.get(name);
   }

   public void registerComponent(String name, Object componentImpl) {
      if (name == null) {
         throw new IllegalArgumentException("Name cannot be null");
      }

      if (this._componentTable.containsKey(name)) {
         throw new IllegalArgumentException("The component " + name + "already exists in the container");
      }

      this._componentTable.put(name, componentImpl);
   }
}
