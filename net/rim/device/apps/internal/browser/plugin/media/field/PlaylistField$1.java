package net.rim.device.apps.internal.browser.plugin.media.field;

import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;

final class PlaylistField$1 implements Runnable {
   private final int val$item;
   private final PlaylistField this$0;

   PlaylistField$1(PlaylistField _1, int _2) {
      this.this$0 = _1;
      this.val$item = _2;
   }

   @Override
   public final void run() {
      this.this$0._list.setSelectedIndex(this.val$item);
      this.this$0._list.invalidate();
      XYRect rect = Ui.getTmpXYRect();
      rect.x = 0;
      rect.y = this.this$0._list.getYForRow(this.val$item);
      rect.height = this.this$0._list.getRowHeight(this.val$item);
      rect.width = this.this$0.getWidth();
      PlaylistField.access$100(this.this$0, false, rect, true, false);
      Ui.returnTmpXYRect(rect);
   }
}
