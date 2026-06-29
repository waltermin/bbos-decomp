package net.rim.device.apps.internal.ribbon.components;

import java.util.Hashtable;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ribbon.ImageProviderRibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;
import net.rim.device.apps.api.utility.props.PropsChangeEventSubscription;
import net.rim.device.apps.api.utility.props.PropsChangeListener;
import net.rim.device.apps.api.utility.props.StringProps;
import net.rim.device.apps.internal.ribbon.skin.svg.MediaLayout;
import net.rim.device.internal.ui.IconCollection;

final class RoamingComponent
   implements SimpleRibbonComponent,
   RibbonComponent$RibbonComponentChangeListener,
   PropsChangeListener,
   RibbonComponentInitializer,
   ImageProviderRibbonComponent {
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private RoamingComponentFactory _factory;
   private StringProps _context;
   private boolean _fczAlways;
   private IconCollection _roamingIcons = IconCollection.get("net_rim_Ribbon_Roaming", 6);
   private int _width;
   private int _height;
   private static final int ICON_COUNT;

   RoamingComponent(RoamingComponentFactory factory) {
      this._factory = factory;
      this.updateDimensions(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   @Override
   public final void applyTheme() {
      this.updateDimensions(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   @Override
   public final void setDimensionsAvailable(int width, int height) {
      this.updateDimensions(width, height);
   }

   private final void updateDimensions(int width, int height) {
      this._width = this._roamingIcons.getWidth(width, height);
      this._height = this._roamingIcons.getHeight(width, height);
   }

   @Override
   public final int getComponentWidth() {
      return this._width;
   }

   @Override
   public final int getComponentHeight() {
      return this._height;
   }

   private final int getIconIndex() {
      if (this._context != null) {
         String title = this._context.get(MediaLayout.TITLE_ID, null);
         if (title != null) {
            return -1;
         }
      }

      return this._factory.getIconIndex(this._fczAlways);
   }

   @Override
   public final Object getImage() {
      int iconIndex = this.getIconIndex();
      return iconIndex != -1 ? this._roamingIcons.getImage(iconIndex) : null;
   }

   @Override
   public final int paintComponent(Graphics g, int x, int y, int width, int height, Object context) {
      g.pushRegion(x, y, width, height, 0, 0);
      int iconIndex = this.getIconIndex();
      if (iconIndex != -1) {
         this._roamingIcons.paint(g, 0, 0, this._width, this._height, iconIndex);
      }

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
   public final void propChanged(long propID) {
      if (propID == MediaLayout.TITLE_ID) {
         this.ribbonComponentChanged(this);
      }
   }

   @Override
   public final void initialize(Hashtable parms, Object context) {
      String fcz = (String)parms.get("fcz");
      if ("always".equals(fcz)) {
         this._fczAlways = true;
      }

      if (context instanceof Object) {
         this._context = (StringProps)context;
         if (context instanceof Object) {
            PropsChangeEventSubscription subscription = (PropsChangeEventSubscription)context;
            subscription.addPropsChangeListener(this);
         }
      }
   }

   @Override
   public final void uninitialize() {
   }
}
