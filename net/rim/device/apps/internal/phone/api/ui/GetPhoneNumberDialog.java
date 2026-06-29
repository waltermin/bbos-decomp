package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.component.VerticalSpacerField;

public final class GetPhoneNumberDialog extends PopupDialog {
   EditField _phoneNumberField;

   public GetPhoneNumberDialog(String prompt) {
      this(prompt, 80);
   }

   public GetPhoneNumberDialog(String prompt, int maxLength) {
      super(new VerticalFieldManager(1153202979583557632L));
      this.setModal(true);
      this._phoneNumberField = new EditField(PhoneResources.getString(500), "", maxLength, 0);
      this.setFilter(new SimplePhoneNumberFilter());
      this.addPrompt(prompt);
      this.add(new VerticalSpacerField(4));
      this.add(this._phoneNumberField);
      this.add(new VerticalSpacerField(4));
   }

   public final void setFilter(TextFilter textFilter) {
      this._phoneNumberField.setFilter(textFilter);
   }

   protected final void addPrompt(String prompt) {
      this.add(new RichTextField(prompt, 36028797018963968L));
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return this.invokeAction(1);
   }

   @Override
   protected final boolean invokeAction(int action) {
      if (action == 1) {
         this.close(0);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.close(-1);
      } else if (key == '\n' && this._phoneNumberField.getTextLength() >= 0) {
         this.close(0);
      }

      return super.keyChar(key, status, time);
   }

   public final String getPhoneNumber() {
      this.show();
      if (this.getCloseReason() == 0) {
         String phoneNumber = this._phoneNumberField.getText();
         if (phoneNumber != null) {
            return phoneNumber.trim();
         }
      }

      return null;
   }
}
