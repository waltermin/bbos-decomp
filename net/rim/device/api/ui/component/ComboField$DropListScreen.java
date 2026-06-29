package net.rim.device.api.ui.component;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.tid.itie.EventHandler;

class ComboField$DropListScreen extends Screen implements TextInputDialog, TextInputObscuringScreen {
   private int _x;
   private int _y;
   private int _width;
   private final ComboField this$0;

   public ComboField$DropListScreen(ComboField _1) {
      super(new VerticalFieldManager(281474976710656L), 281474976710656L);
      this.this$0 = _1;
      this.add(_1._list);
   }

   @Override
   protected void paint(Graphics g) {
      g.drawRect(0, 0, this.getWidth(), this.getHeight());
      super.paint(g);
   }

   public void setPositionAndWidth(int x, int y, int width) {
      this._x = x;
      this._y = y;
      this._width = width;
   }

   public void update() {
      this.updateLayout();
   }

   @Override
   protected void sublayout(int width, int height) {
      int oldHeight = height;
      int padding = 3;
      width = Math.min(this._width, width - this._x);
      height -= this._y;
      this.layoutDelegate(width - 2 * padding, height - 2 * padding);
      this.setPositionDelegate(padding, padding);
      height = this.this$0._list.getHeight() + 2 * padding;
      XYRect rect = new XYRect(this.this$0._editable.getExtent());
      this.this$0._editable.transformToScreen(rect);
      int availableSpace = Display.getHeight() - rect.Y2();
      int preferredVisibleHeight = Math.min(height, this.this$0._list.getRowHeight() * 3 + 2 * padding);
      if (preferredVisibleHeight > availableSpace) {
         Manager manager = this.this$0._editable.getManager();
         int maxScroll = manager == null ? 0 : this.this$0._editable.getExtent().y - manager.getVerticalScroll();

         while (manager != null && !manager.isStyle(281474976710656L)) {
            Manager newManager = manager.getManager();
            maxScroll += newManager == null ? 0 : manager.getExtent().y - newManager.getVerticalScroll();
            manager = newManager;
         }

         int maxSpace = availableSpace + maxScroll;
         if (manager != null && maxSpace >= preferredVisibleHeight) {
            manager.setVerticalScroll(manager.getVerticalScroll() + preferredVisibleHeight - availableSpace);
            rect = new XYRect(this.this$0._editable.getExtent());
            this.this$0._editable.transformToScreen(rect);
            this._y = rect.Y2();
            this.layoutDelegate(width - 2 * padding, oldHeight - this._y - 2 * padding);
         } else if (availableSpace < rect.y) {
            rect = new XYRect(this.this$0._editable.getExtent());
            this.this$0._editable.transformToScreen(rect);
            this._y = Math.max(0, rect.y - height);
            height = rect.y - this._y;
            this.layoutDelegate(width - 2 * padding, height - 2 * padding);
         }
      }

      this.setPosition(this._x, this._y);
      this.setExtent(width, height);
   }

   @Override
   public int processKeyEvent(int event, char keyOrStatus, int keycode, int time) {
      int checkKey = Keypad.key(keycode);
      if (event == 513 && checkKey == 10) {
         this.this$0._control.select(this.getSelectedObject(), 3);
         return 0;
      }

      if (event == 513 && checkKey == 27) {
         this.this$0._control.escape();
         return 0;
      }

      switch (event) {
         case 513:
         case 514:
         case 515:
         case 520:
            int result = 0;
            boolean handled = false;
            if (this.this$0.usingSureType()) {
               result = EventHandler.getInstance().processKeyEvent(event, keycode, keyOrStatus, keycode, time, true);
               handled = (result & 65536) == 65536;
            }

            if (!handled) {
               Screen below = this.getScreenBelow();
               if (below != null) {
                  result = below.processKeyEvent(event, keyOrStatus, keycode, time);
               }
            }

            if (Ui.isTTSEnabled()) {
               super.accessibleEventOccurred(6, new Integer(1), new Integer(2), this.this$0._list);
            }

            return result;
         default:
            return 0;
      }
   }

   @Override
   public boolean processNavigationEvent(int event, int dx, int dy, int status, int time) {
      switch (event) {
         case 516:
            this.this$0._control.select(this.getSelectedObject(), 1);
            return true;
         case 519:
            if ((status & 8) == 0) {
               return false;
            }
         default:
            if (Ui.isTTSEnabled()) {
               super.accessibleEventOccurred(6, new Integer(1), new Integer(2), this.this$0._list);
            }

            return EventHandler.getInstance().processNavigationEvent(event, dx, dy, status, time);
         case 6914:
            this.this$0._control.select(this.getSelectedObject(), 2);
            return true;
      }
   }

   private Object getSelectedObject() {
      int selectionIndex = this.this$0._list.getSelectedIndex();
      if (selectionIndex == -1) {
         return null;
      }

      ListFieldCallback callback = this.this$0._list.getCallback();
      return callback == null ? null : callback.get(this.this$0._list, selectionIndex);
   }
}
