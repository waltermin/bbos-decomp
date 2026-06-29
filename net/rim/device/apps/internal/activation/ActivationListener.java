package net.rim.device.apps.internal.activation;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;

final class ActivationListener implements GlobalEventListener {
   private static ActivationListener _instance;

   private ActivationListener() {
   }

   public static final void register() {
      if (_instance == null) {
         _instance = new ActivationListener();
         Application.getApplication().addGlobalEventListener(_instance);
      }
   }

   public static final void unregister() {
      if (_instance != null) {
         Application.getApplication().removeGlobalEventListener(_instance);
         _instance = null;
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4731267519193158412L) {
         switch (data0) {
            case 3852:
               unregister();
               Application.getApplication().requestForeground();
         }
      }
   }
}
