package net.rim.device.apps.internal.calendar.eventprovider;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.TimeZone;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ActiveAutoTextEditField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressReferenceViewField;
import net.rim.device.apps.api.addressbook.AddressReferenceViewField$AddressReferenceViewDataField;
import net.rim.device.apps.api.calendar.calconstants.CalOptionCache;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarFolder;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarProxy;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.controller.CalendarEventViewer;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.AttendeeFactory;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfo;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.calendar.ota.OTACalendarSyncDataManager;
import net.rim.device.apps.api.framework.hotkeys.HotKeyCheck;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.Recur$Handle;
import net.rim.device.apps.api.framework.model.ResolvedStatusProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.DefaultVerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.apps.api.sync.OTASyncData;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.DurationField;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.FieldUtility;
import net.rim.device.apps.api.utility.framework.RecurUtil;
import net.rim.device.apps.internal.addressbook.lookup.ALPConfiguration;
import net.rim.device.apps.internal.blackberryemail.email.CMIMEReferenceIdProvider;
import net.rim.device.apps.internal.calendar.meeting.AttendeeModel;
import net.rim.device.apps.internal.calendar.meeting.MeetingUtilities;
import net.rim.device.apps.internal.commonmodels.pim.RecurImpl;
import net.rim.device.apps.internal.commonmodels.pim.RecurrenceField;
import net.rim.device.apps.internal.messaging.MessageHotkeys;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.cldc.util.TimeService;
import net.rim.vm.Array;

public class EventViewer
   extends AppsMainScreen
   implements FieldChangeListener,
   CalendarEventViewer,
   GlobalEventListener,
   FocusChangeListener,
   CollectionListener {
   private DateFormat _dateAndTimeFormat = DateFormat.getInstance(46);
   private DateFormat _dateFormat = DateFormat.getInstance(40);
   private LabelField _titleField;
   private ObjectChoiceField _sendUsingField;
   private boolean _sendUsingFieldVisible = false;
   private ActiveAutoTextEditField _subject;
   private ActiveAutoTextEditField _location;
   private CheckboxField _allDayFlag;
   private ChoiceField _showTimeAs;
   private CheckboxField _pencilIn;
   private Field _freeBusyField;
   private SeparatorField _sep2;
   private VerticalFieldManager _timeBlock;
   private int _currentHour;
   private int _currentMinute;
   private Calendar _cal = Calendar.getInstance();
   private Calendar _gmtCal = Calendar.getInstance(TimeZone.getTimeZone(DateTimeUtilities.GMT));
   private DateField _start;
   private DateField _end;
   private DurationField _duration;
   private ChoiceField _timeZone;
   private VerticalFieldManager _debugBlock;
   boolean _debugActive = false;
   private LabelField _debugRefIDField;
   private LabelField _debugLongIDField;
   private LabelField _debugiCalIDField;
   private LabelField _debugParentIDField;
   private LabelField _debugRelatedTimeField;
   private LabelField _debugTimeZoneField;
   private LabelField _debugGmtStartDate;
   private LabelField _debugGMESendStatus;
   private LabelField _debugSeqRevPair;
   private LabelField _debugCalSrvIDField;
   private LabelField _debugCalSrvDefaultIDField;
   private LabelField _debugCalSrvDefaultFldIDField;
   private LabelField _debugCalSrvBaseIDField;
   private LabelField _debugCalDBIDField;
   private LabelField _debugCalDBFldIDField;
   private LabelField _debugCalUIDField;
   private LabelField _debugCalUserIDField;
   private LabelField _debugCalDSIDField;
   private CheckboxField _sensitivityField;
   private VerticalFieldManager _conflictsBlock;
   private RichTextField _conflictsField;
   private RichTextField _meetingOutdatedField;
   private RichTextField _meetingMissingField;
   private RecurrenceField _recurBlock;
   private Field _reminderField;
   private ReminderModel _reminderModel;
   private ActiveAutoTextEditField _notes;
   private Event _eventData;
   private Field[] _externalFields1;
   private RIMModel[] _externalModels1;
   private Manager _meetingInfoField;
   private Field[] _externalFields2;
   private RIMModel[] _externalModels2;
   private boolean _allowRecur;
   private long _maxDurationFromPattern;
   private RecurImpl _recurCopy;
   private TimeZone _sharedTZ;
   private Verb[] _verbs;
   private Verb _saveVerb;
   private Verb _deleteMessageVerb;
   private Verb _defaultVerb;
   private boolean _timeBlockCurrent;
   private Object _context;
   private int[] _calFields = new int[7];
   private long _previousStartTime;
   private UiApplication _uiApp;
   private byte _concurrentModification = 0;
   private boolean _newEvent;
   private boolean _wasAllDay;
   private boolean _meetingOutdated;
   private boolean _viewingAppointmentStatus;
   private boolean _eventNotFound;
   private Recur$Handle _recurHandle = (Recur$Handle)(new Object());
   private OTASyncData _syncData;
   private EventViewer$ConflictCheckingThread _conflictCheckingThread;
   private CalendarService _calendarService;
   private CalendarFolder _calendarFolder;
   private CalendarServiceManager _calendarServiceManager;
   private boolean _attendeesChanged;
   private EventViewer$UpdateLookupFields _updateLookupFields = new EventViewer$UpdateLookupFields(this, null);
   private static final int MAX_RECURRENCE_PERIOD = 999;
   private static final int MAX_NOTES_CHARS = 4096;
   private static final long FIFTEEN_MINUTES = 900000L;
   private static final long SCREEN_UPDATE_INTERVAL = 2000L;
   private static final long CONFLICT_MESSAGE_DELAY = 5000L;
   private static final Tag SECTION_AREA_TAG = Tag.create("calendar-appointment-section-area");
   private static final Tag SUBJECT_TEXT_TAG = Tag.create("calendar-appointment-subject-text");
   private static final Tag WARNING_AREA_TAG = Tag.create("calendar-appointment-warning-area");
   private static final Tag EVENT_VIEWER_SCREEN_TAG = Tag.create("event-viewer-screen");
   private static ResourceBundle _rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
   private static TimeZone _gmtTZ = TimeZone.getTimeZone(DateTimeUtilities.GMT);
   private static final int ALL_DAY_DURATION_ADJUSTMENT = 86400000;

   public String getViewerSubject() {
      return this._subject.getText().trim();
   }

   public CICALConfiguration getCICALConfiguration() {
      return this._calendarService.getCICALConfiguration();
   }

   public boolean isAllDayChecked() {
      return this._allDayFlag.getChecked();
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 1202366544244619460L) {
         if (object0 == this._eventData) {
            this.closeViewer(false);
            return;
         }
      } else if (guid != -7131874474196788121L) {
         if (guid == 6345609069135580235L && PersistentContent.isEncryptionEnabled()) {
            this.refreshContentProtectedFields();
            if (this._saveVerb != null) {
               this.setDirty(true);
            }
         }
      } else if (PersistentContent.isEncryptionEnabled()) {
         if (!this.isDirty()) {
            this.closeViewer(false);
            RIMGlobalMessagePoster.postGlobalEvent(EventUtilities.getCalendarProcessId(), -4802534882197295853L, 0, 0, null, null);
            return;
         }

         if (this._eventData instanceof Object) {
            this.updateEventContents();
            EncryptableProvider ep = (EncryptableProvider)this._eventData;
            ep.reCrypt(true, true);
            boolean subjectDirty = this._subject.isDirty();
            boolean locationDirty = this._location.isDirty();
            boolean notesDirty = this._notes.isDirty();
            this._subject.setText("");
            this._location.setText("");
            this._notes.setText("");
            this._subject.setDirty(subjectDirty);
            this._location.setDirty(locationDirty);
            this._notes.setDirty(notesDirty);
            return;
         }
      }
   }

   @Override
   public void updateEventContents() {
      this.getDataFromFields();
   }

   @Override
   public void openViewer(String title, Verb[] verbs, int saveVerbIndex, int defaultVerbIndex, boolean allowRecur) {
      this.openViewer(title, verbs, saveVerbIndex, defaultVerbIndex, -1, allowRecur);
   }

   @Override
   public void openViewer(String title, Verb[] verbs, int saveVerbIndex, int defaultVerbIndex, int deleteVerbIndex, boolean allowRecur) {
      this._verbs = verbs;
      this._saveVerb = null;
      this._defaultVerb = null;
      this._deleteMessageVerb = null;
      this.checkForVerbOverrides(this._eventData, this._verbs);
      if (verbs != null) {
         if (saveVerbIndex >= 0 && saveVerbIndex < verbs.length) {
            this._saveVerb = verbs[saveVerbIndex];
         }

         if (defaultVerbIndex >= 0 && defaultVerbIndex < verbs.length) {
            this._defaultVerb = verbs[defaultVerbIndex];
         }

         if (deleteVerbIndex >= 0 && deleteVerbIndex < verbs.length) {
            this._deleteMessageVerb = verbs[deleteVerbIndex];
         }
      }

      this._allowRecur = allowRecur;
      ContextObject co = ContextObject.castOrCreate(this._context);
      Recur tmp = this._eventData.getReadOnlyRecurrence();
      if (tmp == null) {
         tmp = this._eventData.getRecurrenceCopy();
      }

      ContextObject.put(co, 4143325197084129318L, new Object(this._eventData.getStartDate(TimeZone.getTimeZone(this._eventData.getTimeZoneID()))));
      ContextObject.put(co, 853966984731399007L, this._eventData.getTimeZoneID());
      ContextObject.put(co, -6289930384059412695L, new Object(this._eventData.getInstanceDuration()));
      ContextObject.put(co, -479237175265400126L, new Object(this._eventData.isAllDay()));
      if (this._saveVerb == null) {
         co.setFlag(85);
      }

      this._recurBlock = (RecurrenceField)((RecurImpl)tmp).getField(co);
      this._recurCopy = (RecurImpl)this._recurBlock.getRecurrenceInfo();
      if (co.getFlag(31)) {
         this._newEvent = true;
      }

      this.allocateFields();
      this._titleField.setText(title);
      this.populateAndAddFields();
      if (this._eventData.getAllDayFlag()) {
         int dayStart = CalendarOptions.getOptions().getDayStart();
         this._currentHour = dayStart / 3600000;
         this._currentMinute = dayStart - this._currentHour * 3600000;
         this._currentMinute /= 60000;
         this._duration.setDuration(this._duration.getDuration() + 3600000);
         this._duration.setDirty(false);
         this._recurBlock.setDirty(false);
      }

      this._uiApp = UiApplication.getUiApplication();
      this._conflictCheckingThread.start();
      this._uiApp.pushModalScreen(this);
      this.cleanUp();
   }

   @Override
   public void closeViewer(boolean param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: iload 1
      // 01: ifeq 08
      // 04: aload 0
      // 05: invokespecial net/rim/device/apps/internal/calendar/eventprovider/EventViewer.getDataFromFields ()V
      // 08: aload 0
      // 09: getfield net/rim/device/apps/internal/calendar/eventprovider/EventViewer._uiApp Lnet/rim/device/api/ui/UiApplication;
      // 0c: aload 0
      // 0d: invokevirtual net/rim/device/api/ui/UiApplication.popScreen (Lnet/rim/device/api/ui/Screen;)V
      // 10: aload 0
      // 11: invokespecial net/rim/device/apps/internal/calendar/eventprovider/EventViewer.cleanUp ()V
      // 14: return
      // 15: astore 2
      // 16: aload 0
      // 17: invokespecial net/rim/device/apps/internal/calendar/eventprovider/EventViewer.cleanUp ()V
      // 1a: return
      // 1b: astore 3
      // 1c: aload 0
      // 1d: invokespecial net/rim/device/apps/internal/calendar/eventprovider/EventViewer.cleanUp ()V
      // 20: aload 3
      // 21: athrow
      // try (4 -> 8): 11 null
      // try (4 -> 8): 15 null
      // try (11 -> 12): 15 null
      // try (15 -> 16): 15 null
   }

   @Override
   public boolean isEventTimeDirty() {
      return this._allDayFlag.getChecked() != this._wasAllDay || this._timeBlock.isDirty() || this.isRecurrenceDirty();
   }

   @Override
   public byte getConcurrentModificationFlag() {
      return this._concurrentModification;
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if ((context & -2147483648) == 0) {
         if (field == this._allDayFlag) {
            this.allDayFlagChanged();
            this._conflictCheckingThread.startConflictingChecking();
         }

         if (field == this._start) {
            this.eventStartChanged();
            this._conflictCheckingThread.startConflictingChecking();
         } else if (field == this._end) {
            this.eventEndChanged();
            this._conflictCheckingThread.startConflictingChecking();
         } else if (field == this._duration) {
            this.durationChanged();
            this._conflictCheckingThread.startConflictingChecking();
         } else if (field == this._timeZone) {
            this.timeZoneChanged();
         } else if (field == this._recurBlock) {
            this.makeDurationConsistent();
            this._conflictCheckingThread.startConflictingChecking();
         } else if (this._meetingInfoField != null && field == this._meetingInfoField) {
            int attendeeCount = this.getMeetingAttendeeCount();
            if (this._newEvent && !this._calendarService.getCICALConfiguration().canInviteToAllDayMeetings()) {
               this._allDayFlag.setEditable(attendeeCount == 0);
            }

            this._attendeesChanged = true;
         } else {
            if (field == this._sendUsingField) {
               this.sendUsingFieldChanged();
               this._conflictCheckingThread.startConflictingChecking();
            }
         }
      }
   }

   @Override
   public void focusChanged(Field field, int eventType) {
      if (eventType == 3 && field == this._end) {
         this.validate(1);
      }
   }

   @Override
   public void reset(Collection collection) {
      this._concurrentModification = (byte)(this._concurrentModification | 4);
      if (collection instanceof Object) {
         this._conflictCheckingThread.startConflictingChecking();
      }
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      if (collection instanceof Object) {
         this._conflictCheckingThread.startConflictingChecking();
      }
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (collection == ALPConfiguration.getManager()) {
         this.scheduleUpdateLookupFields();
      } else {
         if (collection instanceof Object) {
            this.checkForConcurrentModification((Event)oldElement, (byte)1);
            this._conflictCheckingThread.startConflictingChecking();
         }
      }
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      if (collection != ALPConfiguration.getManager()) {
         this.checkForConcurrentModification((Event)element, (byte)2);
      } else {
         if (collection instanceof Object) {
            this._conflictCheckingThread.startConflictingChecking();
         }
      }
   }

   @Override
   public boolean validate(int type) {
      if (this._saveVerb == null) {
         return true;
      }

      boolean result = true;
      long startInstant = this._start.getDate();
      long endInstant = this._end.getDate();
      if ((type & 2) > 0) {
         Field focusedField = this.getLeafFieldWithFocus();
         if (!this._calendarService.getCICALConfiguration().canAppointmentsSpanMidnight()
            && this._allDayFlag.getChecked()
            && locateMeetingInfoFields(focusedField.getScreen()) != null) {
            ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
            Dialog.alert(rb.getString(627));
            return false;
         }
      }

      if ((type & 1) > 0) {
         if (endInstant >= startInstant) {
            if (!DateTimeUtilities.isSameDate(startInstant, endInstant, this._sharedTZ, null, this._cal)) {
               Field focusedField = this.getLeafFieldWithFocus();
               if (this.makeDurationConsistent()) {
                  return false;
               }

               if (!this._calendarService.getCICALConfiguration().canAppointmentsSpanMidnight()
                  && (!this._allDayFlag.getChecked() || locateMeetingInfoFields(focusedField.getScreen()) != null)) {
                  ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
                  Dialog.alert(rb.getString(2));
                  result = false;
               }
            }
         } else {
            ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
            Dialog.alert(rb.getString(115));
            result = false;
         }
      }

      if (result) {
         result = this._recurBlock.validate();
      }

      Recur recurInfo = this._recurBlock.getRecurrenceInfo();
      if ((type & 8) > 0
         && this._calendarService.getCICALConfiguration().isOTAConfigSupported()
         && this._calendarService.getCICALConfiguration().isOTACalendarEnabled()) {
         if (recurInfo != null && recurInfo.getRecurType() != 0 && recurInfo.isFinite()) {
            long recurEnd = recurInfo.getEndDate() / 1000;
            if (recurEnd > Integer.MAX_VALUE) {
               ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
               Dialog.alert(rb.getString(638));
               result = false;
            }
         }

         if (result && (startInstant / 1000 > Integer.MAX_VALUE || endInstant / 1000 > Integer.MAX_VALUE)) {
            ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
            Dialog.alert(rb.getString(637));
         }
      }

      ((FieldProvider)this._reminderModel).grabDataFromField(this._reminderField, null);
      if (result && this._reminderModel.hasReminder() && (type & 4) > 0 && recurInfo.getRecurType() == 0) {
         long time = 0;
         if (this._reminderModel instanceof Object) {
            if (this.isAllDayChecked()) {
               ((CalendarExtensions)this._gmtCal).setTimeLong(startInstant);
               Calendar localCal = Calendar.getInstance();
               startInstant = DateTimeUtilities.copyCalendar(this._gmtCal, localCal);
            }

            time = startInstant - this._reminderModel.getTime();
         }

         if (time < System.currentTimeMillis()) {
            ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
            result = Dialog.ask(3, rb.getString(625), 4) == 4;
            if (result) {
               if (this._reminderField instanceof Object) {
                  ((ChoiceField)this._reminderField).setSelectedIndex(0);
               }

               if (this._reminderField instanceof Object) {
                  ((DateField)this._reminderField).setDate(0);
               }
            }
         }
      }

      return result;
   }

   private long checkForOverlappingAppointments(long duration) {
      int instancesToCheck = 1;
      long lastStart = 0;
      long maxDurationAllowed = 153722867280912L;
      if (this._recurCopy.getRecurType() == 2) {
         instancesToCheck = this._recurBlock.getSetDays().getNumSet();
      }

      if (EventImpl._recurCalc
         .getAnInstance(this._start.getDate() - 1, true, this._recurHandle, this._start.getDate(), 0, this._recurCopy, this._start.getTimeZone())) {
         for (; instancesToCheck > 0; instancesToCheck--) {
            lastStart = this._recurHandle._handle;
            if (EventImpl._recurCalc
               .getFromKnownInstance(this._recurHandle._handle, true, this._recurHandle, this._start.getDate(), 0, this._recurCopy, this._start.getTimeZone())) {
               long dur = this._recurHandle._handle - lastStart;
               if (dur < maxDurationAllowed) {
                  maxDurationAllowed = dur;
               }
            }
         }
      }

      return maxDurationAllowed;
   }

   private void setTime(int[] fields, long time, TimeZone tz) {
      Calendar cal = this._cal;
      cal.setTimeZone(tz);
      ((CalendarExtensions)cal).setTimeLong(time);
      DateTimeUtilities.getCalendarFields(cal, fields);
   }

   private long getTime(int[] fields, TimeZone tz) {
      Calendar cal = this._cal;
      cal.setTimeZone(tz);
      DateTimeUtilities.setCalendarFields(cal, fields);
      return ((CalendarExtensions)cal).getTimeLong();
   }

   private void switchDateFieldToTimeZone(DateField field, TimeZone tz) {
      this.setTime(this._calFields, field.getDate(), field.getTimeZone());
      field.setTimeZone(tz);
      field.setDate(this.getTime(this._calFields, tz));
   }

   private void fixupEndTimeBasedOnStartTimeAndDuration() {
      long duration = this._duration.getDuration();
      if (this._allDayFlag.getChecked()) {
         duration -= 86400000;
      }

      this._end.setDate(this._start.getDate() + duration);
   }

   private void eventStartChanged() {
      this.fixupEndTimeBasedOnStartTimeAndDuration();
      long startDate = this._start.getDate();
      long duration = this._end.getDate() - startDate;
      if (this._allDayFlag.getChecked()) {
         this._cal.setTimeZone(this._sharedTZ);
         ((CalendarExtensions)this._gmtCal).setTimeLong(startDate);
         startDate = DateTimeUtilities.copyCalendar(this._gmtCal, this._cal);
      }

      this._recurBlock.setNewStartDate(startDate, duration);
      this._previousStartTime = this._start.getDate();
      this._timeBlockCurrent = false;
   }

   private void eventEndChanged() {
      long startInstant = this._start.getDate();
      long endInstant = this._end.getDate();
      if (endInstant < startInstant) {
         endInstant = startInstant;
      }

      long duration = endInstant - startInstant;
      if (this._allDayFlag.getChecked()) {
         this._cal.setTimeZone(this._sharedTZ);
         ((CalendarExtensions)this._gmtCal).setTimeLong(startInstant);
         startInstant = DateTimeUtilities.copyCalendar(this._gmtCal, this._cal);
         duration += 86400000;
      }

      this._duration.setDuration(duration);
      this._recurBlock.setNewStartDate(startInstant, duration);
      this._timeBlockCurrent = false;
   }

   private void durationChanged() {
      long startInstant = this._start.getDate();
      long recurStartInstant = startInstant;
      long duration = this._duration.getDuration();
      if (this._allDayFlag.getChecked()) {
         if (duration >= 86400000) {
            duration -= 86400000;
         } else {
            this._duration.setDuration(duration + 86400000);
            this._duration.setDirty(true);
            this._duration.setMuddy(true);
         }

         this._cal.setTimeZone(this._sharedTZ);
         ((CalendarExtensions)this._gmtCal).setTimeLong(recurStartInstant);
         recurStartInstant = DateTimeUtilities.copyCalendar(this._gmtCal, this._cal);
      }

      this._end.setDate(startInstant + duration);
      this._recurBlock.setNewStartDate(recurStartInstant, duration);
      this._timeBlockCurrent = false;
   }

   private void timeZoneChanged() {
      String tzID = TimeService.getTimeService().getTimeZoneIDFromIndex(this._timeZone.getSelectedIndex());
      if (tzID != null) {
         this._sharedTZ = TimeZone.getTimeZone(tzID);
         TimeZone newTimeZone = this._sharedTZ;
         if (!this._allDayFlag.getChecked()) {
            this.switchDateFieldToTimeZone(this._start, newTimeZone);
            this._end.setTimeZone(newTimeZone);
            this.fixupEndTimeBasedOnStartTimeAndDuration();
         }

         this._previousStartTime = this._start.getDate();
         this._timeBlockCurrent = false;
      }
   }

   private void allDayFlagChanged() {
      this.updateFieldsForAllDayEvent();
      int freeBusy = this._allDayFlag.getChecked() ? 0 : 2;
      if (!(this._freeBusyField instanceof Object) && this._freeBusyField instanceof Object) {
         ChoiceField cf = (ChoiceField)this._freeBusyField;
         cf.setSelectedIndex(freeBusy);
      }

      this._freeBusyField.setDirty(true);
   }

   private void updateFieldsForAllDayEvent() {
      int[] fields = this._calFields;
      this.setTime(fields, this._start.getDate(), this._start.getTimeZone());
      TimeZone newTimeZone;
      DateFormat newFormat;
      int smallestUnit;
      int largestUnit;
      int durationAdjustment;
      if (this._allDayFlag.getChecked()) {
         newTimeZone = _gmtTZ;
         newFormat = this._dateFormat;
         smallestUnit = 3;
         largestUnit = 3;
         durationAdjustment = -86400000;
         this._currentHour = fields[3];
         this._currentMinute = fields[4];
         fields[3] = fields[4] = 0;
      } else {
         newTimeZone = this._sharedTZ;
         newFormat = this._dateAndTimeFormat;
         smallestUnit = 1;
         largestUnit = 2;
         durationAdjustment = 86400000;
         fields[3] = this._currentHour;
         fields[4] = this._currentMinute;
      }

      fields[5] = fields[6] = 0;
      this._start.setTimeZone(newTimeZone);
      this._end.setTimeZone(newTimeZone);
      this._previousStartTime = this.getTime(fields, newTimeZone);
      this._start.setDate(this._previousStartTime);
      this._start.setFormat(newFormat);
      this._end.setFormat(newFormat);
      this._recurBlock.setAllDayFlagState(this._allDayFlag.getChecked());
      this._duration.setSmallestUnitDisplayed(smallestUnit);
      this._duration.setLargestUnitDisplayed(largestUnit);
      this._duration.setDuration(this._duration.getDuration() - durationAdjustment);
      this.durationChanged();
   }

   private boolean makeDurationConsistent() {
      boolean result = false;
      long duration = this._duration.getDuration();
      if (this._allDayFlag.getChecked()) {
         long numDays = duration / 86400000;
         duration = numDays * 86400000;
      }

      this._maxDurationFromPattern = RecurUtil.getRecurDurMax(this._recurCopy.getRecurType());
      long maxDuration;
      int msgId;
      if (this._maxDurationFromPattern <= 0) {
         maxDuration = 128849018820000L;
         msgId = 622;
      } else {
         maxDuration = this.checkForOverlappingAppointments(duration);
         msgId = 356;
      }

      if (duration > maxDuration || duration < -1) {
         ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
         Dialog.alert(rb.getString(msgId));
         duration = maxDuration;
         this._duration.setDuration(duration);
         this.fixupEndTimeBasedOnStartTimeAndDuration();
         this._end.setDirty(true);
         this._end.setMuddy(true);
         this._duration.setDirty(true);
         this._duration.setMuddy(true);
         this._timeBlockCurrent = false;
         result = true;
      }

      return result;
   }

   private void checkForConcurrentModification(Event modifiedOrDeletedEvent, byte modificationType) {
      if (!this._uiApp.isEventThread()) {
         long modifiedOrDeletedEventUID = modifiedOrDeletedEvent.getUID();
         boolean concurrentModification = false;
         if (modifiedOrDeletedEventUID == this._eventData.getUID()) {
            concurrentModification = true;
         } else if (this._eventData.getRelatedLUID() != 0) {
            Event parentEvent = (Event)this._calendarService.getCalendarDatabase().get(this._eventData.getRelatedLUID());
            if (parentEvent == null || modifiedOrDeletedEventUID == parentEvent.getUID()) {
               concurrentModification = true;
            }
         }

         if (concurrentModification) {
            this._concurrentModification |= modificationType;
         }
      }
   }

   private boolean isRecurrenceDirty() {
      return this._recurBlock.isDirty();
   }

   private void allocateExternalFields() {
      int max = this._eventData.size();
      Object[] models = new Object[max];
      RIMModel[] sortedModels = new Object[max];
      int[] orders = new int[max];
      this._externalFields1 = new Object[max];
      this._externalModels1 = new Object[max];
      this._externalFields2 = new Object[max];
      this._externalModels2 = new Object[max];
      this._eventData.getElements(models);
      max = FieldUtility.orders(models, sortedModels, orders, null);
      int i = 0;
      int counter = 0;
      Field[] externalFields = this._externalFields1;
      RIMModel[] externalModels = this._externalModels1;

      while (i < max && orders[counter] < 1073741823) {
         RIMModel rm = sortedModels[i++];
         if (rm instanceof Object) {
            FieldProvider fp = (FieldProvider)rm;
            Field f = fp.getField(null);
            if (f != null) {
               if (rm instanceof Object) {
                  this._reminderModel = (ReminderModel)rm;
                  this._reminderField = f;
               } else if (rm instanceof Object && f instanceof Object && f.getChangeListener() == null) {
                  f.setChangeListener(this);
                  this._meetingInfoField = (Manager)f;
               }

               externalFields[counter] = f;
               externalModels[counter] = rm;
               if (this._saveVerb == null) {
                  externalFields[counter].setEditable(false);
               }

               counter++;
            }
         }
      }

      Array.resize(externalModels, counter);
      Array.resize(externalFields, counter);
      counter = 0;
      externalFields = this._externalFields2;
      externalModels = this._externalModels2;

      while (i < max) {
         RIMModel rm = sortedModels[i++];
         if (rm instanceof Object) {
            FieldProvider fp = (FieldProvider)rm;
            Field f = fp.getField(null);
            if (f != null) {
               externalFields[counter] = f;
               externalModels[counter] = rm;
               if (this._saveVerb == null) {
                  externalFields[counter].setEditable(false);
               }

               counter++;
            }
         }
      }

      Array.resize(externalModels, counter);
      Array.resize(externalFields, counter);
   }

   private void allocateFields() {
      ResourceBundle rb = _rb;
      this.allocateExternalFields();
      this._titleField = (LabelField)(new Object(null, 1152921504606847040L));
      this._sendUsingField = this.getCalendarServiceChoices();
      this._subject = (ActiveAutoTextEditField)(new Object(rb.getString(100), null));
      this._location = (ActiveAutoTextEditField)(new Object(rb.getString(101), null, 1000000, 4503601774854144L));
      this._sep2 = (SeparatorField)(new Object());
      this._allDayFlag = (CheckboxField)(new Object(rb.getString(102), false));
      this._allDayFlag.setChangeListener(this);
      this._pencilIn = (CheckboxField)(new Object(rb.getString(7), false));
      this._showTimeAs = (ChoiceField)(new Object(rb.getString(6), rb.getStringArray(635)));
      if (this._eventNotFound) {
         this._meetingMissingField = (RichTextField)(new Object(rb.getString(623)));
         this._meetingMissingField.setEditable(false);
         this._meetingMissingField.setTag(WARNING_AREA_TAG);
      }

      if (this._meetingOutdated) {
         this._meetingOutdatedField = (RichTextField)(new Object(rb.getString(619)));
         this._meetingOutdatedField.setEditable(false);
         this._meetingOutdatedField.setTag(WARNING_AREA_TAG);
      }

      this._conflictsBlock = (VerticalFieldManager)(new Object());
      this._conflictsField = (RichTextField)(new Object(rb.getString(117)));
      Font f = this._conflictsField.getFont();
      this._conflictsField.setFont(f.derive(1));
      this._conflictsField.setEditable(false);
      this._conflictsField.setTag(WARNING_AREA_TAG);
      this._timeBlock = (VerticalFieldManager)(new Object());
      this._start = (DateField)(new Object(rb.getString(103), 0, this._dateAndTimeFormat));
      this._start.setMinuteIncrements(900000);
      this._start.setTimeZone(this._sharedTZ);
      this._start.setChangeListener(this);
      this._end = (DateField)(new Object(rb.getString(104), 0, this._dateAndTimeFormat));
      this._end.setMinuteIncrements(900000);
      this._end.setTimeZone(this._sharedTZ);
      this._end.setChangeListener(this);
      this._end.setFocusListener(this);
      this._duration = (DurationField)(new Object(rb.getString(105), 2, 1, 0));
      this._duration.setChangeListener(this);
      this._timeZone = (ChoiceField)(new Object(CommonResources.getString(2013), TimeService.getTimeService().getTimeZoneNamesShort(), 0, 134217728));
      this._timeZone.setChangeListener(this);
      this._sensitivityField = (CheckboxField)(new Object(rb.getString(613), this._eventData.getSensitivity() == 2));
      this._recurBlock.setChangeListener(this);
      this._notes = (ActiveAutoTextEditField)(new Object(CommonResources.getString(2004), null, 4096));
      if (this._saveVerb == null) {
         this._subject.setEditable(false);
         this._location.setEditable(false);
         this._allDayFlag.setEditable(false);
         this._start.setEditable(false);
         this._end.setEditable(false);
         this._duration.setEditable(false);
         this._timeZone.setEditable(false);
         this._notes.setEditable(false);
         this._sensitivityField.setEditable(false);
      }

      if (this._eventData.getRelatedLUID() != 0) {
         this._sensitivityField.setEditable(false);
      }

      if (this._calendarService.getCICALConfiguration().isRecurrencePatternEditDisabled() && !this._newEvent) {
         this._allDayFlag.setEditable(false);
      }

      if (this._calendarService.getCICALConfiguration().isRecurrenceTimeEditDisabled() && !this._newEvent && this._eventData.isRecurring()) {
         this._allDayFlag.setEditable(false);
         this._duration.setEditable(false);
         this._start.setEditable(false);
         this._end.setEditable(false);
         this._timeZone.setEditable(false);
      }

      this._debugRefIDField = (LabelField)(new Object(((StringBuffer)(new Object("EVT UID: "))).append(this._eventData.getUID()).toString(), 18014398509481984L));
      this._debugLongIDField = (LabelField)(new Object(
         ((StringBuffer)(new Object("EVT LUID: "))).append(this._eventData.getLUID()).toString(), 18014398509481984L
      ));
      this._debugiCalIDField = (LabelField)(new Object(
         ((StringBuffer)(new Object("EVT ICALID: "))).append(this._eventData.getICalID()).toString(), 18014398509481984L
      ));
      this._debugTimeZoneField = (LabelField)(new Object(
         ((StringBuffer)(new Object("Timezone: "))).append(this._eventData.getTimeZoneID()).toString(), 18014398509481984L
      ));
      this._debugGmtStartDate = (LabelField)(new Object(
         ((StringBuffer)(new Object("GMT Start: "))).append(this._eventData.getStartDate(null)).toString(), 18014398509481984L
      ));
      this._debugGMESendStatus = (LabelField)(new Object("GME Send Status: ", 18014398509481984L));
      this._debugCalSrvBaseIDField = (LabelField)(new Object(
         ((StringBuffer)(new Object("BASE CAL SVC ID:"))).append(this._calendarServiceManager.getBaseSystemCalendarService().getUniqueServiceID()).toString(),
         18014398509481984L
      ));
      this._debugCalSrvDefaultIDField = (LabelField)(new Object(
         ((StringBuffer)(new Object("DFLT CAL SVC ID:"))).append(this._calendarServiceManager.getDefaultCalendarService().getUniqueServiceID()).toString(),
         18014398509481984L
      ));
      this._debugCalSrvDefaultFldIDField = (LabelField)(new Object(
         ((StringBuffer)(new Object("DFLT CAL FLD ID:")))
            .append(this._calendarServiceManager.getDefaultCalendarService().getPrimaryCalendarFolderID())
            .toString(),
         18014398509481984L
      ));
      this._debugCalDBIDField = (LabelField)(new Object(
         ((StringBuffer)(new Object("THIS EVT SVC ID: "))).append(this._eventData.getCalendarKey().getCalendarServiceID()).toString(), 18014398509481984L
      ));
      this._debugCalDBFldIDField = (LabelField)(new Object(
         ((StringBuffer)(new Object("THIS EVT FLD ID: "))).append(this._eventData.getCalendarKey().getCalendarFolderID()).toString(), 18014398509481984L
      ));
      this._debugCalSrvIDField = (LabelField)(new Object(
         ((StringBuffer)(new Object("THIS CAL SVC ID: "))).append(this._calendarService.getUniqueServiceID()).toString(), 18014398509481984L
      ));
      this._debugCalUIDField = (LabelField)(new Object(
         ((StringBuffer)(new Object("THIS CAL SVC UID: "))).append(this._calendarService.getCICALConfiguration().getUID()).toString(), 18014398509481984L
      ));
      this._debugCalUserIDField = (LabelField)(new Object(
         ((StringBuffer)(new Object("THIS CAL SVC USERID: "))).append(this._calendarService.getCICALConfiguration().getUserID()).toString(), 18014398509481984L
      ));
      this._debugCalDSIDField = (LabelField)(new Object(
         ((StringBuffer)(new Object("THIS CAL SVC DSID: "))).append(this._calendarService.getCICALConfiguration().getDatasourceID()).toString(),
         18014398509481984L
      ));
      String syncInfo = null;
      if (this._syncData != null) {
         syncInfo = ((StringBuffer)(new Object("D/H Seq: ")))
            .append(this._syncData.getDeviceSequence())
            .append(",")
            .append(this._syncData.getHostSequence())
            .toString();
      } else {
         syncInfo = "UNAVAILABLE";
      }

      this._debugSeqRevPair = (LabelField)(new Object(syncInfo, 18014398509481984L));
      this._debugBlock = (VerticalFieldManager)(new Object());
      this._debugBlock.add(this._debugRefIDField);
      this._debugBlock.add(this._debugLongIDField);
      this._debugBlock.add(this._debugiCalIDField);
      this._debugBlock.add((Field)(new Object()));
      this._debugBlock.add(this._debugTimeZoneField);
      this._debugBlock.add(this._debugGmtStartDate);
      this._debugBlock.add(this._debugSeqRevPair);
      this._debugBlock.add(this._debugGMESendStatus);
      this._debugBlock.add((Field)(new Object()));
      this._debugBlock.add(this._debugCalSrvBaseIDField);
      this._debugBlock.add(this._debugCalSrvDefaultIDField);
      this._debugBlock.add(this._debugCalSrvDefaultFldIDField);
      this._debugBlock.add((Field)(new Object()));
      this._debugBlock.add(this._debugCalDBIDField);
      this._debugBlock.add(this._debugCalDBFldIDField);
      this._debugBlock.add((Field)(new Object()));
      this._debugBlock.add(this._debugCalSrvIDField);
      this._debugBlock.add(this._debugCalUIDField);
      this._debugBlock.add(this._debugCalUserIDField);
      this._debugBlock.add(this._debugCalDSIDField);
      if (this._eventData.getRelatedLUID() != 0) {
         this._debugBlock.add((Field)(new Object()));
         int serverID = (int)(this._eventData.getRelatedLUID() & 4294967295L);
         this._debugParentIDField = (LabelField)(new Object(((StringBuffer)(new Object("Parent Id: "))).append(serverID).toString(), 18014398509481984L));
         this._debugRelatedTimeField = (LabelField)(new Object(
            ((StringBuffer)(new Object("Related Time: "))).append(this._eventData.getRelatedTime()).toString(), 18014398509481984L
         ));
         this._debugBlock.add(this._debugParentIDField);
         this._debugBlock.add(this._debugRelatedTimeField);
      }

      this._debugBlock.add((Field)(new Object()));
   }

   private void populateAndAddFields() {
      this.setTitle(this._titleField);
      this.addSendUsingField(this);
      Manager section = this.createSection();
      this._subject.setText(this._eventData.getSubject());
      this._subject.setCursorPosition(this._subject.getTextLength());
      this._subject.setTag(SUBJECT_TEXT_TAG);
      section.add(this._subject);
      this._subject.setFocus();
      this._location.setText(this._eventData.getLocation());
      section.add(this._location);
      if (!this._viewingAppointmentStatus) {
         if (this._meetingOutdated) {
            this.add(this._meetingOutdatedField);
         }

         this.add(this._conflictsBlock);
      } else if (this._meetingMissingField != null) {
         this.add(this._meetingMissingField);
      }

      section = this.createSection();
      byte recurrenceType = this._recurCopy.getRecurType();
      boolean allDayEvent = this._eventData.getAllDayFlag();
      this._allDayFlag.setChecked(allDayEvent);
      if (this._saveVerb != null || allDayEvent) {
         section.add(this._allDayFlag);
      }

      this.populateAndAddTimeBlock(section);
      this.populateAndAddFreeBusyBlock(section);
      Field[] external = this._externalFields1;
      int max = external.length;

      for (int i = 0; i < max; i++) {
         section.add(external[i]);
      }

      this._maxDurationFromPattern = RecurUtil.getRecurDurMax(this._recurCopy.getRecurType());
      if (this._calendarService.getCICALConfiguration().isRecurrencePatternEditDisabled() && !this._newEvent && this._eventData.isRecurring()) {
         section.add(this._recurBlock);
      } else if (!this._allowRecur || this._saveVerb == null && recurrenceType == 0) {
         section.add(this._sep2);
      } else {
         section.add(this._recurBlock);
      }

      section.add(this._sensitivityField);
      section = this.createSection();
      this._notes.setText(this._eventData.getNotes());
      section.add(this._notes);
      external = this._externalFields2;
      max = external.length;

      for (int i = 0; i < max; i++) {
         section.add(external[i]);
      }

      if (allDayEvent) {
         this.updateFieldsForAllDayEvent();
      }

      this._timeBlockCurrent = true;
   }

   private void checkForVerbOverrides(Event event, Verb[] defaultVerbs) {
      if (defaultVerbs != null) {
         ContextObject contextObject = (ContextObject)(new Object());
         ContextObject.put(contextObject, 248, defaultVerbs);
         ContextObject.setFlag(contextObject, 87);
         ContextObject.put(contextObject, 424670468422402792L, event);
         int count = event.size();
         Object[] models = new Object[count];
         event.getElements(models);

         for (int i = 0; i < count; i++) {
            Object model = models[i];
            if (model instanceof Object) {
               VerbProvider verbProvider = (VerbProvider)model;
               verbProvider.getVerbs(contextObject, new Object[0]);
            }
         }

         ContextObject.remove(contextObject, 248);
         ContextObject.clearFlag(contextObject, 87);
         ContextObject.remove(contextObject, 424670468422402792L);
      }
   }

   private Manager createSection() {
      Manager section = (Manager)(new Object());
      section.setTag(SECTION_AREA_TAG);
      this.add(section);
      return section;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void close() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         super.close();
         var3 = false;
      } finally {
         if (var3) {
            this.cleanUp();
         }
      }

      this.cleanUp();
   }

   private void cleanUp() {
      CalDB[] calDBs = this._calendarServiceManager.getCalendarDatabases();

      for (int i = 0; i < calDBs.length; i++) {
         calDBs[i].removeCollectionListener(this);
      }

      this._uiApp.removeGlobalEventListener(this);
      ALPConfiguration.getManager().removeCollectionListener(this);
      CalOptionCache.setCurrentOpenViewer(null);
      if (this._conflictCheckingThread != null) {
         this._conflictCheckingThread.stopConflictThread();
         ALPConfiguration.getManager().removeCollectionListener(this);
         this._conflictCheckingThread = null;
      }

      if (this._meetingInfoField != null) {
         this._meetingInfoField.setChangeListener(null);
         this._meetingInfoField = null;
      }
   }

   private void refreshContentProtectedFields() {
      try {
         this._subject.setText(this._eventData.getSubject());
         this._location.setText(this._eventData.getLocation());
         this._notes.setText(this._eventData.getNotes());

         for (int i = 0; i < this._externalFields1.length; i++) {
            Field f = this._externalFields1[i];
            if (f instanceof Object) {
               VerticalFieldManager vfm = (VerticalFieldManager)f;

               for (int j = 0; j < vfm.getFieldCount(); j++) {
                  Field f2 = vfm.getField(j);
                  if (f2 instanceof Object) {
                     AddressReferenceViewField arvf = (AddressReferenceViewField)f2;
                     arvf.reInitalizeField();
                  }
               }
            }
         }
      } finally {
         return;
      }
   }

   @Override
   public boolean isDirty() {
      return this._attendeesChanged || super.isDirty();
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (attached) {
         this._concurrentModification = 0;
         CalDB[] calDBs = CalendarServiceManager.getInstance().getCalendarDatabases();

         for (int i = 0; i < calDBs.length; i++) {
            calDBs[i].addCollectionListener(this);
         }

         this._uiApp.addGlobalEventListener(this);
      } else {
         this.cleanUp();
      }

      super.onUiEngineAttached(attached);
   }

   EventViewer(Event event, Object context) {
      super(17592186044416L);
      this._conflictCheckingThread = new EventViewer$ConflictCheckingThread(this);
      this._context = context;
      this._calendarServiceManager = CalendarServiceManager.getInstance();
      this._calendarService = this._calendarServiceManager.findCalendarService(event);
      this._calendarFolder = this._calendarService.getCalendarFolder(event.getCalendarKey().getCalendarFolderID());
      ContextObject co = ContextObject.castOrCreate(context);
      CalOptionCache.setCurrentOpenViewer(this);
      if (ContextObject.getFlag(context, 27)) {
         this._viewingAppointmentStatus = true;
      }

      TimeZone tz = TimeZone.getTimeZone(event.getTimeZoneID());
      if (event.getStartDate(tz) == 0 && this._viewingAppointmentStatus) {
         this._eventData = (Event)this._calendarService.getCalendarDatabase().get(event.getLUID());
         if (this._eventData == null) {
            this._eventNotFound = true;
         }
      }

      if (this._eventData == null) {
         this._eventData = event;
      }

      this._wasAllDay = this._eventData.getAllDayFlag();
      this._sharedTZ = TimeZone.getDefault();
      ALPConfiguration.getManager().addCollectionListener(this);
      this._meetingOutdated = false;
      int messageId = 0;
      Event currentEvent = (Event)this._calendarService.getCalendarDatabase().get(event.getLUID());
      if (currentEvent != null) {
         this._syncData = OTACalendarSyncDataManager.getInstance().get(currentEvent);
      }

      if (!this._newEvent && this._syncData != null && !this._viewingAppointmentStatus) {
         Object o = ContextObject.get(co, 250);
         if (o instanceof Object) {
            messageId = ((CMIMEReferenceIdProvider)o).getCMIMEReferenceIdentifier();
            if (event.isMeeting() && this._syncData.getOwnerId() != -1 && this._syncData.getOwnerId() != messageId) {
               this._meetingOutdated = true;
            }
         }
      }

      this.getMainManager().setTag(EVENT_VIEWER_SCREEN_TAG);
   }

   private Verb[] getMeetingVerbs() {
      Verb[] verbs = new Object[0];
      ContextObject context = (ContextObject)(new Object(86));
      RIMModel model = (RIMModel)this._eventData.getMeetingInfo();
      ContextObject.put(context, SaveEventVerb.SAVE_EVENT_KEY, this._saveVerb);
      ContextObject.put(context, 424670468422402792L, this._eventData);
      ((VerbProvider)model).getVerbs(context, verbs);
      return verbs;
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      menu.add(this._verbs);
      CalendarProxy calProxy = CalendarProxy.getInstance();
      Object[] eventVerbs = calProxy.getRepositoryCopy(5182228461004335870L);
      ContextObject context = (ContextObject)(new Object(86));
      if (eventVerbs != null) {
         for (int i = 0; i < eventVerbs.length; i++) {
            Verb eventVerb = (Verb)eventVerbs[i];
            menu.add(eventVerb);
         }
      }

      Verb[] verbs = new Object[0];
      Field focusedField = this.getLeafFieldWithFocus();
      Object cookie = focusedField.getCookie();
      RIMModel cookieModel = null;
      context.put(9045827404276417370L, focusedField);
      if (focusedField instanceof Object) {
         AddressReferenceViewField toggleField = ((AddressReferenceViewField$AddressReferenceViewDataField)focusedField).getAddressReferenceViewField();
         int resId = toggleField.isFriendlyVisible() ? 1650 : 1700;
         menu.add(toggleField.getToggleVerb(CommonResources.getResourceBundle(), resId));
      }

      if (cookie instanceof Object) {
         VerbProvider verbProvider = (VerbProvider)cookie;
         Array.resize(verbs, 0);
         context.setFlag(2);
         Verb tmpDefaultVerb = verbProvider.getVerbs(context, verbs);
         context.clearFlag(2);
         menu.add(verbs);
         if (this._defaultVerb == null) {
            this._defaultVerb = tmpDefaultVerb;
         }
      }

      if (this._saveVerb == null) {
         ContextObject.clearFlag(context, 0);
      } else {
         ContextObject.setFlag(context, 0);
         ContextObject.put(context, SaveEventVerb.SAVE_EVENT_KEY, this._saveVerb);
      }

      if (!this._allDayFlag.getChecked() || this._calendarService.getCICALConfiguration().canInviteToAllDayMeetings()) {
         RIMModel model = (RIMModel)this._eventData.getMeetingInfo();
         ContextObject.put(context, 4143325197084129318L, this);
         ContextObject.put(context, 424670468422402792L, this._eventData);
         Verb tmpDefaultVerb = ((VerbProvider)model).getVerbs(context, verbs);
         menu.add(verbs);
      }

      ContextObject.remove(context, SaveEventVerb.SAVE_EVENT_KEY);
      Field fieldWithFocus = this.getLeafFieldWithFocus();
      ContextMenu contextMenu = fieldWithFocus.getContextMenu();
      if (contextMenu.getDefaultItem() == null && this.isDirty() && this._saveVerb != null) {
         menu.setDefault(this._saveVerb);
      }

      if ((!this.isMuddy() || this._saveVerb == null) && (!this.isDirty() || this._saveVerb == null || !(fieldWithFocus instanceof Object))) {
         if (this._defaultVerb != null) {
            menu.setDefault(this._defaultVerb);
         }
      } else {
         menu.setDefault(this._saveVerb);
      }

      DefaultVerbProvider defaultVerbProvider = null;
      if (cookieModel != null) {
         Object addressCard = AddressBookServices.reverseLookup(cookieModel);
         if (addressCard instanceof Object) {
            defaultVerbProvider = (DefaultVerbProvider)(new Object((RIMModel)addressCard));
         }
      }

      menu.coalesce(-3072555018635390988L, defaultVerbProvider);
      context.put(3696141428889703675L, this._eventData);
   }

   @Override
   protected ContextObject getMenuContextObject() {
      ContextObject co = this.getContext();
      co = ContextObject.castOrCreate(co);
      co.put(3696141428889703675L, this._eventData);
      return co;
   }

   private boolean deleteViewedMessage() {
      if (this._deleteMessageVerb != null) {
         this._deleteMessageVerb.invoke(null);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      if (super.keyDown(keycode, time)) {
         return true;
      } else {
         char key = Keypad.map(keycode);
         if (key != 127 && Keypad.getAltedChar(key) != 127) {
            int k = MessageHotkeys.map(keycode);
            HotKeyCheck hotkey = (HotKeyCheck)ContextObject.get(this._context, -7922982350060060892L);
            return hotkey != null && hotkey.invokeHotkey(k, this._context);
         } else {
            return this.deleteViewedMessage();
         }
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (Character.isDigit(key) && this._reminderField.isFocus()) {
         ((ChoiceField)this._reminderField).setSelectedIndex(1);
         this.updateDisplay();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected boolean keyControl(char key, int status, int time) {
      char altKey = Keypad.getAltedChar(key);
      Field fieldWithFocus = this.getLeafFieldWithFocus();
      if (Character.isDigit(key) && fieldWithFocus == this._reminderField) {
         ((ChoiceField)this._reminderField).setSelectedIndex(1);
         return true;
      } else {
         return fieldWithFocus != this._duration && fieldWithFocus != this._reminderField && !(fieldWithFocus instanceof Object)
            ? super.keyControl(key, status, time)
            : this.keyChar(altKey, status, time);
      }
   }

   private void populateAndAddFreeBusyBlock(Manager section) {
      if (this._saveVerb != null) {
         byte freeBusySetting = this._eventData.getFreeBusy();
         if (this._calendarService.getCICALConfiguration().isRecurrencePatternEditDisabled()) {
            this._freeBusyField = this._pencilIn;
            if (freeBusySetting == 1) {
               this._pencilIn.setChecked(true);
            } else {
               this._pencilIn.setChecked(false);
            }
         } else {
            this._showTimeAs.setSelectedIndex(freeBusySetting);
            this._freeBusyField = this._showTimeAs;
         }

         section.add(this._freeBusyField);
      }
   }

   private void populateAndAddTimeBlock(Manager section) {
      long duration = this._eventData.getInstanceDuration();
      long startInstant = this._eventData.getStartDate(this._sharedTZ);
      this._previousStartTime = startInstant;
      this._start.setDate(startInstant);
      this._end.setDate(startInstant + duration);
      boolean allDay = this._eventData.getAllDayFlag();
      if (allDay) {
         duration -= 86400000;
      }

      this._duration.setDuration(duration);
      section.add(this._timeBlock);
      if (!this._eventNotFound) {
         this._timeBlock.add(this._start);
         this._timeBlock.add(this._end);
      }

      if (this._saveVerb != null) {
         this._timeBlock.add(this._duration);
      }

      this._timeBlock.add(this._timeZone);
      this._timeZone.setSelectedIndex(TimeService.getTimeService().getTimeZoneIndex(this._sharedTZ.getID()));
      this._conflictCheckingThread.startConflictingChecking();
   }

   private void getDataFromFields() {
      if (this._subject.isDirty()) {
         String subject = this._subject.getText().trim();
         if (this._eventData.getSubject() == null || !this._eventData.getSubject().equals(subject)) {
            this._eventData.setSubject(subject);
         }
      }

      if (this._location.isDirty()) {
         String location = this._location.getText().trim();
         if (this._eventData.getLocation() == null || !this._eventData.getLocation().equals(location)) {
            this._eventData.setLocation(location);
         }
      }

      this.getTimeBlock();
      if (this._freeBusyField.isDirty()) {
         byte freeBusySetting = 2;
         if (this._freeBusyField == this._pencilIn) {
            if (this._pencilIn.getChecked()) {
               freeBusySetting = 1;
            }
         } else {
            freeBusySetting = (byte)this._showTimeAs.getSelectedIndex();
         }

         this._eventData.setFreeBusy(freeBusySetting);
      }

      Field[] externalFields = this._externalFields1;
      RIMModel[] externalModels = this._externalModels1;
      int max = externalModels.length;

      for (int i = 0; i < max; i++) {
         RIMModel rm = externalModels[i];
         ((FieldProvider)rm).grabDataFromField(externalFields[i], null);
      }

      if ((!this._calendarService.getCICALConfiguration().isRecurrencePatternEditDisabled() || this._newEvent)
         && this._allowRecur
         && (this.isRecurrenceDirty() || this._allDayFlag.isDirty() || this._timeZone.isDirty() || this._start.isDirty())) {
         this._recurCopy.setFirstDayOfWeek(CalendarOptions.getOptions().getFirstDayOfWeek());
         if (this._recurCopy.getRecurType() != 0) {
            this._eventData.setRecurrence(this._recurCopy);
         } else {
            this._eventData.setRecurrence(null);
         }
      }

      if (this._notes.isDirty()) {
         String notes = this._notes.getText().trim();
         if (this._eventData.getNotes() == null || !this._eventData.getNotes().equals(notes)) {
            this._eventData.setNotes(notes);
         }
      }

      externalFields = this._externalFields2;
      externalModels = this._externalModels2;
      max = externalModels.length;

      for (int var14 = 0; var14 < max; var14++) {
         RIMModel rm = externalModels[var14];
         ((FieldProvider)rm).grabDataFromField(externalFields[var14], null);
      }

      this._eventData.setSensitivity((byte)(this._sensitivityField.getChecked() ? 2 : 0));
      this.sendUsingFieldChanged();
   }

   private void sendUsingFieldChanged() {
      if (this._sendUsingFieldVisible) {
         CalendarChoice choice = (CalendarChoice)this._sendUsingField.getChoice(this._sendUsingField.getSelectedIndex());
         CalendarKey calendarKey = choice.getCalendarKey();
         this._eventData.setCalendarKey(calendarKey);
      }
   }

   private void scheduleUpdateLookupFields() {
      if (this._updateLookupFields.notStarted()) {
         this._uiApp.invokeLater(this._updateLookupFields);
      }
   }

   private void updateLookupFields() {
      VerticalFieldManager fieldManager = locateMeetingInfoFields(this.getScreen());
      if (fieldManager != null) {
         int fieldCount = this.getFieldCount();
         Field[] fields = new Object[fieldCount];

         for (int j = 0; j < fieldCount; j++) {
            fields[j] = this.getField(j);
         }

         for (int i = 0; i < fieldCount; i++) {
            Field currField = fields[i];
            Object cookie = currField.getCookie();
            if (cookie instanceof AttendeeModel) {
               AttendeeModel newAttendee = (AttendeeModel)cookie;
               RIMModel address = (RIMModel)newAttendee.getAddress();
               if (address instanceof Object) {
                  ResolvedStatusProvider resolvedStatus = (ResolvedStatusProvider)address;
                  if (resolvedStatus.isResolved()) {
                     Object resolvedAddress = resolvedStatus.getResolvedSubItem();
                     newAttendee = (AttendeeModel)AttendeeFactory.createAttendee(1, resolvedAddress);
                  }

                  if (address instanceof Object) {
                     Field newField = MeetingUtilities.createAttendeeField(newAttendee, null);
                     if (newField != null) {
                        fieldManager.insert(newField, currField.getIndex());
                        fieldManager.delete(currField);
                     }
                  }
               }
            }
         }
      }
   }

   private void getTimeBlock() {
      if (!this._timeBlockCurrent) {
         boolean allDay = this._allDayFlag.getChecked();
         this._eventData.setAllDayFlag(allDay);
         this._eventData.setTimeZoneID(this._sharedTZ.getID());
         this.validate(1);
         if (!allDay) {
            this._eventData.setStartDate(this._start.getDate(), this._sharedTZ);
            this._eventData.setInstanceDuration(this._duration.getDuration());
         } else {
            Calendar cal = this._cal;
            cal.setTimeZone(_gmtTZ);
            ((CalendarExtensions)cal).setTimeLong(this._start.getDate());
            DateTimeUtilities.zeroCalendarTime(cal);
            long start = ((CalendarExtensions)cal).getTimeLong();
            ((CalendarExtensions)cal).setTimeLong(this._end.getDate());
            DateTimeUtilities.zeroCalendarTime(cal);
            long end = ((CalendarExtensions)cal).getTimeLong();
            this._eventData.setStartDate(start, _gmtTZ);
            this._eventData.setInstanceDuration(end - start + 86400000);
         }

         this._timeBlockCurrent = true;
      }
   }

   private static VerticalFieldManager locateMeetingInfoFields(Field field) {
      if (field instanceof Object) {
         VerticalFieldManager vfm = (VerticalFieldManager)field;
         if (field.getCookie() instanceof Object) {
            if (vfm.getFieldCount() > 0) {
               return (VerticalFieldManager)field;
            }

            return null;
         }
      }

      if (field instanceof Object) {
         Manager manager = (Manager)field;
         int fieldCount = manager.getFieldCount();

         for (int i = fieldCount - 1; i >= 0; i--) {
            Field subField = manager.getField(i);
            VerticalFieldManager vfm = locateMeetingInfoFields(subField);
            if (vfm != null) {
               return vfm;
            }
         }
      }

      return null;
   }

   private int getMeetingAttendeeCount() {
      int attendeeCount = 0;
      int numFields = this._meetingInfoField.getFieldCount();

      for (int i = 0; i < numFields; i++) {
         if (this._meetingInfoField.getField(i).getCookie() instanceof Object) {
            attendeeCount++;
         }
      }

      return attendeeCount;
   }

   @Override
   protected boolean onSave() {
      if (this._saveVerb != null) {
         this.getMeetingVerbs();
         Object result = this._saveVerb.invoke(null);
         this.verbInvoked(this._saveVerb, null, result);
      }

      return false;
   }

   @Override
   public boolean openProductionBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1297045061:
            if (this._newEvent) {
               Dialog.alert("Cannot move a new appointment.");
               return true;
            } else if (this._eventData == null) {
               Dialog.alert("Event data is NULL.  Unable to move.");
               return true;
            } else {
               CalendarService defaultCalendarService = this._calendarServiceManager.getDefaultCalendarService();
               if (defaultCalendarService.getUniqueServiceID() == this._eventData.getCalendarKey().getCalendarServiceID()) {
                  Dialog.alert("Event is already in default calendar.");
                  return true;
               }

               int result = Dialog.ask(
                  3,
                  ((StringBuffer)(new Object("Move this appointment to the current default calendar, ")))
                     .append(defaultCalendarService.getServiceName())
                     .append("?")
                     .toString()
               );
               if (result == 4) {
                  EventUtilities.moveEvent(this._eventData, null, defaultCalendarService, true, false);
                  Dialog.alert("Appointment moved.");
               }

               return true;
            }
         case 1447642455:
            if (!this._debugActive) {
               String status = "NO DATA";
               OTACalendarSyncDataManager syncManager = OTACalendarSyncDataManager.getInstance();
               int statusCode = syncManager.getTransmissionStatusForEvent(this._eventData);
               switch (statusCode) {
                  case -1:
                     break;
                  case 0:
                     status = "UNSENT";
                     break;
                  case 1:
                     status = "SENT";
                     break;
                  case 2:
                     status = "SENDING";
                     break;
                  case 3:
                     status = "ERROR";
                     break;
                  case 4:
                  default:
                     status = "QUEUED";
               }

               this._debugGMESendStatus.setText(((StringBuffer)(new Object("GME Send Status: "))).append(status).toString());
               this.insert(this._debugBlock, 0);
               this._debugBlock.setFocus();
               this._debugActive = true;
            }

            return true;
         default:
            return super.openProductionBackdoor(backdoorCode);
      }
   }

   @Override
   public boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1178748482:
         default:
            if (this._uiApp != null) {
               this._uiApp.pushScreen((Screen)(new Object(this._eventData.getMeetingInfo(), this._start.getDate(), this._end.getDate())));
               return true;
            }
         case 1178748481:
            return super.openDevelopmentBackdoor(backdoorCode);
      }
   }

   private void addSendUsingField(Manager section) {
      if (!this._sendUsingFieldVisible && this._sendUsingField != null && this._calendarServiceManager.getCalendarServices().length > 1) {
         if (!this._newEvent) {
            this._sendUsingField.setEditable(false);
         }

         section.add(this._sendUsingField);
         this._sendUsingFieldVisible = true;
      }
   }

   private ObjectChoiceField getCalendarServiceChoices() {
      ResourceBundle rb = _rb;
      boolean includeBaseCalendar = !this._newEvent;
      ServiceIdentifier[] calendarServices = this._calendarServiceManager.getCalendarServices(includeBaseCalendar, true);
      if (calendarServices != null && calendarServices.length > (includeBaseCalendar ? 1 : 0)) {
         int initialIndex = 0;
         int currentIndex = 0;
         CalendarChoice[] calendarChoices = new CalendarChoice[0];
         CalendarOptions options = CalendarOptions.getOptions();

         for (int i = 0; i < calendarServices.length; i++) {
            CalendarService service = (CalendarService)calendarServices[i];
            ServiceRecord serviceRecord = service.getServiceRecord();
            if (!this._newEvent || serviceRecord != null && serviceRecord.getType() == 0) {
               for (Enumeration calendarFolders = service.getCalendarFolders().elements(); calendarFolders.hasMoreElements(); currentIndex++) {
                  CalendarFolder folder = (CalendarFolder)calendarFolders.nextElement();
                  CalendarKey calendarKey = (CalendarKey)(new Object(service.getUniqueServiceID(), folder.getFolderID()));
                  String calendarDescription = ((StringBuffer)(new Object())).append(service.getServiceName()).append(folder.getFolderNameSuffix()).toString();
                  int color = options.getCalendarColour(calendarKey);
                  CalendarChoice calendarChoice = new CalendarChoice(calendarKey, calendarDescription, color);
                  Arrays.add(calendarChoices, calendarChoice);
                  if (this._calendarService.getUniqueServiceID() == service.getUniqueServiceID() && this._calendarFolder.getFolderID() == folder.getFolderID()) {
                     initialIndex = currentIndex;
                  }
               }
            }
         }

         boolean meetingModifiable = false;
         MeetingInfo meetingInfo = ((EventImpl)this._eventData).getMeetingInfo();
         if (meetingInfo != null) {
            meetingModifiable = meetingInfo.meetingCanBeModified();
         }

         String serviceChoiceFieldString = !this._newEvent && !meetingModifiable ? rb.getString(652) : rb.getString(648);
         ObjectChoiceField serviceChoiceField = new EventViewer$CalendarChoiceField(serviceChoiceFieldString, calendarChoices, initialIndex);
         serviceChoiceField.setTag(SECTION_AREA_TAG);
         return serviceChoiceField;
      } else {
         return null;
      }
   }
}
