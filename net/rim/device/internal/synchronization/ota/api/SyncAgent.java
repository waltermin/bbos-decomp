package net.rim.device.internal.synchronization.ota.api;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.synchronization.ota.service.Configuration;
import net.rim.device.internal.synchronization.ota.service.DataSource;
import net.rim.device.internal.synchronization.ota.service.ServicesConfigurationManager;
import net.rim.device.internal.synchronization.ota.util.EventHandler;
import net.rim.vm.WeakReference;

public final class SyncAgent {
   private Vector _listeners;
   private LongHashtable _dbsUsedByOtherSyncSources;
   private ServicesConfigurationManager _servicesConfigurationManager;
   private EventHandler _eventHandler;
   private static final long APPLICATION_REG_GUID = -2145435626893159055L;

   public static final SyncAgent getSingletonInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      SyncAgent syncAgent = (SyncAgent)ar.getOrWaitFor(-2145435626893159055L);
      if (syncAgent == null) {
         syncAgent = new SyncAgent();
         ar.put(-2145435626893159055L, syncAgent);
      }

      return syncAgent;
   }

   private SyncAgent() {
      SyncAgentTransactionManager.getInstance();
      this._listeners = (Vector)(new Object());
      this._dbsUsedByOtherSyncSources = (LongHashtable)(new Object());
      this._servicesConfigurationManager = ServicesConfigurationManager.getSingletonInstance();
      this._eventHandler = new EventHandler();
      Proxy.getInstance().startThread(this._eventHandler);
   }

   public final void registerListener(SyncAgentListener aSyncAgentListener) {
      this._listeners.addElement(new Object(aSyncAgentListener));
   }

   public final void notifyListenersWith(int eventId, Object anObject) {
      if (!this._listeners.isEmpty()) {
         for (int xIndex = this._listeners.size() - 1; xIndex > -1; xIndex--) {
            WeakReference xWref = (WeakReference)this._listeners.elementAt(xIndex);
            if (xWref != null) {
               SyncAgentListener xSyncAgentListener = (SyncAgentListener)xWref.get();
               if (xSyncAgentListener != null) {
                  try {
                     xSyncAgentListener.onSyncAgentEvent(eventId, anObject);
                  } finally {
                     continue;
                  }
               } else {
                  this._listeners.removeElementAt(xIndex);
               }
            }
         }
      }
   }

   public final synchronized void markDatabaseAsUsedByOtherSyncSources(long sid, String aDatabaseName) {
      if (!this.checkUseByOtherSyncSources(sid, aDatabaseName)) {
         IntVector databases = (IntVector)this._dbsUsedByOtherSyncSources.get(sid);
         int xHash = CRC32.update(-1, aDatabaseName.getBytes());
         if (databases == null) {
            databases = (IntVector)(new Object());
         }

         databases.addElement(xHash);
         this._dbsUsedByOtherSyncSources.put(sid, databases);
      }
   }

   public final synchronized void unMarkDatabaseAsUsedByOtherSyncSources(long sid, String aDatabaseName) {
      IntVector databases = (IntVector)this._dbsUsedByOtherSyncSources.get(sid);
      if (databases != null) {
         int xHash = CRC32.update(-1, aDatabaseName.getBytes());
         databases.removeElement(xHash);
      }
   }

   private final synchronized boolean checkUseByOtherSyncSources(long sid, String aDatabaseName) {
      boolean result = false;
      IntVector databases = (IntVector)this._dbsUsedByOtherSyncSources.get(sid);
      if (databases != null) {
         int xHash = CRC32.update(-1, aDatabaseName.getBytes());
         result = databases.contains(xHash);
      }

      return result;
   }

   public final synchronized boolean isUsedByOtherSyncSources(long sid, String aDatabaseName) {
      boolean usedByOtherSource = getSingletonInstance().checkUseByOtherSyncSources(-1, aDatabaseName);
      if (!usedByOtherSource && sid != -1) {
         usedByOtherSource = getSingletonInstance().checkUseByOtherSyncSources(sid, aDatabaseName);
      }

      return usedByOtherSource;
   }

   public final long getDefaultSid() {
      Configuration xConfiguration = this._servicesConfigurationManager.getDefaultConfiguration();
      return xConfiguration == null ? -1 : xConfiguration.getSid();
   }

   public final String getDefaultSyncDataSourceFor(long sid) {
      Configuration xConfiguration = this._servicesConfigurationManager.getConfiguration(sid);
      DataSource xDataSource = xConfiguration.getDefaultSyncDataSource();
      return xDataSource == null ? null : xDataSource.getName();
   }

   public final Vector getDefaultSyncDataBasesFor(long sid) {
      Vector result = (Vector)(new Object(3));
      Configuration xConfiguration = this._servicesConfigurationManager.getConfiguration(sid);
      DataSource xDataSource = xConfiguration.getDefaultSyncDataSource();
      if (xDataSource != null) {
         Hashtable datasourceDatabases = xDataSource.getDataSourceDatabases();
         Enumeration e = datasourceDatabases.keys();

         while (e.hasMoreElements()) {
            String databaseName = (String)e.nextElement();
            result.addElement(databaseName);
         }
      }

      return result;
   }

   public final String getDefaultNonSyncDataSourceFor(long sid) {
      Configuration xConfiguration = this._servicesConfigurationManager.getConfiguration(sid);
      DataSource xDataSource = xConfiguration.getDefaultNonSyncDataSource();
      return xDataSource == null ? null : xDataSource.getName();
   }

   public final boolean databaseContainsTables(long sid, String aDataSourceName, String aDatabaseName, int version) {
      Configuration xConfiguration = ServicesConfigurationManager.getSingletonInstance().getConfiguration(sid);
      return xConfiguration == null ? false : xConfiguration.dataSourceDatabaseContainsTables(aDataSourceName, aDatabaseName, version);
   }

   public final String getDataSourceNameFor(long sid, String aDatabaseName) {
      Configuration xConfiguration = ServicesConfigurationManager.getSingletonInstance().getConfiguration(sid);
      Hashtable xDataSourcesMap = xConfiguration.getDataSources();
      Enumeration xDataSourcesList = xDataSourcesMap.elements();

      while (xDataSourcesList.hasMoreElements()) {
         DataSource xDataSource = (DataSource)xDataSourcesList.nextElement();
         if (xDataSource.contains(aDatabaseName)) {
            return xDataSource.getName();
         }
      }

      return null;
   }

   public final boolean isDatabaseDefined(long sid, String aDataSourceName, String aDatabaseName) {
      Configuration xConfiguration = ServicesConfigurationManager.getSingletonInstance().getConfiguration(sid);
      return xConfiguration == null ? false : xConfiguration.isDataSourceDatabaseDefined(aDataSourceName, aDatabaseName);
   }

   public final boolean isDatabaseVersionSupported(long sid, String aDataSourceName, String aDatabaseName, int aDatabaseVersion) {
      Configuration xConfiguration = ServicesConfigurationManager.getSingletonInstance().getConfiguration(sid);
      return xConfiguration == null ? false : xConfiguration.getDataSourceDatabase(aDataSourceName, aDatabaseName, aDatabaseVersion) != null;
   }

   public final boolean isDatabaseEnabled(long sid, String aDataSourceName, String aDatabaseName, int version) {
      Configuration xConfiguration = ServicesConfigurationManager.getSingletonInstance().getConfiguration(sid);
      return xConfiguration == null ? false : xConfiguration.isDataSourceDatabaseEnabled(aDataSourceName, aDatabaseName, version);
   }

   public final boolean isSidEnabled(long sid) {
      Configuration xConfiguration = ServicesConfigurationManager.getSingletonInstance().getConfiguration(sid);
      return xConfiguration == null ? false : xConfiguration.isUserEnabled();
   }

   public final void enableSync(boolean enabled) {
      this._eventHandler.addEvent(new EnableSyncEvent(enabled, this));
   }

   public final boolean isCollectionResetSupported(long sid) {
      Configuration xConfiguration = ServicesConfigurationManager.getSingletonInstance().getConfiguration(sid);
      return xConfiguration.isDeleteOnSlowSyncEnabled();
   }
}
