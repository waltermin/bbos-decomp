package net.rim.device.apps.internal.profiles;

import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.profiles.TuneManager;

final class AlertConfigurations {
   private static final int NUMBER_OF_PRECONFIGURED_PROFILES = 7;
   private static byte[] LOUD_0;
   private static byte[] DISCREET_0;
   private static byte[] QUIET_0;
   private static byte[] DEFAULT_0;
   private static byte[] USER_0;
   private static byte[] OFF_0;
   private static byte[] PHONE_ONLY_0;
   private static byte[] CALENDAR_LOUD_0;
   private static byte[] CALENDAR_DISCREET_0;
   private static byte[] CALENDAR_QUIET_0;
   private static byte[] CALENDAR_DEFAULT_0;
   private static byte[] CALENDAR_USER_0;
   private static byte[] CALENDAR_OFF_0;
   private static byte[] CALENDAR_PHONE_ONLY_0;
   private static byte[] TASK_LOUD_0;
   private static byte[] TASK_DISCREET_0;
   private static byte[] TASK_QUIET_0;
   private static byte[] TASK_DEFAULT_0;
   private static byte[] TASK_USER_0;
   private static byte[] TASK_OFF_0;
   private static byte[] TASK_PHONE_ONLY_0;
   private static byte[] MESSAGES_LOUD_0;
   private static byte[] MESSAGES_DISCREET_0;
   private static byte[] MESSAGES_QUIET_0;
   private static byte[] MESSAGES_DEFAULT_0;
   private static byte[] MESSAGES_USER_0;
   private static byte[] MESSAGES_OFF_0;
   private static byte[] MESSAGES_PHONE_ONLY_0;
   private static byte[] LEVEL_1_MESSAGES_LOUD_0;
   private static byte[] LEVEL_1_MESSAGES_DISCREET_0;
   private static byte[] LEVEL_1_MESSAGES_QUIET_0;
   private static byte[] LEVEL_1_MESSAGES_DEFAULT_0;
   private static byte[] LEVEL_1_MESSAGES_USER_0;
   private static byte[] LEVEL_1_MESSAGES_OFF_0;
   private static byte[] LEVEL_1_MESSAGES_PHONE_ONLY_0;
   private static byte[] SMS_LOUD_0;
   private static byte[] SMS_DISCREET_0;
   private static byte[] SMS_QUIET_0;
   private static byte[] SMS_DEFAULT_0;
   private static byte[] SMS_USER_0;
   private static byte[] SMS_OFF_0;
   private static byte[] SMS_PHONE_ONLY_0;
   private static byte[] MMS_LOUD_0;
   private static byte[] MMS_DISCREET_0;
   private static byte[] MMS_QUIET_0;
   private static byte[] MMS_DEFAULT_0;
   private static byte[] MMS_USER_0;
   private static byte[] MMS_OFF_0;
   private static byte[] MMS_PHONE_ONLY_0;
   private static byte[] PHONE_LOUD_0;
   private static byte[] PHONE_DISCREET_0;
   private static byte[] PHONE_QUIET_0;
   private static byte[] PHONE_DEFAULT_0;
   private static byte[] PHONE_USER_0;
   private static byte[] PHONE_OFF_0;
   private static byte[] PHONE_PHONE_ONLY_0;
   private static byte[] DC_PHONE_LOUD_0;
   private static byte[] DC_PHONE_DISCREET_0;
   private static byte[] DC_PHONE_QUIET_0;
   private static byte[] DC_PHONE_DEFAULT_0;
   private static byte[] DC_PHONE_USER_0;
   private static byte[] DC_PHONE_OFF_0;
   private static byte[] DC_PHONE_PHONE_ONLY_0;
   private static byte[] DC_ALERT_LOUD_0;
   private static byte[] DC_ALERT_DISCREET_0;
   private static byte[] DC_ALERT_QUIET_0;
   private static byte[] DC_ALERT_DEFAULT_0;
   private static byte[] DC_ALERT_USER_0;
   private static byte[] DC_ALERT_OFF_0;
   private static byte[] DC_ALERT_PHONE_ONLY_0;
   private static byte[] BROWSER_LOUD_0;
   private static byte[] BROWSER_DISCREET_0;
   private static byte[] BROWSER_QUIET_0;
   private static byte[] BROWSER_DEFAULT_0;
   private static byte[] BROWSER_USER_0;
   private static byte[] BROWSER_OFF_0;
   private static byte[] BROWSER_PHONE_ONLY_0;
   private static byte[] PAGING_LOUD_0;
   private static byte[] PAGING_DISCREET_0;
   private static byte[] PAGING_QUIET_0;
   private static byte[] PAGING_DEFAULT_0;
   private static byte[] PAGING_USER_0;
   private static byte[] PAGING_OFF_0;
   private static byte[] PAGING_PHONE_ONLY_0;
   private static byte[] KPTT_ALERT_LOUD_0;
   private static byte[] KPTT_ALERT_DISCREET_0;
   private static byte[] KPTT_ALERT_QUIET_0;
   private static byte[] KPTT_ALERT_DEFAULT_0;
   private static byte[] KPTT_ALERT_USER_0;
   private static byte[] KPTT_ALERT_OFF_0;
   private static byte[] KPTT_ALERT_PHONE_ONLY_0;
   private static byte[] KPTT_MESSAGE_LOUD_0;
   private static byte[] KPTT_MESSAGE_DISCREET_0;
   private static byte[] KPTT_MESSAGE_QUIET_0;
   private static byte[] KPTT_MESSAGE_DEFAULT_0;
   private static byte[] KPTT_MESSAGE_USER_0;
   private static byte[] KPTT_MESSAGE_OFF_0;
   private static byte[] KPTT_MESSAGE_PHONE_ONLY_0;
   private static byte[][] _nonAppSpecificDefaults;
   private static byte[][] _calendarAppDefaults;
   private static byte[][] _taskAppDefaults;
   private static byte[][] _messagesAppDefaults;
   private static byte[][] _smsAppDefaults;
   private static byte[][] _mmsAppDefaults;
   private static byte[][] _level1MessagesAppDefaults;
   private static byte[][] _phoneAppDefaults;
   private static byte[][] _dcPhoneDefaults;
   private static byte[][] _dcAlertDefaults;
   private static byte[][] _browserAppDefaults;
   private static byte[][] _pagingDefaults;
   private static byte[][] _kpttAlertDefaults;
   private static byte[][] _kpttMessageDefaults;
   private static LongHashtable _appSpecificDefaultsForSystemProfiles;
   private static LongHashtable _appSpecificDefaultsForUserProfiles;

   private static final synchronized void init() {
      if (_nonAppSpecificDefaults == null) {
         byte phoneTuneIndex = -1;
         if (TuneManager.isTuneManagerAvailable()) {
            TuneManager tm = TuneManager.getTuneManager();
            int brandingTuneIndex = tm.getBrandingTuneIndex();
            if (brandingTuneIndex != -1) {
               phoneTuneIndex = (byte)brandingTuneIndex;
            }
         }

         initOutOfTheBoxSettings();
         if (phoneTuneIndex != -1) {
            PHONE_LOUD_0[1] = phoneTuneIndex;
            PHONE_LOUD_0[6] = phoneTuneIndex;
            PHONE_DISCREET_0[1] = phoneTuneIndex;
            PHONE_DISCREET_0[6] = phoneTuneIndex;
            PHONE_QUIET_0[1] = phoneTuneIndex;
            PHONE_QUIET_0[6] = phoneTuneIndex;
            PHONE_DEFAULT_0[1] = phoneTuneIndex;
            PHONE_DEFAULT_0[6] = phoneTuneIndex;
            PHONE_USER_0 = PHONE_DEFAULT_0;
            PHONE_OFF_0[1] = phoneTuneIndex;
            PHONE_OFF_0[6] = phoneTuneIndex;
            PHONE_PHONE_ONLY_0[1] = phoneTuneIndex;
            PHONE_PHONE_ONLY_0[6] = phoneTuneIndex;
         }

         _nonAppSpecificDefaults = new byte[7][];
         _nonAppSpecificDefaults[0] = LOUD_0;
         _nonAppSpecificDefaults[1] = DISCREET_0;
         _nonAppSpecificDefaults[3] = DEFAULT_0;
         _nonAppSpecificDefaults[2] = QUIET_0;
         _nonAppSpecificDefaults[4] = QUIET_0;
         _nonAppSpecificDefaults[5] = PHONE_ONLY_0;
         _nonAppSpecificDefaults[6] = OFF_0;
         _calendarAppDefaults = new byte[7][];
         _calendarAppDefaults[0] = CALENDAR_LOUD_0;
         _calendarAppDefaults[1] = CALENDAR_DISCREET_0;
         _calendarAppDefaults[3] = CALENDAR_DEFAULT_0;
         _calendarAppDefaults[2] = CALENDAR_QUIET_0;
         _calendarAppDefaults[4] = CALENDAR_QUIET_0;
         _calendarAppDefaults[5] = CALENDAR_PHONE_ONLY_0;
         _calendarAppDefaults[6] = CALENDAR_OFF_0;
         _taskAppDefaults = new byte[7][];
         _taskAppDefaults[0] = TASK_LOUD_0;
         _taskAppDefaults[1] = TASK_DISCREET_0;
         _taskAppDefaults[3] = TASK_DEFAULT_0;
         _taskAppDefaults[2] = TASK_QUIET_0;
         _taskAppDefaults[4] = TASK_QUIET_0;
         _taskAppDefaults[5] = TASK_PHONE_ONLY_0;
         _taskAppDefaults[6] = TASK_OFF_0;
         _messagesAppDefaults = new byte[7][];
         _messagesAppDefaults[0] = MESSAGES_LOUD_0;
         _messagesAppDefaults[1] = MESSAGES_DISCREET_0;
         _messagesAppDefaults[3] = MESSAGES_DEFAULT_0;
         _messagesAppDefaults[2] = MESSAGES_QUIET_0;
         _messagesAppDefaults[4] = MESSAGES_QUIET_0;
         _messagesAppDefaults[5] = MESSAGES_PHONE_ONLY_0;
         _messagesAppDefaults[6] = MESSAGES_OFF_0;
         _level1MessagesAppDefaults = new byte[7][];
         _level1MessagesAppDefaults[0] = LEVEL_1_MESSAGES_LOUD_0;
         _level1MessagesAppDefaults[1] = LEVEL_1_MESSAGES_DISCREET_0;
         _level1MessagesAppDefaults[3] = LEVEL_1_MESSAGES_DEFAULT_0;
         _level1MessagesAppDefaults[2] = LEVEL_1_MESSAGES_QUIET_0;
         _level1MessagesAppDefaults[4] = LEVEL_1_MESSAGES_QUIET_0;
         _level1MessagesAppDefaults[5] = LEVEL_1_MESSAGES_PHONE_ONLY_0;
         _level1MessagesAppDefaults[6] = LEVEL_1_MESSAGES_OFF_0;
         _smsAppDefaults = new byte[7][];
         _smsAppDefaults[0] = SMS_LOUD_0;
         _smsAppDefaults[1] = SMS_DISCREET_0;
         _smsAppDefaults[3] = SMS_DEFAULT_0;
         _smsAppDefaults[2] = SMS_QUIET_0;
         _smsAppDefaults[4] = SMS_QUIET_0;
         _smsAppDefaults[5] = SMS_PHONE_ONLY_0;
         _smsAppDefaults[6] = SMS_OFF_0;
         _mmsAppDefaults = new byte[7][];
         _mmsAppDefaults[0] = MMS_LOUD_0;
         _mmsAppDefaults[1] = MMS_DISCREET_0;
         _mmsAppDefaults[3] = MMS_DEFAULT_0;
         _mmsAppDefaults[2] = MMS_QUIET_0;
         _mmsAppDefaults[4] = MMS_QUIET_0;
         _mmsAppDefaults[5] = MMS_PHONE_ONLY_0;
         _mmsAppDefaults[6] = MMS_OFF_0;
         _phoneAppDefaults = new byte[7][];
         _phoneAppDefaults[0] = PHONE_LOUD_0;
         _phoneAppDefaults[1] = PHONE_DISCREET_0;
         _phoneAppDefaults[3] = PHONE_DEFAULT_0;
         _phoneAppDefaults[2] = PHONE_QUIET_0;
         _phoneAppDefaults[4] = PHONE_QUIET_0;
         _phoneAppDefaults[5] = PHONE_PHONE_ONLY_0;
         _phoneAppDefaults[6] = PHONE_OFF_0;
         _dcPhoneDefaults = new byte[7][];
         _dcPhoneDefaults[0] = DC_PHONE_LOUD_0;
         _dcPhoneDefaults[1] = DC_PHONE_DISCREET_0;
         _dcPhoneDefaults[3] = DC_PHONE_DEFAULT_0;
         _dcPhoneDefaults[2] = DC_PHONE_QUIET_0;
         _dcPhoneDefaults[4] = DC_PHONE_QUIET_0;
         _dcPhoneDefaults[5] = DC_PHONE_PHONE_ONLY_0;
         _dcPhoneDefaults[6] = DC_PHONE_OFF_0;
         _dcAlertDefaults = new byte[7][];
         _dcAlertDefaults[0] = DC_ALERT_LOUD_0;
         _dcAlertDefaults[1] = DC_ALERT_DISCREET_0;
         _dcAlertDefaults[3] = DC_ALERT_DEFAULT_0;
         _dcAlertDefaults[2] = DC_ALERT_QUIET_0;
         _dcAlertDefaults[4] = DC_ALERT_QUIET_0;
         _dcAlertDefaults[5] = DC_ALERT_PHONE_ONLY_0;
         _dcAlertDefaults[6] = DC_ALERT_OFF_0;
         _browserAppDefaults = new byte[7][];
         _browserAppDefaults[0] = BROWSER_LOUD_0;
         _browserAppDefaults[1] = BROWSER_DISCREET_0;
         _browserAppDefaults[3] = BROWSER_DEFAULT_0;
         _browserAppDefaults[2] = BROWSER_QUIET_0;
         _browserAppDefaults[4] = BROWSER_QUIET_0;
         _browserAppDefaults[5] = BROWSER_PHONE_ONLY_0;
         _browserAppDefaults[6] = BROWSER_OFF_0;
         _pagingDefaults = new byte[7][];
         _pagingDefaults[0] = PAGING_LOUD_0;
         _pagingDefaults[1] = PAGING_DISCREET_0;
         _pagingDefaults[3] = PAGING_DEFAULT_0;
         _pagingDefaults[2] = PAGING_QUIET_0;
         _pagingDefaults[4] = PAGING_DEFAULT_0;
         _pagingDefaults[5] = PAGING_PHONE_ONLY_0;
         _pagingDefaults[6] = PAGING_OFF_0;
         _kpttAlertDefaults = new byte[7][];
         _kpttAlertDefaults[0] = KPTT_ALERT_LOUD_0;
         _kpttAlertDefaults[1] = KPTT_ALERT_DISCREET_0;
         _kpttAlertDefaults[3] = KPTT_ALERT_DEFAULT_0;
         _kpttAlertDefaults[2] = KPTT_ALERT_QUIET_0;
         _kpttAlertDefaults[4] = KPTT_ALERT_DEFAULT_0;
         _kpttAlertDefaults[5] = KPTT_ALERT_PHONE_ONLY_0;
         _kpttAlertDefaults[6] = KPTT_ALERT_OFF_0;
         _kpttMessageDefaults = new byte[7][];
         _kpttMessageDefaults[0] = KPTT_MESSAGE_LOUD_0;
         _kpttMessageDefaults[1] = KPTT_MESSAGE_DISCREET_0;
         _kpttMessageDefaults[3] = KPTT_MESSAGE_DEFAULT_0;
         _kpttMessageDefaults[2] = KPTT_MESSAGE_QUIET_0;
         _kpttMessageDefaults[4] = KPTT_MESSAGE_DEFAULT_0;
         _kpttMessageDefaults[5] = KPTT_MESSAGE_PHONE_ONLY_0;
         _kpttMessageDefaults[6] = KPTT_MESSAGE_OFF_0;
         _appSpecificDefaultsForSystemProfiles = new LongHashtable();
         _appSpecificDefaultsForSystemProfiles.put(2666833733215697856L, _calendarAppDefaults);
         _appSpecificDefaultsForSystemProfiles.put(204325571560529255L, _taskAppDefaults);
         _appSpecificDefaultsForSystemProfiles.put(-1845850106795451018L, _messagesAppDefaults);
         _appSpecificDefaultsForSystemProfiles.put(-327746170160875990L, _level1MessagesAppDefaults);
         _appSpecificDefaultsForSystemProfiles.put(7986617465467730856L, _smsAppDefaults);
         _appSpecificDefaultsForSystemProfiles.put(8609386677418041260L, _mmsAppDefaults);
         _appSpecificDefaultsForSystemProfiles.put(2868625504212929964L, _phoneAppDefaults);
         _appSpecificDefaultsForSystemProfiles.put(3975384895524745189L, _dcPhoneDefaults);
         _appSpecificDefaultsForSystemProfiles.put(4237171590573870406L, _dcAlertDefaults);
         _appSpecificDefaultsForSystemProfiles.put(4665536253483290822L, _browserAppDefaults);
         _appSpecificDefaultsForSystemProfiles.put(6432934947797527350L, _pagingDefaults);
         _appSpecificDefaultsForSystemProfiles.put(9045753910170648468L, _kpttAlertDefaults);
         _appSpecificDefaultsForSystemProfiles.put(1868613464211541088L, _kpttMessageDefaults);
         _appSpecificDefaultsForUserProfiles = new LongHashtable();
         _appSpecificDefaultsForUserProfiles.put(2666833733215697856L, CALENDAR_USER_0);
         _appSpecificDefaultsForUserProfiles.put(204325571560529255L, TASK_USER_0);
         _appSpecificDefaultsForUserProfiles.put(-1845850106795451018L, MESSAGES_USER_0);
         _appSpecificDefaultsForUserProfiles.put(7986617465467730856L, SMS_USER_0);
         _appSpecificDefaultsForUserProfiles.put(8609386677418041260L, MMS_USER_0);
         _appSpecificDefaultsForUserProfiles.put(-327746170160875990L, LEVEL_1_MESSAGES_USER_0);
         _appSpecificDefaultsForUserProfiles.put(2868625504212929964L, PHONE_USER_0);
         _appSpecificDefaultsForUserProfiles.put(3975384895524745189L, DC_PHONE_USER_0);
         _appSpecificDefaultsForUserProfiles.put(4237171590573870406L, DC_ALERT_USER_0);
         _appSpecificDefaultsForUserProfiles.put(4665536253483290822L, BROWSER_USER_0);
         _appSpecificDefaultsForUserProfiles.put(6432934947797527350L, PAGING_USER_0);
         _appSpecificDefaultsForUserProfiles.put(9045753910170648468L, KPTT_ALERT_USER_0);
         _appSpecificDefaultsForUserProfiles.put(1868613464211541088L, KPTT_MESSAGE_USER_0);
      }
   }

   public static final AlertConfiguration getConfiguration(
      long consequenceIdLong, long sourceIdLong, byte profileIdentifierByte, int levelInt, Object contextObject
   ) {
      AlertConfiguration configuration = new AlertConfiguration();
      AlertConfiguration defaultConfiguration = null;
      String tuneNameOutOfHolster = null;
      String tuneNameInHolster = null;
      byte[] settings = null;
      init();
      if (profileIdentifierByte != -1) {
         byte[][] allSettingsForSource = (byte[][])_appSpecificDefaultsForSystemProfiles.get(sourceIdLong);
         if (allSettingsForSource == null) {
            long parentSourceID = NotificationsManager.getParentSourceID(sourceIdLong);
            if (parentSourceID != -1) {
               allSettingsForSource = (byte[][])_appSpecificDefaultsForSystemProfiles.get(parentSourceID);
            } else {
               allSettingsForSource = _nonAppSpecificDefaults;
            }
         }

         byte mappedIndex = profileIdentifierByte;
         if (mappedIndex == 7) {
            mappedIndex = 6;
         }

         if (mappedIndex < allSettingsForSource.length) {
            settings = allSettingsForSource[mappedIndex];
         }
      }

      if (settings == null && profileIdentifierByte != 3) {
         Profiles profiles = Profiles.getInstance();
         if (profiles != null) {
            defaultConfiguration = (AlertConfiguration)profiles.getDefault().getConfiguration(-2870941457036655797L, sourceIdLong);
            if (defaultConfiguration == null) {
               if ((settings = (byte[])_appSpecificDefaultsForUserProfiles.get(sourceIdLong)) == null) {
                  settings = USER_0;
               }
            } else {
               settings = defaultConfiguration.getSettings();
               tuneNameOutOfHolster = defaultConfiguration.getTuneName(false);
               tuneNameInHolster = defaultConfiguration.getTuneName(true);
            }
         }
      }

      configuration.initialize(settings, tuneNameOutOfHolster, tuneNameInHolster, 15);
      return configuration;
   }

   private static final void initOutOfTheBoxSettings() {
      LOUD_0 = new byte[]{3, 12, 3, 2, 1, 3, 12, 3, 2, 1, 0, 0, 0, 1, 1};
      DISCREET_0 = new byte[]{2, 12, 0, 2, 1, 2, 12, 0, 2, 1, 0, 0, 0, 1, 1};
      QUIET_0 = new byte[]{0, 12, 0, 0, 1, 0, 12, 0, 0, 1, 0, 0, 0, 1, 1};
      DEFAULT_0 = new byte[]{0, 12, 0, 0, 1, 2, 12, 0, 0, 1, 0, 0, 0, 1, 1};
      USER_0 = DEFAULT_0;
      OFF_0 = new byte[]{0, 12, 0, 0, 0, 0, 12, 0, 0, 0, 0, 0, 0, 1, 1};
      PHONE_ONLY_0 = QUIET_0;
      CALENDAR_LOUD_0 = new byte[]{3, 30, 3, 2, 1, 3, 30, 3, 2, 1, 0, 1, 0, 1, 1};
      CALENDAR_DISCREET_0 = new byte[]{2, 17, 0, 2, 1, 2, 17, 0, 2, 1, 0, 1, 0, 1, 1};
      CALENDAR_QUIET_0 = new byte[]{0, 25, 0, 0, 1, 0, 25, 0, 0, 1, 0, 0, 0, 1, 1};
      CALENDAR_DEFAULT_0 = new byte[]{0, 17, 0, 0, 1, 2, 17, 0, 0, 1, 0, 1, 0, 1, 1};
      CALENDAR_USER_0 = CALENDAR_DEFAULT_0;
      CALENDAR_OFF_0 = new byte[]{0, 17, 0, 0, 0, 0, 17, 0, 0, 0, 0, 0, 0, 1, 1};
      CALENDAR_PHONE_ONLY_0 = new byte[]{0, 17, 0, 0, 1, 0, 17, 0, 0, 1, 0, 0, 0, 1, 1};
      TASK_LOUD_0 = new byte[]{3, 30, 3, 2, 1, 3, 30, 3, 2, 1, 0, 0, 0, 1, 1};
      TASK_DISCREET_0 = new byte[]{2, 11, 0, 2, 1, 2, 11, 0, 2, 1, 0, 0, 0, 1, 1};
      TASK_QUIET_0 = new byte[]{0, 25, 0, 0, 1, 0, 25, 0, 0, 1, 0, 0, 0, 1, 1};
      TASK_DEFAULT_0 = new byte[]{0, 11, 0, 0, 1, 2, 11, 0, 0, 1, 0, 0, 0, 1, 1};
      TASK_USER_0 = TASK_DEFAULT_0;
      TASK_OFF_0 = new byte[]{0, 11, 0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 1, 1};
      TASK_PHONE_ONLY_0 = new byte[]{0, 11, 0, 0, 1, 0, 11, 0, 0, 1, 0, 0, 0, 1, 1};
      MESSAGES_LOUD_0 = new byte[]{3, 19, 3, 2, 1, 3, 19, 3, 2, 1, 0, 0, 0, 1, 1};
      MESSAGES_DISCREET_0 = new byte[]{2, 13, 0, 2, 1, 2, 13, 0, 2, 1, 0, 0, 0, 1, 1};
      MESSAGES_QUIET_0 = new byte[]{0, 18, 0, 0, 1, 0, 18, 0, 0, 1, 0, 0, 0, 1, 1};
      MESSAGES_DEFAULT_0 = new byte[]{0, 13, 0, 0, 1, 2, 13, 0, 0, 1, 0, 0, 0, 1, 1};
      MESSAGES_USER_0 = MESSAGES_DEFAULT_0;
      MESSAGES_OFF_0 = new byte[]{0, 13, 0, 0, 0, 0, 13, 0, 0, 0, 0, 0, 0, 1, 1};
      MESSAGES_PHONE_ONLY_0 = new byte[]{0, 13, 0, 0, 1, 0, 13, 0, 0, 1, 0, 0, 0, 1, 1};
      LEVEL_1_MESSAGES_LOUD_0 = new byte[]{3, 5, 3, 2, 1, 3, 5, 3, 2, 1, 0, 0, 0, 1, 1};
      LEVEL_1_MESSAGES_DISCREET_0 = new byte[]{2, 27, 0, 2, 1, 2, 27, 0, 2, 1, 0, 0, 0, 1, 1};
      LEVEL_1_MESSAGES_QUIET_0 = new byte[]{0, 10, 0, 0, 1, 0, 10, 0, 0, 1, 0, 0, 0, 1, 1};
      LEVEL_1_MESSAGES_DEFAULT_0 = new byte[]{0, 27, 0, 0, 1, 2, 27, 0, 0, 1, 0, 0, 0, 1, 1};
      LEVEL_1_MESSAGES_USER_0 = LEVEL_1_MESSAGES_DEFAULT_0;
      LEVEL_1_MESSAGES_OFF_0 = new byte[]{0, 27, 0, 0, 0, 0, 27, 0, 0, 0, 0, 0, 0, 1, 1};
      LEVEL_1_MESSAGES_PHONE_ONLY_0 = new byte[]{0, 27, 0, 0, 1, 0, 27, 0, 0, 1, 0, 0, 0, 1, 1};
      SMS_LOUD_0 = new byte[]{3, 19, 3, 2, 1, 3, 19, 3, 2, 1, 0, 0, 0, 1, 1};
      SMS_DISCREET_0 = new byte[]{2, 7, 0, 2, 1, 2, 7, 0, 2, 1, 0, 0, 0, 1, 1};
      SMS_QUIET_0 = new byte[]{0, 22, 0, 0, 1, 0, 22, 0, 0, 1, 0, 0, 0, 1, 1};
      SMS_DEFAULT_0 = new byte[]{1, 7, 2, 0, 1, 3, 7, 2, 0, 1, 0, 0, 0, 1, 1};
      SMS_USER_0 = SMS_DEFAULT_0;
      SMS_OFF_0 = new byte[]{0, 7, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 1, 1};
      SMS_PHONE_ONLY_0 = new byte[]{0, 7, 0, 0, 1, 0, 7, 0, 0, 1, 0, 0, 0, 1, 1};
      MMS_LOUD_0 = new byte[]{3, 19, 3, 2, 1, 3, 19, 3, 2, 1, 0, 0, 0, 1, 1};
      MMS_DISCREET_0 = new byte[]{2, 7, 0, 2, 1, 2, 7, 0, 2, 1, 0, 0, 0, 1, 1};
      MMS_QUIET_0 = new byte[]{0, 22, 0, 0, 1, 0, 22, 0, 0, 1, 0, 0, 0, 1, 1};
      MMS_DEFAULT_0 = new byte[]{1, 7, 2, 0, 1, 3, 7, 2, 0, 1, 0, 0, 0, 1, 1};
      MMS_USER_0 = MMS_DEFAULT_0;
      MMS_OFF_0 = new byte[]{0, 7, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 1, 1};
      MMS_PHONE_ONLY_0 = new byte[]{0, 7, 0, 0, 1, 0, 7, 0, 0, 1, 0, 0, 0, 1, 1};
      PHONE_LOUD_0 = new byte[]{3, 31, 2, 2, 1, 3, 31, 2, 2, 1, 0, 0, 0, 1, 1};
      PHONE_DISCREET_0 = new byte[]{2, 34, 0, 2, 1, 2, 34, 0, 2, 1, 0, 0, 0, 1, 1};
      PHONE_QUIET_0 = new byte[]{0, 18, 0, 0, 1, 0, 18, 0, 0, 1, 0, 0, 0, 1, 1};
      PHONE_DEFAULT_0 = new byte[]{1, 34, 2, 0, 1, 3, 34, 2, 0, 1, 0, 0, 0, 1, 1};
      PHONE_USER_0 = PHONE_DEFAULT_0;
      PHONE_OFF_0 = new byte[]{0, 34, 2, 0, 0, 0, 34, 2, 0, 0, 0, 0, 0, 1, 1};
      PHONE_PHONE_ONLY_0 = new byte[]{1, 31, 2, 0, 1, 3, 31, 2, 0, 1, 0, 0, 0, 1, 1};
      DC_PHONE_LOUD_0 = new byte[]{3, 28, 2, 2, 1, 3, 28, 2, 2, 1, 0, 0, 0, 1, 1};
      DC_PHONE_DISCREET_0 = new byte[]{2, 28, 0, 2, 1, 2, 28, 0, 2, 1, 0, 0, 0, 1, 1};
      DC_PHONE_QUIET_0 = new byte[]{0, 28, 0, 0, 1, 0, 28, 0, 0, 1, 0, 0, 0, 1, 1};
      DC_PHONE_DEFAULT_0 = new byte[]{1, 28, 2, 0, 1, 3, 28, 1, 0, 1, 0, 0, 0, 1, 1};
      DC_PHONE_USER_0 = DC_PHONE_DEFAULT_0;
      DC_PHONE_OFF_0 = new byte[]{0, 28, 2, 0, 0, 0, 28, 1, 0, 0, 0, 0, 0, 1, 1};
      DC_PHONE_PHONE_ONLY_0 = DC_PHONE_DEFAULT_0;
      DC_ALERT_LOUD_0 = new byte[]{3, 19, 3, 2, 1, 3, 19, 3, 2, 1, 0, 0, 0, 1, 1};
      DC_ALERT_DISCREET_0 = new byte[]{2, 19, 0, 2, 1, 2, 19, 0, 2, 1, 0, 0, 0, 1, 1};
      DC_ALERT_QUIET_0 = new byte[]{0, 19, 0, 0, 1, 0, 19, 0, 0, 1, 0, 0, 0, 1, 1};
      DC_ALERT_DEFAULT_0 = new byte[]{1, 19, 3, 0, 1, 3, 19, 3, 0, 1, 0, 0, 0, 1, 1};
      DC_ALERT_USER_0 = DC_ALERT_DEFAULT_0;
      DC_ALERT_OFF_0 = new byte[]{0, 19, 3, 0, 0, 0, 19, 3, 0, 0, 0, 0, 0, 1, 1};
      DC_ALERT_PHONE_ONLY_0 = DC_ALERT_DEFAULT_0;
      BROWSER_LOUD_0 = new byte[]{3, 28, 3, 2, 0, 3, 28, 3, 2, 0, 0, 0, 0, 1, 1};
      BROWSER_DISCREET_0 = new byte[]{2, 20, 0, 2, 0, 2, 20, 0, 2, 0, 0, 0, 0, 1, 1};
      BROWSER_QUIET_0 = new byte[]{0, 21, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 0, 1, 1};
      BROWSER_DEFAULT_0 = new byte[]{0, 20, 0, 0, 0, 2, 20, 0, 0, 0, 0, 0, 0, 1, 1};
      BROWSER_USER_0 = BROWSER_DEFAULT_0;
      BROWSER_OFF_0 = new byte[]{0, 20, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 1, 1};
      BROWSER_PHONE_ONLY_0 = new byte[]{0, 20, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 1, 1};
      PAGING_LOUD_0 = new byte[]{1, 16, 3, 2, 1, 3, 16, 3, 2, 1, 0, 0, 0, 1, 1};
      PAGING_DISCREET_0 = new byte[]{1, 16, 1, 2, 1, 3, 16, 1, 2, 1, 0, 0, 0, 1, 1};
      PAGING_QUIET_0 = new byte[]{0, 16, 0, 2, 1, 0, 16, 0, 2, 1, 0, 0, 0, 1, 1};
      PAGING_DEFAULT_0 = new byte[]{1, 16, 2, 2, 1, 3, 16, 2, 2, 1, 0, 0, 0, 1, 1};
      PAGING_USER_0 = PAGING_DEFAULT_0;
      PAGING_OFF_0 = new byte[]{0, 16, 2, 2, 0, 0, 16, 2, 2, 0, 0, 0, 0, 1, 1};
      PAGING_PHONE_ONLY_0 = PAGING_DEFAULT_0;
      KPTT_ALERT_LOUD_0 = new byte[]{3, 24, 2, 2, 1, 3, 24, 2, 2, 1, 0, 0, 0, 1, 1};
      KPTT_ALERT_DISCREET_0 = new byte[]{2, 24, 0, 2, 1, 2, 24, 0, 2, 1, 0, 0, 0, 1, 1};
      KPTT_ALERT_QUIET_0 = new byte[]{0, 24, 0, 0, 1, 0, 24, 0, 0, 1, 0, 0, 0, 1, 1};
      KPTT_ALERT_DEFAULT_0 = new byte[]{1, 24, 2, 0, 1, 3, 24, 2, 0, 1, 0, 0, 0, 1, 1};
      KPTT_ALERT_USER_0 = KPTT_ALERT_DEFAULT_0;
      KPTT_ALERT_OFF_0 = new byte[]{0, 24, 2, 0, 0, 0, 24, 2, 0, 0, 0, 0, 0, 1, 1};
      KPTT_ALERT_PHONE_ONLY_0 = KPTT_ALERT_DEFAULT_0;
      KPTT_MESSAGE_LOUD_0 = new byte[]{3, 26, 2, 2, 1, 3, 26, 2, 2, 1, 0, 0, 0, 1, 1};
      KPTT_MESSAGE_DISCREET_0 = new byte[]{2, 26, 0, 2, 1, 2, 26, 0, 2, 1, 0, 0, 0, 1, 1};
      KPTT_MESSAGE_QUIET_0 = new byte[]{0, 26, 0, 0, 1, 0, 26, 0, 0, 1, 0, 0, 0, 1, 1};
      KPTT_MESSAGE_DEFAULT_0 = new byte[]{1, 26, 2, 0, 1, 3, 26, 2, 0, 1, 0, 0, 0, 1, 1};
      KPTT_MESSAGE_USER_0 = KPTT_MESSAGE_DEFAULT_0;
      KPTT_MESSAGE_OFF_0 = new byte[]{0, 26, 2, 0, 0, 0, 26, 2, 0, 0, 0, 0, 0, 1, 1};
      KPTT_MESSAGE_PHONE_ONLY_0 = KPTT_MESSAGE_DEFAULT_0;
   }
}
