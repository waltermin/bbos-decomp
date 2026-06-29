package net.rim.device.api.crypto.certificate.status;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.ui.component.PopupDialog;

class CertificateStatusQuery$ProviderUiDialog extends PopupDialog implements FieldChangeListener {
   private ButtonField[] _buttons;
   private VerticalFieldManager _scrollManager;
   private RichTextField _messageField;
   private int[] _returnCodes;

   public CertificateStatusQuery$ProviderUiDialog(String title, String message, String[] buttonText, int[] returnCodes, int style) {
      super((Manager)(new Object()), style);
      VerticalFieldManager verticalManager = (VerticalFieldManager)this.getDelegate();
      LabelField titleLabel = (LabelField)(new Object(title == null ? CertificateStatusQuery._rb.getString(17) : title, 64));
      Font boldFont = Font.getDefault();
      boldFont = boldFont.derive(boldFont.getStyle() | 1);
      titleLabel.setFont(boldFont);
      verticalManager.add(titleLabel);
      verticalManager.add((Field)(new Object()));
      this._scrollManager = (VerticalFieldManager)(new Object(299067162755072L));
      this._scrollManager.add(this._messageField = (RichTextField)(new Object(message)));
      this._scrollManager.add((Field)(new Object(4)));
      HorizontalFieldManager buttonManager = (HorizontalFieldManager)(new Object(8589934592L));
      int size = buttonText.length;
      this._buttons = new Object[size];

      for (int i = 0; i < size; i++) {
         this._buttons[i] = (ButtonField)(new Object(buttonText[i]));
         this._buttons[i].setChangeListener(this);
         buttonManager.add(this._buttons[i]);
      }

      this._returnCodes = returnCodes;
      this._scrollManager.add(buttonManager);
      verticalManager.add(this._scrollManager);
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         if (this._scrollManager.getVirtualHeight() > this._scrollManager.getHeight()) {
            this._messageField.setFocus();
            return;
         }

         this._buttons[0].setFocus();
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      int size = this._buttons.length;

      for (int i = 0; i < size; i++) {
         if (field == this._buttons[i]) {
            this.close(this._returnCodes[i]);
            return;
         }
      }
   }
}
