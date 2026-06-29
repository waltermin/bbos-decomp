package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.system.NetworkInfo;
import net.rim.device.internal.ui.component.PopupDialog;

final class PrefNetworkSelectOption$RATSelectionPopup extends PopupDialog {
   private RichTextField _labelField;
   private ButtonField _button2G;
   private ButtonField _button3G;
   private NetworkInfo _info;

   PrefNetworkSelectOption$RATSelectionPopup(NetworkInfo info) {
      super((Manager)(new Object()));
      DialogFieldManager manager = (DialogFieldManager)this.getDelegate();
      this._info = info;
      this._labelField = (RichTextField)(new Object(OptionsResources.getString(1971), 36028797018963968L));
      manager.setMessage(this._labelField);
      String[] buttonText = OptionsResources.getStringArray(1970);
      this._button2G = (ButtonField)(new Object(buttonText[1]));
      this._button3G = (ButtonField)(new Object(buttonText[0]));
      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object());
      hfm.add(this._button2G);
      hfm.add(this._button3G);
      manager.addCustomField(hfm);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         return true;
      } else if (key == '\n') {
         this.acceptInput();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   private final void acceptInput() {
      Field focus = this.getLeafFieldWithFocus();
      int value = this._info.getCategory();
      value = NetworkOptionsUtils.clearFlag(value, 64);
      value = NetworkOptionsUtils.clearFlag(value, 16);
      if (focus == this._button2G) {
         this._info.setCategory(value | 16);
      } else if (focus == this._button3G) {
         this._info.setCategory(value | 64);
      }

      this.close(0);
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      this.acceptInput();
      return true;
   }
}
