package net.rim.device.apps.internal.browser.wml;

import net.rim.device.apps.internal.browser.ui.Table;
import net.rim.device.apps.internal.browser.ui.TableCell;

final class WMLTable extends Table {
   private int _numColumns;
   private String _columnAlignment;

   WMLTable(byte borders, int numColumns, String columnAlignment) {
      super(borders);
      this._numColumns = numColumns;
      this._columnAlignment = columnAlignment;
   }

   final int getNumColumns() {
      return this._numColumns;
   }

   final TableCell getCornerCell() {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   final int getParagraphStyle(int columnNumber) {
      if (this._columnAlignment != null && columnNumber < this._columnAlignment.length()) {
         int rc = 0;
         switch (this._columnAlignment.charAt(columnNumber)) {
            case 'C':
               return 4;
            case 'L':
               return 6;
            case 'R':
               return 5;
            default:
               return 6;
         }
      } else {
         return 6;
      }
   }
}
