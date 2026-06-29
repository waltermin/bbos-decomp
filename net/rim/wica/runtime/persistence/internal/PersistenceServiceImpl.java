package net.rim.wica.runtime.persistence.internal;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.lowmemory.LowMemoryListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.LongIntHashtable;
import net.rim.device.internal.system.NvStore;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.lifecycle.WicletInfo;
import net.rim.wica.runtime.management.AGInfo;
import net.rim.wica.runtime.management.RuntimeInfo;
import net.rim.wica.runtime.persistence.CredentialStore;
import net.rim.wica.runtime.persistence.MessageStore;
import net.rim.wica.runtime.persistence.PersistableObject;
import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.runtime.persistence.WicletStore;
import net.rim.wica.runtime.persistence.internal.backup.RESyncCollection;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.util.LongLongHashtable;

public final class PersistenceServiceImpl implements PersistenceService, Serviceable, PersistentContentListener, LowMemoryListener {
   private LongSubstore _msgUpgradeMapsSubstore;
   private PersistentObject _persistedApplications;
   private Vector _applications;
   private LongHashtable _stores;
   private static final long KEY_APPLICATIONS = -7574762932512512223L;
   private static final long KEY_RESTORED_APPS = 4042714553988139895L;
   private static final long KEY_RESTORED_AG = -3447862290083721068L;
   private static final long KEY_RUNTIME_INFO = 3507216612420288000L;
   private static final long KEY_ALIASES = -8781988818785864316L;
   private static final long KEY_WICLET_STATUS = 5524481063654985060L;
   private static final long KEY_RE_KEY_PAIR = 5145488587492369108L;
   private static final long KEY_AG_PUB_KEYS = 7596987072159554945L;
   private static final long KEY_RECOVERY_REG_KEYS = 5698934363825554085L;
   private static final long KEY_SECONDARY_REG_KEYS = -1217456019626433655L;
   private static final long KEY_MDS_REG_KEYS = 2845920021721577652L;
   private static final long KEY_INCOMING_SEQUENCE = -5719508791209638287L;
   private static final long KEY_OUTGOING_SEQUENCE = 5753714757979124242L;
   private static final long KEY_RES = -178717707915500492L;
   private static final long KEY_MSG_UPGRADE_MAPS = -7334925706471457607L;
   private static final int NV_FIELD_MDS_RESET_KEYS = 46;
   private static final int NV_FIELD_MDS_RECOVERY_RESET_KEYS = 47;
   static Class class$net$rim$wica$runtime$event$EventService;

   public final boolean freeResources() {
      boolean result = false;
      Enumeration e = this.getApplications();

      while (e.hasMoreElements()) {
         WicletStoreImpl store = (WicletStoreImpl)e.nextElement();
         result |= store.freeResources() > 0;
      }

      return result;
   }

   final synchronized void saveApplications() {
      this._persistedApplications.commit();
   }

   @Override
   public final synchronized void deleteApplication(WicletStore store) {
      this._applications.removeElement(((WicletStoreImpl)store).getModel());
      this.saveApplications();
      this._stores.remove(store.getInfo().getId());
   }

   @Override
   public final WicletStore getApplication(long id) {
      return (WicletStore)this._stores.get(id);
   }

   @Override
   public final WicletStore getApplication(String uri) {
      Enumeration e = this._stores.elements();

      while (e.hasMoreElements()) {
         WicletStore store = (WicletStore)e.nextElement();
         if (store.getInfo().getUri().equals(uri)) {
            return store;
         }
      }

      return null;
   }

   @Override
   public final Enumeration getApplications() {
      return this._stores.elements();
   }

   @Override
   public final synchronized WicletStore createApplication(long id) {
      WicletStore store = (WicletStore)this._stores.get(id);
      if (store == null) {
         PersistedApplicationModel persistedModel = new PersistedApplicationModel();
         this._applications.addElement(persistedModel);
         this.saveApplications();
         store = new WicletStoreImpl(this, persistedModel);
         this._stores.put(id, store);
      }

      return store;
   }

   @Override
   public final MessageStore getMessageStore() {
      return new MessageStoreImpl();
   }

   @Override
   public final CredentialStore getCredentialStore() {
      return new CredentialStoreImpl();
   }

   @Override
   public final void storeRuntimeInfo(RuntimeInfo runtimeInfo) {
      storeObject(runtimeInfo, 3507216612420288000L);
   }

   @Override
   public final RuntimeInfo loadRuntimeInfo() {
      Object obj = loadObject(3507216612420288000L);
      return !(obj instanceof RuntimeInfo) ? null : (RuntimeInfo)obj;
   }

   @Override
   public final void storeREKeyPair(Object[] keys) {
      storeObject(new PersistableObject(keys), 5145488587492369108L, true);
   }

   @Override
   public final Object[] loadREKeyPair() {
      Object obj = loadObject(5145488587492369108L);
      if (!(obj instanceof PersistableObject)) {
         return null;
      }

      obj = ((PersistableObject)obj).getObject();
      return obj instanceof Object[] ? (Object[])obj : null;
   }

   @Override
   public final void storeAGPublicKeys(byte[] agPublicKeys) {
      storeObject(new PersistableObject(agPublicKeys), 7596987072159554945L);
   }

   @Override
   public final byte[] loadAGPublicKeys() {
      return this.getPersistableByteArray(7596987072159554945L);
   }

   @Override
   public final void storeRegKeys(byte[] regKeys) {
      storeObject(new PersistableObject(regKeys), 2845920021721577652L);
   }

   @Override
   public final byte[] loadRegKeys() {
      return this.getPersistableByteArray(2845920021721577652L);
   }

   @Override
   public final void storeResetKeys(byte[] resetKeys) {
      NvStore.writeData(46, resetKeys);
   }

   @Override
   public final byte[] loadResetKeys() {
      return NvStore.readData(46);
   }

   @Override
   public final void storeSecondaryRegKeys(byte[] regKeys) {
      storeObject(new PersistableObject(regKeys), -1217456019626433655L);
   }

   @Override
   public final byte[] loadSecondaryRegKeys() {
      return this.getPersistableByteArray(-1217456019626433655L);
   }

   @Override
   public final void storeRecoveryRegKeys(byte[] regKeys) {
      storeObject(new PersistableObject(regKeys), 5698934363825554085L);
   }

   @Override
   public final byte[] loadRecoveryRegKeys() {
      return this.getPersistableByteArray(5698934363825554085L);
   }

   @Override
   public final void storeRecoveryResetKeys(byte[] resetKeys) {
      NvStore.writeData(47, resetKeys);
   }

   @Override
   public final byte[] loadRecoveryResetKeys() {
      return NvStore.readData(47);
   }

   @Override
   public final long[] loadIncomingSequence() {
      return this.getPersistableLongArray(-5719508791209638287L);
   }

   @Override
   public final void storeIncomingSequence(long[] sequence) {
      storeGenericType(sequence, -5719508791209638287L);
   }

   @Override
   public final void storeIncomingSequence(PersistableObject sequence) {
      storeObject(sequence, -5719508791209638287L);
   }

   @Override
   public final long[] loadOutgoingSequence() {
      return this.getPersistableLongArray(5753714757979124242L);
   }

   @Override
   public final void storeOutgoingSequence(long[] sequence) {
      storeGenericType(sequence, 5753714757979124242L);
   }

   @Override
   public final void storeWicletAlias(LongLongHashtable aliases) {
      storeGenericType(aliases, -8781988818785864316L);
   }

   @Override
   public final LongLongHashtable loadWicletAlias() {
      Object obj = loadGenericType(-8781988818785864316L);
      return !(obj instanceof LongLongHashtable) ? null : (LongLongHashtable)obj;
   }

   @Override
   public final void storeMessageUpgradeMap(long id, IntIntHashtable map) {
      this._msgUpgradeMapsSubstore.put(id, map);
   }

   @Override
   public final IntIntHashtable loadMessageUpgradeMap(long id) {
      return (IntIntHashtable)this._msgUpgradeMapsSubstore.get(id);
   }

   @Override
   public final void deleteMessageUpgradeMap(long id) {
      this._msgUpgradeMapsSubstore.remove(id);
   }

   @Override
   public final LongIntHashtable loadWicletStatus() {
      Object obj = loadGenericType(5524481063654985060L);
      return (LongIntHashtable)(!(obj instanceof Object) ? null : obj);
   }

   @Override
   public final void storeWicletStatus(LongIntHashtable statusTable) {
      storeGenericType(statusTable, 5524481063654985060L, false);
   }

   @Override
   public final Vector loadRestoredApplications() {
      Object result = loadGenericType(4042714553988139895L);
      return (Vector)(!(result instanceof Object) ? null : result);
   }

   @Override
   public final void storeRestoredApplications(Vector restoredApplications) {
      storeGenericType(restoredApplications, 4042714553988139895L);
   }

   @Override
   public final void storeRestoredAG(AGInfo info) {
      storeGenericType(info, -3447862290083721068L);
   }

   @Override
   public final AGInfo loadRestoredAG() {
      Object result = loadGenericType(-3447862290083721068L);
      return !(result instanceof AGInfo) ? null : (AGInfo)result;
   }

   @Override
   public final boolean freeStaleObject(int priority) {
      boolean result = false;
      if (priority == 2) {
         result = this.freeResources();
      }

      return result;
   }

   @Override
   public final void setServices(ServiceProvider provider) {
      new RESyncCollection(
         this,
         (EventService)provider.getService(
            class$net$rim$wica$runtime$event$EventService == null
               ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
               : class$net$rim$wica$runtime$event$EventService
         )
      );
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      Enumeration e = this.getApplications();

      while (e.hasMoreElements()) {
         WicletStoreImpl store = (WicletStoreImpl)e.nextElement();
         store.persistentContentModeChanged(generation);
      }
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   static final void storeGenericType(Object obj, long key) {
      storeObject(new PersistableObject(obj), key);
   }

   static final void storeGenericType(Object obj, long key, boolean readOnly) {
      storeObject(new PersistableObject(obj), key, readOnly);
   }

   static final Object loadObject(long key) {
      PersistentObject persistentObject = PersistentStore.getPersistentObject(key);
      return persistentObject.getContents();
   }

   static final void storeObject(Object object, long key) {
      storeObject(object, key, false);
   }

   static final void storeObject(Object object, long key, boolean readOnly) {
      if (readOnly) {
         ObjectGroup.createGroupIgnoreTooBig(object);
      }

      PersistentObject persistentObject = PersistentStore.getPersistentObject(key);
      persistentObject.setContents(object, 51);
      persistentObject.commit();
   }

   private final long[] getPersistableLongArray(long key) {
      Object obj = loadGenericType(key);
      return !(obj instanceof long[]) ? null : (long[])obj;
   }

   public PersistenceServiceImpl() {
      PersistentContent.addListener(this);
      this._persistedApplications = PersistentStore.getPersistentObject(-7574762932512512223L);
      Object obj = this._persistedApplications.getContents();
      if (!(obj instanceof PersistableObject)) {
         this._applications = (Vector)(new Object());
         this._persistedApplications.setContents(new PersistableObject(this._applications), 51);
         this._persistedApplications.commit();
      } else {
         Object contents = ((PersistableObject)obj).getObject();
         if (contents instanceof Object) {
            this._applications = (Vector)contents;
         }
      }

      this._stores = (LongHashtable)(new Object(this._applications.size()));

      for (int i = this._applications.size() - 1; i >= 0; i--) {
         PersistedApplicationModel app = (PersistedApplicationModel)this._applications.elementAt(i);
         WicletInfo info = app._info;
         if (info != null) {
            this._stores.put(info.getId(), new WicletStoreImpl(this, app));
         } else {
            this._applications.removeElementAt(i);
            this._persistedApplications.commit();
         }
      }

      this._msgUpgradeMapsSubstore = new LongSubstore(-7334925706471457607L);
      LowMemoryManager.addLowMemoryListener(this);
   }

   private final byte[] getPersistableByteArray(long key) {
      Object obj = loadGenericType(key);
      return !(obj instanceof byte[]) ? null : (byte[])obj;
   }

   static final Object loadGenericType(long key) {
      Object obj = loadObject(key);
      return !(obj instanceof PersistableObject) ? null : ((PersistableObject)obj).getObject();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
