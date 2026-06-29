package net.rim.device.apps.internal.task;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.DescriptionProvider;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.ForwardAsVerb;
import net.rim.device.apps.api.messaging.messagelist.MessageAttachment;
import net.rim.device.apps.api.messaging.messagelist.MessagePartsProvider;
import net.rim.device.apps.api.pim.TimeBasedObject;
import net.rim.device.apps.api.reminders.Reminder;
import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.task.TaskModel;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.PriorityIcons;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.apps.internal.calendar.eventprovider.CalendarIcons;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;
import net.rim.device.apps.internal.task.resources.TaskResources;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.TextScrapeVisitor;
import net.rim.vm.Array;

final class TaskModelImpl
   implements TaskModel,
   SyncObject,
   PaintProvider,
   VerbProvider,
   ConversionProvider,
   KeyProvider,
   EditableProvider,
   EncryptableProvider,
   Reminder,
   CloneProvider,
   MatchProvider,
   TimeBasedObject,
   MessagePartsProvider,
   HotKeyProvider {
   private Object[] _subModels = new Object[0];
   int _uid = TaskCollectionImpl.getInstance().generateUniqueID();
   private static Recognizer _titleModelRecognizer = RecognizerRepository.getRecognizers(-4904857078378172834L);
   private static ContextObjectWR _taskSyncContextWR = (ContextObjectWR)(new Object(28, 19));
   private static final int ICON_COUNT = 5;
   private static final int MAX_TASK_NOTES_LENGTH = 4096;
   private static IconCollection STATUS_ICONS = IconCollection.get("net_rim_Task_Status", 5);
   public static IconCollection PRIORITY_ICONS = PriorityIcons.ICONS;
   protected static final IconCollection TASK_DETAIL_ICONS = CalendarIcons.CAL_STATUS_ICONS;
   static final int START_MILLIS_DEFAULT = 28800000;
   static final int END_MILLIS_DEFAULT = 61200000;
   static final long TIMEZONE_KEY = -553074023984152200L;
   private static int[] _hints = new int[0];
   private static byte[] taskId = new byte[]{116};

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context) {
      RIMModel titleModel = this.getTitleModel();
      Font font = g.getFont();
      int size = font.getHeight();
      y = VariableRowHeightProxy.getAdjustedY(context, y);
      int priorityWidth = PRIORITY_ICONS.getWidth(font);
      Image icon = PRIORITY_ICONS.getImage(Math.abs(this.getPriority() - 2));
      icon.paint(g, x, y, priorityWidth, size);
      int xoffset = priorityWidth;
      icon = STATUS_ICONS.getImage(this.getStatus());
      icon.paint(g, x + xoffset, y, size, size);
      xoffset += size;
      font = null;
      if (this.hasDueDate() && !this.isCompleted() && this.isOverDue()) {
         font = g.getFont();
         g.setFont(font.derive(1));
      }

      if (titleModel instanceof Object) {
         PaintProvider paintProvider = (PaintProvider)titleModel;
         xoffset += paintProvider.paint(g, x + xoffset, y, width - xoffset, height, context);
      }

      if (font != null) {
         g.setFont(font);
      }

      return xoffset;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      ContextObject contextObject = ContextObject.castOrCreate(context);
      contextObject.put(-1982408962600516813L, this);
      Verb defaultVerb = OpenTaskVerb.getInstance(this);
      Array.resize(verbs, verbs.length + 1);
      verbs[verbs.length - 1] = defaultVerb;
      if (ContextObject.getFlag(contextObject, 81)) {
         return verbs[verbs.length - 1];
      }

      if (!ContextObject.getFlag(contextObject, 36)) {
         ForwardAsVerb forwardAsVerb = (ForwardAsVerb)(new Object(this));
         if (forwardAsVerb.canInvoke(null)) {
            Array.resize(verbs, verbs.length + 1);
            verbs[verbs.length - 1] = forwardAsVerb;
         }
      }

      Verb[] subProviderVerbs = new Object[0];

      for (int i = this._subModels.length - 1; i >= 0; i--) {
         Object o = this._subModels[i];
         Verb result = null;
         if (o instanceof Object) {
            VerbProvider vp = (VerbProvider)o;
            Array.resize(subProviderVerbs, 0);
            result = vp.getVerbs(contextObject, subProviderVerbs);
            if (result != null) {
               defaultVerb = result;
            }

            if (subProviderVerbs.length > 0) {
               int oldLength = verbs.length;
               Array.resize(verbs, verbs.length + subProviderVerbs.length);

               for (int j = oldLength; j < verbs.length; j++) {
                  verbs[j] = subProviderVerbs[j - oldLength];
               }
            }
         }
      }

      if (ContextObject.get(contextObject, 2006691216517637157L) == null) {
         Array.resize(verbs, verbs.length + 1);
         verbs[verbs.length - 1] = new DeleteTaskVerb(this);
      }

      return defaultVerb;
   }

   @Override
   public final int match(Object criteria) {
      if (!(criteria instanceof Object)) {
         return Match.match(this, this, (Object[])criteria, _hints);
      } else {
         SearchCriterion crit = (SearchCriterion)criteria;
         if (crit.getType() == 24) {
            return crit.getValue() == this.getUID() ? 1 : 0;
         } else {
            return -1;
         }
      }
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   protected final String getTimeSummary() {
      String str = null;
      Calendar cal = Calendar.getInstance();
      TimeZone tz = cal.getTimeZone();
      DateFormat timeFormat = DateFormat.getInstance(7);
      StringBuffer b = (StringBuffer)(new Object());
      b.setLength(0);
      b.append(TaskResources.getString(32));
      b.append(' ');
      long start = TaskUtilities.convertFromGMT(this.getDueDate(), tz);
      ((CalendarExtensions)cal).setTimeLong(start);
      timeFormat.format(cal, b, null);
      return b.toString();
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 28) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         syncBuffer.addBytes(1, taskId);
         return syncBuffer.addSubmembers(this, context);
      } else {
         return false;
      }
   }

   @Override
   public final Object clone(Object context) {
      TaskModelImpl taskModel = new TaskModelImpl();
      int fieldCount = this._subModels.length;

      while (fieldCount > 0) {
         Object o = this.getAt(--fieldCount);
         if (o instanceof Object) {
            taskModel.add(((CloneProvider)o).clone(context));
         }
      }

      return taskModel;
   }

   @Override
   public final Object invokeHotkey(Object context, int hotkeyID) {
      Object retVal = null;
      if (hotkeyID == 127 || Keypad.getAltedChar((char)hotkeyID) == 127) {
         Object verbContext = new Object();
         retVal = new DeleteTaskVerb(this).invoke(verbContext);
         if (ContextObject.getPrivateFlag(verbContext, -3866311304884942232L, 1)) {
            RIMGlobalMessagePoster.postGlobalEvent(5483692278053761660L, 0, 0, null, null);
         }
      }

      return retVal;
   }

   protected final String getSummary() {
      StringBuffer b = (StringBuffer)(new Object());
      b.setLength(0);
      b.append(TaskResources.getString(21));
      b.append(':');
      b.append(' ');
      switch (this.getStatus()) {
         case -1:
            EventLogger.logEvent(-1576052272418032312L, 1229870675, 2);
            b.setLength(0);
            break;
         case 0:
            b.append(TaskResources.getString(16));
            break;
         case 1:
            b.append(TaskResources.getString(17));
            break;
         case 2:
         default:
            b.append(TaskResources.getString(18));
            break;
         case 3:
            b.append(TaskResources.getString(19));
            break;
         case 4:
            b.append(TaskResources.getString(20));
      }

      return b.toString();
   }

   public final TaskModel getNextInstance() {
      Recur recurInfo = this.getTaskDataModel().getRecurrenceModel();
      if (recurInfo != null && recurInfo.getRecurType() != 0) {
         ContextObject co = null;
         if (TaskUtilities.getCICALConfiguration().isInfiniteRecurrenceAllowed()) {
            co = (ContextObject)(new Object(65));
         }

         return (TaskModelImpl)this.clone(co);
      } else {
         return null;
      }
   }

   final boolean isOverDue() {
      if (!this.hasDueDate()) {
         return false;
      }

      long dueDate = TaskUtilities.convertFromGMT(this.getDueDate(), TimeZone.getDefault());
      long currentTime = System.currentTimeMillis();
      return dueDate < currentTime;
   }

   public final TaskDataModel getTaskDataModel() {
      for (int i = this._subModels.length - 1; i >= 0; i--) {
         Object o = this._subModels[i];
         if (o instanceof TaskDataModel) {
            return (TaskDataModel)o;
         }
      }

      EventLogger.logEvent(-1576052272418032312L, 1313817665, 2);
      return null;
   }

   @Override
   public final String getNotes() {
      BodyModel bm = this.getNotesModel();
      return bm != null ? bm.getText() : null;
   }

   @Override
   public final void setNotes(String notes) {
      if (notes != null) {
         byte[] bytes = notes.getBytes();
         if (bytes.length > 4096) {
            Array.resize(bytes, 4096);
            notes = (String)(new Object(bytes));
         }
      }

      BodyModel bm = this.getNotesModel();
      if (bm != null) {
         bm.setText(notes);
      } else {
         bm = (BodyModel)FactoryUtil.createInstance(2096811533660483L, null);
         bm.setText(notes);
         this.add(bm);
      }
   }

   @Override
   public final boolean isCompleted() {
      return this.getStatus() == 2;
   }

   @Override
   public final boolean isRecurring() {
      Recur recurInfo = this.getRecurrenceModel();
      return recurInfo != null && recurInfo.getRecurType() != 0;
   }

   @Override
   public final int getStatus() {
      return this.getTaskDataModel().getStatus();
   }

   @Override
   public final void setStatus(int status) {
      this.getTaskDataModel().setStatus(status);
   }

   @Override
   public final Recur getRecurrenceModel() {
      return this.getTaskDataModel().getRecurrenceModel();
   }

   @Override
   public final void setRecurrenceModel(Recur recurrenceModel) {
      this.getTaskDataModel().setRecurrenceModel(recurrenceModel);
   }

   @Override
   public final String getTimeZoneID() {
      return this.getTaskDataModel().getTimeZoneID();
   }

   @Override
   public final void setTimeZoneID(String timeZoneID) {
      this.getTaskDataModel().setTimeZoneID(timeZoneID);
   }

   @Override
   public final RIMModel getTitleModel() {
      for (int i = this._subModels.length - 1; i >= 0; i--) {
         Object o = this._subModels[i];
         if (o instanceof Object) {
            return (RIMModel)o;
         }
      }

      return null;
   }

   @Override
   public final CategoriesModel getCategoriesModel() {
      for (int i = this._subModels.length - 1; i >= 0; i--) {
         Object o = this._subModels[i];
         if (o instanceof Object) {
            return (CategoriesModel)o;
         }
      }

      return null;
   }

   @Override
   public final void setPriority(int priority) {
      this.getTaskDataModel().setPriority(priority);
   }

   @Override
   public final boolean isAllDay() {
      return false;
   }

   @Override
   public final long getStart(TimeZone tz) {
      return TaskUtilities.convertFromGMT(this.getDueDate(), tz);
   }

   @Override
   public final long getDuration(TimeZone tz) {
      return 0;
   }

   @Override
   public final long getStartDate() {
      return this.getTaskDataModel().getStartDate();
   }

   @Override
   public final void setStartDate(long time) {
      this.getTaskDataModel().setStartDate(time);
   }

   @Override
   public final long getRecurrenceStartDate() {
      return this.getTaskDataModel().getRecurrenceStartDate();
   }

   @Override
   public final void setRecurrenceStartDate(long time) {
      this.getTaskDataModel().setRecurrenceStartDate(time);
   }

   @Override
   public final void setDueDate(long date) {
      this.getTaskDataModel().setDueDate(date);
   }

   @Override
   public final long getDueDate() {
      return this.getTaskDataModel().getDueDate();
   }

   @Override
   public final boolean hasDueDate() {
      return this.getDueDate() != Long.MIN_VALUE;
   }

   @Override
   public final int getPriority() {
      return this.getTaskDataModel().getPriority();
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return ReadableListUtil.getAt(index, count, elements, destIndex, this);
   }

   @Override
   public final boolean contains(Object element) {
      return element instanceof Object || Arrays.contains(this._subModels, element);
   }

   @Override
   public final int size() {
      return this._subModels.length;
   }

   @Override
   public final Object getAt(int index) {
      return this._subModels[index];
   }

   @Override
   public final void add(Object element) {
      Arrays.add(this._subModels, element);
   }

   @Override
   public final Object makeReadOnly() {
      ObjectGroup.createGroupIgnoreTooBig(this);
      return this;
   }

   @Override
   public final Object makeReadWrite() {
      return ObjectGroup.isInGroup(this) ? ObjectGroup.expandGroup(this) : this;
   }

   @Override
   public final boolean isReadOnly() {
      return ObjectGroup.isInGroup(this);
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      Object[] fields = this._subModels;
      int count = fields.length;
      int keyCount = 0;

      for (int i = 0; i < count; i++) {
         Object element = fields[i];
         if (element instanceof Object) {
            KeyProvider keyProvider = (KeyProvider)element;
            keyCount += keyProvider.getKeys(context, keyArray, index + keyCount, keyRequested);
         }
      }

      return keyCount;
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      int numSubmembers = this._subModels.length;

      for (int i = 0; i < numSubmembers; i++) {
         Object object = this._subModels[i];
         if (object instanceof Object) {
            EncryptableProvider encryptable = (EncryptableProvider)object;
            if (!encryptable.checkCrypt(compress, encrypt)) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      TaskModelImpl newModel = (TaskModelImpl)this.makeReadWrite();
      int numSubmembers = newModel._subModels.length;

      for (int i = 0; i < numSubmembers; i++) {
         Object object = newModel._subModels[i];
         if (object instanceof Object) {
            EncryptableProvider encryptable = (EncryptableProvider)object;
            Object newObject = encryptable.reCrypt(compress, encrypt);
            if (newObject != null) {
               newModel._subModels[i] = newObject;
            }
         }
      }

      return newModel.makeReadOnly();
   }

   @Override
   public final ReminderModel getReminderModel() {
      return this.getTaskDataModel().getReminderModel();
   }

   @Override
   public final long getReminderID() {
      return this._uid;
   }

   @Override
   public final long getReminderTime(TimeZone tz) {
      TaskDataModel tmd = this.getTaskDataModel();
      ReminderModel rm = tmd.getReminderData();
      if (rm == null) {
         return -1;
      } else {
         TimeZone taskTimeZone = TimeZone.getTimeZone(this.getTimeZoneID());
         long reminderTime = tmd.getReminderModel().getTime();
         if (!rm.hasReminder()) {
            return -1;
         } else {
            return rm.getType() == 1
               ? TaskUtilities.convertFromGMT(this.getDueDate() - reminderTime, taskTimeZone)
               : TaskUtilities.convertFromGMT(reminderTime, taskTimeZone);
         }
      }
   }

   @Override
   public final void setReminderModel(ReminderModel reminderModel) {
      this.getTaskDataModel().setReminderModel(reminderModel);
   }

   @Override
   public final long getReminderEndTime(TimeZone tz) {
      return this.getReminderTime(tz) + 60000;
   }

   @Override
   public final ReminderModel getReminderData() {
      return this.getTaskDataModel().getReminderModel();
   }

   @Override
   public final boolean updateReminderData(ReminderModel rm) {
      this.setReminderModel(rm);
      return true;
   }

   @Override
   public final int getReminderState() {
      ReminderModel rm = this.getReminderData();
      return rm == null ? -1 : rm.getState();
   }

   @Override
   public final String getReminderDescription() {
      return this.getTitleString();
   }

   @Override
   public final String getStringForField(long field) {
      return this.getStringForField(field, 0);
   }

   @Override
   public final String getStringForField(long field, long data) {
      String str = null;
      if (field == -4581712257088750184L) {
         str = this.getTitleString();
      } else if (field == 5649235763655597796L) {
         str = this.getTitleString();
      } else if (field == 9164664086580876244L) {
         str = this.getSummary();
      } else if (field == -8797898085576394050L) {
         str = this.getTimeSummary();
      }

      return str;
   }

   @Override
   public final int getIconsForField(long field, IconCollection[] icons, int[][][] indices) {
      int numIconsAdded = 0;
      if (this.getPriority() != 1) {
         int[] priorityIndicies = new int[]{Math.abs(this.getPriority() - 2)};
         Array.resize(icons, icons.length + 1);
         Array.resize(indices, indices.length + 1);
         icons[icons.length - 1] = PRIORITY_ICONS;
         indices[indices.length - 1] = (int[][])priorityIndicies;
         numIconsAdded++;
      }

      ReminderModel reminder = this.getReminderData();
      if (reminder instanceof Object) {
         numIconsAdded += ((DescriptionProvider)reminder).getIconsForField(field, icons, (int[][])indices);
      }

      int[] taskDetailIndicies = new int[0];
      Recur recur = this.getRecurrenceModel();
      if (recur != null && recur.getRecurType() != 0) {
         Array.resize(taskDetailIndicies, taskDetailIndicies.length + 1);
         taskDetailIndicies[taskDetailIndicies.length - 1] = 0;
      }

      String notes = this.getNotes();
      if (notes != null && notes.trim().length() != 0) {
         Array.resize(taskDetailIndicies, taskDetailIndicies.length + 1);
         taskDetailIndicies[taskDetailIndicies.length - 1] = 2;
      }

      if (taskDetailIndicies.length > 0) {
         Array.resize(icons, icons.length + 1);
         Array.resize(indices, indices.length + 1);
         icons[icons.length - 1] = TASK_DETAIL_ICONS;
         indices[indices.length - 1] = (int[][])taskDetailIndicies;
         numIconsAdded++;
      }

      int[] statusIndicies = new int[]{this.getStatus()};
      Array.resize(icons, icons.length + 1);
      Array.resize(indices, indices.length + 1);
      icons[icons.length - 1] = STATUS_ICONS;
      indices[indices.length - 1] = (int[][])statusIndicies;
      return numIconsAdded + 1;
   }

   @Override
   public final byte getProperties() {
      return 0;
   }

   @Override
   public final void removeAll() {
      Array.resize(this._subModels, 0);
   }

   @Override
   public final int getIndex(Object element) {
      return Arrays.getIndex(this._subModels, element);
   }

   @Override
   public final boolean inbound() {
      return false;
   }

   @Override
   public final boolean allowDescriptiveForwardHeader() {
      return false;
   }

   @Override
   public final String getSender() {
      return null;
   }

   @Override
   public final String[] getRecipients() {
      return null;
   }

   @Override
   public final String getBody() {
      TextScrapeVisitor tsv = (TextScrapeVisitor)(new Object());
      Field field = null;
      ContextObject context = (ContextObject)(new Object(28, 0));
      int total = this._subModels.length;

      for (int i = 0; i < total; i++) {
         Object o = this._subModels[i];
         if (o instanceof Object && !(o instanceof Object)) {
            field = ((FieldProvider)o).getField(context);
            if (field != null) {
               field.acceptVisitor(tsv);
            }
         }
      }

      return tsv.getStringBuffer().toString();
   }

   @Override
   public final String getSubject() {
      return this.getReminderDescription();
   }

   @Override
   public final MessageAttachment[] getAttachments() {
      return null;
   }

   @Override
   public final String getName() {
      return CommonResources.getString(2005);
   }

   @Override
   public final void setRead(Object context) {
   }

   @Override
   public final long getSentDate() {
      return 0;
   }

   @Override
   public final void remove(Object element) {
      Arrays.remove(this._subModels, element);
   }

   private final BodyModel getNotesModel() {
      for (int i = this._subModels.length - 1; i >= 0; i--) {
         Object o = this._subModels[i];
         if (o instanceof Object) {
            return (BodyModel)o;
         }
      }

      return null;
   }

   private final String getTitleString() {
      RIMModel titleModel = this.getTitleModel();
      return titleModel != null ? titleModel.toString() : "";
   }
}
