package net.rim.device.apps.internal.browser.wml;

import net.rim.device.apps.api.framework.verb.Verb;

class TaskContainer extends Verb {
   private Task _task;

   TaskContainer(int menuOrdering) {
   }

   TaskContainer(TaskContainer taskContainer) {
      super(taskContainer._ordering);
      this._task = taskContainer._task;
   }

   void setTask(Task task) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   Task getTask() {
      return this._task;
   }

   void addSetVar(WMLVariable name, WMLVariable value) {
      if (this._task != null) {
         this._task.addSetVar(name, value);
      }
   }

   void setBrowserContent(WMLBrowserContent browserContent) {
      if (this._task != null) {
         this._task.setBrowserContent(browserContent);
      }
   }

   @Override
   public Object invoke(Object context) {
      if (this._task != null) {
         this._task.invoke(context);
      }

      return null;
   }

   String getURL() {
      return this._task != null ? this._task.getURL() : null;
   }
}
