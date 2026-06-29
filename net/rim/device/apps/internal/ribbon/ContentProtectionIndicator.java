package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.PersistentContentInternal;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.indicators.Indicator;
import net.rim.device.apps.api.ribbon.indicators.IndicatorManager;
import net.rim.device.apps.api.ribbon.indicators.TestPoint;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Memory;

public final class ContentProtectionIndicator implements Indicator, PersistentContentListener, TestPoint {
   private IndicatorManager _indicatorManager;
   private Bitmap _bitmap;
   private int _bitmapWidth;
   private int _themeGeneration;
   private StringBuffer _text = new StringBuffer(3);
   private int _state = 1;
   private int _bitmapState = -1;
   private static final long CONTENT_PROTECTION_INDICATOR = -8125869066658600065L;

   static final void initialize() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      Object instance = applicationRegistry.getOrWaitFor(-8125869066658600065L);
      if (instance == null) {
         applicationRegistry.put(-8125869066658600065L, new ContentProtectionIndicator());
      }
   }

   public ContentProtectionIndicator() {
      this._indicatorManager = IndicatorManager.getInstance();
      this._indicatorManager.addIndicator(this);
      PersistentContentInternal.registerPersistentContentIndicator(this);
   }

   @Override
   public final void persistentContentStateChanged(int state) {
      synchronized (this) {
         this._state = state;
         this._text.delete(0, this._text.length());
         if (!InternalServices.isDeviceSecure()) {
            int numPlaintext = Memory.numPlaintext() - Memory.numPlaintextSpecial();
            if (numPlaintext != 0) {
               appendNumber(this._text, numPlaintext);
               int numPersistentPlaintext = Memory.numPersistentPlaintext();
               if (numPersistentPlaintext > 0) {
                  this._text.append('(');
                  appendNumber(this._text, numPersistentPlaintext);
                  this._text.append(')');
               }
            }

            System.out.println("PCI: update: " + numPlaintext);
         }
      }

      this._indicatorManager.updateIndicators();
   }

   private static final void appendNumber(StringBuffer text, int num) {
      if (num > 999) {
         text.append('+');
         text.append('+');
         text.append('+');
      } else {
         if (num > 0) {
            text.append(num);
         }
      }
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
   }

   private final synchronized void checkTheme() {
      int themeGeneration = ThemeManager.getGeneration();
      if (themeGeneration != this._themeGeneration || this._bitmapState != this._state) {
         this._themeGeneration = themeGeneration;
         this._bitmapState = this._state;
         if (this._state == 3) {
            this._bitmap = ThemeManager.getActiveTheme().getBitmap("ContentProtectionIndicator");
         } else {
            this._bitmap = ThemeManager.getActiveTheme().getBitmap("ContentProtectionInsecureIndicator");
         }

         this._bitmapWidth = this._bitmap.getWidth();
      }
   }

   @Override
   public final synchronized int draw(Graphics graphics, int width, int height, int flags) {
      int drawWidth = this.getWidth(graphics);
      if (drawWidth == 0) {
         return 0;
      }

      int xpos = width - drawWidth >> 1;
      if ((flags & 1) != 0) {
         xpos = width - drawWidth;
         graphics.drawBitmap(xpos, 0, this._bitmap.getWidth(), this._bitmap.getHeight(), this._bitmap, 0, 0);
         xpos += this._bitmap.getWidth();
      }

      if ((flags & 4) == 0) {
         xpos += graphics.drawText(this._text, 0, this._text.length(), xpos, 0, flags, width);
      }

      if ((flags & 2) != 0) {
         graphics.drawBitmap(xpos, 0, this._bitmap.getWidth(), this._bitmap.getHeight(), this._bitmap, 0, 0);
      }

      return drawWidth;
   }

   @Override
   public final int getPriority() {
      return 1;
   }

   @Override
   public final String getTypeName() {
      return "contentprotection";
   }

   @Override
   public final void test(Object id, Object testvalue) {
      if (testvalue instanceof Integer) {
         Integer integer = (Integer)testvalue;
         this.persistentContentStateChanged(integer);
         if (integer != 1) {
            this._text.delete(0, this._text.length());
            appendNumber(this._text, 999);
            this._text.append('(');
            appendNumber(this._text, 999);
            this._text.append(')');
         }
      }
   }

   @Override
   public final synchronized int getWidth(Graphics graphics) {
      if (this._state == 1) {
         return 0;
      }

      this.checkTheme();
      return this._bitmapWidth + graphics.getFont().getAdvance(this._text, 0, this._text.length());
   }

   @Override
   public final int getHeight(Graphics graphics) {
      int fontHeight = graphics.getFont().getHeight();
      this.checkTheme();
      return fontHeight > this._bitmap.getHeight() ? fontHeight : this._bitmap.getHeight();
   }
}
