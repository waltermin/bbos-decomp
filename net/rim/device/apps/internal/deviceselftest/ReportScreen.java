package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.gps.GPS;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.internal.bluetooth.BluetoothME;

final class ReportScreen extends MainScreen {
   private LabelField _title;
   private EditField _keyboard;
   private EditField _trackball;
   private EditField _holsterDetector;
   private EditField _lightSensor;
   private EditField _vibrator;
   private EditField _keypadBacklight;
   private EditField _lcdBacklight;
   private EditField _lcdPixels;
   private EditField _led;
   private EditField _handsetSpeaker;
   private EditField _handsfreeSpeaker;
   private EditField _handsetMicrophone;
   private EditField _headsetDetectSwitch;
   private EditField _headsetSpeaker;
   private EditField _headsetMicrophone;
   private EditField _rfAntenna;
   private EditField _gpsAntenna;
   private EditField _bluetoothSpeaker;
   private EditField _bluetoothMicrophone;
   private EditField _pin;
   private EditField _deviceType;
   private EditField _appVersion;
   private EditField _platform;
   private EditField _timeStamp;
   private MenuItem _deleteMenuItem;
   private Report report;
   private int rIndex;
   private DeviceSelfTest app;

   ReportScreen(Report rpt, DeviceSelfTest dst, int index) {
      this.report = rpt;
      this.rIndex = index;
      this.app = dst;
      this._title = (LabelField)(new Object(DeviceSelfTestResources.getString(20), 1152921504606846976L));
      this._pin = (EditField)(new Object(DeviceSelfTestResources.getString(47), null));
      this._pin.setEditable(false);
      this._pin.setText(this.report.getPin());
      this._deviceType = (EditField)(new Object(DeviceSelfTestResources.getString(48), null));
      this._deviceType.setEditable(false);
      this._deviceType.setText(this.report.getDeviceType());
      this._appVersion = (EditField)(new Object(DeviceSelfTestResources.getString(49), null));
      this._appVersion.setEditable(false);
      this._appVersion.setText(this.report.getAppVersion());
      this._platform = (EditField)(new Object(DeviceSelfTestResources.getString(50), null));
      this._platform.setEditable(false);
      this._platform.setText(this.report.getPlatform());
      String str = new Object(this.report.timeStamp).toString();
      this._timeStamp = (EditField)(new Object(DeviceSelfTestResources.getString(51), null));
      this._timeStamp.setEditable(false);
      this._timeStamp.setText(str);
      this._rfAntenna = (EditField)(new Object(DeviceSelfTestResources.getString(43), null));
      this._rfAntenna.setEditable(false);
      switch (this.report.rfAntenna) {
         case -2:
            this._rfAntenna.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._rfAntenna.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._rfAntenna.setText(DeviceSelfTestResources.getString(28));
      }

      this._headsetDetectSwitch = (EditField)(new Object(DeviceSelfTestResources.getString(40), null));
      this._headsetDetectSwitch.setEditable(false);
      switch (this.report.headsetDetectSwitch) {
         case -2:
            this._headsetDetectSwitch.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._headsetDetectSwitch.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._headsetDetectSwitch.setText(DeviceSelfTestResources.getString(28));
      }

      this._headsetSpeaker = (EditField)(new Object(DeviceSelfTestResources.getString(41), null));
      this._headsetSpeaker.setEditable(false);
      switch (this.report.headsetSpeaker) {
         case -2:
            this._headsetSpeaker.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._headsetSpeaker.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._headsetSpeaker.setText(DeviceSelfTestResources.getString(28));
      }

      this._headsetMicrophone = (EditField)(new Object(DeviceSelfTestResources.getString(42), null));
      this._headsetMicrophone.setEditable(false);
      switch (this.report.headsetMicrophone) {
         case -2:
            this._headsetMicrophone.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._headsetMicrophone.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._headsetMicrophone.setText(DeviceSelfTestResources.getString(28));
      }

      this._handsetMicrophone = (EditField)(new Object(DeviceSelfTestResources.getString(39), null));
      this._handsetMicrophone.setEditable(false);
      switch (this.report.handsetMicrophone) {
         case -2:
            this._handsetMicrophone.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._handsetMicrophone.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._handsetMicrophone.setText(DeviceSelfTestResources.getString(28));
      }

      this._handsfreeSpeaker = (EditField)(new Object(DeviceSelfTestResources.getString(38), null));
      this._handsfreeSpeaker.setEditable(false);
      switch (this.report.handsfreeSpeaker) {
         case -2:
            this._handsfreeSpeaker.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._handsfreeSpeaker.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._handsfreeSpeaker.setText(DeviceSelfTestResources.getString(28));
      }

      this._handsetSpeaker = (EditField)(new Object(DeviceSelfTestResources.getString(37), null));
      this._handsetSpeaker.setEditable(false);
      switch (this.report.handsetSpeaker) {
         case -2:
            this._handsetSpeaker.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._handsetSpeaker.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._handsetSpeaker.setText(DeviceSelfTestResources.getString(28));
      }

      this._lcdPixels = (EditField)(new Object(DeviceSelfTestResources.getString(36), null));
      this._lcdPixels.setEditable(false);
      switch (this.report.lcdPixels) {
         case -2:
            this._lcdPixels.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._lcdPixels.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._lcdPixels.setText(DeviceSelfTestResources.getString(28));
      }

      this._keypadBacklight = (EditField)(new Object(DeviceSelfTestResources.getString(35), null));
      this._keypadBacklight.setEditable(false);
      switch (this.report.keypadBacklight) {
         case -2:
            this._keypadBacklight.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._keypadBacklight.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._keypadBacklight.setText(DeviceSelfTestResources.getString(28));
      }

      this._vibrator = (EditField)(new Object(DeviceSelfTestResources.getString(34), null));
      this._vibrator.setEditable(false);
      switch (this.report.vibrator) {
         case -2:
            this._vibrator.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._vibrator.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._vibrator.setText(DeviceSelfTestResources.getString(28));
      }

      this._lightSensor = (EditField)(new Object(DeviceSelfTestResources.getString(33), null));
      this._lightSensor.setEditable(false);
      switch (this.report.lightSensor) {
         case -2:
            this._lightSensor.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._lightSensor.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._lightSensor.setText(DeviceSelfTestResources.getString(28));
      }

      this._holsterDetector = (EditField)(new Object(DeviceSelfTestResources.getString(32), null));
      this._holsterDetector.setEditable(false);
      switch (this.report.holsterDetector) {
         case -2:
            this._holsterDetector.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._holsterDetector.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._holsterDetector.setText(DeviceSelfTestResources.getString(28));
      }

      this._keyboard = (EditField)(new Object(DeviceSelfTestResources.getString(30), null));
      this._keyboard.setEditable(false);
      switch (this.report.keyboard) {
         case -2:
            this._keyboard.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._keyboard.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._keyboard.setText(DeviceSelfTestResources.getString(28));
      }

      this._trackball = (EditField)(new Object(DeviceSelfTestResources.getString(31), null));
      this._trackball.setEditable(false);
      switch (this.report.trackball) {
         case -2:
            this._trackball.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._trackball.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._trackball.setText(DeviceSelfTestResources.getString(28));
      }

      this._led = (EditField)(new Object(DeviceSelfTestResources.getString(21), null));
      this._led.setEditable(false);
      switch (this.report.led) {
         case -2:
            this._led.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._led.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._led.setText(DeviceSelfTestResources.getString(28));
      }

      this._lcdBacklight = (EditField)(new Object(DeviceSelfTestResources.getString(22), null));
      this._lcdBacklight.setEditable(false);
      switch (this.report.lcdBacklight) {
         case -2:
            this._lcdBacklight.setText(DeviceSelfTestResources.getString(29));
            break;
         case -1:
         default:
            this._lcdBacklight.setText(DeviceSelfTestResources.getString(27));
            break;
         case 0:
            this._lcdBacklight.setText(DeviceSelfTestResources.getString(28));
      }

      this.setTitle(this._title);
      this.add(this._deviceType);
      this.add(this._pin);
      this.add(this._platform);
      this.add(this._appVersion);
      this.add((Field)(new Object()));
      this.add(this._timeStamp);
      this.add((Field)(new Object()));
      this.add(this._keyboard);
      this.add(this._trackball);
      this.add(this._lightSensor);
      this.add(this._keypadBacklight);
      this.add(this._led);
      this.add(this._lcdBacklight);
      this.add(this._lcdPixels);
      this.add(this._vibrator);
      this.add(this._holsterDetector);
      this.add(this._handsetSpeaker);
      this.add(this._handsfreeSpeaker);
      this.add(this._handsetMicrophone);
      this.add(this._headsetDetectSwitch);
      this.add(this._headsetSpeaker);
      this.add(this._headsetMicrophone);
      if (BluetoothME.isSupported()) {
         this._bluetoothMicrophone = (EditField)(new Object(DeviceSelfTestResources.getString(46), null));
         this._bluetoothMicrophone.setEditable(false);
         switch (this.report.bluetoothMicrophone) {
            case -2:
               this._bluetoothMicrophone.setText(DeviceSelfTestResources.getString(29));
               break;
            case -1:
            default:
               this._bluetoothMicrophone.setText(DeviceSelfTestResources.getString(27));
               break;
            case 0:
               this._bluetoothMicrophone.setText(DeviceSelfTestResources.getString(28));
         }

         this._bluetoothSpeaker = (EditField)(new Object(DeviceSelfTestResources.getString(45), null));
         this._bluetoothSpeaker.setEditable(false);
         switch (this.report.bluetoothSpeaker) {
            case -2:
               this._bluetoothSpeaker.setText(DeviceSelfTestResources.getString(29));
               break;
            case -1:
            default:
               this._bluetoothSpeaker.setText(DeviceSelfTestResources.getString(27));
               break;
            case 0:
               this._bluetoothSpeaker.setText(DeviceSelfTestResources.getString(28));
         }

         this.add(this._bluetoothSpeaker);
         this.add(this._bluetoothMicrophone);
      }

      this.add(this._rfAntenna);
      if (GPS.isSupported()) {
         this._gpsAntenna = (EditField)(new Object(DeviceSelfTestResources.getString(44), null));
         this._gpsAntenna.setEditable(false);
         switch (this.report.gpsAntenna) {
            case -2:
               this._gpsAntenna.setText(DeviceSelfTestResources.getString(29));
               break;
            case -1:
            default:
               this._gpsAntenna.setText(DeviceSelfTestResources.getString(27));
               break;
            case 0:
               this._gpsAntenna.setText(DeviceSelfTestResources.getString(28));
         }

         this.add(this._gpsAntenna);
      }

      this.setupMenuItems();
   }

   private final void setupMenuItems() {
      this._deleteMenuItem = new ReportScreen$1(this, DeviceSelfTestResources.getString(2), 0, 0);
   }

   private final void doDelete() {
      int index = this.app.reports.size() - 1 - this.rIndex;
      int response = Dialog.ask(3, DeviceSelfTestResources.getString(2), -1);
      if (4 == response) {
         this.app.reports.removeElementAt(index);
         this.app.saveReports();
      }

      this.app.popScreen(this.app.getActiveScreen());
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(this._deleteMenuItem);
   }
}
