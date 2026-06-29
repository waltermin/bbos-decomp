package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.browser.verbs.FollowLinkVerb;

final class QueuePage$QueueField extends LabelField {
   private String _queue;

   public QueuePage$QueueField(String queue) {
      super(queue, 18014398509481984L);
      this._queue = queue;
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu, int instance) {
      super.makeContextMenu(contextMenu, instance);
      VerbMenuItem menuItem = new VerbMenuItem(new FollowLinkVerb("queue:" + this._queue), 1);
      contextMenu.addItem(menuItem);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         new FollowLinkVerb("queue:" + this._queue).invoke(null);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }
}
