package net.rim.device.internal.synchronization;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceIdentifier;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.MultiServiceSyncCollection;
import net.rim.device.api.synchronization.OTASyncDefaultProvider;
import net.rim.device.api.synchronization.OTASyncParametersProvider;
import net.rim.device.api.synchronization.SerialSyncListener;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncManagerStatistics;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.internal.provisioning.ActivationService;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.synchronization.ota.adapters.OTASyncCollectionAdapter;
import net.rim.device.internal.synchronization.ota.api.SyncAgent;
import net.rim.device.internal.synchronization.ota.api.SyncAgentListener;
import net.rim.device.internal.synchronization.ota.api.SyncAgentUrl;
import net.rim.vm.Array;

public final class SyncManagerImpl extends SyncManager implements SyncAgentListener, GlobalEventListener, SyncManagerStatistics {
   private SyncAgent _syncAgent;
   private Vector _otaEnabledServiceIdentifiers = (Vector)(new Object(10));
   private SyncCollection[] _serialCollections = new Object[0];
   private LongHashtable _multiServiceSerialCollections = (LongHashtable)(new Object());
   private Hashtable _otaAdapters = (Hashtable)(new Object());
   private LongHashtable _multiServiceOtaAdapters = (LongHashtable)(new Object());
   private Hashtable _multiServiceSyncCollections = (Hashtable)(new Object());
   private SyncCollection[] _disallowedOTASync = new Object[0];
   private Object[] _syncEventListeners;
   private Object[] _serialSyncListeners;
   private boolean _serialSyncInProgress;
   private ToIntHashtable _itPolicyTagMap = (ToIntHashtable)(new Object(4));
   private static final byte USER_PREFERENCE = 0;
   private static final byte IT_POLICY = 1;
   private static final byte SYNC_AGENT = 2;

   SyncManagerImpl() {
      this._syncAgent = SyncAgent.getSingletonInstance();
      this._syncAgent.registerListener(this);
      Proxy.getInstance().addGlobalEventListener(this);
   }

   @Override
   public final synchronized void enableSynchronization(SyncCollection collection) {
      this.enableSynchronization(collection, true);
   }

   @Override
   public final synchronized void enableSynchronization(SyncCollection collection, boolean allowOTASync, int itPolicyTagForDisablingOTASync) {
      if (itPolicyTagForDisablingOTASync != 1 && !this._itPolicyTagMap.contains(itPolicyTagForDisablingOTASync)) {
         if (collection instanceof Object) {
            this._itPolicyTagMap.put(collection, itPolicyTagForDisablingOTASync);
         }

         this.enableSynchronization(collection, allowOTASync);
      } else {
         throw new Object("IIPT");
      }
   }

   @Override
   public final synchronized void enableSynchronization(SyncCollection collection, boolean allowOTASync) {
      if (!this.checkCollection(collection)) {
         throw new Object("ISC");
      }

      if (!this.isSerialSynchronizationEnabled(collection) && !this.isOTASynchronizationEnabled(collection)) {
         if (collection instanceof Object) {
            if (!allowOTASync) {
               Arrays.add(this._disallowedOTASync, collection);
            } else if (this.enableOTASynchronization(collection)) {
               return;
            }
         }

         this.enableSerialSynchronization(collection);
         if (collection instanceof Object) {
            String databaseName = collection.getSyncName();
            this._multiServiceSyncCollections.put(databaseName, collection);
         }
      } else {
         throw new Object("DSC");
      }
   }

   @Override
   public final void enableOTASync(boolean enable) {
      this._syncAgent.enableSync(enable);
   }

   private final boolean checkCollection(SyncCollection collection) {
      if (collection == null) {
         return false;
      }

      String name = collection.getSyncName();
      if (name == null) {
         return false;
      }

      int len = name.length();
      return len == 0 || len >= 32 ? false : collection.getSyncConverter() != null;
   }

   @Override
   public final synchronized void allowOTASync(SyncCollection collection, boolean allow) {
      this.allowOTASync(collection, allow, (byte)0);
   }

   private final synchronized void allowOTASync(SyncAgentUrl url, boolean allow) {
      this.allowOTASync(this.findSyncCollection(url, allow), allow, (byte)2);
   }

   private final synchronized void allowOTASync(SyncCollection collection, boolean allow, byte requestType) {
      if (!(collection instanceof Object)) {
         if (requestType == 0) {
            throw new Object("NOSC");
         }
      } else {
         boolean otaSyncEnabled = this.isOTASynchronizationEnabled(collection);
         if (!this.isSerialSynchronizationEnabled(collection) && !otaSyncEnabled) {
            throw new Object("SNE");
         }

         if (allow) {
            if (requestType == 0) {
               Arrays.remove(this._disallowedOTASync, collection);
            }

            if (!otaSyncEnabled && this.enableOTASynchronization(collection)) {
               this.disableSerialSynchronization(collection);
               return;
            }
         } else {
            if (requestType == 0 && !Arrays.contains(this._disallowedOTASync, collection)) {
               Arrays.add(this._disallowedOTASync, collection);
            }

            if (otaSyncEnabled) {
               this.disableOTASynchronization(collection);
               this.enableSerialSynchronization(collection);
            }
         }
      }
   }

   @Override
   public final synchronized boolean isOTASyncAvailable(SyncCollection collection, boolean checkUserPreference) {
      return !(collection instanceof Object) ? false : this.isOTASyncAvailable(collection, checkUserPreference, null);
   }

   private final boolean isOTASyncDisabledByDefault(SyncCollection collection) {
      return !(collection instanceof Object) ? false : ((OTASyncDefaultProvider)collection).isDisabledByDefault();
   }

   private final boolean isOTASyncAvailable(SyncCollection collection, boolean checkAllow, String[] otaSyncParameters) {
      if (!this.isOTASynchronizationAvailable()) {
         return false;
      }

      if (ITPolicy.getBoolean(33, 1, false)) {
         return false;
      }

      boolean disabledByDefault = this.isOTASyncDisabledByDefault(collection);
      int itPolicyTag = this._itPolicyTagMap.get(collection);
      if (itPolicyTag != -1) {
         if (ITPolicy.getBoolean(33, itPolicyTag, disabledByDefault)) {
            return false;
         }
      } else if (disabledByDefault) {
         return false;
      }

      if (checkAllow && Arrays.contains(this._disallowedOTASync, collection)) {
         return false;
      }

      if (otaSyncParameters == null) {
         otaSyncParameters = new Object[3];
      }

      if (!this.getOTASyncParameters(collection, otaSyncParameters)) {
         return false;
      }

      long sid = -1;
      if (otaSyncParameters[0] != null) {
         sid = Long.parseLong(otaSyncParameters[0]);
      }

      return this._syncAgent.isDatabaseEnabled(sid, otaSyncParameters[1], otaSyncParameters[2], collection.getSyncVersion());
   }

   private final synchronized SyncCollection findSyncCollection(SyncAgentUrl url, boolean searchSerialCollections) {
      long sid = url.getSid();
      String databaseName = url.getDatabaseName();
      SyncCollection collection = null;
      if (searchSerialCollections) {
         for (int i = this._serialCollections.length - 1; i >= 0; i--) {
            SyncCollection syncCollection = this._serialCollections[i];
            if (this.isMatchingOTASyncCollection(syncCollection, databaseName)) {
               collection = syncCollection;
               break;
            }
         }

         if (collection == null) {
            Hashtable ht = (Hashtable)this._multiServiceSerialCollections.get(sid);
            if (ht != null) {
               Enumeration e = ht.elements();

               while (e.hasMoreElements()) {
                  SyncCollection syncCollection = (SyncCollection)e.nextElement();
                  if (this.isMatchingOTASyncCollection(syncCollection, databaseName)) {
                     collection = syncCollection;
                     break;
                  }
               }
            }
         }
      } else {
         Enumeration e = this._otaAdapters.elements();

         while (e.hasMoreElements()) {
            SyncCollection syncCollection = ((OTASyncCollectionAdapter)e.nextElement()).getSyncCollection();
            if (this.isMatchingOTASyncCollection(syncCollection, databaseName)) {
               collection = syncCollection;
               break;
            }

            Thread.yield();
         }

         if (collection == null) {
            Hashtable ht = (Hashtable)this._multiServiceOtaAdapters.get(sid);
            if (ht != null) {
               Enumeration e2 = ht.elements();

               while (e2.hasMoreElements()) {
                  SyncCollection syncCollection = ((OTASyncCollectionAdapter)e2.nextElement()).getSyncCollection();
                  if (this.isMatchingOTASyncCollection(syncCollection, databaseName)) {
                     collection = syncCollection;
                     break;
                  }
               }
            }
         }
      }

      return collection;
   }

   private final boolean isMatchingOTASyncCollection(SyncCollection syncCollection, String databaseName) {
      if (syncCollection instanceof Object) {
         String syncName = null;
         if (syncCollection instanceof OTASyncParametersProvider) {
            syncName = ((OTASyncParametersProvider)syncCollection).getDatabaseName();
         }

         if (syncName == null || syncName.length() <= 0) {
            syncName = syncCollection.getSyncName();
         }

         return StringUtilities.strEqualIgnoreCase(syncName, databaseName, 1701707776);
      } else {
         return false;
      }
   }

   private final boolean getOTASyncParameters(SyncCollection collection, String[] parameters) {
      long sid = -1;
      String dataSourceName = null;
      String databaseName = null;
      if (collection instanceof Object) {
         sid = ((MultiServiceSyncCollection)collection).getSid();
      }

      if (collection instanceof OTASyncParametersProvider) {
         OTASyncParametersProvider otaSyncParametersProvider = (OTASyncParametersProvider)collection;
         dataSourceName = otaSyncParametersProvider.getDataSourceName();
         databaseName = otaSyncParametersProvider.getDatabaseName();
      }

      if (sid == -1) {
         sid = this._syncAgent.getDefaultSid();
      }

      if (sid != -1 && this._syncAgent.isSidEnabled(sid)) {
         if (databaseName == null || databaseName.length() == 0) {
            databaseName = collection.getSyncName();
         }

         if (dataSourceName == null || dataSourceName.length() == 0) {
            dataSourceName = this._syncAgent.getDataSourceNameFor(sid, databaseName);
            if (dataSourceName == null || dataSourceName.length() == 0) {
               dataSourceName = this._syncAgent.getDefaultSyncDataSourceFor(sid);
            }

            if (dataSourceName != null) {
               if (this._syncAgent.isDatabaseDefined(sid, dataSourceName, databaseName)) {
                  if (!this._syncAgent.isDatabaseVersionSupported(sid, dataSourceName, databaseName, collection.getSyncVersion())) {
                     return false;
                  }
               } else {
                  dataSourceName = this._syncAgent.getDefaultNonSyncDataSourceFor(sid);
               }
            } else {
               dataSourceName = this._syncAgent.getDefaultNonSyncDataSourceFor(sid);
            }
         }

         if (sid != -1 && dataSourceName != null && databaseName != null) {
            parameters[0] = String.valueOf(sid);
            parameters[1] = dataSourceName;
            parameters[2] = databaseName;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private final boolean enableOTASynchronization(SyncCollection param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: bipush 3
      // 02: anewarray 1114
      // 05: astore 2
      // 06: aload 0
      // 07: aload 1
      // 08: bipush 1
      // 09: aload 2
      // 0a: invokespecial net/rim/device/internal/synchronization/SyncManagerImpl.isOTASyncAvailable (Lnet/rim/device/api/synchronization/SyncCollection;Z[Ljava/lang/String;)Z
      // 0d: ifne 12
      // 10: bipush 0
      // 11: ireturn
      // 12: aload 2
      // 13: bipush 0
      // 14: aaload
      // 15: invokestatic java/lang/Long.parseLong (Ljava/lang/String;)J
      // 18: lstore 3
      // 19: aload 2
      // 1a: bipush 1
      // 1b: aaload
      // 1c: astore 5
      // 1e: aload 2
      // 1f: bipush 2
      // 21: aaload
      // 22: astore 6
      // 24: new net/rim/device/internal/synchronization/ota/adapters/OTASyncCollectionAdapter
      // 27: dup
      // 28: aload 1
      // 29: invokespecial net/rim/device/internal/synchronization/ota/adapters/OTASyncCollectionAdapter.<init> (Lnet/rim/device/api/synchronization/SyncCollection;)V
      // 2c: astore 7
      // 2e: aload 7
      // 30: new net/rim/device/internal/synchronization/ota/api/SyncAgentUrl
      // 33: dup
      // 34: lload 3
      // 35: aload 5
      // 37: aload 6
      // 39: invokespecial net/rim/device/internal/synchronization/ota/api/SyncAgentUrl.<init> (JLjava/lang/String;Ljava/lang/String;)V
      // 3c: invokevirtual net/rim/device/internal/synchronization/ota/adapters/OTASyncCollectionAdapter.bind (Lnet/rim/device/internal/synchronization/ota/api/SyncAgentUrl;)V
      // 3f: aload 1
      // 40: instanceof java/lang/Object
      // 43: ifeq 7d
      // 46: aload 0
      // 47: getfield net/rim/device/internal/synchronization/SyncManagerImpl._multiServiceOtaAdapters Lnet/rim/device/api/util/LongHashtable;
      // 4a: lload 3
      // 4b: invokevirtual net/rim/device/api/util/LongHashtable.get (J)Ljava/lang/Object;
      // 4e: checkcast java/lang/Object
      // 51: astore 8
      // 53: aload 8
      // 55: ifnonnull 61
      // 58: new java/lang/Object
      // 5b: dup
      // 5c: invokespecial java/util/Hashtable.<init> ()V
      // 5f: astore 8
      // 61: aload 8
      // 63: aload 1
      // 64: invokeinterface net/rim/device/api/synchronization/SyncCollection.getSyncName ()Ljava/lang/String; 1
      // 69: aload 7
      // 6b: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 6e: pop
      // 6f: aload 0
      // 70: getfield net/rim/device/internal/synchronization/SyncManagerImpl._multiServiceOtaAdapters Lnet/rim/device/api/util/LongHashtable;
      // 73: lload 3
      // 74: aload 8
      // 76: invokevirtual net/rim/device/api/util/LongHashtable.put (JLjava/lang/Object;)Ljava/lang/Object;
      // 79: pop
      // 7a: goto 8d
      // 7d: aload 0
      // 7e: getfield net/rim/device/internal/synchronization/SyncManagerImpl._otaAdapters Ljava/util/Hashtable;
      // 81: aload 1
      // 82: invokeinterface net/rim/device/api/synchronization/SyncCollection.getSyncName ()Ljava/lang/String; 1
      // 87: aload 7
      // 89: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 8c: pop
      // 8d: bipush 1
      // 8e: ireturn
      // 8f: astore 7
      // 91: new java/lang/Object
      // 94: dup
      // 95: ldc_w "Cannot enable OTA sync for SID "
      // 98: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 9b: astore 8
      // 9d: aload 8
      // 9f: lload 3
      // a0: invokevirtual java/lang/StringBuffer.append (J)Ljava/lang/StringBuffer;
      // a3: ldc_w ": "
      // a6: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // a9: aload 1
      // aa: invokeinterface net/rim/device/api/synchronization/SyncCollection.getSyncName ()Ljava/lang/String; 1
      // af: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // b2: pop
      // b3: aload 8
      // b5: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // b8: invokestatic net/rim/device/internal/synchronization/ota/api/Logger.logErrorMessage (Ljava/lang/Object;)V
      // bb: bipush 0
      // bc: ireturn
      // bd: astore 7
      // bf: bipush 0
      // c0: ireturn
      // try (24 -> 73): 74 null
      // try (24 -> 73): 94 null
   }

   private final void enableSerialSynchronization(SyncCollection collection) {
      if (!(collection instanceof Object)) {
         Arrays.add(this._serialCollections, collection);
      } else {
         long sid = ((MultiServiceSyncCollection)collection).getSid();
         Hashtable ht = (Hashtable)this._multiServiceSerialCollections.get(sid);
         if (ht == null) {
            ht = (Hashtable)(new Object(1));
         }

         ht.put(collection.getSyncName(), collection);
         this._multiServiceSerialCollections.put(sid, ht);
      }
   }

   @Override
   public final synchronized void disableSynchronization(SyncCollection collection) {
      if (collection instanceof Object) {
         label19:
         try {
            this.disableOTASynchronization(collection);
            return;
         } finally {
            break label19;
         }
      }

      this.disableSerialSynchronization(collection);
   }

   private final void disableOTASynchronization(SyncCollection collection) {
      String syncName = collection.getSyncName();
      OTASyncCollectionAdapter adapter = null;
      if (!(collection instanceof Object)) {
         adapter = (OTASyncCollectionAdapter)this._otaAdapters.get(syncName);
         if (adapter != null) {
            this._otaAdapters.remove(syncName);
         }
      } else {
         long sid = ((MultiServiceSyncCollection)collection).getSid();
         Hashtable ht = (Hashtable)this._multiServiceOtaAdapters.get(sid);
         if (ht != null) {
            adapter = (OTASyncCollectionAdapter)ht.get(syncName);
            if (adapter != null) {
               ht.remove(syncName);
            }
         }
      }

      if (adapter == null) {
         throw new Object("CNR");
      }

      this.disableOTASynchronization(adapter, false);
   }

   private final void disableOTASynchronization(OTASyncCollectionAdapter adapter, boolean systemOTASyncDisabled) {
      adapter.unbind(systemOTASyncDisabled);
   }

   private final void disableSerialSynchronization(SyncCollection collection) {
      if (!this.doesSerialSyncCollectionExist(collection, true)) {
         throw new Object("CNR");
      }

      if (!(collection instanceof Object)) {
         Arrays.remove(this._serialCollections, collection);
      } else {
         long sid = ((MultiServiceSyncCollection)collection).getSid();
         Hashtable ht = (Hashtable)this._multiServiceSerialCollections.get(sid);
         if (ht != null) {
            ht.remove(collection.getSyncName());
            return;
         }
      }
   }

   final boolean isOTASynchronizationEnabled(SyncCollection collection) {
      Hashtable otaAdapters = null;
      Object var5;
      if (!(collection instanceof Object)) {
         var5 = this._otaAdapters;
      } else {
         long sid = ((MultiServiceSyncCollection)collection).getSid();
         var5 = this._multiServiceOtaAdapters.get(sid);
      }

      if (var5 == null) {
         return false;
      }

      String syncCollectionName = collection.getSyncName();
      return ((Hashtable)var5).get(syncCollectionName) != null;
   }

   private final boolean isSerialSynchronizationEnabled(SyncCollection collection) {
      return this.doesSerialSyncCollectionExist(collection, false);
   }

   private final boolean doesSerialSyncCollectionExist(SyncCollection collection, boolean exactMatchOnly) {
      boolean result = false;
      if (!(collection instanceof Object)) {
         String var8 = collection.getSyncName();
         int var9 = this._serialCollections.length;

         for (int i = 0; i < var9; i++) {
            if (var8.equals(this._serialCollections[i].getSyncName())) {
               if (!exactMatchOnly || this._serialCollections[i] == collection) {
                  return true;
               }
               break;
            }
         }
      } else {
         long sid = ((MultiServiceSyncCollection)collection).getSid();
         Hashtable ht = (Hashtable)this._multiServiceSerialCollections.get(sid);
         if (ht != null) {
            SyncCollection registeredCollection = (SyncCollection)ht.get(collection.getSyncName());
            if (registeredCollection != null && (!exactMatchOnly || collection == registeredCollection)) {
               return true;
            }
         }
      }

      return result;
   }

   private final boolean isOTASynchronizationAvailable() {
      return this._syncAgent.getDefaultSid() != -1;
   }

   private final synchronized void switchFromSerialToOTASynchronization(ServiceIdentifier serviceIdentifier) {
      if (this.isSerialSyncInProgress()) {
         this._otaEnabledServiceIdentifiers.addElement(serviceIdentifier);
      } else {
         long sid = serviceIdentifier.getSid();
         if (!this.isPrimaryService(serviceIdentifier)) {
            Hashtable ht = (Hashtable)this._multiServiceSerialCollections.get(sid);
            if (ht == null) {
               ht = (Hashtable)(new Object(0));
            }

            Vector databaseNames = this._syncAgent.getDefaultSyncDataBasesFor(sid);
            Enumeration e = databaseNames.elements();

            while (e.hasMoreElements()) {
               String databaseName = (String)e.nextElement();
               MultiServiceSyncCollection collection = (MultiServiceSyncCollection)ht.get(databaseName);
               if (collection != null) {
                  if (collection instanceof Object && this.enableOTASynchronization(collection)) {
                     ht.remove(databaseName);
                  }
               } else {
                  collection = ((MultiServiceSyncCollection)this._multiServiceSyncCollections.get(databaseName)).getCollection(sid);
                  this.enableOTASynchronization(collection);
               }
            }
         } else {
            SyncCollection[] serialCollections = this._serialCollections;
            int numSerialCollections = serialCollections.length;

            for (int i = numSerialCollections - 1; i >= 0; i--) {
               SyncCollection syncCollection = serialCollections[i];
               if (syncCollection instanceof Object && this.enableOTASynchronization(syncCollection)) {
                  Arrays.remove(this._serialCollections, syncCollection);
               }
            }

            Hashtable associatedHt = (Hashtable)this._multiServiceSerialCollections.get(sid);
            if (associatedHt != null) {
               Vector collectionNamesToRemove = (Vector)(new Object(associatedHt.size()));
               Enumeration e = associatedHt.elements();

               while (e.hasMoreElements()) {
                  MultiServiceSyncCollection msCollection = (MultiServiceSyncCollection)e.nextElement();
                  if (msCollection instanceof Object && this.enableOTASynchronization(msCollection)) {
                     collectionNamesToRemove.addElement(msCollection.getSyncName());
                  }
               }

               for (int i = 0; i < collectionNamesToRemove.size(); i++) {
                  associatedHt.remove(collectionNamesToRemove.elementAt(i));
               }
            }

            Hashtable noServiceHt = (Hashtable)this._multiServiceSerialCollections.get(-1);
            if (noServiceHt != null) {
               Vector collectionNamesToRemove = (Vector)(new Object(noServiceHt.size()));
               Enumeration e = noServiceHt.elements();

               while (e.hasMoreElements()) {
                  MultiServiceSyncCollection msCollection = (MultiServiceSyncCollection)e.nextElement();
                  msCollection.setSid(sid);
                  if (msCollection instanceof Object && this.enableOTASynchronization(msCollection)) {
                     collectionNamesToRemove.addElement(msCollection.getSyncName());
                  }
               }

               for (int i = 0; i < collectionNamesToRemove.size(); i++) {
                  noServiceHt.remove(collectionNamesToRemove.elementAt(i));
               }
            }
         }
      }
   }

   private final synchronized void switchFromOTAToSerialSynchronization(ServiceIdentifier serviceIdentifier) {
      if (this.isPrimaryService(serviceIdentifier)) {
         Enumeration e = this._otaAdapters.elements();

         while (e.hasMoreElements()) {
            OTASyncCollectionAdapter adapter = (OTASyncCollectionAdapter)e.nextElement();
            SyncCollection collection = adapter.getSyncCollection();
            this.disableOTASynchronization(adapter, true);
            this.enableSerialSynchronization(collection);
            Thread.yield();
         }

         this._otaAdapters.clear();
      }

      long sid = serviceIdentifier.getSid();
      Hashtable ht = (Hashtable)this._multiServiceOtaAdapters.get(sid);
      if (ht != null) {
         Enumeration e = ht.elements();

         while (e.hasMoreElements()) {
            OTASyncCollectionAdapter adapter = (OTASyncCollectionAdapter)e.nextElement();
            SyncCollection collection = adapter.getSyncCollection();
            this.disableOTASynchronization(adapter, true);
            this.enableSerialSynchronization(collection);
            Thread.yield();
         }

         ht.clear();
      }
   }

   @Override
   public final synchronized void addSyncEventListener(SyncEventListener listener) {
      this._syncEventListeners = ListenerUtilities.addListener(this._syncEventListeners, listener);
   }

   @Override
   public final synchronized void removeSyncEventListener(SyncEventListener listener) {
      this._syncEventListeners = ListenerUtilities.removeListener(this._syncEventListeners, listener);
   }

   private final void notifySyncEventListeners(int eventId, Object object) {
      Object[] syncEventListeners = this._syncEventListeners;
      if (syncEventListeners != null) {
         for (int i = syncEventListeners.length - 1; i >= 0; i--) {
            try {
               SyncEventListener listener = (SyncEventListener)syncEventListeners[i];
               listener.syncEventOccurred(eventId, object);
            } finally {
               continue;
            }
         }
      }
   }

   final synchronized SyncCollection[] getSyncCollections() {
      int numSyncCollections = this._serialCollections.length;
      int arraySize = numSyncCollections + this._otaAdapters.size();
      SyncCollection[] syncCollections = new Object[arraySize];
      System.arraycopy(this._serialCollections, 0, syncCollections, 0, numSyncCollections);
      Enumeration adapters = this._otaAdapters.elements();

      while (adapters.hasMoreElements()) {
         syncCollections[numSyncCollections++] = ((OTASyncCollectionAdapter)adapters.nextElement()).getSyncCollection();
      }

      Enumeration hts = this._multiServiceSerialCollections.elements();

      while (hts.hasMoreElements()) {
         Hashtable ht = (Hashtable)hts.nextElement();
         int newArraySize = arraySize + ht.size();
         Array.resize(syncCollections, newArraySize);
         Enumeration e = ht.elements();

         while (e.hasMoreElements()) {
            SyncCollection collection = (SyncCollection)e.nextElement();
            syncCollections[arraySize++] = collection;
         }
      }

      Enumeration hts2 = this._multiServiceOtaAdapters.elements();

      while (hts2.hasMoreElements()) {
         Hashtable ht = (Hashtable)hts2.nextElement();
         int newArraySize = arraySize + ht.size();
         Array.resize(syncCollections, newArraySize);
         Enumeration e = ht.elements();

         while (e.hasMoreElements()) {
            SyncCollection collection = ((OTASyncCollectionAdapter)e.nextElement()).getSyncCollection();
            syncCollections[arraySize++] = collection;
         }
      }

      return syncCollections;
   }

   final synchronized void addSerialSyncListener(SerialSyncListener listener) {
      this._serialSyncListeners = ListenerUtilities.addListener(this._serialSyncListeners, listener);
   }

   final synchronized void removeSerialSyncListener(SerialSyncListener listener) {
      this._serialSyncListeners = ListenerUtilities.removeListener(this._serialSyncListeners, listener);
   }

   final void fireSerialSyncStartedOrStopped(boolean serialSyncStarted, boolean serialRestoreCompleted) {
      this._serialSyncInProgress = serialSyncStarted;
      Object[] serialSyncListeners = this._serialSyncListeners;
      if (serialSyncListeners != null) {
         for (int i = serialSyncListeners.length - 1; i >= 0; i--) {
            SerialSyncListener listener = (SerialSyncListener)serialSyncListeners[i];
            if (serialSyncStarted) {
               listener.serialSyncStarted();
            } else {
               listener.serialSyncStopped();
            }
         }
      }

      this.notifySyncEventListeners(serialSyncStarted ? 1 : 2, null);
      if (!serialSyncStarted && serialRestoreCompleted && !ActivationService.hasThisDeviceBeenActivated()) {
         int numServiceIdentifiers = this._otaEnabledServiceIdentifiers.size();

         for (int i = numServiceIdentifiers - 1; i >= 0; i--) {
            ServiceIdentifier serviceIdentifier = (ServiceIdentifier)this._otaEnabledServiceIdentifiers.elementAt(i);
            this._syncAgent.notifyListenersWith(1, serviceIdentifier);
            this._otaEnabledServiceIdentifiers.removeElement(serviceIdentifier);
         }
      }
   }

   final void collectionUpdatedSerially(SyncCollection syncCollection) {
      if (syncCollection instanceof Object) {
         String databaseName = null;
         if (syncCollection instanceof OTASyncParametersProvider) {
            databaseName = ((OTASyncParametersProvider)syncCollection).getDatabaseName();
         }

         if (databaseName == null || databaseName.length() <= 0) {
            databaseName = syncCollection.getSyncName();
         }

         long sid = -1;
         if (syncCollection instanceof Object) {
            sid = ((MultiServiceSyncCollection)syncCollection).getSid();
         }

         this._syncAgent.markDatabaseAsUsedByOtherSyncSources(sid, databaseName);
      }
   }

   private final OTASyncCollectionAdapter getOTASyncCollectionAdapterFor(SyncCollection aSyncCollection) {
      OTASyncCollectionAdapter result = null;
      if (aSyncCollection != null) {
         Enumeration e = this._otaAdapters.elements();

         while (e.hasMoreElements()) {
            OTASyncCollectionAdapter anOTASyncCollectionAdapter = (OTASyncCollectionAdapter)e.nextElement();
            if (aSyncCollection == anOTASyncCollectionAdapter.getSyncCollection()) {
               result = anOTASyncCollectionAdapter;
               break;
            }
         }

         if (result == null) {
            Enumeration e1 = this._multiServiceOtaAdapters.elements();

            while (e1.hasMoreElements()) {
               Hashtable ht = (Hashtable)e1.nextElement();
               Enumeration e2 = ht.elements();

               while (e2.hasMoreElements()) {
                  OTASyncCollectionAdapter adapter = (OTASyncCollectionAdapter)e2.nextElement();
                  if (aSyncCollection == adapter.getSyncCollection()) {
                     result = adapter;
                     break;
                  }
               }

               if (result != null) {
                  return result;
               }
            }
         }
      }

      return result;
   }

   @Override
   public final void syncImmediately(SyncCollection collection) {
      OTASyncCollectionAdapter anOTASyncCollectionAdapter = this.getOTASyncCollectionAdapterFor(collection);
      if (anOTASyncCollectionAdapter != null) {
         anOTASyncCollectionAdapter.flush();
      }
   }

   @Override
   public final boolean isCollectionResetSupported() {
      long sid = this._syncAgent.getDefaultSid();
      return sid != -1 ? this._syncAgent.isCollectionResetSupported(sid) : false;
   }

   @Override
   public final boolean isSyncCompleted(SyncCollection collection) {
      OTASyncCollectionAdapter anOTASyncCollectionAdapter = this.getOTASyncCollectionAdapterFor(collection);
      return anOTASyncCollectionAdapter != null ? anOTASyncCollectionAdapter.isOTASyncCompleted() : false;
   }

   @Override
   public final void triggerSlowSync(SyncCollection collection) {
      OTASyncCollectionAdapter anOTASyncCollectionAdapter = this.getOTASyncCollectionAdapterFor(collection);
      if (anOTASyncCollectionAdapter != null) {
         anOTASyncCollectionAdapter.reInitialize();
      }
   }

   @Override
   public final boolean isSerialSyncInProgress() {
      return this._serialSyncInProgress;
   }

   @Override
   public final void setSerialSyncStatusMessage(String message) {
      RIMGlobalMessagePoster.postGlobalEvent(-6549094840388549801L, 0, 0, message, null);
   }

   private final synchronized void handleITPolicyUpdate() {
      boolean allWirelessSyncDisabled = ITPolicy.getBoolean(33, 1, false);
      SyncCollection[] collections = this.getSyncCollections();

      for (int i = collections.length - 1; i >= 0; i--) {
         SyncCollection collection = collections[i];
         boolean allow;
         if (allWirelessSyncDisabled) {
            allow = false;
         } else {
            int itPolicyTag = this._itPolicyTagMap.get(collection);
            if (itPolicyTag != -1) {
               allow = !ITPolicy.getBoolean(33, itPolicyTag, false);
            } else {
               allow = true;
            }
         }

         this.allowOTASync(collection, allow, (byte)1);
      }
   }

   private final boolean isPrimaryService(ServiceIdentifier serviceIdentifier) {
      boolean result = false;
      ServiceRecord sr = serviceIdentifier.getServiceRecord();
      if (sr.getServiceIdentifierType() == 1 || sr.getServiceIdentifierType() == 0 && sr.isSecureService()) {
         result = true;
      }

      return result;
   }

   @Override
   public final void onSyncAgentEvent(int eventID, Object object) {
      switch (eventID) {
         case 1:
            this.switchFromSerialToOTASynchronization((ServiceIdentifier)object);
            return;
         case 2:
            this.switchFromOTAToSerialSynchronization((ServiceIdentifier)object);
            return;
         case 4:
            this.allowOTASync((SyncAgentUrl)object, true);
            return;
         case 5:
            this.allowOTASync((SyncAgentUrl)object, false);
            return;
         case 21:
            this.notifySyncEventListeners(3, this.findSyncCollection((SyncAgentUrl)object, false));
            return;
         case 22:
            this.notifySyncEventListeners(4, this.findSyncCollection((SyncAgentUrl)object, false));
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         this.handleITPolicyUpdate();
      }
   }

   @Override
   public final SyncCollectionStatistics[] getSyncCollectionStatistics() {
      SyncCollection[] syncCollections = this.getSyncCollections();
      int syncCount = syncCollections.length;
      SyncCollectionStatistics[] statsCollections = new Object[syncCount];
      int statsCount = 0;

      for (int i = 0; i < syncCount; i++) {
         if (syncCollections[i] instanceof Object) {
            statsCollections[statsCount++] = (SyncCollectionStatistics)syncCollections[i];
         }
      }

      Array.resize(statsCollections, statsCount);
      return statsCollections;
   }

   @Override
   public final String getLocalizedCollectionName(long sid, String name, Locale locale) {
      if (this._otaAdapters.containsKey(name)) {
         String localizedName = ((OTASyncCollectionAdapter)this._otaAdapters.get(name)).getSyncCollection().getSyncName(locale);
         if (localizedName != null) {
            return localizedName;
         }
      }

      Hashtable ht = (Hashtable)this._multiServiceOtaAdapters.get(sid);
      if (ht.contains(name)) {
         String localizedName = ((OTASyncCollectionAdapter)ht.get(name)).getSyncCollection().getSyncName(locale);
         if (localizedName != null) {
            return localizedName;
         }
      }

      return name;
   }
}
