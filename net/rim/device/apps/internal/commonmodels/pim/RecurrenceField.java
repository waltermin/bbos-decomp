package net.rim.device.apps.internal.commonmodels.pim;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.Recur$Modifier;
import net.rim.device.apps.api.framework.model.Recur$RecurCapabilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.DOWField;
import net.rim.device.apps.api.utility.framework.RecurUtil;
import net.rim.device.cldc.util.CalendarExtensions;

public class RecurrenceField extends VerticalFieldManager implements FieldChangeListener, FocusChangeListener {
   private Calendar _cal = Calendar.getInstance();
   private Calendar _gmtcal;
   private Recur _recurCopy;
   private StringBuffer _recurDescString;
   private Recur$Modifier _sharedModifier;
   private long _startDate;
   private long _duration;
   private long _previousStartDate;
   private BitSet _sharedDOWS;
   private boolean _readOnly;
   private boolean _newEvent;
   private TimeZone _sharedTZ;
   private boolean _allDayEvent;
   private VerticalFieldManager _recurBlock;
   private ChoiceField _recurPattern;
   private RichTextField _recurDesc;
   private NumericChoiceField _recurPeriod;
   private VerticalFieldManager _recurEnd;
   private ChoiceField _recurRepeatModifier;
   private DateField _recurEndDate;
   private CheckboxField _recurModifier;
   private DOWField _recurDOW;
   private SeparatorField _sep1;
   private SeparatorField _sep2;
   private Recur$RecurCapabilities _capabilities;
   public static final long RECUR_TIMEZONE = 853966984731399007L;
   public static final long EVENT_DURATION = -6289930384059412695L;
   public static final long ALL_DAY_EVENT = -479237175265400126L;
   private static TimeZone _gmtTimeZone = TimeZone.getTimeZone(DateTimeUtilities.GMT);
   private static DateFormat _dateFormat = DateFormat.getInstance(40);

   public boolean validate() {
      return RecurUtil.getRecurValue(this._recurPattern.getSelectedIndex()) == 0 || !this.makeRecurrenceEndDateConsistent(true);
   }

   public Recur getRecurrenceInfo() {
      this._recurCopy.setRecurType(RecurUtil.getRecurValue(this._recurPattern.getSelectedIndex()));
      if (this._recurCopy.getRecurType() != 0) {
         this.getRecurrenceBlock(false);
      }

      return this._recurCopy;
   }

   public BitSet getSetDays() {
      return this._sharedDOWS;
   }

   public void setReadOnly(boolean flag) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setAllDayFlagState(boolean allDayEvent) {
      boolean changed = this._allDayEvent != allDayEvent;
      this._allDayEvent = allDayEvent;
      TimeZone tz = this.getTimeZone();
      if (changed) {
         long recurUntil = this.adjustForAllDay(this._recurEndDate.getDate());
         this._recurEndDate.setDate(recurUntil);
      }

      this._recurEndDate.setTimeZone(this._allDayEvent ? _gmtTimeZone : tz);
      this.getRecurrenceEndDate();
      this.updateRecurrenceDescription(true);
   }

   public void setNewStartDate(long newStartdate, long duration) {
      this._startDate = newStartdate;
      this._duration = duration;
      this.makeRecurrenceModifierConsistent();
      this._previousStartDate = this._startDate;
      this.makeRecurrenceEndDateConsistent(false);
      this.updateRecurrenceDescription(true);
   }

   public void setNewTimeZone(String timeZoneID) {
      this._sharedTZ = null;
      this._sharedTZ = TimeZone.getTimeZone(timeZoneID);
      this.makeRecurrenceModifierConsistent();
      this.makeRecurrenceEndDateConsistent(false);
      this.updateRecurrenceDescription(true);
   }

   public TimeZone getTimeZone() {
      if (this._sharedTZ == null) {
         this._sharedTZ = TimeZone.getDefault();
      }

      return this._sharedTZ;
   }

   @Override
   public void focusChanged(Field field, int eventType) {
      if (eventType == 3 && field == this._recurEndDate) {
         this.recurrenceEndDateChanged(true);
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if ((context & -2147483648) == 0) {
         if (field == this._recurPattern) {
            this.recurrencePatternChanged();
         } else if (field == this._recurPeriod) {
            this.recurrencePeriodChanged();
         } else if (field == this._recurRepeatModifier) {
            this.recurrenceRepeatModifierChanged();
         } else if (field == this._recurEndDate) {
            this.recurrenceEndDateChanged(false);
         } else if (field == this._recurDOW || field == this._recurModifier) {
            this.recurrenceModifierChanged();
         }

         this.fieldChangeNotify(context);
      }
   }

   private void updateRecurrenceDescription(boolean change) {
      this.getRecurrenceBlock(change);
      RecurUtil.getDesc(this._recurCopy, this._allDayEvent, this._recurDescString, this.getTimeZone());
      this._recurDescString.append('.');
      this._recurDesc.setText(this._recurDescString.toString());
   }

   private void populateAndAddRecurrenceBlock() {
      switch (this._recurCopy.getRecurType()) {
         case 0:
            this.populateAndAddNoRecurrence();
            break;
         case 1:
            this.populateAndAddDailyRecurrence();
            break;
         case 2:
            this.populateAndAddWeeklyRecurrence();
            break;
         case 3:
            this.populateAndAddMonthlyRecurrence();
            break;
         case 4:
         default:
            this.populateAndAddYearlyRecurrence();
      }

      if (!this._readOnly && (!this._capabilities.recurrencePatternEditDisabled || this._newEvent)) {
         this.add(this._recurBlock);
      }

      this.updateRecurrenceDescription(false);
      this.add(this._recurDesc);
   }

   private void populateAndAddNoRecurrence() {
   }

   private void populateAndAddDailyRecurrence() {
      this._recurPeriod = new NumericChoiceField(CommonResources.getString(9083), 1, 999, 1);
      this._recurCopy.setRecurPeriod(Math.min(this._recurCopy.getRecurPeriod(), 999));
      this._recurPeriod.setSelectedIndex(this._recurCopy.getRecurPeriod() - 1);
      this._recurBlock.add(this._recurPeriod);
      this.populateAndAddRecurrenceEndBlock();
   }

   private void populateAndAddWeeklyRecurrence() {
      this._recurPeriod = new NumericChoiceField(CommonResources.getString(9083), 1, 99, 1);
      this._recurCopy.setRecurPeriod(Math.min(this._recurCopy.getRecurPeriod(), 99));
      this._recurPeriod.setSelectedIndex(this._recurCopy.getRecurPeriod() - 1);
      this._recurBlock.add(this._recurPeriod);
      BitSet dows = this._sharedDOWS;
      Recur$Modifier mod = this._sharedModifier;
      Recur recur = this._recurCopy;
      dows.reset();
      int max = recur.numModifierValues(1);

      for (int i = 0; i < max; i++) {
         recur.getModifierAt(1, i, mod);
         dows.set(mod.parm1);
      }

      ((CalendarExtensions)this._cal).setTimeLong(this._startDate);
      this._cal.setTimeZone(this.getTimeZone());
      this.establishRequiredDOW(this._cal);
      this._recurDOW.setDays(dows);
      this._recurBlock.add(this._recurDOW);
      this.populateAndAddRecurrenceEndBlock();
   }

   private void populateAndAddMonthlyRecurrence() {
      this._recurPeriod = new NumericChoiceField(CommonResources.getString(9083), 1, 99, 1);
      this._recurCopy.setRecurPeriod(Math.min(this._recurCopy.getRecurPeriod(), 99));
      this._recurPeriod.setSelectedIndex(this._recurCopy.getRecurPeriod() - 1);
      this._recurBlock.add(this._recurPeriod);
      this._recurModifier.setChecked(RecurUtil.isRelative(this._recurCopy));
      this._recurBlock.add(this._recurModifier);
      this.populateAndAddRecurrenceEndBlock();
   }

   private void populateAndAddYearlyRecurrence() {
      this._recurCopy.setRecurPeriod(1);
      this._recurModifier.setChecked(RecurUtil.isRelative(this._recurCopy));
      this._recurBlock.add(this._recurModifier);
      this.populateAndAddRecurrenceEndBlock();
   }

   private void populateAndAddRecurrenceEndBlock() {
      this._recurEnd.deleteAll();
      if (this._capabilities.finiteRecurrencesOnly) {
         if (!this._recurCopy.isFinite()) {
            this._recurCopy.setEndDate(this._startDate + this._duration);
         }

         this._recurRepeatModifier.setSelectedIndex(0);
      } else {
         this._recurRepeatModifier.setSelectedIndex(RecurUtil.getEndChoice(this._recurCopy.isFinite()));
      }

      this._recurEnd.add(this._recurRepeatModifier);
      if (this.isFinite()) {
         this._recurEndDate.setDate(this._recurCopy.getEndDate());
         this._recurEnd.add(this._recurEndDate);
      }

      this._recurBlock.add(this._recurEnd);
   }

   RecurrenceField(Recur recurCopy, ContextObject context) {
      this._gmtcal = Calendar.getInstance(_gmtTimeZone);
      this._recurDescString = new StringBuffer();
      this._sharedModifier = new Recur$Modifier();
      this._sharedDOWS = new BitSet(7);
      this._recurCopy = recurCopy;
      this.setCookie(this._recurCopy);
      this.init(context);
   }

   private void getRecurrenceBlock(boolean change) {
      switch (this._recurCopy.getRecurType()) {
         case 1:
            this.getDailyRecurrence();
         case 0:
            return;
         case 2:
            this.getWeeklyRecurrence(change);
            return;
         case 3:
            this.getMonthlyRecurrence(change);
            return;
         case 4:
         default:
            this.getYearlyRecurrence(change);
      }
   }

   private void getYearlyRecurrence(boolean recurChange) {
      this.getDailyRecurrence();
      if (recurChange) {
         if (this._recurModifier.getChecked()) {
            TimeZone tz = this.getTimeZone();
            RecurUtil.makeRelativeYear(this._recurCopy, this._startDate, tz);
            return;
         }

         this._recurCopy.clearAllModifiers();
      }
   }

   private void getMonthlyRecurrence(boolean recurChange) {
      this.getDailyRecurrence();
      TimeZone tz = this.getTimeZone();
      if (recurChange) {
         if (this._recurModifier.getChecked()) {
            RecurUtil.makeRelativeMonth(this._recurCopy, this._startDate, tz);
            return;
         }

         this._recurCopy.clearAllModifiers();
         this._cal.setTimeZone(tz);
         ((CalendarExtensions)this._cal).setTimeLong(this._startDate);
         int dayOfMonth = this._cal.get(5);
         RecurUtil.setDayOfMonth(this._recurCopy, dayOfMonth);
      }
   }

   private void getWeeklyRecurrence(boolean recurChange) {
      this.getDailyRecurrence();
      if (recurChange) {
         BitSet dows = this._sharedDOWS;
         Recur$Modifier mod = this._sharedModifier;
         Recur recur = this._recurCopy;
         recur.clearAllModifiers();
         this._recurDOW.getDays(dows);

         for (int i = 0; i < 7; i++) {
            if (dows.isSet(i)) {
               mod.parm1 = i;
               mod.parm2 = 0;
               recur.addModificationValue(1, mod);
            }
         }
      }
   }

   private void getDailyRecurrence() {
      this._recurCopy.setRecurPeriod(this._recurPeriod.getSelectedIndex() + 1);
      this.getRecurrenceEndDate();
   }

   private long getRecurrenceEndDate() {
      if (!this._capabilities.finiteRecurrencesOnly) {
         this._recurCopy.setAsFinite(RecurUtil.getFiniteFromEndChoice(this._recurRepeatModifier.getSelectedIndex()));
      }

      if (!this.isFinite()) {
         return -1;
      }

      long recurEndDate = this._recurEndDate.getDate();
      if (!this._allDayEvent) {
         ((CalendarExtensions)this._cal).setTimeLong(this._startDate);
         int hour = this._cal.get(10);
         int minute = this._cal.get(12);
         int ampm = this._cal.get(9);
         ((CalendarExtensions)this._cal).setTimeLong(recurEndDate);
         DateTimeUtilities.zeroCalendarTime(this._cal);
         this._cal.set(10, hour);
         this._cal.set(12, minute);
         this._cal.set(9, ampm);
         recurEndDate = ((CalendarExtensions)this._cal).getTimeLong();
      } else {
         ((CalendarExtensions)this._gmtcal).setTimeLong(recurEndDate);
         DateTimeUtilities.zeroCalendarTime(this._gmtcal);
         recurEndDate = ((CalendarExtensions)this._gmtcal).getTimeLong();
      }

      this._recurCopy.setEndDate(recurEndDate);
      return recurEndDate;
   }

   private void establishRequiredDOW(Calendar cal) {
      BitSet dows = new BitSet(7);
      switch (cal.get(7)) {
         case 0:
            break;
         case 1:
         default:
            dows.set(0);
            break;
         case 2:
            dows.set(1);
            break;
         case 3:
            dows.set(2);
            break;
         case 4:
            dows.set(3);
            break;
         case 5:
            dows.set(4);
            break;
         case 6:
            dows.set(5);
            break;
         case 7:
            dows.set(6);
      }

      this._recurDOW.setRequiredDays(dows);
   }

   private boolean isFinite() {
      return this._recurCopy.isFinite() || this._capabilities.finiteRecurrencesOnly;
   }

   private void recurrencePeriodChanged() {
      this._recurCopy.setRecurPeriod(this._recurPeriod.getSelectedIndex() + 1);
      this.updateRecurrenceDescription(true);
   }

   private boolean makeRecurrenceEndDateConsistent(boolean validate) {
      if (this.isFinite()) {
         long recurEndDate = this._recurEndDate.getDate();
         long startDate = this._allDayEvent ? this.adjustForAllDay(this._startDate) : this._startDate;
         if (startDate > recurEndDate) {
            if (validate && this._recurCopy.getRecurType() != 0 && !DateTimeUtilities.isSameDate(startDate, recurEndDate, this.getTimeZone(), null)) {
               Status.show(CommonResources.getString(9096));
            }

            long endDate = startDate + this._duration;
            this._recurEndDate.setDate(endDate);
            this._recurCopy.setEndDate(endDate);
            return true;
         }
      }

      return false;
   }

   private boolean makeRecurrenceModifierConsistent() {
      if (this._recurCopy.getRecurType() == 2) {
         Calendar cal = this._cal;
         cal.setTimeZone(this._sharedTZ);
         ((CalendarExtensions)cal).setTimeLong(this._previousStartDate);
         int prevYear = cal.get(1);
         int prevMonth = cal.get(2);
         int prevDay = cal.get(5);
         ((CalendarExtensions)cal).setTimeLong(this._startDate);
         if (prevDay == cal.get(5) && prevMonth == cal.get(2) && prevYear == cal.get(1)) {
            return false;
         }

         this.establishRequiredDOW(cal);
         this._sharedDOWS.reset();
         this._recurDOW.setDays(this._sharedDOWS);
         this._recurDOW.setDirty(true);
         this._recurDOW.setMuddy(true);
         return true;
      } else {
         return false;
      }
   }

   private void init(ContextObject context) {
      this._capabilities = RecurUtil.getRecurrenceCapabilities();
      String newTimeZone = null;
      if (context != null) {
         Object obj = ContextObject.get(context, 4143325197084129318L);
         if (obj instanceof Long) {
            Long temp = (Long)obj;
            this._startDate = temp;
         }

         obj = ContextObject.get(context, 853966984731399007L);
         if (obj instanceof String) {
            newTimeZone = (String)obj;
         }

         obj = ContextObject.get(context, -6289930384059412695L);
         if (obj instanceof Long) {
            Long temp = (Long)obj;
            this._duration = temp;
         }

         obj = ContextObject.get(context, -479237175265400126L);
         if (obj instanceof Boolean) {
            Boolean temp = (Boolean)obj;
            this._allDayEvent = temp;
         }

         if (context.getFlag(31)) {
            this._newEvent = true;
         }

         if (context.getFlag(85)) {
            this.setReadOnly(true);
         }
      }

      if (newTimeZone != null) {
         this._sharedTZ = TimeZone.getTimeZone(newTimeZone);
      }

      if (this._startDate == 0) {
         this._startDate = System.currentTimeMillis();
      }

      this._previousStartDate = this._startDate;
      this._recurPattern = new ObjectChoiceField(CommonResources.getString(9075), RecurUtil.makeChoiceArray());
      this._recurPattern.setChangeListener(this);
      this._recurBlock = new VerticalFieldManager(1152921504606846976L);
      this._recurDesc = new RichTextField(null, 36028797018963968L);
      this._recurPeriod = new NumericChoiceField(CommonResources.getString(9083), 1, 999, 1);
      this._recurPeriod.setSelectedIndex(this._recurCopy.getRecurPeriod() - 1);
      this._recurPeriod.setChangeListener(this);
      this._recurEnd = new VerticalFieldManager(1152921504606846976L);
      this._recurRepeatModifier = new ObjectChoiceField(
         CommonResources.getString(9087), RecurUtil.makeEndChoiceArray(!this._capabilities.finiteRecurrencesOnly)
      );
      if (!this._capabilities.finiteRecurrencesOnly) {
         this._recurRepeatModifier.setSelectedIndex(RecurUtil.getEndChoice(this._recurCopy.isFinite()));
      } else {
         this._recurRepeatModifier.setSelectedIndex(0);
      }

      this._recurRepeatModifier.setChangeListener(this);
      this._recurDOW = new DOWField(CommonResources.getString(9093), null);
      this._recurDOW.setFirstDayOfWeek(this._recurCopy.getFirstDayOfWeek());
      this._recurDOW.setChangeListener(this);
      this._recurModifier = new CheckboxField(CommonResources.getString(9077), false);
      this._recurModifier.setChangeListener(this);
      this._recurEndDate = new DateField(null, this._recurCopy.isFinite() ? this._recurCopy.getEndDate() : this._startDate, _dateFormat);
      if (newTimeZone != null) {
         this._recurEndDate.setTimeZone(TimeZone.getTimeZone(newTimeZone));
      }

      this._recurEndDate.setChangeListener(this);
      this._recurEndDate.setFocusListener(this);
      byte recurrenceType = this._recurCopy.getRecurType();
      this._sep1 = new SeparatorField();
      this._sep2 = new SeparatorField();
      this._recurPattern.setSelectedIndex(RecurUtil.getRecurOffset(recurrenceType));
      this.add(this._sep1);
      this.add(this._recurPattern);
      this.populateAndAddRecurrenceBlock();
      this.add(this._sep2);
      this.makeRecurrenceModifierConsistent();
      if ((this._capabilities.recurrencePatternEditDisabled || this._readOnly) && !this._newEvent) {
         this._recurPattern.setEditable(false);
         this._recurPeriod.setEditable(false);
         this._recurRepeatModifier.setEditable(false);
         this._recurDOW.setEditable(false);
         this._recurModifier.setEditable(false);
         this._recurEndDate.setEditable(false);
      }
   }

   private void recurrenceModifierChanged() {
      this._recurCopy.setRecurType(RecurUtil.getRecurValue(this._recurPattern.getSelectedIndex()));
      this.updateRecurrenceDescription(true);
   }

   private long adjustForAllDay(long date) {
      TimeZone tz = this.getTimeZone();
      this._cal.setTimeZone(tz);
      if (this._allDayEvent) {
         ((CalendarExtensions)this._cal).setTimeLong(date);
         return DateTimeUtilities.copyCalendar(this._cal, this._gmtcal);
      } else {
         ((CalendarExtensions)this._gmtcal).setTimeLong(date);
         return DateTimeUtilities.copyCalendar(this._gmtcal, this._cal);
      }
   }

   private void recurrencePatternChanged() {
      this._recurCopy.setRecurType(RecurUtil.getRecurValue(this._recurPattern.getSelectedIndex()));
      this._recurCopy.clearAllModifiers();
      this._recurBlock.deleteAll();
      switch (this._recurCopy.getRecurType()) {
         case 0:
            this.populateAndAddNoRecurrence();
            break;
         case 1:
            this.populateAndAddDailyRecurrence();
            break;
         case 2:
            ((CalendarExtensions)this._cal).setTimeLong(this._startDate);
            this._cal.setTimeZone(this.getTimeZone());
            this._sharedModifier.parm1 = this._cal.get(7) - 1;
            this._sharedModifier.parm2 = 0;
            this._recurCopy.addModificationValue(1, this._sharedModifier);
            this.populateAndAddWeeklyRecurrence();
            break;
         case 3:
            this.populateAndAddMonthlyRecurrence();
            break;
         case 4:
         default:
            this.populateAndAddYearlyRecurrence();
      }

      this.updateRecurrenceDescription(true);
   }

   private void recurrenceRepeatModifierChanged() {
      if (this._recurEnd.getFieldCount() > 1) {
         this._recurEnd.deleteRange(1, 1);
      }

      if (!this._capabilities.finiteRecurrencesOnly) {
         this._recurCopy.setAsFinite(RecurUtil.getFiniteFromEndChoice(this._recurRepeatModifier.getSelectedIndex()));
      }

      if (this._recurCopy.isFinite()) {
         long startDate = this._allDayEvent ? this.adjustForAllDay(this._startDate) : this._startDate;
         this._recurCopy.setEndDate(startDate + this._duration);
         this._recurEndDate.setDate(this._recurCopy.getEndDate());
         this._recurEnd.add(this._recurEndDate);
         this.makeRecurrenceEndDateConsistent(true);
      }

      this.updateRecurrenceDescription(true);
   }

   private void recurrenceEndDateChanged(boolean validate) {
      this.getRecurrenceEndDate();
      if (validate && this.makeRecurrenceEndDateConsistent(validate)) {
         this.updateRecurrenceDescription(true);
      } else {
         this.updateRecurrenceDescription(false);
      }
   }

   RecurrenceField() {
      this._gmtcal = Calendar.getInstance(_gmtTimeZone);
      this._recurDescString = new StringBuffer();
      this._sharedModifier = new Recur$Modifier();
      this._sharedDOWS = new BitSet(7);
      this._recurCopy = new RecurImpl();
      this.setCookie(this._recurCopy);
      this.init(null);
   }

   RecurrenceField(long style, Recur recurCopy, ContextObject context) {
      super(style);
      this._gmtcal = Calendar.getInstance(_gmtTimeZone);
      this._recurDescString = new StringBuffer();
      this._sharedModifier = new Recur$Modifier();
      this._sharedDOWS = new BitSet(7);
      this._recurCopy = recurCopy;
      this.setCookie(this._recurCopy);
      this.init(context);
   }
}
