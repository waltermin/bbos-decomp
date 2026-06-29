package net.rim.device.apps.internal.ribbon.banners;

import java.util.Hashtable;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentIDs;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;

final class CompressedSignalAndIndicatorsRibbonBannerField extends Field implements BannerField, RibbonComponent$RibbonComponentChangeListener {
   private String _title;
   private SimpleRibbonComponent _indicatorField;
   private SimpleRibbonComponent _signalField;
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private static Hashtable _parameters = (Hashtable)(new Object());
   private static final int SMALL_COMPONENT_GAP;
   private static final int LARGE_COMPONENT_GAP;
   private static final int ARROW_AREA_WIDTH;

   CompressedSignalAndIndicatorsRibbonBannerField(RibbonComponent$RibbonComponentChangeListener listener, String title) {
      this._listener = listener;
      this.setTitle(title);
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      Factory factory = repos.getFactory(RibbonComponentIDs.SIGNAL_NAME);
      RibbonComponent component = (RibbonComponent)factory.createInstance(null);
      component.setChangeListener(this);
      this._signalField = (SimpleRibbonComponent)component;
      factory = repos.getFactory(RibbonComponentIDs.HORIZ_INDICATOR_NAME);
      component = (RibbonComponent)factory.createInstance(null);
      component.setChangeListener(this);
      this._indicatorField = (SimpleRibbonComponent)component;
      ((RibbonComponentInitializer)this._indicatorField).initialize(_parameters, null);
   }

   @Override
   public final int getPreferredWidth() {
      return Display.getWidth();
   }

   @Override
   public final int getPreferredHeight() {
      return Font.getDefault().getHeight();
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(this.getPreferredWidth(), this.getPreferredHeight());
   }

   @Override
   protected final void paint(Graphics graphics) {
      XYRect extent = this.getExtent();
      int widthLeft = extent.width;
      int componentHeight = this._signalField.getComponentHeight();
      int signalWidth = this._signalField.getComponentWidth();
      int x = widthLeft - signalWidth - 1 - 7;
      int y = Math.max(0, extent.height - componentHeight >> 1);
      this._signalField.paintComponent(graphics, x, y, signalWidth, componentHeight, null);
      widthLeft = x - 3;
      this._indicatorField.setDimensionsAvailable(widthLeft, componentHeight);
      this._indicatorField.paintComponent(graphics, 0, y, widthLeft, componentHeight, null);
      widthLeft = widthLeft - this._indicatorField.getComponentWidth() - 1;
      if (this._title != null && widthLeft > 0) {
         graphics.drawText(this._title, 0, 1, 70, widthLeft);
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
   public final String getTitle() {
      return this._title;
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
      _parameters.put("align", "right");
      _parameters.put("reportUsed", "reportUsed");
   }
}
