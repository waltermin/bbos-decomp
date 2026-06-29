package net.rim.device.apps.internal.ribbon.components;

import java.util.Hashtable;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.StringRibbonComponent;
import net.rim.vm.DebugSupport;

final class CoverageField extends StringRibbonComponent implements RibbonComponent$RibbonComponentChangeListener, RibbonComponentInitializer {
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private boolean _hideNetworkInfo;
   private CoverageComponentFactory _coverageComponentFactory;
   private static String descendingChars = "gpjyq\uf3a2\uf3a4\uf3a5";

   public CoverageField(CoverageComponentFactory coverageComponentFactory) {
      this._coverageComponentFactory = coverageComponentFactory;
      this._hideNetworkInfo = DebugSupport.getenv("JvmHideNetworkInfo") != null;
   }

   @Override
   public final void applyTheme() {
   }

   @Override
   public final int getPreferredWidth() {
      return this._coverageComponentFactory.getMaxTextWidth(super._font);
   }

   @Override
   protected final boolean isTextDescentIncluded() {
      return true;
   }

   @Override
   protected final String getDefaultTag() {
      return "coverage";
   }

   @Override
   public final String getText() {
      return this._hideNetworkInfo ? null : this._coverageComponentFactory.getCoverageText();
   }

   @Override
   public final int paintComponent(Graphics graphics, int x, int y, int width, int height, Object context) {
      if (super._font != null) {
         graphics.setFont(super._font);
      }

      String coverageText = this.getText();
      if (coverageText != null) {
         graphics.pushRegion(x, y, width, height, 0, 0);
         if (super._fgColorSet) {
            graphics.setColor(super._fgColor);
         }

         int align = super._align;
         if ((super._align & 56) == 0) {
            if (containsDescendingChars(coverageText)) {
               align |= 40;
            } else {
               align |= 8;
            }
         }

         int text_y = 0;
         if ((super._align & 56) != 48) {
            text_y = super._height;
         }

         this.drawText(graphics, coverageText, 0, text_y, align, super._width + 1);
         graphics.popContext();
      }

      return 0;
   }

   private static final boolean containsDescendingChars(String text) {
      int n = descendingChars.length();

      for (int i = 0; i < n; i++) {
         if (text.indexOf(descendingChars.charAt(i)) != -1) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final synchronized void ribbonComponentChanged(RibbonComponent component) {
      RibbonComponent$RibbonComponentChangeListener listener = this._listener;
      if (listener != null) {
         listener.ribbonComponentChanged(this);
      }
   }

   @Override
   public final synchronized void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
      this._listener = listener;
   }

   @Override
   public final void initialize(Hashtable parms, Object context) {
      super.initialize(parms, context);
      Font font = super._font;
      if (font == null) {
         font = Font.getDefault();
      }

      this.ribbonComponentChanged(null);
   }

   @Override
   public final void uninitialize() {
   }
}
