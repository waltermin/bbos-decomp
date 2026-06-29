package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ui.WorldPhoneDisclaimerDialog;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.system.RadioInternal;

final class NetworkOptionsItem$CDMA_GSM_NetworkModeField implements NetworkOptionsItem$NetworkModeFieldProvider {
   private ObjectChoiceField _modeField;
   private WorldPhoneDisclaimerDialog _dialog;
   private final NetworkOptionsItem this$0;

   NetworkOptionsItem$CDMA_GSM_NetworkModeField(NetworkOptionsItem _1) {
      this.this$0 = _1;
      int enabledRadios = RadioInternal.getEnabledRadios();
      _1._savedNetMode = enabledRadios;
      String[] strs = _1._rb.getStringArray(2040);
      String[] args = new String[0];
      args = this.createRadioChoices(strs, NetworkOptionsItem.SUPPORTED_RADIOS);
      int i = 0;
      int[] map = _1._indexToNetMode;
      i = map.length - 1;

      while (i >= 0 && enabledRadios != map[i]) {
         i--;
      }

      if (i < 0) {
         i = 0;
      }

      this._modeField = new ObjectChoiceField(OptionsResources.getString(1969), args, i);
      this._modeField.setChangeListener(this);
      this._dialog = new WorldPhoneDisclaimerDialog(OptionsResources.getString(2051), enabledRadios);
   }

   @Override
   public final Field getField() {
      return this._modeField;
   }

   private final String[] createRadioChoices(String[] strs, int radios) {
      ResourceBundle rb = OptionsResources.getResourceBundle();
      String[] radioLabels = rb.getStringArray(2040);
      String[] radioValueStrings = rb.getFamily().getBundle(Locale.get(0)).getStringArray(2040);
      int[] radioValues = new int[radioLabels.length];

      for (int i = 0; i < radioValueStrings.length; i++) {
         radioValues[i] = Integer.parseInt(radioValueStrings[i]);
      }

      int[] radioCombinations = new int[0];
      String[] availableRadioLabels = new String[0];

      for (int i = 0; i < radioValues.length; i++) {
         int radioId = radioValues[i];
         if ((radios & radioId) == radioId) {
            Arrays.add(radioCombinations, radioId);
            Arrays.add(availableRadioLabels, strs[i]);
         }
      }

      this.this$0._indexToNetMode = radioCombinations;
      return availableRadioLabels;
   }

   @Override
   public final void discard() {
      if (this._modeField != null && RadioInternal.getActiveRadios() != this.this$0._savedNetMode) {
         this.setActiveRadios(this.this$0._savedNetMode);
      }
   }

   @Override
   public final void save() {
      if (this._modeField.isDirty()) {
         int newMode = this.this$0._indexToNetMode[this._modeField.getSelectedIndex()];
         if (WorldPhoneDisclaimerDialog.isDisclaimerNeeded() && newMode == 3) {
            this._dialog.showDialog();
         } else {
            this.setActiveRadios(newMode);
         }

         this._modeField.setDirty(false);
      }
   }

   @Override
   public final boolean isDirty() {
      return this._modeField.isDirty();
   }

   private final void setActiveRadios(int mode) {
      RadioInternal.setEnabledRadios(mode);
      if (RadioInternal.getActiveRadios() != 0) {
         RadioInternal.activateRadios(mode);
      }

      this.this$0.setupNetworkSelectionModeField(true);
   }

   @Override
   public final void update() {
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._modeField) {
         this.setActiveRadios(this.this$0._indexToNetMode[this._modeField.getSelectedIndex()]);
      }
   }
}
