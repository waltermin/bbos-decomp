package net.rim.device.apps.internal.explorer.file.util;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.explorer.file.FileItemField;
import net.rim.device.apps.internal.explorer.file.UpAliasFileItemField;

public final class FileItemComparator implements Comparator {
   private boolean _isByDate;
   private static FileItemComparator _byDateInstance = new FileItemComparator(true);
   private static FileItemComparator _byNameInstance = new FileItemComparator(false);

   public static final Comparator getInstance(int property) {
      return property == 0 ? _byDateInstance : _byNameInstance;
   }

   private FileItemComparator(boolean byDate) {
      this._isByDate = byDate;
   }

   @Override
   public final int compare(Object o1, Object o2) {
      FileItemField fl1 = (FileItemField)o1;
      FileItemField fl2 = (FileItemField)o2;
      boolean attrOnfl1 = fl1.isExecutableAlias();
      boolean attrOnfl2 = fl2.isExecutableAlias();
      if (attrOnfl1) {
         if (!attrOnfl2 || fl1 instanceof UpAliasFileItemField) {
            return -1;
         }
      } else if (attrOnfl2 && (!attrOnfl1 || fl2 instanceof UpAliasFileItemField)) {
         return 1;
      }

      attrOnfl1 = fl1.isDirectory();
      attrOnfl2 = fl2.isDirectory();
      if (attrOnfl1 && !attrOnfl2) {
         return -1;
      }

      if (attrOnfl2 && !attrOnfl1) {
         return 1;
      }

      int compareVal = 0;
      if (this._isByDate) {
         long diff = fl1.getTimestamp() - fl2.getTimestamp();
         if (diff == 0) {
            return StringUtilities.compareToIgnoreCase(fl1.getName(), fl2.getName());
         }

         compareVal = diff < 0 ? -1 : 1;
      } else {
         compareVal = StringUtilities.compareToIgnoreCase(fl1.getName(), fl2.getName());
      }

      return this._isByDate ? -compareVal : compareVal;
   }
}
