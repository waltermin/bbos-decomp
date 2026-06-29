package net.rim.device.api.servicebook;

import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Array;
import net.rim.vm.TraceBack;

public final class ServiceRouting {
   private ServiceRoutingProperties[] _ifaces = new ServiceRoutingProperties[0];
   private String[] _services = new String[0];
   private int[][] _servicesCapabilities = new int[0][];
   private int[][] _routes = new int[0][];
   private byte[] _routesState = new byte[0];
   private Object[] _listeners;
   private Proxy _proxy = Proxy.getInstance();
   private ServiceRouting$RFServiceTracker _rfServiceTracker = new ServiceRouting$RFServiceTracker(null);
   private static final long GUID = -4262592966653162609L;
   private static ServiceRouting _instance;

   private ServiceRouting() {
   }

   public static final ServiceRouting getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (ServiceRouting)ar.getOrWaitFor(-4262592966653162609L);
         if (_instance == null) {
            _instance = new ServiceRouting();
            ar.put(-4262592966653162609L, _instance);
         }
      }

      return _instance;
   }

   public final boolean isSerialBypassActive() {
      return this.isSerialBypassActive(null);
   }

   public final boolean isSerialBypassActive(String service) {
      int handle = this.getRouteHandle(2);
      return handle != -1 && this.isServiceRoutable(service, handle);
   }

   public final synchronized void addListener(ServiceRoutingListener listener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, listener);
   }

   public final synchronized void removeListener(ServiceRoutingListener listener) {
      this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
   }

   public final boolean isRouteActive(int routeType) {
      int[] handles = this.getRouteHandles(routeType);
      if (handles != null && handles.length > 0) {
         for (int i = handles.length - 1; i >= 0; i--) {
            if (this.isServiceRoutable(null, handles[i])) {
               return true;
            }
         }
      }

      return false;
   }

   public final String getServiceApn() {
      HostRoutingTable defHRT = HRUtils.getDefaultHRT();
      HostRoutingInfo hri = null;
      if (defHRT != null) {
         hri = defHRT.getActiveHri();
      }

      return !(hri instanceof GprsHRI) ? null : ((GprsHRI)hri).getApn();
   }

   public final synchronized boolean isServiceCapable(int capability, String service, int route) {
      boolean capable = false;
      if (service != null) {
         int index = this.getServiceIndex(service);
         if (index >= 0 && this._servicesCapabilities[index] != null) {
            if (route >= 0) {
               int routeIndex = Arrays.getIndex(this._routes[index], route);
               if (routeIndex >= 0 && routeIndex < this._servicesCapabilities[index].length) {
                  capable = (this._servicesCapabilities[index][routeIndex] & capability) == capability;
               }
            } else {
               for (int i = this._servicesCapabilities[index].length - 1; i >= 0; i--) {
                  if ((this._servicesCapabilities[index][i] & capability) == capability) {
                     capable = true;
                     break;
                  }
               }
            }
         }
      } else if (route >= 0) {
         for (int i = this._routes.length - 1; i >= 0; i--) {
            int routeIndex = Arrays.getIndex(this._routes[i], route);
            if (routeIndex >= 0 && routeIndex < this._servicesCapabilities[i].length && (this._servicesCapabilities[i][routeIndex] & capability) == capability) {
               capable = true;
               break;
            }
         }
      } else {
         for (int i = this._servicesCapabilities.length - 1; i >= 0; i--) {
            for (int j = this._servicesCapabilities[i].length - 1; j >= 0; j--) {
               if ((this._servicesCapabilities[i][j] & capability) == capability) {
                  capable = true;
                  break;
               }
            }
         }
      }

      if (capable) {
         capable = this.isServiceRoutable(service, route);
      }

      if (!capable && route == -1) {
         capable = this.isServiceRoutable(service, this.getRouteHandle(ServiceRoutingProperties.MDP))
            || this.isServiceRoutable(service, this.getRouteHandle(ServiceRoutingProperties.RCP_WI_FI))
            || this.isServiceRoutable(service, this.getRouteHandle(ServiceRoutingProperties.RCP_RF));
      }

      return capable;
   }

   public final synchronized boolean isServiceRoutable(String service, int route) {
      boolean result = false;
      if (service != null) {
         int index = this.getServiceIndex(service);
         result = index >= 0 && (route == -1 || Arrays.getIndex(this._routes[index], route) >= 0);
      } else if (route != -1) {
         for (int i = this._routes.length - 1; i >= 0; i--) {
            if (Arrays.getIndex(this._routes[i], route) >= 0) {
               result = true;
               break;
            }
         }
      } else {
         result = this._services.length > 0 && this._routes.length > 0;
      }

      if (!result) {
         int wafs = RadioInfo.getSupportedWAFs();
         if ((wafs & 11) != 0) {
            if (route == -1 || route == this.getRouteHandle(ServiceRoutingProperties.MDP)) {
               result = this.getRouteState(route != -1 ? route : this.getRouteHandle(ServiceRoutingProperties.MDP), true);
            }

            if (!result && (route == -1 || route == this.getRouteHandle(ServiceRoutingProperties.RCP_RF))) {
               result = this.getRouteState(route != -1 ? route : this.getRouteHandle(ServiceRoutingProperties.RCP_RF), false);
            }
         }

         if (!result && (wafs & 4) != 0 && (route == -1 || route == this.getRouteHandle(ServiceRoutingProperties.RCP_WI_FI))) {
            result = this.getRouteState(route != -1 ? route : this.getRouteHandle(ServiceRoutingProperties.RCP_WI_FI), false);
         }
      }

      if (result && RadioInfo.isDataServiceSuspended()) {
         int stpRoute = this.getRouteHandle(ServiceRoutingProperties.STP);
         if (route == -1) {
            return stpRoute != -1 ? this.isServiceRoutable(service, stpRoute) : false;
         }

         if (stpRoute != route) {
            return false;
         }
      }

      return result;
   }

   public final synchronized int getRouteHandle(int routeType) {
      int i;
      for (i = this._ifaces.length - 1; i >= 0; i--) {
         if (this._ifaces[i].getLinkType() == routeType) {
            return i;
         }
      }

      return i;
   }

   public final synchronized int[] getRouteHandles(int routeType) {
      int i = this._ifaces.length - 1;
      int[] handles = null;

      while (i >= 0) {
         if (this._ifaces[i].getLinkType() == routeType) {
            if (handles == null) {
               handles = new int[]{i};
            } else {
               Array.resize(handles, handles.length + 1);
               handles[handles.length - 1] = i;
            }
         }

         i--;
      }

      return handles;
   }

   public final synchronized int getRouteHandle(String routeType) {
      int i;
      for (i = this._ifaces.length - 1; i >= 0; i--) {
         if (StringUtilities.strEqualIgnoreCase(this._ifaces[i].getName(), routeType)) {
            return i;
         }
      }

      return i;
   }

   public final void setServiceState(String service, int serviceCapabilities, int route, boolean serviceState, boolean redirect) {
      ControlledAccess.assertRRISignatures(false);
      Object[] listeners = null;
      boolean serviceStateChanged = true;
      boolean capabilitiesChanged = false;
      synchronized (this) {
         int serviceIndex = this.getServiceIndex(service);
         if (serviceState) {
            boolean routeState = true;
            if (serviceIndex >= 0) {
               int index = Arrays.getIndex(this._routes[serviceIndex], route);
               if (index >= 0) {
                  int currentCapabilities = this._servicesCapabilities[serviceIndex][index];
                  if ((currentCapabilities | serviceCapabilities) == currentCapabilities) {
                     return;
                  }

                  capabilitiesChanged = true;
               } else {
                  routeState = this.isServiceRoutable(null, route);
                  Arrays.add(this._routes[serviceIndex], route);
                  int capabilitiesIndex = Arrays.getIndex(this._servicesCapabilities[serviceIndex], serviceCapabilities);
                  int currentCapabilities = capabilitiesIndex < 0 ? 0 : this._servicesCapabilities[serviceIndex][capabilitiesIndex];
                  if ((currentCapabilities | serviceCapabilities) != currentCapabilities) {
                     capabilitiesChanged = true;
                  }

                  Arrays.add(this._servicesCapabilities[serviceIndex], serviceCapabilities);
               }
            } else {
               routeState = this.isServiceRoutable(null, route);
               Arrays.add(this._services, service);
               int[] routes = new int[]{route};
               Arrays.add(this._routes, routes);
               int[] capabilities = new int[]{serviceCapabilities};
               Arrays.add(this._servicesCapabilities, capabilities);
               capabilitiesChanged = true;
            }

            if (!routeState) {
               this.setRouteState(false, route, true, true);
            }
         } else {
            if (serviceIndex < 0) {
               return;
            }

            int routeIndex = Arrays.getIndex(this._routes[serviceIndex], route);
            if (routeIndex < 0) {
               return;
            }

            if (this._routes[serviceIndex].length > 1) {
               Arrays.removeAt(this._routes[serviceIndex], routeIndex);
               serviceCapabilities = this._servicesCapabilities[serviceIndex][routeIndex];
               Arrays.removeAt(this._servicesCapabilities[serviceIndex], routeIndex);
               serviceStateChanged = false;
               int i = this._servicesCapabilities[serviceIndex].length - 1;

               while (i >= 0 && (this._servicesCapabilities[serviceIndex][i] & serviceCapabilities) != serviceCapabilities) {
                  i--;
               }

               if (i < 0) {
                  capabilitiesChanged = true;
               }
            } else {
               Arrays.removeAt(this._services, serviceIndex);
               Arrays.removeAt(this._routes, serviceIndex);
               Arrays.removeAt(this._servicesCapabilities, serviceIndex);
               capabilitiesChanged = true;
            }

            if (!this.isServiceRoutable(null, route)) {
               this.setRouteState(false, route, false, true);
            }

            if (!serviceStateChanged && !capabilitiesChanged) {
               return;
            }
         }

         listeners = this._listeners;
      }

      if (listeners != null) {
         if (redirect) {
            if (serviceStateChanged || capabilitiesChanged) {
               this._proxy.invokeRunnable(new ServiceRoutingRunnable(listeners, service, serviceState, capabilitiesChanged));
               return;
            }
         } else {
            for (int i = listeners.length - 1; i >= 0; i--) {
               try {
                  if (serviceStateChanged) {
                     ((ServiceRoutingListener)listeners[i]).serviceRoutingStateChanged(service, serviceState);
                  }

                  if (capabilitiesChanged) {
                     Object var10000 = listeners[i];
                     if (listeners[i] instanceof ServiceRoutingListener2) {
                        ((ServiceRoutingListener2)var10000).serviceRoutingCapabilitiesChanged(service);
                     }
                  }
               } finally {
                  continue;
               }
            }
         }
      }
   }

   public final void setRouteState(int route, boolean routeState, boolean redirect) {
      ControlledAccess.assertRRISignatures(false);
      this.setRouteState(true, route, routeState, redirect);
   }

   private final void setRouteState(boolean checkServices, int route, boolean routeState, boolean redirect) {
      if (route >= 0) {
         Object[] listeners = null;
         synchronized (this) {
            if (checkServices && !routeState) {
               for (int i = this._routes.length - 1; i >= 0; i--) {
                  int routeIndex = Arrays.getIndex(this._routes[i], route);
                  if (routeIndex >= 0 && this._routes[i].length >= 1) {
                     this.setServiceState(this._services[i], 0, route, false, true);
                  }
               }
            }

            if (route < 0 || route >= this._routesState.length) {
               return;
            }

            byte state = (byte)(routeState ? 1 : 0);
            if (this._routesState[route] == state) {
               return;
            }

            this._routesState[route] = state;
            listeners = this._listeners;
         }

         if (listeners != null) {
            if (redirect) {
               this._proxy.invokeRunnable(new ServiceRouteRunnable(listeners, route, routeState));
               return;
            }

            for (int i = listeners.length - 1; i >= 0; i--) {
               try {
                  Object var10000 = listeners[i];
                  if (listeners[i] instanceof ServiceRoutingListener2) {
                     ((ServiceRoutingListener2)var10000).serviceRouteStateChanged(route, routeState);
                  }
               } finally {
                  continue;
               }
            }
         }
      }
   }

   private final boolean getRouteState(int route, boolean mdpCheck) {
      synchronized (this) {
         boolean result;
         if (mdpCheck) {
            result = this._rfServiceTracker.isRouteAvailable();
         } else {
            result = route >= 0 && route < this._routesState.length ? this._routesState[route] == 1 : false;
         }

         return result;
      }
   }

   private final int getServiceIndex(String service) {
      for (int i = this._services.length - 1; i >= 0; i--) {
         if (StringUtilities.strEqualIgnoreCase(service, this._services[i], 1701707776)) {
            return i;
         }
      }

      return -1;
   }

   public final synchronized ServiceRoutingProperties getInterface(int routingHandle) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      return routingHandle >= 0 && routingHandle < this._ifaces.length ? this._ifaces[routingHandle] : null;
   }

   public final synchronized int addInterface(ServiceRoutingProperties serviceRoutingProperties) {
      ControlledAccess.assertRRISignatures(false);
      int handle = this._ifaces.length;
      Arrays.add(this._ifaces, serviceRoutingProperties);
      Arrays.add(this._routesState, (byte)0);
      return handle;
   }
}
