package net.rim.device.internal.system;

import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener2;
import net.rim.device.api.servicebook.ServiceRoutingProperties;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.cldc.io.ippp.SBApplicationData;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Message;

public final class CoverageInfoInternalImpl extends CoverageInfoInternal implements ServiceRoutingListener2, GlobalEventListener {
   private boolean _initialized;
   private ServiceRecord[] _ipppServiceRecords;
   private int[] _ipppServiceRecordType;
   private int _cdmaRadioCoverage;
   private int _wlanRadioCoverage;
   private int _idenRadioCoverage;
   private int _3gppRadioCoverage;
   private int _serialBypassCoverage;
   private int _routableServiceRecords;
   private int _totalCoverage;
   private static final int SR_ALWAYS_ROUTABLE = 1;
   private static final int COVERAGE_MDS_AND_BIS_B = 6;
   private static int _lastEmailApnId = -1;
   private static Object _apnIdLock = new Object();
   private static String IPPP = "IPPP";
   private static String IIF_APN;

   private CoverageInfoInternalImpl() {
   }

   public static final void register() {
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      CoverageInfoInternalImpl info = (CoverageInfoInternalImpl)appReg.getOrWaitFor(-809192429028495755L);
      if (info == null) {
         info = new CoverageInfoInternalImpl();
         appReg.put(-809192429028495755L, info);
         String message = "Registered CoverageInfoInternalImpl object.";
         EventLogger.logEvent(-7509200465648525729L, message.getBytes());
      }
   }

   private final void setupOriginalCoverageStates() {
      int wafs = RadioInfo.getSupportedWAFs();
      if ((wafs & 2) == 2) {
         this._cdmaRadioCoverage = determineRadioCoverage(2);
      }

      if ((wafs & 4) == 4) {
         this._wlanRadioCoverage = determineRadioCoverage(4);
      }

      if ((wafs & 8) == 8) {
         this._idenRadioCoverage = determineRadioCoverage(8);
      }

      if ((wafs & 1) == 1) {
         this._3gppRadioCoverage = determineRadioCoverage(1);
      }

      this.updateIpppServiceRecords();
      this._serialBypassCoverage = determineSerialBypassCoverage();
      this._routableServiceRecords = this.determineRoutableServiceRecords();
      this._totalCoverage = this.getCoverage(wafs, true);
   }

   private final void setupListeners() {
      Proxy proxy = Proxy.getInstance();
      int wafs = RadioInfo.getSupportedWAFs();
      if ((wafs & 2) == 2) {
         proxy.addRadioListener(2, new CoverageInfoInternalImpl$MyRadioStatusListener(this, 2));
      }

      if ((wafs & 4) == 4) {
         proxy.addRadioListener(4, new CoverageInfoInternalImpl$MyRadioStatusListener(this, 4));
      }

      if ((wafs & 8) == 8) {
         proxy.addRadioListener(8, new CoverageInfoInternalImpl$MyRadioStatusListener(this, 8));
      }

      if ((wafs & 1) == 1) {
         proxy.addRadioListener(1, new CoverageInfoInternalImpl$MyRadioStatusListener(this, 1));
      }

      proxy.addGlobalEventListener(this);
      ServiceRouting.getInstance().addListener(this);
   }

   @Override
   protected final synchronized void ensureInitialized() {
      if (!this._initialized) {
         try {
            this.setupOriginalCoverageStates();
            this.setupListeners();
            this._initialized = true;
            String message = "Initialized CoverageInfoInternalImpl object.";
            EventLogger.logEvent(-7509200465648525729L, message.getBytes());
         } finally {
            String messagex = "Error initializing CoverageInfoInternalImpl object.";
            EventLogger.logEvent(-7509200465648525729L, messagex.getBytes());
            return;
         }
      }
   }

   @Override
   public final synchronized int getCoverage(int wafs, boolean considerSerialBypass) {
      int coverage = 0;
      if ((wafs & 2) == 2) {
         coverage |= this._cdmaRadioCoverage;
      }

      if ((wafs & 4) == 4) {
         coverage |= this._wlanRadioCoverage;
      }

      if ((wafs & 8) == 8) {
         coverage |= this._idenRadioCoverage;
      }

      if ((wafs & 1) == 1) {
         coverage |= this._3gppRadioCoverage;
      }

      if (considerSerialBypass) {
         coverage |= this._serialBypassCoverage;
      }

      return coverage & this._routableServiceRecords;
   }

   @Override
   public final boolean isCoverageSufficient(int coverageType, int wafs, boolean considerSerialBypass) {
      switch (coverageType) {
         case -1:
         case 3:
            throw new IllegalArgumentException();
         case 0:
         case 1:
         case 2:
         case 4:
         default:
            return (this.getCoverage(wafs, considerSerialBypass) & coverageType) != 0;
      }
   }

   public static final int determineRadioCoverage() {
      return determineRadioCoverage(RadioInfo.getSupportedWAFs());
   }

   private static final int determineRadioCoverage(int wafs) {
      int coverage = 0;
      wafs &= RadioInfo.getActiveWAFs();
      DataServices dataServices = DataServices.getInstance();
      if (dataServices.isDataServicesEnabled()) {
         if ((wafs & 2) == 2 && RadioInfo.getSignalLevel(2) != -256) {
            int networkService = RadioInfo.getNetworkService();

            label375:
            try {
               if ((networkService & 4) != 0) {
                  coverage |= 1;
                  if (RadioInfo.isPDPContextActive(RadioInfo.getAccessPointNumber(""))) {
                     coverage |= 6;
                  }
               }
            } finally {
               break label375;
            }
         }

         if ((wafs & 8) == 8 && RadioInfo.getSignalLevel(8) != -256) {
            int networkService = RadioInfo.getNetworkService();
            boolean tFlag = (networkService & 4) != 0;
            boolean lFlag = tFlag && RadioInfo.isPDPContextActive(0);
            if (lFlag) {
               coverage |= 7;
            } else if (tFlag) {
               coverage |= 1;
            }
         }

         if ((wafs & 1) == 1 && RadioInfo.getSignalLevel(1) != -256) {
            int networkService = RadioInfo.getNetworkService();
            if ((networkService & 4) != 0) {
               coverage |= 1;
               if (GPRSInfo.getGPRSState() != 0) {
                  label357:
                  try {
                     String emailAPN = null;
                     HostRoutingInfo hri = HRUtils.getDefaultHRT().getActiveHri();
                     if (hri instanceof GprsHRI) {
                        emailAPN = ((GprsHRI)hri).getApn();
                     }

                     if (emailAPN != null || IIF_APN != null) {
                        String apn = null;
                        synchronized (_apnIdLock) {
                           if (_lastEmailApnId != -1 && RadioInfo.isPDPContextActive(_lastEmailApnId)) {
                              apn = RadioInfo.getAccessPointName(_lastEmailApnId);
                              if (emailAPN != null && emailAPN.equalsIgnoreCase(apn) || IIF_APN != null && IIF_APN.equalsIgnoreCase(apn)) {
                                 coverage |= 6;
                              }
                           }

                           if ((coverage & 6) != 6) {
                              _lastEmailApnId = -1;

                              for (int apnId = 0; apnId < 7; apnId++) {
                                 if (RadioInfo.isPDPContextActive(apnId)) {
                                    apn = RadioInfo.getAccessPointName(apnId);
                                    if (emailAPN != null && emailAPN.equalsIgnoreCase(apn) || IIF_APN != null && IIF_APN.equalsIgnoreCase(apn)) {
                                       coverage |= 6;
                                       _lastEmailApnId = apnId;
                                       break;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  } finally {
                     break label357;
                  }
               }
            }
         }
      }

      if (dataServices.getMode() != 2 && (wafs & 4) == 4 && RadioInfo.getSignalLevel(4) != -256 && WLAN.isAssociated() != null) {
         coverage |= 1;
         ServiceRouting sr = ServiceRouting.getInstance();
         int[] wifiHandles = sr.getRouteHandles(3);
         if (wifiHandles != null && wifiHandles.length > 0) {
            for (int i = wifiHandles.length - 1; i >= 0; i--) {
               if (sr.isServiceRoutable(null, wifiHandles[i])) {
                  ServiceRoutingProperties srps = sr.getInterface(wifiHandles[i]);
                  if (srps != null) {
                     if ((srps.getLinkCapabilities() & 32) != 0) {
                        coverage |= 4;
                     }

                     if ((srps.getLinkCapabilities() & 2) != 0) {
                        coverage |= 2;
                     }
                  }
               }
            }
         }
      }

      return coverage;
   }

   private static final int determineSerialBypassCoverage() {
      return ServiceRouting.getInstance().isSerialBypassActive() ? 2 : 0;
   }

   private final int determineRoutableServiceRecords() {
      int routableServiceRecords = 1;
      if (this._ipppServiceRecords == null) {
         return routableServiceRecords;
      }

      ServiceRouting sr = ServiceRouting.getInstance();

      for (int i = this._ipppServiceRecords.length - 1; i >= 0; i--) {
         ServiceRecord record = this._ipppServiceRecords[i];
         String uid = record.getUid();
         if (record.isValid() && sr.isServiceRoutable(uid, -1)) {
            if (this._ipppServiceRecordType[i] == 0) {
               routableServiceRecords |= 2;
            } else if (this._ipppServiceRecordType[i] == 1) {
               routableServiceRecords |= 4;
            }
         }
      }

      return routableServiceRecords;
   }

   private final void updateIpppServiceRecords() {
      this._ipppServiceRecords = ServiceBook.getSB().findRecordsByCid(IPPP);
      if (this._ipppServiceRecords == null) {
         this._ipppServiceRecordType = null;
      } else {
         this._ipppServiceRecordType = new int[this._ipppServiceRecords.length];

         for (int i = this._ipppServiceRecords.length - 1; i >= 0; i--) {
            ServiceRecord record = this._ipppServiceRecords[i];

            try {
               this._ipppServiceRecordType[i] = new SBApplicationData(record).getValueAsInt(6);
            } finally {
               continue;
            }
         }
      }
   }

   private final void updateRadioCoverageAndNotifyListeners(int wafs) {
      if ((wafs & 2) == 2) {
         this._cdmaRadioCoverage = determineRadioCoverage(2);
      }

      if ((wafs & 4) == 4) {
         this._wlanRadioCoverage = determineRadioCoverage(4);
      }

      if ((wafs & 8) == 8) {
         this._idenRadioCoverage = determineRadioCoverage(8);
      }

      if ((wafs & 1) == 1) {
         this._3gppRadioCoverage = determineRadioCoverage(1);
      }

      this.updateRoutableServiceRecordsAndNotifyListeners();
   }

   private final void updateRoutableServiceRecordsAndNotifyListeners() {
      this._routableServiceRecords = this.determineRoutableServiceRecords();
      this.updateTotalCoverageAndNotifyListeners();
   }

   private final void updateTotalCoverageAndNotifyListeners() {
      int newTotalCoverage = this.getCoverage(RadioInfo.getSupportedWAFs(), true);
      if (newTotalCoverage != this._totalCoverage) {
         this._totalCoverage = newTotalCoverage;
         Message msg = new Message(9, 0, newTotalCoverage);
         ApplicationManagerInternal appManager = (ApplicationManagerInternal)ApplicationManager.getApplicationManager();
         appManager.postMessage(msg);
      }
   }

   @Override
   public final synchronized void serviceRoutingStateChanged(String service, boolean serviceState) {
      if ((RadioInfo.getSupportedWAFs() & 4) == 4) {
         this._wlanRadioCoverage = determineRadioCoverage(4);
      }

      this._serialBypassCoverage = determineSerialBypassCoverage();
      this.updateRoutableServiceRecordsAndNotifyListeners();
   }

   @Override
   public final synchronized void serviceRouteStateChanged(int routeHandle, boolean routeState) {
      if (routeHandle != -1) {
         ServiceRoutingProperties srProps = ServiceRouting.getInstance().getInterface(routeHandle);
         if (srProps != null) {
            switch (srProps.getLinkType()) {
               case 0:
                  return;
               case 1:
               default:
                  int wafs = RadioInfo.getSupportedWAFs();
                  wafs &= 11;
                  if (wafs != 0) {
                     this.updateRadioCoverageAndNotifyListeners(wafs);
                     return;
                  }
                  break;
               case 2:
                  this._serialBypassCoverage = determineSerialBypassCoverage();
                  this.updateRoutableServiceRecordsAndNotifyListeners();
                  return;
               case 3:
                  if ((RadioInfo.getSupportedWAFs() & 4) == 4) {
                     this.updateRadioCoverageAndNotifyListeners(4);
                     return;
                  }
            }
         }
      }
   }

   @Override
   public final void serviceRoutingCapabilitiesChanged(String service) {
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4220058463650496006L || guid == 2522898683889177438L || guid == 8288627527798139133L) {
         synchronized (this) {
            this.updateIpppServiceRecords();
            this.updateRoutableServiceRecordsAndNotifyListeners();
         }
      } else if (guid == -3556743465989743742L) {
         synchronized (this) {
            this.updateRadioCoverageAndNotifyListeners(RadioInfo.getSupportedWAFs());
         }
      }
   }

   static {
      byte[] data = Branding.getData(13824);
      if (data != null) {
         IIF_APN = new String(data);
      }
   }
}
