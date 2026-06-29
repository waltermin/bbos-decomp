package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.resource.crypto.CryptoIndicatorImages;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.vm.Array;

final class SelectCertificateDialog extends PopupDialog {
   private Certificate[] _certificates;
   private KeyStore _keyStore;
   private KeyStore _trustedKeyStore;
   private CryptoSystemProperties _cryptoSystemProperties;
   private ButtonField _cancelButton;
   private ButtonField _okButton;
   private DialogFieldManager _dfm;
   private Field[] _fields;
   private ImageField[] _iconImageFields;
   private boolean[] _imagesLoaded;
   private boolean _allowMultiSelect;
   private RadioButtonGroup _radioGroup;
   private int _selectedIndex = -1;
   private int[] _selectedIndexes = new int[0];
   private SelectCertificateDialog$CertificateStatusUpdateThread _certificateStatusUpdateThread;

   public SelectCertificateDialog(
      RichTextField descriptionField,
      Certificate[] certificates,
      KeyStore keyStore,
      KeyStore trustedKeyStore,
      CryptoSystemProperties cryptoSystemProperties,
      String[] names,
      int[] selectedCertificates,
      boolean allowMultiSelect,
      long style
   ) {
      super((Manager)(new Object(281474976710656L)), style);
      if (names != null && certificates != null && names.length == certificates.length) {
         this._dfm = (DialogFieldManager)this.getDelegate();
         this._certificates = certificates;
         this._keyStore = keyStore;
         this._trustedKeyStore = trustedKeyStore;
         this._cryptoSystemProperties = cryptoSystemProperties;
         this._allowMultiSelect = allowMultiSelect;
         this._dfm.setMessage(descriptionField);
         HorizontalFieldManager buttonHfm = (HorizontalFieldManager)(new Object(12884901888L));
         this._okButton = (ButtonField)(new Object(CommonResource.getString(10068)));
         buttonHfm.add(this._okButton);
         this._cancelButton = (ButtonField)(new Object(CommonResource.getString(10044)));
         buttonHfm.add(this._cancelButton);
         this._dfm.addCustomField(buttonHfm);
         this._radioGroup = null;
         if (!allowMultiSelect) {
            this._radioGroup = (RadioButtonGroup)(new Object());
         }

         int numNames = names.length;
         this._fields = new Object[numNames];
         this._iconImageFields = new Object[numNames];
         this._imagesLoaded = new boolean[numNames];

         for (int i = 0; i < numNames; i++) {
            if (this._certificates[i] == null) {
               throw new Object();
            }

            HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(1152921504606846976L));
            ImageField imageField = CryptoIndicatorImages.getImageField(0);
            CheckboxField checkBoxField = null;
            RadioButtonField radioButtonField = null;
            Field field;
            if (allowMultiSelect) {
               checkBoxField = (CheckboxField)(new Object(names[i], false));
               field = checkBoxField;
            } else {
               radioButtonField = (RadioButtonField)(new Object(names[i], this._radioGroup, i == 0));
               field = radioButtonField;
            }

            if (selectedCertificates != null) {
               for (int j = 0; j < selectedCertificates.length; j++) {
                  if (i == selectedCertificates[j]) {
                     if (checkBoxField != null) {
                        checkBoxField.setChecked(true);
                     } else {
                        radioButtonField.setSelected(true);
                     }
                     break;
                  }
               }
            }

            this._fields[i] = field;
            this._iconImageFields[i] = imageField;
            hfm.add(imageField);
            hfm.add(field);
            this._dfm.addCustomField(hfm);
         }

         this.setCancelAllowed(true);
         this._okButton.setFocus();
         this._certificateStatusUpdateThread = new SelectCertificateDialog$CertificateStatusUpdateThread(this, null);
         this._certificateStatusUpdateThread.start();
      } else {
         throw new Object();
      }
   }

   @Override
   protected final void close(int closeReason) {
      super.close(closeReason);
      this._certificateStatusUpdateThread.stopThread();
   }

   public final int getSelectedIndex() {
      return this._selectedIndex;
   }

   public final int[] getSelectedIndexes() {
      return this._selectedIndexes;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         Field f = this.getLeafFieldWithFocus();
         if (f == this._cancelButton) {
            this.close(-1);
            return true;
         } else {
            this.select();
            return true;
         }
      } else {
         if (key == 27 && this.isCancelAllowed()) {
            this.close(-1);
            return true;
         }

         SLKeyLayout layout = Keypad.getLayout();
         char k = Keypad.getUnaltedChar(key);
         char capitalK = CharacterUtilities.toUpperCase(k, 1701707776);
         StringBuffer keysBuffer = layout.getComplementaryChars(capitalK, 1);
         if (keysBuffer != null) {
            int keyBufferLength = keysBuffer.length();

            for (int i = 0; i < keyBufferLength; i++) {
               if (keysBuffer.charAt(i) == 'V' && (status & 1) != 0) {
                  int index = this.fieldWithFocusToIndex();
                  if (index == -1) {
                     return true;
                  }

                  Certificate c = this._certificates[index];
                  if (c != null) {
                     CertificateUtilities.displayCertificateDetails(c, null, this._keyStore, this._cryptoSystemProperties, false, null);
                  }

                  return true;
               }
            }
         }

         key = CharacterUtilities.toLowerCase(key, 1701707776);
         int start = 0;
         Field f = this.getLeafFieldWithFocus();
         int numFields = this._fields.length;

         for (int i = 0; i < numFields; i++) {
            if (this._fields[i] == f && i + 1 < numFields) {
               start = i + 1;
               break;
            }
         }

         for (int j = 0; j < 2; j++) {
            if (j == 1) {
               if (start == 0) {
                  break;
               }

               start = 0;
            }

            for (int i = start; i < this._fields.length; i++) {
               Field var10000 = this._fields[i];
               String label;
               if (!(this._fields[i] instanceof Object)) {
                  label = ((RadioButtonField)this._fields[i]).getLabel();
               } else {
                  label = ((CheckboxField)var10000).getLabel();
               }

               if (CharacterUtilities.toLowerCase(label.charAt(0), 1701707776) == key) {
                  this._fields[i].setFocus();
                  return true;
               }
            }
         }

         return super.keyChar(key, status, time);
      }
   }

   private final int fieldWithFocusToIndex() {
      synchronized (Application.getEventLock()) {
         Field f = this.getLeafFieldWithFocus();
         return !(f instanceof Object) && !(f instanceof Object) ? -1 : f.getManager().getIndex() - 1;
      }
   }

   protected final void select() {
      this._selectedIndexes = new int[0];
      if (this._allowMultiSelect) {
         int index = 0;

         for (int i = 0; i < this._fields.length; i++) {
            CheckboxField field = (CheckboxField)this._fields[i];
            if (field.getChecked()) {
               Array.resize(this._selectedIndexes, this._selectedIndexes.length + 1);
               this._selectedIndexes[index++] = i;
            }
         }
      } else {
         this._selectedIndex = this._radioGroup.getSelectedIndex();
      }

      this.close(0);
   }

   private final boolean selectCurrentChoice() {
      Field f = this.getLeafFieldWithFocus();
      if (f == this._okButton) {
         this.select();
         return true;
      }

      if (f == this._cancelButton) {
         this.close(-1);
         return true;
      }

      if (!(f instanceof Object)) {
         if (!(f instanceof Object)) {
            return false;
         }

         RadioButtonField radioField = (RadioButtonField)f;
         radioField.setSelected(true);
         return true;
      } else {
         CheckboxField checkBox = (CheckboxField)f;
         checkBox.setChecked(!checkBox.getChecked());
         return true;
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return !super.trackwheelClick(status, time) ? this.selectCurrentChoice() : true;
   }
}
