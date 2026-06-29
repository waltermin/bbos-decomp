package net.rim.device.apps.internal.remindermanager;

import java.util.TimeZone;
import java.util.Vector;
import net.rim.device.api.notification.NotificationsEngineListener;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.reminders.Reminder;
import net.rim.device.apps.api.reminders.ReminderElement;
import net.rim.device.apps.api.reminders.ReminderManager;
import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.api.reminders.ReminderProvider;
import net.rim.device.apps.internal.api.quincy.QuincyManager;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Array;
import net.rim.vm.Monitor;

final class ReminderDisplayManager implements NotificationsEngineListener, DialogClosedListener {
   private ReminderElement _reminderBeingHandled;
   private long _reminderBeingHandledTime = Long.MAX_VALUE;
   private ReminderProvider _reminderProviderBeingHandled;
   private ReminderDisplayManager$ReminderQueueProcessor _reminderQueueProcessor;
   private Dialog _reminderDialog;
   private ReminderApplication _app;
   private Verb[] _actions = new Object[0];
   private Verb _dismissAction = new ReminderDisplayManager$DismissAction();
   private Verb _dismissAllAction = new ReminderDisplayManager$DismissAllAction();
   private Vector _reminderQueue = (Vector)(new Object());
   private ContextObject _context = (ContextObject)(new Object());
   private Proxy _proxy;
   private Vector _globalScreenQueue = (Vector)(new Object());
   private static final long REMINDER_DISPLAY_MANAGER_KEY = -1132927491084502231L;

   final void init(ReminderApplication app, ReminderManager reminderManager) {
      this._app = app;
      this._reminderQueueProcessor = new ReminderDisplayManager$ReminderQueueProcessor(this);
      this._app.addGlobalEventListener(this._reminderQueueProcessor);
      ContextObject.put(this._context, -442409970680484936L, new Object(-7539463754156969819L));
      this._proxy = Proxy.getInstance();
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(-1132927491084502231L, this);
      this._reminderQueueProcessor.start();
   }

   static final ReminderDisplayManager getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (ReminderDisplayManager)ar.get(-1132927491084502231L);
   }

   final ContextObject getContext() {
      return this._context;
   }

   private final void syncHideReminderUI(ReminderElement element) {
      synchronized (this._reminderQueue) {
         this.hideReminderUI(element);
      }

      this.processGlobalScreenQueue();
   }

   private final void hideReminderUI(ReminderElement element) {
      if (this._reminderBeingHandled != null
         && element != null
         && (this._reminderBeingHandled.getReminderID() == element.getReminderID() || this._reminderBeingHandled == element)) {
         if (this._reminderDialog != null) {
            synchronized (this._globalScreenQueue) {
               this._globalScreenQueue.addElement(new ReminderDisplayManager$GlobalScreenOperation(this, this._reminderDialog, 2));
            }

            this._reminderDialog = null;
         }

         this._reminderBeingHandled = null;
         Array.resize(this._actions, 0);
      }
   }

   private final boolean queueReminderDialog(Reminder reminder, ReminderProvider reminderProvider, long reminderTime) {
      try {
         if (reminder == null) {
            ReminderManager.logEvent(1363562016, 2);
            return false;
         }

         long adjustedCurrentTime = ReminderDeepTimerScheduler.getAdjustedCurrentTimeMillis();
         ReminderModel rm = reminder.getReminderData();
         if (rm == null) {
            ReminderManager.logEvent(1363561760, 2);
            return false;
         }

         if (rm.getState() == 3) {
            ReminderManager.logEvent(1363563808, 5);
            return false;
         }

         if (!rm.hasReminder()) {
            ReminderManager.logEvent(1363563040, 5);
            return false;
         }

         String description = null;
         description = reminder.getReminderDescription();
         Verb[] actions = this._actions;
         int defaultAction = 0;
         reminder = reminderProvider.updateReminderState(reminder.getReminderID(), 2, reminderTime);
         if (reminder instanceof Object) {
            VerbProvider vp = (VerbProvider)reminder;
            this._context.put(2006691216517637157L, new Object(reminderTime));
            this._context.setFlag(36);
            vp.getVerbs(this._context, actions);
            int size = actions.length;
            Array.resize(actions, size + 1);
            defaultAction = size;
            actions[size++] = this._dismissAction;
            int unhandledCount = reminderProvider.getUnhandledCount();
            if (unhandledCount > 1) {
               Array.resize(actions, size + 1);
               actions[size++] = this._dismissAllAction;
            }

            long snoozeMillis = reminderProvider.getSnoozeAmount();
            if (snoozeMillis > 0) {
               int numChoices = ReminderManager.SNOOZE_CHOICES.length;
               int choiceLocation = Arrays.binarySearch(ReminderManager.SNOOZE_CHOICES, snoozeMillis, 0, numChoices);
               if (choiceLocation >= 0) {
                  Array.resize(actions, size + 1);
                  actions[size++] = new ReminderDisplayManager$Snooze(choiceLocation);
               }
            }
         }

         EncodedImage image = reminderProvider.getReminderIcon();
         if (image == null) {
            image = ThemeManager.getActiveTheme().getImage("LargeReminder.gif");
         }

         this._reminderDialog = new ReminderDisplayManager$ReminderDialog(description, actions, null, defaultAction, null, 0);
         this._reminderDialog.setIcon(image);
         this._reminderDialog.setDialogClosedListener(this);
         ReminderManager.logEvent(1381192529, 0);
         synchronized (this._globalScreenQueue) {
            this._globalScreenQueue.addElement(new ReminderDisplayManager$GlobalScreenOperation(this, this._reminderDialog, 1));
         }

         return true;
      } finally {
         ReminderManager.logEvent(1363559768, 2);
         return false;
      }
   }

   @Override
   public final void proceedWithDeferredEvent(long sourceIdLong, long eventIdLong, Object eventReferenceObject, Object contextObject) {
      if (eventReferenceObject instanceof Object) {
         this._proxy.invokeRunnable(new ReminderDisplayManager$ProceedRunnable((ReminderElement)eventReferenceObject));
      }
   }

   @Override
   public final void deferredEventWasSuperseded(long sourceIdLong, long eventIdLong, Object eventReferenceObject, Object contextObject) {
      this._proxy.invokeRunnable(new ReminderDisplayManager$HideRunnable((ReminderElement)eventReferenceObject));
   }

   @Override
   public final void notificationsEngineStateChanged(int stateInt, long sourceIdLong, long eventIdLong, Object eventReferenceObject, Object contextObject) {
   }

   private final void addToQueue(ReminderElement element) {
      synchronized (this._reminderQueue) {
         ReminderProvider reminderProvider = element.getReminderProvider();
         Reminder reminder = reminderProvider.getReminder(element.getReminderID());
         if (reminder == null) {
            ReminderManager.logEvent(1094993457, 2);
            NotificationsManager.cancelDeferredEvent(reminderProvider.getProfileID(element.getReminderID()), 0, element, 1, this._context);
            NotificationsManager.cancelImmediateEvent(reminderProvider.getProfileID(element.getReminderID()), 0, element, null);
         } else {
            int state = reminder.getReminderState();
            ReminderModel rm = reminder.getReminderData();
            if (state != 2 && rm != null && rm.hasReminder()) {
               reminderProvider.reminderQueued(element.getReminderID());
            }

            this._reminderQueue.addElement(element);
            this._reminderQueue.notify();
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void dialogClosed(Dialog dialog, int choice) {
      ReminderManager.logEvent(1380205390, 0);
      boolean snooze = false;
      boolean dismissAll = false;
      Verb actionVerb = null;
      if (choice >= 0 && choice < this._actions.length) {
         actionVerb = this._actions[choice];
      }

      if (actionVerb instanceof ReminderDisplayManager$Snooze) {
         snooze = true;
      } else if (actionVerb instanceof ReminderDisplayManager$DismissAllAction) {
         dismissAll = true;
      }

      ReminderElement reminderDetails = null;
      synchronized (this._reminderQueue) {
         boolean var20 = false /* VF: Semaphore variable */;

         label121: {
            try {
               var20 = true;
               dialog.setDialogClosedListener(null);
               if (this._reminderBeingHandled != null) {
                  if (!snooze) {
                     this._reminderProviderBeingHandled.updateReminderState(this._reminderBeingHandled.getReminderID(), 3, this._reminderBeingHandledTime);
                     if (dismissAll) {
                        ReminderProvider rp = this._reminderBeingHandled.getReminderProvider();
                        Object[] unhandledReminders = rp.getUnhandledReminders();
                        if (unhandledReminders != null) {
                           for (int i = 0; i < unhandledReminders.length; i++) {
                              Object var10000 = unhandledReminders[i];
                              if (unhandledReminders[i] instanceof Object) {
                                 ReminderElement re = (ReminderElement)var10000;
                                 long reminderID = re.getReminderID();
                                 Reminder reminder = rp.getReminder(reminderID);
                                 if (reminder != null) {
                                    long reminderTime = reminder.getReminderTime(TimeZone.getDefault());
                                    rp.updateReminderState(reminderID, 3, reminderTime);
                                    rp.reminderHandled(reminderID);
                                 }
                              }
                           }
                        }
                     }
                  }

                  long reminderBeingHandledID = this._reminderBeingHandled.getReminderID();
                  NotificationsManager.cancelImmediateEvent(
                     this._reminderProviderBeingHandled.getProfileID(reminderBeingHandledID), reminderBeingHandledID, this._reminderBeingHandled, null
                  );
                  if (!dismissAll) {
                     NotificationsManager.cancelDeferredEvent(
                        this._reminderProviderBeingHandled.getProfileID(reminderBeingHandledID),
                        reminderBeingHandledID,
                        this._reminderBeingHandled,
                        1,
                        this._context
                     );
                  } else {
                     NotificationsManager.cancelAllDeferredEvents(this._reminderProviderBeingHandled.getProfileID(reminderBeingHandledID), 1, this._context);
                  }

                  this._reminderProviderBeingHandled.reminderHandled(reminderBeingHandledID);
                  var20 = false;
                  break label121;
               }

               var20 = false;
            } finally {
               if (var20) {
                  QuincyManager.sendUncaughtException("ReminderDialog:Close");
                  ContextObject contextObject = (ContextObject)(new Object());
                  ContextObject.put(contextObject, -442409970680484936L, new Object(-7539463754156969819L));
                  if (this._reminderProviderBeingHandled != null && this._reminderBeingHandled != null) {
                     NotificationsManager.cancelAllDeferredEvents(
                        this._reminderProviderBeingHandled.getProfileID(this._reminderBeingHandled.getReminderID()), 1, contextObject
                     );
                  }
                  break label121;
               }
            }

            return;
         }

         reminderDetails = this._reminderBeingHandled;
         this._reminderBeingHandled = null;
         this._reminderProviderBeingHandled = null;
         this._reminderDialog = null;
         this._reminderQueue.notify();
      }

      Array.resize(this._actions, 0);
      if (actionVerb != null && !dismissAll) {
         this._app.setForegroundable(true);
         actionVerb.invoke(reminderDetails);
         this._app.setForegroundable(false);
      }
   }

   private final void processGlobalScreenQueue() {
      if (Monitor.monitorOwned(this._reminderQueue)) {
         ReminderManager.logEvent(1380273231, 2);
      }

      synchronized (this._app.getAppEventLock()) {
         synchronized (this._globalScreenQueue) {
            int size = this._globalScreenQueue.size();

            for (int i = 0; i < size; i++) {
               try {
                  ReminderDisplayManager$GlobalScreenOperation op = (ReminderDisplayManager$GlobalScreenOperation)this._globalScreenQueue.elementAt(i);
                  op.execute();
               } finally {
                  ;
               }
            }

            this._globalScreenQueue.setSize(0);
         }
      }
   }
}
