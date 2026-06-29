package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.text.PhoneTextFilter;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.component.SimpleInputDialog;

final class AddNumberVerb extends Verb {
   private Field _selectedField;
   private AddNumberVerb$AddNumberCallback _callback;
   private CallForwardingScreen _callForwardingScreen;

   AddNumberVerb(AddNumberVerb$AddNumberCallback callback, Field selectedField) {
      super(120000);
      this._selectedField = selectedField;
      this._callback = callback;
   }

   AddNumberVerb(CallForwardingScreen callForwardingScreen) {
      this(null, null);
      this._callForwardingScreen = callForwardingScreen;
   }

   @Override
   public final String toString() {
      return PhoneResources.getString(6134);
   }

   @Override
   public final Object invoke(Object invoke) {
      SimpleInputDialog dlg = new AddNumberVerb$1(this, 7, PhoneResources.getString(224));
      BasicEditField editField = dlg.getEditField();
      int filterFlags = PhoneUtilities.cdmaWAFActive() ? 0 : 384;
      editField.setFilter(new PhoneTextFilter(filterFlags));
      if (invoke instanceof String) {
         dlg.setText((String)invoke);
      }

      int[] minMaxNumberLengths = CallForwardingOption.getMinMaxForwardingNumberLengths();
      dlg.setModal(true);
      dlg.setMinLength(minMaxNumberLengths[0]);
      dlg.setMaxLength(minMaxNumberLengths[1]);
      dlg.show();
      if (dlg.getCloseReason() == -1) {
         return null;
      }

      String number = dlg.getText();
      PhoneOptions phoneOptions = PhoneOptions.getOptions();
      int initialLen = phoneOptions.getSavedForwardingNumbers().length;
      phoneOptions.addSavedForwardingNumber(number);
      if (initialLen < phoneOptions.getSavedForwardingNumbers().length) {
         if (this._callback != null) {
            this._callback.onForwardingNumberAdded(number, this._selectedField);
         }

         if (this._callForwardingScreen != null) {
            this._callForwardingScreen.onForwardingNumbersChanged();
            UiApplication.getUiApplication()
               .pushScreen(new EditForwardingNumbersScreen(this._callForwardingScreen, this._callForwardingScreen.getActiveForwardingNumbers()));
            return null;
         }
      } else {
         Dialog.inform(PhoneResources.getString(6240));
      }

      return null;
   }
}
