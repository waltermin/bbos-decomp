package net.rim.device.apps.internal.ribbon.banners;

import java.util.Hashtable;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentIDs;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;

final class SignalBatteryRibbonBannerField implements BannerField, RibbonComponent$RibbonComponentChangeListener, SimpleRibbonComponent {
   private SimpleRibbonComponent _signalField;
   private SimpleRibbonComponent _batteryField;
   private SimpleRibbonComponent _coverageField;
   private SimpleRibbonComponent _roamingField;
   private int _width;
   private int _height;
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private static final int SEPARATOR_WIDTH;

   SignalBatteryRibbonBannerField(RibbonComponent$RibbonComponentChangeListener listener) {
      this._listener = listener;
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      Factory factory = repos.getFactory(RibbonComponentIDs.SIGNAL_NAME);
      RibbonComponent rc = (RibbonComponent)factory.createInstance(null);
      rc.setChangeListener(this);
      this._signalField = (SimpleRibbonComponent)rc;
      factory = repos.getFactory(RibbonComponentIDs.BATTERY_NAME);
      rc = (RibbonComponent)factory.createInstance(null);
      rc.setChangeListener(this);
      this._batteryField = (SimpleRibbonComponent)rc;
      factory = repos.getFactory(RibbonComponentIDs.COVERAGE_NAME);
      if (factory != null) {
         rc = (RibbonComponent)factory.createInstance(null);
         rc.setChangeListener(this);
         this._coverageField = (SimpleRibbonComponent)rc;
      }

      factory = repos.getFactory(RibbonComponentIDs.ROAMING_NAME);
      rc = (RibbonComponent)factory.createInstance(null);
      rc.setChangeListener(this);
      this._roamingField = (SimpleRibbonComponent)rc;
   }

   @Override
   public final void applyTheme() {
      Hashtable parms = (Hashtable)(new Object());
      parms.put("align", "right");
      Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
      ThemeAttributeSet$Writer attributesWriter = themeWriter.createThemeAttributeSetWriter(null);
      attributesWriter.setFontFamily(FontFamily.FAMILY_SYSTEM);
      attributesWriter.setFontStyle(0);
      attributesWriter.setFontSize(Math.max(10, Ui.convertSize(800, 4194306, 0)), 0, false);
      ThemeAttributeSet attributes = attributesWriter.getThemeAttributeSet();
      attributes.apply();
      RibbonComponentInitializer rci = null;
      if (this._coverageField != null) {
         rci = (RibbonComponentInitializer)this._coverageField;
         rci.initialize(parms, attributes);
         this._coverageField.applyTheme();
         parms.clear();
         parms.put("xOfs", "2");
         parms.put("yOfs", "1");
         parms.put("align", "right");
      }

      rci = (RibbonComponentInitializer)this._signalField;
      rci.initialize(parms, attributes);
      this._signalField.applyTheme();
      rci = (RibbonComponentInitializer)this._batteryField;
      rci.initialize(parms, attributes);
      this._batteryField.applyTheme();
      this.calcDimensions();
   }

   @Override
   public final void bannerInvalidate() {
      this.ribbonComponentChanged(null);
   }

   private final void calcDimensions() {
      int maxHeight = Display.getHeight();
      int maxWidth = Display.getWidth();
      this._batteryField.setDimensionsAvailable(7, 16);
      this._signalField.setDimensionsAvailable(21, 9);
      this._roamingField.setDimensionsAvailable(maxWidth, maxHeight);
      int coverageRoamingWidth;
      int coverageRoamingHeight;
      if (this._coverageField != null) {
         this._coverageField.setDimensionsAvailable(maxWidth, maxHeight);
         coverageRoamingWidth = this._coverageField.getComponentWidth() + this._roamingField.getComponentWidth();
         coverageRoamingHeight = Math.max(this._coverageField.getComponentHeight(), this._roamingField.getComponentHeight());
      } else {
         coverageRoamingWidth = this._roamingField.getComponentWidth();
         coverageRoamingHeight = this._roamingField.getComponentHeight();
      }

      int signalCoverageWidth = Math.max(this._signalField.getComponentWidth(), coverageRoamingWidth);
      int signalCoverageHeight = this._signalField.getComponentHeight() + coverageRoamingHeight;
      this._width = signalCoverageWidth + 3 + this._batteryField.getComponentWidth();
      this._height = Math.max(signalCoverageHeight, this._batteryField.getComponentHeight());
   }

   @Override
   public final int getComponentHeight() {
      return this._height;
   }

   @Override
   public final int getComponentWidth() {
      return this._width;
   }

   @Override
   public final String getTitle() {
      return null;
   }

   @Override
   public final int paintComponent(Graphics g, int x, int y, int width, int height, Object context) {
      int coverageWidth = 0;
      int coverageHeight = 0;
      if (this._coverageField != null) {
         coverageWidth = this._coverageField.getComponentWidth();
         coverageHeight = this._coverageField.getComponentHeight();
      }

      int roamingWidth = this._roamingField.getComponentWidth();
      int roamingHeight = this._roamingField.getComponentHeight();
      int coverageRoamingHeight = Math.max(coverageHeight, roamingHeight);
      int coverageRoamingWidth = coverageWidth + roamingWidth;
      int signalWidth = this._signalField.getComponentWidth();
      int signalHeight = this._signalField.getComponentHeight();
      int batteryWidth = this._batteryField.getComponentWidth();
      this._signalField
         .paintComponent(
            g, x + this._width - batteryWidth - 3 - signalWidth, y + this._height - coverageRoamingHeight - signalHeight, signalWidth, signalHeight, null
         );
      if (this._coverageField != null) {
         this._coverageField
            .paintComponent(g, x + this._width - batteryWidth - 3 - coverageWidth, y + this._height - coverageHeight, coverageWidth, coverageHeight, null);
      }

      this._roamingField
         .paintComponent(g, x + this._width - batteryWidth - 3 - coverageRoamingWidth, y + this._height - roamingHeight, roamingWidth, roamingHeight, null);
      this._batteryField
         .paintComponent(
            g,
            x + this._width - batteryWidth,
            y + this._height - this._batteryField.getComponentHeight(),
            batteryWidth,
            this._batteryField.getComponentHeight(),
            null
         );
      return 0;
   }

   @Override
   public final void ribbonComponentChanged(RibbonComponent component) {
      this._listener.ribbonComponentChanged(null);
   }

   @Override
   public final void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
   }

   @Override
   public final void setDimensionsAvailable(int width, int height) {
      this._width = width;
      this._height = height;
   }

   @Override
   public final void setTitle(String title) {
   }
}
