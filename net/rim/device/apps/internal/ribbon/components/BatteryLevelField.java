package net.rim.device.apps.internal.ribbon.components;

import java.util.Hashtable;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.TextRibbonComponent;
import net.rim.device.internal.ui.IconCollection;

final class BatteryLevelField extends TextRibbonComponent implements RibbonComponent$RibbonComponentChangeListener {
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private IconCollection _batteryIcons;
   private static final int ICON_COLUMNS;
   private static final int ICON_ROWS;
   private static final int INVALID_BATTERY_INDEX;

   public BatteryLevelField() {
   }

   @Override
   public final void applyTheme() {
   }

   @Override
   public final synchronized void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
      this._listener = listener;
   }

   @Override
   public final synchronized void ribbonComponentChanged(RibbonComponent component) {
      RibbonComponent$RibbonComponentChangeListener listener = this._listener;
      if (listener != null) {
         listener.ribbonComponentChanged(this);
      }
   }

   private final int getIndexFromLevel(int level, boolean charging) {
      int chargingRow = 65536;
      boolean invalidBattery = (DeviceInfo.getBatteryStatus() & 2097152) != 0;
      int index;
      if (invalidBattery) {
         index = 11;
      } else {
         index = (level + 4) / 10;
         if (index < 0) {
            index = 0;
         } else if (index > 10) {
            index = 10;
         }
      }

      if (charging) {
         index += chargingRow;
      }

      return index;
   }

   @Override
   protected final String getDefaultTag() {
      return "signal-level";
   }

   @Override
   public final void initialize(Hashtable params, Object context) {
      super.initialize(params, context);
      this._batteryIcons = this.parseImage(params, context);
      this.ribbonComponentChanged(null);
   }

   @Override
   public final void uninitialize() {
   }

   @Override
   public final int paintComponent(Graphics graphics, int x, int y, int width, int height, Object context) {
      int level = DeviceInfo.getBatteryLevel();
      boolean charging = (DeviceInfo.getBatteryStatus() & 1) != 0;
      int index = this.getIndexFromLevel(level, charging);

      try {
         this._batteryIcons.paint(graphics, x, y, super._width, super._height, index);
         return 0;
      } finally {
         ;
      }
   }

   private final IconCollection parseImage(Hashtable params, Object context) {
      IconCollection collection = null;
      String name = (String)params.get("img");
      if (name == null) {
         name = (String)params.get("icon");
         if (name == null) {
            name = "net_rim_Ribbon_BatteryLevel";
         }

         collection = IconCollection.get(name, 12, 2);
         super._width = collection.getWidth(Integer.MAX_VALUE, Integer.MAX_VALUE);
         super._height = collection.getHeight(Integer.MAX_VALUE, Integer.MAX_VALUE);
         return collection;
      } else {
         collection = (IconCollection)(new Object(12, 2));
         int ext = name.indexOf(46);
         if (ext != -1) {
            name = name.substring(0, ext);
         }

         EncodedImage image = ThemeManager.getActiveTheme().getImage(name, true);
         if (image == null) {
            throw new Object(((StringBuffer)(new Object("Image not found: "))).append(name).toString());
         } else {
            super._width = image.getWidth() / 12;
            super._height = image.getHeight() / 2;
            if (image.getWidth() == super._width * 12 && image.getHeight() == super._height * 2) {
               collection.addImage(image, super._width, super._height, true);
               return collection;
            } else {
               throw new Object(((StringBuffer)(new Object("Image is the wrong size: "))).append(name).toString());
            }
         }
      }
   }
}
