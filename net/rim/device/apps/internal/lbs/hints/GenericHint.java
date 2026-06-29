package net.rim.device.apps.internal.lbs.hints;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.lbs.MapField;

public final class GenericHint implements Runnable {
   private String _label;
   private Font _font;
   private int _width;
   private int _height;
   private boolean _showHint = false;
   private int _hintPID = -1;
   private MapField _parent;
   private static final int BACKGROUND = 16776960;
   private static final int OUTLINE = 15577897;
   private static final int PADDING_WIDTH = 6;

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public GenericHint(MapField parent) {
      label20:
      try {
         this._font = FontFamily.forName("BBSansSerif").getFont(1, 12, 0, 2, 0);
      } catch (Throwable var4) {
         e.printStackTrace();
         break label20;
      }

      this._parent = parent;
   }

   public final void show(String lbl) {
      this._label = lbl;
      this._showHint = true;
      if (this._hintPID != -1) {
         UiApplication.getUiApplication().cancelInvokeLater(this._hintPID);
      }

      this._hintPID = UiApplication.getUiApplication().invokeLater(this, 5000, false);
      this._parent.invalidateHint();
   }

   public final void cancel() {
      if (this._hintPID != -1) {
         this._showHint = false;
         UiApplication.getUiApplication().cancelInvokeLater(this._hintPID);
         this._hintPID = -1;
         this._parent.invalidateHint();
      }
   }

   public final void paint(Graphics g) {
      if (this._label != null) {
         Font oldFont = g.getFont();
         int oldAlpha = g.getGlobalAlpha();
         if (this._font != null) {
            g.setFont(this._font);
         }

         int width = this._font.getAdvance(this._label) + 12;
         g.setGlobalAlpha(192);
         g.setColor(16776960);
         g.fillRect((this._width - width) / 2, 0, width, this._height);
         g.setColor(15577897);
         g.setStrokeWidth(1);
         g.drawRect((this._width - width) / 2, 0, width, this._height);
         g.setGlobalAlpha(255);
         g.setColor(0);
         g.drawText(this._label, 0, this._label.length(), 3, this._height >> 1, 36, this._width - 6);
         g.setFont(oldFont);
         g.setGlobalAlpha(oldAlpha);
      }
   }

   public final int getWidth() {
      return this._width;
   }

   public final int getHeight() {
      return this._height;
   }

   public final int getPreferredWidth() {
      return Display.getWidth();
   }

   public final int getPreferredHeight() {
      return this._font.getHeight() + 6;
   }

   public final void layout(int width, int height) {
      this._width = width;
      this._height = height;
   }

   public final boolean isVisible() {
      return this._showHint;
   }

   @Override
   public final void run() {
      this._showHint = false;
      this._hintPID = -1;
      this._parent.invalidateHint();
   }
}
