package net.rim.device.internal.firewall;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.internal.ui.component.PopupDialog;

public final class FirewallDialog extends PopupDialog {
   private DialogFieldManager _dfm;
   private RichTextField _queryField;
   private CheckboxField _checkBoxProtocol;
   private CheckboxField _checkBoxTarget;
   private ButtonField _allowButton;
   private ButtonField _denyButton;
   private String[] _details;
   private boolean _serverConnection;
   private int _returnValue;
   private String _queryString;
   private ResourceBundle _rb = ResourceBundle.getBundle(3100685609005034344L, "net.rim.device.internal.resource.Firewall");

   public FirewallDialog(String[] details, boolean serverConnection) {
      this(details, serverConnection, null);
   }

   public FirewallDialog(String[] details, boolean serverConnection, String queryString) {
      super(new DialogFieldManager(), 33554432);
      this.setStatusPriority(-2147483643);
      this._queryString = queryString;
      this._details = details;
      this._serverConnection = serverConnection;
      this.populateFields();
   }

   private final void populateFields() {
      this._dfm = (DialogFieldManager)this.getDelegate();
      String query = null;
      if (this._queryString != null) {
         query = MessageFormat.format(this._queryString, this._details);
      } else if (this._serverConnection) {
         query = MessageFormat.format(this._rb.getString(13), this._details);
      } else {
         query = MessageFormat.format(this._rb.getString(0), this._details);
      }

      this._queryField = new RichTextField(query, 45035996273704960L);
      this._dfm.addCustomField(this._queryField);
      this._dfm.addCustomField(new LabelField());
      this._dfm.addCustomField(new LabelField(this._rb.getString(7)));
      this._checkBoxProtocol = this.insertCheckboxField(8, false);
      if (!this._serverConnection) {
         this._checkBoxTarget = this.insertCheckboxField(9, true);
      }

      this._dfm.addCustomField(new LabelField());
      this._allowButton = this.insertButtonField(3);
      this._denyButton = this.insertButtonField(6);
   }

   private final CheckboxField insertCheckboxField(int resourceID, boolean checked) {
      CheckboxField field = new CheckboxField(MessageFormat.format(this._rb.getString(resourceID), this._details), checked);
      this._dfm.addCustomField(field);
      return field;
   }

   private final ButtonField insertButtonField(int resourceID) {
      ButtonField field = new ButtonField(MessageFormat.format(this._rb.getString(resourceID), this._details), 12884901888L);
      this._dfm.addCustomField(field);
      return field;
   }

   public final boolean isProtocolChecked() {
      return this._checkBoxProtocol.getChecked();
   }

   public final boolean isTargetChecked() {
      return this._checkBoxTarget != null ? this._checkBoxTarget.getChecked() : false;
   }

   public final int getSelectedValue() {
      return this._returnValue;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return this.doAction() ? true : super.trackwheelClick(status, time);
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      return c == '\n' && this.doAction() ? true : super.keyChar(c, status, time);
   }

   private final boolean doAction() {
      Field field = this._dfm.getLeafFieldWithFocus();
      if (!(field instanceof CheckboxField)) {
         if (field == this._allowButton) {
            this._returnValue = 2;
            this.close(0);
            return true;
         } else if (field == this._denyButton) {
            this._returnValue = 6;
            this.close(0);
            return true;
         } else {
            return false;
         }
      } else {
         CheckboxField checkBoxField = (CheckboxField)field;
         checkBoxField.setChecked(!checkBoxField.getChecked());
         return true;
      }
   }
}
