package net.rim.device.apps.internal.ribbon.indicators;

import java.util.Hashtable;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.TextRibbonComponent;
import net.rim.device.apps.api.ribbon.indicators.Indicator;
import net.rim.vm.Array;

final class GridIndicatorField extends TextRibbonComponent implements RibbonComponent$RibbonComponentChangeListener {
   private int _xGap;
   private int _xOffset;
   private int[] _columnWidths;
   private int[] _yGapPerColumn;
   private int[] _yOffsetPerColumn;
   private int[] _indicatorHeight;
   private int[] _indicatorWidth;
   private int _rows;
   private int _columns;
   private int _area = 0;
   private String _specificIndicatorName = "";
   private Font _bigFont;
   private boolean _bigIndicator = false;
   private boolean _omit = false;
   private IndicatorDisplay[] _indicatorsForPainting = new IndicatorDisplay[0];
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private IndicatorManagerImpl _indicatorManagerImpl;
   protected static final int INDICATOR_HEIGHT;
   protected static final int INDICATOR_WIDTH;
   protected static final int MINIMUM_VERTICAL_GAP;

   public GridIndicatorField(IndicatorManagerImpl indicatorManagerImpl) {
      this._indicatorManagerImpl = indicatorManagerImpl;
   }

   @Override
   protected final String getDefaultTag() {
      return "indicator";
   }

   @Override
   public final void setDimensionsAvailable(int width, int height) {
      this.setSize(width, height);
   }

   @Override
   public final synchronized void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
      this._listener = listener;
   }

   @Override
   public final synchronized void ribbonComponentChanged(RibbonComponent component) {
      RibbonComponent$RibbonComponentChangeListener listener = this._listener;
      if (listener != null) {
         listener.ribbonComponentChanged(this);
      }
   }

   @Override
   public final void initialize(Hashtable params, Object context) {
      super.initialize(params, context);
      String tmpStr = (String)params.get("bigindicator");
      if (tmpStr != null && tmpStr.equals("true")) {
         this._bigIndicator = true;
      }

      tmpStr = (String)params.get("omitindicator");
      if (tmpStr != null) {
         this._specificIndicatorName = tmpStr.toLowerCase();
         this._omit = true;
      }

      tmpStr = (String)params.get("specificindicator");
      if (tmpStr != null) {
         this._specificIndicatorName = tmpStr.toLowerCase();
         this._omit = false;
      }

      tmpStr = (String)params.get("area");

      label45:
      try {
         if (tmpStr != null) {
            this._area = Integer.parseInt(tmpStr);
         }
      } finally {
         break label45;
      }

      Tag tag = Tag.create(this.getDefaultTag());
      Theme theme = ThemeManager.getActiveTheme();
      ThemeAttributeSet attributes = theme.getAttributeSet(tag, "big", 0);
      if (attributes != null) {
         this._bigFont = attributes.getFont();
      }
   }

   private final void setSize(int width, int height) {
      if (width >= 0 && width <= 1600) {
         super._width = width;
      } else {
         super._width = 0;
      }

      if (height >= 0 && height <= 1200) {
         super._height = height;
      } else {
         super._height = 0;
      }

      this._rows = Math.max(1, (super._height + 1) / 9);
      this._columns = Math.max(1, (super._width + 1) / 25);
      Array.resize(this._indicatorsForPainting, this._rows * this._columns);
      this._columnWidths = new int[this._columns];
      this._yGapPerColumn = new int[this._columns];
      this._yOffsetPerColumn = new int[this._columns];
      this._indicatorHeight = new int[this._rows * this._columns];
      this._indicatorWidth = new int[this._rows * this._columns];
   }

   @Override
   public final synchronized int paintComponent(Graphics graphics, int x, int y, int width, int height, Object context) {
      if (super._width != 0 && super._height != 0) {
         graphics.pushRegion(x, y, width, height, 0, 0);
         if (super._font != null) {
            graphics.setFont(super._font);
         }

         if (super._fgColorSet) {
            graphics.setColor(super._fgColor);
         }

         int numIndicators = this._indicatorManagerImpl
            .getIndicators(this._indicatorsForPainting, graphics, this._area, this._specificIndicatorName, this._omit);
         if (numIndicators < 1) {
            graphics.popContext();
            return 0;
         }

         for (int i = 0; i < this._columns; i++) {
            this._columnWidths[i] = 0;
         }

         boolean tooWide = false;
         int yOffset = 0;
         int curCol = 0;
         int numPerCol = 0;
         int xOffset = 0;
         int var19 = 0;
         XYRect pnt = (XYRect)(new Object());

         while (var19 < numIndicators) {
            while (var19 < numIndicators) {
               Indicator indicator = this._indicatorsForPainting[var19]._indicator;
               this.safeGetRect(pnt, indicator, graphics);
               if (pnt.width > super._width - xOffset) {
                  tooWide = true;
               }

               if (pnt.height > super._height - yOffset) {
                  break;
               }

               this._columnWidths[curCol] = Math.max(pnt.width, this._columnWidths[curCol]);
               yOffset += pnt.height + 1;
               this._indicatorHeight[var19] = pnt.height;
               this._indicatorWidth[var19] = pnt.width;
               numPerCol++;
               var19++;
            }

            if (tooWide) {
               break;
            }

            yOffset -= 1 * (numPerCol - 1);
            this._yGapPerColumn[curCol] = Math.max((super._height - yOffset) / (numPerCol + 1), 1);
            this._yOffsetPerColumn[curCol] = (super._height - ((numPerCol - 1) * this._yGapPerColumn[curCol] + yOffset) + 1) / 2;
            xOffset += this._columnWidths[curCol];
            numPerCol = 0;
            yOffset = 0;
            if (++curCol == this._columns) {
               break;
            }
         }

         this._xGap = Math.max((super._width - xOffset) / (curCol + 1), 0);
         this._xOffset = (super._width - ((curCol - 1) * this._xGap + xOffset) + 1) / 2;
         int flags = 2;
         curCol = 0;
         xOffset = this._xOffset;
         yOffset = this._yOffsetPerColumn[curCol];

         for (int var20 = 0; var20 < numIndicators; var20++) {
            if (this._indicatorHeight[var20] > super._height - yOffset) {
               if (this._columns == 2) {
                  flags = 1;
               }

               xOffset += this._columnWidths[curCol] + this._xGap;
               if (++curCol == this._columns) {
                  break;
               }

               yOffset = this._yOffsetPerColumn[curCol];
            }

            if (this._indicatorWidth[var20] > super._width - xOffset) {
               break;
            }

            graphics.pushRegion(xOffset, yOffset, this._columnWidths[curCol], this._indicatorHeight[var20], 0, 0);
            if (this._bigIndicator && this._bigFont != null) {
               graphics.setFont(this._bigFont);
            }

            label142:
            try {
               this._indicatorsForPainting[var20]._indicator.draw(graphics, this._columnWidths[curCol], this._indicatorHeight[var20], flags);
               this._indicatorsForPainting[var20]._area = this._area;
               yOffset += this._indicatorHeight[var20] + this._yGapPerColumn[curCol];
            } finally {
               break label142;
            }

            graphics.popContext();
         }

         for (int var21 = 0; var21 < numIndicators; var21++) {
            this._indicatorsForPainting[var21] = null;
         }

         graphics.popContext();
         return 0;
      } else {
         return 0;
      }
   }

   private final void safeGetRect(XYRect pnt, Indicator indicator, Graphics graphics) {
      Font originalFont = null;
      if (indicator != null) {
         try {
            originalFont = null;
            if (this._bigIndicator && this._bigFont != null) {
               originalFont = graphics.getFont();
               graphics.setFont(this._bigFont);
            }

            pnt.width = indicator.getWidth(graphics);
            pnt.height = indicator.getHeight(graphics);
            if (originalFont != null) {
               graphics.setFont(originalFont);
               originalFont = null;
               return;
            }
         } finally {
            return;
         }
      }
   }
}
