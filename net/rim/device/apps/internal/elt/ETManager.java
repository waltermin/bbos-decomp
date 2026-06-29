package net.rim.device.apps.internal.elt;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationProvider;
import net.rim.device.api.gps.GPS;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.internal.synchronization.ota.api.SyncAgent;
import net.rim.device.internal.synchronization.ota.api.SyncAgentListener;
import net.rim.device.internal.synchronization.ota.api.SyncAgentUrl;
import net.rim.device.internal.synchronization.ota.service.Configuration;
import net.rim.device.internal.synchronization.ota.service.ServicesConfigurationManager;
import net.rim.device.internal.synchronization.ota.session.SessionManager;

final class ETManager implements SyncAgentListener {
   private ELTState _data;
   private ETCollection _etCollection = null;
   private ETManager$GetLocationThread _getLocationThread;
   private int _syncBatchInterval = 600000;
   private long _syncTimestamp;
   private int _gpsFixTime;
   private SessionManager _mgr;
   private Application _app;
   private ETManager$GPSFixThread _gpsFixThread;
   public static final long PARAM_TRACKING_ENABLED_BY_USER = -5569314734131765067L;
   private static final long GUID = -4208570663947666709L;

   public final void locationError() {
      synchronized (this) {
         this.notify();
      }
   }

   public final void newLocation(Location location) {
      synchronized (this) {
         this.notify();
      }
   }

   final void commit() {
      RIMPersistentStore.getPersistentObject(-4208570663947666709L).commit();
   }

   public final void enableTracking() {
      Logger.logEvent(this, "enableTracking", true);
      this._etCollection.setEnabledbyUser(true);
      if (!this._data.isEnabledByUser()) {
         this._data.setEnabledByUser(true);
         this.commit();
      }

      this._gpsFixTime = 0;
      this._syncTimestamp = 0;
      this.registerSync();
      this._gpsFixThread = new ETManager$GPSFixThread(this);
      this._gpsFixThread.start();
   }

   public final void disableTracking() {
      this._etCollection.setEnabledbyUser(false);
      if (this._data.isEnabledByUser()) {
         this._data.setEnabledByUser(false);
         this.commit();
      }

      if (this._getLocationThread != null) {
         Logger.logEvent(this, ((StringBuffer)(new Object("disableTracking, started="))).append(this._getLocationThread.isStarted()).toString(), true);
         if (this._getLocationThread.isStarted()) {
            this.registerSync();
         }
      } else {
         Logger.logError(this, "disableTracking(), getLocationThread is null!");
      }

      if (this._mgr != null) {
         this._mgr.triggerSync(false);
      }

      this.stopELT();
   }

   final boolean isEnabledByUser() {
      return this._data.isEnabledByUser();
   }

   final boolean isEnabledByITPolicy() {
      return this._data.isEnabledByITPolicy();
   }

   @Override
   public final void onSyncAgentEvent(int eventId, Object anObject) {
      if (eventId == 22 && anObject instanceof Object) {
         SyncAgentUrl obj = (SyncAgentUrl)anObject;
         if (this._syncTimestamp == 0 && obj.getDatabaseName().equals(this._etCollection.getSyncName())) {
            Logger.logEvent(this, ((StringBuffer)(new Object("syncTimeSet: "))).append(System.currentTimeMillis()).toString(), false);
            if (this._data.isEnabledByUser() && this._data.isEnabledByITPolicy()) {
               if (this._getLocationThread != null) {
                  this._syncTimestamp = System.currentTimeMillis();
                  this._getLocationThread.notifyLock();
                  return;
               }
            } else {
               Logger.logEvent(this, "onSyncAgentEvent, calling stopELT()", false);
               this.stopELT();
            }
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void stopELT() {
      Logger.logEvent(this, "disableTracking, notify GPS update to stop.", false);

      label81:
      try {
         synchronized (this) {
            this.notify();
         }
      } catch (Throwable var11) {
         Logger.logError(this, ((StringBuffer)(new Object("notify exception: "))).append(e).toString());
         break label81;
      }

      Logger.logEvent(this, ((StringBuffer)(new Object("getLocationThread: "))).append(this._getLocationThread).toString(), false);
      if (this._getLocationThread != null && this._getLocationThread.isStarted()) {
         this._getLocationThread.stop();
      }

      Logger.logEvent(this, ((StringBuffer)(new Object("gpsFixThread: "))).append(this._gpsFixThread).toString(), false);
      if (this._gpsFixThread != null) {
         this._gpsFixThread.stopAttempt();
      }

      if (this._getLocationThread != null) {
         Logger.logEvent(this, "to kill app", false);
         this._getLocationThread.killApp();
      }

      label73:
      try {
         ApplicationRegistry.getApplicationRegistry().remove(7659638648801846908L);
      } catch (Throwable var10) {
         Logger.logError(this, ((StringBuffer)(new Object("remove app exception: "))).append(e).toString());
         break label73;
      }

      this._gpsFixThread = null;
      this._getLocationThread = null;
   }

   private final void registerSync() {
      long sid = SyncAgent.getSingletonInstance().getDefaultSid();
      SessionManager mgr = null;
      Configuration config = null;
      ServicesConfigurationManager serviceManager = ServicesConfigurationManager.getSingletonInstance();
      config = serviceManager.getDefaultConfiguration();
      if (config == null) {
         config = serviceManager.getConfiguration(sid);
      }

      if (config == null) {
         config = (Configuration)(new Object(sid));
      }

      serviceManager.setConfiguration(config);
      int batchTime = (int)config.getBatchTimeout();
      if (batchTime > 0) {
         this._syncBatchInterval = batchTime;
      }

      mgr = SessionManager.getSessionManagerFor(sid);
      if (mgr != null) {
         mgr.fetchConfiguration();
      }

      SyncAgent.getSingletonInstance().registerListener(this);
      Logger.logEvent(this, ((StringBuffer)(new Object("registerSync, sid: "))).append(sid).append(" config: ").append(config).toString(), true);
      this._mgr = mgr;
   }

   static final ELTState getELTData() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-4208570663947666709L);
      synchronized (persistentObject) {
         ELTState data = (ELTState)persistentObject.getContents();
         if (data == null) {
            data = new ELTState();
            persistentObject.setContents(data, 51);
            persistentObject.commit();
         }

         return data;
      }
   }

   ETManager(Application app) {
      this._app = app;
      this._data = getELTData();
      this._etCollection = ETCollection.getInstance();
      this._getLocationThread = new ETManager$GetLocationThread(this);
      this._data.setEnabledByUser(this._etCollection.isEnabledByUser());
      if (this._data.isEnabledByITPolicy() && this._data.isEnabledByUser()) {
         this.enableTracking();
      }

      this.commit();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean updateLocation(boolean initialAttempt, int timeoutSeconds) {
      LocationProvider provider = this.createLocationProvider(false);
      if (provider == null) {
         provider = this.createLocationProvider(true);
      }

      Logger.logEvent(
         this,
         ((StringBuffer)(new Object("location update, initialAttempt: ")))
            .append(initialAttempt)
            .append(", locationProvider=")
            .append(provider)
            .append(" enabledByUser=")
            .append(this._data.isEnabledByUser())
            .append(" enabledByITPolicy=")
            .append(this._data.isEnabledByITPolicy())
            .toString(),
         true
      );
      boolean acquired = false;
      if (provider != null) {
         long timeIn = System.currentTimeMillis();

         label152:
         try {
            SingleFixListener listener = new SingleFixListener(this, 2);
            provider.setLocationListener(listener, 1, 1, 1);
            synchronized (this) {
               label141:
               try {
                  if (initialAttempt) {
                     this.wait();
                  } else {
                     this.wait(timeoutSeconds * 1000);
                  }
               } finally {
                  break label141;
               }
            }

            Location location = listener.getCurrentLocation();
            if (location != null && this._data.isEnabledByUser() && this._data.isEnabledByITPolicy()) {
               synchronized (this._app) {
                  acquired = true;
                  this._etCollection.addOrUpdate(location);
                  if (this._mgr != null) {
                     this._mgr.triggerSync(false);
                  }
               }
            } else {
               timeIn = 0;
            }
         } catch (Throwable var23) {
            timeIn = 0;
            Logger.logError(this, ((StringBuffer)(new Object("updateLocation exception: "))).append(e).toString());
            break label152;
         }

         provider.setLocationListener(null, 0, 0, 0);
         provider.reset();
         if (timeIn > 0) {
            this._gpsFixTime = (int)(System.currentTimeMillis() - timeIn);
         }

         Logger.logEvent(
            this,
            ((StringBuffer)(new Object("location update, initialAttempt: ")))
               .append(initialAttempt)
               .append(", fixTime=")
               .append(this._gpsFixTime)
               .append(" enabledByUser=")
               .append(this._data.isEnabledByUser())
               .append(" enabledByITPolicy=")
               .append(this._data.isEnabledByITPolicy())
               .toString(),
            false
         );
         if (initialAttempt
            && this._getLocationThread != null
            && !this._getLocationThread.isStarted()
            && this._data.isEnabledByUser()
            && this._data.isEnabledByITPolicy()) {
            this._getLocationThread.start();
         }
      }

      return acquired;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final LocationProvider createLocationProvider(boolean forceAutonomous) {
      try {
         Criteria criteria = (Criteria)(new Object());
         criteria.setAddressInfoRequired(false);
         criteria.setAltitudeRequired(false);
         criteria.setPreferredResponseTime(forceAutonomous ? 60 : 16);
         criteria.setSpeedAndCourseRequired(true);
         criteria.setPreferredPowerConsumption(3);
         criteria.setHorizontalAccuracy(32);
         criteria.setVerticalAccuracy(32);
         int capability = GPS.getAidCapability();
         boolean assistModeSupported = (capability & 2) != 0;
         boolean cellModeSupported = (capability & 1) != 0;
         boolean costing = !forceAutonomous ? assistModeSupported || cellModeSupported : false;
         Logger.logEvent(this, ((StringBuffer)(new Object("costing: "))).append(costing).toString(), false);
         criteria.setCostAllowed(costing);
         LocationProvider provider = LocationProvider.getInstance(criteria);
         Logger.logEvent(this, ((StringBuffer)(new Object("GetLocationProvider: "))).append(provider).toString(), false);
         return provider;
      } catch (Throwable var9) {
         Logger.logError(this, e.getMessage());
         return null;
      }
   }
}
