package net.rim.device.internal.ui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.tid.im.layout.DefaultKeyLayout;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.tid.im.layout.UILocaleKeyLayout;

public final class UiInternal {
   public static final ResourceBundleFamily BUNDLE = ResourceBundle.getBundle(3711053710409943671L, "net.rim.device.internal.resource.UI");
   public static final int BM_CACHE_HITS;
   public static final int BM_CACHE_MISSES;
   public static final int BM_CACHE_FLUSH_COUNT;
   public static final int BM_CACHE_AVG_BITMAP_SIZE;
   public static final int BM_CACHE_AVG_BITMAP_COUNT_WHEN_FLUSHED;
   public static final int BM_CACHE_ADD_COUNT;
   public static final int BM_CACHE_TOTAL_CACHE_SIZE;
   public static final int BM_CACHE_CACHE_FREE;
   public static final int BM_CACHE_NUM_STATISTICS;

   public static final native void clearCacheStatistics();

   public static final int[] getCacheStatistics() {
      int[] data = new int[9];
      getCacheStatistics(data);
      return data;
   }

   public static final native void getCacheStatistics(int[] var0);

   public final native void nopFinal();

   public static final native void nopStatic();

   public static final native void promote(Bitmap var0, Graphics var1);

   public static final void setKeyStateIconsVisible(boolean visible) {
      ThemeManager.getActiveTheme().applyKeyStateIcons(visible);
   }

   public static final void setRadioIconsVisible(boolean visible) {
      ThemeManager.getActiveTheme().applyRadioIcons(visible);
   }

   private static final native void setThemeIcon(int var0, int var1, Bitmap[] var2);

   public static final void setThemeIcon(int type, EncodedImage image) {
      if (image == null) {
         throw new NullPointerException("No data in Icon");
      }

      int numFrames = image.getFrameCount();
      if (numFrames < 1) {
         throw new IllegalArgumentException("Icon contains no frames");
      }

      int transColour = 0;
      Bitmap[] iconArray = new Bitmap[numFrames];
      if (image.getImageType() != 1) {
         iconArray[0] = image.getBitmap();
         transColour = iconArray[0].getTransColor();
      } else {
         GIFEncodedImage icon = (GIFEncodedImage)image;

         for (int i = 0; i < numFrames; i++) {
            Bitmap b = icon.getBitmap(i);
            iconArray[i] = new Bitmap(Bitmap.DEFAULT_TYPE, icon.getWidth(), icon.getHeight());
            Graphics g = new Graphics(iconArray[i]);
            if (i != 0 && icon.getFrameTransition(i) == 1) {
               g.drawBitmap(
                  icon.getFrameLeft(i - 1), icon.getFrameTop(i - 1), iconArray[i - 1].getWidth(), iconArray[i - 1].getHeight(), iconArray[i - 1], 0, 0
               );
            } else {
               if (i == 0) {
                  transColour = b.getTransColor();
               }

               g.setColor(transColour);
               g.fillRect(0, 0, icon.getWidth(), icon.getHeight());
            }

            g.drawBitmap(icon.getFrameLeft(i), icon.getFrameTop(i), b.getWidth(), b.getHeight(), b, 0, 0);
         }
      }

      try {
         setThemeIcon(type, transColour, iconArray);
      } catch (IllegalArgumentException e) {
         String error = "OsIcon " + type + " IAE: Image may be too large.";
         long GUID = 5522554029119272869L;
         EventLogger.register(GUID, "UiInternal", 2);
         EventLogger.logEvent(GUID, error.getBytes(), 2);
         System.err.println(error);
      }
   }

   public static final native void setThemeIconToDefault(int var0);

   public static final void frontBufferSnapshot(Graphics graphics, int x, int y, int width, int height, Bitmap bitmap) {
      Display.screenshot(bitmap, x, y, width, height);
   }

   public static final native int getARGBPoint(Graphics var0, int var1, int var2);

   public static final native void lcdClearDisplay();

   public static final native void lcdPutStringXY(int var0, int var1, String var2);

   public static final char map(int keycode) {
      return map(Keypad.key(keycode), Keypad.status(keycode));
   }

   public static final char mapFromFallbackLayout(int keycode) {
      return mapFromFallbackLayout(Keypad.key(keycode), Keypad.status(keycode));
   }

   public static final char map(int key, int status) {
      return getKeyChars(key, status, false).charAt(0);
   }

   public static final char mapFromFallbackLayout(int key, int status) {
      return getKeyChars(key, status, true).charAt(0);
   }

   public static final void map(int key, int status, StringBuffer result) {
      result.append(getKeyChars(key, status, false));
   }

   public static final void mapFromFallbackLayout(int key, int status, StringBuffer result) {
      result.append(getKeyChars(key, status, true));
   }

   private static final StringBuffer getKeyChars(int key, int status, boolean useFallback) {
      status &= 28695;
      if ((status & 4) != 0 && (status & 16) != 0) {
         status &= -7;
      }

      if ((status & 4) != 0) {
         status &= -3;
         if ((status & 1) != 0) {
            status &= -5;
         }
      }

      if ((status & 16) != 0) {
         status &= -2;
         if ((status & 2) != 0) {
            status &= -17;
         }
      }

      SLKeyLayout layout = useFallback ? DefaultKeyLayout.getDefaultKeyLayout() : UILocaleKeyLayout.getUIKeyLayout();
      if (layout == null) {
         StringBuffer result = new StringBuffer();
         result.append('\u0000');
         return result;
      } else {
         return layout.getKeyChars(key, SLKeyLayout.convertStatusToModifiers(status), false);
      }
   }
}
