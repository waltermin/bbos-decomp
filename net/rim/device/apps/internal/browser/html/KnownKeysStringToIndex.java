package net.rim.device.apps.internal.browser.html;

import java.util.Vector;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.apps.internal.browser.markup.HTMLUtilities;

class KnownKeysStringToIndex {
   private Vector _v = (Vector)(new Object());
   private int _index = 512;
   private ToIntHashtable _h = (ToIntHashtable)(new Object());
   static final int ID_INDEX;
   static final int NAME_INDEX;
   public static final int EMPTY_STR;
   private static final int NULLINDEX;
   private static final int TAGS_INDEX_START;
   private static final int TAGS_INDEX_END;
   public static final int ATTRIBUTES_INDEX_START;
   public static final int ATTRIBUTES_INDEX_END;
   private static final int VALUES_START;
   static String NAME_STR = "NAME";
   static String ID_STR = "ID";

   Object get(int index) {
      if (index == -1) {
         return null;
      } else if (index == -2) {
         return "";
      } else if (index == -3) {
         return NAME_STR;
      } else if (index == -4) {
         return ID_STR;
      } else if (index <= 511) {
         String str = HTMLUtilities.resolveAttribute(index + 80 - 256);
         return str == null ? str : StringUtilities.toUpperCase(str, 1701707776);
      } else if (index <= 255) {
         String str = HTMLUtilities.resolveTag(index - 0);
         return str == null ? str : StringUtilities.toUpperCase(str, 1701707776);
      } else {
         return this._v.elementAt(index - 512);
      }
   }

   int append(String key) {
      if (key == null) {
         return -1;
      }

      if (key.length() == 0) {
         return -2;
      }

      if (key.equals(NAME_STR)) {
         return -3;
      }

      if (key.equals(ID_STR)) {
         return -4;
      }

      int i = this._h.get(key);
      if (i == -1) {
         i = HTMLUtilities.resolveAttribute(key);
         if (i != -1) {
            return i - 80 + 256;
         }

         i = HTMLUtilities.resolveTag(key);
         if (i != -1) {
            return i + 0;
         }

         i = this._index++;
         this._h.put(key, i);
         this._v.addElement(key);
      }

      return i;
   }

   int mapAttribId(int id) {
      switch (id) {
         case 129:
            return -4;
         case 142:
            return -3;
         default:
            return id + 256 - 80;
      }
   }

   int mapTagId(int id) {
      switch (id) {
         case 6:
            return 256;
         case 22:
            return 275;
         case 23:
            return 279;
         case 37:
            return 297;
         case 58:
            return 310;
         case 64:
            return 323;
         case 78:
            return 356;
         case 81:
            return 360;
         case 91:
            return 365;
         default:
            return id + 0;
      }
   }
}
