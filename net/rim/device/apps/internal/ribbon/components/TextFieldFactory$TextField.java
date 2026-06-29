package net.rim.device.apps.internal.ribbon.components;

import java.util.Hashtable;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.StringRibbonComponent;

final class TextFieldFactory$TextField extends StringRibbonComponent implements RibbonComponent$RibbonComponentChangeListener {
   private int _id = -1;
   private String _text;

   @Override
   public final void applyTheme() {
   }

   @Override
   public final String getText() {
      return this._text;
   }

   @Override
   public final void ribbonComponentChanged(RibbonComponent component) {
      if (this._id > 0) {
         Theme currentTheme = ThemeManager.getActiveTheme();
         if (currentTheme != null) {
            label27:
            try {
               this._text = currentTheme.getString(this._id);
            } finally {
               break label27;
            }
         }

         if (this._text == null) {
            this._text = "";
         }
      }

      super.ribbonComponentChanged(component);
   }

   @Override
   public final void initialize(Hashtable params, Object context) {
      String id = (String)params.get("id");
      if (id != null) {
         this._id = Integer.parseInt(id);
      }

      this._text = (String)params.get("text");
      if (this._text == null) {
         this._text = "";
      }

      this.ribbonComponentChanged(null);
      super.initialize(params, context);
   }
}
