package net.rim.device.apps.api.transmission.rim.otasync;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.vm.PersistentInteger;

public final class OTAFMConfiguration implements Persistable {
   private boolean _serverStatusUpdates;
   private boolean _serverDeletes;
   private boolean _serverFiling;
   private boolean _serverPurging;
   private boolean _serverEnabled;
   private boolean _serverInitialized;
   private boolean _serverPurgedMessageList;
   private int _deleteOnIndex;
   private boolean _userDeletes;
   private boolean _userFiling;
   private boolean _userPurgedMessageList;
   private int _userConflicts;
   private boolean _ackReceived;
   private boolean _folderListRequired;
   private static final byte SERIAL_SYNC_SB_FIELD_TYPE = 81;
   private static final long DISABLED_OTAFM_CONFIG_GUID = 1318377347043828279L;
   private static final boolean SEPARATE_CONFIGURATION_OF_SERVICES = false;
   public static final int CONFLICT_DEVICE_WINS = 0;
   public static final int CONFLICT_DESKTOP_WINS = 1;
   private static final int DELETE_ON_ID = 0;
   private static final int WIRELESS_DELETES_ID = 1;
   private static final int WIRELESS_FILING_ID = 2;
   private static final int CONFLICTS_ID = 3;
   private static final int WIRELESS_ENABLED_ID = 4;

   public OTAFMConfiguration() {
      this._userConflicts = 1;
   }

   private OTAFMConfiguration(OTAFMConfiguration source) {
      this._serverStatusUpdates = source._serverStatusUpdates;
      this._serverDeletes = source._serverDeletes;
      this._serverFiling = source._serverFiling;
      this._serverPurging = source._serverPurging;
      this._userDeletes = source._userDeletes;
      this._userFiling = source._userFiling;
      this._userConflicts = source._userConflicts;
      this._serverPurgedMessageList = source._serverPurgedMessageList;
   }

   public static final OTAFMConfiguration getDisabledConfiguration() {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      OTAFMConfiguration configuration = (OTAFMConfiguration)appRegistry.getOrWaitFor(1318377347043828279L);
      if (configuration == null) {
         configuration = new OTAFMConfiguration();
         configuration.setAcknowledgementReceived();
         appRegistry.put(1318377347043828279L, configuration);
      }

      return configuration;
   }

   public final OTAFMConfiguration clone() {
      OTAFMConfiguration configuration = this;
      if (configuration != getDisabledConfiguration()) {
         configuration = new OTAFMConfiguration(this);
      }

      return configuration;
   }

   @Override
   public final boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (!(o instanceof OTAFMConfiguration)) {
         return false;
      }

      OTAFMConfiguration other = (OTAFMConfiguration)o;
      return this._serverStatusUpdates == other._serverStatusUpdates
         && this._serverDeletes == other._serverDeletes
         && this._serverFiling == other._serverFiling
         && this._serverPurging == other._serverPurging
         && this._serverEnabled == other._serverEnabled
         && this._serverInitialized == other._serverInitialized
         && this._userDeletes == other._userDeletes
         && this._userFiling == other._userFiling
         && this._userConflicts == other._userConflicts
         && this._serverPurgedMessageList == other._serverPurgedMessageList;
   }

   public final boolean userConfigurationEqual(OTAFMConfiguration other) {
      return this.getWirelessDeletesSetting() == other._userDeletes
         && this.getWirelessFilingSetting() == other._userFiling
         && this._userConflicts == other._userConflicts;
   }

   public final void setAcknowledgementReceived() {
      this._ackReceived = true;
   }

   public final void clearAcknowledgementReceived() {
      this._ackReceived = false;
   }

   public final boolean hasAcknowledgementBeenReceived() {
      return this._ackReceived;
   }

   public final boolean getFolderListRequired() {
      return this._folderListRequired;
   }

   public final boolean validate(ServiceRecord sr) {
      return this._deleteOnIndex != 1 || this.getWirelessFilingEnabled() || this.getWirelessDeletesEnabled() || serialSyncAllowed(sr);
   }

   public final void setFolderListRequired(boolean folderListRequired) {
      this._folderListRequired = folderListRequired;
   }

   public final void setServerSupport(boolean statusUpdates, boolean deletes, boolean filing, boolean purging, boolean purgedMessageList) {
      this._serverStatusUpdates = statusUpdates;
      this._serverDeletes = deletes;
      this._serverFiling = filing;
      this._serverPurging = purging;
      this._serverPurgedMessageList = purgedMessageList;
   }

   public final void setUserConfiguration(boolean statusUpdates, boolean deletes, boolean filing, boolean purgedMessageList) {
      this._userDeletes = deletes;
      this._userFiling = filing;
      this._userPurgedMessageList = purgedMessageList;
   }

   public final void setUserStatusFlags(boolean userEnabled, boolean deviceInit) {
      this._serverEnabled = userEnabled;
      this._serverInitialized = deviceInit;
   }

   public final void setConflictResolution(int resolution) {
      this._userConflicts = resolution;
   }

   public final boolean getOTAFMEnabled() {
      boolean enabled = ITPolicy.getBoolean(23, 1, true);
      int id = PersistentInteger.getId(-2728804572467266382L, 1);
      int value = PersistentInteger.get(id);
      if (value == 0) {
         enabled = false;
      }

      return enabled;
   }

   public final boolean wirelessStatusUpdatesAllowed() {
      return this._serverStatusUpdates && this.getOTAFMEnabled();
   }

   public final boolean wirelessDeletesAllowed() {
      return this._serverDeletes && this.getOTAFMEnabled();
   }

   public final boolean wirelessFilingAllowed() {
      return this._serverFiling && this.getOTAFMEnabled();
   }

   public final boolean wirelessPurgeDeletedMessagesAllowed() {
      return this._serverPurging && this.getOTAFMEnabled();
   }

   public final boolean wirelessPurgeDeletedMessagesSetting() {
      return (this._userDeletes || this._userFiling) && this.getOTAFMEnabled();
   }

   public final boolean getWirelessStatusUpdatesSetting() {
      return (this._userDeletes || this._userFiling) && this.getOTAFMEnabled();
   }

   public final boolean getWirelessDeletesSetting() {
      return this._userDeletes && this.getOTAFMEnabled();
   }

   public final boolean getWirelessFilingSetting() {
      return this._userFiling && this.getOTAFMEnabled();
   }

   public final int getConflictResolutionSetting() {
      return this._userConflicts;
   }

   public final boolean getWirelessReconcileEnabled() {
      return this.getWirelessStatusUpdatesEnabled() || this.getWirelessDeletesEnabled() || this.getWirelessFilingEnabled();
   }

   public final boolean getWirelessStatusUpdatesEnabled() {
      return this.wirelessStatusUpdatesAllowed() && this.getWirelessStatusUpdatesSetting();
   }

   public final boolean getWirelessDeletesEnabled() {
      return this.wirelessDeletesAllowed() && this.getWirelessDeletesSetting();
   }

   public final boolean getWirelessFilingEnabled() {
      return this.wirelessFilingAllowed() && this.getWirelessFilingSetting();
   }

   public final boolean getWirelessPurgeDeletedMessagesEnabled() {
      return this.wirelessPurgeDeletedMessagesAllowed() && (this.getWirelessDeletesEnabled() || this.getWirelessFilingEnabled());
   }

   public final boolean isFirstInitialization() {
      return !this._serverInitialized;
   }

   public final boolean isPurgedMessageListSupported() {
      return this._userPurgedMessageList && this._serverPurgedMessageList && this.getOTAFMEnabled();
   }

   public final boolean isTemporaryDisabled() {
      return !this._serverEnabled;
   }

   private static final boolean serialSyncAllowed(ServiceRecord sr) {
      byte[] appData = sr.getApplicationData();
      if (appData != null && appData.length > 1) {
         int length = appData.length;
         int offset = 1;

         while (offset < length) {
            if (appData[offset] == 81) {
               if (appData[offset + 2] != 0) {
                  return true;
               }

               return false;
            }

            if (++offset < length) {
               offset += 1 + (appData[offset] & 255);
            }
         }
      }

      return true;
   }

   public final Field getEditableField(ServiceRecord serviceRecord) {
      ResourceBundle commonResources = ResourceBundle.getBundle(-8414468493733347764L, "net.rim.device.apps.internal.resource.Common");
      ResourceBundle messageResources = ResourceBundle.getBundle(1758158344049992104L, "net.rim.device.apps.internal.resource.Message");
      String[] offOnOptions = new Object[]{commonResources.getString(107), commonResources.getString(106)};
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object());
      OTAFMConfigurationManager configManager = OTAFMConfigurationManager$Instance.getInstance();
      boolean disabledConfiguration = getDisabledConfiguration() == this;
      boolean wirelessDeletesAllowed = !disabledConfiguration && configManager.wirelessDeletesAllowed(serviceRecord);
      boolean wirelessFilingAllowed = !disabledConfiguration && configManager.wirelessFilingAllowed(serviceRecord);
      boolean serialSyncAllowed = serialSyncAllowed(serviceRecord);
      ChoiceField deleteOnField = null;
      if (serialSyncAllowed || wirelessDeletesAllowed) {
         String[] deleteOnOptions = new Object[]{messageResources.getString(25), messageResources.getString(166), messageResources.getString(171)};
         switch (CMIMEUtilities.getDeleteOnLocation(serviceRecord)) {
            case 0:
            default:
               this._deleteOnIndex = 0;
               break;
            case 1:
               this._deleteOnIndex = 1;
               break;
            case 2:
               this._deleteOnIndex = 2;
         }

         deleteOnField = new ObjectChoiceFieldWithId(0, messageResources.getString(30), deleteOnOptions, this._deleteOnIndex);
         vfm.add(deleteOnField);
      }

      ChoiceField wirelessDeletesField = null;
      if (wirelessDeletesAllowed || wirelessFilingAllowed) {
         boolean enabled = this._userDeletes | this._userFiling;
         vfm.add(new ObjectChoiceFieldWithId(4, messageResources.getString(164), offOnOptions, enabled ? 1 : 0));
      }

      if (wirelessDeletesAllowed || wirelessFilingAllowed) {
         String[] conflictOptions = new Object[]{messageResources.getString(158), messageResources.getString(167)};
         vfm.add(new ObjectChoiceFieldWithId(3, messageResources.getString(162), conflictOptions, this._userConflicts));
      }

      if (deleteOnField != null && wirelessDeletesField != null) {
         FieldChangeListener listener = new DeleteOnWirelessDeletesListener(deleteOnField, wirelessDeletesField);
         deleteOnField.setChangeListener(listener);
         wirelessDeletesField.setChangeListener(listener);
      }

      vfm.add((Field)(new Object()));
      return vfm;
   }

   public final boolean grabDataFromField(Field field, ServiceRecord serviceRecord) {
      boolean changesMade = false;
      boolean isDisabledConfiguration = getDisabledConfiguration() == this;
      if (field instanceof Object) {
         VerticalFieldManager vfm = (VerticalFieldManager)field;
         int count = vfm.getFieldCount();

         for (int i = 0; i < count; i++) {
            Field child = vfm.getField(i);
            if (child instanceof ObjectChoiceFieldWithId) {
               ObjectChoiceFieldWithId configItem = (ObjectChoiceFieldWithId)child;
               int index = configItem.getSelectedIndex();
               if (configItem._id == 0 || !isDisabledConfiguration) {
                  switch (configItem._id) {
                     case -1:
                        break;
                     case 0:
                     default:
                        int deleteOnIndex = CMIMEUtilities.getDeleteOnLocation(serviceRecord);
                        switch (index) {
                           case 0:
                           default:
                              this._deleteOnIndex = 0;
                              break;
                           case 1:
                              this._deleteOnIndex = 1;
                              break;
                           case 2:
                              this._deleteOnIndex = 2;
                        }

                        if (deleteOnIndex != this._deleteOnIndex) {
                           MessageListOptions options = MessageListOptions.getOptions();
                           String name = null;
                           String uid = null;
                           if (serviceRecord != null) {
                              name = serviceRecord.getName();
                              uid = serviceRecord.getUid();
                           }

                           options.setDeleteOnLocation(name, uid, this._deleteOnIndex);
                           options.commit();
                           changesMade = true;
                        }
                        break;
                     case 1:
                        if (this._userDeletes != (index != 0)) {
                           this._userDeletes = index != 0;
                           changesMade = true;
                        }
                        break;
                     case 2:
                        if (this._userFiling != (index != 0)) {
                           this._userFiling = index != 0;
                           changesMade = true;
                        }
                        break;
                     case 3:
                        if (this._userConflicts != index) {
                           this._userConflicts = index;
                           changesMade = true;
                        }
                        break;
                     case 4:
                        int defaultIndex = !this._userFiling && !this._userDeletes ? 0 : 1;
                        if (defaultIndex != index) {
                           this._userDeletes = index != 0 & this.wirelessDeletesAllowed();
                           this._userFiling = index != 0 & this.wirelessFilingAllowed();
                           changesMade = true;
                        }
                  }
               }
            }
         }
      }

      return changesMade;
   }

   public static final void cleanup(ServiceRecord serviceRecord) {
   }
}
