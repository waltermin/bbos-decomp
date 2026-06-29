package net.rim.device.apps.api.framework.hotkeys;

import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.framework.verb.Verb;

public class HotKeys {
   public static final int MESSAGES_LIST = 1;
   public static final int EMAIL_VIEW_SCREEN = 2;
   public static final int SAVED_MESSAGES_LIST = 3;
   public static final int CALENDAR = 4;
   public static final int MEMOPAD = 5;
   public static final int TASKS = 6;
   public static final int SEARCH = 7;
   public static final int BROWSER = 8;
   private static final long KEY = 6579610397692695143L;
   public static final long GUID_HOTKEYS_CHANGED = -273986034351666339L;
   private static IntHashtable _listTable;

   public static boolean registerHotKey(int hotKeyListGUID, char key, Verb action) {
      return registerEntry(hotKeyListGUID, new HotKeyEntry(key, action));
   }

   private static boolean registerEntry(int hotKeyListGUID, HotKeyEntry entry) {
      IntHashtable table;
      if (!_listTable.containsKey(hotKeyListGUID)) {
         table = (IntHashtable)(new Object());
         _listTable.put(hotKeyListGUID, table);
      } else {
         table = (IntHashtable)_listTable.get(hotKeyListGUID);
         if (table.containsKey(entry._hotkey)) {
            return false;
         }
      }

      table.put(entry._hotkey, entry);
      return true;
   }

   public static boolean registerHotKey(int hotKeyListGUID, char key, Verb action, boolean bothCases) {
      key = Character.toLowerCase(key);
      boolean returnVal = registerHotKey(hotKeyListGUID, key, action);
      if (bothCases && returnVal) {
         returnVal = registerHotKey(hotKeyListGUID, Character.toUpperCase(key), action);
      }

      return returnVal;
   }

   public static boolean registerHotKey(int hotKeyListGUID, ResourceBundleFamily family, int rbKey, Verb action, boolean bothCases) {
      if (rbKey >= 0 && family != null && action != null) {
         boolean returnVal = registerEntry(hotKeyListGUID, new HotKeyEntry(family, rbKey, action, false));
         if (bothCases && returnVal) {
            returnVal = registerEntry(hotKeyListGUID, new HotKeyEntry(family, rbKey, action, true));
         }

         return returnVal;
      } else {
         return false;
      }
   }

   public static boolean unregisterHotKey(int hotKeyListGUID, char key) {
      IntHashtable table = (IntHashtable)_listTable.get(hotKeyListGUID);
      if (table != null && table.containsKey(key)) {
         table.remove(key);
         if (table.isEmpty()) {
            _listTable.remove(hotKeyListGUID);
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean unregisterHotKey(int hotKeyListGUID, char key, boolean bothCases) {
      key = Character.toLowerCase(key);
      boolean returnVal = unregisterHotKey(hotKeyListGUID, key);
      if (bothCases && returnVal) {
         returnVal = unregisterHotKey(hotKeyListGUID, Character.toUpperCase(key));
      }

      return returnVal;
   }

   public static boolean clearHotKeys() {
      IntEnumeration keyEnum = _listTable.keys();

      while (keyEnum.hasMoreElements()) {
         int listID = keyEnum.nextElement();
         if (listID != 7) {
            IntHashtable table = (IntHashtable)_listTable.get(listID);
            if (table != null) {
               int[] keys = new int[table.size()];
               table.keysToArray(keys);
               Vector entryList = (Vector)(new Object());

               for (int i = 0; i < keys.length; i++) {
                  HotKeyEntry entry = (HotKeyEntry)table.get(keys[i]);
                  if (entry != null && entry.canUpdate()) {
                     entry.update();
                     entryList.addElement(entry);
                  }
               }

               table.clear();
               int count = entryList.size();

               for (int i = 0; i < count; i++) {
                  HotKeyEntry entry = (HotKeyEntry)entryList.elementAt(i);
                  table.put(entry._hotkey, entry);
               }

               if (table.size() == 0) {
                  _listTable.remove(listID);
               }
            }
         }
      }

      return true;
   }

   public static char[] getUsedHotKeys(int hotKeyListGUID) {
      IntHashtable table = (IntHashtable)_listTable.get(hotKeyListGUID);
      if (table == null) {
         return null;
      }

      int size = table.size();
      int[] keysArray = new int[size];
      char[] returnArray = new char[size];
      table.keysToArray(keysArray);

      for (int i = 0; i < size; i++) {
         returnArray[i] = (char)keysArray[i];
      }

      return returnArray;
   }

   public static Verb getVerb(int hotKeyListGUID, char key) {
      IntHashtable table = (IntHashtable)_listTable.get(hotKeyListGUID);
      if (table == null) {
         return null;
      }

      HotKeyEntry entry = (HotKeyEntry)table.get(key);
      return entry == null ? null : entry._verb;
   }

   static {
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      _listTable = (IntHashtable)appReg.getOrWaitFor(6579610397692695143L);
      if (_listTable == null) {
         _listTable = (IntHashtable)(new Object());
         appReg.put(6579610397692695143L, _listTable);
         PersistentContent.addListener(new HotKeysPersistentContentListener(_listTable));
      }
   }
}
