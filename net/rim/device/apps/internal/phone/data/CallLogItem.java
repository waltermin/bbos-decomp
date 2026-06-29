package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.i18n.DateFormatSymbols;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.vm.Array;

public final class CallLogItem implements PhoneListItem, EncryptableProvider {
   private PhoneCallModelImpl _callLog;
   private long[] _historyItems = new long[0];
   private static final int MIN_HISTORY_ITEMS_FOR_DELETE_STATUS;
   private static final int ICON_SPACE;
   private static final int TIME_STRING_SPACE;
   private static final int NUMBER_TYPE_STRING_SPACE;
   private static final int CALLER_ID_STRING_LEFT_SPACE;
   private static final int CALLER_ID_STRING_RIGHT_SPACE;
   private static int _timeStringAllotment = 60;
   private static int _numberTypeStringAllotment = 30;

   final void setCallLog(PhoneCallModelImpl callLog) {
      if (callLog == null) {
         throw new Object("CLI.setCallLog() => callLog == null");
      }

      this._callLog = callLog;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Verb defaultVerb = this._callLog.getVerbs(context, verbs);
      if (!PhoneUtilities.getPrivateFlag(context, 76)) {
         PhoneUtilities.appendVerb(verbs, new CallLogItem$ViewCallHistoryVerb(this));
      }

      return defaultVerb;
   }

   final void onDelete() {
      for (int i = this._historyItems.length - 1; i >= 0; i--) {
         PhoneCallModelImpl callLog = lookUpCallLog(this._historyItems[i]);
         if (callLog != null) {
            PhoneFolders.removeItem(callLog, false);
         }
      }
   }

   final void addCallLog(PhoneCallModelImpl log) {
      this.addCallLog(log, false);
   }

   final boolean addCallLog(PhoneCallModelImpl log, boolean initialization) {
      if (initialization) {
         if (log.getTimeStamp() > this._callLog.getTimeStamp()) {
            this.setCallLog(log);
         }
      } else {
         this.setCallLog(log);
      }

      return this.addToHistory(log.getTimeStamp());
   }

   final void callLogRemoved(PhoneCallModelImpl callLog) {
      this.removeFromHistory(callLog.getTimeStamp());
      if (this.getHistoryItemCount() > 0 && callLog == this._callLog) {
         PhoneCallModelImpl newLog = lookUpCallLog(this._historyItems[this._historyItems.length - 1]);
         if (newLog == null) {
            Array.resize(this._historyItems, 0);
            System.out.println("CLI Purge due to bad state.");
            return;
         }

         this._callLog = newLog;
      }
   }

   final void mergeCallLogs(CallLogItem cli) {
      long[] history = cli.getHistoryItems();

      for (int i = history.length - 1; i >= 0; i--) {
         this.addToHistory(history[i]);
      }
   }

   final int getHistoryItemCount() {
      return this._historyItems.length;
   }

   final long[] getHistoryItems() {
      return this._historyItems;
   }

   public final PhoneCallModelImpl getCallLog() {
      return this._callLog;
   }

   final void addressBookUpdated(int updateType, Object o) {
      if (this._callLog != null) {
         this._callLog.addressBookUpdated(updateType, o);
      }
   }

   final boolean doCallLogRefSanityCheck() {
      for (int i = this.getHistoryItemCount() - 1; i >= 0; i--) {
         if (lookUpCallLog(this._historyItems[i]) == null) {
            return false;
         }
      }

      return true;
   }

   final void clearCallLog() {
      this._callLog = null;
   }

   @Override
   public final boolean isLongRunningDelete() {
      return this.getHistoryItemCount() >= 4;
   }

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context, PhoneListView phoneListView, int index) {
      PhoneCallModelImpl callLog = this.getCallLog();
      if (callLog == null) {
         phoneListView.onPaintAborted(this, index);
         return 0;
      }

      CallLogView clv = (CallLogView)phoneListView;
      String callerIDString = clv.getCallerIDString(callLog, index);
      y = VariableRowHeightProxy.getAdjustedY(context, g.getFont(), callerIDString, y);
      int xOffset = x + 3;
      xOffset += callLog.paintIcon(g, xOffset, y);
      String timeString = clv.getDateTimeString(callLog, index);
      int timeStringOffset = x + width - 2 - _timeStringAllotment;
      g.drawText(timeString, timeStringOffset, y, 5, _timeStringAllotment);
      String numberTypeString = clv.getNumberTypeString(callLog, index);
      int numberTypeStringOffset = timeStringOffset;
      if (!PhoneUtilities.isEmptyString(numberTypeString)) {
         int var22 = numberTypeStringOffset - 2;
         numberTypeStringOffset = var22 - _numberTypeStringAllotment;
         g.drawText(numberTypeString, numberTypeStringOffset, y, 4, _numberTypeStringAllotment);
      }

      xOffset += 3;
      int rightX = numberTypeStringOffset - 4;
      int widthRemaining = rightX - xOffset;
      g.drawText(callerIDString, xOffset, y, 64, widthRemaining);
      return width;
   }

   @Override
   public final CallerIDInfo getCallerIDInfo() {
      return this._callLog.getCallerIDInfo();
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return this._callLog.checkCrypt(compress, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      Object newPayload = this._callLog.reCrypt(compress, encrypt);
      if (newPayload != null) {
         this._callLog = (PhoneCallModelImpl)newPayload;
      }

      return null;
   }

   @Override
   public final Field getHintField() {
      CallerIDInfo cidi = this._callLog.getCallerIDInfo();
      String name = cidi.toString();
      String number = null;
      if (cidi.getAddress() != null) {
         Object num = cidi.getNumber();
         if (num != null) {
            number = num.toString();
         }
      }

      StringBuffer dateTime = this._callLog.getDateTimeString(2);
      StringBuffer duration = this._callLog.getDurationString(false);
      HorizontalFieldManager hfm1 = (HorizontalFieldManager)(new Object());
      hfm1.add((Field)(new Object(name, 64)));
      if (number != null) {
         hfm1.add((Field)(new Object(number, 69)));
      }

      HorizontalFieldManager hfm2 = (HorizontalFieldManager)(new Object());
      hfm2.add((Field)(new Object(dateTime)));
      hfm2.add((Field)(new Object(duration, 5)));
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object());
      vfm.add(hfm1);
      vfm.add(hfm2);
      return vfm;
   }

   @Override
   public final char getSpeedDialKey() {
      Object number = this.getCallerIDInfo().getNumber();
      return QuickContactList.getInstance().getQuickContactKey(number);
   }

   @Override
   public final boolean canSpeedDial() {
      CallerIDInfo cidi = this._callLog.getCallerIDInfo();
      if (!cidi.isSpecial() && !cidi.isUnknownNumber() && !cidi.isPrivateNumber()) {
         Object num = cidi.getNumber();
         return !(num instanceof Object) ? false : ((AbstractPhoneNumberModel)num).canSpeedDial();
      } else {
         return false;
      }
   }

   private final boolean addToHistory(long timestamp) {
      if (this._historyItems.length != 0 && timestamp <= this._historyItems[this._historyItems.length - 1]) {
         for (int i = this._historyItems.length - 1; i >= 0; i--) {
            if (timestamp > this._historyItems[i]) {
               insertItemAt(this._historyItems, timestamp, i + 1);
               return false;
            }
         }

         insertItemAt(this._historyItems, timestamp, 0);
         return false;
      } else {
         Array.resize(this._historyItems, this._historyItems.length + 1);
         this._historyItems[this._historyItems.length - 1] = timestamp;
         return true;
      }
   }

   CallLogItem(PhoneCallModelImpl callLog) {
      this.setCallLog(callLog);
      this.addToHistory(callLog.getTimeStamp());
   }

   private static final void insertItemAt(long[] array, long item, int index) {
      int length = array.length;
      Array.resize(array, length + 1);
      System.arraycopy(array, index, array, index + 1, length - index);
      array[index] = item;
   }

   CallLogItem() {
   }

   @Override
   public final String toString() {
      CallerIDInfo cidi = this._callLog.getCallerIDInfo();
      return cidi != null ? cidi.getDisplayString() : null;
   }

   private static final void removeItemAt(long[] array, int index) {
      int newLength = array.length - 1;
      System.arraycopy(array, index + 1, array, index, newLength - index);
      Array.resize(array, newLength);
   }

   @Override
   public final boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (o instanceof CallLogItem) {
         CallLogItem cli = (CallLogItem)o;
         PhoneCallModelImpl thisLog = this.getCallLog();
         PhoneCallModelImpl thatLog = cli.getCallLog();
         if (thisLog != null && thatLog != null) {
            return thisLog.equals(thatLog);
         }
      }

      return false;
   }

   static final PhoneCallModelImpl lookUpCallLog(long timestamp) {
      int refId = (int)timestamp;
      Object o = MessageLookups.get(-7579072715623987642L, refId);
      return !(o instanceof PhoneCallModelImpl) ? null : (PhoneCallModelImpl)o;
   }

   private final void removeFromHistory(long timestamp) {
      for (int i = 0; i < this._historyItems.length; i++) {
         if (this._historyItems[i] == timestamp) {
            removeItemAt(this._historyItems, i);
            return;
         }
      }
   }

   public static final void calculateAllotments(Font f) {
      char[] largest = new char[10];
      char largestFromOneToNine = '1';
      char c = '0';
      largest[0] = c;
      int curValue = f.getBounds(c);
      int maxValue = curValue;
      int maxValueFromOneToNine = 0;
      int maxAMPMLength = 0;
      c++;

      for (int i = 1; i <= 9; c++) {
         curValue = f.getBounds(c);
         if (curValue > maxValue) {
            largest[i] = c;
            maxValue = curValue;
         } else {
            largest[i] = largest[i - 1];
         }

         if (curValue > maxValueFromOneToNine) {
            largestFromOneToNine = c;
            maxValueFromOneToNine = curValue;
         }

         i++;
      }

      String[] ampmStrings = DateFormatSymbols.getInstance().getShortAmPmStrings();

      for (int i = ampmStrings.length - 1; i >= 0; i--) {
         curValue = f.getBounds(ampmStrings[i]);
         if (curValue > maxAMPMLength) {
            maxAMPMLength = curValue;
         }
      }

      StringBuffer testString = (StringBuffer)(new Object());
      testString.append(largest[1]);
      testString.append(largest[9]);
      testString.append('/');
      testString.append(largest[3]);
      testString.append(largest[9]);
      maxValue = f.getBounds(testString);
      testString.setLength(0);
      testString.append(largestFromOneToNine);
      testString.append(':');
      testString.append(largest[5]);
      testString.append(largest[9]);
      curValue = f.getBounds(testString);
      curValue += maxAMPMLength;
      if (curValue > maxValue) {
         maxValue = curValue;
      }

      testString.setLength(0);
      testString.append('1');
      testString.append(largest[2]);
      testString.append(':');
      testString.append(largest[5]);
      testString.append(largest[9]);
      curValue = f.getBounds(testString);
      curValue += maxAMPMLength;
      if (curValue > maxValue) {
         maxValue = curValue;
      }

      testString.setLength(0);
      testString.append(largest[2]);
      testString.append(largest[9]);
      testString.append(':');
      testString.append(largest[5]);
      testString.append(largest[9]);
      curValue = f.getBounds(testString);
      if (curValue > maxValue) {
         maxValue = curValue;
      }

      _timeStringAllotment = maxValue;
      ResourceBundle resources = ResourceBundle.getBundle(2699923441625099942L, "net.rim.device.apps.internal.resource.Phone");
      String[] numberTypes = resources.getStringArray(6079);
      maxValue = 0;

      for (int i = numberTypes.length - 1; i >= 0; i--) {
         curValue = f.getBounds(numberTypes[i]);
         if (curValue > maxValue) {
            maxValue = curValue;
         }
      }

      _numberTypeStringAllotment = maxValue;
   }
}
