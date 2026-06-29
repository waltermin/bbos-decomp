package net.rim.device.api.system;

import net.rim.device.api.util.Arrays;
import net.rim.device.internal.system.SIMCardEfHandler;

public final class CellBroadcast {
   private static final long CHANNEL_INFOS_GUID = 757118313738273256L;
   private static final int[] langPrefTable = new int[]{
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      32,
      -804650996,
      15,
      14,
      15,
      11,
      43,
      47,
      11,
      15,
      15,
      15,
      47,
      11,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      -804650991,
      25701,
      25966,
      26996,
      26226,
      25971,
      28268,
      29558,
      25697,
      28788,
      26217,
      28271,
      25964,
      29810,
      26741,
      28780,
      65535,
      25459,
      -805044096,
      -2119164771,
      701964907,
      -1164754652,
      955341807,
      -847833802,
      943449308,
      -1793643343,
      2112113261,
      -1649273728,
      -427103605,
      877887930,
      1289267590,
      1527690603,
      -1865867928
   };
   private static final int MAX_LANG_PREFS = langPrefTable.length;
   private static final int[] ISO639_TO_DEFAULTS = new int[]{
      25701,
      25966,
      26996,
      26226,
      25971,
      28268,
      29558,
      25697,
      28788,
      26217,
      28271,
      25964,
      29810,
      26741,
      28780,
      65535,
      25459,
      -805044096,
      -2119164771,
      701964907,
      -1164754652,
      955341807,
      -847833802,
      943449308,
      -1793643343,
      2112113261,
      -1649273728,
      -427103605,
      877887930,
      1289267590,
      1527690603,
      -1865867928,
      -1188638039,
      351163150,
      -156814205,
      -941394230,
      -674348371,
      -1206181542,
      1116563887,
      -864947017,
      163261639,
      -1198747839,
      -1406914277,
      888128191,
      1288320825,
      -2108825997,
      363235379,
      -585894947,
      -1297664150,
      -1917201517,
      1076232448,
      16830324,
      -1972564893,
      186343757,
      -1888719018,
      1677787237,
      1867547229,
      2120378665,
      526977875,
      1850065631,
      1938056283,
      -1016463104,
      7612483,
      1136880129,
      1819242306,
      -1016463104,
      1729121347,
      -1016463104
   };

   public static final boolean isSupported() {
      return RadioInfo.areWAFsSupported(1);
   }

   public static final native void enableCellBroadcast(boolean var0);

   public static final CellBroadcast$ChannelInfo[] getChannelInfos() {
      CellBroadcast$ChannelInfo[] internalInfos = getInternalChannelInfos();
      synchronized (internalInfos) {
         boolean first = true;
         boolean commit = false;
         boolean[] found = new boolean[internalInfos.length];

         int id;
         while ((id = getNextChannelIdInternal(first)) != -1) {
            first = false;

            int i;
            for (i = internalInfos.length - 1; i >= 0; i--) {
               if (internalInfos[i].getId() == id) {
                  if (i < found.length) {
                     found[i] = true;
                  }
                  break;
               }
            }

            if (i < 0) {
               Arrays.add(internalInfos, new CellBroadcast$ChannelInfo(id));
               commit = true;
            }
         }

         for (int i = found.length - 1; i >= 0; i--) {
            if (internalInfos[i].isEnabled() && !found[i]) {
               internalInfos[i].setEnabled(false);
               commit = true;
            }
         }

         if (commit) {
            PersistentObject.commit(internalInfos);
         }

         CellBroadcast$ChannelInfo[] infos = new CellBroadcast$ChannelInfo[internalInfos.length];

         for (int i = infos.length - 1; i >= 0; i--) {
            infos[i] = internalInfos[i].clone();
         }

         return infos;
      }
   }

   public static final boolean addChannelInfo(CellBroadcast$ChannelInfo ci) {
      CellBroadcast$ChannelInfo[] infos = getInternalChannelInfos();
      synchronized (infos) {
         int id = ci.getId();

         for (int i = infos.length - 1; i >= 0; i--) {
            if (infos[i].getId() == id) {
               return false;
            }
         }

         Arrays.add(infos, ci);
         PersistentObject.commit(infos);
         writeChannelInfoInternal(id, ci.isEnabled());
         return true;
      }
   }

   public static final boolean deleteChannelInfo(int channelId) {
      CellBroadcast$ChannelInfo[] infos = getInternalChannelInfos();
      synchronized (infos) {
         for (int i = infos.length - 1; i >= 0; i--) {
            if (infos[i].getId() == channelId) {
               Arrays.removeAt(infos, i);
               PersistentObject.commit(infos);
               writeChannelInfoInternal(channelId, false);
               return true;
            }
         }

         return false;
      }
   }

   public static final boolean setChannelInfo(CellBroadcast$ChannelInfo info) {
      return setChannelInfo(info.getId(), info.isEnabled(), info.getNickname());
   }

   public static final boolean setChannelInfo(int channelId, boolean enabled, String nickname) {
      CellBroadcast$ChannelInfo[] infos = getInternalChannelInfos();
      synchronized (infos) {
         for (int i = infos.length - 1; i >= 0; i--) {
            if (infos[i].getId() == channelId) {
               infos[i].setEnabled(enabled);
               infos[i].setNickname(nickname);
               PersistentObject.commit(infos);
               if (enabled ^ channelExistsInternally(channelId)) {
                  writeChannelInfoInternal(channelId, enabled);
               }

               return true;
            }
         }

         return false;
      }
   }

   private static final boolean channelExistsInternally(int channelId) {
      boolean first = true;

      int id;
      while ((id = getNextChannelIdInternal(first)) != -1) {
         first = false;
         if (id == channelId) {
            return true;
         }
      }

      return false;
   }

   private static final void fillLP_TableWithDefaults(CellBroadcast$LanguagePreference[] prefs, int seenLangs) {
      for (int i = 0; i < prefs.length; i++) {
         if (prefs[i] == null) {
            for (int bit = 0; bit < MAX_LANG_PREFS; bit++) {
               if ((seenLangs & 1 << bit) == 0) {
                  prefs[i] = new CellBroadcast$LanguagePreference(getLanguagePrefEntry(bit));
                  prefs[i].setPriority(i);
                  seenLangs |= 1 << bit;
                  break;
               }
            }
         }
      }
   }

   public static final CellBroadcast$LanguagePreference[] getLanguagePreferences() {
      CellBroadcast$LanguagePreference[] prefs = new CellBroadcast$LanguagePreference[MAX_LANG_PREFS];
      boolean usimPresent = false;

      try {
         usimPresent = SIMCard.isUSIMPresent();
      } catch (SIMCardException var9) {
      }

      if (usimPresent) {
         CellBroadcast$LanguageIndication _langIndication = new CellBroadcast$LanguageIndication();
         new SIMCardEfHandler().startTask(_langIndication, true);
         prefs = _langIndication.getLangPrefs();
         return removeUnspecifiedFromLangPrefs(prefs);
      }

      int seenLangs = 0;
      boolean first = true;
      synchronized (getInternalChannelInfos()) {
         CellBroadcast$LanguagePreference pref = new CellBroadcast$LanguagePreference();

         while (getNextLanguagePrefInternal(pref, first)) {
            first = false;
            int priority = pref.getPriority();
            if (priority >= 0 && priority < MAX_LANG_PREFS && prefs[priority] == null) {
               prefs[priority] = pref;
               seenLangs |= 1 << getLanguagePrefIndex(pref.getId());
               pref = new CellBroadcast$LanguagePreference();
            }
         }

         fillLP_TableWithDefaults(prefs, seenLangs);
         return prefs;
      }
   }

   public static final CellBroadcast$LanguagePreference[] removeUnspecifiedFromLangPrefs(CellBroadcast$LanguagePreference[] prefs) {
      CellBroadcast$LanguagePreference[] newPrefs = new CellBroadcast$LanguagePreference[prefs.length - 1];
      boolean foundPrefs = false;
      int posInNewPrefs = 0;

      for (int i = 0; i < prefs.length; i++) {
         if (prefs[i].getId() == 15) {
            foundPrefs = true;
         } else {
            newPrefs[posInNewPrefs] = prefs[i];
            posInNewPrefs++;
         }
      }

      return foundPrefs ? newPrefs : prefs;
   }

   public static final boolean setLanguagePreference(CellBroadcast$LanguagePreference pref) {
      return setLanguagePreference(pref.getId(), pref.isEnabled(), pref.getPriority());
   }

   public static final boolean setLanguagePreference(int id, boolean enabled, int priority) {
      synchronized (getInternalChannelInfos()) {
         writeLanguagePrefInternal(id, enabled, priority);
         return true;
      }
   }

   public static final boolean setLanguageIndication(CellBroadcast$LanguagePreference[] langPrefs) {
      CellBroadcast$LanguageIndicationWriter _liw = new CellBroadcast$LanguageIndicationWriter(langPrefs);
      new SIMCardEfHandler().startTask(_liw, true);
      return true;
   }

   public static final int getLanguagePrefIndex(int langPref) {
      for (int index = MAX_LANG_PREFS - 1; index >= 0; index--) {
         if (langPrefTable[index] == langPref) {
            return index;
         }
      }

      return MAX_LANG_PREFS;
   }

   public static final int getLanguagePrefEntry(int index) {
      return langPrefTable[index];
   }

   private static final native int getNextChannelIdInternal(boolean var0);

   private static final native void writeChannelInfoInternal(int var0, boolean var1);

   private static final native boolean getNextLanguagePrefInternal(CellBroadcast$LanguagePreference var0, boolean var1);

   private static final native void writeLanguagePrefInternal(int var0, boolean var1, int var2);

   private static final CellBroadcast$ChannelInfo[] getInternalChannelInfos() {
      synchronized (PersistentStore.getSynchObject()) {
         PersistentObject po = PersistentStore.getPersistentObject(757118313738273256L);
         CellBroadcast$ChannelInfo[] infos = (CellBroadcast$ChannelInfo[])po.getContents();
         if (infos == null) {
            infos = new CellBroadcast$ChannelInfo[0];
            po.setContents(infos, 51);
            po.commit();
         }

         return infos;
      }
   }
}
