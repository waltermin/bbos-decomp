package net.rim.wica.runtime.ui.internal;

import java.util.Hashtable;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.GlobalRibbonComponentRepository;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentFactoryRepository;
import net.rim.device.apps.api.ribbon.RibbonComponentIDs;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;

final class ScreenTitleField extends Field implements RibbonComponent$RibbonComponentChangeListener {
   private UiApplication _app;
   private String _title;
   private SimpleRibbonComponent _indicatorField;
   private SimpleRibbonComponent _signalField;
   private int _indicatorHeight;
   private Font _indicatorFont;
   private int _x1;
   private int _y1;
   private ScreenTitleField$RedrawRunnable _redrawRunnable = new ScreenTitleField$RedrawRunnable(this);
   private static final Tag TAG = Tag.create("title");
   private static Hashtable _signalParameters = (Hashtable)(new Object());
   private static Hashtable _indicatorParameters = (Hashtable)(new Object());
   private static ThemeAttributeSet _signalAttributes;
   private static final int SEPARATOR_HEIGHT = 1;
   private static final int COMPONENT_GAP = 2;

   ScreenTitleField() {
      this._app = UiApplication.getUiApplication();
      RibbonComponentFactoryRepository repos = GlobalRibbonComponentRepository.getGlobalRibbonComponentFactoryRepository();
      Factory factory = repos.getFactory(RibbonComponentIDs.SIGNAL_NAME);
      RibbonComponent component = (RibbonComponent)factory.createInstance(null);
      RibbonComponentInitializer rci = (RibbonComponentInitializer)component;
      rci.initialize(_signalParameters, _signalAttributes);
      this._signalField = (SimpleRibbonComponent)component;
      factory = repos.getFactory(RibbonComponentIDs.HORIZ_INDICATOR_NAME);
      component = (RibbonComponent)factory.createInstance(null);
      this._indicatorField = (SimpleRibbonComponent)component;
      ((RibbonComponentInitializer)this._indicatorField).initialize(_indicatorParameters, null);
      this.setTag(TAG);
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
      int size = Math.max(this._signalField.getComponentHeight(), Ui.convertSize(6, 3, 0));
      attributesWriter.setFontSize(fontHeight > size ? size : fontHeight, 0);
      ThemeAttributeSet attributes = attributesWriter.getThemeAttributeSet();
      attributes.apply();
      if (fontHeight > size) {
         this.setFont(font.derive(font.getStyle(), size));
      }

      RibbonComponentInitializer rci = (RibbonComponentInitializer)this._signalField;
      rci.initialize(_signalParameters, _signalAttributes);
      this._signalField.applyTheme();
      rci = (RibbonComponentInitializer)this._indicatorField;
      rci.initialize(_indicatorParameters, attributes);
      this._indicatorField.applyTheme();
   }

   final void setTitle(String title) {
      if (title != null) {
         title = title.trim();
         if (title.length() == 0) {
            title = null;
         }
      }

      String oldTitle = this._title;
      this._title = title;
      if (oldTitle == null && title != null || oldTitle != null && title == null) {
         this.updateLayout();
      }

      this.redraw();
   }

   final String getTitle() {
      return this._title;
   }

   private final void redraw() {
      if (this._app.isForeground()) {
         synchronized (this._redrawRunnable) {
            if (this._redrawRunnable._invokeLaterPending) {
               return;
            }
         }

         this._app.invokeLater(this._redrawRunnable);
      }
   }

   private final void updateIndicators() {
      this._indicatorHeight = Math.max(this._signalField.getComponentHeight(), this._indicatorField.getComponentHeight());
      if (this._indicatorFont == null || this._indicatorFont.getHeight() > this._indicatorHeight) {
         Font font = this.getFont();
         this._indicatorFont = font.derive(font.getStyle(), this._indicatorHeight);
      }
   }

   @Override
   public final int getPreferredWidth() {
      return Graphics.getScreenWidth();
   }

   @Override
   public final int getPreferredHeight() {
      this.updateIndicators();
      int contentHeight = Math.max(this._indicatorField.getComponentHeight(), this._indicatorHeight);
      contentHeight = Math.max(contentHeight, this.getFont().getHeight());
      return contentHeight + 1 + 1;
   }

   @Override
   public final boolean isFocusable() {
      return false;
   }

   @Override
   protected final void layout(int width, int height) {
      int fieldWidth = Math.min(width, this.getPreferredWidth());
      int fieldHeight = this.getPreferredHeight();
      if (height >= fieldHeight) {
         int widthAvailable = fieldWidth;
         int signalWidth = this._signalField.getComponentWidth();
         this._x1 = widthAvailable - signalWidth;
         this._y1 = Math.max(0, fieldHeight - this._indicatorHeight - 1 - 1 >> 1);
         this.setExtent(fieldWidth, fieldHeight + 1);
      } else {
         this.setExtent(fieldWidth, 0);
      }
   }

   @Override
   public final void onDisplay() {
      super.onDisplay();
      this._indicatorField.setChangeListener(this);
      this._signalField.setChangeListener(this);
   }

   @Override
   public final void onUndisplay() {
      super.onUndisplay();
      this._indicatorField.setChangeListener(null);
      this._signalField.setChangeListener(null);
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (visible) {
         this._indicatorField.setChangeListener(this);
         this._signalField.setChangeListener(this);
      } else {
         this._indicatorField.setChangeListener(null);
         this._signalField.setChangeListener(null);
      }

      super.onVisibilityChange(visible);
   }

   @Override
   protected final void paint(Graphics graphics) {
      graphics.pushRegion(0, 0, 1073741823, 1073741823, 0, 0);
      this._signalField.paintComponent(graphics, this._x1, this._y1, this._signalField.getComponentWidth(), this._indicatorHeight, null);
      int remaining = this._x1 - 2;
      this._indicatorField.setDimensionsAvailable(remaining, this._indicatorHeight);
      Font font = graphics.getFont();
      graphics.setFont(this._indicatorFont);
      this._indicatorField.paintComponent(graphics, 0, this._y1, remaining, this._indicatorHeight, null);
      graphics.setFont(font);
      remaining -= this._indicatorField.getComponentWidth() + 2;
      graphics.popContext();
      if (remaining > 0 && this._title != null) {
         graphics.drawText(this._title, 0, 1, 70, remaining);
      }

      XYRect extent = this.getExtent();
      int yPos = extent.height - 1;
      graphics.drawLine(0, yPos, extent.width, yPos);
   }

   @Override
   public final void ribbonComponentChanged(RibbonComponent component) {
      this.redraw();
   }

   static final void access$100(ScreenTitleField x0) {
      x0.invalidate();
   }

   static {
      _indicatorParameters.put("align", "right");
      _indicatorParameters.put("reportUsed", "reportUsed");
      _signalParameters.put("xOfs", "10");
      _signalParameters.put("yOfs", "2");
      _signalParameters.put("align", "left");
      _signalParameters.put("icon", "net_rim_Browser_SignalLevel");
      Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
      ThemeAttributeSet$Writer attributesWriter = themeWriter.createThemeAttributeSetWriter(null);
      attributesWriter.setFontFamily(FontFamily.FAMILY_SYSTEM);
      attributesWriter.setFontStyle(0);
      attributesWriter.setFontSize(1000, 4194306);
      _signalAttributes = attributesWriter.getThemeAttributeSet();
      _signalAttributes.apply();
   }
}
