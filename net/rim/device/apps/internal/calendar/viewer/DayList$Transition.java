package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import java.util.Date;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MultiServiceEvent;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.cldc.util.CalendarExtensions;

public final class DayList$Transition implements EncryptableProvider {
   int _sequenceNumber;
   byte _transitionType;
   Object _summaryText;
   long _timeInMillis;
   String _displayTime;
   Object _calElement;
   char[] _barString;
   int[] _barColours;
   char[] _extraLinesBarString;
   int[] _extraLinesBarColours;
   boolean _subsequentDayOfSpanningEvent = false;
   long _endTimeInMillis;
   int _attributes;
   int _colour;
   private String _formattedSummaryText;
   static final byte END_MASK;
   static final byte TRANSITION_MASK;
   static final byte NO_TRANSITION;
   static final byte SEPARATOR;
   static final byte ALL_DAY;
   static final byte FREE_TIME_SEPARATOR;
   static final byte BEGINNING;
   static final byte MIDDLE_BEGINNING;
   static final byte MIDDLE_END;
   static final byte END;
   static final byte BEGINNING_AND_END;
   static final byte IN_THE_PAST;
   static final byte CONFLICTS;

   protected DayList$Transition() {
   }

   final String getSummaryText(boolean viewSupportsAdvancedThemeing) {
      if (this._formattedSummaryText == null) {
         String summaryText = PersistentContent.decodeString(this._summaryText);
         if (viewSupportsAdvancedThemeing
            && (CalendarOptions.getOptions().showEndTime() || this.conflicts())
            && !this._subsequentDayOfSpanningEvent
            && this._endTimeInMillis != 0
            && this._transitionType != 2) {
            Calendar endDate = Calendar.getInstance();
            Calendar startDate = Calendar.getInstance();
            endDate.setTime((Date)(new Object(this._endTimeInMillis)));
            startDate.setTime((Date)(new Object(this._timeInMillis)));
            String dateString = TimeStringCache.getString(endDate);
            ((CalendarExtensions)startDate).add(5, 1);
            if (startDate.getTime().getTime() < endDate.getTime().getTime()) {
               DateFormat df = DateFormat.getInstance(48);
               dateString = ((StringBuffer)(new Object())).append(dateString).append('(').append(df.format(endDate)).append(')').toString();
            }

            summaryText = ((StringBuffer)(new Object("- "))).append(dateString).append(' ').append(summaryText).toString();
         }

         this._formattedSummaryText = summaryText;
      }

      return this._formattedSummaryText;
   }

   final void setSummaryText(Object text) {
      this._formattedSummaryText = null;
      this._summaryText = text;
   }

   final boolean isInThePast() {
      return (this._attributes & 1) > 0;
   }

   final boolean conflicts() {
      return (this._attributes & 2) > 0;
   }

   final void setAttribute(byte attributeMask, boolean value) {
      int i = 0;
      if (value) {
         i = attributeMask;
      }

      this._attributes = this._attributes & ~attributeMask | i;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._summaryText, compress, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._formattedSummaryText = null;
      this._summaryText = PersistentContent.reEncode(this._summaryText, compress, encrypt);
      return this;
   }

   private final void setTransitionData(byte type, long time, int sequenceNumber, long endTimeInMillis, Object calElement, String summaryText) {
      this._transitionType = type;
      this._timeInMillis = time;
      this._sequenceNumber = sequenceNumber;
      this._displayTime = null;
      this.setSummaryText(summaryText);
      this._calElement = calElement;
      this._barString = null;
      this._barColours = null;
      this._extraLinesBarString = null;
      this._extraLinesBarColours = null;
      this._subsequentDayOfSpanningEvent = false;
      this._attributes = 0;
      this._endTimeInMillis = endTimeInMillis;
      this._colour = -1;
      if (calElement != null && summaryText != null && calElement instanceof Object) {
         this._colour = ((MultiServiceEvent)calElement).getColour();
      }
   }
}
