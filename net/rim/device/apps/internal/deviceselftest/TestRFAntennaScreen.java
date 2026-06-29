package net.rim.device.apps.internal.deviceselftest;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.internal.system.RadioInternal;

final class TestRFAntennaScreen extends TestScreenBase {
   TestRFAntenna handler;
   TestRFAntennaScreen$ReportListCallback _callback;
   ListField _reportList;
   LabelField _reportLabel;
   EditField _phoneInput;
   Vector reports;
   MenuItem _callMenuItem;
   MenuItem _hangupMenuItem;

   @Override
   protected final void setupMenuItems() {
      super.setupMenuItems();
      this._callMenuItem = new TestRFAntennaScreen$1(this, DeviceSelfTestResources.getString(66), 0, 0);
      this._hangupMenuItem = new TestRFAntennaScreen$2(this, DeviceSelfTestResources.getString(67), 0, 0);
   }

   TestRFAntennaScreen(TestRFAntenna _handler) {
      this.handler = _handler;
      this.reports = (Vector)(new Object());
      super._title = (LabelField)(new Object(DeviceSelfTestResources.getString(61), 1152921504606846976L));
      super._input = (LabelField)(new Object(DeviceSelfTestResources.getString(62), 1152921504606846976L));
      super._output = (LabelField)(new Object(DeviceSelfTestResources.getString(63), 1152921504606846976L));
      this._reportLabel = (LabelField)(new Object(DeviceSelfTestResources.getString(64), 1152921504606846976L));
      this._phoneInput = (EditField)(new Object(DeviceSelfTestResources.getString(65), null, 16, 100663296));
      this._phoneInput.setEditable(true);
      this._phoneInput.setText("15198887465");
      this.setTitle(super._title);
      this.add(super._input);
      this.add((Field)(new Object()));
      this.add(super._output);
      this.add((Field)(new Object()));
      this.add(this._phoneInput);
      this.add((Field)(new Object()));
      this.add(this._reportLabel);
      this.setupMenuItems();
      this._callback = new TestRFAntennaScreen$ReportListCallback();
      this._reportList = (ListField)(new Object());
      this._reportList.setCallback(this._callback);
      this._reportList.setSearchable(false);
      this.add(this._reportList);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (Phone.isPhoneActive()) {
         menu.add(this._hangupMenuItem);
      } else {
         menu.add(this._callMenuItem);
      }

      super.isMenuOn = true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void doCall() {
      int radioState = RadioInfo.getState();
      if (radioState == 0) {
         TestTaskBase.showMessage(DeviceSelfTestResources.getString(132));
      } else if (!RadioInternal.isSIMCardPresent()) {
         TestTaskBase.showMessage(DeviceSelfTestResources.getString(131));
      } else {
         boolean var4 = false /* VF: Semaphore variable */;

         try {
            var4 = true;
            if (!SIMCard.isValid()) {
               TestTaskBase.showMessage(DeviceSelfTestResources.getString(130));
               return;
            }

            var4 = false;
         } finally {
            if (var4) {
               TestTaskBase.showMessage(DeviceSelfTestResources.getString(133));
               return;
            }
         }

         super.isMenuOn = false;
         this.handler.doCall();
      }
   }

   final void doHangup() {
      super.isMenuOn = false;
      this.handler.doHangup();
   }

   final void insertReport() {
      int measurement = RadioInfo.getSignalLevel();
      String timeStamp = new Object(System.currentTimeMillis()).toString();
      this.reports.addElement(((StringBuffer)(new Object())).append(measurement).append(" -dBm @ ").append(timeStamp.substring(0, 20)).toString());
      this.populate();
   }

   final void insertReport(String msg) {
      String timeStamp = new Object(System.currentTimeMillis()).toString();
      this.reports.addElement(((StringBuffer)(new Object())).append(msg).append(timeStamp.substring(0, 20)).toString());
      this.populate();
   }

   final void populate() {
      this._callback.erase();
      int numReports = this.reports.size();

      for (int i = 0; i < numReports; i++) {
         Object entry = this.reports.elementAt(i);
         this._callback.insert(entry, i);
         synchronized (Application.getEventLock()) {
            this._reportList.insert(i);
         }
      }

      synchronized (Application.getEventLock()) {
         this._reportList.setSize(this._callback.size());
      }
   }

   @Override
   protected final void doNext() {
      Application.getApplication().invokeLater(new TestRFAntennaScreen$3(this));
   }

   @Override
   protected final void cleanup() {
      super.app.removeRadioListener(this.handler.phoneListener);
      super.app.removeKeyListener(super.app.currentTest);
      super.app.popScreen(super.app.getActiveScreen());
   }
}
