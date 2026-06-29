package net.rim.device.apps.internal.profiles;

import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.notification.Consequence;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncListener;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.LongIntHashtable;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.deviceoptions.LegacyDeviceOptionsListener;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.ProfilesVolumeChanger;
import net.rim.device.internal.ui.UiSettings;
import net.rim.vm.Array;
import net.rim.vm.PersistentInteger;

public final class Profiles
   implements Collection,
   CollectionEventSource,
   ReadableList,
   OTASyncCapable,
   OTASyncListener,
   SyncCollection,
   SyncConverter,
   LegacyDeviceOptionsListener,
   ProfilesVolumeChanger,
   CollectionListener {
   private CollectionListenerManager _collectionListenerManager;
   private int _enabledProfileId;
   private int _previousProfileId;
   private Vector _profiles;
   private Profile _restoredEnabledProfile;
   private int _lastVersionRestored;
   private SyncCollectionSchema _schema;
   private Profiles$SourceExpireRunnable _sourceExpireRunnable = new Profiles$SourceExpireRunnable();
   private static final long PROFILES_ID = 1406503904366187671L;
   public static final byte PROFILE_IDENTIFIER_PROPERTY = 0;
   public static final byte PROFILE_NAME_PROPERTY = 1;
   public static final byte PROFILE_ENABLED_PROPERTY = 2;
   public static final byte PROFILE_SOURCES_COUNT_PROPERTY = 3;
   public static final byte PROFILE_CONSEQUENCES_COUNT_PROPERTY = 4;
   public static final byte PROFILE_SOURCE_ID_PROPERTY = 5;
   public static final byte PROFILE_CONSEQUENCE_ID_PROPERTY = 6;
   public static final byte ALERT_CONFIGURATION_PROPERTY = 7;
   public static final byte ALERT_CONFIGURATION_SETTINGS_PROPERTY = 8;
   public static final byte ALERT_CONFIGURATION_TUNENAME_UNHOLSTERED_PROPERTY = 9;
   public static final byte ALERT_CONFIGURATION_TUNENAME_HOLSTERED_PROPERTY = 10;
   public static final byte ALERT_CONFIGURATION_END_PROPERTY = 11;
   public static final byte PROFILE_ENABLED_OTA_PROPERTY = 12;
   public static final byte ALERT_CONFIGURATION_TUNENAME_FQN_UNHOLSTERED_PROPERTY = 13;
   public static final byte ALERT_CONFIGURATION_TUNENAME_FQN_HOLSTERED_PROPERTY = 14;
   public static final byte OVERRIDE_IDENTIFIER_PROPERTY = 16;
   public static final byte OVERRIDE_NAME_PROPERTY = 17;
   public static final byte OVERRIDE_ENABLED_PROPERTY = 18;
   public static final byte OVERRIDE_CONTEXT_PROPERTY = 19;
   public static final byte OVERRIDE_PROFILE_UID_PROPERTY = 20;
   public static final byte OVERRIDE_USE_TUNE_PROPERTY = 21;
   public static final byte OVERRIDE_CUSTOM_TUNE_PROPERTY = 22;
   public static final byte OVERRIDE_FROM_COUNT_PROPERTY = 23;
   public static final byte OVERRIDE_FROM_UID_PROPERTY = 24;
   public static final byte OVERRIDE_FROM_NAME_PROPERTY = 25;
   public static final byte OVERRIDE_FROM_CONTACT_INFO_PROPERTY = 32;
   public static final byte OVERRIDE_FROM_CONTACT_INFO_TYPE_PROPERTY = 33;
   public static final byte OVERRIDE_FROM_END_PROPERTY = 34;
   public static final byte PROFILE_NAME_PROPERTY_2 = -127;
   private static final long PROFILES_PERSISTENT_ID = -126079441489341474L;
   private static final long ENABLED_PROFILE_PERSISTENT_ID = -5522933223976053723L;
   private static final long PREVIOUS_PROFILE_PERSISTENT_ID = 7792220392351022642L;
   private static final int PROFILES_DATABASE_VERSION = 2;
   private static final int VERSION_WHERE_OVERRIDES_INTRODUCED = 2;
   private static final String PROFILES_DATABASE_NAME = "Profiles";
   public static long PROFILES_APP_DESCRIPTOR_ID = 7850907034192831055L;
   private static long SOURCE_EXPIRE_AMOUNT = 259200000;
   public static final int LOUD_PROFILE_UID = 1;
   public static final int DISCREET_PROFILE_UID = 2;
   public static final int QUIET_PROFILE_UID = 3;
   public static final int DEFAULT_PROFILE_UID = 4;
   public static final int PHONE_ONLY_PROFILE_UID = 5;
   public static final int OFF_PROFILE_UID = 6;
   public static final int OBSOLETE_OUT_OF_BOX_OVERRIDE_UID = 1;
   public static final int OUT_OF_BOX_OVERRIDE_UID = 7;
   public static final int LAST_RIM_PROFILE_UID = 7;
   private static final int[] KEY_FIELD_IDS = new int[]{1, -804651005, 1, 2};
   private static final int DEFAULT_RECORD_TYPE = 1;
   private static final LongIntHashtable _sourcesToExpire = new LongIntHashtable();

   @Override
   public final void setLegacyDeviceOptions(DataBuffer aDataBuffer) {
      Profile legacyProfile = this.getByIdentifier((byte)6);
      if (legacyProfile == null) {
         ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
         legacyProfile = this.createNewProfile(resources.getString(225), (byte)6);
         this.add(legacyProfile, true, false);
      }

      AlertConsequence.fillWithLegacyData(legacyProfile, aDataBuffer);
      PersistentObject.commit(legacyProfile);
   }

   final void resetExpireTimes() {
      long[] hiddenSources = NotificationsManager.getHiddenSourceIds();
      int expireTime = (int)(InternalServices.getUptime() / 1000);
      _sourcesToExpire.clear();

      for (int i = 0; i < hiddenSources.length; i++) {
         if (NotificationsManager.getParentSourceID(hiddenSources[i]) != -1) {
            _sourcesToExpire.put(hiddenSources[i], expireTime);
         }
      }
   }

   final void checkForOrphanedSources() {
      for (int i = 0; i < this._profiles.size(); i++) {
         Profile p = (Profile)this._profiles.elementAt(i);
         if (p != null) {
            p.checkForOrphanedSources(this);
         }
      }
   }

   public final void moveSource(long srcSourceID, long destSourceID) {
      synchronized (this._profiles) {
         NotificationsManager.moveSource(srcSourceID, destSourceID);

         for (int i = 0; i < this._profiles.size(); i++) {
            Profile p = (Profile)this._profiles.elementAt(i);
            if (p != null) {
               p.moveSource(srcSourceID, destSourceID);
            }
         }

         EventLogger.logEvent(6982943375119825480L, 1297045061, 0);
      }
   }

   public final void unHideSource(long sourceID) {
      NotificationsManager.unHideSource(sourceID);
      _sourcesToExpire.remove(sourceID);
   }

   public final void hideSource(long sourceID) {
      NotificationsManager.hideSource(sourceID);
      if (NotificationsManager.getParentSourceID(sourceID) != -1) {
         _sourcesToExpire.put(sourceID, (int)(InternalServices.getUptime() / 1000));
      }
   }

   public final synchronized void enablePagingSupport() {
      synchronized (this._profiles) {
         if (!this.pagingProfilePresent()) {
            ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
            this.add(this.createNewProfile(resources.getString(239), (byte)4), true, false);
            PersistentObject.commit(this._profiles);
         }
      }
   }

   public final boolean pagingProfilePresent() {
      for (int i = this._profiles.size() - 1; i >= 0; i--) {
         Profile profile = (Profile)this._profiles.elementAt(i);
         if (profile.getIdentifier() == 4) {
            return true;
         }
      }

      return false;
   }

   public final boolean commitChanges(Profile profile, boolean fireNotification) {
      synchronized (this._profiles) {
         String profileName = profile.getName();

         for (int i = this._profiles.size() - 1; i >= 0; i--) {
            Profile currentProfile = (Profile)this._profiles.elementAt(i);
            if (currentProfile != profile && StringUtilities.strEqualIgnoreCase(profileName, currentProfile.getName())) {
               return false;
            }
         }

         PersistentObject.commit(profile);
      }

      if (fireNotification) {
         this._collectionListenerManager.fireElementUpdated(this, profile, profile);
      }

      if (profile == this.getEnabled()) {
         this.fireActiveProfileChanged(profile);
      }

      return true;
   }

   final void checkForExpiredSources() {
      LongEnumeration keys = _sourcesToExpire.keys();

      while (keys.hasMoreElements()) {
         long sourceID = keys.nextElement();
         int time = _sourcesToExpire.get(sourceID);
         int currentTime = (int)(InternalServices.getUptime() / 1000);
         if (currentTime - time > SOURCE_EXPIRE_AMOUNT) {
            for (int i = 0; i < this._profiles.size(); i++) {
               Profile p = (Profile)this._profiles.elementAt(i);
               Object o = p.getConfiguration(-2870941457036655797L, sourceID);
               if (o != null) {
                  p.setConfiguration(-2870941457036655797L, sourceID, null);
                  this.commitChanges(p, true);
               }
            }

            _sourcesToExpire.remove(sourceID);
         }
      }
   }

   public final boolean add(Profile profile, boolean replaceIfDuplicate, boolean fireNotification) {
      Profile duplicateProfile = null;
      synchronized (this._profiles) {
         byte profileIdentifier = profile.getIdentifier();
         String profileName = profile.getName();
         if (profileName.trim().length() == 0) {
            return false;
         }

         int duplicateProfileIndex = -1;
         int profileInsertionIndex = -1;
         int numProfiles = this._profiles.size();

         for (int i = 0; i < numProfiles; i++) {
            Profile currentProfile = (Profile)this._profiles.elementAt(i);
            if ((profileIdentifier == -1 || profileIdentifier != currentProfile.getIdentifier())
               && !StringUtilities.strEqualIgnoreCase(profileName, currentProfile.getName())) {
               if (profileIdentifier != -1 && profileInsertionIndex == -1 && currentProfile.getIdentifier() > profileIdentifier) {
                  profileInsertionIndex = i;
               }
            } else {
               if (duplicateProfileIndex != -1) {
                  throw new RuntimeException("MDPE");
               }

               duplicateProfileIndex = i;
            }
         }

         if (duplicateProfileIndex != -1) {
            if (!replaceIfDuplicate) {
               return false;
            }

            duplicateProfile = (Profile)this._profiles.elementAt(duplicateProfileIndex);
            if (profile.getIdentifier() == -1 && duplicateProfile.getIdentifier() != -1) {
               return false;
            }

            if (duplicateProfile.getUID() != profile.getUID()) {
               return false;
            }

            this._profiles.setElementAt(profile, duplicateProfileIndex);
         } else if (profileInsertionIndex != -1) {
            this._profiles.insertElementAt(profile, profileInsertionIndex);
            int indexOfEnabledProfile = PersistentInteger.get(this._enabledProfileId);
            if (indexOfEnabledProfile >= profileInsertionIndex) {
               PersistentInteger.set(this._enabledProfileId, indexOfEnabledProfile + 1);
            }
         } else {
            this._profiles.addElement(profile);
         }

         PersistentObject.commit(this._profiles);
      }

      if (fireNotification) {
         if (duplicateProfile != null) {
            this._collectionListenerManager.fireElementRemoved(this, duplicateProfile);
         }

         this._collectionListenerManager.fireElementAdded(this, profile);
      }

      return true;
   }

   public final void remove(Profile aProfile, boolean fireNotificationBoolean) {
      synchronized (this._profiles) {
         int indexOfEnabledProfile = PersistentInteger.get(this._enabledProfileId);
         int indexOfProfileToRemove = this.indexOf(aProfile);
         if (indexOfEnabledProfile == indexOfProfileToRemove) {
            this.enable(this.getDefault());
         } else if (indexOfEnabledProfile > indexOfProfileToRemove) {
            PersistentInteger.set(this._enabledProfileId, indexOfEnabledProfile - 1);
         }

         this._profiles.removeElementAt(indexOfProfileToRemove);
         PersistentObject.commit(this._profiles);
      }

      if (fireNotificationBoolean) {
         this._collectionListenerManager.fireElementRemoved(this, aProfile);
      }
   }

   public final void removeAll(boolean fireNotificationBoolean) {
      synchronized (this._profiles) {
         this._profiles.removeAllElements();
         this.addBuiltInProfiles();
         this.enable(this.getDefault());
         PersistentObject.commit(this._profiles);
      }

      if (fireNotificationBoolean) {
         this._collectionListenerManager.fireReset(this);
      }
   }

   public final Profile getDefault() {
      Profile defaultProfile = this.locateProfile(3);
      if (defaultProfile == null) {
         EventLogger.logEvent(6982943375119825480L, 1348757101, 3);
         ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
         this.add(this.createNewProfile(resources.getString(224), (byte)3, 4), true, false);
         defaultProfile = this.locateProfile(3);
      }

      return defaultProfile;
   }

   public final boolean isOffEnabled() {
      return this.getEnabled().getIdentifier() == 7;
   }

   public final int getPreviousProfileUID() {
      return PersistentInteger.get(this._previousProfileId);
   }

   public final Profile getEnabled() {
      int enabledIndex = PersistentInteger.get(this._enabledProfileId);
      if (enabledIndex < 0 || enabledIndex >= this._profiles.size()) {
         this.enable(this.getDefault());
         enabledIndex = 3;
      }

      return (Profile)this._profiles.elementAt(enabledIndex);
   }

   public final void enable(Profile aProfile) {
      synchronized (this._profiles) {
         int index = this._profiles.indexOf(aProfile);
         int oldIndex = PersistentInteger.get(this._enabledProfileId);
         if (index != -1) {
            UiSettings.setOffProfileEnabled(aProfile.getUID() == 6);
            Alert.mute(aProfile.getUID() == 3);
            PersistentInteger.set(this._enabledProfileId, index);
            this.fireActiveProfileChanged(aProfile);
            if (oldIndex >= 0 && oldIndex < this._profiles.size()) {
               Profile previousProfile = (Profile)this.getAt(oldIndex);
               PersistentInteger.set(this._previousProfileId, previousProfile.getUID());
               this.commitChanges(previousProfile, true);
            }

            this.commitChanges(aProfile, true);
         }
      }
   }

   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8877632280522743328L) {
         this.resetExpireTimes();
      }
   }

   public final int indexOf(Profile aProfile) {
      return this._profiles.indexOf(aProfile);
   }

   public final Profile createNewProfile(String name, byte identifier, int uid) {
      return new Profile(name, identifier, uid);
   }

   public final Profile createNewProfile(String name, byte identifier) {
      return this.createNewProfile(name, identifier, UIDGenerator.getUID());
   }

   public final void resetToDefaultTune(Profile[] profiles, String tuneName) {
      if (profiles != null && profiles.length != 0) {
         Consequence consequence = NotificationsManager.getConsequence(-2870941457036655797L);

         for (int index = this._profiles.size() - 1; index >= 0; index--) {
            Profile profile = (Profile)this._profiles.elementAt(index);
            LongHashtable configurations = profile.getConfigurations();
            LongEnumeration configurationKeys = configurations.keys();

            while (configurationKeys.hasMoreElements()) {
               long sourceKey = configurationKeys.nextElement();
               LongHashtable subConfigurations = (LongHashtable)configurations.get(sourceKey);
               LongEnumeration subConfigurationKeys = subConfigurations.keys();

               while (subConfigurationKeys.hasMoreElements()) {
                  long alertConfigurationKey = subConfigurationKeys.nextElement();
                  AlertConfiguration alertConfiguration = (AlertConfiguration)subConfigurations.get(alertConfigurationKey);
                  AlertConfiguration defaultConfiguration = (AlertConfiguration)consequence.newConfiguration(-2870941457036655797L, sourceKey, (byte)4, 0, null);
                  if (tuneName.equals(alertConfiguration.getTuneName(true))) {
                     alertConfiguration.setTuneName(defaultConfiguration.getTuneName(true), true);
                  }

                  if (tuneName.equals(alertConfiguration.getTuneName(false))) {
                     alertConfiguration.setTuneName(defaultConfiguration.getTuneName(false), false);
                  }

                  PersistentObject.commit(alertConfiguration);
               }
            }
         }
      }
   }

   public final Profile getByIdentifier(byte identifierByte) {
      if (identifierByte == -1) {
         throw new IllegalArgumentException();
      }

      synchronized (this._profiles) {
         Profile profile = null;

         for (int index = this._profiles.size() - 1; index >= 0; index--) {
            profile = (Profile)this._profiles.elementAt(index);
            if (profile.getIdentifier() == identifierByte) {
               return profile;
            }
         }

         return null;
      }
   }

   public final Profile getByUID(int uid) {
      synchronized (this._profiles) {
         Profile profile = null;

         for (int index = this._profiles.size() - 1; index >= 0; index--) {
            profile = (Profile)this._profiles.elementAt(index);
            if (profile.getUID() == uid) {
               return profile;
            }
         }

         return null;
      }
   }

   public final Profile[] getProfilesUsingTune(String tuneName) {
      Profile[] profiles = new Profile[0];

      for (int index = this._profiles.size() - 1; index >= 0; index--) {
         boolean isAffectedProfile = false;
         Profile profile = (Profile)this._profiles.elementAt(index);
         LongHashtable configurations = profile.getConfigurations();
         LongEnumeration configurationKeys = configurations.keys();

         while (configurationKeys.hasMoreElements() && !isAffectedProfile) {
            long sourceKey = configurationKeys.nextElement();
            LongHashtable subConfigurations = (LongHashtable)configurations.get(sourceKey);
            LongEnumeration subConfigurationKeys = subConfigurations.keys();

            while (subConfigurationKeys.hasMoreElements() && !isAffectedProfile) {
               long alertConfigurationKey = subConfigurationKeys.nextElement();
               AlertConfiguration alertConfiguration = (AlertConfiguration)subConfigurations.get(alertConfigurationKey);
               if (tuneName.equals(alertConfiguration.getTuneName(true)) || tuneName.equals(alertConfiguration.getTuneName(false))) {
                  Arrays.add(profiles, profile);
                  isAffectedProfile = true;
               }
            }
         }
      }

      return profiles;
   }

   @Override
   public final int getIndex(Object element) {
      return ReadableListUtil.getIndex(element, this);
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return ReadableListUtil.getAt(index, count, elements, destIndex, this);
   }

   @Override
   public final Object getAt(int indexInt) {
      return (Profile)this._profiles.elementAt(indexInt);
   }

   @Override
   public final void addCollectionListener(Object listenerObject) {
      this._collectionListenerManager.addCollectionListener(listenerObject);
   }

   @Override
   public final void removeCollectionListener(Object listenerObject) {
      this._collectionListenerManager.removeCollectionListener(listenerObject);
   }

   @Override
   public final boolean addSyncObject(SyncObject aSyncObject) {
      if (aSyncObject instanceof Profile) {
         PersistentObject.commit(aSyncObject);
         this.add((Profile)aSyncObject, true, true);
         return true;
      } else if (aSyncObject instanceof Override) {
         PersistentObject.commit(aSyncObject);
         Overrides.getInstance().add((Override)aSyncObject, true);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldSyncObject, SyncObject newSyncObject) {
      if (newSyncObject instanceof Profile) {
         synchronized (this._profiles) {
            Profile profile = null;
            int uid = newSyncObject.getUID();

            for (int index = this._profiles.size() - 1; index >= 0; index--) {
               profile = (Profile)this._profiles.elementAt(index);
               if (profile.getUID() == uid) {
                  this._profiles.setElementAt(newSyncObject, index);
                  this.commitChanges((Profile)newSyncObject, true);
                  return true;
               }
            }

            return false;
         }
      } else if (newSyncObject instanceof Override) {
         Overrides overrides = Overrides.getInstance();
         synchronized (overrides) {
            Override override = null;
            int uid = newSyncObject.getUID();

            for (int index = overrides.size() - 1; index >= 0; index--) {
               override = (Override)overrides.getAt(index);
               if (override.getUID() == uid) {
                  overrides.setElementAt(newSyncObject, index);
                  overrides.commitChanges((Override)newSyncObject, true);
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public final boolean removeSyncObject(SyncObject aSyncObject) {
      if (aSyncObject instanceof Profile) {
         this.remove((Profile)aSyncObject, true);
         return true;
      }

      if (aSyncObject instanceof Override) {
         Overrides.getInstance().delete((Override)aSyncObject);
      }

      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      this.removeAll(false);
      Overrides.getInstance().removeAll();
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      SyncObject[] array = null;
      int idx = 0;
      synchronized (this._profiles) {
         int numProfiles = this._profiles.size();
         array = new SyncObject[numProfiles];

         for (int i = 0; i < numProfiles; i++) {
            Profile profile = (Profile)this._profiles.elementAt(i);
            if (profile != null && profile.getUID() != 6) {
               array[idx++] = profile;
            }
         }
      }

      Overrides overrides = Overrides.getInstance();
      synchronized (overrides) {
         int numOverrides = overrides.size();
         Array.resize(array, idx + numOverrides);

         for (int i = 0; i < numOverrides; i++) {
            array[idx++] = (SyncObject)overrides.getAt(i);
         }

         return array;
      }
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      synchronized (this._profiles) {
         int numProfiles = this._profiles.size();

         for (int i = 0; i < numProfiles; i++) {
            SyncObject profile = (SyncObject)this._profiles.elementAt(i);
            if (profile.getUID() == uid) {
               return profile;
            }
         }
      }

      Overrides overrides = Overrides.getInstance();
      synchronized (overrides) {
         int numOverrides = overrides.size();

         for (int i = 0; i < numOverrides; i++) {
            SyncObject override = (SyncObject)overrides.getAt(i);
            if (override.getUID() == uid) {
               return override;
            }
         }

         return null;
      }
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject aSyncObject) {
      return false;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject aSyncObject) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject aSyncObject) {
   }

   @Override
   public final int getSyncObjectCount() {
      int count = this.size() + Overrides.getInstance().size();
      if (this.getSyncObject(6) != null) {
         count--;
      }

      return count;
   }

   @Override
   public final int getSyncVersion() {
      return 2;
   }

   @Override
   public final String getSyncName() {
      return "Profiles";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final void beginTransaction() {
      this.syncTransactionStarted();
   }

   @Override
   public final void endTransaction() {
      this.syncTransactionStopped();
   }

   @Override
   public final boolean convert(SyncObject aSyncObject, DataBuffer aDataBuffer, int versionInt) {
      EventLogger.logEvent(6982943375119825480L, 1348695155, 5);
      if (!(aSyncObject instanceof Profile)) {
         if (!(aSyncObject instanceof Override)) {
            return false;
         }

         Override override = (Override)aSyncObject;
         SyncConverter converter = null;
         aDataBuffer.writeShort(1);
         aDataBuffer.writeByte(16);
         aDataBuffer.writeByte(16);
         ConverterUtilities.writeStringSmart(aDataBuffer, 17, override.getName());
         aDataBuffer.writeShort(1);
         aDataBuffer.writeByte(18);
         aDataBuffer.writeByte(override.isEnabled() ? 1 : 0);
         aDataBuffer.writeShort(1);
         aDataBuffer.writeByte(19);
         aDataBuffer.writeByte(override.isFromAddressBook() ? 1 : 0);
         aDataBuffer.writeShort(4);
         aDataBuffer.writeByte(20);
         aDataBuffer.writeInt(override.getProfileUID());
         aDataBuffer.writeShort(1);
         aDataBuffer.writeByte(21);
         aDataBuffer.writeByte(override.getUseTune() ? 1 : 0);
         ConverterUtilities.writeStringSmart(aDataBuffer, 22, override.getTuneName());
         FromContact[] fromContacts = Overrides.getInstance().getFromContactsInAddressBookData(override);
         aDataBuffer.writeShort(4);
         aDataBuffer.writeByte(23);
         aDataBuffer.writeInt(fromContacts.length);

         for (int i = 0; i < fromContacts.length; i++) {
            FromContact fromContact = fromContacts[i];
            int addressCardUID = fromContact._addressCardUID;
            aDataBuffer.writeShort(4);
            aDataBuffer.writeByte(24);
            aDataBuffer.writeInt(addressCardUID);
            ConverterUtilities.writeStringSmart(aDataBuffer, 25, fromContact.getName());
            String contactInfo = fromContact.getContactInfo();
            if (contactInfo != null) {
               aDataBuffer.writeShort(1);
               aDataBuffer.writeByte(33);
               aDataBuffer.writeByte(fromContact._contactInfoType);
               ConverterUtilities.writeStringSmart(aDataBuffer, 32, contactInfo);
            }

            aDataBuffer.writeShort(0);
            aDataBuffer.writeByte(34);
         }

         return true;
      } else {
         Profile profile = (Profile)aSyncObject;
         SyncConverter converter = null;
         LongHashtable configurationsAtSourceLevel = null;
         LongHashtable configurationsAtConsequenceLevel = null;
         LongEnumeration sourceIds = null;
         LongEnumeration consequenceIds = null;
         SyncObject configuration = null;
         byte isEnabled = 0;
         aDataBuffer.writeShort(1);
         aDataBuffer.writeByte(0);
         aDataBuffer.writeByte(profile.getIdentifier());
         ConverterUtilities.writeStringSmart(aDataBuffer, 1, profile.getSyncName());
         if (this.getEnabled() == profile) {
            aDataBuffer.writeShort(0);
            aDataBuffer.writeByte(2);
            isEnabled = 1;
         }

         aDataBuffer.writeShort(1);
         aDataBuffer.writeByte(12);
         aDataBuffer.writeByte(isEnabled);
         configurationsAtSourceLevel = profile.getConfigurations();
         sourceIds = configurationsAtSourceLevel.keys();
         aDataBuffer.writeShort(4);
         aDataBuffer.writeByte(3);
         aDataBuffer.writeInt(configurationsAtSourceLevel.size());

         while (sourceIds.hasMoreElements()) {
            long sourceId = sourceIds.nextElement();
            configurationsAtConsequenceLevel = (LongHashtable)configurationsAtSourceLevel.get(sourceId);
            consequenceIds = configurationsAtConsequenceLevel.keys();
            aDataBuffer.writeShort(8);
            aDataBuffer.writeByte(5);
            aDataBuffer.writeLong(sourceId);
            aDataBuffer.writeShort(4);
            aDataBuffer.writeByte(4);
            aDataBuffer.writeInt(configurationsAtConsequenceLevel.size());

            while (consequenceIds.hasMoreElements()) {
               long consequenceId = consequenceIds.nextElement();
               configuration = (SyncObject)configurationsAtConsequenceLevel.get(consequenceId);
               converter = (SyncConverter)NotificationsManager.getConsequence(consequenceId);
               aDataBuffer.writeShort(8);
               aDataBuffer.writeByte(6);
               aDataBuffer.writeLong(consequenceId);
               converter.convert(configuration, aDataBuffer, versionInt);
            }
         }

         return true;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final SyncObject convert(DataBuffer aDataBuffer, int versionInt, int uidInt) {
      Profile profile = null;
      SyncConverter converter = null;
      Object configuration = null;
      int sourcesCount = 0;
      int sourcesIndex = 0;
      int configurationsPerSourceCount = 0;
      int configurationsPerSourceIndex = 0;
      long consequenceId = 0;
      long sourceId = 0;
      boolean expectMore = true;
      this._lastVersionRestored = versionInt;
      EventLogger.logEvent(6982943375119825480L, 1348691571, 5);
      profile = this.createNewProfile("", (byte)-1, uidInt);

      while (expectMore) {
         boolean var20 = false /* VF: Semaphore variable */;

         try {
            var20 = true;
            int length = aDataBuffer.readShort();
            byte type = aDataBuffer.readByte();
            switch (type) {
               case -127:
                  profile.setName(
                     ConverterUtilities.readStringEncoded(aDataBuffer.getArray(), aDataBuffer.getArrayPosition(), length, aDataBuffer.isBigEndian())
                  );
                  aDataBuffer.skipBytes(length);
                  var20 = false;
                  break;
               case 0:
                  byte identifier = aDataBuffer.readByte();
                  profile.setIdentifier(identifier);
                  var20 = false;
                  break;
               case 1:
                  profile.setName(StringUtilities.decodeBOM(aDataBuffer.getArray(), aDataBuffer.getArrayPosition(), length, true));
                  aDataBuffer.skipBytes(length);
                  var20 = false;
                  break;
               case 2:
                  this._restoredEnabledProfile = profile;
                  var20 = false;
                  break;
               case 3:
                  sourcesCount = aDataBuffer.readInt();
                  if (sourcesCount == 0) {
                     expectMore = false;
                  }

                  sourcesIndex = 0;
                  var20 = false;
                  break;
               case 4:
                  configurationsPerSourceCount = aDataBuffer.readInt();
                  configurationsPerSourceIndex = 0;
                  var20 = false;
                  break;
               case 5:
                  sourceId = aDataBuffer.readLong();
                  sourcesIndex++;
                  var20 = false;
                  break;
               case 6:
                  consequenceId = aDataBuffer.readLong();
                  configurationsPerSourceIndex++;
                  converter = (SyncConverter)NotificationsManager.getConsequence(consequenceId);
                  configuration = converter.convert(aDataBuffer, versionInt, 0);
                  if (configuration != null) {
                     profile.setConfiguration(consequenceId, sourceId, configuration);
                  }

                  if (sourcesIndex < sourcesCount) {
                     var20 = false;
                  } else if (configurationsPerSourceIndex < configurationsPerSourceCount) {
                     var20 = false;
                  } else {
                     expectMore = false;
                     var20 = false;
                  }
                  break;
               case 12:
                  if (aDataBuffer.readByte() != 1) {
                     var20 = false;
                  } else {
                     this._restoredEnabledProfile = profile;
                     var20 = false;
                  }
                  break;
               case 16:
                  aDataBuffer.skipBytes(length);
                  return this.convertOverride(aDataBuffer, uidInt);
               default:
                  aDataBuffer.skipBytes(length);
                  EventLogger.logEvent(6982943375119825480L, 1097233763, 2);
                  var20 = false;
            }
         } finally {
            if (var20) {
               EventLogger.logEvent(6982943375119825480L, 1348824934, 2);
               expectMore = false;
               continue;
            }
         }
      }

      return profile;
   }

   @Override
   public final int size() {
      return this._profiles.size();
   }

   @Override
   public final void otaSyncOperationStarted(SyncCollection syncCollection, int type) {
      if (type == 1) {
         this.syncTransactionStarted();
      }
   }

   @Override
   public final void otaSyncOperationStopped(SyncCollection syncCollection, int type) {
      if (type == 1) {
         this.syncTransactionStopped();
      }
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return this._schema;
   }

   @Override
   public final int adjustCurrentProfileVolume(int volumeLevelChange) {
      Profile profile = this.getEnabled();
      AlertConfiguration config = (AlertConfiguration)profile.getConfiguration(-2870941457036655797L, 2868625504212929964L);
      boolean inHolster = DeviceInfo.isInHolster();
      int oldVolume = config.getSetting(2, inHolster);
      int newVolume = MathUtilities.clamp(0, oldVolume + volumeLevelChange, AlertConsequence._volumeValues.length - 1);
      if (newVolume != oldVolume) {
         config.setSetting(2, (byte)newVolume, inHolster);
         if (!this.commitChanges(profile, true)) {
            return -1;
         }
      }

      return AlertConsequence._volumeValues[newVolume];
   }

   @Override
   public final String getCurrentProfileName() {
      return this.getEnabled().getName();
   }

   @Override
   public final void reset(Collection collection) {
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (collection instanceof Overrides && newElement instanceof Override && oldElement instanceof Override) {
         this._collectionListenerManager.fireElementUpdated(this, oldElement, newElement);
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (collection instanceof Overrides && element instanceof Override) {
         this._collectionListenerManager.fireElementRemoved(this, element);
      }
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      if (collection instanceof Overrides && element instanceof Override) {
         this._collectionListenerManager.fireElementAdded(this, element);
      }
   }

   private Profiles() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-126079441489341474L);
      synchronized (persistentObject) {
         if ((this._profiles = (Vector)persistentObject.getContents()) == null) {
            this._profiles = new Vector();
            persistentObject.setContents(this._profiles, 51);
            persistentObject.commit();
         }

         if (this._profiles.isEmpty()) {
            this.addBuiltInProfiles();
            persistentObject.commit();
         }
      }

      this._enabledProfileId = PersistentInteger.getId(-5522933223976053723L, 3);
      this._previousProfileId = PersistentInteger.getId(7792220392351022642L, 2);
      this._collectionListenerManager = new CollectionListenerManager();
      this._restoredEnabledProfile = null;
      this._lastVersionRestored = -1;
      this._schema = new SyncCollectionSchema();
      this._schema.setDefaultRecordType(1);
      this._schema.setKeyFieldIds(1, KEY_FIELD_IDS);
      this.checkForOrphanedSources();
      Overrides.getInstance().addCollectionListener(this);
      Proxy.getInstance().invokeLater(this._sourceExpireRunnable, 86400000, true);
   }

   public static final String[] getDefaultTuneNames(long sourceId) {
      Consequence consequence = NotificationsManager.getConsequence(-2870941457036655797L);
      AlertConfiguration defaultConfiguration = (AlertConfiguration)consequence.newConfiguration(-2870941457036655797L, sourceId, (byte)3, 0, null);
      return new String[]{defaultConfiguration.getTuneName(true), defaultConfiguration.getTuneName(false)};
   }

   public static final String[] getDefaultTuneNames(long sourceId, byte profileIdentifier) {
      Consequence consequence = NotificationsManager.getConsequence(-2870941457036655797L);
      AlertConfiguration defaultConfiguration = (AlertConfiguration)consequence.newConfiguration(-2870941457036655797L, sourceId, profileIdentifier, 0, null);
      return new String[]{defaultConfiguration.getTuneName(true), defaultConfiguration.getTuneName(false)};
   }

   private final void addBuiltInProfiles() {
      ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
      this.add(this.createNewProfile(resources.getString(221), (byte)0, 1), true, false);
      this.add(this.createNewProfile(resources.getString(222), (byte)1, 2), true, false);
      this.add(this.createNewProfile(resources.getString(223), (byte)2, 3), true, false);
      this.add(this.createNewProfile(resources.getString(224), (byte)3, 4), true, false);
      this.add(this.createNewProfile(resources.getString(324), (byte)7, 6), true, false);
      if (Phone.isSupported()) {
         this.add(this.createNewProfile(resources.getString(227), (byte)5, 5), true, false);
      }
   }

   private final synchronized void syncTransactionStopped() {
      if (this._restoredEnabledProfile != null) {
         this.enable(this._restoredEnabledProfile);
         this._restoredEnabledProfile = null;
      }

      if (UiSettings.getOffProfileEnabled()) {
         Profile offProfile = getInstance().getByUID(6);
         if (offProfile != null) {
            this.enable(offProfile);
         }
      }

      if (this._lastVersionRestored < 2 && this._lastVersionRestored > -1 && this.getByUID(7) == null) {
         ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
         Overrides.getInstance().add(Overrides.getInstance().createNewOverride(resources.getString(246), 7), false);
      }
   }

   private final synchronized void syncTransactionStarted() {
      this._restoredEnabledProfile = null;
   }

   private final Profile locateProfile(int id) {
      int size = this._profiles.size();

      for (int i = 0; i < size; i++) {
         Profile profile = (Profile)this._profiles.elementAt(i);
         if (profile.getIdentifier() == id) {
            return profile;
         }
      }

      return null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final SyncObject convertOverride(DataBuffer aDataBuffer, int uidInt) {
      if (uidInt == 1) {
         uidInt = 7;
      }

      byte[] bytes = null;
      int fromCount = 0;
      int fromIndex = 0;
      boolean expectMore = true;
      Override override = Overrides.getInstance().createNewOverride(null, uidInt);
      int addresscardUID = -1;
      String addresscardName = null;
      String contactInfo = null;
      byte contactInfoType = 0;
      FromContact[] fromContacts = new FromContact[0];
      boolean validFromContacts = true;
      int validatedAddressCardUID = -1;

      while (expectMore) {
         boolean var20 = false /* VF: Semaphore variable */;

         try {
            var20 = true;
            int length = aDataBuffer.readShort();
            byte type = aDataBuffer.readByte();
            switch (type) {
               case 16:
               case 26:
               case 27:
               case 28:
               case 29:
               case 30:
               case 31:
                  bytes = new byte[length];
                  aDataBuffer.readFully(bytes);
                  var20 = false;
                  break;
               case 17:
               default:
                  override.setName(StringUtilities.decodeBOM(aDataBuffer.getArray(), aDataBuffer.getArrayPosition(), length, true));
                  aDataBuffer.skipBytes(length);
                  var20 = false;
                  break;
               case 18:
                  override.setEnabled(aDataBuffer.readByte() == 1);
                  var20 = false;
                  break;
               case 19:
                  override.setFromAddressBook(aDataBuffer.readByte() == 1);
                  var20 = false;
                  break;
               case 20:
                  override.setProfileUID(aDataBuffer.readInt());
                  var20 = false;
                  break;
               case 21:
                  override.setUseTune(aDataBuffer.readByte() == 1);
                  var20 = false;
                  break;
               case 22:
                  override.setTuneName(StringUtilities.decodeBOM(aDataBuffer.getArray(), aDataBuffer.getArrayPosition(), length, true));
                  aDataBuffer.skipBytes(length);
                  var20 = false;
                  break;
               case 23:
                  fromCount = aDataBuffer.readInt();
                  if (fromCount == 0) {
                     expectMore = false;
                  }

                  fromIndex = 0;
                  var20 = false;
                  break;
               case 24:
                  addresscardUID = aDataBuffer.readInt();
                  var20 = false;
                  break;
               case 25:
                  addresscardName = StringUtilities.decodeBOM(aDataBuffer.getArray(), aDataBuffer.getArrayPosition(), length, true);
                  aDataBuffer.skipBytes(length);
                  var20 = false;
                  break;
               case 32:
                  contactInfo = StringUtilities.decodeBOM(aDataBuffer.getArray(), aDataBuffer.getArrayPosition(), length, true);
                  aDataBuffer.skipBytes(length);
                  var20 = false;
                  break;
               case 33:
                  contactInfoType = aDataBuffer.readByte();
                  var20 = false;
                  break;
               case 34:
                  validatedAddressCardUID = Overrides.validateFromNames(addresscardUID, addresscardName, contactInfo, contactInfoType, true);
                  if (validatedAddressCardUID == 0) {
                     validFromContacts = false;
                  } else if (addresscardUID != validatedAddressCardUID) {
                     addresscardUID = validatedAddressCardUID;
                  }

                  FromContact fromContact = new FromContact(addresscardUID, addresscardName, contactInfo, contactInfoType);
                  int numids = fromContacts.length;
                  Array.resize(fromContacts, numids + 1);
                  fromContacts[numids] = fromContact;
                  addresscardUID = -1;
                  addresscardName = null;
                  contactInfo = null;
                  contactInfoType = 0;
                  if (++fromIndex < fromCount) {
                     var20 = false;
                  } else {
                     expectMore = false;
                     var20 = false;
                  }
            }
         } finally {
            if (var20) {
               EventLogger.logEvent(6982943375119825480L, 1348824934, 2);
               expectMore = false;
               continue;
            }
         }
      }

      override.setFromContacts(fromContacts);
      if (override.isFromAddressBook()) {
         override.updateOverrideName();
      }

      if (!validFromContacts) {
         Overrides.getInstance().addToOverridesToValidate(override);
      }

      return override;
   }

   public static final Profiles getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      Profiles profiles = (Profiles)registry.getOrWaitFor(1406503904366187671L);
      if (profiles == null) {
         profiles = new Profiles();
         registry.put(1406503904366187671L, profiles);
         registry.put(-8941179583806470257L, profiles);
      }

      return profiles;
   }

   private final void fireActiveProfileChanged(Profile profile) {
      RIMGlobalMessagePoster.postGlobalEvent(6679759753682678305L, 0, 0, profile, null);
   }
}
