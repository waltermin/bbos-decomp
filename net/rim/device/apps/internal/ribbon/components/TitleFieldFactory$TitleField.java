package net.rim.device.apps.internal.ribbon.components;

import java.util.Hashtable;
import net.rim.device.apps.api.ribbon.StringRibbonComponent;
import net.rim.device.apps.api.utility.props.StringProps;

final class TitleFieldFactory$TitleField extends StringRibbonComponent {
   private StringProps _context;

   @Override
   public final String getText() {
      return this._context != null ? this._context.get(TitleFieldFactory.TITLE_ID, "") : null;
   }

   @Override
   public final void initialize(Hashtable params, Object context) {
      if (context instanceof StringProps) {
         this._context = (StringProps)context;
      }

      super.initialize(params, context);
   }
}
