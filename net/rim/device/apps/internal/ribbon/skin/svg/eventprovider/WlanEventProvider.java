package net.rim.device.apps.internal.ribbon.skin.svg.eventprovider;

import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener2;
import net.rim.device.api.servicebook.ServiceRoutingProperties;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.system.WLANListenerInternal;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.apps.internal.ribbon.components.GlobalListenerFactoryHelper;

public class WlanEventProvider implements WLANListenerInternal, ServiceRoutingListener2, RadioStatusListener, GlobalEventListener {
   private SkinEventProvider _skinProvider;
   private boolean _registeredForWLANEnabled;
   private boolean _registeredForWLAN;
   private boolean _registeredForSR;
   private boolean _registeredForRadioStatus;
   private boolean _hasGAN;
   private boolean _radioOn;
   private boolean _wlanEnabled;
   private static final long SR_HELPER_KEY = 8991282014109009253L;
   public static final String WIFI_ENABLED = "wifi_enabled";
   public static final String WIFI_DISABLED = "wifi_disabled";
   public static final String WIFI_RADIO_ON = "wifi_radio_on";
   public static final String WIFI_RADIO_OFF = "wifi_radio_off";
   public static final String WIFI_CONNECT = "wifi_connect";
   public static final String WIFI_DISCONNECT = "wifi_disconnect";
   public static final String DATA_WIFI_CONNECT = "data_wifi_connect";
   public static final String DATA_WIFI_DISCONNECT = "data_wifi_disconnect";
   public static final String DATA_CARRIER_CONNECT = "data_carrier_connect";
   public static final String DATA_CARRIER_DISCONNECT = "data_carrier_disconnect";
   public static final String UMA_CONNECT = "UMA_connect";
   public static final String UMA_DISCONNECT = "UMA_disconnect";
   private static final int EVENT_TYPE_WLAN_ENABLED = 0;
   private static final int EVENT_TYPE_WLAN = 1;
   private static final int EVENT_TYPE_SR = 2;
   private static final int EVENT_TYPE_RADIO_STATUS = 3;
   private static WLANListenerHelper _wlanHelper;
   private static ServiceRoutingListener2Helper _srHelper;
   private static RadioStatusListenerHelper _radioStatusHelper;
   private static GlobalListenerFactoryHelper _globalHelper;
   private static ToIntHashtable _events;

   public boolean isEventSupported(String eventId) {
      return _events.get(eventId) != -1;
   }

   public void provideEvent(String eventId) {
      int eventType = _events.get(eventId);
      switch (eventType) {
         case -1:
            break;
         case 0:
         default:
            if (!this._registeredForWLANEnabled) {
               _globalHelper.addComponentForUpdate(this);
               this._skinProvider.registerEventPair("wifi_enabled", "wifi_disabled");
               this._registeredForWLANEnabled = true;
               return;
            }
            break;
         case 1:
            if (!this._registeredForWLAN) {
               _wlanHelper.addComponentForUpdate(this);
               this._skinProvider.registerEventPair("wifi_radio_on", "wifi_radio_off");
               this._skinProvider.registerEventPair("wifi_connect", "wifi_disconnect");
               this._registeredForWLAN = true;
               return;
            }
            break;
         case 2:
            if (!this._registeredForSR) {
               _srHelper.addComponentForUpdate(this);
               this._skinProvider.registerEventPair("data_wifi_connect", "data_wifi_disconnect");
               this._skinProvider.registerEventPair("data_carrier_connect", "data_carrier_disconnect");
               this._registeredForSR = true;
               return;
            }
            break;
         case 3:
            if (!this._registeredForRadioStatus) {
               _radioStatusHelper.addComponentForUpdate(this);
               this._skinProvider.registerEventPair("UMA_connect", "UMA_disconnect");
               this._registeredForRadioStatus = true;
            }
      }
   }

   public void dispatchInitEvents() {
      this._hasGAN = false;
      this._radioOn = false;
      if (this._registeredForWLANEnabled && (this._wlanEnabled = wlanEnabled())) {
         this._skinProvider.dispatchEvent("wifi_enabled");
      }

      if (this._registeredForWLAN && WLAN.isRadioOn()) {
         this._skinProvider.dispatchEvent("wifi_radio_on");
         if (WLAN.isAssociated() != null) {
            this._skinProvider.dispatchEvent("wifi_connect");
         }
      }

      if (this._registeredForSR) {
         if (this.dataServiceLinkTypeRoutable(1)) {
            this._skinProvider.dispatchEvent("data_carrier_connect");
         }

         if (this.dataServiceLinkTypeRoutable(3)) {
            this._skinProvider.dispatchEvent("data_wifi_connect");
         }
      }

      if (this._registeredForRadioStatus) {
         this._radioOn = RadioInfo.getState() == 1;
         int networkService = RadioInfo.getNetworkService();
         if ((networkService & 16384) != 0) {
            this._hasGAN = true;
            if (this._radioOn) {
               this._skinProvider.dispatchEvent("UMA_connect");
            }
         }
      }
   }

   public void mobilityManagementEvent(int eventCode, int cause) {
   }

   @Override
   public void radioStatus(boolean started) {
      if (started) {
         this._skinProvider.dispatchEvent("wifi_radio_on");
      } else {
         this._skinProvider.dispatchEvent("wifi_radio_off");
      }
   }

   @Override
   public void networkSuccess() {
      this._skinProvider.dispatchEvent("wifi_connect");
   }

   @Override
   public void networkFail(int status, int error, int extendedInfo) {
      this._skinProvider.dispatchEvent("wifi_disconnect");
   }

   @Override
   public void networkFound(int handle) {
   }

   @Override
   public void networkApChange() {
   }

   @Override
   public void serviceRouteStateChanged(int routeHandle, boolean routeState) {
      ServiceRouting sr = ServiceRouting.getInstance();
      ServiceRoutingProperties srp = sr.getInterface(routeHandle);
      if (srp != null) {
         int linkType = srp.getLinkType();
         boolean routable = this.dataServiceLinkTypeRoutable(linkType);
         switch (linkType) {
            case 0:
               break;
            case 1:
            case 2:
            default:
               if (routable) {
                  this._skinProvider.dispatchEvent("data_carrier_connect");
                  return;
               }

               this._skinProvider.dispatchEvent("data_carrier_disconnect");
               return;
            case 3:
               if (routable) {
                  this._skinProvider.dispatchEvent("data_wifi_connect");
                  return;
               }

               this._skinProvider.dispatchEvent("data_wifi_disconnect");
         }
      }
   }

   @Override
   public void serviceRoutingStateChanged(String service, boolean serviceState) {
   }

   @Override
   public void serviceRoutingCapabilitiesChanged(String service) {
   }

   @Override
   public void networkStarted(int networkId, int service) {
      this.networkServiceChange(networkId, service);
   }

   @Override
   public void networkServiceChange(int networkId, int service) {
      int radioState = RadioInfo.getState();
      if (this._radioOn && radioState != 1) {
         this._radioOn = false;
         if (this._hasGAN) {
            this._skinProvider.dispatchEvent("UMA_disconnect");
         }
      } else if (!this._radioOn && radioState == 1) {
         this._radioOn = true;
         if (this._hasGAN) {
            this._skinProvider.dispatchEvent("UMA_connect");
         }
      }

      if (this._hasGAN && (service & 16384) == 0) {
         this._hasGAN = false;
         if (this._radioOn) {
            this._skinProvider.dispatchEvent("UMA_disconnect");
            return;
         }
      } else if (!this._hasGAN && (service & 16384) != 0) {
         this._hasGAN = true;
         if (this._radioOn) {
            this._skinProvider.dispatchEvent("UMA_connect");
         }
      }
   }

   @Override
   public void signalLevel(int level) {
   }

   @Override
   public void baseStationChange() {
   }

   @Override
   public void radioTurnedOff() {
   }

   @Override
   public void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public void networkScanComplete(boolean success) {
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 2950066364548195165L || guid == -5567093064078848383L || guid == 7181491349594683390L) {
         boolean wlanEnabled = wlanEnabled();
         if (wlanEnabled != this._wlanEnabled) {
            this._wlanEnabled = wlanEnabled;
            this._skinProvider.dispatchEvent(wlanEnabled ? "wifi_enabled" : "wifi_disabled");
         }
      }
   }

   @Override
   public void networkStateChange(int state) {
   }

   private static boolean wlanEnabled() {
      return WLAN.isWLANAllowed();
   }

   private boolean dataServiceLinkTypeRoutable(int linkType) {
      boolean routable = false;
      ServiceRouting sr = ServiceRouting.getInstance();
      int[] handles = sr.getRouteHandles(linkType);
      if (handles != null && handles.length > 0) {
         for (int i = handles.length - 1; i >= 0; i--) {
            routable = sr.isServiceRoutable(null, handles[i]);
            if (routable) {
               return routable;
            }
         }
      }

      return routable;
   }

   WlanEventProvider(SkinEventProvider skinProvider) {
      this._skinProvider = skinProvider;
   }

   static void initialize() {
      _events = (ToIntHashtable)(new Object(15));
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      if (WLAN.isSupported()) {
         Application app = Application.getApplication();
         _globalHelper = new GlobalListenerFactoryHelper();
         app.addGlobalEventListener(_globalHelper);
         _events.put("wifi_enabled", 0);
         _events.put("wifi_disabled", 0);
         _wlanHelper = new WLANListenerHelper();
         app.addRadioListener(4, _wlanHelper);
         _events.put("wifi_radio_on", 1);
         _events.put("wifi_radio_off", 1);
         _events.put("wifi_connect", 1);
         _events.put("wifi_disconnect", 1);
         _radioStatusHelper = new RadioStatusListenerHelper();
         app.addRadioListener(5, _radioStatusHelper);
         _events.put("UMA_connect", 3);
         _events.put("UMA_disconnect", 3);
      }

      ServiceRouting sr = ServiceRouting.getInstance();
      if (sr != null) {
         _srHelper = (ServiceRoutingListener2Helper)ar.getOrWaitFor(8991282014109009253L);
         if (_srHelper == null) {
            _srHelper = new ServiceRoutingListener2Helper();
            sr.addListener(_srHelper);
            ar.put(8991282014109009253L, _srHelper);
         }

         _events.put("data_wifi_connect", 2);
         _events.put("data_wifi_disconnect", 2);
         _events.put("data_carrier_connect", 2);
         _events.put("data_carrier_disconnect", 2);
      }
   }
}
