package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;

public class AbstractMode {
   private int _view = 0;
   private boolean _enabled = false;
   public static final int VIEW_HIDDEN;
   public static final int VIEW_SUMMARY;
   public static final int VIEW_FULL;
   public static final int VIEW_SCREEN;

   public void setEnabled(boolean enable) {
      this._enabled = enable;
   }

   public boolean isEnabled() {
      return this._enabled;
   }

   public boolean isScreen() {
      throw null;
   }

   public void paintHeader(Graphics _1, int _2, int _3) {
      throw null;
   }

   public void paintBody(Graphics _1, int _2, int _3) {
      throw null;
   }

   protected boolean screenViewSupported() {
      throw null;
   }

   protected Screen getScreen() {
      throw null;
   }

   protected boolean hasHeaderToPaint() {
      throw null;
   }

   public int changeView(int mode) {
      int oldMode = this._view;
      switch (mode) {
         case -1:
            break;
         case 0:
         case 1:
         case 2:
         default:
            this._view = mode;
            break;
         case 3:
            if (!this.screenViewSupported()) {
               return this._view;
            }

            this._view = mode;
      }

      if (oldMode == 3 && this._view != 3 && UiApplication.getUiApplication().getActiveScreen() == this.getScreen()) {
         UiApplication.getUiApplication().popScreen(this.getScreen());
      }

      if (this._view == 3 && this.isScreen()) {
         UiApplication.getUiApplication().pushScreen(this.getScreen());
      }

      return this._view;
   }

   public int getView() {
      return this._view;
   }
}
