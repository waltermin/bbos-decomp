package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;

public final class DiagScreen extends MainScreen {
   Diag _manager;
   RunningScreen runningScreen;
   OptionsScreen optionScreen;
   private DiagScreen$ReportListCallback _callback;
   private ListField _reportList;
   LabelField _title;
   private MenuItem _runMenuItem;
   private MenuItem _viewReportMenuItem;
   private MenuItem _pinReportMenuItem;
   private MenuItem _emailReportMenuItem;
   private MenuItem _deleteMenuItem;
   private MenuItem _deleteAllMenuItem;
   private MenuItem _optionsMenuItem;

   DiagScreen(Diag diag) {
      this.setupMenuItems();
      this._manager = diag;
      this._title = (LabelField)(new Object(DiagnosticResources.getString(0), 1152921504606846976L));
      this.setTitle(this._title);
      this._callback = new DiagScreen$ReportListCallback();
      this._reportList = (ListField)(new Object());
      this._reportList.setCallback(this._callback);
      this._reportList.setSearchable(false);
      this.add(this._reportList);
      this.populate();
   }

   private final void setupMenuItems() {
      this._runMenuItem = new DiagScreen$1(this, DiagnosticResources.getString(18), 0, 5);
      this._viewReportMenuItem = new DiagScreen$2(this, DiagnosticResources.getString(19), 5, 0);
      this._deleteMenuItem = new DiagScreen$3(this, DiagnosticResources.getString(20), 10, 10);
      this._deleteAllMenuItem = new DiagScreen$4(this, DiagnosticResources.getString(21), 15, 15);
      this._emailReportMenuItem = new DiagScreen$5(this, DiagnosticResources.getString(8), 20, 20);
      this._pinReportMenuItem = new DiagScreen$6(this, DiagnosticResources.getString(9), 25, 25);
      this._optionsMenuItem = new DiagScreen$7(this, DiagnosticResources.getString(22), 30, 30);
   }

   final synchronized void populate() {
      this._callback.erase();
      int numReports = this._manager.reports.size();

      for (int i = 0; i < numReports; i++) {
         Object entry = this._manager.reports.elementAt(i);
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
   protected final void makeMenu(Menu menu, int instance) {
      label46: {
         super.makeMenu(menu, instance);
         if (this.runningScreen != null) {
            int var10000 = this.runningScreen.state;
            this.runningScreen.getClass();
            if (var10000 != 0) {
               break label46;
            }
         }

         if (instance != 65536 || this._callback.size() <= 0) {
            menu.add(this._runMenuItem);
         }
      }

      menu.addSeparator();
      Field focus = this.getLeafFieldWithFocus();
      if (focus != null) {
         menu.addSeparator();
         if (this._callback.size() > 0) {
            menu.add(this._viewReportMenuItem);
            label29:
            if (instance != 65536) {
               if (this.runningScreen != null) {
                  int var4 = this.runningScreen.state;
                  this.runningScreen.getClass();
                  if (var4 != 0) {
                     break label29;
                  }
               }

               menu.add(this._deleteMenuItem);
               if (this._callback.size() >= 2) {
                  menu.add(this._deleteAllMenuItem);
               }

               menu.addSeparator();
               menu.add(this._pinReportMenuItem);
               menu.add(this._emailReportMenuItem);
            }
         }

         menu.add(focus.getContextMenu(), true);
      }

      menu.addSeparator();
      if (instance != 65536) {
         menu.add(this._optionsMenuItem);
      }

      menu.setDefault(this._viewReportMenuItem);
   }

   public final void pinReport() {
      int index = this._reportList.getSelectedIndex();
      Report r = (Report)this._callback.get(this._reportList, index);
      if (this._manager.isPinRecptSetByITPolicy()) {
         new EmailUtil(0, ITPolicy.getString(46, 3), this._manager.subLineReport, r.toString()).start();
      } else {
         synchronized (Application.getEventLock()) {
            this._manager.sendReport(0, DiagOptions.getOptions().getPinRecpt(), this._manager.subLineReport, r.toString());
         }
      }
   }

   public final void emailReport() {
      int index = this._reportList.getSelectedIndex();
      Report r = (Report)this._callback.get(this._reportList, index);
      if (this._manager.isEmailRecptSetByITPolicy()) {
         new EmailUtil(1, ITPolicy.getString(46, 2), this._manager.subLineReport, r.toString()).start();
      } else {
         synchronized (Application.getEventLock()) {
            this._manager.sendReport(1, DiagOptions.getOptions().getEmailRecpt(), this._manager.subLineReport, r.toString());
         }
      }
   }

   public final void doView() {
      int index = this._reportList.getSelectedIndex();
      Object obj = this._callback.get(this._reportList, index);
      if (obj instanceof Report) {
         this._manager.pushScreen(new ReportScreen((Report)obj, this._manager));
      }

      if (obj instanceof DummyItem) {
         this._manager.pushScreen(this.runningScreen);
      }
   }

   public final void doDelete() {
      int index = this._reportList.getSelectedIndex();
      index = this._manager.reports.size() - 1 - index;
      int response = Dialog.ask(3, DiagnosticResources.getString(23), -1);
      if (4 == response) {
         this._manager.reports.removeElementAt(index);
         this._manager.saveReports();
      }
   }

   public final void doDeleteAll() {
      int response = Dialog.ask(3, DiagnosticResources.getString(24), -1);
      if (4 == response) {
         this._manager.reports.removeAllElements();
         this._manager.saveReports();
      }
   }

   public final void showOptions() {
      this._manager.pushScreen(new OptionsScreen(this._manager));
   }

   public final void doRun() {
      this.runningScreen = new RunningScreen(this._manager);
      this._manager.pushScreen(this.runningScreen);
      this.runningScreen.doRun();
   }

   @Override
   public final boolean onClose() {
      if (this.runningScreen != null && this.runningScreen.state == 1) {
         int response = Dialog.ask(3, DiagnosticResources.getString(25), -1);
         if (4 == response) {
            if (this._manager.reports.getLastElement() instanceof DummyItem) {
               this._manager.reports.removeLastElement();
            }

            this.systemExit();
            return true;
         } else {
            return false;
         }
      } else {
         this.systemExit();
         return true;
      }
   }

   private final void systemExit() {
      boolean appDisabled = ITPolicy.getBoolean(46, 1, false);
      if (appDisabled != Diag.appDisabled) {
         if (appDisabled) {
            VerbRepository repository1 = VerbRepository.getVerbRepository(-2430495530417912432L);
            Verb[] v = repository1.getVerbs(1);

            for (int i = 0; i < v.length; i++) {
               if (v[i] instanceof DiagnosticDisplayVerb) {
                  repository1.deregister(v[i], 4738722199580714034L);
                  break;
               }
            }

            Diag.appDisabled = true;
         } else {
            boolean isVerbRegistered = false;
            VerbRepository repository1 = VerbRepository.getVerbRepository(-2430495530417912432L);
            Verb[] v = repository1.getVerbs(1);

            for (int i = 0; i < v.length; i++) {
               if (v[i] instanceof DiagnosticDisplayVerb) {
                  isVerbRegistered = true;
                  break;
               }
            }

            if (!isVerbRegistered) {
               Diag.registerVerb();
               Diag.appDisabled = false;
            }
         }
      }

      System.exit(0);
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               if (this._callback.size() > 0) {
                  this.doView();
                  return true;
               }
         }
      }

      return handled;
   }
}
