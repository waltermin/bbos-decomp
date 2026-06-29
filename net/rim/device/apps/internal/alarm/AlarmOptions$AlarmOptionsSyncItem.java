package net.rim.device.apps.internal.alarm;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.profiles.TuneManager;

final class AlarmOptions$AlarmOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int ALARM_TIME_TAG;
   private static final int ALARM_ENABLED_TAG;
   private static final int ALARM_WEEKEND_TAG;
   private static final int ALARM_SNOOZE_TAG;
   private static final int ALARM_ALERT_TAG;
   private static final int ALARM_TUNE_TAG;
   private static final int ALARM_VOLUME_TAG;
   private static final int ALARM_NUMBER_OF_VIBRATES_TAG;
   private static final int ALARM_TUNE_FQN_TAG;
   private static final int DB_VERSION;

   @Override
   public final String getSyncName() {
      return "Alarm Options";
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
   public final boolean setSyncData(DataBuffer buffer, int version) {
      AlarmOptions options = AlarmOptions.getOptions();
      boolean fqnTuneNameRead = false;

      label59:
      try {
         while (buffer.available() > 0) {
            short dataLength = buffer.readShort();
            byte dataTag = buffer.readByte();
            switch (dataTag) {
               case 0:
                  buffer.skipBytes(dataLength);
                  break;
               case 1:
               default:
                  options.setAlarmTime(buffer.readInt());
                  break;
               case 2:
                  options.setAlarmEnabled(buffer.readByte() == 1);
                  break;
               case 3:
                  options.setActiveWeekends(buffer.readByte() == 1);
                  break;
               case 4:
                  options.setSnooze(buffer.readByte());
                  break;
               case 5:
                  options.setAlertType(buffer.readByte());
                  break;
               case 6: {
                  byte[] tuneNameBytes = new byte[dataLength];
                  buffer.readFully(tuneNameBytes);
                  if (!fqnTuneNameRead) {
                     String tuneNameStringx = (String)(new Object(tuneNameBytes));
                     options.setTuneName(tuneNameStringx);
                  }
                  break;
               }
               case 7:
                  options.setVolume(buffer.readByte());
                  break;
               case 8:
                  options.setNumberOfVibrates(buffer.readByte());
                  break;
               case 9: {
                  byte[] tuneNameBytes = new byte[dataLength];
                  buffer.readFully(tuneNameBytes);
                  String tuneNameString = new Object(tuneNameBytes);
                  options.setTuneName((String)tuneNameString);
                  fqnTuneNameRead = true;
               }
            }
         }
      } finally {
         break label59;
      }

      options.commit();
      Alarm.setAlarm(false);
      return true;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      AlarmOptions options = AlarmOptions.getOptions();
      ConverterUtilities.writeInt(buffer, 1, options.getAlarmTime());
      WriteBufferByte(buffer, 2, (byte)(options.getAlarmEnabled() ? 1 : 0));
      WriteBufferByte(buffer, 3, (byte)(options.getActiveWeekends() ? 1 : 0));
      WriteBufferByte(buffer, 4, options.getSnooze());
      WriteBufferByte(buffer, 5, options.getAlertType());
      ConverterUtilities.writeString(buffer, 6, TuneManager.getTuneManager().getLegacyName(options.getTuneName()));
      WriteBufferByte(buffer, 7, options.getVolume());
      WriteBufferByte(buffer, 8, options.getNumberOfVibrates());
      ConverterUtilities.writeString(buffer, 9, options.getTuneName());
      return true;
   }

   private static final void WriteBufferByte(DataBuffer buffer, int tag, byte value) {
      buffer.writeShort(1);
      buffer.writeByte(tag);
      buffer.writeByte(value);
   }
}
