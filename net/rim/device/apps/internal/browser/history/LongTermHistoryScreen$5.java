package net.rim.device.apps.internal.browser.history;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

class LongTermHistoryScreen$5 extends MenuItem {
   private final LongTermHistoryScreen this$0;

   LongTermHistoryScreen$5(LongTermHistoryScreen _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Object item = this.this$0._treeField.getCookie(this.this$0._treeField.getCurrentNode());
      LongTermHistoryNode node = (LongTermHistoryNode)item;
      String time = LongTermHistoryScreen.DATE_FORMAT.formatLocal(node.getTimestamp());
      StringBuffer buffer = (StringBuffer)(new Object());
      buffer.append(BrowserResources.getString(706));
      buffer.append(' ');
      buffer.append(node.getTitle());
      buffer.append("\n\n");
      buffer.append(BrowserResources.getString(411));
      buffer.append(' ');
      buffer.append(node.getUrl());
      buffer.append("\n\n");
      buffer.append(BrowserResources.getString(801));
      buffer.append(' ');
      buffer.append(time);
      Dialog dialog = (Dialog)(new Object(0, buffer.toString(), 0, null, 0));
      dialog.doModal();
   }
}
