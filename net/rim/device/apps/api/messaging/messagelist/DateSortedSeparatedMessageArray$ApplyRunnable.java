package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.ribbon.indicators.IndicatorManager;
import net.rim.device.apps.api.ui.DialogWithBackgroundThread;
import net.rim.device.apps.api.ui.DialogWithBackgroundThreadRunnable;

class DateSortedSeparatedMessageArray$ApplyRunnable implements DialogWithBackgroundThreadRunnable {
   int _lowValue;
   int _highValue;
   long _action;
   Object _context;
   boolean _askedToStop;
   DialogWithBackgroundThread _DialogWithBackgroundThread;
   private final DateSortedSeparatedMessageArray this$0;
   private static final int PERSISTENT_GC_THRESHOLD;
   private static final int NOTIFY_THRESHOLD;
   private static final int PROGRESS_INCREMENT_DIVISOR;

   DateSortedSeparatedMessageArray$ApplyRunnable(DateSortedSeparatedMessageArray _1) {
      this.this$0 = _1;
      this._askedToStop = false;
   }

   void setValues(int lowValue, int highValue, long action, Object context) {
      this._lowValue = lowValue;
      this._highValue = highValue;
      this._action = action;
      this._context = context;
   }

   @Override
   public void stop() {
      this._askedToStop = true;
   }

   @Override
   public void setDialogWithBackgroundThread(DialogWithBackgroundThread dialogWithBackgroundThread) {
      this.this$0._dialogWithBackgroundThread = dialogWithBackgroundThread;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      int unseparatedStartIndex = 0;
      int unseparatedEndIndex = 0;
      boolean shouldPerformGC = false;
      boolean tooLargeForNotify = true;
      IndicatorManager im = IndicatorManager.getInstance();
      synchronized (RIMPersistentStore.getSynchObject()) {
         synchronized (FolderHierarchies.getLockObject()) {
            boolean var20 = false /* VF: Semaphore variable */;

            try {
               var20 = true;
               int size = 0;
               if (this._lowValue < 0) {
                  this._lowValue = 0;
               }

               size = this.this$0.size();
               if (this._highValue >= size) {
                  this._highValue = size - 1;
               }

               unseparatedStartIndex = this.this$0.getUnseparatedIndexOnOrBefore(this._highValue);
               unseparatedEndIndex = this.this$0.getUnseparatedIndexOnOrAfter(this._lowValue);
               if (unseparatedStartIndex > unseparatedEndIndex) {
                  int temp = unseparatedStartIndex;
                  unseparatedStartIndex = unseparatedEndIndex;
                  unseparatedEndIndex = temp;
               }

               if (unseparatedEndIndex - unseparatedStartIndex < 64) {
                  tooLargeForNotify = false;
               }

               if (tooLargeForNotify) {
                  this.this$0.suspendNotification(null);
               }

               if (im != null) {
                  im.suspendIndicatorUpdates();
               }

               boolean orderPreservingChanges = this._action == -6225946334564270161L || this._action == 5213547777258110094L;
               Object contextObject = this._context;
               if (orderPreservingChanges) {
                  contextObject = ContextObject.clone(this._context);
                  ContextObject.setFlag(contextObject, 62);
               }

               for (int i = unseparatedEndIndex; i >= unseparatedStartIndex; i--) {
                  Object object = this.this$0._dateSortedItems.getAt(i);
                  if (object instanceof Object) {
                     ActionProvider actionProvider = (ActionProvider)object;
                     actionProvider.perform(this._action, contextObject);
                  }
               }

               if (tooLargeForNotify) {
                  this.this$0.resumeNotification(contextObject);
               }

               if (this._action == -3967872215949752466L) {
                  if (unseparatedEndIndex - unseparatedStartIndex > 10) {
                     shouldPerformGC = true;
                     var20 = false;
                  } else {
                     var20 = false;
                  }
               } else {
                  var20 = false;
               }
            } finally {
               if (var20) {
                  this.this$0._dialogWithBackgroundThread.dismiss();
                  if (im != null) {
                     im.resumeIndicatorUpdates();
                  }
               }
            }

            this.this$0._dialogWithBackgroundThread.dismiss();
            if (im != null) {
               im.resumeIndicatorUpdates();
            }
         }
      }

      if (shouldPerformGC) {
         Application.getApplication().invokeAndWait(new DateSortedSeparatedMessageArray$GCRunnable(null));
      }
   }
}
