package net.rim.device.internal.ui.autotext;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.DataBuffer;

final class AutoTextDataVersion extends OTASyncCapableSyncItem {
   private PersistentObject _store = RIMPersistentStore.getPersistentObject(5042818100109773249L);
   private int _version;
   public static final long REGISTRY_ID;
   public static final long PERSISTENCE_ID;
   private static AutoTextDataVersion _instance;
   private static final int DB_VERSION;

   private AutoTextDataVersion() {
      synchronized (this._store) {
         if (this._store.getContents() == null) {
            this._store.setContents(new Object(this._version), 51);
            this._store.commit();
         }
      }

      this._version = this._store.getContents();
   }

   static final AutoTextDataVersion getInstance() {
      return _instance;
   }

   @Override
   public final String getSyncName() {
      return "AutoText Data Version";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final synchronized boolean setSyncData(DataBuffer buffer, int version) {
      try {
         buffer.readShort();
         buffer.readByte();
         this._version = buffer.readInt();
         return true;
      } finally {
         ;
      }
   }

   @Override
   public final synchronized boolean getSyncData(DataBuffer buffer, int version) {
      buffer.writeShort(4);
      buffer.writeByte(0);
      buffer.writeInt(this._version);
      return true;
   }

   final void setVersion(int version) {
      if (this._version != version) {
         this._version = version;
         this._store.setContents(new Object(this._version), 51);
         this._store.commit();
         this.fireSyncItemUpdated();
      }
   }

   final int getVersion() {
      return this._version;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (AutoTextDataVersion)ar.getOrWaitFor(-1064388854033361755L);
      if (_instance == null) {
         _instance = new AutoTextDataVersion();
         ar.put(-1064388854033361755L, _instance);
         SyncManager sm = SyncManager.getInstance();
         if (sm != null) {
            sm.enableSynchronization(_instance);
         }
      }
   }
}
