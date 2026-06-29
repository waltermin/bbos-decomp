package net.rim.device.apps.internal.browser.plugin.media.field;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.vm.Array;

final class TextOverlayFieldManager extends VerticalFieldManager {
   private int _overlayTextId = -1;
   private int _foregroundColor;
   private int _backgroundColor;
   private int _width = 240;
   private int _height = 180;
   private Field[][] _fieldsForView = new Object[3][];
   private int _currentView;
   public static final int VIEW_AUDIO = 0;
   public static final int VIEW_PLAYLIST = 1;
   public static final int VIEW_VIDEO = 2;
   private static final int MAX_VIEWS = 3;

   TextOverlayFieldManager() {
      super(3458764513820540928L);

      for (int i = 0; i < 3; i++) {
         this._fieldsForView[i] = new Object[0];
      }
   }

   public final void clearViewFields(int view) {
      synchronized (Application.getEventLock()) {
         if (view == this._currentView) {
            this.deleteAll();
         }

         Array.resize(this._fieldsForView[view], 0);
      }
   }

   public final void addField(int view, Field f) {
      synchronized (Application.getEventLock()) {
         Arrays.add(this._fieldsForView[view], f);
         if (view == this._currentView) {
            this.add(f);
         }
      }
   }

   public final boolean isPlaylistActive() {
      return this._currentView == 1;
   }

   public final void setActiveView(int view) {
      if (view != this._currentView) {
         this._currentView = view;
         synchronized (Application.getEventLock()) {
            this.deleteAll();

            for (int i = 0; i < this._fieldsForView[view].length; i++) {
               this.add(this._fieldsForView[view][i]);
            }
         }
      }
   }

   public final void setOverlayText(int id, int foregroundColor, int backgroundColor) {
      this._overlayTextId = id;
      this._foregroundColor = foregroundColor;
      this._backgroundColor = backgroundColor;
      this.invalidate();
   }

   public final int getOverlayTextId() {
      return this._overlayTextId;
   }

   @Override
   protected final void subpaint(Graphics graphics) {
      int oldColor = graphics.getBackgroundColor();
      graphics.setBackgroundColor(0);
      graphics.clear();
      graphics.setBackgroundColor(oldColor);
      super.subpaint(graphics);
      if (this._overlayTextId != -1) {
         String overlayText = BrowserResources.getString(this._overlayTextId);
         Font gFont = graphics.getFont();
         int width = gFont.getAdvance(overlayText) + 6;
         int height = gFont.getHeight() + 6;
         int x = this.getContentWidth() - width;
         graphics.setColor(this._backgroundColor);
         graphics.fillRect(x, 0, width, height);
         graphics.setColor(this._foregroundColor);
         graphics.drawText(overlayText, x + 3, 3);
      }
   }

   public final void setDimensions(int width, int height) {
      this._width = width;
      this._height = height;
   }

   @Override
   protected final void sublayout(int width, int height) {
      super.sublayout(this._width, this._height);
      this.setExtent(this._width, this._height);
      this.setVirtualExtent(this._width, this._height);
   }
}
