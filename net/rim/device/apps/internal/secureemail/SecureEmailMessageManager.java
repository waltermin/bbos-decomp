package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.internal.ui.component.HorizontalSpacerField;
import net.rim.device.internal.ui.component.ImageField;

class SecureEmailMessageManager extends SecureEmailMessageBlockManager {
   private CursorProviderHorizontalFieldManager _shortFormStatusManager;
   private SeparatorField _shortFormSeparator;
   private LabelField _shortTextField;
   private long[] _statusFieldTypes;
   private StatusField[][] _statusFieldsByType;
   private StatusField _highestPriorityStatusField;
   private boolean _showShortForm;
   private VerbMenuItem _showShortFormVerb;
   private VerbMenuItem _showLongFormVerb;
   protected EmailMessageModel _messageModel;
   private static final int HORIZONTAL_SPACER_PIXEL_WIDTH = 3;

   SecureEmailMessageManager(long style, EmailMessageModel messageModel) {
      super(style);
      this._shortFormStatusManager = new CursorProviderHorizontalFieldManager(style);
      this._shortFormSeparator = new SeparatorField();
      this._shortTextField = new LabelField(null, 51539607552L);
      this._statusFieldTypes = new long[0];
      this._statusFieldsByType = new StatusField[0][];
      this._showShortFormVerb = new VerbMenuItem(
         new SecureEmailMessageManager$ShowShortFormVerb(this, SecureEmailResources.getBundle(), 23, true), Integer.MAX_VALUE
      );
      this._showLongFormVerb = new VerbMenuItem(
         new SecureEmailMessageManager$ShowShortFormVerb(this, SecureEmailResources.getBundle(), 22, false), Integer.MAX_VALUE
      );
      this._messageModel = messageModel;
   }

   @Override
   public void showShortForm(boolean showShortForm) {
      if (this._messageModel != null) {
         SecureEmailCache.getInstance().putShowShortForm(this._messageModel, showShortForm);
      }

      if (this._showShortForm != showShortForm) {
         this._shortFormStatusManager.deleteAll();
         if (this._shortFormStatusManager.getManager() != null) {
            this.delete(this._shortFormStatusManager);
            this.delete(this._shortFormSeparator);
         }

         super.showShortForm(showShortForm);
         this._showShortForm = showShortForm;
         this.updateShortFormStatus();
      }
   }

   public void handleStatusUpdate() {
      this.updateShortFormStatus();
      this.invalidateScreen();
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      Field oldFieldWithFocus = this._shortFormStatusManager.getFieldWithFocus();
      int returnValue = super.moveFocus(amount, status, time);
      Field newFieldWithFocus = this._shortFormStatusManager.getFieldWithFocus();
      this.updateShortTextOnFocusMove(oldFieldWithFocus, newFieldWithFocus);
      return returnValue;
   }

   @Override
   protected void onFocus(int direction) {
      super.onFocus(direction);
      Field newFieldWithFocus = this._shortFormStatusManager.getFieldWithFocus();
      this.updateShortTextOnFocusMove(null, newFieldWithFocus);
   }

   @Override
   protected void onUnfocus() {
      Field oldFieldWithFocus = this._shortFormStatusManager.getFieldWithFocus();
      super.onUnfocus();
      this.updateShortTextOnFocusMove(oldFieldWithFocus, null);
   }

   private void updateShortTextOnFocusMove(Field oldFieldWithFocus, Field newFieldWithFocus) {
      if (newFieldWithFocus != null) {
         Object cookie = newFieldWithFocus.getCookie();
         if (cookie != null && cookie instanceof StatusField) {
            this._shortTextField.setText(((StatusField)cookie).getShortText());
            return;
         }
      } else if (oldFieldWithFocus != null) {
         this._shortTextField.setText(this._highestPriorityStatusField.getShortText());
      }
   }

   private void updateShortFormStatus() {
      int numStatusFieldTypes = this._statusFieldTypes.length;
      if (this._showShortForm && numStatusFieldTypes != 0) {
         if (this._shortFormStatusManager.getManager() == null) {
            this.insert(this._shortFormStatusManager, 0);
            this.insert(this._shortFormSeparator, 1);
         }

         Field previousFieldWithFocus = this._shortFormStatusManager.getFieldWithFocus();
         StatusField previousStatusFieldWithFocus = previousFieldWithFocus != null ? (StatusField)previousFieldWithFocus.getCookie() : null;
         Field newFieldWithFocus = null;
         this._shortFormStatusManager.deleteAll();
         this._highestPriorityStatusField = null;

         for (int i = 0; i < numStatusFieldTypes; i++) {
            StatusField highestPriorityStatusFieldByType = null;
            int numStatusFieldsOfCurrentType = this._statusFieldsByType[i].length;

            for (int j = 0; j < numStatusFieldsOfCurrentType; j++) {
               if (highestPriorityStatusFieldByType == null || highestPriorityStatusFieldByType.getPriority() < this._statusFieldsByType[i][j].getPriority()) {
                  highestPriorityStatusFieldByType = this._statusFieldsByType[i][j];
               }
            }

            if (this._highestPriorityStatusField == null || this._highestPriorityStatusField.getPriority() < highestPriorityStatusFieldByType.getPriority()) {
               this._highestPriorityStatusField = highestPriorityStatusFieldByType;
            }

            ImageField highestPriorityImageFieldByType = highestPriorityStatusFieldByType.getImageField();
            highestPriorityImageFieldByType.setCookie(highestPriorityStatusFieldByType);
            this._shortFormStatusManager.add(highestPriorityImageFieldByType);
            if (previousStatusFieldWithFocus != null && previousStatusFieldWithFocus.getStatusType() == highestPriorityStatusFieldByType.getStatusType()) {
               newFieldWithFocus = highestPriorityImageFieldByType;
            }
         }

         this._shortFormStatusManager.add(new HorizontalSpacerField(3));
         this._shortTextField.setText(this._highestPriorityStatusField.getShortText());
         this._shortFormStatusManager.add(this._shortTextField);
         if (newFieldWithFocus != null) {
            newFieldWithFocus.setFocus();
         }
      }
   }

   public void makeDelegateContextMenu(ContextMenu contextMenu) {
      contextMenu.addItem(this._showShortForm ? this._showLongFormVerb : this._showShortFormVerb);
   }

   @Override
   public void add(Field field) {
      if (field instanceof StatusProviderField) {
         StatusProviderField statusProviderField = (StatusProviderField)field;
         statusProviderField.setSecureEmailMessageManager(this);
         statusProviderField.showShortForm(this._showShortForm);
         StatusField[] statusFields = statusProviderField.getStatusFields();
         int numStatusFields = statusFields.length;
         int numStatusFieldTypes = this._statusFieldTypes.length;

         label28:
         for (int i = 0; i < numStatusFields; i++) {
            long statusFieldType = statusFields[i].getStatusType();

            for (int j = 0; j < numStatusFieldTypes; j++) {
               if (statusFieldType == this._statusFieldTypes[j]) {
                  Arrays.add(this._statusFieldsByType[j], statusFields[i]);
                  continue label28;
               }
            }

            StatusField[] newStatusFieldArray = new StatusField[]{statusFields[i]};
            Arrays.add(this._statusFieldsByType, newStatusFieldArray);
            Arrays.add(this._statusFieldTypes, statusFieldType);
            numStatusFieldTypes++;
         }

         this.updateShortFormStatus();
      }

      super.add(field);
   }

   @Override
   public void deleteAll() {
      super.deleteAll();
      this._shortFormStatusManager.deleteAll();
      this._statusFieldTypes = new long[0];
      this._statusFieldsByType = new StatusField[0][];
      this._highestPriorityStatusField = null;
      this.invalidateScreen();
   }

   private void invalidateScreen() {
      Screen screen = this.getScreen();
      if (screen != null) {
         screen.invalidate();
      }
   }
}
