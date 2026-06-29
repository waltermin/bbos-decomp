package net.rim.device.api.crypto.certificate;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncCollectionStatusProvider;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.DirtyBits;

public class CertificateSummaryDataSyncCollection
   implements SystemListener,
   CollectionListener,
   CollectionEventSource,
   SyncCollection,
   SyncConverter,
   OTASyncCapable,
   SyncCollectionStatusProvider {
   private Vector _certificateSummaryDataSyncModels;
   private boolean _startupComplete;
   private ContextObjectWR _convertContextWR = (ContextObjectWR)(new Object(19));
   private KeyStore[] _keyStoresToSync;
   protected CollectionListenerManager _listenerManager = (CollectionListenerManager)(new Object());
   private static final int COLLECTION_VERSION = 0;
   private static final long INSTANCE_ID = -5204122086742082501L;
   private static CertificateSummaryDataSyncCollection _instance;

   public static CertificateSummaryDataSyncCollection getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (CertificateSummaryDataSyncCollection)applicationRegistry.getOrWaitFor(-5204122086742082501L);
         if (_instance == null) {
            _instance = new CertificateSummaryDataSyncCollection();
            applicationRegistry.put(-5204122086742082501L, _instance);
         }
      }

      return _instance;
   }

   private CertificateSummaryDataSyncCollection() {
      this._keyStoresToSync = new KeyStore[0];
      this._certificateSummaryDataSyncModels = (Vector)(new Object());
      Proxy.getInstance().addSystemListener(this);
   }

   public void registerKeyStore(KeyStore keyStore) {
      if (this._startupComplete) {
         throw new Object();
      }

      synchronized (keyStore) {
         synchronized (this) {
            if (Arrays.contains(this._keyStoresToSync, keyStore)) {
               return;
            }

            Enumeration keyStoreElements = keyStore.elements();

            while (keyStoreElements.hasMoreElements()) {
               this.add((KeyStoreData)keyStoreElements.nextElement());
            }
         }

         Arrays.add(this._keyStoresToSync, keyStore);
         keyStore.addCollectionListener(this);
      }
   }

   private synchronized void add(KeyStoreData keyStoreData) {
      CertificateSummaryDataSyncModel certificateSummaryDataSyncModel = CertificateSummaryDataSyncModelFactory.createCertificateSummaryDataSyncModel(
         keyStoreData
      );
      if (certificateSummaryDataSyncModel != null && !this._certificateSummaryDataSyncModels.contains(certificateSummaryDataSyncModel)) {
         this._certificateSummaryDataSyncModels.addElement(certificateSummaryDataSyncModel);
         this._listenerManager.fireElementAdded(this, certificateSummaryDataSyncModel);
      }
   }

   private synchronized void remove(KeyStoreData keyStoreData) {
      CertificateSummaryDataSyncModel certificateSummaryDataSyncModel = CertificateSummaryDataSyncModelFactory.createCertificateSummaryDataSyncModel(
         keyStoreData
      );
      if (certificateSummaryDataSyncModel != null) {
         int existingIndex = this._certificateSummaryDataSyncModels.indexOf(certificateSummaryDataSyncModel);
         if (existingIndex >= 0) {
            this._certificateSummaryDataSyncModels.removeElementAt(existingIndex);
            this._listenerManager.fireElementRemoved(this, certificateSummaryDataSyncModel);
         }
      }
   }

   @Override
   public void powerUp() {
      this._startupComplete = true;
      Proxy.getInstance().removeSystemListener(this);
      SyncManager.getInstance().enableSynchronization(this);
   }

   @Override
   public void powerOff() {
   }

   @Override
   public void batteryLow() {
   }

   @Override
   public void batteryGood() {
   }

   @Override
   public void batteryStatusChange(int status) {
   }

   @Override
   public void addCollectionListener(Object listener) {
      this._listenerManager.addCollectionListener(listener);
   }

   @Override
   public void removeCollectionListener(Object listener) {
      this._listenerManager.removeCollectionListener(listener);
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this.add((KeyStoreData)element);
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      this.remove((KeyStoreData)element);
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
   }

   @Override
   public void reset(Collection collection) {
   }

   @Override
   public int getSyncVersion() {
      return 0;
   }

   @Override
   public String getSyncName() {
      return "Certificate Summary Data";
   }

   @Override
   public String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public int getSyncObjectCount() {
      return this._certificateSummaryDataSyncModels.size();
   }

   @Override
   public synchronized SyncObject[] getSyncObjects() {
      CertificateSummaryDataSyncModel[] certificateSummaryDataSyncModelArray = new CertificateSummaryDataSyncModel[this._certificateSummaryDataSyncModels
         .size()];
      this._certificateSummaryDataSyncModels.copyInto(certificateSummaryDataSyncModelArray);
      return certificateSummaryDataSyncModelArray;
   }

   @Override
   public synchronized SyncObject getSyncObject(int uid) {
      int numCertificateSummaryDataSyncModels = this._certificateSummaryDataSyncModels.size();

      for (int i = 0; i < numCertificateSummaryDataSyncModels; i++) {
         CertificateSummaryDataSyncModel currentModel = (CertificateSummaryDataSyncModel)this._certificateSummaryDataSyncModels.elementAt(i);
         if (currentModel.hashCode() == uid) {
            return currentModel;
         }
      }

      return null;
   }

   @Override
   public boolean isSyncObjectDirty(SyncObject object) {
      return DirtyBits.isDirty(object);
   }

   @Override
   public void setSyncObjectDirty(SyncObject object) {
      DirtyBits.setDirty(object);
   }

   @Override
   public void clearSyncObjectDirty(SyncObject object) {
      DirtyBits.setClean(object);
   }

   @Override
   public boolean addSyncObject(SyncObject object) {
      return false;
   }

   @Override
   public boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return false;
   }

   @Override
   public boolean removeSyncObject(SyncObject object) {
      return false;
   }

   @Override
   public boolean removeAllSyncObjects() {
      return false;
   }

   @Override
   public void beginTransaction() {
   }

   @Override
   public void endTransaction() {
   }

   @Override
   public boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (object instanceof CertificateSummaryDataSyncModel) {
         SyncBuffer syncBuffer = (SyncBuffer)(new Object(buffer, version, object.getUID()));
         return syncBuffer.addModel((CertificateSummaryDataSyncModel)object, this._convertContextWR.getContextObject());
      } else {
         return false;
      }
   }

   @Override
   public SyncObject convert(DataBuffer data, int version, int UID) {
      return null;
   }

   @Override
   public SyncCollectionSchema getSchema() {
      return null;
   }

   @Override
   public boolean isWritableForSerialSync() {
      return false;
   }

   @Override
   public boolean isReadableForSerialSync() {
      return true;
   }

   @Override
   public boolean isWritableForOTASL() {
      return false;
   }

   @Override
   public int getOTASLControlMask() {
      return 0;
   }
}
