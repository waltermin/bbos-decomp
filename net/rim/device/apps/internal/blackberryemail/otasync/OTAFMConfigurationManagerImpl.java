package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.transmission.rim.RIMMessagingFolderManagement;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfiguration;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfigurationManager;

public final class OTAFMConfigurationManagerImpl implements OTAFMConfigurationManager {
   private OTAFMConfigurationData _configurations;
   private SyncWorkerThread _workerThread;
   static final long COLLECTION_GUID = 8822586609645349199L;
   private static final byte OTAFM_SB_FIELD_TYPE = 80;
   private static final byte OTAFM_DELETE_ON_DEFAULT_FIELD_TYPE = 82;

   private OTAFMConfigurationManagerImpl() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(8822586609645349199L);
      this._configurations = (OTAFMConfigurationData)persistentObject.getContents();
      if (this._configurations == null) {
         synchronized (persistentObject) {
            this._configurations = (OTAFMConfigurationData)persistentObject.getContents();
            if (this._configurations == null) {
               this._configurations = new OTAFMConfigurationData();
               persistentObject.setContents(this._configurations, 51);
               persistentObject.commit();
            }
         }
      }
   }

   public static final void register() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      if (ar.getOrWaitFor(-2200702284699521671L) == null) {
         ar.put(-2200702284699521671L, new OTAFMConfigurationManagerImpl());
      }
   }

   final void start(SyncWorkerThread workerThread) {
      this._workerThread = workerThread;
   }

   final void messageListRestored() {
      this._configurations._messageListRestored = true;
   }

   final void serviceRecordAdded(ServiceRecord serviceRecord) {
      this.serviceRecordUpdated(null, serviceRecord);
   }

   final void serviceRecordUpdated(ServiceRecord oldSR, ServiceRecord newSR) {
      if (this.containsOTAFMInfo(newSR)) {
         this._workerThread.sleepFor(15);
         this._workerThread.sendConfigurationQuery(newSR);
      } else if (oldSR == null) {
         this.serviceRecordRemoved(newSR);
      } else {
         if (this.containsOTAFMInfo(oldSR)) {
            this.serviceRecordRemoved(oldSR);
         }
      }
   }

   final boolean containsOTAFMInfo(ServiceRecord serviceRecord) {
      if (serviceRecord.getType() != 0) {
         return false;
      }

      byte[] appData = serviceRecord.getApplicationData();
      if (appData != null && appData.length > 1) {
         int length = appData.length;
         int offset = 1;

         while (offset < length) {
            if (appData[offset] == 80) {
               return true;
            }

            if (++offset < length) {
               offset += 1 + (appData[offset] & 255);
            }
         }
      }

      return false;
   }

   final void serviceRecordRemoved(ServiceRecord serviceRecord) {
      OTAFMConfiguration.cleanup(serviceRecord);
      this._configurations.remove(serviceRecord);
      PersistentObject.commit(this._configurations);
   }

   final ServiceRecord[] getServiceRecords() {
      return this._configurations._serviceRecords;
   }

   final ServiceSyncInfo getServiceSyncInfo(ServiceRecord serviceRecord) {
      return this._configurations.get(serviceRecord);
   }

   final RIMMessagingFolderManagement getTransmitBuffer(ServiceRecord serviceRecord) {
      ServiceSyncInfo syncInfo = this.getServiceSyncInfo(serviceRecord);
      return syncInfo != null ? syncInfo.compileQueuedCommands() : null;
   }

   final boolean hasUnflushedBuffers() {
      ServiceRecord[] serviceRecords = this.getServiceRecords();
      synchronized (serviceRecords) {
         for (int i = serviceRecords.length - 1; i >= 0; i--) {
            ServiceSyncInfo syncInfo = this.getServiceSyncInfo(serviceRecords[i]);
            if (syncInfo != null && syncInfo.size() > 0) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public final boolean isOTAFMAvailable() {
      return this._configurations.size() > 0;
   }

   @Override
   public final OTAFMConfiguration getConfiguration(ServiceRecord serviceRecord) {
      ServiceSyncInfo syncInfo = this._configurations.get(serviceRecord);
      OTAFMConfiguration configuration = null;
      if (syncInfo != null) {
         configuration = syncInfo._otafmConfiguration;
      }

      if (configuration == null) {
         configuration = OTAFMConfiguration.getDisabledConfiguration();
      }

      return configuration;
   }

   @Override
   public final boolean wirelessDeletesAllowed(ServiceRecord serviceRecord) {
      OTAFMConfiguration configuration = this.getConfiguration(serviceRecord);
      return configuration != null ? configuration.wirelessDeletesAllowed() : false;
   }

   @Override
   public final boolean wirelessFilingAllowed(ServiceRecord serviceRecord) {
      OTAFMConfiguration configuration = this.getConfiguration(serviceRecord);
      return configuration != null ? configuration.wirelessFilingAllowed() : false;
   }

   @Override
   public final void updateConfiguration(ServiceRecord serviceRecord, OTAFMConfiguration newConfiguration, boolean fromServer) {
      boolean newServiceRecord = false;
      ServiceSyncInfo syncInfo = this._configurations.get(serviceRecord);
      if (syncInfo == null) {
         syncInfo = new ServiceSyncInfo();
         this._configurations.put(serviceRecord, syncInfo);
         syncInfo.setRestoreMessageList(this._configurations._messageListRestored);
         newServiceRecord = true;
      }

      OTAFMConfiguration oldConfiguration = this.getConfiguration(serviceRecord);
      syncInfo._otafmConfiguration = newConfiguration;
      boolean sendConfig = false;
      if (!fromServer) {
         sendConfig = true;
      } else if (!newConfiguration.wirelessStatusUpdatesAllowed()
         && !newConfiguration.wirelessDeletesAllowed()
         && !newConfiguration.wirelessFilingAllowed()
         && !newConfiguration.wirelessPurgeDeletedMessagesAllowed()) {
         newConfiguration.setAcknowledgementReceived();
      } else if (newConfiguration.isFirstInitialization()) {
         newConfiguration.setUserConfiguration(true, true, true, true);
         sendConfig = true;
      } else if (newConfiguration.isTemporaryDisabled()) {
         sendConfig = true;
      } else {
         newConfiguration.setAcknowledgementReceived();
      }

      if (newServiceRecord) {
         byte[] appData = serviceRecord.getApplicationData();
         if (appData != null && appData.length > 1) {
            int length = appData.length;
            int offset = 1;
            int deleteOnLocation = -1;

            label106:
            while (offset < length) {
               if (appData[offset] == 82) {
                  if (appData[offset + 1] == 1) {
                     int value = appData[offset + 2] & 255;
                     switch (value) {
                        case -1:
                           break label106;
                        case 0:
                        default:
                           deleteOnLocation = 0;
                           break label106;
                        case 1:
                           deleteOnLocation = 1;
                           break label106;
                        case 2:
                           deleteOnLocation = 2;
                     }
                  }
                  break;
               }

               if (++offset < length) {
                  offset += 1 + (appData[offset] & 255);
               }
            }

            int currentDeleteOnLocationFromMessageListOptions = MessageListOptions.getOptions()
               .getDeleteOnLocation(serviceRecord.getName(), serviceRecord.getUid(), -1);
            if (currentDeleteOnLocationFromMessageListOptions == -1 && deleteOnLocation != -1) {
               MessageListOptions options = MessageListOptions.getOptions();
               options.setDeleteOnLocation(serviceRecord.getName(), serviceRecord.getUid(), deleteOnLocation);
               options.commit();
            }
         }
      }

      boolean oldDeleteEnabled = oldConfiguration.getWirelessDeletesEnabled() && !oldConfiguration.isTemporaryDisabled();
      boolean oldFilingEnabled = oldConfiguration.getWirelessFilingEnabled() && !oldConfiguration.isTemporaryDisabled();
      boolean newDeleteEnabled = newConfiguration.getWirelessDeletesEnabled();
      boolean newFilingEnabled = newConfiguration.getWirelessFilingEnabled();
      boolean updateMessageList = false;
      boolean updateFolderList = false;
      newConfiguration.setFolderListRequired(oldConfiguration.getFolderListRequired());
      if (sendConfig) {
         newConfiguration.setUserStatusFlags(newDeleteEnabled && newFilingEnabled, true);
         this._workerThread.sendConfiguration(serviceRecord, newConfiguration);
      }

      if (!oldDeleteEnabled && newDeleteEnabled) {
         updateMessageList = true;
      }

      if (!oldFilingEnabled && newFilingEnabled) {
         updateMessageList = false;
         updateFolderList = true;
      }

      if (updateFolderList) {
         if (sendConfig) {
            newConfiguration.setFolderListRequired(true);
         } else {
            this._workerThread.sendFolderListRequest(serviceRecord);
         }
      }

      if (updateMessageList) {
         this._workerThread.sendMessageList(serviceRecord);
      }

      PersistentObject.commit(this._configurations);
   }
}
