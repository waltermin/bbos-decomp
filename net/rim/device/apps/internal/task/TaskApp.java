package net.rim.device.apps.internal.task;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.pim.TimeBasedCollection;
import net.rim.device.apps.api.reminders.ReminderManager;

public final class TaskApp extends UiApplication {
   private Task _task = Task.getInstance();
   private static TaskReminderProvider _taskReminderProvider;

   private TaskApp() {
   }

   private final Screen getScreen() {
      return this._task.getUI();
   }

   public static final void main(String[] args) {
      if (args != null && args.length == 1 && args[0].equals("init")) {
         Application.setAcceptEventsForProcess(false);
         PackageManager.registerOnceOnSystemStart();
         ReminderManager rm = ReminderManager.getInstance();
         if (rm == null) {
            throw new RuntimeException("Task:RM");
         }

         TaskCollectionImpl taskCollection = TaskCollectionImpl.getInstance();
         _taskReminderProvider = new TaskReminderProvider(rm, taskCollection);
         taskCollection.addCollectionListener(_taskReminderProvider);
         TimeBasedCollection.getInstance().registerProvider(taskCollection, false);
      } else {
         TaskApp app = new TaskApp();
         app.pushScreen(app.getScreen());
         app.enterEventDispatcher();
      }
   }
}
