package net.rim.device.internal.synchronization.ota.service;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.LongHashtable;

public final class ServicesConfigurationManager {
   private LongHashtable _loadedConfigurations;
   private Configuration _defaultConfiguration;
   private static final long GUID;

   public static final ServicesConfigurationManager getSingletonInstance() {
      ApplicationRegistry theApplicationRegistry = ApplicationRegistry.getApplicationRegistry();
      ServicesConfigurationManager xInstance = (ServicesConfigurationManager)theApplicationRegistry.getOrWaitFor(6489741850224630387L);
      if (xInstance == null) {
         xInstance = new ServicesConfigurationManager();
         theApplicationRegistry.put(6489741850224630387L, xInstance);
      }

      return xInstance;
   }

   private ServicesConfigurationManager() {
      PersistentObject xPersistentObject = PersistentStore.getPersistentObject(6489741850224630387L);
      this._loadedConfigurations = this.performIntegerityCheck((LongHashtable)xPersistentObject.getContents());
      xPersistentObject.setContents(this._loadedConfigurations, 51);
      xPersistentObject.forceCommit();
   }

   private final LongHashtable performIntegerityCheck(LongHashtable loadedConfigurations) {
      if (loadedConfigurations == null) {
         return (LongHashtable)(new Object(1));
      }

      long[] xKeys = new long[loadedConfigurations.size()];
      loadedConfigurations.keysToArray(xKeys);

      for (int xIndex = xKeys.length - 1; xIndex > -1; xIndex--) {
         long xKey = xKeys[xIndex];
         Configuration xConfigurations = (Configuration)loadedConfigurations.get(xKey);
         long sid = xConfigurations.getSid();
         if (ServiceBook.getSB().getRecordByCidAndSid("sync", sid) == null) {
            loadedConfigurations.remove(xKey);
         }
      }

      return loadedConfigurations;
   }

   public final synchronized void purgeConfig(long sid) {
      Configuration xConfiguration = (Configuration)this._loadedConfigurations.get(sid);
      if (xConfiguration != null) {
         if (xConfiguration == this._defaultConfiguration) {
            this._defaultConfiguration = null;
         }

         this._loadedConfigurations.remove(sid);
      }
   }

   public final synchronized Configuration getConfiguration(long sid) {
      if (sid == -1) {
         return null;
      }

      try {
         Configuration xConfiguration = (Configuration)this._loadedConfigurations.get(sid);
         if (xConfiguration == null) {
            xConfiguration = new Configuration(sid);
            this._loadedConfigurations.put(sid, xConfiguration);
         }

         return xConfiguration;
      } finally {
         ;
      }
   }

   public final void setConfiguration(Configuration aConfiguration) {
      long sid = aConfiguration.getSid();
      Configuration xOldConfiguration = (Configuration)this._loadedConfigurations.get(sid);
      if (xOldConfiguration != null) {
         if (xOldConfiguration != aConfiguration) {
            aConfiguration.copyInto(xOldConfiguration);
         }
      } else {
         xOldConfiguration = aConfiguration;
         this._loadedConfigurations.put(sid, aConfiguration);
      }

      if (this._defaultConfiguration == null) {
         this._defaultConfiguration = xOldConfiguration;
      } else {
         if (!this._defaultConfiguration.isDefault() && xOldConfiguration.isDefault()) {
            this._defaultConfiguration = xOldConfiguration;
         }
      }
   }

   public final Configuration getDefaultConfiguration() {
      return this._defaultConfiguration;
   }
}
