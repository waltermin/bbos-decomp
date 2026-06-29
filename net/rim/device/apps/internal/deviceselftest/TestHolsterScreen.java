package net.rim.device.apps.internal.deviceselftest;

import java.util.Date;
import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.LED;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.SeparatorField;

final class TestHolsterScreen extends TestScreenBase {
   TestHolsterScreen$ReportListCallback _callback;
   ListField _reportList;
   LabelField _event;
   Vector reports = new Vector();

   TestHolsterScreen() {
      super._title = new LabelField(DeviceSelfTestResources.getString(55), 1152921504606846976L);
      super._input = new LabelField(DeviceSelfTestResources.getString(56), 1152921504606846976L);
      super._output = new LabelField(DeviceSelfTestResources.getString(57), 1152921504606846976L);
      this._event = new LabelField(DeviceSelfTestResources.getString(58), 1152921504606846976L);
      this.setTitle(super._title);
      this.add(super._input);
      this.add(new SeparatorField());
      this.add(super._output);
      this.add(new SeparatorField());
      this.add(this._event);
      this.setupMenuItems();
      this._callback = new TestHolsterScreen$ReportListCallback();
      this._reportList = new ListField();
      this._reportList.setCallback(this._callback);
      this._reportList.setSearchable(false);
      this.add(this._reportList);
   }

   final void insertReport(int type) {
      String timeStamp = new Date(System.currentTimeMillis()).toString();
      String event;
      if (type == 1) {
         event = DeviceSelfTestResources.getString(59) + timeStamp.substring(0, 20);
      } else {
         event = DeviceSelfTestResources.getString(60) + timeStamp.substring(0, 20);
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
      Application.getApplication().invokeLater(new TestHolsterScreen$1(this));
   }

   @Override
   protected final void cleanup() {
      LED.setState(0, 0);
      super.app.removeHolsterListener((TestHolster)super.app.currentTest);
      super.app.removeKeyListener((TestHolster)super.app.currentTest);
      super.app.popScreen(super.app.getActiveScreen());
   }
}
