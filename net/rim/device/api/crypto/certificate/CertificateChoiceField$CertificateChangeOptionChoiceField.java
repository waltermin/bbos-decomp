package net.rim.device.api.crypto.certificate;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.ChoiceField$ChangeOptionChoiceField;

public class CertificateChoiceField$CertificateChangeOptionChoiceField extends ChoiceField$ChangeOptionChoiceField {
   public CertificateChoiceField$CertificateChangeOptionChoiceField(
      CertificateChoiceField certificateChoiceField, String label, int numChoices, int selectedIndex, long style
   ) {
      certificateChoiceField.getClass();
      super(certificateChoiceField, label, numChoices, selectedIndex, style);
   }

   public void underlyingFieldInvalidated() {
      this.invalidate();
   }

   @Override
   protected void setSize(int size) {
      super.setSize(size);
      if (size == 0) {
         Screen choiceInPlaceScreen = this.getScreen();
         if (choiceInPlaceScreen.isDisplayed()) {
            choiceInPlaceScreen.close();
         }
      }
   }
}
