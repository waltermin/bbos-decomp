package net.rim.device.apps.internal.blackberryemail.email.emailsetting;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;

public final class EmailSettingSyncItem extends SyncItem {
   public static final int AUTO_SIGNATURE = 1;
   public static final int EMAIL_REDIRECTION = 2;
   public static final int SAVE_IN_SENT_ITEMS = 3;
   public static final int FILTER_DEFAULT_ACTION = 4;
   private static final int NUMBER_OF_EMAIL_SIGNATURE_ITEMS = 1;
   private static PersistentObject _persistentObject;
   private static IntHashtable _optionsTable;
   private static long EMAIL_SETTING_SYNC_ITEM = -6482084252764382337L;
   private static final long EMAIL_SETTING_PERSISTENT_OBJECT_KEY = 5873493118923882263L;
   private static EmailSettingSyncItem _instance;

   public static final synchronized void registerSyncItem() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (EmailSettingSyncItem)applicationRegistry.get(EMAIL_SETTING_SYNC_ITEM);
         synchronized (applicationRegistry) {
            _instance = (EmailSettingSyncItem)applicationRegistry.get(EMAIL_SETTING_SYNC_ITEM);
            if (_instance == null) {
               _instance = new EmailSettingSyncItem();
               applicationRegistry.put(EMAIL_SETTING_SYNC_ITEM, _instance);
               SyncManager.getInstance().enableSynchronization(_instance);
            }
         }
      }
   }

   private static final void initialize() {
      _persistentObject = RIMPersistentStore.getPersistentObject(5873493118923882263L);
      synchronized (_persistentObject) {
         if (_persistentObject.getContents() == null) {
            IntHashtable optionsTable = (IntHashtable)(new Object(1));
            _persistentObject.setContents(optionsTable, 51);
            optionsTable.put(1, "");
            _persistentObject.commit();
         }

         _optionsTable = (IntHashtable)_persistentObject.getContents();
      }
   }

   public static final Object getOption(int optionId) {
      if (_optionsTable == null) {
         initialize();
      }

      return _optionsTable.get(optionId);
   }

   public static final void setOption(int optionId, Object value) {
      if (_optionsTable == null) {
         initialize();
      }

      _optionsTable.put(optionId, value);
      _persistentObject.commit();
   }

   @Override
   public final boolean setSyncData(DataBuffer buffer, int version) {
      try {
         while (buffer.available() > 0) {
            int tag = ConverterUtilities.getType(buffer, true);
            switch (tag) {
               case 1:
                  setOption(1, ConverterUtilities.readString(buffer));
            }
         }
      } finally {
         return true;
      }

      return true;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      ConverterUtilities.writeStringSmart(buffer, 1, (String)getOption(1));
      return true;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return "Email Settings";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }
}
