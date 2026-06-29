package net.rim.wica.runtime.persistence.internal.backup;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntEnumeration;
import net.rim.vm.OTAUpgrade;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.lifecycle.WicletInfo;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.management.AGInfo;
import net.rim.wica.runtime.management.RuntimeInfo;
import net.rim.wica.runtime.metadata.internal.def.ComponentDefStruct;
import net.rim.wica.runtime.persistence.ApplicationSyncModel;
import net.rim.wica.runtime.persistence.WicletStore;
import net.rim.wica.runtime.persistence.internal.PersistenceServiceImpl;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.util.SerializerUtil;
import net.rim.wica.runtime.util.internal.RuntimeUtilities;

public final class RESyncCollection implements SyncCollection, SyncConverter, SyncEventListener, GlobalEventListener {
   private PersistenceServiceImpl _persistenceService;
   private EventService _eventService;
   private boolean _continue = true;
   private AGInfo _restoredServerInfo;
   private Vector _restoredApplications;
   private ApplicationSyncModel _currentApplicationSync;
   private Vector _nonSystemWiclets;
   private RuntimeInfo _runtimeInfo;
   private static final int SYNC_VERSION = 3;
   private static final int NUMBER_OF_WICLETS_PARTS_V2 = 11;
   private static final byte RE_WITHOUT_WICLETS = -1;
   private static final byte RE_VERSION = 0;
   private static final byte RUNTIME_INFO = 1;
   private static final byte AG_INFO_ARRAY = 2;
   private static final byte IN_REQUEST_QUEUE = 3;
   private static final byte INCOMING_MESSAGES = 4;
   private static final byte OUTGOING_MESSAGES = 5;
   private static final byte DEDICATED_OUTGOING_MESSAGES = 6;
   private static final byte COMMON_RESOURCES = 7;
   private static final byte WICLET_INFO = 0;
   private static final byte WICLET_COLLISION_TABLE = 1;
   private static final byte WICLET_GLOBAL_DATA_DEF_ID = 2;
   private static final byte WICLET_DATA_DEFS = 3;
   private static final byte WICLET_MSG_DEFS = 4;
   private static final byte WICLET_SCRIPT_DEFS = 5;
   private static final byte WICLET_UI_DEFS = 6;
   private static final byte WICLET_INCOMING_MESSAGES = 7;
   private static final byte WICLET_RESOURCES = 8;
   private static final byte WICLET_DATA = 9;
   private static final byte WICLET_MAPPING_TABLE = 10;
   private static final byte APPLICATION_SYNC_DATA = 0;

   public RESyncCollection(PersistenceServiceImpl persistenceService, EventService eventService) {
      this._persistenceService = persistenceService;
      this._eventService = eventService;
      SyncManager syncManager = SyncManager.getInstance();
      syncManager.enableSynchronization(this);
      syncManager.addSyncEventListener(this);
      Application.getApplication().addGlobalEventListener(this);
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      int size = this.getSyncObjectCount();
      SyncObject[] objects = new Object[size];

      for (int i = 0; i < size; i++) {
         objects[i] = new RESyncCollection$RESyncObject(i - 1);
      }

      return objects;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      return null;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final int getSyncObjectCount() {
      this.init();
      int size = 0;
      if (this._runtimeInfo != null && this._runtimeInfo.isRegistered()) {
         size = 1;
         if (this._nonSystemWiclets != null) {
            size += this._nonSystemWiclets.size();
         }
      }

      return size;
   }

   @Override
   public final int getSyncVersion() {
      return 3;
   }

   @Override
   public final String getSyncName() {
      return RuntimeResources.getString(13);
   }

   @Override
   public final String getSyncName(Locale locale) {
      return this.getSyncName();
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final void beginTransaction() {
      this.init();
   }

   @Override
   public final void endTransaction() {
      this.clean();
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer dataBuffer, int version) {
      int uid = object.getUID();
      if (uid == -1) {
         SerializerUtil.writeString(dataBuffer, (byte)0, RuntimeUtilities.getRuntimeVersion(true));
         RuntimeInfoSerializer.getInstance().serialize(dataBuffer, (byte)1, this._runtimeInfo);
         return true;
      } else if (uid >= 0) {
         ApplicationSyncModelSerializer.getInstance()
            .serialize(dataBuffer, (byte)0, this.prepareApplication((WicletStore)this._nonSystemWiclets.elementAt(uid)));
         return true;
      } else {
         return false;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      if (uid != -1) {
         if (uid >= 0 && this._continue) {
            boolean var14 = false /* VF: Semaphore variable */;

            try {
               var14 = true;
               if (version >= 3) {
                  this.addApplication((ApplicationSyncModel)ApplicationSyncModelSerializer.getInstance().deserialize(dataBuffer));
                  var14 = false;
               } else {
                  int wicletIndex = uid / 11;
                  int wicletPart = uid % 11;
                  if (wicletPart == 0) {
                     this._currentApplicationSync = new ApplicationSyncModel();
                  }

                  if (this._currentApplicationSync == null) {
                     this._continue = false;
                     var14 = false;
                  } else {
                     switch (wicletPart) {
                        case -1:
                           ConverterUtilities.skipField(dataBuffer);
                           Logger.log("MDS Runtime Sync", "Unrecognized object type in restore file.", 2);
                           this._continue = false;
                           return null;
                        case 0:
                        default:
                           WicletInfo info = (WicletInfo)WicletInfoSerializer.getInstance().deserialize(dataBuffer);
                           DeploymentDescriptor descriptor = new DeploymentDescriptor();
                           info.setDescriptor(descriptor);
                           descriptor.setUri(info.getUri());
                           descriptor.setVersion(info.getVersion());
                           descriptor.setLanguages(new String[]{"en"});
                           descriptor.setName(info.getName());
                           descriptor.setVendor(info.getVendor());
                           descriptor.setDescription(info.getDescription());
                           descriptor.setTargetFolder(info.getTargetFolder());
                           this._currentApplicationSync.setId(info.getId());
                           this._currentApplicationSync.setDescriptor(descriptor);
                           this._currentApplicationSync.setPackageLocation(info.getPackageLocation());
                           var14 = false;
                           break;
                        case 1:
                           StringToIntHashtableSerializer.getInstance().deserialize(dataBuffer);
                           var14 = false;
                           break;
                        case 2:
                           ConverterUtilities.readInt(dataBuffer);
                           var14 = false;
                           break;
                        case 3:
                           this.restoreDef(dataBuffer, version);
                           var14 = false;
                           break;
                        case 4:
                           this.restoreDef(dataBuffer, version);
                           var14 = false;
                           break;
                        case 5:
                           this.restoreDef(dataBuffer, version);
                           var14 = false;
                           break;
                        case 6:
                           this.restoreDef(dataBuffer, version);
                           var14 = false;
                           break;
                        case 7:
                           ByteArraySerializer.getInstance().deserializeBigVector(dataBuffer);
                           var14 = false;
                           break;
                        case 8:
                           PersResourceStructSerializer.getInstance().deserializeArray(dataBuffer);
                           var14 = false;
                           break;
                        case 9:
                           this._currentApplicationSync
                              .setCollections((IntPersistablePair[])IntPersistablePairSerializer.getInstance().deserializeArray(dataBuffer));
                           var14 = false;
                           break;
                        case 10:
                           ConverterUtilities.readByteArray(dataBuffer);
                           if (this._continue) {
                              this.addApplication(this._currentApplicationSync);
                           }

                           this._currentApplicationSync = null;
                           var14 = false;
                     }
                  }
               }
            } finally {
               if (var14) {
                  Logger.log("MDS Runtime Sync", "Error during application restore.", 2);
                  this._continue = false;
                  return this._continue ? new RESyncCollection$RESyncObject(uid) : null;
               }
            }
         }
      } else {
         this._continue = true;
         RuntimeInfo runtimeInfo = null;

         label187:
         try {
            int type = ConverterUtilities.getType(dataBuffer);
            if (type != 0) {
               Logger.log("MDS Runtime Sync", "Error restoring runtime service information.", 2);
               this._continue = false;
               return null;
            }

            ConverterUtilities.readString(dataBuffer);

            while (dataBuffer.available() > 0 && this._continue) {
               type = ConverterUtilities.getType(dataBuffer);
               if (type == 1) {
                  runtimeInfo = (RuntimeInfo)RuntimeInfoSerializer.getInstance().deserialize(dataBuffer);
               } else if (version < 3) {
                  switch (type) {
                     case 1:
                        this._continue = false;
                        break;
                     case 2:
                     default:
                        PersistableAGInfoSerializer.getInstance().deserializeArray(dataBuffer);
                        break;
                     case 3:
                        ByteArraySerializer.getInstance().deserializeBigVector(dataBuffer);
                        break;
                     case 4:
                        ByteArraySerializer.getInstance().deserializeBigVector(dataBuffer);
                        break;
                     case 5:
                        ByteArraySerializer.getInstance().deserializeBigVector(dataBuffer);
                        break;
                     case 6:
                        LongBigVectorPairSerializer.getInstance().deserializeArray(dataBuffer);
                        break;
                     case 7:
                        PersResourceStructSerializer.getInstance().deserializeArray(dataBuffer);
                  }
               } else {
                  this._continue = false;
               }
            }
         } catch (Throwable var16) {
            Logger.log("MDS Runtime Sync", "Error restoring runtime service information.", 2);
            e.printStackTrace();
            this._continue = false;
            break label187;
         }

         this._restoredServerInfo = this._continue ? runtimeInfo.getDefaultAGInfo() : null;
         synchronized (this._persistenceService) {
            this._persistenceService.storeRestoredAG(this._restoredServerInfo);
         }
      }

      return this._continue ? new RESyncCollection$RESyncObject(uid) : null;
   }

   @Override
   public final void syncEventOccurred(int eventId, Object object) {
      switch (eventId) {
         case 1:
         default:
            Logger.log("S S-");
            this.init();
            return;
         case 2:
            Logger.log("S S+");
            this.endSync();
         case 0:
      }
   }

   private final void init() {
      synchronized (this._persistenceService) {
         this._runtimeInfo = this._persistenceService.loadRuntimeInfo();
         this._nonSystemWiclets = this.filterApplications(this._persistenceService.getApplications());
      }
   }

   private final void clean() {
      this._runtimeInfo = null;
      this._nonSystemWiclets = null;
      this._restoredApplications = null;
      this._restoredServerInfo = null;
      this.nullSerializersInstances();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -5179361672050507927L && !OTAUpgrade.isOTASLInProgress()) {
         Logger.log("M OTASL+");
         this.endSync();
      }
   }

   private final void endSync() {
      this.clean();
      AGInfo serverInfo;
      synchronized (this._persistenceService) {
         serverInfo = this._persistenceService.loadRestoredAG();
         if (serverInfo != null) {
            this._persistenceService.storeRestoredAG(null);
         }
      }

      if (serverInfo != null) {
         this._eventService.dispatchEvent(1000, serverInfo);
      }
   }

   private final Vector filterApplications(Enumeration applications) {
      Vector v = (Vector)(new Object());

      while (applications.hasMoreElements()) {
         WicletStore store = (WicletStore)applications.nextElement();
         if (!store.getInfo().isSystemApplication()) {
            v.addElement(store);
         }
      }

      return v;
   }

   private final ApplicationSyncModel prepareApplication(WicletStore store) {
      ApplicationSyncModel model = null;
      WicletInfo info = store.getInfo();
      model = new ApplicationSyncModel();
      long id = info.getId();
      model.setId(id);
      model.setDescriptor(info.getDescriptor());
      model.setPackageLocation(info.getPackageLocation());
      IntEnumeration defIds = store.loadDataDefinitionIds();
      if (defIds != null) {
         Vector v = (Vector)(new Object());

         while (defIds.hasMoreElements()) {
            int defId = defIds.nextElement();
            v.addElement(new IntPersistablePair(defId, store.loadData(defId)));
         }

         IntPersistablePair[] data = new IntPersistablePair[v.size()];
         v.copyInto(data);
         model.setCollections(data);
      }

      return model;
   }

   private final void addApplication(ApplicationSyncModel model) {
      if (model != null && this._persistenceService.getApplication(model.getDescriptor().getUri()) == null) {
         if (this._restoredApplications == null) {
            synchronized (this._persistenceService) {
               this._restoredApplications = this._persistenceService.loadRestoredApplications();
            }

            if (this._restoredApplications == null) {
               this._restoredApplications = (Vector)(new Object());
            }
         }

         this._restoredApplications.addElement(model);
         synchronized (this._persistenceService) {
            this._persistenceService.storeRestoredApplications(this._restoredApplications);
         }
      }
   }

   private final void nullSerializersInstances() {
      AGInfoSerializer.nullInstance();
      AlertSerializer.nullInstance();
      ByteArraySerializer.nullInstance();
      ComponentDefStructSerializer.nullInstance();
      DependencySerializer.nullInstance();
      DeploymentDescriptorSerializer.nullInstance();
      IntPersistablePairSerializer.nullInstnace();
      LongBigVectorPairSerializer.nullInstance();
      LongIntHashtableSerializer.nullInstance();
      ObjectSerializer.nullInstance();
      PersDataCollectionStructSerializer.nullInstance();
      PersistableAGInfoSerializer.nullInstance();
      PersKeylessDataCollectionStructSerializer.nullInstance();
      PersResourceStructSerializer.nullInstance();
      PersStandaloneDataStructSerializer.nullInstance();
      QuarantineTaskInfoSerializer.nullInstance();
      RuntimeInfoSerializer.nullInstance();
      StringSerializer.nullInstance();
      StringToIntHashtableSerializer.nullInstance();
      UninstallTaskInfoSerializer.nullInstance();
      UpgradeTaskInfoSerializer.nullInstance();
      WicletAdminPolicySerializer.nullInstance();
      WicletInfoSerializer.nullInstance();
      ApplicationSyncModelSerializer.nullInstance();
   }

   private final ComponentDefStruct restoreDef(DataBuffer dataBuffer, int version) {
      return (ComponentDefStruct)ComponentDefStructSerializer.getInstance().deserialize(dataBuffer);
   }
}
