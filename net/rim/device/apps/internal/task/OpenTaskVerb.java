package net.rim.device.apps.internal.task;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.task.TaskCollection;
import net.rim.device.apps.api.task.TaskModel;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.internal.i18n.CommonResource;

final class OpenTaskVerb extends Verb implements SetParameter {
   Object _record;
   TaskCollection _taskCollection;
   static OpenTaskVerb _theVerb;

   @Override
   public final String toString() {
      return CommonResource.getString(15);
   }

   OpenTaskVerb() {
      super(611424);
   }

   static final OpenTaskVerb getInstance(Object record) {
      if (_theVerb == null) {
         _theVerb = new OpenTaskVerb();
      }

      _theVerb._record = record;
      _theVerb._taskCollection = Task.getInstance().getUICollection();
      return _theVerb;
   }

   @Override
   public final Object invoke(Object context) {
      if (this._record != null) {
         Application app = Application.getApplication();
         boolean wasBackground = !app.isForeground();
         if (wasBackground) {
            app.requestForeground();
         }

         Object newTask;
         if (ObjectGroup.isInGroup(this._record)) {
            newTask = ObjectGroup.expandGroup(this._record);
         } else {
            newTask = this._record;
         }

         ContextObject co = ContextObject.castOrCreate(context);
         co.setPrivateFlag(-3866311304884942232L, 1);
         if (co == context) {
            co = ContextObject.clone(context);
         }

         co.setFlag(0);
         co.setFlag(28);
         co.clearFlag(31);
         EditTaskScreen taskEditScreen = new EditTaskScreen(this._taskCollection, null, co);
         taskEditScreen.setTaskModel((TaskModelImpl)newTask);
         taskEditScreen.go();
         if (wasBackground) {
            app.requestBackground();
         }
      }

      return null;
   }

   @Override
   public final void setParameter(Object parameter) {
      if (parameter instanceof TaskModel) {
         this._record = parameter;
         this._taskCollection = Task.getInstance().getUICollection();
      }
   }
}
