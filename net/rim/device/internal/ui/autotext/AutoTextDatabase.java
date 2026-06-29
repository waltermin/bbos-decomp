package net.rim.device.internal.ui.autotext;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;

final class AutoTextDatabase implements Persistable {
   private IntHashtable _autoTextTables = new IntHashtable();

   final void clear() {
      this._autoTextTables.clear();
   }

   final Enumeration getElements(Locale locale) {
      return new AutoTextDatabase$Enumerator(this._autoTextTables, locale);
   }

   final Enumeration getAllElements() {
      return new AutoTextDatabase$Enumerator(this._autoTextTables, null);
   }

   final Enumeration enumEntryTables() {
      return this._autoTextTables.elements();
   }

   final boolean isEmpty() {
      if (this._autoTextTables.isEmpty()) {
         return true;
      }

      Enumeration e = this._autoTextTables.elements();

      while (e.hasMoreElements()) {
         Hashtable table = (Hashtable)e.nextElement();
         if (!table.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   final Hashtable getEntries(int localeCode) {
      if ((localeCode & -65536) == 2053636096) {
         localeCode = 1701707776;
      }

      Hashtable result = (Hashtable)this._autoTextTables.get(localeCode);
      if (result == null) {
         result = new Hashtable();
         this._autoTextTables.put(localeCode, result);
      }

      return result;
   }

   final void populate(boolean clearDatabase) {
      if (clearDatabase) {
         this.clear();
      }

      int localeCode = Locale.getDefaultInputForSystem().getCode();
      this.loadEntries(0);
      this.loadEntries(localeCode);
      if ((localeCode & 65535) != 0) {
         this.loadEntries(localeCode & -65536);
      }
   }

   final void populateIfEmpty() {
      int localeCode = Locale.getDefaultInputForSystem().getCode();
      Hashtable table = (Hashtable)this._autoTextTables.get(localeCode);
      if (table == null || table.isEmpty()) {
         this.loadEntries(localeCode);
      }

      table = (Hashtable)this._autoTextTables.get(localeCode & -65536);
      if (table == null || table.isEmpty()) {
         this.loadEntries(localeCode & -65536);
      }
   }

   private final void loadEntries(int localeCode) {
      if ((localeCode & -65536) == 2053636096) {
         localeCode = 1701707776;
      }

      Hashtable entries = (Hashtable)this._autoTextTables.get(localeCode);
      if (entries == null) {
         entries = load(localeCode);
         this._autoTextTables.put(localeCode, entries);
      }
   }

   private static final Hashtable load(int localeCode) {
      String[] entries = getStringTable(localeCode);
      if (entries == null) {
         return new Hashtable();
      }

      int n = entries.length;
      Hashtable hashtable = new Hashtable(n * 3 >> 1);

      for (int i = n - 1; i >= 0; i--) {
         int flags = 0;
         String entryString = entries[i];
         if (entryString.charAt(1) != ' ') {
            System.out.println("Invalid AutoText entry: " + entryString);
         } else {
            boolean valid = true;
            switch (entryString.charAt(0)) {
               case '.':
                  flags = 0;
                  break;
               case 'C':
                  flags = 1;
                  break;
               default:
                  System.out.println("Invalid AutoText entry: " + entryString);
                  valid = false;
            }

            if (valid) {
               int endOfFind = 2;

               do {
                  endOfFind = entryString.indexOf(32, endOfFind);
                  if (endOfFind == -1) {
                     valid = false;
                     System.out.println("Invalid AutoText entry: " + entryString);
                     break;
                  }
               } while (entryString.charAt(endOfFind - 1) == '\\');

               if (valid) {
                  String find = entryString.substring(2, endOfFind);
                  String replace = entryString.substring(endOfFind + 1);
                  AutoTextEntry entry = new AutoTextEntry(find, replace, flags, localeCode);
                  if (!ObjectGroup.isInGroup(entry)) {
                     ObjectGroup.createGroupIgnoreTooBig(entry);
                  }

                  hashtable.put(entry.getFindString(), entry);
               }
            }
         }
      }

      return hashtable;
   }

   private static final String[] getStringTable(int localeCode) {
      ResourceBundleFamily family = ResourceBundle.getBundle(8562590855522002223L, "net.rim.device.internal.resource.Input");
      ResourceBundle bundle = family.getBundle(Locale.get(localeCode));
      if (bundle.getLocale().getCode() != localeCode) {
         return null;
      }

      String[] entries = (String[])bundle.getObject(1, false);
      if (entries == null) {
         return null;
      }

      if (DirectConnect.isSupported()) {
         String[] dcEntries = (String[])bundle.getObject(17, false);
         if (dcEntries != null && dcEntries.length > 0) {
            int oldLen = entries.length;
            Array.resize(entries, oldLen + dcEntries.length);
            System.arraycopy(dcEntries, 0, entries, oldLen, dcEntries.length);
         }
      }

      return entries;
   }

   final void reCrypt(boolean encrypt) {
      Enumeration enumeration = this._autoTextTables.elements();

      while (enumeration.hasMoreElements()) {
         this.reCrypt((Hashtable)enumeration.nextElement(), encrypt);
      }
   }

   private final void reCrypt(Hashtable hashtable, boolean encrypt) {
      Enumeration enumeration = hashtable.keys();

      while (enumeration.hasMoreElements()) {
         Object key = enumeration.nextElement();
         AutoTextEntry value = (AutoTextEntry)hashtable.get(key);
         if (!value.checkCrypt(encrypt)) {
            AutoTextEntry newValue = value.reCrypt(encrypt);
            hashtable.put(newValue.getFindString(), newValue);
         }
      }
   }
}
