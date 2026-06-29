package net.rim.blackberry.api.pim;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.task.TaskCollection;
import net.rim.device.apps.api.task.TaskCollectionHolder;
import net.rim.device.apps.api.task.TaskModel;
import net.rim.device.apps.internal.commonmodels.title.TitleModel;
import net.rim.vm.Array;

public final class ToDoImpl extends PIMItemImpl implements BlackBerryToDo {
   protected TaskModel _taskModel;
   private TaskModel _committedTaskModel;
   private ToDoListImpl _todoList;
   private int _mode;
   private boolean _modified;
   private boolean _isInList;
   public static final int STATUS = 16777225;
   public static final int STATUS_NOT_STARTED = 1;
   public static final int STATUS_IN_PROGRESS = 2;
   public static final int STATUS_COMPLETED = 3;
   public static final int STATUS_WAITING = 4;
   public static final int STATUS_DEFERRED = 5;
   private static Factory _taskModelFactory;
   private static Factory _titleModelFactory;

   final TaskModel getRimTodo() {
      return this._committedTaskModel != null ? this._committedTaskModel : this._taskModel;
   }

   @Override
   public final void initialize(int mode, Object input, ToDoList todoList) {
      this._mode = mode;
      this._isInList = true;
      if (todoList instanceof ToDoListImpl) {
         this._todoList = (ToDoListImpl)todoList;
      }

      if (input instanceof Object) {
         this._committedTaskModel = (TaskModel)input;
         this._taskModel = (TaskModel)((EditableProvider)input).makeReadWrite();
      }

      this._modified = false;
   }

   @Override
   public final boolean isInternalModel(Object selected) {
      return selected instanceof Object;
   }

   @Override
   public final void initialize(int mode) {
      this._taskModel = (TaskModel)_taskModelFactory.createInstance(null);
      this._mode = mode;
      this._modified = false;
      this._isInList = false;
   }

   ToDoImpl() {
   }

   ToDoImpl(int mode, ToDoListImpl todoList) {
      this(mode);
      this._todoList = todoList;
   }

   ToDoImpl(int mode) {
      this.initialize(mode);
   }

   public ToDoImpl(int mode, TaskModel input, ToDoListImpl todoList) {
      this.initialize(mode, input, todoList);
   }

   @Override
   public final int countValues(int field) {
      switch (field) {
         case 100:
         case 101:
         case 102:
         case 106:
            throw new Object(field);
         case 103:
            if (this._taskModel.hasDueDate()) {
               return 1;
            }

            return 0;
         case 104:
            if (this._taskModel.getNotes() == null) {
               return 0;
            }

            return 1;
         case 105:
            if (this._taskModel.getPriority() != -1) {
               return 1;
            }

            return 0;
         case 107:
            if (this._taskModel.getTitleModel() == null) {
               return 0;
            }

            return 1;
         case 108:
            TaskCollection _collection = TaskCollectionHolder.getTaskCollection();
            if (_collection.contains(this._committedTaskModel)) {
               return 1;
            }

            return 0;
         case 16777225:
            if (this._taskModel.getStatus() != 0) {
               return 1;
            }

            return 0;
         default:
            throw new Object();
      }
   }

   @Override
   public final long getDate(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 103:
            return this._taskModel.getDueDate();
         default:
            throw new Object();
      }
   }

   @Override
   public final void addDate(int field, int attributes, long value) {
      this.checkFieldNotFull(field);
      switch (field) {
         case 103:
            this._taskModel.setDueDate(value);
            this._modified = true;
            return;
         default:
            throw new Object();
      }
   }

   @Override
   public final boolean getBoolean(int field, int index) {
      switch (field) {
         case 101:
            throw new Object(field);
         default:
            throw new Object();
      }
   }

   @Override
   public final void addBoolean(int field, int attributes, boolean value) {
      this.getBoolean(field, 0);
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
            return String.valueOf(this._taskModel.getUID());
         default:
            throw new Object();
      }
   }

   @Override
   public final void addString(int field, int attributes, String value) {
      this.checkFieldNotFull(field);
      switch (field) {
         case 104:
            this._taskModel.setNotes(value);
            break;
         case 107:
            TitleModel summaryModel = (TitleModel)_titleModelFactory.createInstance(value);
            this._taskModel.add(summaryModel);
            break;
         case 108:
            throw new Object("UID is a read-only field.");
         default:
            throw new Object();
      }

      this._modified = true;
   }

   @Override
   public final int getInt(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 105:
            return this._taskModel.getPriority();
         case 16777225:
            return this._taskModel.getStatus();
         default:
            throw new Object();
      }
   }

   @Override
   public final void addInt(int field, int attributes, int value) {
      this.checkFieldNotFull(field);
      label24:
      switch (field) {
         case 105:
            if (value < 0 || value > 9) {
               throw new Object();
            }

            this._taskModel.setPriority(value);
            break;
         case 16777225:
            switch (value) {
               case 0:
                  throw new Object();
               case 1:
                  this._taskModel.setStatus(0);
                  break label24;
               case 2:
                  this._taskModel.setStatus(1);
                  break label24;
               case 3:
               default:
                  this._taskModel.setStatus(2);
                  break label24;
               case 4:
                  this._taskModel.setStatus(3);
                  break label24;
               case 5:
                  this._taskModel.setStatus(4);
                  break label24;
            }
         default:
            throw new Object();
      }

      this._modified = true;
   }

   @Override
   public final void removeValue(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 103:
            this._taskModel.setDueDate(Long.MIN_VALUE);
            break;
         case 104:
            this._taskModel.setNotes(null);
            break;
         case 105:
            this._taskModel.setPriority(0);
            break;
         case 107:
            RIMModel tm = this._taskModel.getTitleModel();
            this._taskModel.remove(tm);
            break;
         case 108:
            throw new Object("UID is a read-only field.");
         case 16777225:
            this._taskModel.setStatus(0);
            break;
         default:
            throw new Object();
      }

      this._modified = true;
   }

   @Override
   public final PIMList getPIMList() {
      return this._todoList;
   }

   @Override
   public final void commit() {
      if (this._todoList._closed) {
         throw new Object("ToDo List is closed.", 2);
      }

      if (this._mode == 1) {
         throw new Object();
      }

      if (this.isEmpty()) {
         throw new Object("Cannot commit an empty ToDo");
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
      this._modified = false;
   }

   @Override
   public final boolean isModified() {
      return this._modified;
   }

   @Override
   public final int[] getFields() {
      int[] fields = new int[7];
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

      Array.resize(fields, index + 1);
      return fields;
   }

   @Override
   public final void setBoolean(int field, int index, int attributes, boolean value) {
      this.getBoolean(field, index);
   }

   @Override
   public final void setDate(int field, int index, int attributes, long value) {
      this.checkIndex(field, index);
      switch (field) {
         case 103:
            this._taskModel.setDueDate(value);
            this._modified = true;
            return;
         default:
            throw new Object();
      }
   }

   @Override
   public final void setInt(int field, int index, int attributes, int value) {
      this.checkIndex(field, index);
      switch (field) {
         case 105:
            if (value < 0 || value > 9) {
               throw new Object();
            }

            this._taskModel.setPriority(value);
            break;
         case 16777225:
            if (value < 0 || value > 4) {
               throw new Object();
            }

            this._taskModel.setStatus(value);
            break;
         default:
            throw new Object();
      }

      this._modified = true;
   }

   @Override
   public final void setString(int field, int index, int attributes, String value) {
      this.checkIndex(field, index);
      switch (field) {
         case 104:
            this._taskModel.setNotes(value);
            break;
         case 107:
            ((TitleModel)this._taskModel.getTitleModel()).setTitle(value);
            break;
         case 108:
            throw new Object("UID is a read-only field.");
         default:
            throw new Object();
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

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _taskModelFactory = (Factory)ar.waitFor(-4172790793103625162L);
      _titleModelFactory = (Factory)ar.waitFor(-4904857078378172834L);
   }
}
