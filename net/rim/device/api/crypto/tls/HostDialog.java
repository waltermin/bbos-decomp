package net.rim.device.api.crypto.tls;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.ui.component.PopupDialog;

public class HostDialog extends PopupDialog implements FieldChangeListener {
   private VerticalFieldManager _vfm;
   private ButtonField _okButton;
   private ButtonField _cancelButton;
   private HorizontalFieldManager _buttonManager;
   private ObjectChoiceField _certificateChoiceField;
   private EditField _hostNameField;
   public static final int OK = 1;
   public static final int CANCEL = 2;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(5710659227867441061L, "net.rim.device.internal.resource.crypto.SSL");

   public HostDialog(String hostName, String hostCertificate) {
      super(new VerticalFieldManager(281474976710656L), 0);
      this.initialize(false, hostName, new String[]{hostCertificate}, null);
   }

   public HostDialog(String hostName, String[] certificateChoices, String defaultCertificate) {
      super(new VerticalFieldManager(281474976710656L), 0);
      this.initialize(true, hostName, certificateChoices, defaultCertificate);
   }

   public HostDialog(String[] certificateChoices) {
      super(new VerticalFieldManager(281474976710656L), 0);
      this.initialize(true, null, certificateChoices, null);
   }

   private void initialize(boolean editable, String hostName, String[] certificateChoices, String defaultCertificate) {
      this._vfm = (VerticalFieldManager)this.getDelegate();
      this._vfm.add(new LabelField(_rb.getString(40)));
      this._hostNameField = new EditField(4412407808L | (editable ? 0 : 9007199254740992L));
      if (hostName != null) {
         this._hostNameField.setText(hostName);
      }

      this._vfm.add(this._hostNameField);
      this._vfm.add(new LabelField(_rb.getString(47)));
      this._certificateChoiceField = new ObjectChoiceField(null, certificateChoices);
      this._vfm.add(this._certificateChoiceField);
      if (defaultCertificate != null) {
         for (int i = 0; i < certificateChoices.length; i++) {
            if (certificateChoices[i].equals(defaultCertificate)) {
               this._certificateChoiceField.setSelectedIndex(i);
            }
         }
      }

      this._buttonManager = new HorizontalFieldManager(12884901888L);
      this._okButton = new ButtonField(CommonResources.getString(117));
      this._okButton.setChangeListener(this);
      this._buttonManager.add(this._okButton);
      if (editable) {
         this._cancelButton = new ButtonField(CommonResources.getString(9042));
         this._cancelButton.setChangeListener(this);
         this._buttonManager.add(this._cancelButton);
      }

      this._vfm.add(this._buttonManager);
   }

   public String getHostName() {
      return this._hostNameField.getText();
   }

   public int getCertificateIndex() {
      return this._certificateChoiceField.getSelectedIndex();
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._okButton) {
         this.close(1);
      } else {
         if (field == this._cancelButton) {
            this.close(2);
         }
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (!super.keyChar(key, status, time) && key == 27) {
         this.close(2);
         return true;
      } else {
         return false;
      }
   }
}
