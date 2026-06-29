package net.rim.device.apps.internal.ribbon.indicators;

import java.util.Hashtable;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.TextRibbonComponent;
import net.rim.device.apps.api.ribbon.indicators.Indicator;

class HorizontalIndicatorField extends TextRibbonComponent implements RibbonComponent$RibbonComponentChangeListener, RibbonComponentInitializer {
   private IndicatorDisplay[] _indicatorsForPainting = new IndicatorDisplay[20];
   private int[] _widths = new int[20];
   private IndicatorManagerImpl _indicatorManagerImpl;
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private boolean _reportUsed;
   private int _widthUsed;
   private int _heightUsed;
   private static final int PREFERRED_HORIZONTAL_GAP = 4;
   protected static final int MINIMUM_HORIZONTAL_GAP = 1;
   private static final int MAXIMUM_INDICATORS = 20;

   public HorizontalIndicatorField(IndicatorManagerImpl indicatorManagerImpl) {
      this._indicatorManagerImpl = indicatorManagerImpl;
      this._reportUsed = false;
      this._widthUsed = -1;
      this._heightUsed = -1;
   }

   @Override
   protected String getDefaultTag() {
      return "indicator";
   }

   @Override
   public void setDimensionsAvailable(int width, int height) {
      super._width = width;
      super._height = height;
   }

   @Override
   public int getComponentWidth() {
      return this._reportUsed && this._widthUsed != -1 ? this._widthUsed : super._width;
   }

   @Override
   public int getComponentHeight() {
      return this._reportUsed && this._heightUsed != -1 ? this._heightUsed : super._height;
   }

   @Override
   public synchronized void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
      this._listener = listener;
   }

   @Override
   public synchronized void ribbonComponentChanged(RibbonComponent component) {
      RibbonComponent$RibbonComponentChangeListener listener = this._listener;
      if (listener != null) {
         listener.ribbonComponentChanged(this);
      }
   }

   @Override
   public int paintComponent(Graphics graphics, int x, int y, int width, int height, Object context) {
      graphics.pushRegion(x, y, width, height, 0, 0);
      IndicatorDisplay[] indicatorsForPainting = this._indicatorsForPainting;
      int[] widths = this._widths;
      int numIndicators = this._indicatorManagerImpl.getIndicators(indicatorsForPainting, graphics);
      if (numIndicators == 0) {
         graphics.popContext();
         this._widthUsed = 0;
         this._heightUsed = 0;
         return 0;
      }

      int totalWidth = 0;

      for (int i = 0; i < numIndicators; i++) {
         Indicator indicator = indicatorsForPainting[i]._indicator;
         widths[i] = this.safeGetWidth(indicator, graphics);
         totalWidth += widths[i];
      }

      int xGap = 4;
      int remainingSpace = super._width - totalWidth;
      if (remainingSpace > 0) {
         int numberofGapsNeeded = numIndicators - 1;
         if (numberofGapsNeeded > 0) {
            xGap = MathUtilities.clamp(1, remainingSpace / numberofGapsNeeded, 4);
            totalWidth += xGap * numberofGapsNeeded;
         }
      }

      int flags = 2;
      int offset = 0;
      this._widthUsed = super._width;
      this._heightUsed = super._height;
      if (totalWidth > super._width) {
         flags |= 4;
         offset = 0;
      } else {
         offset = 0;
         if (super._align == 5) {
            offset = super._width - totalWidth;
            this._widthUsed = totalWidth;
         } else if (super._align == 4) {
            offset = super._width - totalWidth >> 1;
         }
      }

      for (int i = 0; i < numIndicators; i++) {
         int widthAllowed = widths[i];
         graphics.pushRegion(offset, 1, widthAllowed, height, 0, 0);

         int indWidth;
         try {
            indWidth = indicatorsForPainting[i]._indicator.draw(graphics, widthAllowed, height, flags);
         } finally {
            ;
         }

         offset += Math.min(indWidth, widthAllowed) + xGap;
         graphics.popContext();
      }

      numIndicators = indicatorsForPainting.length;

      for (int i = 0; i < numIndicators; i++) {
         indicatorsForPainting[i] = null;
      }

      graphics.popContext();
      return 0;
   }

   private int safeGetWidth(Indicator indicator, Graphics graphics) {
      try {
         int width = indicator.getWidth(graphics);
         if (width < 0 || width == 0) {
            width = 0;
         }

         return width;
      } finally {
         ;
      }
   }

   @Override
   public void initialize(Hashtable parms, Object context) {
      if (parms != null) {
         super.initialize(parms, context);
         this._reportUsed = parms.contains("reportUsed");
      } else {
         this._reportUsed = false;
      }
   }

   @Override
   public void uninitialize() {
      super._align = 6;
      this._reportUsed = false;
   }
}
