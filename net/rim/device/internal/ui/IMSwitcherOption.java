package net.rim.device.internal.ui;

import java.io.EOFException;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.DataBuffer;

public final class IMSwitcherOption extends OTASyncCapableSyncItem {
   private byte _state = 1;
   private PersistentObject _store = RIMPersistentStore.getPersistentObject(-9055322099094090145L);
   public static final byte SHOW_ALWAYS;
   public static final byte DONT_SHOW;
   public static final byte DISABLED;
   public static final long REGISTRY_ID;
   public static final long PERSISTENCE_ID;
   private static IMSwitcherOption _instance;
   private static final int DB_VERSION;

   public static final IMSwitcherOption getInstance() {
      return _instance;
   }

   private IMSwitcherOption() {
      synchronized (this._store) {
         if (this._store.getContents() == null) {
            this._store.setContents(new Byte(this._state), 51);
            this._store.commit();
         }
      }

      this._state = (Byte)this._store.getContents();
   }

   @Override
   public final String getSyncName() {
      return "Input Method Switcher Option";
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
         this._state = buffer.readByte();
         return true;
      } catch (EOFException var4) {
         return true;
      }
   }

   @Override
   public final synchronized boolean getSyncData(DataBuffer buffer, int version) {
      buffer.writeShort(1);
      buffer.writeByte(0);
      buffer.writeByte(this._state);
      return true;
   }

   public final void setState(byte state) {
      if (this._state != state && state != 0) {
         this._state = state;
         this._store.setContents(new Byte(this._state), 51);
         this._store.commit();
         this.fireSyncItemUpdated();
      }
   }

   public final byte getState() {
      return this._state;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (IMSwitcherOption)ar.getOrWaitFor(-8427367214306252346L);
      if (_instance == null) {
         _instance = new IMSwitcherOption();
         ar.put(-8427367214306252346L, _instance);
         SyncManager sm = SyncManager.getInstance();
         if (sm != null) {
            sm.enableSynchronization(_instance);
         }
      }
   }
}
