package net.rim.device.api.memorycleaner;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.listener.EventListenerManager;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Memory;

public final class MemoryCleanerManager implements HolsterListener, RealtimeClockListener, GlobalEventListener, SyncEventListener, SystemListener, Comparator {
   private EventListenerManager _listeners;
   private MemoryCleanerSettings _settings;
   private PersistentObject _settingsHolder;
   private long _lastProcessedUserAction;
   private boolean _userEnabledSecureOldObjects;
   private boolean _memoryCleanerSecureOldObjects;
   private boolean _persistentContentSecureOldObjects;
   private boolean _cryptoAPISecureOldObjects;
   private boolean _SMIMESecureOldObjects;
   private boolean _PGPSecureOldObjects;
   private boolean _registeredListeners;
   private boolean _otaSyncOperationStopped;
   private MemoryCleanerManager$MemoryCleanerSyncItem _syncItem;
   private MemoryCleanerManager$ClipboardMemoryCleaner _clipboardMemoryCleaner;
   private static final long MINIMUM_TIMEOUT_SECONDS;
   private static final long PERSISTENT_STORE_KEY;
   private static final long SINGLETON_ID;
   private static final long LOGGER_GUID;
   public static final long SHOW_APP_ON_RIBBON_CHANGED;
   private static final int[] EVENT_LOGGER_CODES = new int[]{
      1231964268,
      1231311981,
      1400460148,
      1400460144,
      1299009650,
      1416446824,
      1148603499,
      1349665644,
      1349993582,
      1330921812,
      1346596718,
      1230262339,
      -804650982,
      39811448,
      57570299,
      62029285,
      33576464,
      917716,
      8541952,
      23050683,
      1717248,
      1009688,
      53248,
      54702544,
      20841735,
      67108045,
      34785653,
      4096,
      21642481,
      1,
      5259280,
      53116895,
      40564460,
      50597953,
      1057109,
      262176,
      0,
      1572880,
      4329473,
      -804651004,
      1000,
      100,
      10,
      1,
      1866989824,
      727916,
      1094676481,
      1655138688
   };

   public final void registerWithSyncManager() {
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.enableSynchronization(this._syncItem);
      }
   }

   public final synchronized void setMemoryCleanerSecureOldObjects(boolean flag) {
      this._memoryCleanerSecureOldObjects |= flag;
      this.setSecureOldObjects();
   }

   public final synchronized void setPersistentContentSecureOldObjects(boolean flag) {
      this._persistentContentSecureOldObjects = flag;
      this.setSecureOldObjects();
   }

   public final synchronized void setCryptoAPISecureOldObjects(boolean flag) {
      byte fipsLevel = ITPolicy.getByte(24, 39, (byte)1);
      if (fipsLevel > 1) {
         this._cryptoAPISecureOldObjects |= flag;
         this.setSecureOldObjects();
      }
   }

   public final synchronized void setSMIMESecureOldObjects(boolean flag) {
      this._SMIMESecureOldObjects = flag;
      this.setSecureOldObjects();
   }

   public final synchronized void setPGPSecureOldObjects(boolean flag) {
      this._PGPSecureOldObjects = flag;
      this.setSecureOldObjects();
   }

   public final boolean userEnabled() {
      return this._userEnabledSecureOldObjects
         && !this._memoryCleanerSecureOldObjects
         && !this._persistentContentSecureOldObjects
         && !this._cryptoAPISecureOldObjects
         && !this._SMIMESecureOldObjects
         && !this._PGPSecureOldObjects;
   }

   public final boolean enabled() {
      return this._userEnabledSecureOldObjects
         || this._memoryCleanerSecureOldObjects
         || this._persistentContentSecureOldObjects
         || this._cryptoAPISecureOldObjects
         || this._SMIMESecureOldObjects
         || this._PGPSecureOldObjects;
   }

   public final synchronized void addListener(MemoryCleanerListener listener, boolean weak, boolean enable) {
      if (listener == null) {
         throw new IllegalArgumentException();
      }

      this._listeners.add(listener, weak);
      this.setMemoryCleanerSecureOldObjects(enable);
   }

   public final synchronized void removeListener(MemoryCleanerListener listener) {
      this._listeners.remove(listener);
   }

   public final void setUserCleanEnabled(boolean setting) {
      synchronized (this) {
         this._settings._userCleanEnabled = setting;
         this._settingsHolder.commit();
      }

      this.setUserEnabledSecureOldObjects(setting);
      this._syncItem.fireSyncItemUpdated();
   }

   public final synchronized boolean getUserCleanEnabled() {
      return this._settings._userCleanEnabled;
   }

   public final void setCleanWhenHolstered(boolean setting) {
      synchronized (this) {
         setting |= ITPolicy.getBoolean(27, 3, false);
         this._settings._cleanWhenHolstered = setting;
         this._settingsHolder.commit();
      }

      this._syncItem.fireSyncItemUpdated();
   }

   public final synchronized boolean getCleanWhenHolstered() {
      return this._settings._cleanWhenHolstered;
   }

   public final void setShowAppOnRibbon(boolean setting) {
      synchronized (this) {
         this._settings._showAppOnRibbon = setting;
         this._settingsHolder.commit();
      }

      this._syncItem.fireSyncItemUpdated();
      RIMGlobalMessagePoster.postGlobalEvent(5924166216341050021L);
   }

   public final synchronized boolean getShowAppOnRibbon() {
      return this._settings._showAppOnRibbon;
   }

   public final void setCleanWhenIdle(boolean setting) {
      synchronized (this) {
         setting |= ITPolicy.getBoolean(27, 2, false);
         this._settings._cleanWhenIdle = setting;
         this._settingsHolder.commit();
      }

      this._syncItem.fireSyncItemUpdated();
   }

   public final synchronized boolean getCleanWhenIdle() {
      return this._settings._cleanWhenIdle;
   }

   public final void setIdleTimeout(long millis) {
      synchronized (this) {
         long seconds = millis / 1000;
         long maxTimeoutSeconds = ITPolicy.getInteger(27, 1, 60) * 60;
         seconds = Math.min(seconds, maxTimeoutSeconds);
         seconds = Math.max(seconds, 60);
         this._settings._idleTimeoutSeconds = seconds;
         this._settingsHolder.commit();
      }

      this._syncItem.fireSyncItemUpdated();
   }

   public final synchronized long getIdleTimeout() {
      return this._settings._idleTimeoutSeconds * 1000;
   }

   public final synchronized MemoryCleanerListener[] getListeners() {
      MemoryCleanerListener[] listeners = (MemoryCleanerListener[])this._listeners.getListeners(new MemoryCleanerListener[0]);
      Arrays.sort(listeners, this);
      return listeners;
   }

   public final boolean isUpdateComplete() {
      return this._listeners.isUpdateComplete();
   }

   public final void cleanAll() {
      this.notifyAllListeners(7);
   }

   public final void cleanPersistentContent() {
      this.notifyAllListeners(10);
   }

   @Override
   public final int compare(Object o1, Object o2) {
      String s1;
      try {
         s1 = ((MemoryCleanerListener)o1).getDescription();
      } catch (Throwable t) {
         s1 = null;
      }

      String s2;
      try {
         s2 = ((MemoryCleanerListener)o2).getDescription();
      } catch (Throwable t) {
         s2 = null;
      }

      if (s1 == s2) {
         return 0;
      } else if (s1 == null) {
         return 1;
      } else {
         return s2 == null ? -1 : StringUtilities.compareToIgnoreCase(s1, s2, 1701707776);
      }
   }

   @Override
   public final void inHolster() {
      if (this._settings._cleanWhenHolstered) {
         this.notifyAllListeners(0);
      }
   }

   @Override
   public final void outOfHolster() {
   }

   @Override
   public final void clockUpdated() {
      if (this._settings._cleanWhenIdle) {
         long idleTimeSeconds = DeviceInfo.getIdleTime();
         if (idleTimeSeconds > this._settings._idleTimeoutSeconds - 60) {
            long currentTimeMillis = System.currentTimeMillis();
            long lastUserAction = currentTimeMillis - idleTimeSeconds * 1000;
            if (lastUserAction > this._lastProcessedUserAction) {
               this._lastProcessedUserAction = lastUserAction + 1000;
               this.notifyAllListeners(1);
            }
         }
      }

      if (this._otaSyncOperationStopped) {
         this._otaSyncOperationStopped = false;
         this.notifyAllListeners(9);
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8877632280522743328L || guid == 3596208183088439728L) {
         this.notifyAllListeners(5);
         this._lastProcessedUserAction = 0;
      } else if (guid == -7131874474196788121L) {
         this.notifyAllListeners(6);
      } else {
         if (guid == 8508406279413621091L || guid == -594020114676189989L) {
            this.setCleanWhenHolstered(this.getCleanWhenHolstered());
            this.setCleanWhenIdle(this.getCleanWhenIdle());
            this.setIdleTimeout(this.getIdleTimeout());
            this.notifyAllListeners(11);
         }
      }
   }

   @Override
   public final void syncEventOccurred(int eventId, Object object) {
      switch (eventId) {
         case 1:
         default:
            this.notifyAllListeners(2);
            return;
         case 2:
            this.notifyAllListeners(3);
            return;
         case 4:
            this._otaSyncOperationStopped = true;
         case 0:
         case 3:
      }
   }

   @Override
   public final void powerOff() {
      this.notifyAllListeners(8);
   }

   @Override
   public final void powerUp() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   private final synchronized void setUserEnabledSecureOldObjects(boolean flag) {
      this._userEnabledSecureOldObjects = flag;
      this.setSecureOldObjects();
   }

   public static final MemoryCleanerManager getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      MemoryCleanerManager manager = (MemoryCleanerManager)registry.getOrWaitFor(-63698109761663168L);
      if (manager == null) {
         manager = new MemoryCleanerManager();
         registry.put(-63698109761663168L, manager);
      }

      return manager;
   }

   private final void resetOptions() {
      this._settings._userCleanEnabled = this.enabled();
      this._settings._cleanWhenHolstered = true;
      this._settings._cleanWhenIdle = true;
      this._settings._idleTimeoutSeconds = 300;
      this._settings._showAppOnRibbon = false;
   }

   private MemoryCleanerManager() {
      EventLogger.register(-3818033069674138067L, "net.rim.memclean", 2);
      this._listeners = new EventListenerManager();
      this._settingsHolder = RIMPersistentStore.getPersistentObject(-8102403034661658129L);
      if (this._settingsHolder == null) {
         throw new RuntimeException();
      }

      this._settings = (MemoryCleanerSettings)this._settingsHolder.getContents();
      if (this._settings == null) {
         this._settings = new MemoryCleanerSettings();
         this._settingsHolder.setContents(this._settings, 51);
         this.resetOptions();
         this._settingsHolder.commit();
      }

      this._syncItem = new MemoryCleanerManager$MemoryCleanerSyncItem(this);
      this._clipboardMemoryCleaner = new MemoryCleanerManager$ClipboardMemoryCleaner();
      this.addListener(this._clipboardMemoryCleaner, false, false);
   }

   private final void notifyAllListeners(int event) {
      if (this.enabled()) {
         this._listeners.update(new MemoryCleanerManager$MemoryCleanerEvent(event));
      }
   }

   private final void setSecureOldObjects() {
      if (this.enabled()) {
         if (!this._registeredListeners) {
            Memory.setSecureOldObjects(true);
            Proxy.getInstance().invokeLater(new MemoryCleanerManager$AddListeners(this, null));
            this._registeredListeners = true;
            return;
         }
      } else if (this._registeredListeners) {
         Memory.setSecureOldObjects(false);
         Proxy.getInstance().invokeLater(new MemoryCleanerManager$RemoveListeners(this, null));
         this._registeredListeners = false;
      }
   }
}
