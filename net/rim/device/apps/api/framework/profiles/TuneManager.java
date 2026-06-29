package net.rim.device.apps.api.framework.profiles;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.system.AlertPlayer;

public class TuneManager {
   protected static final long GUID;
   public static final byte ALARM_ANTELOPE;
   public static final byte NOTIFIER_2LOUD;
   public static final byte NOTIFIER_BBPRO_5;
   public static final byte NOTIFIER_BBPRO_6;
   public static final byte NOTIFIER_CHIGONG;
   public static final byte NOTIFIER_CONTENTMENT;
   public static final byte NOTIFIER_CRICKET;
   public static final byte NOTIFIER_CRYSTAL;
   public static final byte NOTIFIER_ENTRANCE;
   public static final byte NOTIFIER_ETVOILA;
   public static final byte NOTIFIER_GREETER;
   public static final byte NOTIFIER_LIGHTSPEED;
   public static final byte NOTIFIER_LUCID;
   public static final byte NOTIFIER_MAGIC;
   public static final byte NOTIFIER_MORNING;
   public static final byte NOTIFIER_NYMPH;
   public static final byte NOTIFIER_READY;
   public static final byte NOTIFIER_SOAPBREAK;
   public static final byte NOTIFIER_SONAR;
   public static final byte NOTIFIER_SPELL;
   public static final byte NOTIFIER_UFO;
   public static final byte RINGER_BBPRO_1;
   public static final byte RINGER_BBPRO_4;

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
