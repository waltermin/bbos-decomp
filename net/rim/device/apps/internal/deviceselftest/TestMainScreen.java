package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

public final class TestMainScreen extends MainScreen {
   DeviceSelfTest app = (DeviceSelfTest)Application.getApplication();
   ConfigScreen configScreen;
   private TestMainScreen$ReportListCallback _callback;
   private ListField _reportList;
   LabelField _title;
   private MenuItem _startMenuItem;
   private MenuItem _viewReportMenuItem;
   private MenuItem _deleteMenuItem;
   private MenuItem _deleteAllMenuItem;

   TestMainScreen() {
      this.setupMenuItems();
      this._title = new LabelField(DeviceSelfTestResources.getString(5), 1152921504606846976L);
      this.setTitle(this._title);
      this._callback = new TestMainScreen$ReportListCallback();
      this._reportList = new ListField();
      this._reportList.setCallback(this._callback);
      this._reportList.setSearchable(false);
      this.add(this._reportList);
      this.populate();
      Application.getApplication().invokeLater(new TestMainScreen$1(this));
   }

   final void populate() {
      this._callback.erase();
      int numReports = this.app.reports.size();

      for (int i = 0; i < numReports; i++) {
         Object entry = this.app.reports.elementAt(i);
         this._callback.insert(entry, i);
         synchronized (Application.getEventLock()) {
            this._reportList.insert(i);
         }
      }

      synchronized (Application.getEventLock()) {
         this._reportList.setSize(this._callback.size());
      }
   }

   private final void setupMenuItems() {
      this._startMenuItem = new TestMainScreen$2(this, DeviceSelfTestResources.getString(0), 0, 5);
      this._viewReportMenuItem = new TestMainScreen$3(this, DeviceSelfTestResources.getString(1), 5, 0);
      this._deleteMenuItem = new TestMainScreen$4(this, DeviceSelfTestResources.getString(2), 10, 10);
      this._deleteAllMenuItem = new TestMainScreen$5(this, DeviceSelfTestResources.getString(3), 15, 15);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(this._startMenuItem);
      Field focus = this.getLeafFieldWithFocus();
      if (focus != null) {
         menu.addSeparator();
         if (this._callback.size() > 0) {
            menu.add(this._viewReportMenuItem);
            menu.add(this._deleteMenuItem);
            if (this._callback.size() >= 2) {
               menu.add(this._deleteAllMenuItem);
            }
         }

         menu.add(focus.getContextMenu(), true);
      }

      menu.addSeparator();
      menu.setDefault(this._viewReportMenuItem);
   }

   final void doStart() {
      if (this.configScreen == null) {
         this.configScreen = new ConfigScreen();
      }

      this.app.pushScreen(this.configScreen);
   }

   final void doView() {
      int index = this._reportList.getSelectedIndex();
      Object obj = this._callback.get(this._reportList, index);
      if (obj instanceof Report) {
         this.app.pushScreen(new ReportScreen((Report)obj, this.app, index));
      }
   }

   final void doDelete() {
      int index = this._reportList.getSelectedIndex();
      index = this.app.reports.size() - 1 - index;
      int response = Dialog.ask(3, DeviceSelfTestResources.getString(2), -1);
      if (4 == response) {
         this.app.reports.removeElementAt(index);
         this.app.saveReports();
      }
   }

   final void doDeleteAll() {
      int response = Dialog.ask(3, DeviceSelfTestResources.getString(3), -1);
      if (4 == response) {
         this.app.reports.removeAllElements();
         this.app.saveReports();
      }
   }

   @Override
   public final boolean onClose() {
      int response = Dialog.ask(3, DeviceSelfTestResources.getString(120), -1);
      if (4 == response) {
         System.exit(0);
         return true;
      } else {
         return false;
      }
   }
}
