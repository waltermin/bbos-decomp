package net.rim.device.apps.internal.task;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.task.resources.TaskResources;

final class ToggleViewableTasks extends Verb {
   TaskUICollection _taskUICollection;
   TaskList _list;
   CollectionListField _listField;
   int _statusToHide;
   static ToggleViewableTasks _theVerb;

   @Override
   public final String toString() {
      return this._statusToHide == 2 ? TaskResources.getString(56) : TaskResources.getString(55);
   }

   ToggleViewableTasks() {
      super(615712);
   }

   static final ToggleViewableTasks getInstance(TaskList list, TaskUICollection view, CollectionListField listField, int statusToHide) {
      if (_theVerb == null) {
         _theVerb = new ToggleViewableTasks();
      }

      _theVerb._taskUICollection = view;
      _theVerb._list = list;
      _theVerb._statusToHide = statusToHide;
      _theVerb._listField = listField;
      return _theVerb;
   }

   @Override
   public final Object invoke(Object context) {
      Application app = Application.getApplication();
      boolean wasBackground = !app.isForeground();
      if (wasBackground) {
         app.requestForeground();
      }

      _theVerb._taskUICollection.setStatusToHide(this._statusToHide);
      this._taskUICollection.reset(TaskCollectionImpl.getInstance());
      this._list.invalidate();
      this._listField.invalidate();
      this._listField.reset(_theVerb._taskUICollection);
      Task.getInstance().getFilteredList().recalculateResults();
      this._listField.setDirty(true);
      this._listField.updateList();
      this._list.updateDisplay();
      if (wasBackground) {
         app.requestBackground();
      }

      return null;
   }
}
