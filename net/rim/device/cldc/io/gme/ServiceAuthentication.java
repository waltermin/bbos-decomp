package net.rim.device.cldc.io.gme;

import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingProperties;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;

public final class ServiceAuthentication {
   private String[] _services = new Object[0];
   private AuthThread[] _threads = new AuthThread[0];
   private static final long MAX_SESSION_TIME;
   private static final int NO_KEY_FOR_SERVICE;
   private static final int ALREADY_AUTHENTICATING;
   private static final int NOT_AUTHENTICATING;

   protected ServiceAuthentication() {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected final void startService(String service, int serviceCapabilities, String connectionAddress, int routingHandle) {
      AuthThread thread;
      synchronized (this._services) {
         int index = this.getServiceIndexByHandle(service, routingHandle, true);
         if (index < 0) {
            index = this.getServiceIndex(service);
            if (index >= 0) {
               ServiceRouting sr = ServiceRouting.getInstance();
               ServiceRoutingProperties cProps = sr.getInterface(this._threads[index].getRoutingHandle());
               ServiceRoutingProperties iProps = sr.getInterface(routingHandle);
               if (iProps == null || cProps == null || iProps.getMonetaryImpact() >= cProps.getMonetaryImpact()) {
                  EventLogger.logEvent(1866032962523356178L, 1380475762, 3);
                  return;
               }

               EventLogger.logEvent(1866032962523356178L, 1095525234, 0);
               index = -1;
            }
         } else {
            thread = this._threads[index];
            int state = thread.getAuthState();
            switch (state) {
               case -1:
                  break;
               case 0:
               case 1:
               default:
                  long sessionTime = System.currentTimeMillis() - thread.getSessionTimestamp();
                  if (sessionTime > 0 && sessionTime < 600000) {
                     EventLogger.logEvent(1866032962523356178L, 1096118894, 3);
                     break;
                  }
               case 2:
                  if (state == 2) {
                     EventLogger.logEvent(1866032962523356178L, 1096119155, 3);
                  }

                  thread.shutdown();
               case 3:
               case 4:
                  Arrays.removeAt(this._services, index);
                  Arrays.removeAt(this._threads, index);
                  index = -1;
            }
         }

         if (index >= 0) {
            EventLogger.logEvent(1866032962523356178L, 1094808948, 3);
            return;
         }

         boolean var14 = false /* VF: Semaphore variable */;

         try {
            var14 = true;
            thread = new AuthThread(service, serviceCapabilities, connectionAddress, routingHandle);
            Arrays.add(this._services, service);
            Arrays.add(this._threads, thread);
            var14 = false;
         } finally {
            if (var14) {
               EventLogger.logEvent(1866032962523356178L, 1313564025, 3);
               return;
            }
         }
      }

      ProtocolDaemon.getInstance().startThread(thread);
   }

   protected final void stopService(String service, int routingHandle) {
      AuthThread thread;
      synchronized (this._services) {
         int index = this.getServiceIndexByHandle(service, routingHandle, true);
         if (index < 0) {
            EventLogger.logEvent(1866032962523356178L, 1312912756, 3);
            return;
         }

         thread = this._threads[index];
         Arrays.removeAt(this._services, index);
         Arrays.removeAt(this._threads, index);
      }

      thread.shutdown();
   }

   private final int getServiceIndex(String service) {
      return this.getServiceIndexByHandle(service, -1, false);
   }

   private final int getServiceIndexByHandle(String service, int routingHandle, boolean checkHandle) {
      for (int i = (this._services != null ? this._services.length : 0) - 1; i >= 0; i--) {
         if (StringUtilities.strEqualIgnoreCase(service, this._services[i], 1701707776)) {
            if (!checkHandle) {
               return i;
            }

            if (this._threads[i].getRoutingHandle() == routingHandle) {
               return i;
            }
         }
      }

      return -1;
   }
}
