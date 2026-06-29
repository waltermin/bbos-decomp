package net.rim.device.cldc.impl.hrt.editor;

import net.rim.device.api.hrt.DAC;
import net.rim.device.api.hrt.DomainNameDAC;
import net.rim.device.api.hrt.IPv4UdpDAC;
import net.rim.device.api.hrt.IntDAC;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.text.IPTextFilter;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.IPEditField;

final class HRTAppDacDialog extends Dialog {
   private int _netType;
   private EditField _dacField;
   private int _retCode;
   private String _retObject;
   private DAC _dac;
   public static final int CANCEL = 0;
   public static final int OKAY = 1;

   HRTAppDacDialog(DAC dac, int netType, String initString) {
      super(null, null, null, 0, Bitmap.getPredefinedBitmap(0), 65536);
      this._dac = dac;
      this._netType = netType;
      this.getLabel().setText(this.getAppStringFromArray(initString == null ? 201 : 202));
      this._dacField = new IPEditField(this.getAppStringFromArray(41), initString);
      this._dacField.setFilter(dac instanceof DomainNameDAC ? new IPTextFilter() : new IPTextFilter(2));
      this.add(this._dacField);
   }

   private final String getAppStringFromArray(int id) {
      return HRTAppResources.getResourceBundle().getStringArray(id)[this._netType - 2];
   }

   public final int go() {
      UiApplication.getUiApplication().pushModalScreen(this);
      return this._retCode;
   }

   public final String getRetObject() {
      return this._retObject;
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      menu.add(new HRTAppDacDialog$MyMenuItem(this, this.getAppStringFromArray(203), 1));
      menu.add(new HRTAppDacDialog$MyMenuItem(this, CommonResource.getString(19), 0));
   }

   private final boolean canClose() {
      String text = this._dacField.getText();

      try {
         if (!(this._dac instanceof IntDAC)) {
            if (this._dac instanceof IPv4UdpDAC) {
               IPv4UdpDAC.string2Addr(text);
            }
         } else {
            ((IntDAC)this._dac).string2Addr(text);
         }

         this._retObject = text;
         return true;
      } finally {
         Dialog.alert(this.getAppStringFromArray(204));
         return false;
      }
   }
}
