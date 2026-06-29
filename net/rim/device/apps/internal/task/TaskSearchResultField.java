package net.rim.device.apps.internal.task;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.search.SearchResultField;
import net.rim.device.apps.api.task.TaskModel;
import net.rim.device.apps.internal.task.resources.TaskResources;

final class TaskSearchResultField extends CollectionListField implements SearchResultField, VerbProvider {
   public TaskSearchResultField(TaskSearchResultCollection results, ListFieldCallback callback) {
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      Object selectedElement = this.getSelectedElement();
      if (selectedElement instanceof TaskModel) {
         if (key == '\n') {
            this.getScreen().invokeDefaultMenuItem(0);
            return true;
         }

         if (key == ' ') {
            if (TaskUtilities.toggleStatus((TaskModel)selectedElement)) {
               return true;
            }
         } else if (key == 127 || Keypad.getAltedChar(key) == 127) {
            new DeleteTaskVerb((TaskModel)selectedElement).invoke(null);
            return true;
         }
      }

      return true;
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            Object selectedObject = this.getSelectedObject();
            if (selectedObject != null) {
               this.getScreen().invokeDefaultMenuItem(0);
               return true;
            }
         default:
            return super.invokeAction(action);
      }
   }

   @Override
   protected final boolean keyDown(int keyCode, int time) {
      if ((Keypad.status(keyCode) & 1) != 0) {
         int key = Keypad.key(keyCode);
         if (TaskResources.getString(43).indexOf(key) != -1 && TaskUtilities.toggleStatus((TaskModel)this.getSelectedElement())) {
            return true;
         }
      }

      return super.keyDown(keyCode, time);
   }

   @Override
   public final Object getSelectedObject() {
      return this.getSelectedElement();
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Object selectedElement = this.getSelectedElement();
      return !(selectedElement instanceof VerbProvider) ? null : ((VerbProvider)selectedElement).getVerbs(context, verbs);
   }
}
