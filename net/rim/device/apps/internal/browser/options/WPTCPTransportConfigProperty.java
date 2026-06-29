package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;

public final class WPTCPTransportConfigProperty extends TransportConfigProperty {
   private CheckboxField _persistentConnectionsField;
   private CheckboxField _http11ModeField;
   private EditField _proxyAddressField;
   private EditField _connectProxyAddressField;
   private EditField _authConfigurateAddressField;
   private CheckboxField _loadBalancingDNSField;
   private EditField _primaryDNSField;
   private EditField _secondaryDNSField;
   private NumericChoiceField _proxyUsernameTypeField;
   private NumericChoiceField _proxyPasswordTypeField;
   private EditField _proxyUsernameField;
   private EditField _proxyPasswordField;
   private NumericChoiceField _defaultTCPConfigModeField;
   private EditField _sessionTimeoutField;
   private CheckboxField _alwaysSendBasicProxyAuth;

   public WPTCPTransportConfigProperty(boolean restrictedAccess) {
      super(restrictedAccess);
   }

   @Override
   public final VerticalFieldManager addFields(
      VerticalFieldManager screen, boolean editable, String cid, String uid, int wtlsMode, int wtlsClientIdType, String wtlsClientIdValue
   ) {
      WPTCPServiceRecord record = WPTCPServiceRecord.getRecord(cid, uid);
      if (record == null) {
         return screen;
      }

      if (!super._restrictedAccess) {
         screen.add((Field)(new Object("")));
         screen.add((Field)(new Object(BrowserResources.getString(689))));
      }

      this._http11ModeField = (CheckboxField)(new Object(BrowserResources.getString(690), record.getPropertyAsBoolean(3)));
      this._persistentConnectionsField = (CheckboxField)(new Object(BrowserResources.getString(691), record.getPropertyAsBoolean(2)));
      this._authConfigurateAddressField = (EditField)(new Object(
         ((StringBuffer)(new Object())).append(BrowserResources.getString(692)).append(' ').toString(), record.getPropertyAsString(4)
      ));
      this._proxyAddressField = (EditField)(new Object(
         ((StringBuffer)(new Object())).append(BrowserResources.getString(693)).append(' ').toString(), record.getPropertyAsString(1)
      ));
      this._connectProxyAddressField = (EditField)(new Object(
         ((StringBuffer)(new Object())).append(BrowserResources.getString(719)).append(' ').toString(), record.getPropertyAsString(8)
      ));
      this._loadBalancingDNSField = (CheckboxField)(new Object(BrowserResources.getString(702), record.getPropertyAsBoolean(5)));
      this._primaryDNSField = (EditField)(new Object(
         ((StringBuffer)(new Object())).append(BrowserResources.getString(703)).append(' ').toString(), record.getPropertyAsString(6)
      ));
      this._secondaryDNSField = (EditField)(new Object(
         ((StringBuffer)(new Object())).append(BrowserResources.getString(704)).append(' ').toString(), record.getPropertyAsString(7)
      ));
      this._proxyUsernameTypeField = (NumericChoiceField)(new Object("Username type:", 0, 24, 1, record.getPropertyAsInt(9)));
      this._proxyPasswordTypeField = (NumericChoiceField)(new Object("Password type:", 0, 24, 1, record.getPropertyAsInt(10)));
      this._proxyUsernameField = (EditField)(new Object("Username value: ", record.getPropertyAsString(11)));
      this._proxyPasswordField = (EditField)(new Object("Password value: ", record.getPropertyAsString(12)));
      this._defaultTCPConfigModeField = (NumericChoiceField)(new Object("Default TCP config mode:", 0, 1, 1, record.getPropertyAsInt(18)));
      this._alwaysSendBasicProxyAuth = (CheckboxField)(new Object("Always send basic HTTP auth: ", record.getPropertyAsBoolean(21)));
      int sessionTimeout = record.getPropertyAsInt(20);
      this._sessionTimeoutField = (EditField)(new Object(BrowserResources.getString(202), sessionTimeout >= 0 ? String.valueOf(sessionTimeout) : null));
      this._sessionTimeoutField.setFilter(TextFilter.get(1));
      if (!editable) {
         this._http11ModeField.setEditable(false);
         this._persistentConnectionsField.setEditable(false);
         this._authConfigurateAddressField.setEditable(false);
         this._proxyAddressField.setEditable(false);
         this._connectProxyAddressField.setEditable(false);
         this._loadBalancingDNSField.setEditable(false);
         this._primaryDNSField.setEditable(false);
         this._secondaryDNSField.setEditable(false);
         this._proxyUsernameTypeField.setEditable(false);
         this._proxyPasswordTypeField.setEditable(false);
         this._proxyUsernameField.setEditable(false);
         this._proxyPasswordField.setEditable(false);
         this._defaultTCPConfigModeField.setEditable(false);
         this._sessionTimeoutField.setEditable(false);
         this._alwaysSendBasicProxyAuth.setEditable(false);
      }

      if (!super._restrictedAccess) {
         screen.add(this._http11ModeField);
         screen.add(this._persistentConnectionsField);
         screen.add(this._authConfigurateAddressField);
         screen.add(this._proxyAddressField);
         screen.add(this._connectProxyAddressField);
         screen.add(this._loadBalancingDNSField);
         screen.add(this._primaryDNSField);
         screen.add(this._secondaryDNSField);
         screen.add(this._proxyUsernameTypeField);
         screen.add(this._proxyPasswordTypeField);
         screen.add(this._proxyUsernameField);
         screen.add(this._proxyPasswordField);
         screen.add(this._defaultTCPConfigModeField);
         screen.add(this._sessionTimeoutField);
         screen.add(this._alwaysSendBasicProxyAuth);
      }

      return screen;
   }

   @Override
   public final void saveProperty(String cid, String uid) {
      WPTCPServiceRecord record = WPTCPServiceRecord.getRecord(cid, uid);
      if (record == null) {
         Status.show(BrowserResources.getString(291));
      } else {
         record.setProperty(1, this._proxyAddressField.getText());
         record.setProperty(8, this._connectProxyAddressField.getText());
         record.setProperty(2, this._persistentConnectionsField.getChecked());
         record.setProperty(3, this._http11ModeField.getChecked());
         record.setProperty(4, this._authConfigurateAddressField.getText());
         record.setProperty(5, this._loadBalancingDNSField.getChecked());
         record.setProperty(6, this._primaryDNSField.getText());
         record.setProperty(7, this._secondaryDNSField.getText());
         record.setProperty(9, this._proxyUsernameTypeField.getSelectedValue());
         record.setProperty(10, this._proxyPasswordTypeField.getSelectedValue());
         record.setProperty(11, this._proxyUsernameField.getText());
         record.setProperty(12, this._proxyPasswordField.getText());
         record.setProperty(18, this._defaultTCPConfigModeField.getSelectedValue());
         record.setProperty(21, this._alwaysSendBasicProxyAuth.getChecked());
         String sessionTimeoutString = this._sessionTimeoutField.getText();
         if (sessionTimeoutString.length() > 0) {
            label53:
            try {
               int sessionTimeout = Integer.parseInt(sessionTimeoutString);
               if (sessionTimeout >= 0) {
                  record.setProperty(20, sessionTimeout);
               }
            } finally {
               break label53;
            }
         }

         try {
            if (!record.saveData()) {
               Status.show(BrowserResources.getString(291));
            }
         } finally {
            return;
         }
      }
   }
}
