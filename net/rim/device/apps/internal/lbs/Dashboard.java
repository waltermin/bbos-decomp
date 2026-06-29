package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public final class Dashboard extends Field {
   private boolean _titleVisible = true;
   private AbstractMode _mode;
   private Bitmap _backgroundBlank = Bitmap.getBitmapResource(
      ((StringBuffer)(new Object("images/lbs_dashboard_blank_"))).append(Display.getWidth()).append(".png").toString()
   );
   private Bitmap _background = Bitmap.getBitmapResource(
      ((StringBuffer)(new Object("images/lbs_dashboard_"))).append(Display.getWidth()).append(".png").toString()
   );
   private static final int TITLE_HEIGHT = 16;

   public Dashboard() {
      super(36028797018963968L);
   }

   public final void setMode(AbstractMode mode) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final AbstractMode getMode() {
      return this._mode;
   }

   public final void setView(int view) {
      if (this._mode != null) {
         this._mode.changeView(view);
      }
   }

   private final void paintBody(Graphics graphics) {
      Bitmap img;
      if (this._mode.hasHeaderToPaint()) {
         img = this._background;
      } else {
         img = this._backgroundBlank;
      }

      graphics.drawBitmap(0, 0, img.getWidth(), img.getHeight(), img, 0, 0);
   }

   private final void paintMode(Graphics graphics) {
      int titleHeight = 0;
      if (this._titleVisible && this._mode.hasHeaderToPaint()) {
         titleHeight = 16;
         graphics.pushContext(0, this.getHeight() - titleHeight, this.getWidth(), titleHeight, 0, this.getHeight() - titleHeight);
         this._mode.paintHeader(graphics, this.getWidth(), titleHeight);
         graphics.popContext();
      }

      graphics.pushContext(0, 0, this.getWidth(), this.getHeight() - titleHeight, 0, 0);
      this._mode.paintBody(graphics, this.getWidth(), this.getHeight() - titleHeight);
      graphics.popContext();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void refresh() {
      try {
         this.invalidate();
      } catch (Throwable var3) {
         EventLogger.logEvent(LBSApplication.UID, e.getMessage().getBytes(), 2);
         return;
      }
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(width, height);
   }

   @Override
   protected final void paint(Graphics graphics) {
      if (this._mode != null && (this._mode.getView() == 1 || this._mode.getView() == 2)) {
         this.paintBody(graphics);
         this.paintMode(graphics);
      }
   }

   @Override
   public final int getPreferredHeight() {
      if (this._mode != null) {
         switch (this._mode.getView()) {
            case -1:
               return 0;
            case 0:
            default:
               return 0;
            case 1:
               return this._background.getHeight();
            case 2:
               return Display.getHeight();
         }
      } else {
         return 0;
      }
   }
}
