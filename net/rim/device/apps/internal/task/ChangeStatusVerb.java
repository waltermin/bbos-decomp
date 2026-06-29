package net.rim.device.apps.internal.task;

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.task.TaskModel;
import net.rim.device.apps.internal.task.resources.TaskResources;

final class ChangeStatusVerb extends Verb {
   private int _descriptionId;
   private int _newStatus;

   private ChangeStatusVerb(int ordering, int descriptionId, int newStatus) {
      super(ordering);
      this._descriptionId = descriptionId;
      this._newStatus = newStatus;
   }

   static final Verb getInstance(TaskModel taskModel, int ordering, int descriptionId, int newStatus) {
      return new CollectionUpdateWrapper(taskModel, new ChangeStatusVerb(ordering, descriptionId, newStatus));
   }

   @Override
   public final String toString() {
      return TaskResources.getString(this._descriptionId);
   }

   @Override
   public final Object invoke(Object context) {
      if (context instanceof TaskModelImpl) {
         TaskModelImpl taskModel = (TaskModelImpl)context;
         TaskModelImpl result = taskModel;
         TaskDataModel taskDataModel = taskModel.getTaskDataModel();
         Recur recurInfo = taskDataModel.getRecurrenceModel();
         if (this._newStatus == 2 && recurInfo != null && recurInfo.getRecurType() != 0) {
            result = TaskUtilities.advanceTaskRecurrence(taskModel, false, this._newStatus, null);
            if (result == null) {
               TaskModelImpl lastTaskInstance = (TaskModelImpl)taskModel.clone(null);
               lastTaskInstance.setStatus(this._newStatus);
               lastTaskInstance.getReminderModel().setTime(-1);
               lastTaskInstance.getRecurrenceModel().setRecurType((byte)0);
               if (!ObjectGroup.isInGroup(lastTaskInstance)) {
                  ObjectGroup.createGroupIgnoreTooBig(lastTaskInstance);
               }

               TaskCollectionImpl taskCollection = TaskCollectionImpl.getInstance();
               taskCollection.add(lastTaskInstance);
               return null;
            }

            return result;
         }

         if (TaskUtilities.getCICALConfiguration().isInfiniteRecurrenceAllowed() && this._newStatus == 2) {
            taskDataModel.getReminderModel().setTime(-1);
         }

         if (result == taskModel) {
            taskDataModel.setStatus(this._newStatus);
         }
      }

      return context;
   }
}
