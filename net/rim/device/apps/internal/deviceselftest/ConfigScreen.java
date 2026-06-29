package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.gps.GPS;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.internal.bluetooth.BluetoothME;

public final class ConfigScreen extends MainScreen implements FieldChangeListener {
   LabelField _title;
   LabelField _prompt;
   private MenuItem _runMenuItem;
   private MenuItem _backMenuItem;
   private MenuItem _selectAllMenuItem;
   private MenuItem _unSelectAllMenuItem;
   DeviceSelfTest app = (DeviceSelfTest)Application.getApplication();
   private CheckboxField _inputCheckBox;
   private CheckboxField _miscCheckBox;
   private CheckboxField _handsetAudioCheckBox;
   private CheckboxField _headsetAudioCheckBox;
   private CheckboxField _bluetoothAudioCheckBox;
   private CheckboxField _rfCheckBox;
   private CheckboxField _gpsCheckBox;
   private ButtonField _runButtonField;

   protected final void doRun() {
      if (!this._inputCheckBox.getChecked()
         && !this._miscCheckBox.getChecked()
         && !this._handsetAudioCheckBox.getChecked()
         && !this._headsetAudioCheckBox.getChecked()
         && !this._bluetoothAudioCheckBox.getChecked()
         && !this._rfCheckBox.getChecked()
         && !this._gpsCheckBox.getChecked()) {
         Dialog.alert(DeviceSelfTestResources.getString(124));
      } else {
         this.app.popScreen(this);
         this.app.currentReport = new Report(System.currentTimeMillis());
         this.app.reports.addElement(this.app.currentReport);
         this.app.saveReports();
         if (this._inputCheckBox.getChecked()) {
            this.app.injectTaskSuite(this.app.taskSuiteInput);
         }

         if (this._miscCheckBox.getChecked()) {
            this.app.injectTaskSuite(this.app.taskSuiteMisc);
         }

         if (this._handsetAudioCheckBox.getChecked()) {
            this.app.injectTaskSuite(this.app.taskSuiteHandsetAudio);
         }

         if (this._headsetAudioCheckBox.getChecked()) {
            this.app.injectTaskSuite(this.app.taskSuiteHeadsetAudio);
         }

         if (this._bluetoothAudioCheckBox != null && this._bluetoothAudioCheckBox.getChecked()) {
            this.app.injectTaskSuite(this.app.taskSuiteBluetoothAudio);
         }

         if (this._rfCheckBox.getChecked()) {
            this.app.injectTaskSuite(this.app.taskSuiteRF);
         }

         if (this._gpsCheckBox != null && this._gpsCheckBox.getChecked()) {
            this.app.injectTaskSuite(this.app.taskSuiteGPS);
         }

         this.app.getNextTest();
      }
   }

   protected final void doSelectAll() {
      this._inputCheckBox.setChecked(true);
      this._miscCheckBox.setChecked(true);
      this._handsetAudioCheckBox.setChecked(true);
      this._headsetAudioCheckBox.setChecked(true);
      if (this._bluetoothAudioCheckBox != null) {
         this._bluetoothAudioCheckBox.setChecked(true);
      }

      this._rfCheckBox.setChecked(true);
      if (this._gpsCheckBox != null) {
         this._gpsCheckBox.setChecked(true);
      }
   }

   protected final void doUnSelectAll() {
      this._inputCheckBox.setChecked(false);
      this._miscCheckBox.setChecked(false);
      this._handsetAudioCheckBox.setChecked(false);
      this._headsetAudioCheckBox.setChecked(false);
      if (this._bluetoothAudioCheckBox != null) {
         this._bluetoothAudioCheckBox.setChecked(false);
      }

      this._rfCheckBox.setChecked(false);
      if (this._gpsCheckBox != null) {
         this._gpsCheckBox.setChecked(false);
      }
   }

   protected final void doBack() {
      this.app.popScreen(this);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._runButtonField) {
         this.doRun();
      }
   }

   private final void setupMenuItems() {
      this._runMenuItem = new ConfigScreen$1(this, DeviceSelfTestResources.getString(11), 0, 0);
      this._backMenuItem = new ConfigScreen$2(this, DeviceSelfTestResources.getString(12), 20000, 20000);
      this._selectAllMenuItem = new ConfigScreen$3(this, DeviceSelfTestResources.getString(121), 20000, 20000);
      this._unSelectAllMenuItem = new ConfigScreen$4(this, DeviceSelfTestResources.getString(143), 20000, 20000);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(this._runMenuItem);
      menu.add(this._backMenuItem);
      menu.add(this._selectAllMenuItem);
      menu.add(this._unSelectAllMenuItem);
      menu.addSeparator();
   }

   ConfigScreen() {
      this.setupMenuItems();
      this._title = (LabelField)(new Object(DeviceSelfTestResources.getString(6), 1152921504606846976L));
      this.setTitle(this._title);
      this._prompt = (LabelField)(new Object(DeviceSelfTestResources.getString(6), 1152921504606846976L));
      this._inputCheckBox = (CheckboxField)(new Object(DeviceSelfTestResources.getString(7), false));
      this._miscCheckBox = (CheckboxField)(new Object(DeviceSelfTestResources.getString(9), false));
      this._handsetAudioCheckBox = (CheckboxField)(new Object(DeviceSelfTestResources.getString(8), false));
      this._headsetAudioCheckBox = (CheckboxField)(new Object(DeviceSelfTestResources.getString(23), false));
      this._rfCheckBox = (CheckboxField)(new Object(DeviceSelfTestResources.getString(10), false));
      this.add(this._inputCheckBox);
      this.add(this._miscCheckBox);
      this.add(this._handsetAudioCheckBox);
      this.add(this._headsetAudioCheckBox);
      if (BluetoothME.isSupported()) {
         this._bluetoothAudioCheckBox = (CheckboxField)(new Object(DeviceSelfTestResources.getString(24), false));
         this.add(this._bluetoothAudioCheckBox);
      }

      this.add(this._rfCheckBox);
      if (GPS.isSupported()) {
         this._gpsCheckBox = (CheckboxField)(new Object(DeviceSelfTestResources.getString(105), false));
         this.add(this._gpsCheckBox);
      }

      this._runButtonField = (ButtonField)(new Object(DeviceSelfTestResources.getString(125), 65536));
      this._runButtonField.setChangeListener(this);
      this.add(this._runButtonField);
   }

   @Override
   public final boolean onClose() {
      this.app.popScreen(this);
      return true;
   }
}
