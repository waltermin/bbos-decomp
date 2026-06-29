package net.rim.device.apps.internal.browser.core;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.util.RunnableThread;

final class BrowserImpl$NavigationThread extends RunnableThread {
   private final BrowserImpl this$0;
   static final int MENU_SHOW_REQUEST;
   static final int FOLLOW_LINK_UNDER_CURSOR_REQUEST;
   static final int CLOSE_BROWSER_REQUEST;
   static final int FETCH_REQUEST;
   static final int ABORT_REQUEST;
   static final int HOT_KEY;
   static final int SHOW_DEBUG_SCREEN;
   static final int ESCAPE_REQUEST;
   static final int PERFORM_DEFAULT_ACTION;
   static final int UPDATE_BROWSER_STATE;
   static final int ACTIVATE_BROWSER_REQUEST;
   static final int RUNNABLE;

   BrowserImpl$NavigationThread(BrowserImpl _1) {
      this.this$0 = _1;
   }

   final void requestExclusiveTask(int task) {
      this.addExclusiveToQueue(new BrowserImpl$NavigationRequest(this.this$0, task, null));
   }

   final void requestExclusiveHotKeyInvoke(Verb verb) {
      this.addExclusiveToQueue(new BrowserImpl$NavigationRequest(this.this$0, 6, verb));
   }

   final void requestHotKeyInvoke(Verb verb) {
      this.addToQueue(new BrowserImpl$NavigationRequest(this.this$0, 6, verb));
   }

   final void requestFetchRequest(FetchRequest fetchRequest) {
      this.addToQueue(new BrowserImpl$NavigationRequest(this.this$0, 4, fetchRequest));
   }

   final void requestTask(int task, Object context) {
      this.addToQueue(new BrowserImpl$NavigationRequest(this.this$0, task, context));
   }

   @Override
   protected final void runItem(Object obj) {
      if (obj instanceof BrowserImpl$NavigationRequest) {
         ((BrowserImpl$NavigationRequest)obj).run();
      }
   }
}
