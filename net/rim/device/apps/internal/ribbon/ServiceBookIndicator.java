package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.indicators.Indicator;
import net.rim.device.apps.api.ribbon.indicators.IndicatorManager;
import net.rim.device.apps.api.ribbon.indicators.TestPoint;

public final class ServiceBookIndicator implements Indicator, TestPoint {
   private IndicatorManager _indicatorManager = IndicatorManager.getInstance();
   private boolean _indicatorOn;
   private Bitmap _bitmap;
   private int _bitmapWidth;
   private int _bitmapHeight;
   private int _themeGeneration;
   static ServiceBookIndicator _instance;
   public static final long SERVICE_BOOK_INDICATOR = -5367908543902225098L;

   public final void updateIndicator() {
      if (this.isPendingServiceRecord()) {
         if (!this._indicatorOn) {
            this._indicatorManager.addIndicator(this);
            this._indicatorOn = true;
            return;
         }
      } else if (this._indicatorOn) {
         this._indicatorManager.removeIndicator(this);
         this._indicatorOn = false;
      }
   }

   @Override
   public final int getPriority() {
      return 2;
   }

   @Override
   public final String getTypeName() {
      return "servicebook";
   }

   @Override
   public final void test(Object id, Object value) {
      if (value instanceof Boolean) {
         if ((Boolean)value) {
            this._indicatorManager.addIndicator(this);
            this._indicatorOn = true;
            return;
         }

         if (this._indicatorOn) {
            this._indicatorManager.removeIndicator(this);
            this._indicatorOn = false;
         }
      }
   }

   @Override
   public final int getWidth(Graphics graphics) {
      this.checkTheme();
      return this._bitmapWidth;
   }

   @Override
   public final int getHeight(Graphics graphics) {
      this.checkTheme();
      return this._bitmapHeight;
   }

   @Override
   public final int draw(Graphics graphics, int width, int height, int flags) {
      this.checkTheme();
      if (this._indicatorOn) {
         int offset = 3;
         boolean iconOnLeft = (flags & 1) != 0;
         if (iconOnLeft || this._bitmap.getWidth() == width) {
            offset = 0;
         }

         graphics.drawBitmap(offset, 0, this._bitmap.getWidth(), this._bitmap.getHeight(), this._bitmap, 0, 0);
         return this._bitmapWidth;
      } else {
         return 0;
      }
   }

   private final void checkTheme() {
      int themeGeneration = ThemeManager.getGeneration();
      if (this._bitmap == null || themeGeneration != this._themeGeneration) {
         this._themeGeneration = themeGeneration;
         this._bitmap = ThemeManager.getActiveTheme().getBitmap("ServiceBookPending");
         this._bitmapWidth = this._bitmap.getWidth();
         this._bitmapHeight = this._bitmap.getHeight();
      }
   }

   public static final ServiceBookIndicator getInstance() {
      if (_instance == null) {
         initialize();
      }

      return _instance;
   }

   private final boolean isPendingServiceRecord() {
      ServiceRecord[] records = ServiceBook.getSB().findRecordsByType(1);
      return records.length > 0;
   }

   static final void initialize() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (ServiceBookIndicator)applicationRegistry.getOrWaitFor(-5367908543902225098L);
         if (_instance == null) {
            _instance = new ServiceBookIndicator();
            applicationRegistry.put(-5367908543902225098L, _instance);
         }
      }
   }
}
