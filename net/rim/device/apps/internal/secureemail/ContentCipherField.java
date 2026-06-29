package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class ContentCipherField extends VerticalFieldManager {
   private CheckboxField[] _checkboxFields = new Object[9];
   private int _ciphersToShow;

   public ContentCipherField(int ciphersToShow, int ciphersToCheck) {
      super(1152921504606846976L);
      this._ciphersToShow = ciphersToShow;
      String[] cipherLabels = ContentCiphers.getLabels();

      for (int i = 0; i < 9; i++) {
         int currentBit = 1 << i;
         if ((ciphersToShow & currentBit) != 0) {
            CheckboxField currentField = (CheckboxField)(new Object(cipherLabels[i], (ciphersToCheck & currentBit) != 0));
            this.add(currentField);
            this._checkboxFields[i] = currentField;
         }
      }
   }

   public int getCheckedCiphers() {
      int checkedCiphers = 0;

      for (int i = 0; i < 9; i++) {
         CheckboxField currentField = this._checkboxFields[i];
         if (currentField != null && currentField.getChecked()) {
            checkedCiphers |= 1 << i;
         }
      }

      return checkedCiphers;
   }

   public void setCheckedCiphers(int ciphersToCheck) {
      for (int i = 0; i < 9; i++) {
         int currentBit = 1 << i;
         if ((this._ciphersToShow & currentBit) != 0) {
            this._checkboxFields[i].setChecked((ciphersToCheck & currentBit) != 0);
         }
      }
   }
}
