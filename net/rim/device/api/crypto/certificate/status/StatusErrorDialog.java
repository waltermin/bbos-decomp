package net.rim.device.api.crypto.certificate.status;

import java.util.Enumeration;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ldap.LDAPQuery;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.resource.crypto.CryptoIcons;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.device.internal.ui.component.PopupDialog;

public class StatusErrorDialog extends PopupDialog implements FieldChangeListener {
   private VerticalFieldManager _vfm;
   private ButtonField _okButton;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(-7644390350925054654L, "net.rim.device.internal.resource.crypto.StatusProviders");

   public StatusErrorDialog(CertificateStatusQuery query) {
      this(query, 0);
   }

   public StatusErrorDialog(CertificateStatusQuery query, long style) {
      super((Manager)(new Object()), style);
      if (query == null) {
         throw new Object();
      }

      String errorMessage = query.getErrorMessage();
      if (errorMessage != null) {
         this.addFields(errorMessage);
         this.appendErrorMessages(query);
      }
   }

   public StatusErrorDialog(LDAPQuery query) {
      super((Manager)(new Object()));
      this.setModal(true);
      if (query == null) {
         throw new Object();
      }

      String errorMessage = query.getErrorMsg();
      if (errorMessage != null) {
         this.addFields(errorMessage);
      }
   }

   private void addFields(String errorMessage) {
      VerticalFieldManager vfm = (VerticalFieldManager)this.getDelegate();
      LabelField titleField = (LabelField)(new Object(_rb.getString(17), 36028797018964032L));
      Font boldFont = Font.getDefault();
      boldFont = boldFont.derive(boldFont.getStyle() | 1);
      titleField.setFont(boldFont);
      vfm.add(titleField);
      vfm.add((Field)(new Object()));
      this._vfm = (VerticalFieldManager)(new Object(299067162755072L));
      HorizontalFieldManager messageManager = (HorizontalFieldManager)(new Object());
      ImageField iconField = CryptoIcons.getLargeImageField(CryptoIcons.getImage(4), 51539607552L);
      messageManager.add(iconField);
      messageManager.add((Field)(new Object(4)));
      RichTextField textMessage = (RichTextField)(new Object(errorMessage, 36028848558571520L));
      messageManager.add(textMessage);
      this._vfm.add(messageManager);
      HorizontalFieldManager buttonManager = (HorizontalFieldManager)(new Object(8589934592L));
      this._okButton = (ButtonField)(new Object(CommonResource.getString(100)));
      this._okButton.setChangeListener(this);
      buttonManager.add(this._okButton);
      this._vfm.add((Field)(new Object(4)));
      this._vfm.add(buttonManager);
      vfm.add(this._vfm);
   }

   private void appendErrorMessages(CertificateStatusQuery query) {
      Enumeration errorMessages = query.getProviderErrorMessages();

      while (errorMessages.hasMoreElements()) {
         this.insertAdditionalField((Field)(new Object((String)errorMessages.nextElement())));
      }
   }

   private void insertAdditionalField(Field field) {
      this._vfm.insert((Field)(new Object()), 1);
      this._vfm.insert(field, 2);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean handled = super.keyChar(key, status, time);
      if (!handled && key == 27) {
         this.fieldChanged(this._okButton, Integer.MIN_VALUE);
      }

      return handled;
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._okButton) {
         this.close(4);
      }
   }
}
