package net.rim.device.apps.internal.browser.core;

import java.util.Vector;
import net.rim.device.api.lowmemory.LowMemoryListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener2;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardStatusListener;
import net.rim.device.api.system.WLANListenerInternal;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.browser.Browser;
import net.rim.device.apps.api.browser.OTAStatusReportService;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.internal.browser.cod.OTAStatusReportSender;
import net.rim.device.apps.internal.browser.options.BrowserConfigChangeListener;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.options.IBrowserProperty;
import net.rim.device.apps.internal.browser.store.FolderEventListener;
import net.rim.device.apps.internal.browser.util.ReregistrationListener;
import net.rim.device.apps.internal.browser.util.ReregistrationManager;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public final class BrowserDaemonRegistry
   implements Browser,
   GlobalEventListener,
   RadioStatusListener,
   WLANListenerInternal,
   LowMemoryListener,
   SyncEventListener,
   SIMCardStatusListener,
   ReregistrationManager,
   ServiceRoutingListener2 {
   private BrowserImpl _instance;
   private Vector _plugInOptions;
   private WeakReference[] _browserStateListeners = new WeakReference[0];
   private WeakReference[] _folderEventListeners = new WeakReference[0];
   private WeakReference[] _urlCollectionListeners = new WeakReference[0];
   private WeakReference[] _configListeners = new WeakReference[0];
   private WeakReference[] _registrationListeners = new WeakReference[0];

   public final void mobilityManagementEvent(int eventCode, int cause) {
   }

   @Override
   public final boolean loadUrl(String url, String uid) {
      this._instance.fetchUrl(url, uid);
      return true;
   }

   @Override
   public final OTAStatusReportService getOTAStatusReportService() {
      return OTAStatusReportSender.getOTAStatusReportSender();
   }

   @Override
   public final boolean showBrowser(String uid) {
      this._instance.activateBrowser(uid);
      return true;
   }

   @Override
   public final String getBISEmailSetupServiceUID() {
      BrowserConfigRecord[] browserConfigs = BrowserConfigRecord.getValidBrowserConfigRecords();

      for (int i = 0; i < browserConfigs.length; i++) {
         if (browserConfigs[i].getPropertyAsInt(12) == 2) {
            return browserConfigs[i].getUid();
         }
      }

      return null;
   }

   @Override
   public final void radioStatus(boolean started) {
      this._instance.radioStatus(started);
   }

   @Override
   public final void networkSuccess() {
      this._instance.networkSuccess();
   }

   @Override
   public final void networkFail(int status, int error, int extendedInfo) {
      this._instance.networkFail(status, error);
   }

   @Override
   public final void networkApChange() {
   }

   @Override
   public final void networkFound(int handle) {
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      try {
         this._instance.eventOccurred(guid, data0, data1, object0, object1);
      } finally {
         return;
      }
   }

   @Override
   public final void signalLevel(int level) {
      this._instance.signalLevel(level);
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this._instance.networkStarted(networkId, service);
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
      this._instance.radioTurnedOff();
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
      this._instance.pdpStateChange(apn, state, cause);
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final boolean isBrowserAvailable() {
      return this._instance != null ? this._instance.isBrowserAvailable() : false;
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this._instance.networkServiceChange(networkId, service);
   }

   @Override
   public final void cardInserted() {
   }

   @Override
   public final void cardUpdated() {
   }

   @Override
   public final void cardInvalid(int code, int subCode) {
   }

   @Override
   public final void cardFault(int code) {
   }

   @Override
   public final void smsEFFull() {
   }

   @Override
   public final void responseDeleteSMS(int status, int packetId) {
   }

   @Override
   public final void responseMarkSMSAsRead(int status, int packetId) {
   }

   @Override
   public final void cardReady() {
      ReregistrationListener[] listeners = null;
      synchronized (this._registrationListeners) {
         listeners = new ReregistrationListener[this._registrationListeners.length];

         for (int i = listeners.length - 1; i >= 0; i--) {
            Object obj = this._registrationListeners[i].get();
            if (obj == null) {
               Arrays.removeAt(this._registrationListeners, i);
            } else {
               listeners[i] = (ReregistrationListener)obj;
            }
         }
      }

      this._instance.cardReady(listeners);
   }

   @Override
   public final boolean freeStaleObject(int priority) {
      return this._instance.freeStaleObject(priority);
   }

   @Override
   public final void addReregistrationListener(ReregistrationListener l) {
      synchronized (this._registrationListeners) {
         for (int i = this._registrationListeners.length - 1; i >= 0; i--) {
            Object obj = this._registrationListeners[i].get();
            if (obj == null) {
               Arrays.removeAt(this._registrationListeners, i);
            }
         }

         Array.resize(this._registrationListeners, this._registrationListeners.length + 1);
         this._registrationListeners[this._registrationListeners.length - 1] = new WeakReference(l);
      }
   }

   @Override
   public final void removeReregistrationListener(ReregistrationListener l) {
      synchronized (this._registrationListeners) {
         for (int i = this._registrationListeners.length - 1; i >= 0; i--) {
            Object obj = this._registrationListeners[i].get();
            if (obj == null || obj == l) {
               Arrays.removeAt(this._registrationListeners, i);
            }
         }
      }
   }

   @Override
   public final void syncEventOccurred(int eventId, Object object) {
      switch (eventId) {
         case 1:
         default:
            this._instance.serialSyncStarted();
            return;
         case 2:
            this._instance.serialSyncStopped();
         case 0:
      }
   }

   @Override
   public final void serviceRoutingStateChanged(String service, boolean serviceState) {
      this._instance.serviceStatus(serviceState, service);
   }

   @Override
   public final void serviceRouteStateChanged(int routeHandle, boolean routeState) {
      this._instance.routeStatus(routeState, routeHandle);
   }

   @Override
   public final void serviceRoutingCapabilitiesChanged(String service) {
   }

   private BrowserDaemonRegistry(BrowserImpl instance) {
      this._plugInOptions = new Vector();
      this._instance = instance;
   }

   public static final void addBrowserUrlCollectionListener(UrlCollectionListener urlCollectionListener) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (registry._urlCollectionListeners) {
         for (int i = registry._urlCollectionListeners.length - 1; i >= 0; i--) {
            if (registry._urlCollectionListeners[i].get() == null) {
               Arrays.removeAt(registry._urlCollectionListeners, i);
            }
         }

         Array.resize(registry._urlCollectionListeners, registry._urlCollectionListeners.length + 1);
         registry._urlCollectionListeners[registry._urlCollectionListeners.length - 1] = new WeakReference(urlCollectionListener);
      }
   }

   public static final void removeBrowserUrlCollectionListener(UrlCollectionListener urlCollectionListener) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (registry._urlCollectionListeners) {
         for (int i = registry._urlCollectionListeners.length - 1; i >= 0; i--) {
            WeakReference ref = registry._urlCollectionListeners[i];
            Object obj = ref.get();
            if (obj == null || obj == urlCollectionListener) {
               Arrays.removeAt(registry._urlCollectionListeners, i);
            }
         }
      }
   }

   public static final void notifyUrlCollectionListeners(String url, int action) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (registry._urlCollectionListeners) {
         for (int i = registry._urlCollectionListeners.length - 1; i >= 0; i--) {
            Object obj = registry._urlCollectionListeners[i].get();
            if (obj == null) {
               Arrays.removeAt(registry._urlCollectionListeners, i);
            } else {
               ((UrlCollectionListener)obj).collectionChanged(url, action);
            }
         }
      }
   }

   static final boolean isRegistered() {
      return ApplicationRegistry.getApplicationRegistry().get(4307171400805038204L) != null;
   }

   public static final void notifyBrowserConfigChangeListeners(boolean validConfig) {
      Proxy p = Proxy.getInstance();
      p.invokeLater(new BrowserDaemonRegistry$1(validConfig));
   }

   private final void detachInstance() {
      Proxy p = Proxy.getInstance();
      p.removeRadioListener(this);
      p.removeGlobalEventListener(this);
      SIMCard.removeListener(p, this);
      LowMemoryManager.removeLowMemoryListener(this);
      SyncManager.getInstance().removeSyncEventListener(this);
      ServiceRouting.getInstance().removeListener(this);

      label23:
      try {
         if (this._instance != null) {
            this._instance.cleanup();
         }
      } finally {
         break label23;
      }

      this._instance = null;
   }

   static final void doneStartup() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (registry) {
         if (registry._instance != null) {
            Proxy p = Proxy.getInstance();
            p.addRadioListener(registry);
            p.addGlobalEventListener(registry);
            SIMCard.addListener(p, registry);
            LowMemoryManager.addLowMemoryListener(registry);
            SyncManager.getInstance().addSyncEventListener(registry);
            ServiceRouting.getInstance().addListener(registry);
            registry.notifyAll();
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final BrowserImpl getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (registry) {
         if (registry._instance != null && !registry._instance.isStartingUp()) {
            return registry._instance;
         }

         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            registry.wait();
            var7 = false;
         } finally {
            if (var7) {
               return null;
            }
         }

         return registry._instance;
      }
   }

   static final BrowserImpl setInstance(BrowserImpl instance) {
      BrowserImpl oldInstance = null;
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.getOrWaitFor(4307171400805038204L);
      if (registry == null) {
         registry = new BrowserDaemonRegistry(instance);
         ar.put(4307171400805038204L, registry);
         synchronized (ar) {
            ReregistrationManager registryInstance = (ReregistrationManager)ar.get(8461344725264262391L);
            if (registryInstance == null) {
               ar.put(8461344725264262391L, registry);
            }

            return oldInstance;
         }
      } else {
         synchronized (registry) {
            oldInstance = registry._instance;
            registry.detachInstance();
            registry._instance = instance;
            registry.notifyAll();
            return oldInstance;
         }
      }
   }

   public static final void addPlugInOption(IBrowserProperty browserProperty) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (registry._plugInOptions) {
         if (!registry._plugInOptions.contains(browserProperty)) {
            registry._plugInOptions.addElement(browserProperty);
         }
      }
   }

   public static final void addBrowserStateListener(BrowserStateListener listener) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (registry._browserStateListeners) {
         for (int i = registry._browserStateListeners.length - 1; i >= 0; i--) {
            Object obj = registry._browserStateListeners[i].get();
            if (obj == null) {
               Arrays.removeAt(registry._browserStateListeners, i);
            }
         }

         Array.resize(registry._browserStateListeners, registry._browserStateListeners.length + 1);
         registry._browserStateListeners[registry._browserStateListeners.length - 1] = new WeakReference(listener);
      }
   }

   public static final void removeBrowserStateListener(BrowserStateListener listener) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (registry._browserStateListeners) {
         for (int i = registry._browserStateListeners.length - 1; i >= 0; i--) {
            Object obj = registry._browserStateListeners[i].get();
            if (obj == null || obj == listener) {
               Arrays.removeAt(registry._browserStateListeners, i);
            }
         }
      }
   }

   public static final void notifyBrowserStateListeners(int newState) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (registry._browserStateListeners) {
         for (int i = registry._browserStateListeners.length - 1; i >= 0; i--) {
            Object obj = registry._browserStateListeners[i].get();
            if (obj == null) {
               Arrays.removeAt(registry._browserStateListeners, i);
            } else {
               ((BrowserStateListener)obj).browserStateChanged(newState);
            }
         }
      }
   }

   public static final IBrowserProperty[] getPlugInOptions() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (registry._plugInOptions) {
         IBrowserProperty[] options = new IBrowserProperty[registry._plugInOptions.size()];
         registry._plugInOptions.copyInto(options);
         return options;
      }
   }

   public static final void broadCastEvent(int eventId, Object param) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (FolderHierarchies.getLockObject()) {
         synchronized (registry._folderEventListeners) {
            for (int i = registry._folderEventListeners.length - 1; i >= 0; i--) {
               Object obj = registry._folderEventListeners[i].get();
               if (obj == null) {
                  Arrays.removeAt(registry._folderEventListeners, i);
               } else {
                  ((FolderEventListener)obj).browserEventOccured(eventId, param);
               }
            }
         }
      }

      if ((eventId == 100 || eventId == 101 || eventId == 102) && registry._instance != null) {
         registry._instance.bookmarksChanged();
      }
   }

   public static final void addFolderEventListener(FolderEventListener listener) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (registry._folderEventListeners) {
         for (int i = registry._folderEventListeners.length - 1; i >= 0; i--) {
            Object obj = registry._folderEventListeners[i].get();
            if (obj == null) {
               Arrays.removeAt(registry._folderEventListeners, i);
            }
         }

         Array.resize(registry._folderEventListeners, registry._folderEventListeners.length + 1);
         registry._folderEventListeners[registry._folderEventListeners.length - 1] = new WeakReference(listener);
      }
   }

   public static final void removeFolderEventListener(FolderEventListener listener) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (registry._folderEventListeners) {
         for (int i = registry._folderEventListeners.length - 1; i >= 0; i--) {
            Object obj = registry._folderEventListeners[i].get();
            if (obj == null || obj == listener) {
               Arrays.removeAt(registry._folderEventListeners, i);
            }
         }
      }
   }

   public static final void addBrowserConfigChangeListener(BrowserConfigChangeListener listener) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (registry._configListeners) {
         for (int i = registry._configListeners.length - 1; i >= 0; i--) {
            if (registry._configListeners[i].get() == null) {
               Arrays.removeAt(registry._configListeners, i);
            }
         }

         Array.resize(registry._configListeners, registry._configListeners.length + 1);
         registry._configListeners[registry._configListeners.length - 1] = new WeakReference(listener);
      }
   }

   public static final void removeBrowserConfigChangeListener(BrowserConfigChangeListener listener) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (registry._configListeners) {
         for (int i = registry._configListeners.length - 1; i >= 0; i--) {
            WeakReference ref = registry._configListeners[i];
            Object obj = ref.get();
            if (obj == null || obj == listener) {
               Arrays.removeAt(registry._configListeners, i);
            }
         }
      }
   }
}
