package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.internal.browser.wap.WAPServiceRecord;

public final class WAPTransportConfigProperty extends TransportConfigProperty implements FieldChangeListener {
   private CheckboxField _secureAccessField;
   private EditField _authUsernameField;
   private PasswordEditField _authPasswordField;
   private CheckboxField _sessionResumeField;
   private ObjectChoiceField _wtlsModeField;
   private ObjectChoiceField _wtlsClientIdTypeField;
   private EditField _wtlsClientIdValueField;
   private EditField _mmscUrlField;
   private CheckboxField _wap20ConformanceField;
   private boolean _valuesAdded;
   private VerticalFieldManager _screen;

   public WAPTransportConfigProperty(boolean restrictedAccess) {
      super(restrictedAccess);
   }

   @Override
   public final VerticalFieldManager addFields(
      VerticalFieldManager screen, boolean editable, String cid, String uid, int wtlsMode, int wtlsClientIdType, String wtlsClientIdValue
   ) {
      WAPServiceRecord record = WAPServiceRecord.getRecord(cid, uid);
      if (record == null) {
         return screen;
      }

      this._screen = screen;
      if (!record.getContainsWtlsClientIdValue()) {
         record.setWtlsClientIdType(wtlsClientIdType);
         record.setWtlsModeValue(wtlsMode);
         record.setWtlsClientIdValue(wtlsClientIdValue);

         label178:
         try {
            record.saveData();
         } finally {
            break label178;
         }
      }

      if (!super._restrictedAccess) {
         screen.add((Field)(new Object("")));
         screen.add((Field)(new Object(BrowserResources.getString(527))));
      }

      this._sessionResumeField = (CheckboxField)(new Object(BrowserResources.getString(535), record.getSessionResume() == 1));
      if (!editable) {
         this._sessionResumeField.setEditable(false);
      }

      if (!super._restrictedAccess) {
         screen.add(this._sessionResumeField);
      }

      this._secureAccessField = (CheckboxField)(new Object(BrowserResources.getString(290), record.getSecureAccess() == 1));
      if (!editable) {
         this._secureAccessField.setEditable(false);
      }

      if (!super._restrictedAccess) {
         screen.add(this._secureAccessField);
         this._secureAccessField.setChangeListener(this);
      }

      if (!super._restrictedAccess) {
         String authUsernameValue = null;
         String authPasswordValue = null;
         if (record.getAuthUsername() != null && record.getAuthPassword() != null) {
            authUsernameValue = record.getAuthUsername();
            authPasswordValue = record.getAuthPassword();
         } else {
            authUsernameValue = "";
            authPasswordValue = "";
         }

         this._authUsernameField = (EditField)(new Object(
            ((StringBuffer)(new Object())).append(BrowserResources.getString(437)).append(' ').toString(), authUsernameValue
         ));
         this._authPasswordField = (PasswordEditField)(new Object(
            ((StringBuffer)(new Object())).append(BrowserResources.getString(438)).append(' ').toString(), authPasswordValue
         ));
         this._authPasswordField.setAllowUnicodeInput(true);
         if (!editable) {
            this._authUsernameField.setEditable(false);
            this._authPasswordField.setEditable(false);
         }

         screen.add((Field)(new Object("")));
         screen.add(this._authUsernameField);
         screen.add(this._authPasswordField);
      }

      this._mmscUrlField = (EditField)(new Object(
         ((StringBuffer)(new Object())).append(BrowserResources.getString(694)).append(' ').toString(), record.getMMSCUrl()
      ));
      if (!editable) {
         this._mmscUrlField.setEditable(false);
      }

      if (!super._restrictedAccess) {
         screen.add(this._mmscUrlField);
      }

      this._wtlsModeField = (ObjectChoiceField)(new Object(BrowserResources.getString(457), BrowserResources.getStringArray(528), record.getWtlsModeValue()));
      if (!editable) {
         this._wtlsModeField.setEditable(false);
      }

      if (!super._restrictedAccess && record.getSecureAccess() == 1) {
         screen.add((Field)(new Object("")));
         screen.add(this._wtlsModeField);
      }

      this._wap20ConformanceField = (CheckboxField)(new Object(BrowserResources.getString(649), record.getWAP20Conformance() == 1));
      if (!editable) {
         this._wap20ConformanceField.setEditable(false);
      }

      if (!super._restrictedAccess && record.getSecureAccess() == 1) {
         screen.add(this._wap20ConformanceField);
      }

      this._wtlsClientIdTypeField = (ObjectChoiceField)(new Object(
         BrowserResources.getString(491), BrowserResources.getStringArray(593), record.getWtlsClientIdType() + 1
      ));
      if (!editable) {
         this._wtlsClientIdTypeField.setEditable(false);
      }

      if (!super._restrictedAccess && record.getSecureAccess() == 1) {
         screen.add(this._wtlsClientIdTypeField);
      }

      this._wtlsClientIdValueField = (EditField)(new Object(BrowserResources.getString(492), record.getWtlsClientIdValue()));
      if (!editable) {
         this._wtlsClientIdValueField.setEditable(false);
      }

      if (!super._restrictedAccess && record.getSecureAccess() == 1) {
         screen.add(this._wtlsClientIdValueField);
         this._valuesAdded = true;
         return screen;
      } else {
         this._valuesAdded = false;
         return screen;
      }
   }

   @Override
   public final void saveProperty(String cid, String uid) {
      WAPServiceRecord record = WAPServiceRecord.getRecord(cid, uid);
      if (record == null) {
         Status.show(BrowserResources.getString(291));
      } else {
         if (this._secureAccessField.getChecked()) {
            record.setSecureAccess(1);
         } else {
            record.setSecureAccess(0);
         }

         if (this._sessionResumeField.getChecked()) {
            record.setSessionResume(1);
         } else {
            record.setSessionResume(0);
         }

         if (this._authUsernameField != null && this._authPasswordField != null) {
            String username = this._authUsernameField.getText();
            String password = this._authPasswordField.getText();
            if (username.length() == 0 && password.length() == 0) {
               record.setAuthUsername(null);
               record.setAuthPassword(null);
            } else {
               record.setAuthUsername(username);
               record.setAuthPassword(password);
            }
         }

         record.setWtlsModeValue(this._wtlsModeField.getSelectedIndex());
         record.setWtlsClientIdType(this._wtlsClientIdTypeField.getSelectedIndex() - 1);
         record.setWtlsClientIdValue(this._wtlsClientIdValueField.getText().trim());
         record.setWAP20Conformance(this._wap20ConformanceField.getChecked() ? 1 : 0);
         record.setMMSCUrl(this._mmscUrlField.getText());

         try {
            if (!record.saveData()) {
               Status.show(BrowserResources.getString(291));
            }
         } finally {
            return;
         }
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._secureAccessField && context != Integer.MIN_VALUE) {
         boolean checked = this._secureAccessField.getChecked();
         if (this._valuesAdded && !checked) {
            this._screen.delete(this._wtlsModeField);
            this._screen.delete(this._wap20ConformanceField);
            this._screen.delete(this._wtlsClientIdTypeField);
            this._screen.delete(this._wtlsClientIdValueField);
            this._valuesAdded = false;
            return;
         }

         if (!this._valuesAdded) {
            this._screen.add(this._wtlsModeField);
            this._screen.add(this._wap20ConformanceField);
            this._screen.add(this._wtlsClientIdTypeField);
            this._screen.add(this._wtlsClientIdValueField);
            this._valuesAdded = true;
         }
      }
   }
}
