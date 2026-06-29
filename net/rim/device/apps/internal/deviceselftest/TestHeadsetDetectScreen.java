package net.rim.device.apps.internal.deviceselftest;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Audio;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;

final class TestHeadsetDetectScreen extends TestScreenBase {
   TestHeadsetDetectScreen$ReportListCallback _callback;
   ListField _reportList;
   LabelField _event;
   Vector reports = (Vector)(new Object());

   TestHeadsetDetectScreen() {
      super._title = (LabelField)(new Object(DeviceSelfTestResources.getString(68), 1152921504606846976L));
      super._input = (LabelField)(new Object(DeviceSelfTestResources.getString(72), 1152921504606846976L));
      super._output = (LabelField)(new Object(DeviceSelfTestResources.getString(73), 1152921504606846976L));
      this._event = (LabelField)(new Object(DeviceSelfTestResources.getString(74), 1152921504606846976L));
      this.setTitle(super._title);
      this.add(super._input);
      this.add((Field)(new Object()));
      this.add(super._output);
      this.add((Field)(new Object()));
      this.add(this._event);
      this.setupMenuItems();
      this._callback = new TestHeadsetDetectScreen$ReportListCallback();
      this._reportList = (ListField)(new Object());
      this._reportList.setCallback(this._callback);
      this._reportList.setSearchable(false);
      this.add(this._reportList);
   }

   final void insertReport(int type) {
      String timeStamp = new Object(System.currentTimeMillis()).toString();
      String event;
      if (type == 1) {
         event = ((StringBuffer)(new Object())).append(DeviceSelfTestResources.getString(75)).append(timeStamp.substring(0, 20)).toString();
      } else if (type == 2) {
         event = ((StringBuffer)(new Object())).append(DeviceSelfTestResources.getString(76)).append(timeStamp.substring(0, 20)).toString();
      } else if (type == 3) {
         event = ((StringBuffer)(new Object())).append(DeviceSelfTestResources.getString(118)).append(timeStamp.substring(0, 20)).toString();
      } else {
         event = ((StringBuffer)(new Object())).append(DeviceSelfTestResources.getString(119)).append(timeStamp.substring(0, 20)).toString();
      }

      this.reports.addElement(event);
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
      Application.getApplication().invokeLater(new TestHeadsetDetectScreen$1(this));
   }

   @Override
   protected final void cleanup() {
      Audio.removeListener(super.app, (TestHeadsetDetect)super.app.currentTest);
      super.app.removeKeyListener((TestHeadsetDetect)super.app.currentTest);
      super.app.popScreen(super.app.getActiveScreen());
   }
}
