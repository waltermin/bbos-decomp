package net.rim.device.cldc.impl.sb.editor;

import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.HexEditField;

final class SBAppEditRecordUI extends MainScreen implements ListFieldCallback {
   private String[] _encryptModeDescriptions;
   private String[] _compModeDescriptions;
   private boolean _isNew;
   private ServiceBook _sb;
   private ServiceRecord _rec;
   private LabelField _screenTitle;
   private int _viewMode;
   private boolean _displayAllFields;
   private ObjectChoiceField _recTypeField;
   private AutoTextEditField _recNameField;
   private AutoTextEditField _recUidField;
   private AutoTextEditField _recCidField;
   private ListField _recEncryptField;
   private int _encryptModes;
   private int _originalEncryptModes;
   private boolean _encryptModesDirty;
   private ListField _recCompField;
   private int _compModes;
   private boolean _compModesDirty;
   private ObjectChoiceField _recSourceField;
   private AutoTextEditField _recCARealmField;
   private AutoTextEditField _recCAAddressField;
   private EditField _recCAPortField;
   private ObjectChoiceField _recHriUsageField;
   private AutoTextEditField _recActivePinField;
   private AutoTextEditField _recDescriptionField;
   private HexEditField _recAppDataField;
   private AutoTextEditField _recHomeAddressField;
   private EditField _recBBRRoutingField;
   private EditField _recDataSourceIdField;
   private EditField _recUserIdField;
   private EditField _serviceIdTypeField;
   private EditField _serviceIdSubTypeField;
   private CheckboxField _isRestoredFromBackupField;
   private CheckboxField _isRestoreEnabledField;
   private CheckboxField _isRestoreDisabledField;
   public static final int RET_CLOSE = 0;
   public static final int RET_SAVE = 1;
   private static final int HRI_USAGE_DEFAULT_INDEX = 0;
   private static final int HRI_USAGE_SPECIFIED_INDEX = 1;
   private static final int _encryptModesMask = 2;

   public SBAppEditRecordUI(ServiceBook sb, ServiceRecord theRec, boolean iNew) {
      this._sb = sb;
      this._rec = theRec;
      this._isNew = iNew;
      this._viewMode = -1;
      this._screenTitle = new LabelField("", 64);
      this.setTitle(this._screenTitle);
      ResourceBundle rb = SBAppResources.getResourceBundle();
      this._encryptModeDescriptions = rb.getStringArray(333);
      this._compModeDescriptions = rb.getStringArray(334);
      this._recTypeField = this.makeObjectChoiceField(rb, 201, 325);
      this._recNameField = this.makeAutoTextEditField(rb, 202, 1000000);
      this._recUidField = this.makeAutoTextEditField(rb, 203, 127);
      this._recCidField = this.makeAutoTextEditField(rb, 204, 127);
      this._recEncryptField = this.makeListField(this._encryptModeDescriptions.length);
      this._recCompField = this.makeListField(this._compModeDescriptions.length);
      this._recSourceField = this.makeObjectChoiceField(rb, 324, 323);
      this._recCARealmField = this.makeAutoTextEditField(rb, 207, 127);
      this._recCAAddressField = this.makeAutoTextEditField(rb, 208, 127);
      this._recCAPortField = new EditField(rb.getString(209), null, 1000000, 16777216);
      this._recHriUsageField = this.makeObjectChoiceField(rb, 210, 301);
      this._recDescriptionField = this.makeAutoTextEditField(rb, 211, 127);
      this._recAppDataField = new HexEditField(rb.getString(212), 1023);
      this._recHomeAddressField = this.makeAutoTextEditField(rb, 326, 127);
      this._recBBRRoutingField = new EditField(rb.getString(335), "");
      this._recDataSourceIdField = new EditField(rb.getString(328), "");
      this._recUserIdField = new EditField(rb.getString(329), "");
      this._serviceIdTypeField = new EditField(rb.getString(216), "");
      this._serviceIdSubTypeField = new EditField(rb.getString(217), "");
      this._isRestoredFromBackupField = new CheckboxField(rb.getString(220), theRec.isRestoredFromBackup());
      this._isRestoreEnabledField = new CheckboxField(rb.getString(219), theRec.isRestoreEnabled());
      this._isRestoreDisabledField = new CheckboxField(rb.getString(218), theRec.isRestoreDisabled());
      int id;
      switch (RadioInfo.getNetworkType()) {
         case 2:
            id = 327;
            break;
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         default:
            id = 215;
      }

      this._recActivePinField = this.makeAutoTextEditField(rb, id, 1000000);
      this.initDisplay();
      this.load();
   }

   public final void go(int viewMode, boolean showExtras) {
      this.setViewMode(viewMode);
      if (showExtras) {
         this.addExtraFields();
      }

      UiApplication.getUiApplication().pushScreen(this);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      ResourceBundle rb = SBAppResources.getResourceBundle();
      menu.add(new SBAppEditRecordUI$MyMenuItem(this, rb, 104));
      menu.addSeparator();
      if (this.isDirty()) {
         menu.add(new SBAppEditRecordUI$MyMenuItem(this, CommonResource.getBundle(), 18));
         menu.addSeparator();
      } else if (!this._isNew && this._rec.getType() == 1) {
         menu.add(new SBAppEditRecordUI$MyMenuItem(this, rb, 101));
         menu.add(new SBAppEditRecordUI$MyMenuItem(this, rb, 102));
         menu.addSeparator();
      }

      super.makeMenu(menu, instance);
   }

   @Override
   protected final boolean keyCharUnhandled(char key, int status, int time) {
      if (key == ' ') {
         Field f = this.getLeafFieldWithFocus();
         if (f instanceof ListField) {
            ListField lf = (ListField)f;
            int index = lf.getSelectedIndex();
            int mask = 1 << lf.getSelectedIndex();
            if (f == this._recEncryptField) {
               if (this._isNew || (this._originalEncryptModes & 2) == 0) {
                  this._encryptModes ^= mask;
                  this._encryptModesDirty = true;
               }
            } else {
               this._compModes ^= mask;
               this._compModesDirty = true;
            }

            lf.invalidate(index);
            return true;
         }
      }

      return super.keyCharUnhandled(key, status, time);
   }

   @Override
   protected final boolean openDevelopmentBackdoor(int backdoorCode) {
      boolean keyUsed = false;
      if (backdoorCode == 1396852047) {
         keyUsed = true;
         if (this._viewMode == 1) {
            this.setViewMode(0);
         } else if (this._viewMode == 2 && !this._displayAllFields) {
            this.addExtraFields();
         } else {
            keyUsed = false;
         }
      }

      return keyUsed ? keyUsed : super.openDevelopmentBackdoor(backdoorCode);
   }

   @Override
   public final boolean isDataValid() {
      int id = -1;
      if (this._recNameField.getTextLength() != 0 && this._recUidField.getTextLength() != 0 && this._recCidField.getTextLength() != 0) {
         if (this._recTypeField.getSelectedIndex() == 4) {
            id = 11;
         } else if (this._encryptModes == 0 || this._compModes == 0) {
            id = 17;
         }
      } else {
         id = 12;
      }

      if (id != -1) {
         Dialog.alert(SBAppResources.getString(id));
         return false;
      }

      if (this._recTypeField.getSelectedIndex() == 5) {
         id = 318;
      } else if (this._recTypeField.getSelectedIndex() == 6) {
         id = 319;
      } else if (this._recTypeField.getSelectedIndex() == 0) {
         id = 320;
      }

      if (id != -1) {
         Dialog.inform(SBAppResources.getString(id));
      }

      return true;
   }

   @Override
   public final void save() {
      this._rec.setType(this._recTypeField.getSelectedIndex());
      this._rec.setName(this._recNameField.getText());
      this._rec.setUid(this._recUidField.getText());
      this._rec.setCid(this._recCidField.getText());
      this._rec.setEncryptionMode(this._encryptModes);
      this._rec.setCompressionMode(this._compModes);
      this._rec.setSource(this._recSourceField.getSelectedIndex());
      this._rec.setDescription(this._recDescriptionField.getText());
      if (this._recHriUsageField.getSelectedIndex() == 0 && this._rec.getAttachedHrt() != null) {
         this._rec.setAttachedHrt(null);
      }

      label128:
      try {
         if (this._recBBRRoutingField.getText().length() > 0) {
            StringTokenizer strtok = new StringTokenizer(this._recBBRRoutingField.getText(), "//:;", false);
            int count = strtok.countTokens() / 2;
            String[] hosts = new String[count];
            int[] ports = new int[count];

            for (int i = 0; i < count; i++) {
               hosts[i] = strtok.nextToken();
               ports[i] = Integer.parseInt(strtok.nextToken()) & 65535;
            }

            this._rec.setBBRHosts(hosts);
            this._rec.setBBRPorts(ports);
         }
      } finally {
         break label128;
      }

      this._rec.setCARealm(this._recCARealmField.getText());
      this._rec.setCAAddress(this._recCAAddressField.getText());
      this._rec.setCAPort((short)this.parseInt(this._recCAPortField.getText()));
      this._rec.setApplicationData(this._recAppDataField.getAsBytes());
      this._rec.setHomeAddress(this._recHomeAddressField.getText());
      this._rec.setDataSourceId(this._recDataSourceIdField.getText());

      label117:
      try {
         this._rec.setUserId(Integer.parseInt(this._recUserIdField.getText()));
      } finally {
         break label117;
      }

      label114:
      try {
         this._rec.setServiceIdentifierType(Short.parseShort(this._serviceIdTypeField.getText()));
         this._rec.setServiceIdentifierSubType(Short.parseShort(this._serviceIdSubTypeField.getText()));
      } finally {
         break label114;
      }

      this._rec.setRestoredFromBackup(this._isRestoredFromBackupField.getChecked());
      this._rec.setRestoreDisabled(this._isRestoreDisabledField.getChecked());
      this._rec.setRestoreEnabled(this._isRestoreEnabledField.getChecked());
      if (this._isNew) {
         this._sb.addRecord(this._rec);
      } else {
         this._sb.commit();
      }
   }

   @Override
   public final boolean isDirty() {
      return super.isDirty() || this._encryptModesDirty || this._compModesDirty;
   }

   private final void initDisplay() {
      this.add(this._recTypeField);
      this.add(this._recNameField);
      this.add(this._recUidField);
      this.add(this._recCidField);
      this.add(new SeparatorField());
      this.add(this._recUserIdField);
      this.add(this._recDataSourceIdField);
      this.add(new SeparatorField());
      this.add(this._recActivePinField);
      this.add(this._recDescriptionField);
      if (this._displayAllFields) {
         this.addExtraFields();
      }
   }

   private final void addExtraFields() {
      ResourceBundle rb = SBAppResources.getResourceBundle();
      if (!this._displayAllFields) {
         this.delete(this._recActivePinField);
         this.add(new LabelField(rb.getString(205)));
         this.add(this._recEncryptField);
         this.add(new LabelField(rb.getString(206)));
         this.add(this._recCompField);
         this.add(this._recSourceField);
         this.add(this._recHriUsageField);
         this.add(this._recBBRRoutingField);
         this.add(new SeparatorField());
         this.add(this._recCARealmField);
         this.add(this._recCAAddressField);
         this.add(this._recCAPortField);
         this.add(new SeparatorField());
         this.add(this._recAppDataField);
         this.add(new SeparatorField());
         this.add(this._recHomeAddressField);
         this._displayAllFields = true;
      }
   }

   private final void setViewMode(int viewMode) {
      if (viewMode != this._viewMode) {
         this._viewMode = viewMode;
         boolean mode;
         if (this._viewMode == 0) {
            mode = true;
            this.addExtraFields();
         } else {
            mode = false;
         }

         for (int i = this.getFieldCount() - 1; i >= 0; i--) {
            this.getField(i).setEditable(mode);
         }

         int id;
         if (this._isNew) {
            id = 2;
         } else if (viewMode == 0) {
            id = 3;
         } else {
            id = 4;
         }

         this._screenTitle.setText(SBAppResources.getString(id));
      }
   }

   private final void load() {
      this._recTypeField.setSelectedIndex(this._rec.getType());
      this.setFieldText(this._recNameField, this._rec.getName());
      this.setFieldText(this._recUidField, this._rec.getUid());
      this.setFieldText(this._recCidField, this._rec.getCid());
      this._encryptModes = this._rec.getEncryptionMode();
      this._originalEncryptModes = this._encryptModes;
      this._encryptModesDirty = false;
      this._recEncryptField.invalidate();
      this._compModes = this._rec.getCompressionMode();
      this._compModesDirty = false;
      this._recCompField.invalidate();
      this._recSourceField.setSelectedIndex(this._rec.getSource());
      this._recHriUsageField.setSelectedIndex(this._rec.getAttachedHrt() != null ? 1 : 0);
      if (this._rec.getBBRHosts() != null && this._rec.getBBRPorts() != null) {
         String[] hosts = this._rec.getBBRHosts();
         int[] ports = this._rec.getBBRPorts();
         StringBuffer buf = new StringBuffer(64);

         for (int i = 0; i < hosts.length; i++) {
            buf.append('/');
            buf.append('/');
            buf.append(hosts[i]);
            buf.append(':');
            buf.append(ports[i]);
            buf.append(';');
         }

         this._recBBRRoutingField.setText(buf.toString());
      }

      String s;
      if (this._rec.isDisabled()) {
         s = SBAppResources.getString(13);
      } else {
         HostRoutingTable hrt = this._rec.getAttachedHrt();
         if (hrt == null) {
            hrt = HRUtils.getDefaultHRT();
         }

         s = hrt.getActiveHri().getAddressBase().getAddress();
      }

      this._recActivePinField.setText(s);
      this.setFieldText(this._recDescriptionField, this._rec.getDescription());
      this.setFieldText(this._recCARealmField, this._rec.getCARealm());
      this.setFieldText(this._recCAAddressField, this._rec.getCAAddress());
      this._recCAPortField.setText(Integer.toString(this._rec.getCAPort()));
      byte[] b = this._rec.getApplicationData();
      if (b != null) {
         this._recAppDataField.setData(b, 0, b.length);
      }

      this.setFieldText(this._recHomeAddressField, this._rec.getHomeAddress());
      String dataSourceId = this._rec.getDataSourceId();
      if (dataSourceId == null) {
         dataSourceId = "";
      }

      this._recDataSourceIdField.setText(dataSourceId);
      this._recUserIdField.setText(Integer.toString(this._rec.getUserId()));
   }

   private final void setFieldText(AutoTextEditField atef, String text) {
      if (text != null) {
         atef.setText(text);
      }
   }

   private final int parseInt(String str) {
      if (str == null || str.length() == 0) {
         return 0;
      } else if (str.startsWith("0x")) {
         return Integer.parseInt(str.substring(2), 16);
      } else {
         return str.startsWith("0b") ? Integer.parseInt(str.substring(2), 2) : Integer.parseInt(str, 10);
      }
   }

   private final AutoTextEditField makeAutoTextEditField(ResourceBundle rb, int id, int maxSize) {
      return new AutoTextEditField(rb.getString(id), null, maxSize, 2199023255552L);
   }

   private final ObjectChoiceField makeObjectChoiceField(ResourceBundle rb, int labelId, int stringsId) {
      return new ObjectChoiceField(rb.getString(labelId), rb.getStringArray(stringsId));
   }

   private final ListField makeListField(int size) {
      ListField lf = new ListField(size);
      lf.setCallback(this);
      return lf;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      int value;
      String[] descriptions;
      if (listField == this._recEncryptField) {
         value = this._encryptModes;
         descriptions = this._encryptModeDescriptions;
      } else {
         value = this._compModes;
         descriptions = this._compModeDescriptions;
      }

      char ch = (char)((value & 1 << index) == 0 ? 9744 : 9745);
      int x = 5;
      x += graphics.drawText(ch, x, y, 0, -1);
      graphics.drawText(descriptions[index], x + 2, y);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return this.getWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      int value;
      if (listField == this._recEncryptField) {
         value = this._encryptModes;
      } else {
         value = this._compModes;
      }

      value &= 1 << index;
      return new Boolean(value != 0);
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   static final boolean access$300(SBAppEditRecordUI x0) {
      return x0.onSave();
   }
}
