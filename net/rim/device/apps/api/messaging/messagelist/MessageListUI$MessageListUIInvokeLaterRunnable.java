package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.ui.InvokeLaterRunnable;

final class MessageListUI$MessageListUIInvokeLaterRunnable extends InvokeLaterRunnable {
   private MessageListUI$MessageListUIInvokeLaterRunnable() {
   }

   @Override
   public final void run() {
      synchronized (FolderHierarchies.getLockObject()) {
         super.run();
      }
   }

   MessageListUI$MessageListUIInvokeLaterRunnable(MessageListUI$1 x0) {
      this();
   }
}
