package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.reminders.ReminderManager;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.TimeChoiceField;
import net.rim.device.cldc.util.CalendarExtensions;

final class CalendarOptionsScreen extends SaveableMainScreenOptionsListItem implements FocusChangeListener {
   private ObjectChoiceField _startDOW;
   private DateField _start;
   private DateField _end;
   private TimeChoiceField _reminderByDefault;
   private TimeChoiceField _snooze;
   private ObjectChoiceField _deleteConfirm;
   private ObjectChoiceField _initialView;
   private ObjectChoiceField _keystrokesTriggerQuickInput;
   private ObjectChoiceField _keepAppointmentsDuration;
   private ObjectChoiceField _tasksInView;
   private BooleanChoiceField _useLegacyAgendaView;
   private BooleanChoiceField _showEndTime;
   private CalendarOptions _calendarOptions = CalendarOptions.getOptions();
   private Calendar _cal;
   private static TimeZone _gmtTZ = TimeZone.getTimeZone(DateTimeUtilities.GMT);
   private static final long THIRTY_MINUTES = 1800000L;
   private static final long FIFTEEN_MINUTES = 900000L;
   private static final long DEFAULT_DEFAULT_REMINDER_MILLIS = 900000L;
   private static Tag OPTIONS_SECTION_AREA_TAG = Tag.create("options-section-area");
   private static Tag OPTIONS_SECTION_HEADER_TAG = Tag.create("options-section-header");
   private static final CalendarOptionsScreen$ViewChoice[] VIEWS = new CalendarOptionsScreen$ViewChoice[]{
      new CalendarOptionsScreen$ViewChoice(CalendarApp._rb.getString(370), 0),
      new CalendarOptionsScreen$ViewChoice(CalendarApp._rb.getString(371), 1),
      new CalendarOptionsScreen$ViewChoice(CalendarApp._rb.getString(372), 2),
      new CalendarOptionsScreen$ViewChoice(CalendarApp._rb.getString(373), 3),
      new CalendarOptionsScreen$ViewChoice(CalendarApp._rb.getString(374), 4)
   };

   private CalendarOptionsScreen() {
      super(CalendarApp._rb.getString(661));
      this._cal = Calendar.getInstance(_gmtTZ);
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      this._startDOW = new ObjectChoiceField(CalendarApp._rb.getString(361), getDaysOfWeek(this._cal), this._calendarOptions.getFirstDayOfWeek() - 1);
      this._start = new DateField(CalendarApp._rb.getString(362), 0, 32);
      this._start.setMinuteIncrements(1800000);
      this._start.setTimeZone(_gmtTZ);
      this._start.setDate(this._calendarOptions.getDayStart());
      this._start.setFocusListener(this);
      this._end = new DateField(CalendarApp._rb.getString(363), 0, 32);
      this._end.setMinuteIncrements(1800000);
      this._end.setTimeZone(_gmtTZ);
      this._end.setDate(this._calendarOptions.getDayEnd());
      this._end.setFocusListener(this);
      this._reminderByDefault = new TimeChoiceField(CalendarApp._rb.getString(364), CalendarOptions.REMINDER_CHOICES, this._calendarOptions.getReminderMillis());
      ReminderManager reminderManager = ReminderManager.getInstance();
      if (reminderManager != null) {
         this._snooze = reminderManager.getSnoozeOptionsField(this._calendarOptions.getSnoozeMillis());
      }

      this._initialView = new ObjectChoiceField(CalendarApp._rb.getString(365), VIEWS, 0);
      int initialView = this._calendarOptions.getInitialView();

      for (int i = 0; i < VIEWS.length; i++) {
         if (VIEWS[i].getView() == initialView) {
            this._initialView.setSelectedIndex(i);
            break;
         }
      }

      this._deleteConfirm = new ObjectChoiceField(CommonResources.getString(2008), CommonResources.getYesNoArray(0));
      this._deleteConfirm.setSelectedIndex(this._calendarOptions.getConfirmDelete() ? 0 : 1);
      this._keystrokesTriggerQuickInput = new ObjectChoiceField(CalendarApp._rb.getString(366), CommonResources.getYesNoArray(0));
      this._keystrokesTriggerQuickInput.setSelectedIndex(this._calendarOptions.isQuickInputTriggeredByKeystrokes() ? 0 : 1);
      this._tasksInView = new ObjectChoiceField(CalendarApp._rb.getString(633), CommonResources.getYesNoArray(0));
      this._tasksInView.setSelectedIndex(this._calendarOptions.isTasksIntegratedIntoViews() ? 0 : 1);
      Manager section = this.createSection(CalendarApp._rb.getString(656), screen);
      section.add(this._startDOW);
      section.add(this._start);
      section.add(this._end);
      section = this.createSection(CommonResources.getString(9178), screen);
      section.add(this._initialView);
      this._useLegacyAgendaView = new BooleanChoiceField(CalendarApp._rb.getString(13), 0, !this._calendarOptions.useLegacyAgendaView());
      section.add(this._useLegacyAgendaView);
      this._showEndTime = new BooleanChoiceField(CalendarApp._rb.getString(646), 0, this._calendarOptions.showEndTime());
      section.add(this._showEndTime);
      section = this.createSection(CommonResources.getString(9179), screen);
      if (this._snooze != null) {
         section.add(this._snooze);
      }

      section.add(this._reminderByDefault);
      section.add(this._keystrokesTriggerQuickInput);
      int numDurations = CalendarOptions.KEEP_APPOINTMENTS_DURATION_CHOICES.length;
      String[] durations = new String[numDurations];
      short currentDuration = this._calendarOptions.getKeepAppointmentsDuration();
      int initialIndex = 0;
      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < numDurations; i++) {
         short duration = CalendarOptions.KEEP_APPOINTMENTS_DURATION_CHOICES[i];
         if (duration < 0) {
            durations[i] = CommonResources.getString(9145);
         } else {
            sb.setLength(0);
            sb.append(duration);
            sb.append(' ');
            sb.append(CommonResources.getString(9144));
            durations[i] = sb.toString();
         }

         if (duration == currentDuration) {
            initialIndex = i;
         }
      }

      this._keepAppointmentsDuration = new ObjectChoiceField(CalendarApp._rb.getString(624), durations, initialIndex);
      section.add(this._deleteConfirm);
      section.add(this._keepAppointmentsDuration);
      section.add(this._tasksInView);
   }

   protected final Manager createSection(String title, Manager parent) {
      Manager section = new VerticalFieldManager(1153484454560268288L);
      section.setTag(OPTIONS_SECTION_AREA_TAG);
      if (title != null) {
         LabelField titleField = new LabelField(title, 1152921504606846976L);
         titleField.setTag(OPTIONS_SECTION_HEADER_TAG);
         section.add(titleField);
      }

      section.add(new SeparatorField());
      parent.add(section);
      return section;
   }

   @Override
   protected final boolean save() {
      int sod = this.getHoursAndMinutesInMillis(this._start, true);
      int eod = this.getHoursAndMinutesInMillis(this._end, true);
      if (eod - sod < 3600000) {
         eod = sod + 3600000;
      }

      this._calendarOptions.setDayStartAndEnd(sod, eod);
      this._calendarOptions.setFirstDayOfWeek(this._startDOW.getSelectedIndex() + 1);
      this._calendarOptions.setReminderMillis(this._reminderByDefault.getSelectedTimeInMillis());
      if (this._snooze != null) {
         this._calendarOptions.setSnoozeMillis(this._snooze.getSelectedTimeInMillis());
      }

      this._calendarOptions.setConfirmDelete(this._deleteConfirm.getSelectedIndex() != 1);
      this._calendarOptions.setInitialView(VIEWS[this._initialView.getSelectedIndex()].getView());
      this._calendarOptions.setQuickInputTriggeredByKeystrokes(this._keystrokesTriggerQuickInput.getSelectedIndex() != 1);
      if (this._keepAppointmentsDuration != null) {
         short keepAppointmentsDuration = CalendarOptions.KEEP_APPOINTMENTS_DURATION_CHOICES[this._keepAppointmentsDuration.getSelectedIndex()];
         this._calendarOptions.setKeepAppointmentsDuration(keepAppointmentsDuration);
      }

      boolean integrateTasks = this._tasksInView.getSelectedIndex() != 1;
      this._calendarOptions.integrateTasksIntoViews(integrateTasks);
      if (this._useLegacyAgendaView != null) {
         boolean useLegacyAgendaView = this._useLegacyAgendaView.isAffirmative();
         this._calendarOptions.setUseLegacyAgendaView(!useLegacyAgendaView);
      }

      if (this._showEndTime != null) {
         this._calendarOptions.setShowEndTime(this._showEndTime.isAffirmative());
      }

      this._calendarOptions.commit();
      boolean returnValue = super.save();
      RIMGlobalMessagePoster.postGlobalEvent(5483692278053761660L, 0, 0, null, null);
      return returnValue;
   }

   @Override
   protected final boolean discard() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   private static final String[] getDaysOfWeek(Calendar cal) {
      CalendarExtensions calEx = (CalendarExtensions)cal;
      calEx.setTimeLong(0);
      calEx.add(5, 1 - cal.get(7));
      SimpleDateFormat dowFormatter = new SimpleDateFormat("E");
      String[] dows = new String[7];

      for (int i = 0; i < 7; i++) {
         dows[i] = dowFormatter.format(cal);
         calEx.add(5, 1);
      }

      return dows;
   }

   private final int getHoursAndMinutesInMillis(DateField field, boolean adjustToNearestHalfHour) {
      ((CalendarExtensions)this._cal).setTimeLong(field.getDate());
      int time = this._cal.get(11) * 3600000 + this._cal.get(12) * 60000;
      if (adjustToNearestHalfHour) {
         time = (int)((long)time + 900000);
         time = (int)((long)time / 1800000);
         time = (int)((long)time * 1800000);
      }

      if (field == this._end && time == 0) {
         time = 86400000;
      }

      return time;
   }

   private final void clampSODOrEOD(boolean clampSOD) {
      int sod = this.getHoursAndMinutesInMillis(this._start, false);
      int eod = this.getHoursAndMinutesInMillis(this._end, false);
      DateField fieldToClamp = null;
      int clampedTime = 0;
      if (eod - sod < 3600000) {
         if (clampSOD) {
            clampedTime = eod - 3600000;
            fieldToClamp = this._start;
         } else {
            clampedTime = sod + 3600000;
            fieldToClamp = this._end;
         }
      }

      if (fieldToClamp != null) {
         fieldToClamp.setDate(clampedTime);
         fieldToClamp.setDirty(true);
      }
   }

   public static final void showEditOptionsScreen() {
      CalendarOptionsScreen optionsScreen = new CalendarOptionsScreen();
      optionsScreen.perform(6099736323056465049L, null);
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      super.keyChar(key, status, time);
      if (key == ' ') {
         Field f = UiApplication.getUiApplication().getActiveScreen().getLeafFieldWithFocus();
         if (f instanceof ObjectListField) {
            ObjectListField olf = (ObjectListField)f;
            int i = olf.getSelectedIndex();
            String text = (String)olf.get(olf, i);
            Dialog.alert(text);
         }
      }

      return key == ' ' || key == 27;
   }

   @Override
   public final void focusChanged(Field field, int eventType) {
      if (eventType == 3) {
         if (field == this._start) {
            this.clampSODOrEOD(true);
            return;
         }

         if (field == this._end) {
            this.clampSODOrEOD(false);
         }
      }
   }
}
