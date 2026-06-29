package net.rim.blackberry.api.mail;

import net.rim.blackberry.api.mail.event.ServiceListener;

public class Service {
   private boolean _connected;

   Service() {
   }

   public boolean isConnected() {
      return this._connected;
   }

   public void setConnected(boolean connected) {
      this._connected = connected;
   }

   @Override
   public String toString() {
      return "";
   }

   public ServiceConfiguration getServiceConfiguration() {
      return null;
   }

   public void addServiceListener(ServiceListener sl) {
      ListenerManager.addServiceListener(sl, this);
   }

   public void removeServiceListener(ServiceListener sl) {
      ListenerManager.removeServiceListener(sl, this);
   }
}
