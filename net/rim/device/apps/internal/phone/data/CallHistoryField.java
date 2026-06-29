package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;

final class CallHistoryField extends PhoneListFieldManager {
   private CallLogItem _callLogItem;
   private long[] _callHistoryItems;
   private CallSummaryInfo _summaryInfo;
   static final int MIN_NUM_LINES = 2;
   private static final int ICON_SPACE = 2;
   private static final int NUMBER_TYPE_SPACE = 2;
   private static final int DATE_TIME_SPACE = 3;

   CallHistoryField(CallLogItem callLogItem, CallSummaryInfo summaryInfo, FocusChangeListener focusListener) {
      super(0, false);
      this._callLogItem = callLogItem;
      this._summaryInfo = summaryInfo;
      this._callHistoryItems = callLogItem.getHistoryItems();
      super._listField.setSize(this._callHistoryItems.length);
      super._listField.setFocusListener(focusListener);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void drawListRow(ListField listField, Graphics g, int index, int y, int width) {
      PhoneCallModelImpl log = this.getCallLog(index);
      if (log != null) {
         if (log.isUnopened()) {
            log.perform(5803508244060051872L, null);
         }

         int xOffset = 2;
         int iconWidth = log.paintIcon(g, xOffset, y);
         xOffset += iconWidth;
         xOffset += 2;
         if (log.hasNotes()) {
            boolean var14 = false /* VF: Semaphore variable */;

            label47:
            try {
               var14 = true;
               xOffset += log.paintIcon(g, xOffset, y, 14);
               var14 = false;
            } finally {
               if (var14) {
                  xOffset += iconWidth;
                  break label47;
               }
            }
         } else {
            xOffset += iconWidth;
         }

         int availableWidth = width;
         if (this._summaryInfo.getPhoneNumberCount() > 1) {
            availableWidth -= xOffset;
            String numberType = log.getNumberTypeString();
            if (numberType != null) {
               availableWidth -= 2;
               availableWidth -= g.drawText(numberType, xOffset, y, 5, availableWidth);
            }
         }

         xOffset += 3;
         availableWidth -= 6;
         Font font = g.getFont();
         StringBuffer dateTimeString = log.getDateTimeString(8);
         int dateTimeWidth = font.getAdvance(dateTimeString, 0, dateTimeString.length());
         if (dateTimeWidth >= availableWidth) {
            dateTimeString = log.getDateTimeString(48);
         }

         g.drawText(dateTimeString, 0, dateTimeString.length(), xOffset, y, 64, availableWidth);
      }
   }

   final PhoneCallModelImpl getCallLog(int index) {
      if (index >= 0 && index < this._callLogItem.getHistoryItemCount()) {
         int reverseOrderIndex = this._callHistoryItems.length - index - 1;
         return CallLogItem.lookUpCallLog(this._callHistoryItems[reverseOrderIndex]);
      } else {
         return null;
      }
   }

   final PhoneCallModelImpl getCurrentCallLog() {
      int[] selItems = super._listField.getSelection();
      return selItems != null && selItems.length == 1 ? this.getCallLog(selItems[0]) : null;
   }
}
