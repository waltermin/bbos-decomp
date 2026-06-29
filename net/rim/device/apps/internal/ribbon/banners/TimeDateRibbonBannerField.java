package net.rim.device.apps.internal.ribbon.banners;

import java.util.Hashtable;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.FontRegistry;
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

final class TimeDateRibbonBannerField implements BannerField, RibbonComponent$RibbonComponentChangeListener, SimpleRibbonComponent {
   private SimpleRibbonComponent _dateField;
   private SimpleRibbonComponent _timeField;
   private SimpleRibbonComponent _alarmStatusField;
   private int _width;
   private int _height;
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private static final int SEPARATOR_HEIGHT = 1;
   private static final int DATE_TIME_WIDTH = 73;

   public final int getPreferredWidth() {
      return 73;
   }

   public final int getPreferredHeight() {
      return this._height;
   }

   @Override
   public final void applyTheme() {
      Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
      Hashtable parms = new Hashtable();
      if (this._timeField != null) {
         ThemeAttributeSet$Writer attributesWriter = themeWriter.createThemeAttributeSetWriter(null);
         attributesWriter.setFontFamily("LargeTimerFont");
         attributesWriter.setFontStyle(1);
         attributesWriter.setFontSize(1400, 4194306, false);
         ThemeAttributeSet attributes = attributesWriter.getThemeAttributeSet();
         attributes.apply();
         parms.put(RibbonComponentIDs.DATE_TIME_TYPE_PARAM_NAME, RibbonComponentIDs.DATE_TIME_TIME_VALUE);
         RibbonComponentInitializer rci = (RibbonComponentInitializer)this._timeField;
         rci.initialize(parms, attributes);
      }

      if (this._dateField != null) {
         RibbonComponentInitializer rci = (RibbonComponentInitializer)this._dateField;
         parms.clear();
         parms.put(RibbonComponentIDs.DATE_TIME_TYPE_PARAM_NAME, RibbonComponentIDs.DATE_TIME_DATE_VALUE);
         ThemeAttributeSet$Writer attributesWriter = themeWriter.createThemeAttributeSetWriter(null);
         attributesWriter.setFontFamily(FontFamily.FAMILY_SYSTEM);
         attributesWriter.setFontStyle(0);
         attributesWriter.setFontSize(Math.max(10, Ui.convertSize(800, 4194306, 0)), 0, false);
         ThemeAttributeSet attributes = attributesWriter.getThemeAttributeSet();
         attributes.apply();
         rci.initialize(parms, attributes);
      }

      if (this._alarmStatusField != null) {
         this._alarmStatusField.applyTheme();
      }

      this.calcHeights();
   }

   @Override
   public final String getTitle() {
      return null;
   }

   @Override
   public final void setTitle(String title) {
   }

   @Override
   public final void bannerInvalidate() {
      this.ribbonComponentChanged(null);
   }

   @Override
   public final int getComponentWidth() {
      return this._width;
   }

   @Override
   public final int getComponentHeight() {
      return this._height;
   }

   @Override
   public final int paintComponent(Graphics g, int x, int y, int width, int height, Object context) {
      int yOffset = 0;
      if (this._timeField != null) {
         yOffset = this._timeField.getComponentHeight();
         this._timeField.paintComponent(g, 0, 0, 73, yOffset, null);
         yOffset++;
      }

      if (this._dateField != null) {
         int xOffset = 0;
         if (this._alarmStatusField != null) {
            this._alarmStatusField.setDimensionsAvailable(73, this._dateField.getComponentHeight());
            xOffset = this._alarmStatusField.getComponentWidth();
            this._alarmStatusField.paintComponent(g, 0, yOffset, xOffset, this._dateField.getComponentHeight(), null);
         }

         this._dateField.setDimensionsAvailable(73 - xOffset, this._dateField.getComponentHeight());
         this._dateField.paintComponent(g, xOffset, yOffset, 73 - xOffset, this._dateField.getComponentHeight(), null);
      }

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

   TimeDateRibbonBannerField(RibbonComponent$RibbonComponentChangeListener listener, boolean includeTime, boolean includeDate) {
      this._listener = listener;
      if (includeTime) {
         FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
         Factory factory = repos.getFactory(RibbonComponentIDs.DATE_TIME_NAME);
         RibbonComponent rc = (RibbonComponent)factory.createInstance(null);
         rc.setChangeListener(this);
         this._timeField = (SimpleRibbonComponent)rc;
      }

      if (includeDate) {
         FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
         Factory factory = repos.getFactory(RibbonComponentIDs.DATE_TIME_NAME);
         RibbonComponent rc = (RibbonComponent)factory.createInstance(null);
         rc.setChangeListener(this);
         this._dateField = (SimpleRibbonComponent)rc;
         factory = repos.getFactory(RibbonComponentIDs.ALARM_NAME);
         if (factory != null) {
            rc = (RibbonComponent)factory.createInstance(null);
            rc.setChangeListener(this);
            this._alarmStatusField = (SimpleRibbonComponent)rc;
         }
      }
   }

   private final void calcHeights() {
      int maxHeight = Display.getHeight();
      this._height = 0;
      if (this._timeField != null) {
         this._timeField.setDimensionsAvailable(73, maxHeight);
         this._height = this._height + this._timeField.getComponentHeight();
      }

      if (this._dateField != null) {
         if (this._height > 0) {
            this._height++;
         }

         int alarmStatusFieldWidth = 0;
         if (this._alarmStatusField != null) {
            this._alarmStatusField.setDimensionsAvailable(73, maxHeight - this._height);
            alarmStatusFieldWidth = this._alarmStatusField.getComponentWidth();
         }

         this._dateField.setDimensionsAvailable(73 - alarmStatusFieldWidth, maxHeight - this._height);
         this._height = this._height + this._dateField.getComponentHeight();
      }

      this._width = 73;
   }

   static {
      FontRegistry.loadFont("LargeTimerFont_14B.cbtf", "net_rim_bb_framework_api", "LargeTimerFont");
   }
}
