package net.rim.device.apps.internal.manageconnections;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingProperties;
import net.rim.device.api.system.GAN;
import net.rim.device.api.system.GANServiceZoneInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.system.WLANNetInfo;
import net.rim.device.api.system.WLANSystem;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.RibbonNetworkInfo;
import net.rim.device.apps.api.ribbon.StringRibbonComponent;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.system.CoverageInfoInternal;
import net.rim.device.internal.system.RadioInternal;

final class ServicesStatusData {
   private final boolean _wifiEnabled;
   private ResourceBundleFamily _rbf = ResourceBundle.getBundle(-348546850453906601L, "net.rim.device.apps.internal.manageconnections.ManageConnections");
   private StringRibbonComponent _coverageComponent;
   private String _voiceStatus;
   private String _voiceConnection;
   private String _BISConnection;
   private String _BESConnection;
   private String _wifiNetworkName;
   private String _wifiProfile;
   private String _wifiSSID;
   private String _wifiType;
   private String _bluetoothStatus;

   ServicesStatusData(boolean wifiEnabled) {
      this._wifiEnabled = wifiEnabled;
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      Factory factory = repos.getFactory("Coverage");
      this._coverageComponent = (StringRibbonComponent)factory.createInstance(null);
      this.update();
   }

   final String getVoiceStatus() {
      return this._voiceStatus;
   }

   final String getVoiceConnection() {
      return this._voiceConnection;
   }

   final String getBISConnection() {
      return this._BISConnection;
   }

   final String getBESConnection() {
      return this._BESConnection;
   }

   final String getCoverage() {
      String coverage = this._coverageComponent.getText();
      return coverage != null && !coverage.equals("") ? coverage : this._rbf.getString(5);
   }

   final String getProvider() {
      String operator = RibbonNetworkInfo.getInstance().getOperatorName();
      if (RadioInfo.getSignalLevel() != -256 && RadioInfo.getState() == 1 && operator == null) {
         operator = RadioInfo.getCurrentNetworkName();
      }

      return operator;
   }

   final String getProviderString() {
      String provider = this.getProvider();
      return provider == null ? this._rbf.getString(5) : provider;
   }

   final String getWiFiNetworkName() {
      return this._wifiNetworkName;
   }

   final String getWiFiActiveProfile() {
      return this._wifiProfile;
   }

   final String getWiFiSSID() {
      return this._wifiSSID;
   }

   final String getWiFiType() {
      return this._wifiType;
   }

   final String getBluetoothStatus() {
      return this._bluetoothStatus;
   }

   public final void update() {
      this.updateConnectionStrings();
      if (this._wifiEnabled) {
         this.updateWifiStrings();
      }

      this.updateBluetoothString();
   }

   private final void updateWifiStrings() {
      WLANSystem system = WLAN.getWLANSystem();
      if (system == null) {
         this._wifiProfile = this._wifiSSID = this._wifiType = this._rbf.getString(5);
         this._wifiNetworkName = null;
      } else {
         String umaServiceZoneName = null;
         GANServiceZoneInfo info;
         if ((info = GAN.getGANServiceZoneInfo()) != null) {
            umaServiceZoneName = info._serviceZoneName;
         }

         this._wifiProfile = system.getActiveProfileName();
         this._wifiSSID = system.getActiveProfileSSID();
         if (umaServiceZoneName != null) {
            this._wifiNetworkName = umaServiceZoneName;
         } else if (this._wifiProfile != null) {
            this._wifiNetworkName = this._wifiProfile;
         } else {
            this._wifiNetworkName = this._wifiSSID;
         }

         if (this._wifiProfile == null) {
            this._wifiProfile = this._rbf.getString(5);
         }

         if (this._wifiSSID == null) {
            this._wifiSSID = this._rbf.getString(5);
         }

         int handle = system.getActiveProfileSet();
         int profileId = system.getActiveProfileId(handle);
         WLANNetInfo netInfo = system.getWLANNetworkInfo(handle, profileId);
         if (netInfo == null) {
            this._wifiType = this._rbf.getString(5);
         } else {
            switch (netInfo._band & 7) {
               case 1:
                  this._wifiType = this._rbf.getString(38);
                  return;
               case 2:
                  this._wifiType = this._rbf.getString(30);
                  return;
               case 4:
                  this._wifiType = this._rbf.getString(35);
                  return;
               case 7:
                  this._wifiType = this._rbf.getString(39);
                  return;
               default:
                  this._wifiType = this._rbf.getString(5);
            }
         }
      }
   }

   private final void updateBluetoothString() {
      if (!BluetoothME.isPowerOn()) {
         this._bluetoothStatus = this._rbf.getString(13);
      } else if (!BluetoothME.isAnyDeviceConnected()) {
         this._bluetoothStatus = this._rbf.getString(12);
      } else {
         this._bluetoothStatus = this._rbf.getString(14);
      }
   }

   private final void updateConnectionStrings() {
      this._voiceStatus = this._rbf.getString(10);
      this._voiceConnection = this._rbf.getString(5);
      this._BISConnection = this._rbf.getString(5);
      this._BESConnection = this._rbf.getString(5);
      if (RadioInternal.getActiveRadios() != 0) {
         int networkService = RadioInfo.getNetworkService();
         boolean isUMA = (networkService & 16384) != 0;
         if ((networkService & 1) != 0) {
            this._voiceStatus = this._rbf.getString(7);
         } else if ((networkService & 2) != 0) {
            if ((networkService & 8) != 0) {
               this._voiceStatus = this._rbf.getString(6);
            } else {
               this._voiceStatus = this._rbf.getString(4);
            }

            if (isUMA) {
               this._voiceConnection = this._rbf.getString(11);
            } else {
               this._voiceConnection = this._rbf.getString(2);
            }
         }

         if (dataServiceLinkTypeRoutable(1)) {
            CoverageInfoInternal coverageInfo = CoverageInfoInternal.getInstance();
            if (coverageInfo != null) {
               int coverageInfoFlags = coverageInfo.getCoverage(RadioInfo.getActiveWAFs(), false);
               if ((coverageInfoFlags & 4) > 0) {
                  this._BISConnection = this._rbf.getString(isUMA ? 11 : 2);
               }

               if ((coverageInfoFlags & 2) > 0) {
                  this._BESConnection = this._rbf.getString(isUMA ? 11 : 2);
               }
            }
         }

         if (this._wifiEnabled) {
            int cps = dataServiceLinkTypeRoutableInternal(3);
            if ((cps & 2) != 0) {
               this._BESConnection = this._rbf.getString(3);
               if ((cps & 32) != 0) {
                  this._BISConnection = this._rbf.getString(3);
               }
            }
         }
      }
   }

   static final boolean dataServiceLinkTypeRoutable(int linkType) {
      return (dataServiceLinkTypeRoutableInternal(linkType) & 2) != 0;
   }

   private static final int dataServiceLinkTypeRoutableInternal(int linkType) {
      int capabilities = 0;
      ServiceRouting sr = ServiceRouting.getInstance();
      if (!sr.isRouteActive(linkType)) {
         return capabilities;
      }

      int[] handles = sr.getRouteHandles(linkType);
      if (handles != null && handles.length > 0) {
         for (int i = handles.length - 1; i >= 0; i--) {
            ServiceRoutingProperties srps = sr.isServiceRoutable(null, handles[i]) ? sr.getInterface(handles[i]) : null;
            if (srps != null) {
               if ((srps.getLinkCapabilities() & 32) != 0) {
                  capabilities |= 32;
               }

               if ((srps.getLinkCapabilities() & 2) != 0) {
                  capabilities |= 2;
               }
            }
         }
      }

      return capabilities;
   }
}
