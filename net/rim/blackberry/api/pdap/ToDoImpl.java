package net.rim.blackberry.api.pdap;

import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.RepeatRule;
import javax.microedition.pim.ToDoList;
import javax.microedition.pim.UnsupportedFieldException;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.task.TaskCollection;
import net.rim.device.apps.api.task.TaskCollectionHolder;
import net.rim.device.apps.api.task.TaskModel;
import net.rim.device.apps.internal.commonmodels.title.TitleModel;
import net.rim.vm.Array;

public final class ToDoImpl extends PIMItemImpl implements InternalToDo {
   protected TaskModel _taskModel;
   private TaskModel _committedTaskModel;
   private ToDoListImpl _todoList;
   private int _mode;
   private boolean _modified;
   private boolean _isInList;
   private RepeatRule _repeat;
   private String _uid;
   private boolean _committed;
   private boolean priorityFieldSet;
   private boolean statusFieldSet;
   private boolean completedFieldSet;
   public static final int STATUS = 16777225;
   private static Factory _taskModelFactory;
   private static Factory _titleModelFactory;

   final void removeFromList() {
      this._todoList = null;
   }

   public final Object getInternalModel() {
      return this._committedTaskModel == null ? this._taskModel : this._committedTaskModel;
   }

   @Override
   public final boolean isInternalModel(Object selected) {
      return selected instanceof TaskModel;
   }

   @Override
   public final void initialize(int mode, Object input, ToDoList todoList) {
      this._mode = mode;
      this._isInList = true;
      if (todoList instanceof ToDoListImpl) {
         this._todoList = (ToDoListImpl)todoList;
      }

      if (input instanceof TaskModel) {
         this._committedTaskModel = (TaskModel)input;
         this._taskModel = (TaskModel)((EditableProvider)input).makeReadWrite();
         super._categoriesModel = this._taskModel.getCategoriesModel();
      }

      this.priorityFieldSet = true;
      this.statusFieldSet = true;
      if (this._taskModel.isCompleted()) {
         this.completedFieldSet = true;
      }

      if (this._taskModel.isRecurring()) {
         this._repeat = RepeatRuleUtil.createRepeatRule(this._taskModel.getRecurrenceModel());
      }

      this._committed = true;
      this._modified = false;
   }

   @Override
   public final void initialize(int mode) {
      this._taskModel = (TaskModel)_taskModelFactory.createInstance(null);
      this._mode = mode;
      this._modified = true;
      this._isInList = false;
   }

   @Override
   public final RepeatRule getRepeat() {
      if (this._taskModel.isRecurring()) {
         if (this._repeat == null) {
            this._repeat = RepeatRuleUtil.createRepeatRule(this._taskModel.getRecurrenceModel());
         }

         return this._repeat;
      } else {
         return null;
      }
   }

   @Override
   public final void setRepeat(RepeatRule value) {
      this._repeat = value;
      Recur recur = RepeatRuleUtil.createRecur(this._taskModel.getRecurrenceModel(), value);
      this._taskModel.setRecurrenceModel(recur);
      this._modified = true;
   }

   ToDoImpl(int mode, ToDoListImpl todoList) {
      this(mode);
      this._todoList = todoList;
   }

   @Override
   public final int countValues(int field) {
      switch (field) {
         case 100:
         case 102:
         case 106:
            throw new UnsupportedFieldException("", field);
         case 101:
            if (this.completedFieldSet) {
               return 1;
            }

            return 0;
         case 103:
            if (this._taskModel.hasDueDate()) {
               return 1;
            }

            return 0;
         case 104:
            if (this._taskModel.getNotes() != null && this._taskModel.getNotes().length() != 0) {
               return 1;
            }

            return 0;
         case 105:
            if (this.priorityFieldSet) {
               return 1;
            }

            return 0;
         case 107:
            if (this._taskModel.getTitleModel() == null) {
               return 0;
            }

            return 1;
         case 108:
            if (this._committed) {
               if (String.valueOf(this._taskModel.getUID()) != null) {
                  return 1;
               }

               return 0;
            } else {
               if (this._uid != null) {
                  return 1;
               }

               return 0;
            }
         case 16777225:
            if (this.statusFieldSet) {
               return 1;
            }

            return 0;
         case 20000927:
            if (this._taskModel.getReminderModel() != null && this._taskModel.getReminderModel().hasReminder()) {
               return 1;
            }

            return 0;
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public final long getDate(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 103:
            return this._taskModel.getDueDate();
         case 20000927:
            return this._taskModel.getReminderModel().getTime();
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public final void addDate(int field, int attributes, long value) {
      this.checkFieldNotFull(field);
      switch (field) {
         case 103:
            this._taskModel.setDueDate(value);
            break;
         case 20000927:
            this._taskModel.getReminderModel().setTime(value);
            break;
         default:
            throw new IllegalArgumentException();
      }

      this._modified = true;
   }

   @Override
   public final boolean getBoolean(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 101:
            return this._taskModel.isCompleted();
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public final void addBoolean(int field, int attributes, boolean value) {
      this.checkFieldNotFull(field);
      switch (field) {
         case 101:
            if (value) {
               this._taskModel.setStatus(2);
            } else {
               this._taskModel.setStatus(0);
            }

            this.completedFieldSet = true;
            this._modified = true;
            return;
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getString(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 104:
            return this._taskModel.getNotes();
         case 107:
            return this._taskModel.getTitleModel().toString();
         case 108:
            if (this._committed) {
               return String.valueOf(this._taskModel.getUID());
            }

            return this._uid;
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public final void addString(int field, int attributes, String value) {
      this.checkFieldNotFull(field);
      if (value == null) {
         throw new NullPointerException();
      }

      switch (field) {
         case 104:
            this._taskModel.setNotes(value);
            break;
         case 107:
            TitleModel summaryModel = (TitleModel)_titleModelFactory.createInstance(value);
            this._taskModel.add(summaryModel);
            break;
         case 108:
            if (this._committed) {
               throw new IllegalArgumentException("UID is a read-only field.");
            }

            this._uid = value;
            break;
         default:
            throw new IllegalArgumentException();
      }

      this._modified = true;
   }

   @Override
   public final int getInt(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 105:
            switch (this._taskModel.getPriority()) {
               case -1:
                  break;
               case 0:
               default:
                  return 1;
               case 1:
                  return 5;
               case 2:
                  return 9;
            }
         case 16777225:
            return this._taskModel.getStatus();
         default:
            throw new IllegalArgumentException();
      }
   }

   private final void setPriority(int value) {
      if (value < 0 || value > 9) {
         throw new IllegalArgumentException();
      }

      if (value == 0) {
         this._taskModel.setPriority(1);
      } else if (value <= 3) {
         this._taskModel.setPriority(0);
      } else if (value <= 6) {
         this._taskModel.setPriority(1);
      } else {
         this._taskModel.setPriority(2);
      }
   }

   @Override
   public final void addInt(int field, int attributes, int value) {
      this.checkFieldNotFull(field);
      switch (field) {
         case 105:
            this.setPriority(value);
            this.priorityFieldSet = true;
            break;
         case 16777225:
            if (value < 0 || value > 4) {
               throw new IllegalArgumentException();
            }

            this._taskModel.setStatus(value);
            this.statusFieldSet = true;
            break;
         default:
            throw new IllegalArgumentException();
      }

      this._modified = true;
   }

   @Override
   public final void removeValue(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 101:
            this._taskModel.setStatus(0);
            this.completedFieldSet = false;
            break;
         case 103:
            this._taskModel.setDueDate(Long.MIN_VALUE);
            break;
         case 104:
            this._taskModel.setNotes(null);
            break;
         case 105:
            this._taskModel.setPriority(1);
            this.priorityFieldSet = false;
            break;
         case 107:
            RIMModel tm = this._taskModel.getTitleModel();
            this._taskModel.remove(tm);
            break;
         case 108:
            if (this._committed) {
               throw new IllegalArgumentException("UID is a read-only field.");
            }

            this._uid = null;
            break;
         case 16777225:
            this._taskModel.setStatus(0);
            this.statusFieldSet = false;
            break;
         case 20000927:
            this._taskModel.getReminderModel().setTime(-1);
            break;
         default:
            throw new IllegalArgumentException();
      }

      this._modified = true;
   }

   @Override
   public final PIMList getPIMList() {
      return this._todoList;
   }

   public ToDoImpl(int mode, TaskModel input, ToDoListImpl todoList) {
      this.initialize(mode, input, todoList);
   }

   @Override
   public final void commit() throws PIMException {
      if (this._todoList == null) {
         throw new PIMException();
      }

      if (this._todoList._closed) {
         throw new PIMException("ToDo List is closed.", 2);
      }

      if (this._mode == 1) {
         throw new SecurityException();
      }

      if (this._taskModel.getTitleModel() == null) {
         TitleModel summaryModel = (TitleModel)_titleModelFactory.createInstance("Untitled Task");
         this._taskModel.add(summaryModel);
      }

      TaskCollection _collection = TaskCollectionHolder.getTaskCollection();
      this._taskModel.getStatus();
      this._committedTaskModel = (TaskModel)((EditableProvider)this._taskModel).makeReadOnly();
      if (!this._isInList) {
         _collection.add(this._committedTaskModel);
         this._isInList = true;
      } else {
         int uid = this._committedTaskModel.getUID();

         for (int i = _collection.size() - 1; i >= 0; i--) {
            TaskModel tModel = (TaskModel)_collection.getAt(i);
            if (tModel.getUID() == uid) {
               _collection.update(tModel, this._committedTaskModel);
               break;
            }
         }
      }

      this._taskModel = (TaskModel)((EditableProvider)this._taskModel).makeReadWrite();
      super._categoriesModel = this._taskModel.getCategoriesModel();
      this._committed = true;
      this._modified = false;
   }

   @Override
   public final boolean isModified() {
      return this._modified;
   }

   @Override
   public final int[] getFields() {
      int[] fields = new int[8];
      int index = -1;
      if (this.countValues(103) > 0) {
         fields[++index] = 103;
      }

      if (this.countValues(105) > 0) {
         fields[++index] = 105;
      }

      if (this.countValues(104) > 0) {
         fields[++index] = 104;
      }

      if (this.countValues(107) > 0) {
         fields[++index] = 107;
      }

      if (this.countValues(108) > 0) {
         fields[++index] = 108;
      }

      if (this.countValues(16777225) > 0) {
         fields[++index] = 16777225;
      }

      if (this.countValues(101) > 0) {
         fields[++index] = 101;
      }

      if (this.countValues(20000927) > 0) {
         fields[++index] = 20000927;
      }

      Array.resize(fields, index + 1);
      return fields;
   }

   @Override
   public final void setBoolean(int field, int index, int attributes, boolean value) {
      this.checkIndex(field, index);
      switch (field) {
         case 101:
            if (value) {
               this._taskModel.setStatus(2);
            } else {
               this._taskModel.setStatus(0);
            }

            this._modified = true;
            return;
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public final void setDate(int field, int index, int attributes, long value) {
      this.checkIndex(field, index);
      switch (field) {
         case 103:
            this._taskModel.setDueDate(value);
            break;
         case 20000927:
            this._taskModel.getReminderModel().setTime(value);
            break;
         default:
            throw new IllegalArgumentException();
      }

      this._modified = true;
   }

   @Override
   public final void setInt(int field, int index, int attributes, int value) {
      this.checkIndex(field, index);
      switch (field) {
         case 105:
            this.setPriority(value);
            break;
         case 16777225:
            if (value < 0 || value > 4) {
               throw new IllegalArgumentException();
            }

            this._taskModel.setStatus(value);
            break;
         default:
            throw new IllegalArgumentException();
      }

      this._modified = true;
   }

   @Override
   public final void setString(int field, int index, int attributes, String value) {
      this.checkIndex(field, index);
      if (value == null) {
         throw new NullPointerException();
      }

      switch (field) {
         case 104:
            this._taskModel.setNotes(value);
            break;
         case 107:
            ((TitleModel)this._taskModel.getTitleModel()).setTitle(value);
            break;
         case 108:
            if (this._committed) {
               throw new IllegalArgumentException("UID is a read-only field.");
            }

            this._uid = value;
            break;
         default:
            throw new IllegalArgumentException();
      }

      this._modified = true;
   }

   private final boolean isEmpty() {
      return this._taskModel.getTitleModel() == null
         && this._taskModel.getRecurrenceModel() == null
         && this._taskModel.getPriority() == -1
         && this._taskModel.getNotes() == null
         && this._taskModel.getReminderModel() == null;
   }

   @Override
   public final void addToCategory(String category) {
      if (this.addCategoryToModel(this._taskModel, category)) {
         this._modified = true;
      }
   }

   @Override
   public final void removeFromCategory(String category) {
      if (this.removeCategoryFromModel(this._taskModel, category)) {
         this._modified = true;
      }
   }

   ToDoImpl() {
   }

   ToDoImpl(int mode) {
      this.initialize(mode);
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _taskModelFactory = (Factory)ar.waitFor(-4172790793103625162L);
      _titleModelFactory = (Factory)ar.waitFor(-4904857078378172834L);
   }
}
