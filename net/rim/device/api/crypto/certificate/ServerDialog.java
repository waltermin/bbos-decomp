package net.rim.device.api.crypto.certificate;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

final class ServerDialog extends PopupDialog implements FieldChangeListener {
   private Font _boldFont;
   private ButtonField _ok;
   private ButtonField _cancel;
   private AutoTextEditField _friendlyNameField;
   private AutoTextEditField _serverField;
   private AutoTextEditField _baseQueryField;
   private ObjectChoiceField _authField;
   private ObjectChoiceField _connectionTypeField;
   private EditField _portField;
   private boolean _editable;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(
      -3843310740975580338L, "net.rim.device.internal.resource.crypto.CertificateServersOptions"
   );
   private static final int DEFAULT_LDAP_PORT;
   private static final int INDENT_PIXEL_WIDTH;

   public final int getPort() {
      if (this._portField != null) {
         String num = this._portField.getText();
         if (num != null) {
            try {
               return Integer.parseInt(num);
            } finally {
               return -1;
            }
         }
      }

      return -1;
   }

   public final String getBaseQuery() {
      return this._baseQueryField != null ? this._baseQueryField.getText() : null;
   }

   public final String getFriendlyName() {
      return this._friendlyNameField != null ? this._friendlyNameField.getText() : null;
   }

   public final String getServer() {
      return this._serverField != null ? this._serverField.getText() : null;
   }

   public final int getAuthType() {
      return this._authField != null ? this._authField.getSelectedIndex() : 0;
   }

   public final int getConnectionType() {
      return this._connectionTypeField != null ? this._connectionTypeField.getSelectedIndex() : 0;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._ok) {
         this.close(0);
      } else {
         if (field == this._cancel) {
            this.close(-1);
         }
      }
   }

   public ServerDialog(CertificateServerInfo serverInfo, long style) {
      this(null, serverInfo, false, serverInfo == null ? 1 : serverInfo.getType(), style);
   }

   ServerDialog(String label, CertificateServerInfo serverInfo, boolean editable) {
      this(label, serverInfo, editable, serverInfo == null ? 1 : serverInfo.getType(), 0);
   }

   ServerDialog(String label, CertificateServerInfo serverInfo, boolean editable, int type) {
      this(label, serverInfo, editable, type, 0);
   }

   ServerDialog(String label, CertificateServerInfo serverInfo, boolean editable, int type, long style) {
      super((Manager)(new Object(1153220571769602048L)), style);
      VerticalIndentFieldManager vifm = (VerticalIndentFieldManager)this.getDelegate();
      this._editable = editable;
      this._boldFont = Font.getDefault();
      this._boldFont = this._boldFont.derive(this._boldFont.getStyle() | 1);
      if (label == null) {
         if (serverInfo != null) {
            type = serverInfo.getType();
            switch (type) {
               case 0:
                  break;
               case 1:
                  label = _rb.getString(111);
                  break;
               case 2:
                  label = _rb.getString(110);
                  break;
               case 3:
               default:
                  label = _rb.getString(109);
            }
         } else {
            label = _rb.getString(111);
         }
      }

      if (label != null) {
         RichTextField labelField = (RichTextField)(new Object(label, 45035996273704960L));
         labelField.setFont(this._boldFont);
         vifm.add(labelField);
      }

      vifm.add((Field)(new Object()));
      String friendlyName = null;
      String server = null;
      String baseQuery = null;
      int authType = 0;
      int connectionType = 0;
      int port = 389;
      if (serverInfo != null) {
         friendlyName = serverInfo.getFriendlyName();
         server = serverInfo.getServer();
         baseQuery = serverInfo.getBaseQuery();
         port = serverInfo.getPort();
         authType = serverInfo.getAuthType();
         connectionType = serverInfo.getConnectionType();
      }

      if (!this._editable) {
         style |= 9007199254740992L;
      }

      if (friendlyName != null && friendlyName.length() != 0 || this._editable) {
         if (friendlyName == null) {
            friendlyName = "";
         }

         LabelField labelField = (LabelField)(new Object(_rb.getString(4)));
         labelField.setFont(this._boldFont);
         vifm.add(labelField);
         this._friendlyNameField = (AutoTextEditField)(new Object(null, friendlyName, 256, 2147483648L));
         vifm.add(this._friendlyNameField, 12);
      }

      if (server != null && server.length() != 0 || this._editable) {
         if (server == null) {
            server = "";
         }

         LabelField labelField;
         if (type == 1) {
            labelField = (LabelField)(new Object(_rb.getString(2)));
         } else {
            labelField = (LabelField)(new Object(_rb.getString(3)));
         }

         labelField.setFont(this._boldFont);
         vifm.add(labelField);
         this._serverField = (AutoTextEditField)(new Object(null, server, 1024, 2264924160L));
         vifm.add(this._serverField, 12);
      }

      if (type == 1) {
         if (baseQuery != null && baseQuery.length() != 0 || this._editable) {
            if (baseQuery == null) {
               baseQuery = "";
            }

            LabelField labelField = (LabelField)(new Object(_rb.getString(5)));
            labelField.setFont(this._boldFont);
            vifm.add(labelField);
            this._baseQueryField = (AutoTextEditField)(new Object(null, baseQuery, 1024, 2147483648L));
            vifm.add(this._baseQueryField, 12);
         }

         LabelField labelField = (LabelField)(new Object(_rb.getString(6)));
         labelField.setFont(this._boldFont);
         vifm.add(labelField);
         this._portField = (EditField)(new Object(null, String.valueOf(port), 10, style | 16777216 | 2147483648L));
         vifm.add(this._portField, 12);
         RichTextField authLabelField = (RichTextField)(new Object(_rb.getString(118), 45035996273704960L));
         authLabelField.setFont(this._boldFont);
         vifm.add(authLabelField);
         this._authField = (ObjectChoiceField)(new Object(null, _rb.getStringArray(119), authType, 4294967296L));
         this._authField.setEditable(this._editable);
         vifm.add(this._authField, 12);
         RichTextField connectionTypeLabelField = (RichTextField)(new Object(_rb.getString(120), 45035996273704960L));
         connectionTypeLabelField.setFont(this._boldFont);
         vifm.add(connectionTypeLabelField);
         this._connectionTypeField = (ObjectChoiceField)(new Object(null, _rb.getStringArray(121), connectionType, 4294967296L));
         this._connectionTypeField.setEditable(this._editable);
         vifm.add(this._connectionTypeField, 12);
      }

      HorizontalFieldManager buttonManager = (HorizontalFieldManager)(new Object(12884901888L));
      this._ok = (ButtonField)(new Object(CommonResource.getString(100)));
      this._ok.setChangeListener(this);
      buttonManager.add(this._ok);
      if (this._editable) {
         this._cancel = (ButtonField)(new Object(CommonResource.getString(10005)));
         this._cancel.setChangeListener(this);
         buttonManager.add(this._cancel);
      }

      vifm.add(buttonManager);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.close(-1);
         return true;
      }

      if (key == '\n') {
         Field field = this.getLeafFieldWithFocus();
         if (field == this._cancel) {
            this.close(-1);
            return true;
         }

         if (field == this._ok) {
            this.close(0);
            return true;
         }
      }

      return super.keyChar(key, status, time);
   }

   public ServerDialog(CertificateServerInfo serverInfo) {
      this(null, serverInfo, false);
   }

   private final boolean verifyField(EditField editField, int resourceID) {
      if (editField != null) {
         String text = editField.getText();
         text = text.trim();
         if (text.length() == 0) {
            Dialog.alert(_rb.getString(resourceID));
            editField.setFocus();
            return false;
         }
      }

      return true;
   }

   @Override
   protected final void close(int closeReason) {
      if (closeReason != 0
         || this.verifyField(this._friendlyNameField, 115)
            && this.verifyField(this._serverField, 117)
            && this.verifyField(this._baseQueryField, 114)
            && this.verifyField(this._portField, 116)) {
         super.close(closeReason);
      }
   }
}
