package net.rim.device.apps.internal.deviceselftest;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.Menu;

final class TestTrackballScreen extends TestScreenBase {
   TestTrackballScreen$ReportListCallback _callback;
   ListField _reportList;
   LabelField _event;
   Vector reports = (Vector)(new Object());

   TestTrackballScreen() {
      super._title = (LabelField)(new Object(DeviceSelfTestResources.getString(101), 1152921504606846976L));
      super._input = (LabelField)(new Object(DeviceSelfTestResources.getString(102), 1152921504606846976L));
      super._output = (LabelField)(new Object(DeviceSelfTestResources.getString(103), 1152921504606846976L));
      this._event = (LabelField)(new Object(DeviceSelfTestResources.getString(104), 1152921504606846976L));
      this.setTitle(super._title);
      this.add(super._input);
      this.add((Field)(new Object()));
      this.add(super._output);
      this.add((Field)(new Object()));
      this.add(this._event);
      this._callback = new TestTrackballScreen$ReportListCallback();
      this._reportList = (ListField)(new Object());
      this._reportList.setCallback(this._callback);
      this._reportList.setSearchable(false);
      this.add(this._reportList);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
   }

   final void insertReport(String msg) {
      if (this.reports.size() == 5) {
         this.reports.removeElementAt(0);
      }

      this.reports.addElement(msg);
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
      Application.getApplication().invokeLater(new TestTrackballScreen$1(this));
   }

   @Override
   protected final void cleanup() {
      super.app.removeKeyListener(super.app.currentTest);
      super.app.popScreen(super.app.getActiveScreen());
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      String msg = "Trackball Click!";
      this.insertReport(msg);
      return true;
   }

   @Override
   public final boolean navigationMovement(int dx, int dy, int status, int time) {
      String msg = "Trackball Roll : ";
      if (dx < 0) {
         msg = ((StringBuffer)(new Object())).append(msg).append("left ").append(-dx).toString();
      } else if (dx > 0) {
         msg = ((StringBuffer)(new Object())).append(msg).append("rigt ").append(dx).toString();
      }

      if (dx != 0 && dy != 0) {
         msg = ((StringBuffer)(new Object())).append(msg).append(" , ").toString();
      }

      if (dy < 0) {
         msg = ((StringBuffer)(new Object())).append(msg).append("up ").append(-dy).toString();
      } else if (dy > 0) {
         msg = ((StringBuffer)(new Object())).append(msg).append("down ").append(dy).toString();
      }

      this.insertReport(msg);
      return true;
   }

   @Override
   public final boolean navigationUnclick(int status, int time) {
      String msg = "Trackball Unclick!";
      this.insertReport(msg);
      return true;
   }
}
