package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.NullField;

public class CheckListWizardPage extends ListWizardPage {
   private CheckboxField[] _checkboxFields = new CheckboxField[0];
   private String[] _listItemStrings = new String[0];
   private Manager _listContainer = null;
   private boolean _useSmallListFont;
   private boolean _noAutoFocus;
   private boolean _floatDisabledToTop;

   public CheckListWizardPage(String title, int priority, WizardCategory category, int wizardFlags) {
      super(title, priority, category, wizardFlags);
   }

   public CheckListWizardPage(ResourceBundle rb, int rbTitleId, int priority, WizardCategory category, int wizardFlags) {
      super(rb, rbTitleId, priority, category, wizardFlags);
   }

   public CheckListWizardPage(ResourceBundle rb, int rbTitleId, int priority, WizardCategory category) {
      this(rb, rbTitleId, priority, category, 16);
   }

   @Override
   public void useSmallListFont(boolean val) {
      this._useSmallListFont = val;
   }

   @Override
   public void setSelectedIndex(int index) {
   }

   public boolean getAllItemsSelected() {
      CheckboxField checkBox = null;

      for (int i = 0; i <= this._listContainer.getFieldCount() - 1; i++) {
         Field listField = this._listContainer.getField(i);
         if (listField instanceof CheckboxField) {
            checkBox = (CheckboxField)this._listContainer.getField(i);
            if (!checkBox.getChecked()) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean[] getItemSelection() {
      boolean[] fieldValues = new boolean[this._listContainer.getFieldCount()];

      for (int i = 0; i <= this._listContainer.getFieldCount() - 1; i++) {
         Field listField = this._listContainer.getField(i);
         if (listField instanceof CheckboxField) {
            CheckboxField checkBox = (CheckboxField)this._listContainer.getField(i);
            fieldValues[i] = checkBox.getChecked();
         }
      }

      return fieldValues;
   }

   public CheckboxField[] getCheckboxFields() {
      return this._checkboxFields;
   }

   public void setFloatDisabledItemsToTop(boolean value) {
      this._floatDisabledToTop = value;
   }

   @Override
   protected void setListItems(String[] items) {
      this._listItemStrings = items;
   }

   protected void setListItems(CheckboxField[] items) {
      this._checkboxFields = items;
   }

   @Override
   protected void initFlags(int wizardFlags) {
      super.initFlags(wizardFlags);
      this._noAutoFocus = (wizardFlags & 524288) != 0;
   }

   @Override
   protected void populateList(Manager content) {
      this._listContainer = content;
      this._listContainer.setBorder(4, 8, 4, 8);
      if (this._useSmallListFont) {
         this._listContainer.setFont(this.getHeaderFont());
      }

      if (this._noAutoFocus) {
         this._listContainer.add(new NullField());
      }

      if (this._listItemStrings != null && this._listItemStrings.length > 0) {
         for (int i = 0; i < this._listItemStrings.length; i++) {
            if (this._listItemStrings[i] != null) {
               CheckboxField checkBox = new CheckboxField(this._listItemStrings[i], false);
               this._listContainer.add(checkBox);
            }
         }
      } else if (this._checkboxFields != null) {
         for (int i = 0; i < this._checkboxFields.length; i++) {
            if (this._checkboxFields[i] != null) {
               if (!this._checkboxFields[i].isEditable() && this._floatDisabledToTop) {
                  this._listContainer.insert(this._checkboxFields[i], 0);
               } else {
                  this._listContainer.add(this._checkboxFields[i]);
               }
            }
         }
      }

      this._listContainer.setFocusListener(this);
   }
}
