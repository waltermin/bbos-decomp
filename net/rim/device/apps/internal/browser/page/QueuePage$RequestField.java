package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.browser.ui.BrowserIcons;
import net.rim.device.internal.ui.IconCollection;

final class QueuePage$RequestField extends Field {
   private IconCollection _icons = BrowserIcons.getIcons();
   private String _queue;
   private PageModel _pageModel;

   public QueuePage$RequestField(String queue, PageModel pageModel) {
      super(18014398509482048L);
      this._queue = queue;
      this._pageModel = pageModel;
   }

   public final PageModel getPageModel() {
      return this._pageModel;
   }

   public final void update() {
      this.invalidate();
   }

   @Override
   public final int getPreferredWidth() {
      Font font = this.getFont();
      int width = this._icons.getWidth(font);
      width += font.getHeight() >> 2;
      return width + font.getBounds(this._pageModel.getTitle());
   }

   @Override
   public final int getPreferredHeight() {
      return this.getFont().getHeight();
   }

   @Override
   protected final void layout(int width, int height) {
      Font font = this.getFont();
      if ((this.getStyle() & 1152921504606846976L) == 0) {
         width = Math.min(this.getPreferredWidth(), width);
      }

      height = font.getHeight();
      this.setExtent(width, height);
   }

   @Override
   protected final void paint(Graphics graphics) {
      char icon;
      switch (this._pageModel.getStatus()) {
         case 0:
            icon = 3;
            break;
         case 1:
         default:
            icon = 0;
            break;
         case 2:
            icon = 1;
            break;
         case 3:
            icon = 2;
            break;
         case 4:
            icon = 3;
            break;
         case 5:
            icon = 4;
      }

      int x = this._icons.paint(graphics, 0, 0, icon);
      x += graphics.getFont().getHeight() >> 2;
      graphics.drawText(this._pageModel.getTitle(), x, 0, this.getFieldStyle(), this.getWidth() - x);
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu, int instance) {
      super.makeContextMenu(contextMenu, instance);
      if (this._pageModel.isCompleted()) {
         VerbMenuItem menuItem = (VerbMenuItem)(new Object(new QueuePage$ViewRequestVerb(this._pageModel), 1));
         contextMenu.addItem(menuItem);
         menuItem = (VerbMenuItem)(new Object(new QueuePage$DeleteRequestVerb(this._queue, this._pageModel), 1));
         contextMenu.addItem(menuItem);
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         if (this._pageModel.isCompleted()) {
            new QueuePage$ViewRequestVerb(this._pageModel).invoke(null);
            return true;
         }
      } else if ((key == 127 || Keypad.getAltedChar(key) == 127) && this._pageModel.isCompleted()) {
         new QueuePage$DeleteRequestVerb(this._queue, this._pageModel).invoke(null);
         return true;
      }

      return super.keyChar(key, status, time);
   }
}
