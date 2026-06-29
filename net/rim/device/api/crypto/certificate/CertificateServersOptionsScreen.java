package net.rim.device.api.crypto.certificate;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

public final class CertificateServersOptionsScreen extends AppsMainScreen implements FieldChangeListener {
   private CertificateServers _certificateServers;
   private CertificateServerInfo _serverInfo;
   private ObjectChoiceField _serverTypeField;
   private AutoTextEditField _friendlyNameField;
   private RichTextField _serverLabelField;
   private RichTextField _baseQueryLabelField;
   private RichTextField _portLabelField;
   private RichTextField _authLabelField;
   private RichTextField _connectionTypeLabelField;
   private EditField _serverField;
   private EditField _baseQueryField;
   private EditField _portField;
   private ObjectChoiceField _authField;
   private ObjectChoiceField _connectionTypeField;
   private int _initialType;
   private boolean _ldapMode;
   private CertificateServersOptionsScreen$ServerVerb _saveVerb;
   private CertificateServersOptionsScreen$ServerVerb _deleteVerb;
   private CertificateServersOptionsScreen$ServerVerb _closeVerb;
   private Font _boldFont;
   private VerticalIndentFieldManager _vifm;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(
      -3843310740975580338L, "net.rim.device.internal.resource.crypto.CertificateServersOptions"
   );
   private static final int DEFAULT_LDAP_PORT = 389;
   private static final int DEFAULT_LDAPS_PORT = 636;

   public CertificateServersOptionsScreen(CertificateServerInfo serverInfo, int initialType) {
      super(0);
      this.setDefaultClose(false);
      this._serverInfo = serverInfo;
      this._initialType = initialType;
      this._ldapMode = initialType == 1;
      this._certificateServers = CertificateServers.getInstance();
      this.addFields();
      this.setHelp("net_rim_bb_secureemail_help/certificate_servers");
   }

   private final void addFields() {
      if (this._serverInfo == null) {
         this.setTitle(_rb.getString(0));
      } else {
         this.setTitle(this._serverInfo.getFriendlyName());
      }

      String[] choices = new String[]{_rb.getString(111), _rb.getString(110), _rb.getString(109)};
      Font font = Font.getDefault();
      this._boldFont = font.derive(font.getStyle() | 1);
      this._serverTypeField = new ObjectChoiceField(_rb.getString(1), choices, this.convertTypeToIndex(this._initialType));
      this._serverTypeField.setFont(this._boldFont);
      this._serverTypeField.setEditable(true);
      this._serverTypeField.setChangeListener(this);
      this.add(this._serverTypeField);
      this.add(new SeparatorField());
      String friendlyName = null;
      String server = null;
      String baseQuery = null;
      int port = 389;
      int authType = 0;
      int connType = 0;
      if (this._serverInfo != null) {
         friendlyName = this._serverInfo.getFriendlyName();
         server = this._serverInfo.getServer();
         baseQuery = this._serverInfo.getBaseQuery();
         port = this._serverInfo.getPort();
         authType = this._serverInfo.getAuthType();
         connType = this._serverInfo.getConnectionType();
      } else {
         friendlyName = "";
         server = "";
         baseQuery = "";
      }

      server = server.trim();
      int type = this.getType();
      this._vifm = new VerticalIndentFieldManager(1153220571769602048L);
      this.add(this._vifm);
      long style = 0;
      if (friendlyName == null) {
         friendlyName = "";
      }

      LabelField labelField = new LabelField(_rb.getString(4), 45035996273704960L);
      labelField.setFont(this._boldFont);
      this._vifm.add(labelField);
      this._friendlyNameField = new AutoTextEditField(null, friendlyName, 256, style | 2147483648L);
      this._vifm.add(this._friendlyNameField, 12);
      if (server == null) {
         server = "";
      }

      String label = null;
      if (type == 1) {
         label = _rb.getString(2);
      } else {
         label = _rb.getString(3);
      }

      this._serverLabelField = new RichTextField(label, 45035996273704960L);
      this._serverLabelField.setFont(this._boldFont);
      this._vifm.add(this._serverLabelField);
      this._serverField = new EditField(null, server, 1024, style | 2147483648L | 117440512);
      this._vifm.add(this._serverField, 12);
      if (baseQuery == null) {
         baseQuery = "";
      }

      this._baseQueryLabelField = new RichTextField(_rb.getString(5), 45035996273704960L);
      this._baseQueryLabelField.setFont(this._boldFont);
      this._baseQueryField = new EditField(null, baseQuery, 1024, style | 2147483648L);
      this._portLabelField = new RichTextField(_rb.getString(6), 45035996273704960L);
      this._portLabelField.setFont(this._boldFont);
      this._portField = new EditField(null, String.valueOf(port), 10, style | 16777216 | 2147483648L);
      this._authLabelField = new RichTextField(_rb.getString(118), 45035996273704960L);
      this._authLabelField.setFont(this._boldFont);
      this._authField = new ObjectChoiceField(null, _rb.getStringArray(119), authType, 4294967296L);
      this._authField.setEditable(true);
      this._connectionTypeLabelField = new RichTextField(_rb.getString(120), 45035996273704960L);
      this._connectionTypeLabelField.setFont(this._boldFont);
      this._connectionTypeField = new ObjectChoiceField(null, _rb.getStringArray(121), connType, 4294967296L);
      this._connectionTypeField.setEditable(true);
      this._connectionTypeField.setChangeListener(this);
      if (type == 1) {
         this._vifm.add(this._baseQueryLabelField);
         this._vifm.add(this._baseQueryField, 12);
         this._vifm.add(this._portLabelField);
         this._vifm.add(this._portField, 12);
         this._vifm.add(this._authLabelField);
         this._vifm.add(this._authField, 12);
         this._vifm.add(this._connectionTypeLabelField);
         this._vifm.add(this._connectionTypeField, 12);
      }
   }

   private final String getServer() {
      return this._serverField.getText();
   }

   private final int getType() {
      return this.convertIndexToType(this._serverTypeField.getSelectedIndex());
   }

   private final String getFriendlyName() {
      return this._friendlyNameField.getText();
   }

   private final String getBaseQuery() {
      return this.getType() == 1 ? this._baseQueryField.getText() : null;
   }

   private final int getPort() {
      if (this.getType() != 1) {
         return 389;
      }

      String num = this._portField.getText();
      if (num == null) {
         return -1;
      }

      try {
         return Integer.parseInt(num);
      } finally {
         ;
      }
   }

   private final void setPort(int port) {
      this._portField.setText(Integer.toString(port));
   }

   private final int getAuthType() {
      return this.getType() != 1 ? 0 : this._authField.getSelectedIndex();
   }

   private final int getConnectionType() {
      return this.getType() != 1 ? 0 : this._connectionTypeField.getSelectedIndex();
   }

   @Override
   public final boolean keyChar(char key, int time, int status) {
      if (key == '\n') {
         Field field = this.getFieldWithFocus();
         if (field == this._serverField) {
            this._friendlyNameField.setFocus();
            return true;
         }

         if (field == this._friendlyNameField) {
            if (this.getType() == 1) {
               this._baseQueryField.setFocus();
               return true;
            }
         } else if (field == this._baseQueryField) {
            if (this.getType() == 1) {
               this._portField.setFocus();
               return true;
            }
         } else if (field == this._portField && this.getType() == 1) {
            this._authField.setFocus();
         }

         return true;
      } else if (key == 27) {
         if (this.hasChanged()) {
            this.promptForSave();
            return true;
         } else {
            this.doClose();
            return true;
         }
      } else {
         return super.keyChar(key, time, status);
      }
   }

   private final int convertTypeToIndex(int type) {
      switch (type) {
         case 0:
            throw new IllegalArgumentException();
         case 1:
         default:
            return 0;
         case 2:
            return 1;
         case 3:
            return 2;
      }
   }

   private final int convertIndexToType(int index) {
      switch (index) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            return 1;
         case 1:
            return 2;
         case 2:
            return 3;
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      if (this._saveVerb == null) {
         this._saveVerb = new CertificateServersOptionsScreen$ServerVerb(this, 0, 332288, CommonResource.getBundle(), 18);
      }

      menu.add(this._saveVerb);
      menu.setDefault(this._saveVerb);
      if (this._serverInfo != null) {
         if (this._deleteVerb == null) {
            this._deleteVerb = new CertificateServersOptionsScreen$ServerVerb(this, 1, 611472, CommonResource.getBundle(), 17);
         }

         menu.add(this._deleteVerb);
         VerbRepository verbRepository = VerbRepository.getVerbRepository(1363053212162519223L);
         Verb[] verbs = verbRepository.getVerbs(4738722199580714034L);
         if (verbs != null) {
            for (int i = 0; i < verbs.length; i++) {
               Verb var10000 = verbs[i];
               if (verbs[i] instanceof SetParameter) {
                  ((SetParameter)var10000).setParameter(this._serverInfo);
               }

               if (verbs[i] instanceof Copyable) {
                  menu.add((Verb)((Copyable)verbs[i]).copy());
               } else {
                  menu.add(verbs[i]);
               }
            }
         }
      }

      if (this._closeVerb == null) {
         this._closeVerb = new CertificateServersOptionsScreen$ServerVerb(this, 2, 268501008, CommonResource.getBundle(), 9);
      }

      menu.add(this._closeVerb);
      if (!this.hasChanged()) {
         menu.setDefault(this._closeVerb);
      }
   }

   private final boolean hasChanged() {
      return this._serverInfo == null
         || !StringUtilities.strEqual(this._serverInfo.getServer(), this.getServer())
         || !StringUtilities.strEqual(this._serverInfo.getFriendlyName(), this.getFriendlyName())
         || !StringUtilities.strEqual(this._serverInfo.getBaseQuery(), this.getBaseQuery())
         || this._serverInfo.getPort() != this.getPort()
         || this._serverInfo.getType() != this.getType()
         || this._serverInfo.getAuthType() != this.getAuthType()
         || this._serverInfo.getConnectionType() != this.getConnectionType();
   }

   private final void promptForSave() {
      int choice = Dialog.ask(1);
      if (choice == 1) {
         this.doSave();
      } else {
         if (choice == 2) {
            this.doClose();
         }
      }
   }

   private final void doClose() {
      UiApplication.getUiApplication().popScreen(this);
      synchronized (this) {
         this.notifyAll();
      }
   }

   private final void doSave() {
      if (this.getFriendlyName().trim().length() == 0) {
         Dialog.alert(_rb.getString(115));
         this._friendlyNameField.setFocus();
      } else if (this.getServer().trim().length() == 0) {
         if (this.getType() == 1) {
            Dialog.alert(_rb.getString(10));
         } else {
            Dialog.alert(_rb.getString(117));
         }

         this._serverField.setFocus();
      } else if (this.getType() == 1 && this.getPort() == -1) {
         Dialog.alert(_rb.getString(116));
         this._portField.setFocus();
      } else {
         if (this._serverInfo != null) {
            this.doDelete();
         }

         if (this.getType() == 1) {
            this._certificateServers
               .addServer(
                  this.getServer(), this.getType(), this.getFriendlyName(), this.getBaseQuery(), this.getPort(), this.getAuthType(), this.getConnectionType()
               );
         } else {
            this._certificateServers.addServer(this.getServer(), this.getType(), this.getFriendlyName(), null, 389, 0);
         }

         this.doClose();
      }
   }

   private final void doDelete() {
      this._certificateServers.removeServer(this._serverInfo);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field != this._serverTypeField) {
         if (field == this._connectionTypeField) {
            if (this.getConnectionType() == 1 && this.getPort() == 389) {
               this.setPort(636);
               return;
            }

            if (this.getConnectionType() == 0 && this.getPort() == 636) {
               this.setPort(389);
            }
         }
      } else {
         int type = this.getType();
         if (type == 1 && !this._ldapMode) {
            this._ldapMode = true;
            this._serverLabelField.setText(_rb.getString(2));
            this._vifm.add(this._baseQueryLabelField);
            this._vifm.add(this._baseQueryField, 12);
            this._vifm.add(this._portLabelField);
            this._vifm.add(this._portField, 12);
            this._vifm.add(this._authLabelField);
            this._vifm.add(this._authField, 12);
            this._vifm.add(this._connectionTypeLabelField);
            this._vifm.add(this._connectionTypeField, 12);
            return;
         }

         if (this._ldapMode) {
            this._ldapMode = false;
            this._serverLabelField.setText(_rb.getString(3));
            this._vifm.delete(this._baseQueryLabelField);
            this._vifm.delete(this._baseQueryField);
            this._vifm.delete(this._portLabelField);
            this._vifm.delete(this._portField);
            this._vifm.delete(this._authLabelField);
            this._vifm.delete(this._authField);
            this._vifm.delete(this._connectionTypeLabelField);
            this._vifm.delete(this._connectionTypeField);
            return;
         }
      }
   }
}
