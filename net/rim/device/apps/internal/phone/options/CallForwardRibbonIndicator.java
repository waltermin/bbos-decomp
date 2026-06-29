package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ribbon.indicators.Indicator;
import net.rim.device.apps.api.ribbon.indicators.IndicatorManager;
import net.rim.device.apps.api.ribbon.indicators.TestPoint;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class CallForwardRibbonIndicator implements Indicator, TestPoint {
   private int _lineIndicator;
   static CallForwardRibbonIndicator _instance;
   public static final long CALL_FORWARD_RIBBON_INDICATOR = 5948557681649763178L;

   public static final void initialize() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (CallForwardRibbonIndicator)applicationRegistry.getOrWaitFor(5948557681649763178L);
         if (_instance == null) {
            _instance = new CallForwardRibbonIndicator();
            applicationRegistry.put(5948557681649763178L, _instance);
            IndicatorManager indicatorManager = IndicatorManager.getInstance();
            if (indicatorManager != null) {
               indicatorManager.addIndicator(_instance);
            }
         }
      }
   }

   public static final CallForwardRibbonIndicator getInstance() {
      if (_instance == null) {
         initialize();
      }

      return _instance;
   }

   private final void updateIndicatorManager() {
      IndicatorManager im = IndicatorManager.getInstance();
      if (im != null) {
         im.updateIndicators();
      }
   }

   public final void updateIndicators(boolean forwarded) {
      this._lineIndicator = 0;
      if (!forwarded) {
         this.updateIndicatorManager();
      } else {
         int[] lineIds = PhoneUtilities.getAllLineIds();

         for (int i = lineIds.length - 1; i >= 0; i--) {
            if (forwarded) {
               this._lineIndicator = this._lineIndicator + lineIds[i];
            }
         }

         this.updateIndicatorManager();
      }
   }

   public final void updateIndicators() {
      this._lineIndicator = 0;
      int[] lineIds = PhoneUtilities.getAllLineIds();

      for (int i = lineIds.length - 1; i >= 0; i--) {
         if (SSManager.isCallForwardUnconditionalActive(lineIds[i])) {
            this._lineIndicator = this._lineIndicator + lineIds[i];
         }
      }

      this.updateIndicatorManager();
   }

   @Override
   public final int draw(Graphics graphics, int width, int height, int flags) {
      int x = PhoneResources.drawIcon(graphics, 0, 0, height, 13);
      if (this._lineIndicator == 1) {
         x += graphics.drawText('1', x, 0, 0, width);
      } else if (this._lineIndicator == 2) {
         x += graphics.drawText('2', x, 0, 0, width);
      }

      return x;
   }

   @Override
   public final int getWidth(Graphics graphics) {
      if (this._lineIndicator == 0) {
         return 0;
      }

      char lineForwarded = 0;
      if (PhoneUtilities.getAllLineIds().length > 1) {
         if (this._lineIndicator == 1) {
            lineForwarded = '1';
         } else if (this._lineIndicator == 2) {
            lineForwarded = '2';
         }
      }

      Font font = graphics.getFont();
      int width = 0;
      if (lineForwarded != 0) {
         width += font.getAdvance(lineForwarded);
      }

      return width + PhoneResources.getIconWidth(font, 13);
   }

   @Override
   public final int getHeight(Graphics graphics) {
      return PhoneResources.getIcons().getHeight(graphics.getFont());
   }

   @Override
   public final int getPriority() {
      return 4;
   }

   @Override
   public final String getTypeName() {
      return "callforward";
   }

   @Override
   public final void test(Object id, Object value) {
      if (value instanceof Object) {
         this.updateIndicators(value);
      }
   }
}
