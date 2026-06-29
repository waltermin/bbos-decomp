package net.rim.device.apps.internal.task;

import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.task.TaskCollection;
import net.rim.device.apps.api.task.TaskModel;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.task.resources.TaskResources;
import net.rim.device.internal.i18n.CommonResource;

final class NewTaskVerb extends Verb implements SetParameter, Copyable {
   private String _pattern;
   TaskModel _model;
   TaskCollection _taskCollection;

   @Override
   public final String toString() {
      return CommonResource.getString(10023);
   }

   NewTaskVerb(String pattern) {
      super(610832);
      this._pattern = pattern;
      this._taskCollection = Task.getInstance().getUICollection();
   }

   @Override
   public final Object invoke(Object context) {
      if (this._model == null) {
         this._model = (TaskModel)FactoryUtil.createInstance(-4172790793103625162L, null);
      }

      if (this._pattern != null && this._pattern.length() > 0) {
         this._model.add(FactoryUtil.createInstance(-4904857078378172834L, this._pattern));
      }

      ContextObject co = ContextObject.castOrCreate(context);
      co.setFlag(0);
      co.setFlag(28);
      co.setFlag(31);
      EditTaskScreen taskEditScreen = new EditTaskScreen(this._taskCollection, TaskResources.getString(57), co);
      taskEditScreen.setTaskModel((TaskModelImpl)this._model);
      taskEditScreen.go();
      return this._model;
   }

   @Override
   public final void setParameter(Object parameter) {
      if (parameter instanceof Object) {
         this._model = (TaskModel)parameter;
      }
   }

   @Override
   public final Object copy() {
      return new NewTaskVerb("");
   }
}
