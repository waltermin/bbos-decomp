package net.rim.device.internal.ui.component;

import net.rim.device.api.i18n.ResourceBundle;

public class ColorChoiceField$NamedColor {
   private int _color;
   private int _resourceId;
   private static ResourceBundle BUNDLE = ResourceBundle.getBundle(4053028540858866058L, "net.rim.device.internal.resource.Color");

   public ColorChoiceField$NamedColor(int color, int resourceId) {
      this._color = color;
      this._resourceId = resourceId;
   }

   @Override
   public boolean equals(Object object) {
      if (!(object instanceof ColorChoiceField$NamedColor)) {
         return false;
      }

      ColorChoiceField$NamedColor other = (ColorChoiceField$NamedColor)object;
      return this._color == other._color;
   }

   public int getColor() {
      return this._color;
   }

   @Override
   public String toString() {
      return BUNDLE.getString(this._resourceId);
   }
}
