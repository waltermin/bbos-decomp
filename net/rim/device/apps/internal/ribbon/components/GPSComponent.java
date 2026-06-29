package net.rim.device.apps.internal.ribbon.components;

import java.util.Hashtable;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;
import net.rim.device.internal.ui.IconCollection;

final class GPSComponent implements SimpleRibbonComponent, RibbonComponent$RibbonComponentChangeListener, RibbonComponentInitializer {
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private GPSComponentFactory _factory;
   private IconCollection _gpsIcons;
   private int _width;
   private int _height;
   private static final int ICON_COUNT = 2;

   GPSComponent(GPSComponentFactory factory) {
      this._factory = factory;
      this._gpsIcons = IconCollection.get("net_rim_Ribbon_GPS", 2);
      this._width = this._gpsIcons.getWidth(Integer.MAX_VALUE, Integer.MAX_VALUE);
      this._height = this._gpsIcons.getHeight(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   @Override
   public final void applyTheme() {
      this._width = this._gpsIcons.getWidth(Integer.MAX_VALUE, Integer.MAX_VALUE);
      this._height = this._gpsIcons.getHeight(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   @Override
   public final void setDimensionsAvailable(int width, int height) {
      this._width = this._gpsIcons.getWidth(width, height);
      this._height = this._gpsIcons.getHeight(width, height);
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
      int iconIndex = this._factory._iconIndex;
      if (iconIndex == -1) {
         return 0;
      }

      g.pushRegion(x, y, width, height, 0, 0);
      this._gpsIcons.paint(g, 0, 0, this._width, this._height, iconIndex);
      g.popContext();
      return 0;
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
   }

   @Override
   public final void uninitialize() {
   }
}
