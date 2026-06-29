package net.rim.device.internal.ui.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.internal.ui.Image;

public class SimpleInputDialog extends PopupDialog {
   private BasicEditField _editField;
   private String _initialText;
   private RichTextField _promptField;
   private int _minLength;
   private int _maxLength;
   private DialogFieldManager _dfm;
   private byte _isUnicodeInputAllowed = 0;
   public static final int NORMAL_INPUT;
   public static final int LOWER_CASE_INPUT;
   public static final int UPPER_CASE_INPUT;
   public static final int NUMERIC_INPUT;
   public static final int HEX_NUMERIC_INPUT;
   public static final int PASSWORD_INPUT;
   public static final int NUMERIC_PASSWORD_INPUT;
   public static final int PHONE_NUMBER_INPUT;
   public static final int EMAIL_ADDRESS_INPUT;
   public static final int URL_INPUT;
   public static final int NORMAL_INPUT_NO_COMPLEX;
   public static final int AUTOTEXT_INPUT;
   private static final byte UNICODE_INPUT_UNSET;
   private static final byte UNICODE_INPUT_ALLOWED;
   private static final byte UNICODE_INPUT_FORBIDDEN;

   public SimpleInputDialog(int type, String prompt) {
      this(type, prompt, 0, 1000000, 0);
   }

   public SimpleInputDialog(int type, String prompt, int minLength, int maxLength, long style) {
      super(new DialogFieldManager(), style);
      this._minLength = minLength;
      this._maxLength = maxLength;
      this._promptField = new RichTextField(prompt, 36028797018963968L);
      this._initialText = "";
      this._dfm = (DialogFieldManager)this.getDelegate();
      this._dfm.setMessage(this._promptField);
      this.setType(type);
   }

   public void setIcon(Image image) {
      ImageField field = null;
      if (image != null) {
         field = new ImageField();
         field.setImage(image);
      }

      this._dfm.setIcon(field);
   }

   public BasicEditField getEditField() {
      return this._editField;
   }

   public void setEditField(BasicEditField field) {
      this._editField = field;
      if (this._editField != null && this._isUnicodeInputAllowed != 0) {
         this._editField.setAllowUnicodeInput(this._isUnicodeInputAllowed == 1);
      }
   }

   public void setPrompt(String prompt) {
      this._promptField.setText(prompt);
   }

   public void setType(int type) {
      if (this._editField != null) {
         this._dfm.deleteCustomField(this._editField);
      }

      switch (type) {
         case 0:
            this._editField = new EditField(null, null, this._maxLength, 4503601774854144L);
            break;
         case 1:
         default:
            this._editField = new EditField(null, null, this._maxLength, 2197815296L);
            break;
         case 2:
            this._editField = new EditField(null, null, this._maxLength, 2181038080L);
            break;
         case 3:
            this._editField = new EditField(null, null, this._maxLength, 2164260864L);
            break;
         case 4:
            this._editField = new EditField(null, null, this._maxLength, 2214592512L);
            break;
         case 5:
            this._editField = new PasswordEditField(null, null, this._maxLength, 0);
            break;
         case 6:
            this._editField = new PasswordEditField(null, null, this._maxLength, 0);
            this._editField.setFilter(TextFilter.get(1));
            break;
         case 7:
            this._editField = new EditField(null, null, this._maxLength, 100663296);
            break;
         case 8:
            this._editField = new EmailAddressEditField(null, null, this._maxLength);
            break;
         case 9:
            this._editField = new EditField(null, null, this._maxLength, 117440512);
            break;
         case 10:
            this._editField = new EditField(null, null, this._maxLength, 4503602848595968L);
            break;
         case 11:
            this._editField = new AutoTextEditField(null, null, this._maxLength, 4503601774854144L);
      }

      if (this._isUnicodeInputAllowed != 0) {
         this._editField.setAllowUnicodeInput(this._isUnicodeInputAllowed == 1);
      }

      this._dfm.addCustomField(this._editField);
   }

   @Override
   public void add(Field field) {
      this._dfm.addCustomField(field);
   }

   public String getText() {
      return this._editField.getText();
   }

   public void setText(String text) {
      if (text == null) {
         text = "";
      }

      if (text.length() > this._maxLength) {
         throw new IllegalArgumentException("The initial text is too long");
      }

      this._initialText = text;
   }

   public void show(String prompt) {
      this.setPrompt(prompt);
      this.show();
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this._editField.setText(this._initialText);
         this._editField.setCursorPosition(this._initialText.length());
      }
   }

   protected boolean cancel() {
      if (!this.isCancelAllowed()) {
         return false;
      }

      this._editField.setText("");
      if (this.isGlobal() && !this.getApplication().isForeground()) {
         this.doPaint();
      }

      this.close(-1);
      return true;
   }

   protected boolean accept() {
      if (this._editField.getText().length() < this._minLength) {
         return false;
      }

      this.close(0);
      return true;
   }

   public void setMinLength(int minLength) {
      this._minLength = minLength;
   }

   public int getMinLength() {
      return this._minLength;
   }

   public void setMaxLength(int maxLength) {
      this._maxLength = maxLength;
      this._editField.setMaxSize(this._maxLength);
   }

   public int getMaxLength() {
      return this._maxLength;
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      return super.navigationClick(status, time) ? true : this.accept();
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         return this.cancel();
      } else {
         return key == '\n' ? this.accept() : super.keyChar(key, status, time);
      }
   }

   public void setAllowUnicodeInput(boolean allow) {
      this._isUnicodeInputAllowed = (byte)(allow ? 1 : 2);
      if (this._editField != null) {
         this._editField.setAllowUnicodeInput(allow);
      }
   }

   public boolean isUnicodeInputAllowed() {
      return this._editField != null ? this._editField.isUnicodeInputAllowed() : this._isUnicodeInputAllowed != 2;
   }
}
