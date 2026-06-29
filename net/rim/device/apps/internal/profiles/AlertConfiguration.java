package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.profiles.TuneChoiceField;
import net.rim.device.apps.api.framework.profiles.TuneManager;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.internal.system.InternalServices;

public final class AlertConfiguration implements PersistableRIMModel, SyncObject, FieldChangeListener, FieldProvider, Copyable {
   private byte[] _settings = new byte[15];
   private String[] _tuneNames = new String[2];
   public static final int NUMBER_OF_SETTINGS_PER_HOLSTER_STATE = 5;
   public static final int NUMBER_OF_SETTING_INDEPENDENT_OF_HOLSTER_STATE = 5;
   public static final int TOTAL_NUMBER_OF_SETTINGS = 15;
   public static final int TOTAL_NUMBER_OF_SETTINGS_JAVAAPPS_30 = 14;
   public static final int TOTAL_NUMBER_OF_SETTINGS_JAVAAPPS_31 = 12;
   public static final int TOTAL_NUMBER_OF_SETTINGS_JAVAAPPS_38 = 13;
   public static final int ALERT_CHOICE = 0;
   public static final int TUNE_CHOICE = 1;
   public static final int VOLUME_CHOICE = 2;
   public static final int NUMBER_OF_BEEPS_CHOICE = 3;
   public static final int REPEAT_NOTIFICATION_CHOICE = 4;
   public static final int LEVEL_1_NOTIFY_ONLY_CHOICE = 10;
   public static final int DO_NOT_DISTURB_CHOICE = 11;
   public static final int NOTIFY_DURING_VOICE_CALL_CHOICE = 11;
   public static final int ENABLE_BACKLIGHT = 12;
   public static final int NUMBER_OF_VIBRATIONS_CHOICE = 13;

   public final String getTuneName(boolean inHolsterBoolean) {
      return inHolsterBoolean ? this._tuneNames[1] : this._tuneNames[0];
   }

   public final void initialize(byte[] byteArray, String tuneNameOutOfHolster, String tuneNameInHolster, int numberOfSettings) {
      if (byteArray != null && byteArray.length <= 15) {
         this._settings = Arrays.copy(byteArray);
         this.initHolsterTune(tuneNameOutOfHolster, false, numberOfSettings);
         this.initHolsterTune(tuneNameInHolster, true, numberOfSettings);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final byte getSetting(int indexInt, boolean inHolsterBoolean) {
      return this.getSetting(indexInt, inHolsterBoolean, Integer.MAX_VALUE);
   }

   @Override
   public final int getUID() {
      return 0;
   }

   public final byte getSetting(int indexInt, boolean inHolsterBoolean, int maxValue) {
      byte result;
      if (indexInt < 5) {
         result = inHolsterBoolean ? this._settings[indexInt + 5] : this._settings[indexInt];
      } else {
         result = inHolsterBoolean ? this._settings[indexInt + 1] : this._settings[indexInt];
      }

      if (result >= maxValue) {
         result = 0;
      }

      return result;
   }

   public final byte getHolsterIndependentSetting(int indexInt) {
      return this._settings[indexInt];
   }

   public final void setSetting(int indexInt, byte valueByte, boolean inHolsterBoolean) {
      if (indexInt < 5) {
         this._settings[inHolsterBoolean ? indexInt + 5 : indexInt] = valueByte;
      } else {
         this._settings[inHolsterBoolean ? indexInt + 1 : indexInt] = valueByte;
      }
   }

   public final void setTuneName(String tuneName, boolean inHolsterBoolean) {
      this._tuneNames[inHolsterBoolean ? 1 : 0] = tuneName;
   }

   public final void setHolsterIndependentSetting(int indexInt, byte valueByte) {
      this._settings[indexInt] = valueByte;
   }

   public final byte[] getSettings() {
      return this._settings;
   }

   @Override
   public final Object copy() {
      return new AlertConfiguration(this._settings, this.getTuneName(false), this.getTuneName(true), this._settings.length);
   }

   @Override
   public final void fieldChanged(Field aField, int context) {
      AlertConsequence consequence = (AlertConsequence)ApplicationRegistry.getApplicationRegistry().get(-2870941457036655797L);
      ChoiceField originalField = (ChoiceField)aField.getOriginal();
      Manager manager = originalField.getManager();
      int fieldIndex = manager.getFieldWithFocusIndex();
      if (fieldIndex == 0 || fieldIndex == 1 || fieldIndex == 2) {
         ChoiceField fieldWithChanges = (ChoiceField)aField;
         long sourceId = 0;
         int alertIndex = fieldIndex == 0 ? fieldWithChanges.getSelectedIndex() : 1;
         int volumeIndex = 0;
         int beepsIndex = 0;
         int vibratesIndex = 0;
         String tuneName = null;
         int repeatCount = -1;
         int fieldCount = manager.getFieldCount();
         if (fieldCount <= 1) {
            Object o = originalField.getCookie();
            if (o instanceof ContextObject) {
               ContextObject co = (ContextObject)o;
               if (ContextObject.getFlag(co, 48)) {
                  sourceId = 3975384895524745189L;
               }
            }
         } else {
            TuneChoiceField tuneChoiceField = (TuneChoiceField)manager.getField(1);
            tuneName = tuneChoiceField.getSelectedTuneName();
            if (fieldIndex == 2) {
               volumeIndex = fieldWithChanges.getSelectedIndex();
               tuneChoiceField.setVolumeIndex(volumeIndex == 0 ? 1 : volumeIndex);
            } else {
               volumeIndex = ((ObjectChoiceField)manager.getField(2)).getSelectedIndex();
            }

            if (fieldIndex == 1 && volumeIndex == 0) {
               volumeIndex = 1;
            }

            if (fieldCount > 5) {
               beepsIndex = fieldIndex != 1 ? ((ObjectChoiceField)manager.getField(3)).getSelectedIndex() : 0;
               vibratesIndex = ((ObjectChoiceField)manager.getField(5)).getSelectedIndex();
            } else {
               vibratesIndex = ((ObjectChoiceField)manager.getField(4)).getSelectedIndex();
            }

            Object cookie = originalField.getCookie();
            if (cookie instanceof ContextObject) {
               Object source = ContextObject.get(cookie, 250);
               long id = NotificationsManager.getSourceId(source);
               if (id == 2868625504212929964L || NotificationsManager.getParentSourceID(id) == 2868625504212929964L) {
                  repeatCount = 1;
               }
            }
         }

         if ((context & 2) == 0 && AlertConsequence._alertValues[alertIndex] > 0) {
            consequence.triggerTuneAndVibration(alertIndex, tuneName, volumeIndex, beepsIndex, vibratesIndex, sourceId, -1, repeatCount, -1, -1);
         }
      }
   }

   @Override
   public final Field getField(Object contextObject) {
      ObjectChoiceField field = null;
      String[] fieldValues = null;
      String fieldName = null;
      Field result = null;
      ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
      if (!ContextObject.getFlag(contextObject, 89)) {
         ObjectChoiceField objectChoiceField = null;
         Manager manager = new VerticalFieldManager(1152921504606846976L);
         boolean holstered = ContextObject.getFlag(contextObject, 67);
         fieldName = resources.getString(holstered ? 301 : 300);
         fieldValues = (String[])resources.getObject(302);
         field = new ObjectChoiceField(fieldName, fieldValues, this.getSetting(0, holstered, fieldValues.length));
         field.setChangeListener(this);
         field.setCookie(contextObject);
         manager.add(field);
         if (!ContextObject.getFlag(contextObject, 48)) {
            fieldName = resources.getString(310);
            String tuneName = this.getTuneName(holstered);
            Object alertSource = ContextObject.get(contextObject, 250);
            String currentTune = null;
            if (alertSource != null) {
               long sourceId = NotificationsManager.getSourceId(alertSource);
               Integer profileIdentifier = (Integer)ContextObject.get(contextObject, -4054673099568009991L);
               if (profileIdentifier != null) {
                  String[] defaultTuneNames = Profiles.getDefaultTuneNames(sourceId, profileIdentifier.byteValue());
                  currentTune = holstered ? defaultTuneNames[0] : defaultTuneNames[1];
               }
            }

            TuneChoiceField tuneChoiceField = TuneManager.getTuneManager().getTuneChoiceField(fieldName, tuneName, currentTune, false);
            ObjectChoiceField var19 = tuneChoiceField;
            manager.add(var19);
            fieldName = resources.getString(320);
            fieldValues = (String[])resources.getObject(321);
            byte volumeIndex = this.getSetting(2, holstered, fieldValues.length);
            var19 = new ObjectChoiceField(fieldName, fieldValues, volumeIndex);
            tuneChoiceField.setVolumeIndex(volumeIndex);
            var19.setChangeListener(this);
            var19.setCookie(contextObject);
            manager.add(var19);
            Object source = ContextObject.get(contextObject, 250);
            long sourceId = NotificationsManager.getSourceId(source);
            if (sourceId != 2868625504212929964L && NotificationsManager.getParentSourceID(sourceId) != 2868625504212929964L) {
               fieldName = resources.getString(330);
               fieldValues = (String[])resources.getObject(331);
               var19 = new ObjectChoiceField(fieldName, fieldValues, this.getSetting(3, holstered, fieldValues.length));
               manager.add(var19);
            }

            fieldName = resources.getString(340);
            fieldValues = (String[])resources.getObject(341);
            var19 = new ObjectChoiceField(fieldName, fieldValues, this.getSetting(4, holstered, fieldValues.length));
            manager.add(var19);
            fieldName = resources.getString(350);
            fieldValues = (String[])resources.getObject(351);
            var19 = new ObjectChoiceField(fieldName, fieldValues, this.getSetting(13, holstered, fieldValues.length));
            manager.add(var19);
         }

         manager.setCookie(this);
         result = manager;
      } else {
         Object source = ContextObject.get(contextObject, 250);
         if (source != null) {
            Manager manager = null;
            if (supportsEnableBackLight(NotificationsManager.getSourceId(source))) {
               manager = new VerticalFieldManager(1152921504606846976L);
               manager.setCookie(this);
               fieldName = resources.getString(229);
               fieldValues = (String[])resources.getObject(230);
               field = new ObjectChoiceField(fieldName, fieldValues, this.getHolsterIndependentSetting(12));
               manager.add(field);
            }

            if (supportsDoNotDisturb(NotificationsManager.getSourceId(source))) {
               if (manager == null) {
                  manager = new VerticalFieldManager(1152921504606846976L);
                  manager.setCookie(this);
               }

               fieldName = resources.getString(360);
               fieldValues = (String[])resources.getObject(361);
               field = new ObjectChoiceField(fieldName, fieldValues, this.getHolsterIndependentSetting(11));
               field.setCookie(this);
               manager.add(field);
            }

            if (supportsMuteDuringVoiceCall(NotificationsManager.getSourceId(source))) {
               if (manager == null) {
                  manager = new VerticalFieldManager(1152921504606846976L);
                  manager.setCookie(this);
               }

               fieldName = resources.getString(249);
               fieldValues = (String[])resources.getObject(361);
               field = new ObjectChoiceField(fieldName, fieldValues, this.getHolsterIndependentSetting(11));
               field.setCookie(this);
               manager.add(field);
            }

            result = manager;
         }
      }

      return result;
   }

   @Override
   public final boolean grabDataFromField(Field aField, Object contextObject) {
      String inHolsterLabel = null;
      String outOfHolsterLabel = null;
      String fieldLabel = null;
      ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
      if (aField instanceof Manager) {
         Manager manager = (Manager)aField;
         inHolsterLabel = resources.getString(301);
         outOfHolsterLabel = resources.getString(300);
         fieldLabel = ((ObjectChoiceField)manager.getField(0)).getLabel();
         boolean holstered = false;
         boolean normalSettings = false;
         if (StringUtilities.compareToIgnoreCase(fieldLabel, inHolsterLabel) == 0) {
            holstered = true;
            normalSettings = true;
         } else if (StringUtilities.compareToIgnoreCase(fieldLabel, outOfHolsterLabel) == 0) {
            normalSettings = true;
         }

         if (!normalSettings) {
            for (int index = 0; index < manager.getFieldCount(); index++) {
               ObjectChoiceField field = (ObjectChoiceField)manager.getField(index);
               fieldLabel = field.getLabel();
               String labelToCompareTo = resources.getString(360);
               if (StringUtilities.compareToIgnoreCase(fieldLabel, labelToCompareTo) == 0) {
                  this.setHolsterIndependentSetting(11, (byte)field.getSelectedIndex());
               }

               labelToCompareTo = resources.getString(229);
               if (StringUtilities.compareToIgnoreCase(fieldLabel, labelToCompareTo) == 0) {
                  this.setHolsterIndependentSetting(12, (byte)field.getSelectedIndex());
               }

               labelToCompareTo = resources.getString(249);
               if (StringUtilities.compareToIgnoreCase(fieldLabel, labelToCompareTo) == 0) {
                  this.setHolsterIndependentSetting(11, (byte)field.getSelectedIndex());
               }
            }
         } else {
            this.setSetting(0, (byte)((ObjectChoiceField)manager.getField(0)).getSelectedIndex(), holstered);
            if (manager.getFieldCount() > 1) {
               TuneChoiceField tuneChoiceField = (TuneChoiceField)manager.getField(1);
               String tuneName = tuneChoiceField.getSelectedTuneName();
               this.setSetting(1, (byte)tuneChoiceField.getSelectedIndex(), holstered);
               this.setTuneName(tuneName, holstered);
               this.setSetting(2, (byte)((ObjectChoiceField)manager.getField(2)).getSelectedIndex(), holstered);
               if (manager.getFieldCount() > 5) {
                  this.setSetting(3, (byte)((ObjectChoiceField)manager.getField(3)).getSelectedIndex(), holstered);
                  this.setSetting(4, (byte)((ObjectChoiceField)manager.getField(4)).getSelectedIndex(), holstered);
                  this.setSetting(13, (byte)((ObjectChoiceField)manager.getField(5)).getSelectedIndex(), holstered);
               } else {
                  this.setSetting(4, (byte)((ObjectChoiceField)manager.getField(3)).getSelectedIndex(), holstered);
                  this.setSetting(13, (byte)((ObjectChoiceField)manager.getField(4)).getSelectedIndex(), holstered);
               }
            }
         }
      }

      PersistentObject.commit(this);
      return true;
   }

   @Override
   public final boolean validate(Field aField, Object contextObject) {
      return true;
   }

   @Override
   public final int getOrder(Object contextObject) {
      return 0;
   }

   private final int adjustForBrandingTune(int index, int numberOfSettings) {
      int result = index;
      if (TuneManager.isTuneManagerAvailable()) {
         TuneManager tm = TuneManager.getTuneManager();
         if (numberOfSettings == 12 || numberOfSettings == 14) {
            int count = tm.getBuiltInTuneCount();
            if (index >= count) {
               String tuneName = tm.getBuiltInTuneFileName(count);
               if (tuneName != null && tm.isBrandingTune(tuneName)) {
                  result = index + 1;
               }
            }
         }
      }

      return result;
   }

   static final boolean supportsMuteDuringVoiceCall(long sourceId) {
      return !supportsDoNotDisturb(sourceId)
         && (sourceId == 2666833733215697856L || sourceId == 7986617465467730856L || sourceId == 204325571560529255L || InternalServices.isDeviceClassA());
   }

   static final boolean supportsDoNotDisturb(long sourceId) {
      return sourceId == 2868625504212929964L || NotificationsManager.getParentSourceID(sourceId) == 2868625504212929964L || sourceId == 4237171590573870406L;
   }

   static final boolean supportsEnableBackLight(long sourceId) {
      return !ProfilesOptions.getOptions().isBacklightOptionEnabled()
         ? false
         : sourceId == -1845850106795451018L
            || sourceId == -327746170160875990L
            || sourceId == 7986617465467730856L
            || sourceId == 8609386677418041260L
            || sourceId == 2666833733215697856L;
   }

   public AlertConfiguration(byte[] byteArray, String tuneNameOutOfHolster, String tuneNameInHolster, int numberOfSettings) {
      this();
      this.initialize(byteArray, tuneNameOutOfHolster, tuneNameInHolster, numberOfSettings);
   }

   public AlertConfiguration() {
   }

   public AlertConfiguration(byte[] byteArray, int numberOfSettings) {
      this();
      this.initialize(byteArray, null, null, numberOfSettings);
   }

   private final void initHolsterTune(String tuneName, boolean inHolster, int numberOfSettings) {
      if (tuneName == null) {
         int tuneIndex = this.adjustForBrandingTune(this.getSetting(1, inHolster), numberOfSettings);
         if (TuneManager.isTuneManagerAvailable()) {
            TuneManager tm = TuneManager.getTuneManager();
            tuneName = tm.getBuiltInTuneFileName(tuneIndex);
            if (tuneName == null) {
               tuneName = tm.getBuiltInTuneFileName(0);
            }
         }
      }

      this.setTuneName(tuneName, inHolster);
   }
}
