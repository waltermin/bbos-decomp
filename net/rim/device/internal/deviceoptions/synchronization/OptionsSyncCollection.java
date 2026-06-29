package net.rim.device.internal.deviceoptions.synchronization;

import java.util.Vector;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncPriorityAndDependencyProvider;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncCollectionStatusProvider;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.internal.deviceoptions.DeviceOptions;
import net.rim.device.internal.deviceoptions.OptionsProvider;
import net.rim.device.internal.deviceoptions.OptionsProviderChangeListener;

final class OptionsSyncCollection
   implements SyncCollection,
   SyncConverter,
   OTASyncCapable,
   OTASyncPriorityAndDependencyProvider,
   CollectionEventSource,
   OptionsProviderChangeListener,
   SyncCollectionStatusProvider {
   private CollectionListenerManager _listenerManager = new CollectionListenerManager();
   private Vector _providers = DeviceOptions.getInstance().getOptionsProviders();
   private Vector _legacyProviders = new Vector();

   final void removeLegacyOptionsProvider(OptionsProvider provider) {
      if (this._legacyProviders.size() == 1) {
         this._legacyProviders.setSize(0);
      } else {
         this._legacyProviders = ListenerUtilities.removeListener(this._legacyProviders, provider);
      }
   }

   final void addLegacyOptionsProvider(OptionsProvider provider) {
      this._legacyProviders = ListenerUtilities.addListener(this._legacyProviders, provider);
   }

   @Override
   public final int getSyncObjectCount() {
      return this._providers.size();
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return "Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      this._listenerManager.fireElementAdded(this, object);
      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      this._listenerManager.fireElementUpdated(this, oldObject, newObject);
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      this._listenerManager.fireElementRemoved(this, object);
      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      Vector providers = this._providers;
      synchronized (providers) {
         DataBuffer emptyBuffer = new DataBuffer();
         int num = providers.size();

         while (--num >= 0) {
            OptionsProvider provider = (OptionsProvider)providers.elementAt(num);
            provider.setOptionsData(emptyBuffer);
         }

         return true;
      }
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return true;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void endTransaction() {
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      ((OptionsProvider)object).getOptionsData(buffer);
      return true;
   }

   @Override
   public final SyncObject convert(DataBuffer data, int version, int UID) {
      OptionsProvider provider = this.getProvider(UID);
      if (provider != null) {
         provider.setOptionsData(data);
         return provider;
      } else {
         return new OptionsSyncCollection$DummySyncObject(UID);
      }
   }

   @Override
   public final int getSyncPriority() {
      return 1;
   }

   @Override
   public final int getDependencyLevel() {
      return 4;
   }

   @Override
   public final boolean isWritableForSerialSync() {
      return true;
   }

   @Override
   public final boolean isReadableForSerialSync() {
      return true;
   }

   @Override
   public final boolean isWritableForOTASL() {
      return true;
   }

   @Override
   public final int getOTASLControlMask() {
      return 1;
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._listenerManager.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._listenerManager.removeCollectionListener(listener);
   }

   @Override
   public final void optionsProviderChanged(OptionsProvider optionsProvider) {
      this._listenerManager.fireElementUpdated(this, null, optionsProvider);
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return null;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      Vector providers = this._providers;
      synchronized (providers) {
         int numProviders = providers.size();

         for (int i = 0; i < numProviders; i++) {
            SyncObject provider = (SyncObject)providers.elementAt(i);
            if (provider.getUID() == uid) {
               return provider;
            }
         }

         return null;
      }
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      Vector providers = this._providers;
      synchronized (providers) {
         int count = providers.size();
         SyncObject[] result = new SyncObject[count];

         for (int i = 0; i < count; i++) {
            result[i] = (SyncObject)providers.elementAt(i);
         }

         return result;
      }
   }

   private final OptionsProvider getProvider(int UID) {
      OptionsProvider provider = null;
      synchronized (this._providers) {
         int num = this._providers.size();

         while (--num >= 0) {
            OptionsProvider candidate = (OptionsProvider)this._providers.elementAt(num);
            if (candidate.getUID() == UID) {
               provider = candidate;
               break;
            }
         }
      }

      if (provider == null) {
         synchronized (this._legacyProviders) {
            int num = this._legacyProviders.size();

            while (--num >= 0) {
               OptionsProvider candidate = (OptionsProvider)this._legacyProviders.elementAt(num);
               if (candidate.getUID() == UID) {
                  provider = candidate;
                  break;
               }
            }

            return provider;
         }
      } else {
         return provider;
      }
   }
}
