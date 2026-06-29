package net.rim.device.apps.internal.ribbon.banners;

import java.util.Hashtable;
import java.util.Vector;
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
import net.rim.vm.WeakReference;

public final class StandardRibbonBannerPaintable implements BannerField, RibbonComponent$RibbonComponentChangeListener, SimpleRibbonComponent {
   private int _heightTitle = 8;
   private int _indicatorX;
   private int _indicatorWidth;
   private int _networkBlockX;
   private int _networkBlockY;
   private int _gpsX;
   private int _gpsY;
   private int _width;
   private int _height;
   private TimeDateRibbonBannerField _dateTime;
   private SimpleRibbonComponent _indicatorField;
   private SimpleRibbonComponent _statusField;
   private SimpleRibbonComponent _networkBlock;
   private SimpleRibbonComponent _gpsField;
   private Vector _listeners = new Vector();

   public StandardRibbonBannerPaintable() {
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      Factory factory = repos.getFactory("GridIndicators");
      RibbonComponent rc = (RibbonComponent)factory.createInstance(null);
      rc.setChangeListener(this);
      this._indicatorField = (SimpleRibbonComponent)rc;
      factory = repos.getFactory(RibbonComponentIDs.STATUS_NAME);
      rc = (RibbonComponent)factory.createInstance(null);
      rc.setChangeListener(this);
      this._statusField = (SimpleRibbonComponent)rc;
      this._dateTime = new TimeDateRibbonBannerField(this, true, true);
      this._networkBlock = new SignalBatteryRibbonBannerField(this);
      factory = repos.getFactory(RibbonComponentIDs.GPS_MODE_NAME);
      if (factory != null) {
         rc = (RibbonComponent)factory.createInstance(null);
         rc.setChangeListener(this);
         this._gpsField = (SimpleRibbonComponent)rc;
      }
   }

   @Override
   public final void applyTheme() {
      Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
      Hashtable parms = new Hashtable();
      ThemeAttributeSet$Writer attributesWriter = themeWriter.createThemeAttributeSetWriter(null);
      attributesWriter.setFontFamily(FontFamily.FAMILY_SYSTEM);
      attributesWriter.setFontStyle(0);
      attributesWriter.setFontSize(8, 0, false);
      ThemeAttributeSet attributes = attributesWriter.getThemeAttributeSet();
      attributes.apply();
      parms.put("align", "center");
      RibbonComponentInitializer rci = (RibbonComponentInitializer)this._indicatorField;
      rci.initialize(parms, attributes);
      attributesWriter = themeWriter.createThemeAttributeSetWriter(null);
      attributesWriter.setFontFamily(FontFamily.FAMILY_SYSTEM);
      attributesWriter.setFontStyle(0);
      attributesWriter.setFontSize(Math.max(8, Ui.convertSize(600, 4194306, 0)), 0, false);
      attributes = attributesWriter.getThemeAttributeSet();
      attributes.apply();
      parms.clear();
      parms.put("align", "center");
      rci = (RibbonComponentInitializer)this._statusField;
      rci.initialize(parms, attributes);
      this._heightTitle = this._statusField.getComponentHeight();
      this._dateTime.applyTheme();
      this._networkBlock.applyTheme();
      if (this._gpsField != null) {
         this._gpsField.applyTheme();
      }
   }

   @Override
   public final void bannerInvalidate() {
      this.ribbonComponentChanged(null);
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
      int preferredWidth = this.getComponentWidth();
      int bottomPos = this.getComponentHeight() - this._heightTitle;
      this._indicatorField.paintComponent(g, this._indicatorX, 0, this._indicatorWidth, bottomPos, null);
      this._dateTime.paintComponent(g, 0, 0, this._dateTime.getComponentWidth(), this._dateTime.getComponentHeight(), null);
      this._networkBlock
         .paintComponent(g, this._networkBlockX, this._networkBlockY, this._networkBlock.getComponentWidth(), this._networkBlock.getComponentHeight(), null);
      if (this._gpsField != null) {
         this._gpsField.paintComponent(g, this._gpsX, this._gpsY, this._gpsField.getComponentWidth(), this._gpsField.getComponentHeight(), null);
      }

      this._statusField.paintComponent(g, 0, bottomPos, preferredWidth, this._heightTitle, context);
      return 0;
   }

   @Override
   public final void ribbonComponentChanged(RibbonComponent component) {
      synchronized (this._listeners) {
         for (int lv = this._listeners.size() - 1; lv >= 0; lv--) {
            WeakReference ref = (WeakReference)this._listeners.elementAt(lv);
            RibbonComponent$RibbonComponentChangeListener listener = (RibbonComponent$RibbonComponentChangeListener)ref.get();
            if (listener != null) {
               listener.ribbonComponentChanged(null);
            } else {
               this._listeners.removeElement(ref);
            }
         }
      }
   }

   @Override
   public final void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
      this._listeners.addElement(new WeakReference(listener));
   }

   @Override
   public final void setDimensionsAvailable(int width, int height) {
      if (width != this._width || height != this._height) {
         int preferredWidth = width;
         int preferredHeight = height;
         int dateTimeWidth = this._dateTime.getPreferredWidth();
         int dateTimeHeight = this._dateTime.getPreferredHeight();
         int networkBlockWidth = this._networkBlock.getComponentWidth();
         int networkBlockHeight = this._networkBlock.getComponentHeight();
         this._dateTime.setDimensionsAvailable(dateTimeWidth, dateTimeHeight);
         this._networkBlock.setDimensionsAvailable(networkBlockWidth, networkBlockHeight);
         this._networkBlockX = preferredWidth - networkBlockWidth;
         this._networkBlockY = preferredHeight - this._heightTitle - networkBlockHeight - 2;
         if (this._gpsField != null) {
            this._gpsX = preferredWidth - this._gpsField.getComponentWidth();
            this._gpsY = preferredHeight - this._gpsField.getComponentHeight();
         }

         this._indicatorX = dateTimeWidth;
         this._indicatorWidth = preferredWidth - networkBlockWidth - dateTimeWidth;
         this._indicatorField.setDimensionsAvailable(this._indicatorWidth, preferredHeight - this._heightTitle);
         this._statusField.setDimensionsAvailable(preferredWidth, this._heightTitle);
         this._width = preferredWidth;
         this._height = preferredHeight;
      }
   }

   @Override
   public final void setTitle(String title) {
   }
}
