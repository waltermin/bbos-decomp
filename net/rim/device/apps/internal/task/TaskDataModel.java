package net.rim.device.apps.internal.task;

import java.util.TimeZone;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.DescriptionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.api.reminders.ReminderModelAbsolute;
import net.rim.device.apps.api.reminders.ReminderModelRelative;
import net.rim.device.apps.api.task.TaskModel;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.framework.RecurUtil;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.internal.commonmodels.pim.RecurImpl;
import net.rim.device.apps.internal.commonmodels.pim.RecurrenceField;
import net.rim.device.apps.internal.task.resources.TaskResources;
import net.rim.device.cldc.util.TimeService;
import net.rim.vm.Array;

final class TaskDataModel implements FieldProvider, VerbProvider, ConversionProvider, CloneProvider, FieldChangeListener, PersistableRIMModel {
   private int _priority = 1;
   private int _status = 0;
   private long _taskStartTime = Long.MIN_VALUE;
   private long _recurrenceStartTime = Long.MIN_VALUE;
   private long _dueDate = Long.MIN_VALUE;
   private ReminderModel _reminderModel;
   private RecurImpl _recurrenceModel;
   private String _timeZoneID;
   static RIMModelFactory _reminderFactory;

   public final void changeReminderType(byte type) {
      if (type == 2) {
         this._reminderModel = (ReminderModel)_reminderFactory.createInstance(new Byte((byte)2));
         this._reminderModel.setTime(-1);
      } else {
         if (type == 1) {
            this._reminderModel = (ReminderModel)_reminderFactory.createInstance(new Byte((byte)1));
            this._reminderModel.setTime(-1);
         }
      }
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      ContextObject contextObject = ContextObject.castOrCreate(context);
      Verb defaultVerb = null;
      if (!ContextObject.getFlag(context, 87)) {
         TaskModel taskModel = (TaskModel)contextObject.get(-1982408962600516813L);
         if (!contextObject.getFlag(0)) {
            Array.resize(verbs, verbs.length + 1);
            if (this._status != 2) {
               verbs[verbs.length - 1] = ChangeStatusVerb.getInstance(taskModel, 615680, 22, 2);
               if (this._status == 0) {
                  Array.resize(verbs, verbs.length + 1);
                  verbs[verbs.length - 1] = ChangeStatusVerb.getInstance(taskModel, 615696, 23, 1);
               }
            } else {
               verbs[verbs.length - 1] = ChangeStatusVerb.getInstance(taskModel, 615696, 23, 1);
            }
         }
      }

      return defaultVerb;
   }

   public final void setPriority(int priority) {
      this._priority = priority;
   }

   public final int getStatus() {
      return this._status;
   }

   public final void setStatus(int status) {
      this._status = status;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 28) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         syncBuffer.addInt(9, this._status, 4);
         syncBuffer.addInt(10, this._priority, 4);
         String tzID = this.getTimeZoneID();
         TimeZone tz = TimeZone.getTimeZone(tzID);
         int tzId = TimeService.getTimeService().getSerialSyncID(this.getTimeZoneID());
         syncBuffer.addInt(16, tzId, 4);
         if (this.getStartDate() != Long.MIN_VALUE) {
            long startDate = TaskUtilities.convertFromGMT(this.getStartDate(), tz);
            syncBuffer.addInt(6, DateTimeUtilities.convertMillisecondsToEpoch(startDate), 4);
         }

         if (this.hasDueDate()) {
            syncBuffer.addInt(8, 1, 4);
            long dt = TaskUtilities.convertFromGMT(this.getDueDate(), tz);
            syncBuffer.addInt(5, DateTimeUtilities.convertMillisecondsToEpoch(dt), 4);
         } else {
            syncBuffer.addInt(8, 0, 4);
         }

         if (this._reminderModel.hasReminder()) {
            int state = this._reminderModel.getState();
            if (state == 5 || state == 6) {
               state = 4;
            }

            syncBuffer.addInt(18, state, 4);
            long reminderDelta = 0;
            if (this._reminderModel.getType() == 1) {
               reminderDelta = this._reminderModel.getTime();
               syncBuffer.addInt(14, 2, 4);
               long tmp = TaskUtilities.convertFromGMT(this.getDueDate() - reminderDelta, tz);
               syncBuffer.addInt(15, DateTimeUtilities.convertMillisecondsToEpoch(tmp), 4);
            } else {
               reminderDelta = TaskUtilities.convertFromGMT(this._reminderModel.getTime(), tz);
               syncBuffer.addInt(14, 1, 4);
               syncBuffer.addInt(15, DateTimeUtilities.convertMillisecondsToEpoch(reminderDelta), 4);
            }
         }

         DataBuffer buffer = syncBuffer.getDataBuffer();
         if (this._recurrenceModel.getRecurType() != 0) {
            RecurUtil.serializeRecurInfo(this.getRecurrenceStartDate(), TimeZone.getTimeZone(this.getTimeZoneID()), this._recurrenceModel, false, buffer);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final Object clone(Object context) {
      TaskDataModel taskDataModel = new TaskDataModel(null);
      taskDataModel.setPriority(this._priority);
      taskDataModel.setStatus(this._status);
      taskDataModel.setReminderModel((ReminderModel)((Copyable)this._reminderModel).copy());
      taskDataModel.setRecurrenceModel((Recur)this._recurrenceModel.clone(context));
      taskDataModel.setStartDate(this._taskStartTime);
      taskDataModel.setRecurrenceStartDate(this._recurrenceStartTime);
      taskDataModel.setDueDate(this._dueDate);
      taskDataModel.setTimeZoneID(this._timeZoneID);
      return taskDataModel;
   }

   public final long getRecurrenceStartDate() {
      return this._recurrenceStartTime;
   }

   public final void setRecurrenceStartDate(long time) {
      this._recurrenceStartTime = time;
   }

   public final void setDueDate(long date) {
      this._dueDate = date;
   }

   public final long getDueDate() {
      return this._dueDate;
   }

   public final ReminderModel getReminderModel() {
      return this._reminderModel;
   }

   public final void setReminderModel(ReminderModel reminderModel) {
      this._reminderModel = reminderModel;
   }

   public final Recur getRecurrenceModel() {
      return this._recurrenceModel;
   }

   public final void setRecurrenceModel(Recur recurrenceModel) {
      this._recurrenceModel = (RecurImpl)recurrenceModel;
   }

   public final String getTimeZoneID() {
      return this._timeZoneID;
   }

   public final void setTimeZoneID(String timeZoneID) {
      this._timeZoneID = timeZoneID;
   }

   public final boolean isRecurring() {
      if (this._recurrenceModel instanceof Recur) {
         Recur recurInfo = this._recurrenceModel;
         return recurInfo.getRecurType() != 0;
      } else {
         return false;
      }
   }

   public final boolean hasDueDate() {
      return this.getDueDate() != Long.MIN_VALUE;
   }

   public final int getPriority() {
      return this._priority;
   }

   public final long getStartDate() {
      return this._taskStartTime;
   }

   final DateField getDueDateField() {
      long timeStamp = this.hasDueDate() ? this._dueDate : TaskUtilities.convertToMidnightGMT(System.currentTimeMillis(), 1) + 61200000;
      DateField df = new DateField("", timeStamp, TaskUtilities._dateAndTimeFormat);
      df.setTimeZone(TaskUtilities._gmtCalendar.getTimeZone());
      df.setChangeListener(this);
      return df;
   }

   final ObjectChoiceField getTimeZoneField() {
      ObjectChoiceField tzcf = new ObjectChoiceField(CommonResources.getString(2013), TimeService.getTimeService().getTimeZoneNamesShort(), 0, 134217728);
      tzcf.setSelectedIndex(TimeService.getTimeService().getTimeZoneIndex(this._timeZoneID));
      return tzcf;
   }

   final Field getRecurrenceField(Object context) {
      return this._recurrenceModel.getField(context);
   }

   final Field getReminderChoiceField(boolean includeRelative) {
      String label = "";
      if (this._reminderModel instanceof DescriptionProvider) {
         label = ((DescriptionProvider)this._reminderModel).getStringForField(0);
      }

      Object[] reminderChoices;
      if (!this.hasDueDate() && !includeRelative) {
         reminderChoices = new Object[]{TaskResources.getString(3), TaskResources.getString(9)};
      } else {
         reminderChoices = new Object[]{TaskResources.getString(3), TaskResources.getString(9), TaskResources.getString(48)};
      }

      int choice = 0;
      if (this._reminderModel.getType() == 2 && this._reminderModel.hasReminder()) {
         choice = 1;
      } else if (this._reminderModel.getType() == 1 && this._reminderModel.hasReminder()) {
         choice = 2;
      }

      ObjectChoiceField rcf = new ObjectChoiceField(label, reminderChoices, choice);
      rcf.setCookie(this._reminderModel);
      rcf.setChangeListener(this);
      return rcf;
   }

   public final ReminderModel getReminderData() {
      return this.getReminderModel();
   }

   public final void setStartDate(long time) {
      this._taskStartTime = time;
   }

   final Field getReminderField(Object context) {
      if (this._reminderModel instanceof FieldProvider && this._reminderModel.hasReminder()) {
         ContextObject contextObject = ContextObject.clone(context);
         contextObject.setFlag(1);
         contextObject.setFlag(60);
         return ((FieldProvider)this._reminderModel).getField(contextObject);
      } else {
         return null;
      }
   }

   @Override
   public final int getOrder(Object context) {
      return 13200;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      boolean result = true;
      VerticalFieldManager mainVFM = (VerticalFieldManager)field;
      VerticalFieldManager dueDateVFM = (VerticalFieldManager)mainVFM.getField(2);
      ChoiceField cf = (ChoiceField)dueDateVFM.getField(0);
      long dueDate = -1;
      ObjectChoiceField tzcf = (ObjectChoiceField)mainVFM.getField(3);
      String tzid = TimeService.getTimeService().getTimeZoneIDFromIndex(tzcf.getSelectedIndex());
      if (cf.getSelectedIndex() != 0) {
         DateField df = (DateField)dueDateVFM.getField(1);
         dueDate = TaskUtilities.convertFromGMT(df.getDate(), TimeZone.getTimeZone(tzid));
         RecurrenceField rfield = (RecurrenceField)mainVFM.getField(mainVFM.getFieldCount() - 1);
         if (dueDate < System.currentTimeMillis()) {
            Dialog.alert(TaskResources.getString(45));
            result = true;
         } else {
            RecurrenceField rField = (RecurrenceField)mainVFM.getField(mainVFM.getFieldCount() - 1);
            result = rField.validate();
         }
      }

      VerticalFieldManager reminderVFM = (VerticalFieldManager)mainVFM.getField(4);
      ChoiceField reminderChoiceField = (ChoiceField)reminderVFM.getField(0);
      Recur recurInfo = null;
      Field recurrenceField = mainVFM.getField(mainVFM.getFieldCount() - 1);
      if (recurrenceField instanceof RecurrenceField) {
         recurInfo = ((RecurrenceField)recurrenceField).getRecurrenceInfo();
      }

      if (result && reminderChoiceField.getSelectedIndex() > 0 && (recurInfo == null || recurInfo.getRecurType() == 0)) {
         Field reminderField = reminderVFM.getField(1);
         ((FieldProvider)this._reminderModel).grabDataFromField(reminderField, null);
         long time = 0;
         if (this._reminderModel instanceof ReminderModelRelative) {
            time = dueDate - this._reminderModel.getTime();
         } else if (this._reminderModel instanceof ReminderModelAbsolute) {
            time = TaskUtilities.convertFromGMT(this._reminderModel.getTime(), TimeZone.getTimeZone(tzid));
         }

         long currentTimeTrimmed = System.currentTimeMillis() / 1000;
         currentTimeTrimmed = (currentTimeTrimmed - currentTimeTrimmed % 60) * 1000;
         if (time < currentTimeTrimmed) {
            result = Dialog.ask(3, TaskResources.getString(54), 4) == 4;
            if (result) {
               reminderChoiceField.setSelectedIndex(0);
               this._reminderModel.setTime(-1);
               return result;
            }

            result = false;
         }
      }

      return result;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      VerticalFieldManager mainVFM = (VerticalFieldManager)field;
      ChoiceField cf = (ChoiceField)mainVFM.getField(0);
      this.setStatus(cf.getSelectedIndex());
      cf = (ChoiceField)mainVFM.getField(1);
      this.setPriority(cf.getSelectedIndex());
      VerticalFieldManager dueDateVFM = (VerticalFieldManager)mainVFM.getField(2);
      ObjectChoiceField tzcf = (ObjectChoiceField)mainVFM.getField(3);
      this.setTimeZoneID(TimeService.getTimeService().getTimeZoneIDFromIndex(tzcf.getSelectedIndex()));
      VerticalFieldManager reminderVFM = (VerticalFieldManager)mainVFM.getField(4);
      cf = (ChoiceField)dueDateVFM.getField(0);
      if (cf.getSelectedIndex() != 0) {
         DateField df = (DateField)dueDateVFM.getField(1);
         this.setDueDate(df.getDate());
         Field recurrenceField = mainVFM.getField(mainVFM.getFieldCount() - 1);
         this._recurrenceModel.grabDataFromField(recurrenceField, context);
      } else {
         this.setDueDate(Long.MIN_VALUE);
      }

      Recur recurInfo = this.getRecurrenceModel();
      if (recurInfo.getRecurType() != 0) {
         ContextObject co = ContextObject.castOrCreate(context);
         if (co.getFlag(31)) {
            this.setStartDate(this.getDueDate());
            this.setRecurrenceStartDate(this.getDueDate());
         }
      }

      ChoiceField reminderChoiceField = (ChoiceField)reminderVFM.getField(0);
      if (reminderChoiceField.getSelectedIndex() > 0) {
         Field reminderField = reminderVFM.getField(1);
         ((FieldProvider)this._reminderModel).grabDataFromField(reminderField, context);
         this.setStartDate(this._dueDate);
      }

      return true;
   }

   @Override
   public final Field getField(Object context) {
      ContextObject co = ContextObject.castOrCreate(context);
      VerticalFieldManager mainVFM = new VerticalFieldManager();
      VerticalFieldManager reminderVFM = new VerticalFieldManager();
      Object[] inChoices = null;
      int status = this.getStatus();
      if (TaskUtilities.getCICALConfiguration().isInfiniteRecurrenceAllowed()) {
         inChoices = new Object[]{
            TaskResources.getString(16), TaskResources.getString(17), TaskResources.getString(18), TaskResources.getString(19), TaskResources.getString(20)
         };
      } else {
         inChoices = new Object[]{TaskResources.getString(16), TaskResources.getString(17), TaskResources.getString(18)};
         if (status > 2) {
            status = 1;
         }
      }

      ObjectChoiceField ocf = new ObjectChoiceField(TaskResources.getString(33), inChoices, status);
      ocf.setCookie(context);
      mainVFM.add(ocf);
      inChoices = new Object[]{TaskResources.getString(14), TaskResources.getString(12), TaskResources.getString(13)};
      ocf = new ObjectChoiceField(TaskResources.getString(15), inChoices, this.getPriority());
      mainVFM.add(ocf);
      inChoices = new Object[]{TaskResources.getString(3), TaskResources.getString(9)};
      ocf = new ObjectChoiceField(TaskResources.getString(10), inChoices, this.hasDueDate() ? 1 : 0);
      ocf.setChangeListener(this);
      VerticalFieldManager dueDateVFM = new VerticalFieldManager(1152921504606846976L);
      dueDateVFM.add(ocf);
      if (this.hasDueDate()) {
         dueDateVFM.add(this.getDueDateField());
      }

      dueDateVFM.setCookie(this);
      mainVFM.add(dueDateVFM);
      ObjectChoiceField timeZoneField = this.getTimeZoneField();
      mainVFM.add(timeZoneField);
      Field reminderChoiceField = this.getReminderChoiceField(this.hasDueDate());
      Field reminderField = this.getReminderField(co);
      reminderChoiceField.setCookie(this.getReminderModel());
      reminderVFM.add(reminderChoiceField);
      if (reminderField != null) {
         reminderVFM.add(reminderField);
      }

      mainVFM.add(reminderVFM);
      if (this.hasDueDate()) {
         String tzid = TimeService.getTimeService().getTimeZoneIDFromIndex(timeZoneField.getSelectedIndex());
         long startDate = this.getStartDate();
         if (this.isRecurring()) {
            startDate = this.getRecurrenceStartDate();
         }

         if (startDate == Long.MIN_VALUE) {
            startDate = this.getDueDate();
         }

         Long startDateLong = new Long(startDate);
         ContextObject.put(co, 4143325197084129318L, startDateLong);
      }

      ContextObject.put(co, 853966984731399007L, this._timeZoneID);
      Field recurrenceField = this.getRecurrenceField(co);
      if (this.hasDueDate() && recurrenceField != null) {
         recurrenceField.setCookie(this.getRecurrenceModel());
         recurrenceField.setDirty(false);
         mainVFM.add(recurrenceField);
      }

      mainVFM.setCookie(this);
      return mainVFM;
   }

   @Override
   public final void fieldChanged(Field f, int context) {
      if ((context & -2147483648) == 0) {
         if (!(f instanceof ObjectChoiceField)) {
            if (f instanceof DateField) {
               DateField df = (DateField)f;
               this.dueDateChanged(df);
            }
         } else {
            ObjectChoiceField cf = (ObjectChoiceField)f;
            if (cf.getCookie() instanceof ReminderModel) {
               this.reminderOptionChanged(cf);
            } else {
               this.dueOptionChanged(cf);
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   TaskDataModel(Object initialData) {
      this.changeReminderType((byte)2);
      this._timeZoneID = TimeZone.getDefault().getID();
      this._recurrenceModel = new RecurImpl();
      if (ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         boolean var10 = false /* VF: Semaphore variable */;

         try {
            var10 = true;
            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(10)) {
               int priority = syncBuffer.getInt(10, true);
               if (priority < 0 || priority > 2) {
                  priority = 1;
               }

               this.setPriority(priority);
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(9)) {
               int status = syncBuffer.getInt(9, true);
               if (status < 0 || status > 4) {
                  status = 0;
               }

               this.setStatus(status);
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(16)) {
               DataBuffer data = syncBuffer.getDataBuffer();
               short shortData = data.readShort();
               data.skipBytes(1);
               shortData = data.readShort();
               String stringData = TimeService.getTimeService().getTimeZoneIDFromSerialSyncID(shortData);
               if (stringData != null) {
                  String timeZoneID = stringData;
                  this.setTimeZoneID(timeZoneID);
               }

               syncBuffer.getBytes(0, 16, true);
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(6)) {
               long date = Long.MIN_VALUE;
               switch (syncBuffer.getVersion()) {
                  case -1:
                     break;
                  case 0:
                  case 1:
                  default:
                     date = syncBuffer.getDateGMT(6, true);
                     break;
                  case 2:
                     int epochTime = syncBuffer.getInt(0, 6, true);
                     date = TaskUtilities.convertToGMT(DateTimeUtilities.convertEpochToMilliseconds(epochTime));
               }

               this.setStartDate(date);
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(8)) {
               int index = syncBuffer.getInt(0, 8, true);
               long date = Long.MIN_VALUE;
               if (index == 1) {
                  switch (syncBuffer.getVersion()) {
                     case -1:
                        break;
                     case 0:
                     case 1:
                     default:
                        date = syncBuffer.getDateGMT(5, true);
                        break;
                     case 2:
                        int epochTime = syncBuffer.getInt(0, 5, true);
                        date = TaskUtilities.convertToGMT(DateTimeUtilities.convertEpochToMilliseconds(epochTime));
                  }
               }

               this.setDueDate(date);
            }

            syncBuffer.setPosition(0);
            int reminderType = 0;
            if (syncBuffer.containsType(14)) {
               reminderType = syncBuffer.getInt(14, true);
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(15)) {
               int epochTime = syncBuffer.getInt(15, true);
               if (reminderType != 0) {
                  long reminderTime = DateTimeUtilities.convertEpochToMilliseconds(epochTime);
                  reminderTime = TaskUtilities.convertToGMT(reminderTime);
                  if (reminderType == 1) {
                     this.changeReminderType((byte)2);
                  } else if (reminderType == 2) {
                     this.changeReminderType((byte)1);
                     reminderTime = this.getDueDate() - reminderTime;
                  }

                  ReminderModel rm = this.getReminderModel();
                  if (rm != null) {
                     rm.setTime(reminderTime);
                  }

                  syncBuffer.setPosition(0);
                  if (syncBuffer.containsType(18)) {
                     int state = syncBuffer.getInt(18, true);
                     if (rm != null) {
                        rm.setState(state);
                     }
                  }
               }
            }

            int exclusionRecordOffset = -1;
            int inclusionRecordOffset = -1;
            int rstart = -1;
            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(12)) {
               int recurrenceOffset = syncBuffer.getPosition() + 3;
               syncBuffer.setPosition(0);
               if (syncBuffer.containsType(13)) {
                  exclusionRecordOffset = syncBuffer.getPosition();
               }

               syncBuffer.setPosition(0);
               if (syncBuffer.containsType(32)) {
                  inclusionRecordOffset = syncBuffer.getPosition();
               }

               Recur recurInfo = this.getRecurrenceModel();
               syncBuffer.setPosition(recurrenceOffset);
               rstart = RecurUtil.parseRecurInfo(
                  recurInfo,
                  false,
                  TimeZone.getTimeZone(this.getTimeZoneID()),
                  syncBuffer.getDataBuffer(),
                  recurrenceOffset,
                  exclusionRecordOffset,
                  inclusionRecordOffset
               );
               syncBuffer.getBytes(0, 12, true);
               if (rstart != -1) {
                  this.setRecurrenceStartDate(DateTimeUtilities.convertEpochToMilliseconds(rstart));
               }
            }

            syncBuffer.setPosition(0);
            if (syncBuffer.containsType(31)) {
               int fdow = syncBuffer.getInt(0, 31, true) + 1;
               if (fdow < 1 || fdow > 7) {
                  fdow = 1;
               }

               Recur recurInfo = this.getRecurrenceModel();
               recurInfo.setFirstDayOfWeek(fdow);
            }

            if (this.getStartDate() == Long.MIN_VALUE) {
               if (rstart == -1) {
                  this.setStartDate(this.getDueDate());
                  return;
               }

               this.setRecurrenceStartDate(DateTimeUtilities.convertEpochToMilliseconds(rstart));
               var10 = false;
            } else {
               var10 = false;
            }
         } finally {
            if (var10) {
               EventLogger.logEvent(-1576052272418032312L, 1162040900, 2);
               return;
            }
         }
      }
   }

   private final void dueDateChanged(DateField df) {
      VerticalFieldManager vfm = (VerticalFieldManager)df.getManager();
      if (vfm.getCookie() != null) {
         VerticalFieldManager mainVFM = (VerticalFieldManager)vfm.getManager();
         long dueDate = df.getDate();
         RecurrenceField rfield = (RecurrenceField)mainVFM.getField(mainVFM.getFieldCount() - 1);
         rfield.setNewStartDate(TaskUtilities.convertFromGMT(dueDate, rfield.getTimeZone()), 0);
      }
   }

   private final void reminderOptionChanged(ObjectChoiceField cf) {
      VerticalFieldManager reminderVFM = (VerticalFieldManager)cf.getManager();
      VerticalFieldManager mainVFM = (VerticalFieldManager)reminderVFM.getManager();
      int index = cf.getSelectedIndex();
      int fieldCount = reminderVFM.getFieldCount();
      if (fieldCount > 1) {
         reminderVFM.delete(reminderVFM.getField(1));
      }

      ReminderModel rm = null;
      TaskDataModel taskDataModel = (TaskDataModel)mainVFM.getCookie();
      if (taskDataModel != null) {
         rm = taskDataModel.getReminderModel();
         if (index == 0) {
            if (rm != null) {
               rm.setTime(-1);
               return;
            }
         } else {
            if (index == 1) {
               taskDataModel.changeReminderType((byte)2);
               rm = taskDataModel.getReminderModel();
               long timeStamp = TaskUtilities.convertToMidnight(System.currentTimeMillis(), 1) + 28800000;
               timeStamp = TaskUtilities.convertToGMT(timeStamp);
               rm.setTime(timeStamp);
            } else if (index == 2) {
               taskDataModel.changeReminderType((byte)1);
               rm = taskDataModel.getReminderModel();
               rm.setTime(900000);
            }

            if (rm instanceof FieldProvider) {
               ContextObject contextObject = new ContextObject(1, 60);
               Field reminderField = taskDataModel.getReminderField(contextObject);
               if (reminderField != null) {
                  reminderVFM.add(reminderField);
               }
            }
         }
      }
   }

   private final void dueOptionChanged(ObjectChoiceField cf) {
      Field original = cf.getOriginal();
      VerticalFieldManager dueDateVFM = (VerticalFieldManager)original.getManager();
      VerticalFieldManager mainVFM = (VerticalFieldManager)dueDateVFM.getManager();
      Field field = mainVFM.getField(0);
      ContextObject context = (ContextObject)field.getCookie();
      int index = cf.getSelectedIndex();
      int fieldCount = dueDateVFM.getFieldCount();
      if (fieldCount > 1) {
         dueDateVFM.delete(dueDateVFM.getField(1));
      }

      TaskDataModel taskDataModel = (TaskDataModel)mainVFM.getCookie();
      ObjectChoiceField tzcf = (ObjectChoiceField)mainVFM.getField(3);
      Field rfield = mainVFM.getField(mainVFM.getFieldCount() - 1);
      if (index != 1) {
         if (index == 0) {
            mainVFM.delete(rfield);
            VerticalFieldManager reminderVFM = (VerticalFieldManager)mainVFM.getField(mainVFM.getFieldCount() - 1);
            ObjectChoiceField reminderChoiceField = (ObjectChoiceField)reminderVFM.getField(0);
            Field reminderField = null;
            if (reminderVFM.getFieldCount() > 1) {
               reminderField = reminderVFM.getField(1);
            }

            if (reminderChoiceField.getSelectedIndex() == 2) {
               this.changeReminderType((byte)2);
               reminderField = this.getReminderField(context);
            }

            reminderChoiceField = (ObjectChoiceField)this.getReminderChoiceField(false);
            reminderVFM.deleteAll();
            reminderVFM.add(reminderChoiceField);
            if (reminderField != null) {
               reminderVFM.add(reminderField);
            }

            this.setDueDate(Long.MIN_VALUE);
         }
      } else {
         VerticalFieldManager reminderVFM = (VerticalFieldManager)rfield;
         String tzid = TimeService.getTimeService().getTimeZoneIDFromIndex(tzcf.getSelectedIndex());
         DateField df = taskDataModel.getDueDateField();
         dueDateVFM.add(df);
         long dueDate = TaskUtilities.convertFromGMT(df.getDate(), TimeZone.getTimeZone(tzid));
         Long dueDateLong = new Long(dueDate);
         ContextObject.put(context, 4143325197084129318L, dueDateLong);
         rfield = taskDataModel.getRecurrenceField(context);
         mainVFM.add(rfield);
         Field reminderChoiceField = this.getReminderChoiceField(true);
         Field reminderField = null;
         if (reminderVFM.getFieldCount() > 1) {
            reminderField = reminderVFM.getField(1);
         }

         reminderVFM.deleteAll();
         reminderChoiceField.setCookie(this.getReminderModel());
         reminderVFM.add(reminderChoiceField);
         if (reminderField != null) {
            reminderVFM.add(reminderField);
            return;
         }
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _reminderFactory = (RIMModelFactory)ar.get(813899564474876953L);
   }
}
