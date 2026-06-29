package net.rim.device.apps.internal.task;

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.task.TaskCollection;
import net.rim.device.apps.api.task.TaskModel;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.internal.task.resources.TaskResources;

final class EditTaskScreen extends EditorUsingRIMModelFactory {
   private Verb _deleteVerb;
   TaskModel _task;
   TaskModel _originalTask;
   private TaskCollection _taskCollection;
   private Field _categoriesField;
   boolean _infoVisible;

   EditTaskScreen(TaskCollection taskCollection, String screenTitle, ContextObject context) {
      super(context, screenTitle != null ? screenTitle : TaskResources.getString(4), 7798410905730545828L, -1);
      this._taskCollection = taskCollection;
      this.getMainManager().setTag(ThemeUtilities.TASK_SCREEN_TAG);
   }

   public final void setTaskModel(TaskModel task) {
      super.setModel(task);
      this._task = task;
      int size = this._taskCollection.size();

      for (int i = size - 1; i >= 0; i--) {
         TaskModel t = (TaskModel)this._taskCollection.getAt(i);
         if (t.getUID() == task.getUID()) {
            this._originalTask = t;
            break;
         }
      }

      if (this._originalTask != null) {
         this._deleteVerb = new DeleteTaskVerb(this._originalTask, true);
      }

      this._categoriesField = this.findField(RecognizerRepository.getRecognizers(-537018776823173138L));
      if (this._categoriesField != null) {
         this.replaceField(this._categoriesField, this._categoriesField, 13400);
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      if (instance != 65537) {
         super.makeMenu(menu, instance);
         MenuItem saveItem = MenuItem.getPrefab(15);
         menu.add(saveItem);
         if (instance == 65536 && this.isDirty()) {
            menu.setDefault(saveItem);
         }

         if (instance == 0) {
            VerbRepository vr = VerbRepository.getVerbRepository(-5653680829824974669L);
            if (vr != null) {
               menu.add(vr.getVerbs(null));
            }

            if (this._deleteVerb != null) {
               menu.add(this._deleteVerb);
            }

            if (this._categoriesField != null && !(this.getModelFieldWithFocus().getCookie() instanceof Object)) {
               menu.add((Verb)(new Object(this._categoriesField)));
            }

            if (!this.isDirty()) {
               menu.setDefault(MenuItem.getPrefab(0));
               return;
            }

            if (this.isMuddy()) {
               menu.setDefault(saveItem);
            }
         }
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            Field field = this.getModelFieldWithFocus();
            if (field.getCookie() instanceof Object) {
               return this.invokeDefaultMenuItem(0);
            }
         default:
            return super.invokeAction(action);
      }
   }

   @Override
   protected final ContextObject getContext() {
      ContextObject co = ContextObject.castOrCreate(super._context);
      ContextObject.put(co, 3696141428889703675L, this._task);
      return co;
   }

   @Override
   public final boolean onSave() {
      if (this._originalTask != null && !this._taskCollection.contains(this._originalTask)) {
         Status.show(TaskResources.getString(53));
         return true;
      }

      int fieldFailed = this.validateFields();
      if (fieldFailed != -1) {
         if (fieldFailed == 0) {
            Status.show(TaskResources.getString(36));
         }

         return false;
      } else {
         this.getModel();
         TaskDataModel taskDataModel = ((TaskModelImpl)this._task).getTaskDataModel();
         Recur recurInfo = taskDataModel.getRecurrenceModel();
         int newStatus = this._task.getStatus();
         boolean needsUpdate = true;
         boolean recurChangeStatus = false;
         if (newStatus == 2 && recurInfo != null && recurInfo.getRecurType() != 0) {
            if (this._originalTask != null) {
               int oldStatus = this._originalTask.getStatus();
               if (oldStatus != 2) {
                  this._task.setStatus(oldStatus);
                  recurChangeStatus = true;
               }
            } else {
               recurChangeStatus = true;
            }
         }

         if (recurChangeStatus) {
            if (TaskUtilities.advanceTaskRecurrence((TaskModelImpl)this._task, false, newStatus, (TaskModelImpl)this._originalTask) != null) {
               needsUpdate = false;
            } else {
               recurInfo.setRecurType((byte)0);
               this._task.setStatus(newStatus);
               if (TaskUtilities.getCICALConfiguration().isInfiniteRecurrenceAllowed() && newStatus == 2) {
                  this._task.getReminderModel().setTime(-1);
               }
            }
         }

         if (needsUpdate) {
            if (!taskDataModel.hasDueDate()) {
               taskDataModel.setStartDate(Long.MIN_VALUE);
               if (recurInfo != null) {
                  recurInfo.setRecurPeriod(1);
                  recurInfo.setRecurType((byte)0);
               }
            } else {
               taskDataModel.setStartDate(taskDataModel.getDueDate());
            }

            if (recurInfo != null && recurInfo.getRecurType() == 0) {
               taskDataModel.setRecurrenceStartDate(Long.MIN_VALUE);
            } else {
               taskDataModel.setRecurrenceStartDate(taskDataModel.getDueDate());
            }

            if (!ObjectGroup.isInGroup(this._task)) {
               ObjectGroup.createGroupIgnoreTooBig(this._task);
            }
         }

         if (this._originalTask != null) {
            this._taskCollection.update(this._originalTask, this._task);
         } else {
            this._taskCollection.add(this._task);
         }

         RIMGlobalMessagePoster.postGlobalEvent(5483692278053761660L, 0, 0, null, null);
         return true;
      }
   }

   @Override
   protected final boolean openProductionBackdoor(int backDoor) {
      switch (backDoor) {
         case 1447642454:
            return super.openProductionBackdoor(backDoor);
         case 1447642455:
         default:
            if (!this._infoVisible) {
               this.insertInfo();
            }

            return true;
      }
   }

   private final void insertInfo() {
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object());
      vfm.add((Field)(new Object(((StringBuffer)(new Object("RefId: "))).append(this._task.getUID()).toString(), 18014398509481984L)));
      vfm.add((Field)(new Object()));
      this.insert(vfm, 0);
      vfm.setFocus();
      this._infoVisible = true;
   }

   @Override
   protected final Manager createManagerForField(Field f, int order) {
      Manager manager = null;
      if (order == 13500) {
         manager = new EditTaskScreen$1(this);
      } else {
         manager = (Manager)(new Object());
      }

      if (order == 13000) {
         manager.setTag(ThemeUtilities.TASK_TITLE_AREA_TAG);
         return manager;
      } else if (order == 13500) {
         manager.setTag(ThemeUtilities.TASK_NOTES_AREA_TAG);
         return manager;
      } else {
         manager.setTag(ThemeUtilities.TASK_DATA_AREA_TAG);
         return manager;
      }
   }

   @Override
   protected final int getOrderForManagerForField(Field field, int order) {
      if (order == 13000) {
         return 11000;
      } else {
         return order == 13500 ? 14000 : 13100;
      }
   }
}
