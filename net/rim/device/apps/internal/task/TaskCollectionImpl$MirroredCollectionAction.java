package net.rim.device.apps.internal.task;

import net.rim.device.api.collection.Collection;

final class TaskCollectionImpl$MirroredCollectionAction implements Runnable {
   private int _action;
   private Collection _collection;
   private Object _newObject;
   private Object _oldObject;
   private final TaskCollectionImpl this$0;
   public static final int RELOAD;
   public static final int ADD;
   public static final int UPDATE;
   public static final int REMOVE;

   public TaskCollectionImpl$MirroredCollectionAction(TaskCollectionImpl _1, int action, Collection collection, Object newObject, Object oldObject) {
      this.this$0 = _1;
      this._action = action;
      this._collection = collection;
      this._newObject = newObject;
      this._oldObject = oldObject;
   }

   @Override
   public final void run() {
      if (this._action == 1) {
         this.this$0._mirroredCollection.reload(this._collection);
      } else if (this._action == 2) {
         this.this$0._mirroredCollection.add(this._newObject);
      } else if (this._action == 3) {
         this.this$0._mirroredCollection.update(this._oldObject, this._newObject);
      } else {
         if (this._action == 4) {
            this.this$0._mirroredCollection.remove(this._newObject);
         }
      }
   }
}
