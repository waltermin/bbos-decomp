package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.options.OptionsBase;

public final class DirectConnectOptions extends OptionsBase {
   private DirectConnectOptions$PersistedDirectConnectOptions _persistedDirectConnectOptions;
   private static final long PERSISTED_DIRECT_CONNECT_OPTIONS;
   private static final int NUM_DIRECT_CONNECT_OPTIONS;
   private static final int PHONE_ONLY;
   private static final int SILENT_PRIVATE_CALL;
   private static final int SILENT_CALL_ALERT;
   private static final int SILENT_TALK_GROUP;
   private static final int TALK_GROUP_ID;
   private static final int TALK_GROUP_AREA;
   public static final int SERVICE_UNKNOWN;
   public static final int SERVICE_DISABLED;
   public static final int SERVICE_ENABLED;
   private static DirectConnectOptions _options;

   private DirectConnectOptions() {
   }

   public static final DirectConnectOptions getOptions() {
      if (_options == null) {
         _options = new DirectConnectOptions();
      }

      return _options;
   }

   final void resetOptions() {
      this.setTalkGroup(0);
      this.enableService(1, false);
      this.enableService(2, false);
      this.enableService(3, false);
      this.enableService(4, false);
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(5346469576985826158L);
      synchronized (persistentObject) {
         this._persistedDirectConnectOptions = (DirectConnectOptions$PersistedDirectConnectOptions)persistentObject.getContents();
         if (this._persistedDirectConnectOptions == null) {
            this._persistedDirectConnectOptions = new DirectConnectOptions$PersistedDirectConnectOptions();
            persistentObject.setContents(this._persistedDirectConnectOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      return (SyncItem)ApplicationRegistry.getApplicationRegistry().waitFor(9065083732853317491L);
   }

   @Override
   public final void enableSynchronization() {
   }

   private final int getOption(int optionIndex) {
      return this.getAllOptions()[optionIndex];
   }

   private final void setOption(int optionIndex, int value, boolean updateNetwork) {
      if (DirectConnect.isSupported()) {
         boolean policyEnabled = ITPolicy.getBoolean(1, true);
         if (policyEnabled || updateNetwork || optionIndex != 0) {
            this.getAllOptions()[optionIndex] = value;
            if (updateNetwork) {
               if (policyEnabled || optionIndex != 1 && optionIndex != 2 && optionIndex != 3) {
                  try {
                     switch (optionIndex) {
                        case -1:
                           break;
                        case 0:
                        default:
                           DirectConnect.enableService(1, value == 1);
                           break;
                        case 1:
                           DirectConnect.enableService(2, value == 1);
                           break;
                        case 2:
                           DirectConnect.enableService(3, value == 1);
                           break;
                        case 3:
                           DirectConnect.enableService(4, value == 1);
                           break;
                        case 4:
                           DirectConnect.setTalkGroupId(value);
                     }
                  } finally {
                     return;
                  }
               }
            }
         }
      }
   }

   final int[] getAllOptions() {
      return this._persistedDirectConnectOptions._options;
   }

   final void setAllOptions(int[] options) {
      if (DirectConnect.isSupported()) {
         for (int i = 0; i < 6; i++) {
            if (options[i] != -1) {
               this.setOption(i, options[i], true);
            }
         }
      }
   }

   public final int getTalkGroup() {
      return this.getOption(4);
   }

   public final void setTalkGroup(int talkGroup) {
      this.setOption(4, talkGroup, true);
   }

   public final void setTalkGroupWithNoNetworkUpdate(int talkGroup) {
      this.setOption(4, talkGroup, false);
   }

   public final int getTalkGroupArea() {
      return this.getOption(5);
   }

   public final void setTalkGroupArea(int talkGroupArea) {
      this.setOption(5, talkGroupArea, true);
   }

   public final boolean isPhoneOnlyMode() {
      return this.getOption(0) == 1;
   }

   public final boolean isGroupSilentEnabled() {
      return this.getOption(3) == 1;
   }

   public final boolean isAlertSilentEnabled() {
      return this.getOption(2) == 1;
   }

   public final boolean isPrivateSilentEnabled() {
      return this.getOption(1) == 1;
   }

   public final void enablePhoneOnlyMode(boolean enable) {
      this.enableService(1, enable);
   }

   private final void enableService(int serviceId, boolean enable, boolean updateNetwork) {
      int value = enable ? 1 : 0;
      int index;
      switch (serviceId) {
         case 0:
            throw new Object("Unknown Service ID");
         case 1:
         default:
            index = 0;
            break;
         case 2:
            index = 1;
            break;
         case 3:
            index = 2;
            break;
         case 4:
            index = 3;
      }

      this.setOption(index, value, updateNetwork);
   }

   public final void enableService(int serviceId, boolean enable) {
      this.enableService(serviceId, enable, true);
   }

   public final void enableServiceWithNoNetworkUpdate(int serviceId, boolean enable) {
      this.enableService(serviceId, enable, false);
   }

   public final int queryService(int serviceId) {
      switch (serviceId) {
         case 0:
            throw new Object("Unknown Service ID");
         case 1:
         default:
            return this.getOption(0);
         case 2:
            return this.getOption(1);
         case 3:
            return this.getOption(2);
         case 4:
            return this.getOption(3);
      }
   }
}
