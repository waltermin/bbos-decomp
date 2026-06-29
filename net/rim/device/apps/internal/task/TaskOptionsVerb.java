package net.rim.device.apps.internal.task;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.task.TaskCollection;
import net.rim.device.internal.i18n.CommonResource;

final class TaskOptionsVerb extends Verb {
   TaskList _list;
   TaskCollectionImpl _collection;
   static TaskOptionsVerb _theVerb;

   TaskOptionsVerb() {
      super(16986368);
   }

   @Override
   public final String toString() {
      return CommonResource.getString(20);
   }

   static final TaskOptionsVerb getInstance(TaskList list, TaskCollection collection) {
      if (_theVerb == null) {
         _theVerb = new TaskOptionsVerb();
      }

      _theVerb._list = list;
      _theVerb._collection = (TaskCollectionImpl)collection.getCollectionStore();
      return _theVerb;
   }

   @Override
   public final Object invoke(Object p) {
      TaskOptions.editOptions();
      return null;
   }
}
