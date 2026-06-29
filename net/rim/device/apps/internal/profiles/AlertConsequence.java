package net.rim.device.apps.internal.profiles;

import net.rim.device.api.notification.Consequence;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.Phone;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.profiles.TuneChoiceField;
import net.rim.device.apps.api.framework.profiles.TuneManager;
import net.rim.device.internal.system.AlertPlayer;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.LEDEngine;

public final class AlertConsequence implements Consequence, SyncConverter {
   private LEDEngine _ledEngine = LEDEngine.getInstance();
   private AlertEngine _alertEngine;
   private long _upTimeOfLastAlertDuringCall;
   public static final long ALERT_CONSEQUENCE_ID = -2870941457036655797L;
   private static final int SILENT_VOLUME = 0;
   private static final int LOW_VOLUME = 33;
   private static final int MEDIUM_VOLUME = 66;
   private static final int HIGH_VOLUME = 100;
   private static final int POLY_SILENT_VOLUME = 0;
   private static final int POLY_LOW_VOLUME = 65;
   private static final int POLY_MEDIUM_VOLUME = 85;
   private static final int POLY_HIGH_VOLUME = 100;
   private static final int HEADSET_SILENT_VOLUME = 0;
   private static final int HEADSET_LOW_VOLUME = 33;
   private static final int HEADSET_MEDIUM_VOLUME = 66;
   private static final int HEADSET_HIGH_VOLUME = 100;
   public static final int OUTPUT_SPEAKER = 0;
   public static final int OUTPUT_HEADSET = 1;
   private static final int ALERT_CHOICE = 0;
   private static final int TUNE_CHOICE = 1;
   private static final int VOLUME_CHOICE = 2;
   private static final int NUMBER_OF_BEEPS_CHOICE = 3;
   private static final int REPEAT_NOTIFICATION_CHOICE = 4;
   private static final int DO_NOT_DISTURB_CHOICE = 11;
   private static final int ENABLE_BACKLIGHT = 12;
   private static final int NOTIFY_DURING_VOICE_CALL_CHOICE = 11;
   private static final int NUMBER_OF_VIBRATIONS_CHOICE = 13;
   public static final int TYPE_NONE = 0;
   public static final int TYPE_TONE = 1;
   public static final int TYPE_VIBRATE = 2;
   public static final int TYPE_VIBRATE_THEN_TONE = 4;
   public static final int[] _alertValues = new int[]{
      0, 1, 2, 4, -805044209, 1792, 458752, 0, 65792, -805044209, 1792, 458753, 256, 65792, -805044213, 775162112
   };
   public static final int[] _volumeValues = getVolumeLevels(0);
   private static final int[] _beepsValues = new int[]{1, 2, 3, -805044209, 132865, 34013953, 256, 65792, -805044209, 33624065, 17826561, 258};
   private static final int[] _vibratesValues = new int[]{1, 2, 3, -805044209, 132865, 34013953, 256, 65792, -805044209, 33624065, 17826561, 258};
   private static final int _lowVolume = getLowVolume(0);
   private static final int _highVolume = getHighVolume(0);
   private static final int MINIMUM_TIME_BETWEEN_ALERTS = 15000;
   private static final short[] BLACKBERRY_DISCRETE = new short[]{
      900, 100, 0, 25, 900, 100, 0, 25, 900, 100, 8, -12280, 2000, 100, 3000, 200, 4000, 200, 5000, 100
   };

   public AlertConsequence(AlertEngine alertEngine) {
      this._alertEngine = alertEngine;
   }

   public static final AlertConsequence getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      return (AlertConsequence)registry.get(-2870941457036655797L);
   }

   public final LEDEngine getLEDEngine() {
      return this._ledEngine;
   }

   public final void stopTuneAndVibration(long sourceIdLong) {
      if (this._alertEngine.isAlertInProgress()) {
         this._alertEngine.stopAlert(sourceIdLong);
      }
   }

   public final void triggerTuneAndVibration(int alertIndexInt, String tuneName, int volumeIndexInt, int beepsIndexInt, long sourceIdLong) {
      this.triggerTuneAndVibration(alertIndexInt, tuneName, volumeIndexInt, beepsIndexInt, 1, sourceIdLong, 3, -1, -1, -1);
   }

   final void triggerTuneAndVibration(
      int alertIndexInt,
      String tuneName,
      int volumeIndexInt,
      int beepsIndexInt,
      int vibratesIndexInt,
      long sourceIdLong,
      int alertLevel,
      int repeatCount,
      int repeatDelay,
      int stopCondition
   ) {
      int alertType = _alertValues[alertIndexInt];
      if (alertType > 0) {
         int beeps = _beepsValues[beepsIndexInt];
         int vibrates = 0;
         if (alertType == 2 || alertType == 4) {
            vibrates = _vibratesValues[vibratesIndexInt];
         }

         int startVolume = _lowVolume;
         int endVolume = _highVolume;
         if (volumeIndexInt < _volumeValues.length) {
            startVolume = _volumeValues[volumeIndexInt];
            endVolume = startVolume;
         }

         AlertPlayer tune = null;
         if (TuneManager.isTuneManagerAvailable()) {
            TuneManager manager = TuneManager.getTuneManager();
            tune = manager.getTune(tuneName);
            if (tune == null && sourceIdLong != 0) {
               AlertConfiguration defaultConfig = AlertConfigurations.getConfiguration(-2870941457036655797L, sourceIdLong, (byte)3, alertLevel, null);
               int tuneIndex = defaultConfig.getSetting(1, false);
               if (!manager.isTuneAvailable(tuneIndex)) {
                  tuneIndex = 12;
               }

               tuneName = manager.getBuiltInTuneFileName(tuneIndex);
               tune = manager.getTune(tuneName);
            }
         } else {
            EventLogger.logEvent(6982943375119825480L, 1414354241, 2);
         }

         if (Phone.isSupported() && Phone.getInstance().getActiveCallId() != 0) {
            EventLogger.logEvent(6982943375119825480L, 1128352844, 3);
            if (vibrates > 0 || startVolume > 0) {
               long currentUpTime = InternalServices.getUptime();
               if (currentUpTime - this._upTimeOfLastAlertDuringCall >= 15000) {
                  Alert.startAudio(BLACKBERRY_DISCRETE, 33);
                  this._upTimeOfLastAlertDuringCall = currentUpTime;
               }
            }

            return;
         }

         if (sourceIdLong == 3975384895524745189L) {
            if (alertType == 4) {
               alertType = 2;
            } else if (alertType == 1) {
               return;
            }
         }

         if (tune != null) {
            if (sourceIdLong == 0) {
               this._alertEngine
                  .startNewAlertLater(
                     alertType, tune, beeps, vibrates, startVolume, endVolume, sourceIdLong, alertLevel, repeatCount, repeatDelay, stopCondition
                  );
               return;
            }

            this._alertEngine
               .startNewAlert(alertType, tune, beeps, vibrates, startVolume, endVolume, sourceIdLong, alertLevel, repeatCount, repeatDelay, stopCondition, 2);
         }
      }
   }

   public static final int[] getVolumeLevels(int outputDevice) {
      int[] volumes = new int[4];
      if (outputDevice == 1) {
         volumes[0] = 0;
         volumes[1] = 33;
         volumes[2] = 66;
         volumes[3] = 100;
         return volumes;
      } else if (Alert.isMIDISupported()) {
         volumes[0] = 0;
         volumes[1] = 65;
         volumes[2] = 85;
         volumes[3] = 100;
         return volumes;
      } else {
         volumes[0] = 0;
         volumes[1] = 33;
         volumes[2] = 66;
         volumes[3] = 100;
         return volumes;
      }
   }

   public static final int getLowVolume(int outputDevice) {
      if (outputDevice == 1) {
         return 33;
      } else {
         return Alert.isMIDISupported() ? 65 : 33;
      }
   }

   public static final int getHighVolume(int outputDevice) {
      if (outputDevice == 1) {
         return 100;
      } else {
         return Alert.isMIDISupported() ? 100 : 100;
      }
   }

   public final boolean shouldNegotiateDeferredEvent(long sourceIdLong, Object configurationObject) {
      if (AlertConfiguration.supportsDoNotDisturb(sourceIdLong)) {
         AlertConfiguration configuration = (AlertConfiguration)configurationObject;
         return configuration.getHolsterIndependentSetting(11) == 0;
      } else {
         return true;
      }
   }

   public final boolean isImmediateEventOccuring(long sourceIdLong) {
      return this._ledEngine.contains(sourceIdLong) || this._alertEngine.isPlayingForSource(sourceIdLong);
   }

   public final boolean isSensoryAlert(long sourceIdLong, Object configurationObject, boolean inHolster) {
      AlertConfiguration configuration = (AlertConfiguration)configurationObject;
      return configuration.getSetting(0, inHolster) != 0;
   }

   public static final void fillWithLegacyData(Profile legacyProfile, DataBuffer legacyDataBuffer) {
      AlertConfiguration configuration = null;
      byte[] settings = null;

      try {
         int offset = legacyDataBuffer.getPosition();
         legacyDataBuffer.setPosition(offset + 8);
         int tune = legacyDataBuffer.readUnsignedByte();
         int volume = legacyDataBuffer.readUnsignedByte();
         legacyDataBuffer.skipBytes(3);
         int numberOfBeeps = legacyDataBuffer.readUnsignedByte();
         legacyDataBuffer.skipBytes(2);
         int inHolsterNotify = legacyDataBuffer.readUnsignedByte();
         int outHolsterNotify = legacyDataBuffer.readUnsignedByte();
         legacyDataBuffer.setPosition(offset + 285);
         int repeatNotifications = legacyDataBuffer.readUnsignedByte();
         legacyDataBuffer.setPosition(offset + 76);
         legacyDataBuffer.readUnsignedByte();
         configuration = new AlertConfiguration();
         configuration.setSetting(0, (byte)mapAlertChoice(inHolsterNotify), true);
         configuration.setSetting(1, (byte)tune, true);
         configuration.setSetting(2, (byte)volume, true);
         configuration.setSetting(3, (byte)mapNumberOfBeepsChoice(numberOfBeeps), true);
         configuration.setSetting(4, (byte)mapRepeatNotificationChoice(repeatNotifications), true);
         configuration.setSetting(0, (byte)mapAlertChoice(outHolsterNotify), false);
         configuration.setSetting(1, (byte)tune, false);
         configuration.setSetting(2, (byte)volume, false);
         configuration.setSetting(3, (byte)mapNumberOfBeepsChoice(numberOfBeeps), false);
         configuration.setSetting(4, (byte)mapRepeatNotificationChoice(repeatNotifications), false);
         settings = configuration.getSettings();
         legacyProfile.setConfiguration(-2870941457036655797L, -1845850106795451018L, configuration);
         legacyProfile.setConfiguration(-2870941457036655797L, 7986617465467730856L, new AlertConfiguration(settings, 15));
         legacyProfile.setConfiguration(-2870941457036655797L, 2666833733215697856L, new AlertConfiguration(settings, 15));
         legacyDataBuffer.setPosition(offset + 104);
         legacyDataBuffer.skipBytes(11);
         tune = legacyDataBuffer.readUnsignedByte();
         volume = legacyDataBuffer.readUnsignedByte();
         configuration = new AlertConfiguration();
         configuration.setSetting(0, (byte)1, true);
         configuration.setSetting(1, (byte)tune, true);
         configuration.setSetting(2, (byte)volume, true);
         configuration.setSetting(3, (byte)2, true);
         configuration.setSetting(4, (byte)0, true);
         configuration.setSetting(0, (byte)1, false);
         configuration.setSetting(1, (byte)tune, false);
         configuration.setSetting(2, (byte)volume, false);
         configuration.setSetting(3, (byte)2, false);
         configuration.setSetting(4, (byte)0, false);
         legacyProfile.setConfiguration(-2870941457036655797L, 2469778178827742799L, configuration);
      } finally {
         return;
      }
   }

   private static final int mapAlertChoice(int valueInt) {
      return valueInt > 3 ? 3 : valueInt;
   }

   private static final int mapRepeatNotificationChoice(int valueInt) {
      return valueInt == 4 ? 1 : 0;
   }

   private static final int mapNumberOfBeepsChoice(int valueInt) {
      if (valueInt == 0) {
         return 0;
      } else {
         return valueInt > 2 ? 2 : valueInt - 1;
      }
   }

   @Override
   public final void startNotification(long consequenceIdLong, long sourceIdLong, long eventIdLong, Object configurationObject, Object contextObject) {
      ContextObject context = ContextObject.castOrCreate(contextObject);
      AlertConfiguration configuration = (AlertConfiguration)configurationObject;
      int repeatCount = -1;
      int repeatDelay = -1;
      int stopCondition = -1;
      int alertLevel = 3;
      boolean forceNoRepeatNotifications = false;
      String overrideTuneName = null;
      if (context != null) {
         overrideTuneName = (String)context.get(6476586477082074028L);
         Integer repeatCountInt = (Integer)context.get(-2000078441617626078L);
         if (repeatCountInt != null) {
            repeatCount = repeatCountInt;
         }

         Integer repeatDelayInt = (Integer)context.get(3423823800652933171L);
         if (repeatDelayInt != null) {
            repeatDelay = repeatDelayInt;
         }

         Integer stopConditionInt = (Integer)context.get(-8706641515457485416L);
         if (stopConditionInt != null) {
            stopCondition = stopConditionInt;
         }

         Integer alertLevelInt = (Integer)context.get(-2832590917644170714L);
         if (alertLevelInt != null) {
            alertLevel = alertLevelInt;
         }

         forceNoRepeatNotifications = context.getPrivateFlag(5593808198828868151L, 1);
      }

      if (!AlertConfiguration.supportsDoNotDisturb(sourceIdLong) || configuration.getHolsterIndependentSetting(11) <= 0) {
         if (AlertConfiguration.supportsEnableBackLight(sourceIdLong) && configuration.getHolsterIndependentSetting(12) == 1) {
            Backlight.enable(true);
         }

         boolean holstered = context.getFlag(67);
         if (!forceNoRepeatNotifications && configuration.getSetting(4, holstered) > 0) {
            this._ledEngine.addEvent(sourceIdLong, holstered, context.getIntegerData(-1));
         }

         if (!AlertConfiguration.supportsMuteDuringVoiceCall(sourceIdLong)
            || configuration.getHolsterIndependentSetting(11) != 0
            || !Phone.isSupported()
            || Phone.getInstance().getActiveCallId() == 0) {
            int alertIndex = configuration.getSetting(0, holstered);
            int volumeIndex = configuration.getSetting(2, holstered);
            String tuneName = configuration.getTuneName(holstered);
            if (overrideTuneName != null) {
               if (overrideTuneName.equals(TuneChoiceField.MUTE_TUNE_NAME)) {
                  volumeIndex = 0;
               } else {
                  tuneName = overrideTuneName;
               }
            }

            int numberOfBeepsIndex = configuration.getSetting(3, holstered);
            int numberOfVibratesIndex = configuration.getSetting(13, holstered);
            this.triggerTuneAndVibration(
               alertIndex, tuneName, volumeIndex, numberOfBeepsIndex, numberOfVibratesIndex, sourceIdLong, alertLevel, repeatCount, repeatDelay, stopCondition
            );
         }
      }
   }

   @Override
   public final void stopNotification(long consequenceIdLong, long sourceIdLong, long eventIdLong, Object configurationObject, Object contextObject) {
      this._ledEngine.removeEvents(sourceIdLong, ContextObject.getIntegerData(contextObject, -1));
      if (contextObject != null) {
         ContextObject co = (ContextObject)contextObject;
         if (co.getFlag(39)) {
            this.stopTuneAndVibration(sourceIdLong);
         }
      }
   }

   @Override
   public final Object newConfiguration(long consequenceIdLong, long sourceIdLong, byte profileIdentifierByte, int levelInt, Object contextObject) {
      return AlertConfigurations.getConfiguration(consequenceIdLong, sourceIdLong, profileIdentifierByte, levelInt, contextObject);
   }

   @Override
   public final boolean convert(SyncObject aSyncObject, DataBuffer aDataBuffer, int versionInt) {
      if (!(aSyncObject instanceof AlertConfiguration)) {
         return false;
      }

      AlertConfiguration configuration = (AlertConfiguration)aSyncObject;
      byte[] bytes = configuration.getSettings();
      aDataBuffer.writeShort(bytes.length);
      aDataBuffer.writeByte(8);
      aDataBuffer.write(bytes);
      String tuneName = configuration.getTuneName(false);
      if (tuneName != null) {
         String shortName = TuneManager.getTuneManager().getLegacyName(tuneName);
         bytes = shortName.getBytes();
         aDataBuffer.writeShort(bytes.length);
         aDataBuffer.writeByte(9);
         aDataBuffer.write(bytes);
         bytes = tuneName.getBytes();
         aDataBuffer.writeShort(bytes.length);
         aDataBuffer.writeByte(13);
         aDataBuffer.write(bytes);
      }

      tuneName = configuration.getTuneName(true);
      if (tuneName != null) {
         String shortName = TuneManager.getTuneManager().getLegacyName(tuneName);
         bytes = shortName.getBytes();
         aDataBuffer.writeShort(bytes.length);
         aDataBuffer.writeByte(10);
         aDataBuffer.write(bytes);
         bytes = tuneName.getBytes();
         aDataBuffer.writeShort(bytes.length);
         aDataBuffer.writeByte(14);
         aDataBuffer.write(bytes);
      }

      aDataBuffer.writeShort(0);
      aDataBuffer.writeByte(11);
      return true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final SyncObject convert(DataBuffer aDataBuffer, int versionInt, int uidInt) {
      AlertConfiguration configuration = null;
      boolean var15 = false /* VF: Semaphore variable */;

      try {
         var15 = true;
         int anEOFException = aDataBuffer.readShort();
         byte type = aDataBuffer.readByte();
         boolean javaApps38 = false;
         if (type != 7 && type != 8) {
            EventLogger.logEvent(6982943375119825480L, 1096967024, 2);
            return null;
         }

         if (type == 8 && (anEOFException == 15 || anEOFException == 13)) {
            byte[] bytes = new byte[15];
            if (anEOFException == 13) {
               javaApps38 = true;
               EventLogger.logEvent(6982943375119825480L, 1097216818, 5);
               aDataBuffer.readFully(bytes, 0, 13);
            } else {
               EventLogger.logEvent(6982943375119825480L, 1097229173, 5);
               aDataBuffer.readFully(bytes);
            }

            String tuneNameOutOfHolster = null;
            String tuneNameInHolster = null;
            boolean fqnInHolsterProvided = false;
            boolean fqnOutOfHolsterProvided = false;
            if (type == 8) {
               while (!aDataBuffer.eof() & type != 11) {
                  anEOFException = aDataBuffer.readShort();
                  type = aDataBuffer.readByte();
                  switch (type) {
                     case 8:
                     case 11:
                     case 12:
                        aDataBuffer.skipBytes(anEOFException);
                        break;
                     case 9:
                     default:
                        byte[] tuneNameBytes = new byte[anEOFException];
                        aDataBuffer.read(tuneNameBytes);
                        if (!fqnOutOfHolsterProvided) {
                           tuneNameOutOfHolster = (String)(new Object(tuneNameBytes));
                        }
                        break;
                     case 10:
                        byte[] var22 = new byte[anEOFException];
                        aDataBuffer.read(var22);
                        if (!fqnInHolsterProvided) {
                           tuneNameInHolster = (String)(new Object(var22));
                        }
                        break;
                     case 13:
                        byte[] var21 = new byte[anEOFException];
                        aDataBuffer.read(var21);
                        tuneNameOutOfHolster = (String)(new Object(var21));
                        fqnOutOfHolsterProvided = true;
                        break;
                     case 14:
                        byte[] tuneManager = new byte[anEOFException];
                        aDataBuffer.read(tuneManager);
                        tuneNameInHolster = (String)(new Object(tuneManager));
                        fqnInHolsterProvided = true;
                  }
               }
            }

            configuration = new AlertConfiguration();
            if (TuneManager.isTuneManagerAvailable()) {
               TuneManager tuneManager = TuneManager.getTuneManager();
               tuneNameInHolster = tuneManager.resolveTune(tuneNameInHolster);
               tuneNameOutOfHolster = tuneManager.resolveTune(tuneNameOutOfHolster);
            }

            configuration.initialize(bytes, tuneNameOutOfHolster, tuneNameInHolster, 15);
            if (!javaApps38) {
               var15 = false;
            } else {
               configuration.setSetting(13, (byte)1, false);
               configuration.setSetting(13, (byte)1, true);
               var15 = false;
            }
         } else if (anEOFException == 12) {
            EventLogger.logEvent(6982943375119825480L, 1097216817, 5);
            byte[] bytes = new byte[15];
            aDataBuffer.readFully(bytes, 0, 12);
            configuration = new AlertConfiguration();
            configuration.initialize(bytes, null, null, 12);
            configuration.setSetting(13, (byte)1, false);
            configuration.setSetting(13, (byte)1, true);
            var15 = false;
         } else if (anEOFException != 14) {
            aDataBuffer.skipBytes(anEOFException);
            EventLogger.logEvent(6982943375119825480L, 1097233763, 2);
            var15 = false;
         } else {
            EventLogger.logEvent(6982943375119825480L, 1097216816, 5);
            byte[] bytes = new byte[15];
            byte[] bytesIn30Format = new byte[14];
            aDataBuffer.readFully(bytesIn30Format);
            configuration = new AlertConfiguration();
            configuration.setSetting(0, bytesIn30Format[0], false);
            configuration.setSetting(1, bytesIn30Format[1], false);
            configuration.setSetting(2, bytesIn30Format[2], false);
            configuration.setSetting(3, bytesIn30Format[3], false);
            configuration.setSetting(4, (byte)(bytesIn30Format[4] > 0 ? 1 : 0), false);
            configuration.setSetting(13, (byte)1, false);
            configuration.setSetting(0, bytesIn30Format[6], true);
            configuration.setSetting(1, bytesIn30Format[7], true);
            configuration.setSetting(2, bytesIn30Format[8], true);
            configuration.setSetting(3, bytesIn30Format[9], true);
            configuration.setSetting(4, (byte)(bytesIn30Format[10] > 0 ? 1 : 0), true);
            configuration.setSetting(13, (byte)1, true);
            configuration.setHolsterIndependentSetting(11, (byte)0);
            var15 = false;
         }
      } finally {
         if (var15) {
            EventLogger.logEvent(6982943375119825480L, 1097166694, 2);
            return configuration;
         }
      }

      return configuration;
   }
}
