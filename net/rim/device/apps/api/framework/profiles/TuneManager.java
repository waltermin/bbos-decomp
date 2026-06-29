package net.rim.device.apps.api.framework.profiles;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.system.AlertPlayer;

public class TuneManager {
   protected static final long GUID = 8253404770240635913L;
   public static final byte ALARM_ANTELOPE = 0;
   public static final byte NOTIFIER_2LOUD = 5;
   public static final byte NOTIFIER_BBPRO_5 = 6;
   public static final byte NOTIFIER_BBPRO_6 = 7;
   public static final byte NOTIFIER_CHIGONG = 10;
   public static final byte NOTIFIER_CONTENTMENT = 11;
   public static final byte NOTIFIER_CRICKET = 12;
   public static final byte NOTIFIER_CRYSTAL = 13;
   public static final byte NOTIFIER_ENTRANCE = 16;
   public static final byte NOTIFIER_ETVOILA = 17;
   public static final byte NOTIFIER_GREETER = 18;
   public static final byte NOTIFIER_LIGHTSPEED = 19;
   public static final byte NOTIFIER_LUCID = 20;
   public static final byte NOTIFIER_MAGIC = 21;
   public static final byte NOTIFIER_MORNING = 22;
   public static final byte NOTIFIER_NYMPH = 24;
   public static final byte NOTIFIER_READY = 25;
   public static final byte NOTIFIER_SOAPBREAK = 26;
   public static final byte NOTIFIER_SONAR = 27;
   public static final byte NOTIFIER_SPELL = 28;
   public static final byte NOTIFIER_UFO = 30;
   public static final byte RINGER_BBPRO_1 = 31;
   public static final byte RINGER_BBPRO_4 = 34;

   public static boolean isTuneManagerAvailable() {
      return ApplicationRegistry.getApplicationRegistry().get(8253404770240635913L) != null;
   }

   public static TuneManager getTuneManager() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (TuneManager)ar.waitFor(8253404770240635913L);
   }

   public boolean isBuiltinTune(String _1) {
      throw null;
   }

   public boolean isTuneAvailable(int _1) {
      throw null;
   }

   public boolean isBrandingTune(String _1) {
      throw null;
   }

   public boolean isTuneAvailable(String _1) {
      throw null;
   }

   public String getBrandingTuneFileName() {
      throw null;
   }

   public String getBuiltInTuneFileName(int _1) {
      throw null;
   }

   public String[] getAllTuneFileNames() {
      throw null;
   }

   public String getDisplayName(String _1) {
      throw null;
   }

   public String getLegacyName(String _1) {
      throw null;
   }

   public int getIndex(String[] _1, String _2) {
      throw null;
   }

   public AlertPlayer getTune(String _1) {
      throw null;
   }

   public void showTuneListingScreen() {
      throw null;
   }

   public int getBuiltInTuneCount() {
      throw null;
   }

   public int getTuneCount() {
      throw null;
   }

   public TuneChoiceField getTuneChoiceField(String _1, String _2, String _3, boolean _4) {
      throw null;
   }

   public boolean isValidTuneEntry(String _1) {
      throw null;
   }

   public int getBrandingTuneIndex() {
      throw null;
   }

   public String resolveTune(String _1) {
      throw null;
   }
}
