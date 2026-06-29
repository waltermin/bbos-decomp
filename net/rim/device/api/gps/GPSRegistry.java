package net.rim.device.api.gps;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.vm.Process;

public final class GPSRegistry {
   private GPSRegistry$GPSListenerImpl _gpsListener;
   private IntIntHashtable _autoFixConsumers;
   private IntIntHashtable _assistFixConsumers;
   private int _autoGCD;
   private int _assistGCD;
   private IntHashtable _pdeTable;
   private IntHashtable _criteriaTable;
   private int _gcd;
   private boolean _simulateGPSPuck;
   private GPSLocationStandard _standardLocation;
   private GPSLocationExtended _extendedLocation;
   private boolean _pdeRequestSuccess;
   private long _lastLogEntryTime;
   Object _locationLock = new Object();
   private static final long REGISTRY_NAME = 131144863201889171L;
   private static final long LAST_FIX_TIME = 6548244683844264658L;
   public static final long FIX_REQUESTED = -2101050060249085778L;
   private static GPSRegistry _registry;
   private static int _maxGPSInterval = 9;
   private static Object _pdeLock = new Object();
   private static final long LOG_ENTRY_INTERVAL = 60000L;
   private static final long EVENT_LOGGER_GUID = 2845560962249627645L;
   private static final String EVENT_LOGGER_NAME = "Location API";
   public static final long LOCATION_EVENT = 5678354684824604352L;
   public static final long GPS_TEMPORARILY_UNAVAILABLE = -7999774137434187609L;
   private static PersistentObject _lastFixTimeStore;
   private static long[] _lastFixTimes;

   private GPSRegistry() {
      this._autoFixConsumers = new IntIntHashtable();
      this._assistFixConsumers = new IntIntHashtable();
      this._standardLocation = new GPSLocationStandard();
      this._extendedLocation = new GPSLocationExtended();
      EventLogger.register(2845560962249627645L, "Location API", 2);
      this._gpsListener = new GPSRegistry$GPSListenerImpl(this);
      GPS.addListener(Application.getApplication(), this._gpsListener);
      this._pdeTable = new IntHashtable();
      this._criteriaTable = new IntHashtable();
   }

   public static final void initialize() {
      if (DeviceInfo.isSimulator()) {
         EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
         synchronized (dispatchManager) {
            if (dispatchManager.getDispatcher(22) == null) {
               dispatchManager.setDispatcher(22, new FledgeEventDispatcher());
            }
         }

         Proxy proxy = Proxy.getInstance();
         proxy.addListener(22, _registry);
      }
   }

   public static final GPSRegistry getInstance() {
      return _registry;
   }

   public final boolean getSimulateGPSPuck() {
      return this._simulateGPSPuck;
   }

   public final void setSimulateGPSPuck(boolean b) {
      this._simulateGPSPuck = b;
   }

   public final Object getLocationLock() {
      return this._locationLock;
   }

   public final GPSLocation getLocation(int mode) {
      if (mode == 0) {
         return this._standardLocation.getUTCTime() > this._extendedLocation.getUTCTime() ? this._standardLocation : this._extendedLocation;
      }

      if (isVerizon() && mode == 1) {
         int processId = Application.getApplication().getProcessId();
         GPSRegistry$PDEInfoStatus pdeInfoStatus = (GPSRegistry$PDEInfoStatus)this._pdeTable.get(processId);
         if (pdeInfoStatus == null || !pdeInfoStatus.getCredStatus()) {
            return null;
         }
      }

      return this._extendedLocation;
   }

   public final long getLastFixTime(int mode) {
      switch (mode) {
         case -1:
            return 0;
         case 0:
            return _lastFixTimes[2];
         case 1:
            return _lastFixTimes[1];
         case 2:
         default:
            return _lastFixTimes[0];
      }
   }

   public static final boolean isVerizon() {
      return Branding.getVendorId() == 105 || Branding.getVendorId() == 226;
   }

   public final boolean getCredentialStatus(int processId) {
      GPSRegistry$PDEInfoStatus pdeInfoStatus = (GPSRegistry$PDEInfoStatus)this._pdeTable.get(processId);
      return pdeInfoStatus != null ? pdeInfoStatus.getCredStatus() : false;
   }

   public final synchronized boolean startLocationUpdate(int interval, int mode, GPS$AppCriteria criteria) {
      if (!GPS.isSupportedOnCurrentNetwork()) {
         return false;
      }

      if (criteria == null) {
         criteria = new GPS$AppCriteria();
      }

      if (mode == 2) {
         _lastFixTimes[0] = System.currentTimeMillis();
      } else if (mode == 1) {
         _lastFixTimes[1] = System.currentTimeMillis();
      } else {
         _lastFixTimes[2] = System.currentTimeMillis();
      }

      _lastFixTimeStore.commit();
      RIMGlobalMessagePoster.postGlobalEvent(-2101050060249085778L, mode, 0);
      if (mode == 0) {
         return GPS.startLocationUpdate(0, mode, 0, null);
      }

      int processId = Application.getApplication().getProcessId();
      if (RadioInfo.getNetworkType() == 4 && mode == 1) {
         this._criteriaTable.put(processId, criteria);
         this.setPDEInfo(processId);
      }

      IntIntHashtable table = null;
      if (mode == 2) {
         table = this._autoFixConsumers;
      } else {
         if (mode != 1) {
            return false;
         }

         table = this._assistFixConsumers;
      }

      if (interval == 0) {
         if (table.size() == 0) {
            return GPS.startLocationUpdate(3, mode, 0, criteria);
         } else if (mode == 1) {
            return GPS.startLocationUpdate(3, mode, this._assistGCD, criteria);
         } else {
            return mode == 2 ? GPS.startLocationUpdate(3, mode, this._autoGCD, criteria) : false;
         }
      } else {
         if (!table.contains(processId)) {
            ApplicationProcess process = (ApplicationProcess)Process.currentProcess();
            process.addCleanupRunnable(new GPSRegistry$GPSCleanerRunnable(this, processId, mode));
         }

         table.put(processId, interval);
         int gcd = this.calculateGCD(table.elements());
         if (mode == 2) {
            this._autoFixConsumers = table;
            this._autoGCD = gcd;
         } else if (mode == 1) {
            this._assistFixConsumers = table;
            this._assistGCD = gcd;
         }

         this._gcd = this.getCommonGCD();
         this._gpsListener._errorCount = 0;
         return GPS.startLocationUpdate(3, mode, gcd, criteria);
      }
   }

   private final synchronized void restartCDMAAssistedLocationUpdate() {
      if (this._assistFixConsumers.size() != 0) {
         this._assistGCD = this.calculateGCD(this._assistFixConsumers.elements());
         this._gcd = this.getCommonGCD();
         GPS$AppCriteria criteria = null;
         IntEnumeration en = this._assistFixConsumers.keys();
         if (en.hasMoreElements()) {
            int pid = en.nextElement();
            this.setPDEInfo(pid);
            criteria = (GPS$AppCriteria)this._criteriaTable.get(pid);
         }

         GPS.startLocationUpdate(3, 1, this._assistGCD, criteria);
      }
   }

   private final int calculateGCD(int a, int b) {
      while (b != 0) {
         int t = b;
         b = a % b;
         a = t;
      }

      return a;
   }

   private final int calculateGCD(IntEnumeration en) {
      int gcd = 0;

      while (en.hasMoreElements()) {
         if (gcd == 0) {
            gcd = en.nextElement();
         } else {
            gcd = this.calculateGCD(gcd, en.nextElement());
         }
      }

      if (gcd > _maxGPSInterval) {
         for (int i = _maxGPSInterval; i > 0; i--) {
            if (gcd % i == 0) {
               return i;
            }
         }
      }

      return gcd;
   }

   public final synchronized boolean stopLocationUpdate(int mode) {
      if (mode == 0) {
         return true;
      }

      int processId = Application.getApplication().getProcessId();
      return this.stopLocationUpdate(mode, processId);
   }

   private final boolean stopLocationUpdate(int mode, int processId) {
      IntIntHashtable table = null;
      if (mode == 2) {
         table = this._autoFixConsumers;
      } else {
         if (mode != 1) {
            return false;
         }

         table = this._assistFixConsumers;
      }

      if (!table.containsKey(processId)) {
         if (table.size() == 0) {
            GPS.stopLocationUpdate(mode);
         }

         return true;
      } else {
         table.remove(processId);
         if (table.size() == 0) {
            if (mode == 2) {
               this._autoGCD = 0;
            } else if (mode == 1) {
               this._assistGCD = 0;
            }

            return GPS.stopLocationUpdate(mode);
         } else {
            int gcd = this.calculateGCD(table.elements());
            if (mode == 2) {
               this._autoFixConsumers = table;
               this._autoGCD = gcd;
            } else if (mode == 1) {
               this._assistFixConsumers = table;
               this._assistGCD = gcd;
            }

            this._criteriaTable.remove(processId);
            this._gcd = this.getCommonGCD();
            GPS$AppCriteria criteria = new GPS$AppCriteria();
            if (RadioInfo.getNetworkType() == 4 && mode == 1) {
               this._pdeTable.remove(processId);
               if (this._assistFixConsumers.size() != 0) {
                  IntEnumeration en = this._assistFixConsumers.keys();
                  if (en.hasMoreElements()) {
                     int pid = en.nextElement();
                     this.setPDEInfo(pid);
                     criteria = (GPS$AppCriteria)this._criteriaTable.get(pid);
                  }
               }
            }

            GPS.startLocationUpdate(3, mode, gcd, criteria);
            return true;
         }
      }
   }

   private final int getCommonGCD() {
      if (this._autoFixConsumers.size() == 0) {
         return this._assistGCD;
      } else {
         return this._assistFixConsumers.size() == 0 ? this._autoGCD : this.calculateGCD(this._autoGCD, this._assistGCD);
      }
   }

   public final boolean addPDEInfo(GPS$GPSPDEInfo pdeInfo) {
      int processId = Application.getApplication().getProcessId();
      GPSRegistry$PDEInfoStatus pdeStatus = new GPSRegistry$PDEInfoStatus(pdeInfo, false, false, null);
      this._pdeTable.put(processId, pdeStatus);
      ApplicationProcess process = (ApplicationProcess)Process.currentProcess();
      process.addCleanupRunnable(new GPSRegistry$1(this, processId));
      return isVerizon() ? true : this.setPDEInfo(pdeInfo);
   }

   private final boolean setPDEInfo(int processId) {
      if (this._pdeTable.size() != 0 && this._pdeTable.containsKey(processId)) {
         GPSRegistry$PDEInfoStatus pdeStatus = (GPSRegistry$PDEInfoStatus)this._pdeTable.get(processId);
         return this.setPDEInfo(pdeStatus.getPDEInfo());
      } else {
         this.logPDEFailure();
         return false;
      }
   }

   private final boolean setPDEInfo(GPS$GPSPDEInfo pdeInfo) {
      this._pdeRequestSuccess = false;
      GPS.setPDEInfo(pdeInfo);
      if (!isVerizon()) {
         synchronized (_pdeLock) {
            try {
               _pdeLock.wait(3000);
            } catch (InterruptedException var5) {
            }

            if (!this._pdeRequestSuccess) {
               this.logPDEFailure();
            }
         }
      }

      return this._pdeRequestSuccess;
   }

   private final void logPDEFailure() {
      long currTime = System.currentTimeMillis();
      if (currTime - this._lastLogEntryTime > 60000) {
         EventLogger.logEvent(2845560962249627645L, "Application must set valid PDE IP and Port to get Location".getBytes(), 0);
         this._lastLogEntryTime = currTime;
      }
   }

   private final int gpsGetLocation(GPSLocationStandard info, int aidMode) {
      int result = 1;

      try {
         if (DeviceInfo.isSimulator() && this._simulateGPSPuck && !GPS.startFledgeGPS()) {
            return result;
         }

         result = GPS.getLocation(info, aidMode);
         if (result == 0) {
            info.setUTCTime(System.currentTimeMillis());
         }

         if (DeviceInfo.isSimulator() && this._simulateGPSPuck) {
            GPS.stopFledgeGPS();
            return result;
         }
      } catch (Exception var5) {
      }

      return result;
   }

   public final int getGCD() {
      return this._gcd;
   }

   private final void notifyListeners(int mode, long event) {
      synchronized (this) {
         if (mode == 2 || mode == 1) {
            if (this._autoFixConsumers.size() > 0) {
               IntEnumeration en = this._autoFixConsumers.keys();

               while (en.hasMoreElements()) {
                  RIMGlobalMessagePoster.postGlobalEvent(en.nextElement(), event, this._gcd, 0, null, null);
               }
            }

            if (this._assistFixConsumers.size() > 0) {
               IntEnumeration en = this._assistFixConsumers.keys();

               while (en.hasMoreElements()) {
                  RIMGlobalMessagePoster.postGlobalEvent(en.nextElement(), event, this._gcd, 0, null, null);
               }
            }
         }
      }
   }

   private final void restartLocationUpdate(int mode) {
      if ((mode != 2 || this._autoFixConsumers.size() != 0) && (mode != 1 || this._assistFixConsumers.size() != 0)) {
         if (RadioInfo.getNetworkType() == 4 && mode == 1) {
            new GPSRegistry$2(this).start();
         } else {
            GPS.startLocationUpdate(3, mode, this._gcd, new GPS$AppCriteria());
         }
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _registry = (GPSRegistry)ar.getOrWaitFor(131144863201889171L);
      if (_registry == null) {
         _registry = new GPSRegistry();
         ar.put(131144863201889171L, _registry);
      }

      _lastFixTimeStore = RIMPersistentStore.getPersistentObject(6548244683844264658L);
      _lastFixTimes = (long[])_lastFixTimeStore.getContents();
      if (_lastFixTimes == null) {
         _lastFixTimes = new long[3];
         _lastFixTimeStore.setContents(_lastFixTimes, 51);
      }

      if (GPS.isSupported()) {
         _maxGPSInterval = GPS.getMaxFixPeriod();
      }
   }
}
