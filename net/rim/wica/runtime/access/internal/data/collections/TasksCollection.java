package net.rim.wica.runtime.access.internal.data.collections;

import java.util.Enumeration;
import javax.microedition.pim.ToDo;
import javax.microedition.pim.ToDoList;
import net.rim.blackberry.api.pdap.ToDoListFactory;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.wica.common.builtindata.componentdefn.TaskCompDef;
import net.rim.wica.runtime.access.internal.data.enumeration.StatusEnumConverter;
import net.rim.wica.runtime.access.internal.data.enumeration.TaskPriorityEnumConverter;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;
import net.rim.wica.runtime.access.internal.data.handlers.LongFieldHandler;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;
import net.rim.wica.runtime.metadata.internal.WicletEx;

public class TasksCollection extends StdCmpCollectionImpl {
   private ToDoList _taskList;
   private ToDo _uidMatchToDo;
   private IntHashtable _uidsToTasks;
   public static final int STATUS = 16777225;

   public TasksCollection(WicletEx wiclet) {
      super(wiclet, TaskCompDef.getInstance());
      int access = super._wiclet.getContext().getExternalAccessType();
      if (access != 0) {
         int mode = 3;
         if (access == 1) {
            mode = 1;
         }

         try {
            this._taskList = ToDoListFactory.createToDoList(mode);
            this._uidMatchToDo = this._taskList.createToDo();
            if (this._uidMatchToDo != null) {
               this._uidMatchToDo.addString(108, 0, "");
            }

            this._uidsToTasks = (IntHashtable)(new Object());
         } finally {
            return;
         }
      }
   }

   @Override
   public void loadItem(long dataHandle, Object item) {
      if (this._taskList != null) {
         if (item instanceof Object) {
            this.setIntFieldValue(dataHandle, 2, ((IntFieldHandler)super._intFieldHandlers.get(2)).getValue(item));
            this.setObjectFieldValue(dataHandle, 0, ((ObjectFieldHandler)super._objectFieldHandlers.get(0)).getValue(item));
            this.setObjectFieldValue(dataHandle, 1, ((ObjectFieldHandler)super._objectFieldHandlers.get(1)).getValue(item));
            this.setIntFieldValue(dataHandle, 3, ((IntFieldHandler)super._intFieldHandlers.get(3)).getValue(item));
            this.setIntFieldValue(dataHandle, 5, ((IntFieldHandler)super._intFieldHandlers.get(5)).getValue(item));
            this.setLongFieldValue(dataHandle, 4, ((LongFieldHandler)super._longFieldHandlers.get(4)).getValue(item));
         }
      }
   }

   @Override
   public IntVector uidsInExternalDB() {
      IntVector uidsInDB = null;

      Enumeration items;
      try {
         items = this._taskList.items();
      } finally {
         ;
      }

      if (items != null && items.hasMoreElements()) {
         uidsInDB = (IntVector)(new Object());
         this._uidsToTasks = (IntHashtable)(new Object());

         while (items.hasMoreElements()) {
            ToDo task = (ToDo)items.nextElement();

            try {
               String uid = task.getString(108, 0);
               int intUID = Integer.parseInt(uid);
               uidsInDB.addElement(intUID);
               this._uidsToTasks.put(intUID, task);
            } finally {
               continue;
            }
         }
      }

      return uidsInDB;
   }

   @Override
   public void saveDeletedItems() {
      if (this._taskList != null) {
         if (super._handles.size() == 0) {
            try {
               Enumeration e = this._taskList.items();

               while (e.hasMoreElements()) {
                  this._taskList.removeToDo((ToDo)e.nextElement());
               }
            } finally {
               return;
            }
         } else {
            for (int i = super._deletedItems.size() - 1; i >= 0; i--) {
               Object taskToRemove = this.getDBItemFromHandle((long)super._defs.getId() << 32 | 4294967295L & super._deletedItems.elementAt(i));
               if (taskToRemove instanceof Object) {
                  try {
                     this._taskList.removeToDo((ToDo)taskToRemove);
                  } finally {
                     continue;
                  }
               }
            }
         }
      }
   }

   @Override
   public void saveModifiedItems() {
      for (int i = super._modifiedItems.size() - 1; i >= 0; i--) {
         ToDo task = this.updateToDoItem(super._modifiedItems.elementAt(i));

         try {
            task.commit();
         } finally {
            continue;
         }
      }
   }

   @Override
   public void saveCreatedItems() {
      for (int i = super._createdItems.size() - 1; i >= 0; i--) {
         ToDo task = this.updateToDoItem(super._createdItems.elementAt(i));

         try {
            task.commit();
         } finally {
            continue;
         }
      }
   }

   private ToDo getTaskByUID(int uid) {
      return (ToDo)(this._uidsToTasks != null ? this._uidsToTasks.get(uid) : null);
   }

   private ToDo updateToDoItem(int handle) {
      ToDo task = this.getTaskByUID(handle);
      if (task != null) {
         long dataHandle = (long)super._defs.getId() << 32 | 4294967295L & handle;

         try {
            if (task.countValues(103) > 0) {
               task.setDate(103, 0, 0, this.getLongFieldValue(dataHandle, 4));
            } else {
               task.addDate(103, 0, this.getLongFieldValue(dataHandle, 4));
            }

            if (task.countValues(104) > 0) {
               task.setString(104, 0, 0, (String)this.getObjectFieldValue(dataHandle, 0));
            } else {
               task.addString(104, 0, (String)this.getObjectFieldValue(dataHandle, 0));
            }

            if (task.countValues(105) > 0) {
               task.setInt(105, 0, 0, TaskPriorityEnumConverter.commonToDevice(this.getIntFieldValue(dataHandle, 3)));
            } else {
               task.addInt(105, 0, TaskPriorityEnumConverter.commonToDevice(this.getIntFieldValue(dataHandle, 3)));
            }

            if (this._taskList.isSupportedField(16777225)) {
               if (task.countValues(16777225) > 0) {
                  task.setInt(16777225, 0, 0, StatusEnumConverter.commonToDevice(this.getIntFieldValue(dataHandle, 5)));
               } else {
                  task.addInt(16777225, 0, StatusEnumConverter.commonToDevice(this.getIntFieldValue(dataHandle, 5)));
               }
            }

            if (task.countValues(107) <= 0) {
               task.addString(107, 0, (String)this.getObjectFieldValue(dataHandle, 1));
               return task;
            }

            task.setString(107, 0, 0, (String)this.getObjectFieldValue(dataHandle, 1));
         } finally {
            return task;
         }
      }

      return task;
   }

   @Override
   protected int getUID() {
      if (this._taskList != null) {
         ToDo task = this._taskList.createToDo();
         if (task != null) {
            try {
               task.commit();
               String uid = task.getString(108, 0);
               int intUID = Integer.parseInt(uid);
               this._uidsToTasks.put(intUID, task);
               return intUID;
            } finally {
               return -1;
            }
         }
      }

      return -1;
   }

   @Override
   public Object getDBItemFromHandle(long dataHandle) {
      int uid = this.getHandle(dataHandle);
      if (uid != -1) {
         ToDo item = this.getTaskByUID(uid);
         if (item != null) {
            return item;
         }
      }

      return null;
   }

   @Override
   public void initFieldHandlers() {
      super._objectFieldHandlers = (IntHashtable)(new Object(3));
      super._objectFieldHandlers.put(0, new TasksCollection$NoteHandler(null));
      super._objectFieldHandlers.put(1, new TasksCollection$SummaryHandler(null));
      super._intFieldHandlers = (IntHashtable)(new Object(4));
      super._intFieldHandlers.put(2, new TasksCollection$UIDHandler(null));
      if (this._taskList != null) {
         super._intFieldHandlers.put(5, new TasksCollection$StatusHandler(this._taskList.isSupportedField(16777225)));
      }

      super._intFieldHandlers.put(3, new TasksCollection$PriorityHandler(null));
      super._longFieldHandlers = (IntHashtable)(new Object(1));
      super._longFieldHandlers.put(4, new TasksCollection$DueDateHandler(null));
   }
}
