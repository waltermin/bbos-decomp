package net.rim.device.apps.internal.browser.ui;

import com.sun.cldc.i18n.Helper;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class DialogSelectCharset extends PopupScreen implements FieldChangeListener {
   private ObjectChoiceField _charsetField;
   private CheckboxField _autoField;
   private ButtonField _buttonOk;
   private ButtonField _buttonCancel;
   private boolean _cancelled;
   private String[] _supportedEncodings;
   private static final String[] MDS_SUPPORTED_ENCODINGS = new String[]{
      "Big5",
      "Big5-HKSCS",
      "EUC-JP",
      "EUC-KR",
      "GB18030",
      "GB2312",
      "GBK",
      "ISO-2022-CN",
      "ISO-2022-JP",
      "ISO-2022-KR",
      "ISO-8859-1",
      "ISO-8859-2",
      "ISO-8859-3",
      "ISO-8859-4",
      "ISO-8859-5",
      "ISO-8859-6",
      "ISO-8859-7",
      "ISO-8859-8",
      "ISO-8859-9",
      "ISO-8859-13",
      "ISO-8859-15",
      "KOI8-R",
      "Shift_JIS",
      "TIS-620",
      "US-ASCII",
      "UTF-16",
      "UTF-16BE",
      "UTF-16LE",
      "UTF-8",
      "windows-1250",
      "windows-1251",
      "windows-1252",
      "windows-1253",
      "windows-1254",
      "windows-1255",
      "windows-1256",
      "windows-1257",
      "windows-1258"
   };

   public final boolean promptForCharset() {
      this._charsetField.setFocus();
      UiApplication.getUiApplication().pushModalScreen(this);
      int index = this._charsetField.getSelectedIndex();
      String typedData = null;
      if (index >= 0 && index < this._supportedEncodings.length) {
         typedData = this._supportedEncodings[index];
      }

      if (this._cancelled) {
         return false;
      }

      if (typedData != null && typedData.length() == 0) {
         typedData = null;
      }

      GeneralProperty.setCurrentProperty(14, this._autoField.getChecked() ? 0 : 1);
      GeneralProperty.setDefaultCharsetValue(typedData);
      GeneralProperty.notifyListeners();
      return true;
   }

   public final void handleSelection() {
      Field fieldWithFocus = this.getLeafFieldWithFocus();
      if (fieldWithFocus == this._buttonCancel) {
         this._cancelled = true;
      }

      UiApplication.getUiApplication().popScreen(this);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field instanceof ButtonField) {
         this.handleSelection();
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      this._cancelled = false;
      if (super.keyChar(key, status, time)) {
         return true;
      } else if (key == 27) {
         this._cancelled = true;
         UiApplication.getUiApplication().popScreen(this);
         return true;
      } else if (key == '\n') {
         this.handleSelection();
         return true;
      } else {
         return false;
      }
   }

   public DialogSelectCharset() {
      super(new DialogFieldManager(), 0);
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      dfm.setMessage(new RichTextField(BrowserResources.getString(618), 36028797018963968L));
      String defaultValue = GeneralProperty.getDefaultCharsetValue();
      BrowserSession session = BrowserSession.getCurrentSession();
      if (session != null && session.getConfig() != null) {
         int configType = session.getConfig().getPropertyAsInt(12);
         if (configType == 1 || configType == 4 || configType == 8) {
            this._supportedEncodings = MDS_SUPPORTED_ENCODINGS;
         }
      }

      if (this._supportedEncodings == null) {
         this._supportedEncodings = Helper.getSupportedEncodings();
         if (this._supportedEncodings == null) {
            this._supportedEncodings = MDS_SUPPORTED_ENCODINGS;
         }
      }

      int count = this._supportedEncodings.length;
      int index = 0;
      if (defaultValue != null) {
         for (int i = 0; i < count; i++) {
            if (StringUtilities.strEqualIgnoreCase(this._supportedEncodings[i], defaultValue, 1701707776)) {
               index = i;
               break;
            }
         }
      }

      this._autoField = new BrowserCheckboxField(BrowserResources.getString(619), GeneralProperty.getDefaultCharsetModeValue() == 0);
      dfm.addCustomField(this._autoField);
      this._charsetField = new ObjectChoiceField(BrowserResources.getString(620), this._supportedEncodings, index);
      dfm.addCustomField(this._charsetField);
      HorizontalFieldManager hfm = new HorizontalFieldManager(12884901888L);
      this._buttonOk = new ButtonField(CommonResources.getString(117));
      this._buttonOk.setChangeListener(this);
      hfm.add(this._buttonOk);
      this._buttonCancel = new ButtonField(CommonResources.getString(9042));
      this._buttonCancel.setChangeListener(this);
      hfm.add(this._buttonCancel);
      dfm.addCustomField(hfm);
   }
}
