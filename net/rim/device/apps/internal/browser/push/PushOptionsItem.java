package net.rim.device.apps.internal.browser.push;

import net.rim.device.api.browser.push.PushOptions;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.utility.framework.VerbToMenu;

final class PushOptionsItem extends SaveableMainScreenOptionsListItem implements FieldChangeListener {
   private CheckboxField _enablePushField;
   private CheckboxField _wapEnablePushField;
   private CheckboxField _mdsEnablePushField;
   private LabelField _siLabelField;
   private LabelField _slLabelField;
   private LabelField _otherLabelField;
   private ObjectChoiceField _acceptSLMdsChoiceField;
   private ObjectChoiceField _filterSLMdsChoiceField;
   private ObjectChoiceField _acceptSLSmsChoiceField;
   private ObjectChoiceField _filterSLSmsChoiceField;
   private EditField _filterSLSmsValueField;
   private ObjectChoiceField _acceptSLIpChoiceField;
   private ObjectChoiceField _filterSLIpChoiceField;
   private EditField _filterSLIpValueField;
   private ObjectChoiceField _acceptSIMdsChoiceField;
   private ObjectChoiceField _filterSIMdsChoiceField;
   private ObjectChoiceField _acceptSISmsChoiceField;
   private ObjectChoiceField _filterSISmsChoiceField;
   private EditField _filterSISmsValueField;
   private ObjectChoiceField _acceptSIIpChoiceField;
   private ObjectChoiceField _filterSIIpChoiceField;
   private EditField _filterSIIpValueField;
   private CheckboxField _allowOtherApplicationsField;
   private ObjectChoiceField _acceptOtherMdsChoiceField;
   private ObjectChoiceField _filterOtherMdsChoiceField;
   private ObjectChoiceField _acceptOtherSmsChoiceField;
   private ObjectChoiceField _filterOtherSmsChoiceField;
   private EditField _filterOtherSmsValueField;
   private ObjectChoiceField _acceptOtherIpChoiceField;
   private ObjectChoiceField _filterOtherIpChoiceField;
   private EditField _filterOtherIpValueField;
   private MainScreen _mainScreen;
   private boolean _fieldsAdded;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(-918730770534202080L, "net.rim.device.apps.internal.resource.BrowserPush");

   public PushOptionsItem() {
      super(_rb.getString(1), -1514481539159318190L);
      ContextObject.put(super._context, 244, new Object(27786));
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this._mainScreen = mainScreen;
      this._fieldsAdded = false;
      PushOptions options = PushOptions.getOptions();
      boolean mainPushEnabled = options.getEnablePush();
      this._enablePushField = (CheckboxField)(new Object(_rb.getString(20), mainPushEnabled));
      this._enablePushField.setChangeListener(this);
      mainScreen.add(this._enablePushField);
      boolean mdsEnabled = options.getMDSEnablePush();
      boolean wapEnabled = options.getWAPEnablePush();
      this._mdsEnablePushField = (CheckboxField)(new Object(_rb.getString(19), mdsEnabled));
      this._mdsEnablePushField.setChangeListener(this);
      this._wapEnablePushField = (CheckboxField)(new Object(_rb.getString(2), wapEnabled));
      this._wapEnablePushField.setChangeListener(this);
      this._siLabelField = (LabelField)(new Object(_rb.getString(33)));
      this._slLabelField = (LabelField)(new Object(_rb.getString(34)));
      this._otherLabelField = (LabelField)(new Object(_rb.getString(35)));
      this._allowOtherApplicationsField = (CheckboxField)(new Object(_rb.getString(5), options.getAllowOtherApplications()));
      this._acceptSLMdsChoiceField = (ObjectChoiceField)(new Object(_rb.getString(36), _rb.getStringArray(7), options.getAcceptMode(0, 2)));
      this._acceptSLMdsChoiceField.setChangeListener(this);
      int filterMode = options.getFilterMode(0, 2);
      if (filterMode == 2) {
         filterMode--;
      }

      this._filterSLMdsChoiceField = (ObjectChoiceField)(new Object(_rb.getString(22), _rb.getStringArray(26), filterMode));
      this._acceptSIMdsChoiceField = (ObjectChoiceField)(new Object(_rb.getString(36), _rb.getStringArray(7), options.getAcceptMode(1, 2)));
      this._acceptSIMdsChoiceField.setChangeListener(this);
      filterMode = options.getFilterMode(1, 2);
      if (filterMode == 2) {
         filterMode--;
      }

      this._filterSIMdsChoiceField = (ObjectChoiceField)(new Object(_rb.getString(22), _rb.getStringArray(26), filterMode));
      this._acceptOtherMdsChoiceField = (ObjectChoiceField)(new Object(_rb.getString(36), _rb.getStringArray(7), options.getAcceptMode(2, 2)));
      this._acceptOtherMdsChoiceField.setChangeListener(this);
      filterMode = options.getFilterMode(2, 2);
      if (filterMode == 2) {
         filterMode--;
      }

      this._filterOtherMdsChoiceField = (ObjectChoiceField)(new Object(_rb.getString(22), _rb.getStringArray(26), filterMode));
      this._acceptSLSmsChoiceField = (ObjectChoiceField)(new Object(_rb.getString(37), _rb.getStringArray(7), options.getAcceptMode(0, 1)));
      this._acceptSLSmsChoiceField.setChangeListener(this);
      this._filterSLSmsChoiceField = (ObjectChoiceField)(new Object(_rb.getString(23), _rb.getStringArray(21), options.getFilterMode(0, 1)));
      this._filterSLSmsChoiceField.setChangeListener(this);
      this._acceptSISmsChoiceField = (ObjectChoiceField)(new Object(_rb.getString(37), _rb.getStringArray(7), options.getAcceptMode(1, 1)));
      this._acceptSISmsChoiceField.setChangeListener(this);
      this._filterSISmsChoiceField = (ObjectChoiceField)(new Object(_rb.getString(23), _rb.getStringArray(21), options.getFilterMode(1, 1)));
      this._filterSISmsChoiceField.setChangeListener(this);
      this._acceptOtherSmsChoiceField = (ObjectChoiceField)(new Object(_rb.getString(37), _rb.getStringArray(7), options.getAcceptMode(2, 1)));
      this._acceptOtherSmsChoiceField.setChangeListener(this);
      this._filterOtherSmsChoiceField = (ObjectChoiceField)(new Object(_rb.getString(23), _rb.getStringArray(21), options.getFilterMode(2, 1)));
      this._filterOtherSmsChoiceField.setChangeListener(this);
      this._filterSLSmsValueField = (EditField)(new Object(_rb.getString(24), options.getFilterValue(0, 1)));
      this._filterSISmsValueField = (EditField)(new Object(_rb.getString(24), options.getFilterValue(1, 1)));
      this._filterOtherSmsValueField = (EditField)(new Object(_rb.getString(24), options.getFilterValue(2, 1)));
      this._acceptSLIpChoiceField = (ObjectChoiceField)(new Object(_rb.getString(38), _rb.getStringArray(7), options.getAcceptMode(0, 0)));
      this._acceptSLIpChoiceField.setChangeListener(this);
      this._filterSLIpChoiceField = (ObjectChoiceField)(new Object(_rb.getString(25), _rb.getStringArray(21), options.getFilterMode(0, 0)));
      this._filterSLIpChoiceField.setChangeListener(this);
      this._acceptSIIpChoiceField = (ObjectChoiceField)(new Object(_rb.getString(38), _rb.getStringArray(7), options.getAcceptMode(1, 0)));
      this._acceptSIIpChoiceField.setChangeListener(this);
      this._filterSIIpChoiceField = (ObjectChoiceField)(new Object(_rb.getString(25), _rb.getStringArray(21), options.getFilterMode(1, 0)));
      this._filterSIIpChoiceField.setChangeListener(this);
      this._acceptOtherIpChoiceField = (ObjectChoiceField)(new Object(_rb.getString(38), _rb.getStringArray(7), options.getAcceptMode(2, 0)));
      this._acceptOtherIpChoiceField.setChangeListener(this);
      this._filterOtherIpChoiceField = (ObjectChoiceField)(new Object(_rb.getString(25), _rb.getStringArray(21), options.getFilterMode(2, 0)));
      this._filterOtherIpChoiceField.setChangeListener(this);
      this._filterSLIpValueField = (EditField)(new Object(_rb.getString(24), options.getFilterValue(0, 0)));
      this._filterSIIpValueField = (EditField)(new Object(_rb.getString(24), options.getFilterValue(1, 0)));
      this._filterOtherIpValueField = (EditField)(new Object(_rb.getString(24), options.getFilterValue(2, 0)));
      if (mainPushEnabled) {
         this.addFields();
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (context != Integer.MIN_VALUE) {
         if (field == this._enablePushField) {
            if (this._enablePushField.getChecked()) {
               if (!this._fieldsAdded) {
                  this.addFields();
                  return;
               }
            } else if (this._fieldsAdded) {
               this.deleteFields();
               return;
            }
         } else {
            if (field == this._mdsEnablePushField) {
               if (this._mdsEnablePushField.getChecked()) {
                  this._mainScreen.insert(this._acceptSLMdsChoiceField, this._slLabelField.getIndex() + 1);
                  this.insertSelectionField(this._acceptSLMdsChoiceField, this._filterSLMdsChoiceField, 2, false);
                  this._mainScreen.insert(this._acceptSIMdsChoiceField, this._siLabelField.getIndex() + 1);
                  this.insertSelectionField(this._acceptSIMdsChoiceField, this._filterSIMdsChoiceField, 2, false);
                  this._mainScreen.insert(this._acceptOtherMdsChoiceField, this._otherLabelField.getIndex() + 1);
                  this.insertSelectionField(this._acceptOtherMdsChoiceField, this._filterOtherMdsChoiceField, 2, false);
                  return;
               }

               this.deleteField(this._acceptSLMdsChoiceField);
               this.deleteField(this._filterSLMdsChoiceField);
               this.deleteField(this._acceptSIMdsChoiceField);
               this.deleteField(this._filterSIMdsChoiceField);
               this.deleteField(this._acceptOtherMdsChoiceField);
               this.deleteField(this._filterOtherMdsChoiceField);
               return;
            }

            if (field != this._wapEnablePushField) {
               if (field == this._filterSLSmsChoiceField) {
                  this.addRemoveFilterValueField(this._filterSLSmsChoiceField, this._filterSLSmsValueField);
                  return;
               }

               if (field == this._filterSISmsChoiceField) {
                  this.addRemoveFilterValueField(this._filterSISmsChoiceField, this._filterSISmsValueField);
                  return;
               }

               if (field == this._filterOtherSmsChoiceField) {
                  this.addRemoveFilterValueField(this._filterOtherSmsChoiceField, this._filterOtherSmsValueField);
                  return;
               }

               if (field == this._filterSLIpChoiceField) {
                  this.addRemoveFilterValueField(this._filterSLIpChoiceField, this._filterSLIpValueField);
                  return;
               }

               if (field == this._filterSIIpChoiceField) {
                  this.addRemoveFilterValueField(this._filterSIIpChoiceField, this._filterSIIpValueField);
                  return;
               }

               if (field == this._filterOtherIpChoiceField) {
                  this.addRemoveFilterValueField(this._filterOtherIpChoiceField, this._filterOtherIpValueField);
                  return;
               }

               if (field == this._acceptSLMdsChoiceField) {
                  this.addRemoveFilterField(this._acceptSLMdsChoiceField, this._filterSLMdsChoiceField);
                  return;
               }

               if (field == this._acceptSLSmsChoiceField) {
                  if (!this.addRemoveFilterField(this._acceptSLSmsChoiceField, this._filterSLSmsChoiceField)) {
                     this.deleteField(this._filterSLSmsValueField);
                     return;
                  }

                  this.addRemoveFilterValueField(this._filterSLSmsChoiceField, this._filterSLSmsValueField);
                  return;
               }

               if (field == this._acceptSLIpChoiceField) {
                  if (!this.addRemoveFilterField(this._acceptSLIpChoiceField, this._filterSLIpChoiceField)) {
                     this.deleteField(this._filterSLIpValueField);
                     return;
                  }

                  this.addRemoveFilterValueField(this._filterSLIpChoiceField, this._filterSLIpValueField);
                  return;
               }

               if (field == this._acceptSIMdsChoiceField) {
                  this.addRemoveFilterField(this._acceptSIMdsChoiceField, this._filterSIMdsChoiceField);
                  return;
               }

               if (field == this._acceptSISmsChoiceField) {
                  if (!this.addRemoveFilterField(this._acceptSISmsChoiceField, this._filterSISmsChoiceField)) {
                     this.deleteField(this._filterSISmsValueField);
                     return;
                  }

                  this.addRemoveFilterValueField(this._filterSISmsChoiceField, this._filterSISmsValueField);
                  return;
               }

               if (field == this._acceptSIIpChoiceField) {
                  if (!this.addRemoveFilterField(this._acceptSIIpChoiceField, this._filterSIIpChoiceField)) {
                     this.deleteField(this._filterSIIpValueField);
                     return;
                  }

                  this.addRemoveFilterValueField(this._filterSIIpChoiceField, this._filterSIIpValueField);
                  return;
               }

               if (field == this._acceptOtherMdsChoiceField) {
                  this.addRemoveFilterField(this._acceptOtherMdsChoiceField, this._filterOtherMdsChoiceField);
                  return;
               }

               if (field == this._acceptOtherSmsChoiceField) {
                  if (!this.addRemoveFilterField(this._acceptOtherSmsChoiceField, this._filterOtherSmsChoiceField)) {
                     this.deleteField(this._filterOtherSmsValueField);
                     return;
                  }

                  this.addRemoveFilterValueField(this._filterOtherSmsChoiceField, this._filterOtherSmsValueField);
                  return;
               }

               if (field == this._acceptOtherIpChoiceField) {
                  if (!this.addRemoveFilterField(this._acceptOtherIpChoiceField, this._filterOtherIpChoiceField)) {
                     this.deleteField(this._filterOtherIpValueField);
                     return;
                  }

                  this.addRemoveFilterValueField(this._filterOtherIpChoiceField, this._filterOtherIpValueField);
               }
            } else {
               if (!this._wapEnablePushField.getChecked()) {
                  this.deleteField(this._acceptSLSmsChoiceField);
                  this.deleteField(this._filterSLSmsChoiceField);
                  this.deleteField(this._filterSLSmsValueField);
                  this.deleteField(this._acceptSISmsChoiceField);
                  this.deleteField(this._filterSISmsChoiceField);
                  this.deleteField(this._filterSISmsValueField);
                  this.deleteField(this._acceptOtherSmsChoiceField);
                  this.deleteField(this._filterOtherSmsChoiceField);
                  this.deleteField(this._filterOtherSmsValueField);
                  this.deleteField(this._acceptSLIpChoiceField);
                  this.deleteField(this._filterSLIpChoiceField);
                  this.deleteField(this._filterSLIpValueField);
                  this.deleteField(this._acceptSIIpChoiceField);
                  this.deleteField(this._filterSIIpChoiceField);
                  this.deleteField(this._filterSIIpValueField);
                  this.deleteField(this._acceptOtherIpChoiceField);
                  this.deleteField(this._filterOtherIpChoiceField);
                  this.deleteField(this._filterOtherIpValueField);
                  return;
               }

               this._mainScreen
                  .insert(
                     this._acceptSLSmsChoiceField,
                     Math.max(this._slLabelField.getIndex(), Math.max(this._acceptSLMdsChoiceField.getIndex(), this._filterSLMdsChoiceField.getIndex())) + 1
                  );
               if (this.insertSelectionField(this._acceptSLSmsChoiceField, this._filterSLSmsChoiceField, 2, false)) {
                  this.insertSelectionField(this._filterSLSmsChoiceField, this._filterSLSmsValueField, 1, true);
               }

               this._mainScreen
                  .insert(
                     this._acceptSLIpChoiceField,
                     Math.max(
                           Math.max(this._acceptSLSmsChoiceField.getIndex(), this._filterSLSmsChoiceField.getIndex()), this._filterSLSmsValueField.getIndex()
                        )
                        + 1
                  );
               if (this.insertSelectionField(this._acceptSLIpChoiceField, this._filterSLIpChoiceField, 2, false)) {
                  this.insertSelectionField(this._filterSLIpChoiceField, this._filterSLIpValueField, 1, true);
               }

               this._mainScreen
                  .insert(
                     this._acceptSISmsChoiceField,
                     Math.max(this._siLabelField.getIndex(), Math.max(this._acceptSIMdsChoiceField.getIndex(), this._filterSIMdsChoiceField.getIndex())) + 1
                  );
               if (this.insertSelectionField(this._acceptSISmsChoiceField, this._filterSISmsChoiceField, 2, false)) {
                  this.insertSelectionField(this._filterSISmsChoiceField, this._filterSISmsValueField, 1, true);
               }

               this._mainScreen
                  .insert(
                     this._acceptSIIpChoiceField,
                     Math.max(
                           Math.max(this._acceptSISmsChoiceField.getIndex(), this._filterSISmsChoiceField.getIndex()), this._filterSISmsValueField.getIndex()
                        )
                        + 1
                  );
               if (this.insertSelectionField(this._acceptSIIpChoiceField, this._filterSIIpChoiceField, 2, false)) {
                  this.insertSelectionField(this._filterSIIpChoiceField, this._filterSIIpValueField, 1, true);
               }

               this._mainScreen
                  .insert(
                     this._acceptOtherSmsChoiceField,
                     Math.max(
                           this._otherLabelField.getIndex(), Math.max(this._acceptOtherMdsChoiceField.getIndex(), this._filterOtherMdsChoiceField.getIndex())
                        )
                        + 1
                  );
               if (this.insertSelectionField(this._acceptOtherSmsChoiceField, this._filterOtherSmsChoiceField, 2, false)) {
                  this.insertSelectionField(this._filterOtherSmsChoiceField, this._filterOtherSmsValueField, 1, true);
               }

               this._mainScreen
                  .insert(
                     this._acceptOtherIpChoiceField,
                     Math.max(
                           Math.max(this._acceptOtherSmsChoiceField.getIndex(), this._filterOtherSmsChoiceField.getIndex()),
                           this._filterOtherSmsValueField.getIndex()
                        )
                        + 1
                  );
               if (this.insertSelectionField(this._acceptOtherIpChoiceField, this._filterOtherIpChoiceField, 2, false)) {
                  this.insertSelectionField(this._filterOtherIpChoiceField, this._filterOtherIpValueField, 1, true);
                  return;
               }
            }
         }
      }
   }

   private final void addRemoveFilterValueField(ObjectChoiceField choiceField, Field otherField) {
      int index = choiceField.getSelectedIndex();
      if (index != 0 && index != 2) {
         this.insertSelectionField(choiceField, otherField, 1, true);
      } else {
         this.deleteField(otherField);
      }
   }

   private final boolean addRemoveFilterField(ObjectChoiceField choiceField, Field otherField) {
      int index = choiceField.getSelectedIndex();
      if (index == 2) {
         this.deleteField(otherField);
         return false;
      } else {
         this.insertSelectionField(choiceField, otherField, 2, false);
         return true;
      }
   }

   @Override
   protected final boolean save() {
      PushOptions options = PushOptions.getOptions();
      int mask = options.getDirtyMask();
      if (this._allowOtherApplicationsField.isDirty()) {
         mask |= 64;
      }

      options.setAllowOtherApplications(this._allowOtherApplicationsField.getChecked());
      if (this._acceptSLMdsChoiceField.isDirty()) {
         mask |= 8;
      }

      options.setAcceptMode(0, 2, this._acceptSLMdsChoiceField.getSelectedIndex());
      if (this._filterSLMdsChoiceField.isDirty()) {
         mask |= 128;
      }

      int index = this._filterSLMdsChoiceField.getSelectedIndex();
      if (index == 1) {
         index++;
      }

      options.setFilterMode(0, 2, index, null);
      if (this._acceptSLSmsChoiceField.isDirty()) {
         mask |= 65536;
      }

      options.setAcceptMode(0, 1, this._acceptSLSmsChoiceField.getSelectedIndex());
      if (this._filterSLSmsChoiceField.isDirty() || this._filterSLSmsValueField.isDirty()) {
         mask |= 1024;
      }

      options.setFilterMode(0, 1, this._filterSLSmsChoiceField.getSelectedIndex(), this._filterSLSmsValueField.getText());
      if (this._acceptSLIpChoiceField.isDirty()) {
         mask |= 524288;
      }

      options.setAcceptMode(0, 0, this._acceptSLIpChoiceField.getSelectedIndex());
      if (this._filterSLIpChoiceField.isDirty() || this._filterSLIpValueField.isDirty()) {
         mask |= 8192;
      }

      options.setFilterMode(0, 0, this._filterSLIpChoiceField.getSelectedIndex(), this._filterSLIpValueField.getText());
      if (this._acceptSIMdsChoiceField.isDirty()) {
         mask |= 16;
      }

      options.setAcceptMode(1, 2, this._acceptSIMdsChoiceField.getSelectedIndex());
      if (this._filterSIMdsChoiceField.isDirty()) {
         mask |= 256;
      }

      index = this._filterSIMdsChoiceField.getSelectedIndex();
      if (index == 1) {
         index++;
      }

      options.setFilterMode(1, 2, index, null);
      if (this._acceptSISmsChoiceField.isDirty()) {
         mask |= 131072;
      }

      options.setAcceptMode(1, 1, this._acceptSISmsChoiceField.getSelectedIndex());
      if (this._filterSISmsChoiceField.isDirty() || this._filterSISmsValueField.isDirty()) {
         mask |= 2048;
      }

      options.setFilterMode(1, 1, this._filterSISmsChoiceField.getSelectedIndex(), this._filterSISmsValueField.getText());
      if (this._acceptSIIpChoiceField.isDirty()) {
         mask |= 1048576;
      }

      options.setAcceptMode(1, 0, this._acceptSIIpChoiceField.getSelectedIndex());
      if (this._filterSIIpChoiceField.isDirty() || this._filterSIIpValueField.isDirty()) {
         mask |= 16384;
      }

      options.setFilterMode(1, 0, this._filterSIIpChoiceField.getSelectedIndex(), this._filterSIIpValueField.getText());
      if (this._acceptOtherMdsChoiceField.isDirty()) {
         mask |= 32;
      }

      options.setAcceptMode(2, 2, this._acceptOtherMdsChoiceField.getSelectedIndex());
      if (this._filterOtherMdsChoiceField.isDirty()) {
         mask |= 512;
      }

      index = this._filterOtherMdsChoiceField.getSelectedIndex();
      if (index == 1) {
         index++;
      }

      options.setFilterMode(2, 2, index, null);
      if (this._acceptOtherSmsChoiceField.isDirty()) {
         mask |= 262144;
      }

      options.setAcceptMode(2, 1, this._acceptOtherSmsChoiceField.getSelectedIndex());
      if (this._filterOtherSmsChoiceField.isDirty() || this._filterOtherSmsValueField.isDirty()) {
         mask |= 4096;
      }

      options.setFilterMode(2, 1, this._filterOtherSmsChoiceField.getSelectedIndex(), this._filterOtherSmsValueField.getText());
      if (this._acceptOtherIpChoiceField.isDirty()) {
         mask |= 2097152;
      }

      options.setAcceptMode(2, 0, this._acceptOtherIpChoiceField.getSelectedIndex());
      if (this._filterOtherIpChoiceField.isDirty() || this._filterOtherIpValueField.isDirty()) {
         mask |= 32768;
      }

      options.setFilterMode(2, 0, this._filterOtherIpChoiceField.getSelectedIndex(), this._filterOtherIpValueField.getText());
      boolean restart = false;
      if (this._enablePushField.isDirty()) {
         mask |= 1;
         restart = true;
      }

      options.setEnablePush(this._enablePushField.getChecked(), false);
      if (this._wapEnablePushField.isDirty()) {
         mask |= 4;
         restart = true;
      }

      options.setWAPEnablePush(this._wapEnablePushField.getChecked(), false);
      if (this._mdsEnablePushField.isDirty()) {
         mask |= 2;
         restart = true;
      }

      options.setMDSEnablePush(this._mdsEnablePushField.getChecked(), restart);
      options.setDirtyMask(mask);
      options.commit();
      return super.save();
   }

   private final void addFields() {
      boolean mdsEnabled = this._mdsEnablePushField.getChecked();
      boolean wapEnabled = this._wapEnablePushField.getChecked();
      this._mainScreen.add(this._mdsEnablePushField);
      this._mainScreen.add(this._wapEnablePushField);
      this._mainScreen.add(this._slLabelField);
      if (mdsEnabled) {
         this._mainScreen.add(this._acceptSLMdsChoiceField);
         this.addSelectionField(this._acceptSLMdsChoiceField, this._filterSLMdsChoiceField, 2, false);
      }

      if (wapEnabled) {
         this._mainScreen.add(this._acceptSLSmsChoiceField);
         if (this.addSelectionField(this._acceptSLSmsChoiceField, this._filterSLSmsChoiceField, 2, false)) {
            this.addSelectionField(this._filterSLSmsChoiceField, this._filterSLSmsValueField, 1, true);
         }

         this._mainScreen.add(this._acceptSLIpChoiceField);
         if (this.addSelectionField(this._acceptSLIpChoiceField, this._filterSLIpChoiceField, 2, false)) {
            this.addSelectionField(this._filterSLIpChoiceField, this._filterSLIpValueField, 1, true);
         }
      }

      this._mainScreen.add(this._siLabelField);
      if (mdsEnabled) {
         this._mainScreen.add(this._acceptSIMdsChoiceField);
         this.addSelectionField(this._acceptSIMdsChoiceField, this._filterSIMdsChoiceField, 2, false);
      }

      if (wapEnabled) {
         this._mainScreen.add(this._acceptSISmsChoiceField);
         if (this.addSelectionField(this._acceptSISmsChoiceField, this._filterSISmsChoiceField, 2, false)) {
            this.addSelectionField(this._filterSISmsChoiceField, this._filterSISmsValueField, 1, true);
         }

         this._mainScreen.add(this._acceptSIIpChoiceField);
         if (this.addSelectionField(this._acceptSIIpChoiceField, this._filterSIIpChoiceField, 2, false)) {
            this.addSelectionField(this._filterSIIpChoiceField, this._filterSIIpValueField, 1, true);
         }
      }

      this._mainScreen.add(this._otherLabelField);
      if (mdsEnabled) {
         this._mainScreen.add(this._acceptOtherMdsChoiceField);
         this.addSelectionField(this._acceptOtherMdsChoiceField, this._filterOtherMdsChoiceField, 2, false);
      }

      if (wapEnabled) {
         this._mainScreen.add(this._acceptOtherSmsChoiceField);
         if (this.addSelectionField(this._acceptOtherSmsChoiceField, this._filterOtherSmsChoiceField, 2, false)) {
            this.addSelectionField(this._filterOtherSmsChoiceField, this._filterOtherSmsValueField, 1, true);
         }

         this._mainScreen.add(this._acceptOtherIpChoiceField);
         if (this.addSelectionField(this._acceptOtherIpChoiceField, this._filterOtherIpChoiceField, 2, false)) {
            this.addSelectionField(this._filterOtherIpChoiceField, this._filterOtherIpValueField, 1, true);
         }
      }

      this._mainScreen.add(this._allowOtherApplicationsField);
      this._fieldsAdded = true;
   }

   private final boolean addSelectionField(ObjectChoiceField choiceField, Field valueField, int magicIndex, boolean equal) {
      if (choiceField.getSelectedIndex() == magicIndex == equal) {
         this._mainScreen.add(valueField);
         return true;
      } else {
         return false;
      }
   }

   private final boolean insertSelectionField(ObjectChoiceField choiceField, Field valueField, int magicIndex, boolean equal) {
      if (valueField.getScreen() == this._mainScreen) {
         return true;
      } else if (choiceField.getSelectedIndex() == magicIndex == equal) {
         this._mainScreen.insert(valueField, choiceField.getIndex() + 1);
         return true;
      } else {
         return false;
      }
   }

   private final void deleteFields() {
      this.deleteField(this._slLabelField);
      this.deleteField(this._siLabelField);
      this.deleteField(this._otherLabelField);
      this.deleteField(this._mdsEnablePushField);
      this.deleteField(this._wapEnablePushField);
      this.deleteField(this._acceptSLMdsChoiceField);
      this.deleteField(this._filterSLMdsChoiceField);
      this.deleteField(this._acceptSLSmsChoiceField);
      this.deleteField(this._filterSLSmsChoiceField);
      this.deleteField(this._filterSLSmsValueField);
      this.deleteField(this._acceptSLIpChoiceField);
      this.deleteField(this._filterSLIpChoiceField);
      this.deleteField(this._filterSLIpValueField);
      this.deleteField(this._acceptSIMdsChoiceField);
      this.deleteField(this._filterSIMdsChoiceField);
      this.deleteField(this._acceptSISmsChoiceField);
      this.deleteField(this._filterSISmsChoiceField);
      this.deleteField(this._filterSISmsValueField);
      this.deleteField(this._acceptSIIpChoiceField);
      this.deleteField(this._filterSIIpChoiceField);
      this.deleteField(this._filterSIIpValueField);
      this.deleteField(this._acceptOtherMdsChoiceField);
      this.deleteField(this._filterOtherMdsChoiceField);
      this.deleteField(this._acceptOtherSmsChoiceField);
      this.deleteField(this._filterOtherSmsChoiceField);
      this.deleteField(this._filterOtherSmsValueField);
      this.deleteField(this._acceptOtherIpChoiceField);
      this.deleteField(this._filterOtherIpChoiceField);
      this.deleteField(this._filterOtherIpValueField);
      this.deleteField(this._allowOtherApplicationsField);
      this._fieldsAdded = false;
   }

   private final void deleteField(Field field) {
      if (field.getScreen() == this._mainScreen) {
         this._mainScreen.delete(field);
      }
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      if (this._mainScreen.isDirty() || PushOptions.getOptions().getDirtyMask() != 0) {
         verbToMenu.addVerb(new PushOptionsItem$RestoreDefaultsVerb(this));
      }
   }
}
