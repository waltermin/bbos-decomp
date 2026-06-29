package net.rim.device.apps.internal.ribbon.components;

import java.util.Hashtable;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.ImageProviderRibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.StringRibbonComponent;
import net.rim.device.internal.ui.IconCollection;

final class SignalLevelField extends StringRibbonComponent implements RibbonComponent$RibbonComponentChangeListener, ImageProviderRibbonComponent {
   private SignalComponentFactory _signalComponentFactory;
   private IconCollection _signalIcons;
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private static final int ICON_COUNT = 9;

   private SignalLevelField() {
   }

   SignalLevelField(SignalComponentFactory signalComponentFactory) {
      this._signalComponentFactory = signalComponentFactory;
   }

   @Override
   protected final String getDefaultTag() {
      return "signal-level";
   }

   @Override
   public final void applyTheme() {
   }

   @Override
   public final Object getImage() {
      int index = this._signalComponentFactory.getLevel();
      return index >= 0 && index < 9 ? this._signalIcons.getImage(index) : null;
   }

   @Override
   public final String getText() {
      return null;
   }

   @Override
   public final int paintComponent(Graphics graphics, int x, int y, int width, int height, Object context) {
      if (this._signalComponentFactory._numericDisplay) {
         Font currentFont = graphics.getFont();
         Font font = graphics.getFont().derive(0, height);
         graphics.setFont(font);
         String text = this._signalComponentFactory._valueString.toString();
         this.drawText(graphics, text, x, y, super._align, super._width);
         graphics.setFont(currentFont);
         return 0;
      } else {
         int index = this._signalComponentFactory.getLevel();
         this._signalIcons.paint(graphics, x, y, super._width, super._height, index);
         return 0;
      }
   }

   @Override
   public final void initialize(Hashtable params, Object context) {
      super.initialize(params, context);
      this._signalIcons = this.parseImage(params, context);
      this.ribbonComponentChanged(null);
   }

   private final IconCollection parseImage(Hashtable params, Object context) {
      IconCollection collection = null;
      String name = (String)params.get("img");
      if (name == null) {
         name = (String)params.get("icon");
         if (name == null) {
            name = "net_rim_Ribbon_SignalLevel";
         }

         collection = IconCollection.get(name, 9);
         super._width = collection.getWidth(Integer.MAX_VALUE, Integer.MAX_VALUE);
         super._height = collection.getHeight(Integer.MAX_VALUE, Integer.MAX_VALUE);
         return collection;
      } else {
         collection = new IconCollection(9, 1);
         int ext = name.indexOf(46);
         if (ext != -1) {
            name = name.substring(0, ext);
         }

         EncodedImage image = ThemeManager.getActiveTheme().getImage(name, true);
         if (image == null) {
            throw new NullPointerException("Image not found: " + name);
         }

         super._width = image.getWidth() / 9;
         super._height = image.getHeight();
         if (image.getWidth() != super._width * 9) {
            throw new IllegalArgumentException("Image is the wrong size: " + name);
         }

         collection.addImage(image, super._width, super._height, true);
         return collection;
      }
   }

   @Override
   public final synchronized void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
      this._listener = listener;
   }

   @Override
   public final void setDimensionsAvailable(int width, int height) {
      super._width = width;
      super._height = height;
   }

   @Override
   public final synchronized void ribbonComponentChanged(RibbonComponent component) {
      RibbonComponent$RibbonComponentChangeListener listener = this._listener;
      if (listener != null) {
         listener.ribbonComponentChanged(this);
      }
   }

   @Override
   public final void uninitialize() {
   }
}
