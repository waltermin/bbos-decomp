package net.rim.device.apps.internal.task;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.task.resources.TaskResources;
import net.rim.device.internal.i18n.CommonResource;

final class DeleteTaskVerb extends Verb {
   private Object _taskModel;
   private boolean _popScreen;
   private char _keyPressed;

   DeleteTaskVerb(Object taskModel) {
      this(taskModel, false);
   }

   DeleteTaskVerb(Object taskModel, char keyPressed) {
      this(taskModel, false);
      this._keyPressed = keyPressed;
   }

   DeleteTaskVerb(Object taskModel, boolean popScreen) {
      super(611952);
      this._taskModel = taskModel;
      this._popScreen = popScreen;
   }

   @Override
   public final String toString() {
      return CommonResource.getString(17);
   }

   @Override
   public final Object invoke(Object context) {
      boolean popScreen = this._popScreen;
      ResourceBundle rb = ResourceBundle.getBundle(2546929077131262015L, "net.rim.device.apps.internal.resource.Task");
      Application app = Application.getApplication();
      boolean wasBackground = !app.isForeground();
      if (wasBackground) {
         app.requestForeground();
      }

      TaskModelImpl taskToDelete = (TaskModelImpl)this._taskModel;
      TaskDataModel taskDataModel = taskToDelete.getTaskDataModel();
      Recur recurInfo = taskDataModel.getRecurrenceModel();
      if (recurInfo != null && recurInfo.getRecurType() != 0) {
         String[] choices = new String[]{rb.getString(50), rb.getString(51), CommonResource.getString(19)};
         int[] values = new int[]{0, 1, -1, -805044216, 67108864, 1953066601, -805044196, 1632896256, 7564147, 1935758355, 1850286187, 1634301033};
         switch (Dialog.ask(rb.getString(49), choices, values, -1)) {
            case -1:
               popScreen = false;
               taskToDelete = null;
            case 0:
               break;
            case 1:
            default:
               taskToDelete = TaskUtilities.advanceTaskRecurrence(taskToDelete, true, 0, null);
               if (taskToDelete == null) {
                  taskToDelete = (TaskModelImpl)this._taskModel;
               }
         }
      } else if (TaskOptions.getOptions().getConfirmDelete() || this._keyPressed == '\b') {
         int retVal = Dialog.ask(2, TaskResources.getString(40), -1);
         if (retVal != 3) {
            popScreen = false;
            taskToDelete = null;
         }
      }

      if (taskToDelete != null) {
         if (context instanceof ContextObject) {
            ((ContextObject)context).setPrivateFlag(-3866311304884942232L, 1);
         }

         Task.getInstance().getUICollection().remove(taskToDelete);
      }

      if (popScreen) {
         UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
      }

      if (wasBackground) {
         app.requestBackground();
      }

      return this._taskModel;
   }
}
