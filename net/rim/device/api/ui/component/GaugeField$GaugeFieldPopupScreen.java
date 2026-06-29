package net.rim.device.api.ui.component;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.UiInternal;

class GaugeField$GaugeFieldPopupScreen extends PopupScreen {
   private boolean _cancelled;

   public GaugeField$GaugeFieldPopupScreen(GaugeField fake) {
      super(new VerticalFieldManager(1152921504606846976L));
      this.add(new RichTextField(MessageFormat.format(CommonResource.getString(1011), new Object[]{UiInternal.BUNDLE.getString(7)}), 36028797018963968L));
      this.add(fake);
   }

   public boolean doModal() {
      Ui.getUiEngine().pushModalScreen(this);
      return !this._cancelled;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\u001b':
            this._cancelled = true;
         case '\n':
            Ui.getUiEngine().popScreen(this);
            return true;
         default:
            return super.keyChar(key, status, time);
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      Ui.getUiEngine().popScreen(this);
      return true;
   }
}
