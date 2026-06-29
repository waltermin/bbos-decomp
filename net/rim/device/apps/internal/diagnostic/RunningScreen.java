package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;

public final class RunningScreen extends ReportScreen {
   final String RUNNING_TITLE = DiagnosticResources.getString(4);
   final String STOPPED_TITLE = DiagnosticResources.getString(6);
   final String COMPLETED_TITLE = DiagnosticResources.getString(5);
   public Diag _manager;
   public final int STOPPED = 0;
   public final int RUNNING = 1;
   public int state;
   public int _apnId;
   public boolean radioFinished;
   public boolean icmpPingFinished;
   public boolean bbRegFinished;
   public boolean mdpPingFinished;
   public boolean pin2pinPingFinished;
   public boolean emailServiceFinished;
   public boolean email1Finished;
   RunningScreen$TestSuites tThread;
   RunningScreen$Blinking bThread;
   static long startTimeStamp;
   static long endTimeStamp;

   RunningScreen(Diag d) {
      this._manager = d;
      this.state = 0;
      this.initReport();
      this.initScreen();
      this.setupMenuItems();
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      menu.deleteAll();
      if (this.state == 0) {
         menu.add(super._runMenuItem);
         menu.add(super._emailReportMenuItem);
         menu.add(super._pinReportMenuItem);
      } else {
         menu.add(super._stopMenuItem);
      }
   }

   public final void initReport() {
      super.report = new Report();
      ServiceRecord[] sr = ServiceBook.getSB().findRecordsByCid("CMIME");
      if (sr.length == 0) {
         super.hasEmail1 = false;
         super.hasEmail2 = false;
      }

      if (sr.length == 1) {
         super.serverName1 = sr[0].getUid();
         super.emailAddress1 = CMIMEUtilities.getEmailAddress(sr[0]);
         super.report.setServerName1(super.serverName1);
         super.report.setEmailAddress1(super.emailAddress1);
         super.hasEmail1 = true;
         super.hasEmail2 = false;
      }

      if (sr.length >= 2) {
         super.serverName1 = sr[0].getUid();
         super.emailAddress1 = CMIMEUtilities.getEmailAddress(sr[0]);
         super.report.setServerName1(super.serverName1);
         super.report.setEmailAddress1(super.emailAddress1);
         super.hasEmail1 = true;
         String uid = sr[0].getUid();

         for (int i = 1; i < sr.length; i++) {
            if (!sr[i].getUid().equalsIgnoreCase(uid)) {
               super.serverName2 = sr[i].getUid();
               super.emailAddress2 = CMIMEUtilities.getEmailAddress(sr[i]);
               super.report.setServerName2(super.serverName2);
               super.report.setEmailAddress2(super.emailAddress2);
               super.hasEmail2 = true;
               return;
            }
         }
      }
   }

   @Override
   public final void doRun() {
      try {
         if (this.tThread == null) {
            new RunningScreen$TestLauncher(this, this).start();
         } else if (!this.tThread.isAlive()) {
            new RunningScreen$TestLauncher(this, this).start();
            return;
         }
      } finally {
         return;
      }
   }

   @Override
   public final void doStop() {
      this.state = 0;
   }

   @Override
   final void doEmailReport() {
      if (this._manager.isEmailRecptSetByITPolicy()) {
         new EmailUtil(1, ITPolicy.getString(46, 2), this._manager.subLineReport, super.report.toString()).start();
      } else {
         synchronized (Application.getEventLock()) {
            this._manager.sendReport(1, DiagOptions.getOptions().getEmailRecpt(), this._manager.subLineReport, super.report.toString());
         }
      }
   }

   @Override
   final void doPinReport() {
      if (this._manager.isPinRecptSetByITPolicy()) {
         new EmailUtil(0, ITPolicy.getString(46, 3), this._manager.subLineReport, super.report.toString()).start();
      } else {
         synchronized (Application.getEventLock()) {
            this._manager.sendReport(0, DiagOptions.getOptions().getPinRecpt(), this._manager.subLineReport, super.report.toString());
         }
      }
   }

   public final void saveReport() {
      this._manager.reports.removeLastElement();
      this._manager.reports.addElement(super.report);
      this._manager.saveReports();
   }

   public final void saveIncompleteReport() {
      if (!this.radioFinished) {
         super.report.radioActivation = -1;
         super.report.icmpPing = -1;
         super.report.bbReg = -1;
         super.report.mdpPing = -1;
         super.report.pin2pinPing = -1;
         if (super.hasEmail1) {
            super.report.emailService1 = -1;
         }

         if (super.hasEmail2) {
            super.report.emailService2 = -1;
         }
      } else if (!this.icmpPingFinished) {
         super.report.icmpPing = -1;
         super.report.bbReg = -1;
         super.report.mdpPing = -1;
         super.report.pin2pinPing = -1;
         if (super.hasEmail1) {
            super.report.emailService1 = -1;
         }

         if (super.hasEmail2) {
            super.report.emailService2 = -1;
         }
      } else if (!this.bbRegFinished) {
         super.report.bbReg = -1;
         super.report.mdpPing = -1;
         super.report.pin2pinPing = -1;
         if (super.hasEmail1) {
            super.report.emailService1 = -1;
         }

         if (super.hasEmail2) {
            super.report.emailService2 = -1;
         }
      } else if (!this.mdpPingFinished) {
         super.report.mdpPing = -1;
         super.report.pin2pinPing = -1;
         if (super.hasEmail1) {
            super.report.emailService1 = -1;
         }

         if (super.hasEmail2) {
            super.report.emailService2 = -1;
         }
      } else if (!this.pin2pinPingFinished) {
         super.report.pin2pinPing = -1;
         if (super.hasEmail1) {
            super.report.emailService1 = -1;
         }

         if (super.hasEmail2) {
            super.report.emailService2 = -1;
         }
      } else if (super.hasEmail1 && !this.email1Finished) {
         super.report.emailService1 = -1;
         if (super.hasEmail2) {
            super.report.emailService2 = -1;
         }
      } else if (super.hasEmail2 && !this.emailServiceFinished) {
         super.report.emailService2 = -1;
      }

      this.saveReport();
   }
}
