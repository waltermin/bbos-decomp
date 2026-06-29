package net.rim.device.apps.internal.ribbon.banners;

import java.util.Hashtable;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentIDs;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.Edit$BidiLineRuns;
import net.rim.device.internal.ui.RichText;

final class CompressedRibbonBannerField extends Field implements BannerField, RibbonComponent$RibbonComponentChangeListener {
   private String _title;
   private SimpleRibbonComponent[] _components;
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private int[] _lastBidiData;
   private static Hashtable[] _componentParameters = new Object[4];
   private static final String[] _componentIDs = new Object[]{
      RibbonComponentIDs.SIGNAL_NAME, RibbonComponentIDs.BATTERY_NAME, RibbonComponentIDs.DATE_TIME_NAME, RibbonComponentIDs.HORIZ_INDICATOR_NAME
   };
   private static final int COMPONENT_GAP = 2;
   private static final int SEPARATOR_HEIGHT = 1;
   private static final int SIGNAL_ARROW_WIDTH = Graphics.isColor() ? (InternalServices.isReducedFormFactor() ? -2 : 12) : 7;

   CompressedRibbonBannerField(RibbonComponent$RibbonComponentChangeListener listener, String title) {
      super(0);
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      this._components = new Object[_componentIDs.length];
      int i = this._components.length;

      while (--i >= 0) {
         Factory factory = repos.getFactory(_componentIDs[i]);
         this._components[i] = (SimpleRibbonComponent)factory.createInstance(null);
      }

      this._listener = listener;
      this.setTitle(title);
      this.applyTheme();
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      this.setFont(null);
      Font font = this.getFont();
      int fontHeight = font.getHeight();
      Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
      ThemeAttributeSet$Writer attributesWriter = themeWriter.createThemeAttributeSetWriter(null);
      attributesWriter.setFontFamily(FontFamily.FAMILY_SYSTEM);
      int size = Math.max(this._components[0].getComponentHeight(), Ui.convertSize(6, 3, 0));
      attributesWriter.setFontSize(fontHeight > size ? size : fontHeight, 0, false);
      ThemeAttributeSet attributes = attributesWriter.getThemeAttributeSet();
      attributes.apply();
      if (fontHeight > size) {
         this.setFont(font.derive(font.getStyle(), size));
      }

      for (int i = this._components.length; --i >= 0; this._components[i].applyTheme()) {
         SimpleRibbonComponent var10000 = this._components[i];
         if (this._components[i] instanceof Object) {
            ((RibbonComponentInitializer)var10000).initialize(_componentParameters[i], attributes);
         }
      }
   }

   @Override
   public final void onUndisplay() {
      super.onUndisplay();
      int i = this._components.length;

      while (--i >= 0) {
         this._components[i].setChangeListener(null);
      }
   }

   @Override
   public final void onDisplay() {
      super.onDisplay();
      int i = this._components.length;

      while (--i >= 0) {
         this._components[i].setChangeListener(this);
      }
   }

   private final int getPreferredComponentHeight() {
      int height = 0;
      int i = this._components.length;

      while (--i >= 0) {
         height = Math.max(height, this._components[i].getComponentHeight());
      }

      return height;
   }

   @Override
   public final int getPreferredHeight() {
      return this.getPreferredComponentHeight() + 1 + 1;
   }

   @Override
   public final int getPreferredWidth() {
      return Display.getWidth();
   }

   @Override
   public final String getTitle() {
      return this._title;
   }

   @Override
   protected final void layout(int width, int height) {
      int tempWidth = Math.min(this.getPreferredWidth(), width);
      int tempHeight = this.getPreferredComponentHeight();
      if (height < tempHeight) {
         this.setExtent(tempWidth, 0);
      } else {
         int i = this._components.length;

         while (--i >= 0) {
            SimpleRibbonComponent component = this._components[i];
            int componentWidth = component.getComponentWidth();
            component.setDimensionsAvailable(componentWidth, tempHeight);
         }

         this.setExtent(tempWidth, tempHeight + 1 + 1);
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      int componentHeight = this.getContentHeight() - 1 - 1;
      int x = this.getContentWidth() - SIGNAL_ARROW_WIDTH;
      int last = this._components.length - 1;
      int i = 0;

      while (true) {
         SimpleRibbonComponent component = this._components[i];
         int componentWidth = component.getComponentWidth();
         x -= componentWidth + 2;
         if (i == last) {
            if (this._title == null) {
               component.paintComponent(graphics, 0, 0, x, componentHeight, null);
            } else {
               if (this._lastBidiData == null) {
                  Edit$BidiLineRuns bidiData = RichText.getBidiOrder(this._title, 0, this._title.length(), null, true, 2, null, 0, 0);
                  if (!bidiData.isIgnored()) {
                     this._lastBidiData = Arrays.copy(bidiData._runs);
                  } else {
                     this._lastBidiData = new int[3];
                     this._lastBidiData[1] = this._title.length();
                  }
               }

               if (this._lastBidiData != null) {
                  DrawTextParam drawParam = Ui.getTmpDrawTextParam();
                  drawParam.reset();
                  drawParam.iTruncateWithEllipsis = 2;
                  drawParam.iMaxAdvance = x;
                  int xStart = 0;

                  for (int ix = 0; ix < this._lastBidiData.length; ix++) {
                     int runStart = this._lastBidiData[ix++];
                     int runLength = this._lastBidiData[ix++];
                     drawParam.iReverse = this._lastBidiData[ix];
                     int xMeasure = graphics.drawText(this._title, runStart, runLength, xStart, 1, drawParam, null);
                     drawParam.iMaxAdvance -= xMeasure;
                     xStart += xMeasure;
                     if (drawParam.iMaxAdvance <= 0) {
                        break;
                     }
                  }

                  Ui.returnTmpDrawTextParam(drawParam);
               }
            }

            XYRect extent = this.getExtent();
            int yPos = extent.height - 1;
            graphics.drawLine(0, yPos, extent.width, yPos);
            return;
         }

         component.paintComponent(graphics, x, 0, componentWidth, componentHeight, null);
         i++;
      }
   }

   @Override
   public final void setTitle(String title) {
      if (title != null) {
         title = title.trim();
         if (title.length() == 0) {
            title = null;
         }
      }

      this._title = title;
      this.ribbonComponentChanged(null);
   }

   @Override
   public final void bannerInvalidate() {
      this.invalidate();
   }

   @Override
   public final void ribbonComponentChanged(RibbonComponent component) {
      this._listener.ribbonComponentChanged(null);
   }

   static {
      Hashtable parameters = (Hashtable)(new Object());
      parameters.put("xOfs", "10");
      parameters.put("yOfs", "2");
      parameters.put("align", "left");
      parameters.put("icon", "net_rim_Browser_SignalLevel");
      _componentParameters[0] = parameters;
      parameters = (Hashtable)(new Object());
      parameters.put("xOfs", "2");
      parameters.put("yOfs", "1");
      parameters.put("align", "right");
      parameters.put("icon", "net_rim_Compressed_BatteryLevel");
      _componentParameters[1] = parameters;
      parameters = (Hashtable)(new Object());
      parameters.put("type", "time");
      parameters.put("align", "center");
      _componentParameters[2] = parameters;
      parameters = (Hashtable)(new Object());
      parameters.put("align", "right");
      parameters.put("reportUsed", "reportUsed");
      _componentParameters[3] = parameters;
   }
}
