package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

public class ReportScreen extends MainScreen {
   Report report;
   Diag _manager;
   LabelField _title;
   EditField _startTime;
   EditField _endTime;
   EditField _pin;
   EditField _msisdn;
   EditField _deviceType;
   EditField _appVersion;
   EditField _platform;
   EditField _serviceBook;
   EditField _freeSpace;
   EditField _radioActivation;
   EditField _signalLevel;
   EditField _networkType;
   EditField _network;
   EditField _ip;
   EditField _icmpPing;
   EditField _bbReg;
   EditField _mdpPing;
   EditField _pin2pinPing;
   boolean hasEmail1;
   boolean hasEmail2;
   EditField _serverName1;
   EditField _serverName2;
   EditField _emailService1;
   EditField _emailService2;
   EditField _emailAddress1;
   EditField _emailAddress2;
   String emailAddress1;
   String serverName1;
   String emailAddress2;
   String serverName2;
   protected MenuItem _emailReportMenuItem;
   protected MenuItem _pinReportMenuItem;
   protected MenuItem _runMenuItem;
   protected MenuItem _stopMenuItem;

   ReportScreen() {
   }

   ReportScreen(Report reportToDisplay, Diag manager) {
      this.report = reportToDisplay;
      this._manager = manager;
      if (this.report.getServerName1().equalsIgnoreCase(" Unknown")) {
         this.hasEmail1 = false;
      } else {
         this.serverName1 = this.report.getServerName1();
         this.emailAddress1 = this.report.getEmailAddress1();
         this.hasEmail1 = true;
      }

      if (this.report.getServerName2().equalsIgnoreCase(" Unknown")) {
         this.hasEmail2 = false;
      } else {
         this.serverName2 = this.report.getServerName2();
         this.emailAddress2 = this.report.getEmailAddress2();
         this.hasEmail2 = true;
      }

      this.setupMenuItems();
      this.initScreen();
      this.displayReport();
   }

   protected void setupMenuItems() {
      this._runMenuItem = new ReportScreen$1(this, DiagnosticResources.getString(7), 0, 0);
      this._emailReportMenuItem = new ReportScreen$2(this, DiagnosticResources.getString(8), 20, 20);
      this._pinReportMenuItem = new ReportScreen$3(this, DiagnosticResources.getString(9), 25, 25);
      this._stopMenuItem = new ReportScreen$4(this, DiagnosticResources.getString(10), 30, 30);
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(this._emailReportMenuItem);
      menu.add(this._pinReportMenuItem);
   }

   void doRun() {
      this._manager.popScreen(this);
      this._manager.mainScreen.doRun();
   }

   void doStop() {
   }

   void doEmailReport() {
      if (this._manager.isEmailRecptSetByITPolicy()) {
         new EmailUtil(1, ITPolicy.getString(46, 2), this._manager.subLineReport, this.report.toString()).start();
      } else {
         synchronized (Application.getEventLock()) {
            this._manager.sendReport(1, DiagOptions.getOptions().getEmailRecpt(), this._manager.subLineReport, this.report.toString());
         }
      }
   }

   void doPinReport() {
      if (this._manager.isPinRecptSetByITPolicy()) {
         new EmailUtil(0, ITPolicy.getString(46, 3), this._manager.subLineReport, this.report.toString()).start();
      } else {
         synchronized (Application.getEventLock()) {
            this._manager.sendReport(0, DiagOptions.getOptions().getPinRecpt(), this._manager.subLineReport, this.report.toString());
         }
      }
   }

   void initScreen() {
      this._title = (LabelField)(new Object(DiagnosticResources.getString(28), 1152921504606846976L));
      this.setTitle(this._title);
      this._radioActivation = (EditField)(new Object(DiagnosticResources.getString(29), null));
      this._radioActivation.setEditable(false);
      this.add(this._radioActivation);
      this._signalLevel = (EditField)(new Object(DiagnosticResources.getString(30), null));
      this._signalLevel.setEditable(false);
      this.add(this._signalLevel);
      this._networkType = (EditField)(new Object(DiagnosticResources.getString(31), null));
      this._networkType.setEditable(false);
      this.add(this._networkType);
      this._network = (EditField)(new Object(DiagnosticResources.getString(32), null));
      this._network.setEditable(false);
      this.add(this._network);
      this._ip = (EditField)(new Object(DiagnosticResources.getString(33), null));
      this._ip.setEditable(false);
      this.add(this._ip);
      this._icmpPing = (EditField)(new Object(DiagnosticResources.getString(34), null));
      this._icmpPing.setEditable(false);
      this.add(this._icmpPing);
      this.add((Field)(new Object()));
      this._bbReg = (EditField)(new Object(DiagnosticResources.getString(35), null));
      this._bbReg.setEditable(false);
      this.add(this._bbReg);
      this._mdpPing = (EditField)(new Object(DiagnosticResources.getString(36), null));
      this._mdpPing.setEditable(false);
      this.add(this._mdpPing);
      this._pin2pinPing = (EditField)(new Object(DiagnosticResources.getString(37), null));
      this._pin2pinPing.setEditable(false);
      this.add(this._pin2pinPing);
      this.add((Field)(new Object()));
      if (this.hasEmail1) {
         this._serverName1 = (EditField)(new Object(DiagnosticResources.getString(38), this.serverName1));
         this._serverName1.setEditable(false);
         this.add(this._serverName1);
         this._emailAddress1 = (EditField)(new Object(DiagnosticResources.getString(39), this.emailAddress1));
         this._emailAddress1.setEditable(false);
         this.add(this._emailAddress1);
         this._emailService1 = (EditField)(new Object(
            ((StringBuffer)(new Object())).append(DiagnosticResources.getString(40)).append(this.emailAddress1).append(" : ").toString(), null
         ));
         this._emailService1.setEditable(false);
         this.add(this._emailService1);
      }

      if (this.hasEmail2) {
         this._serverName2 = (EditField)(new Object(DiagnosticResources.getString(38), this.serverName2));
         this._serverName2.setEditable(false);
         this.add(this._serverName2);
         this._emailAddress2 = (EditField)(new Object(DiagnosticResources.getString(39), this.emailAddress2));
         this._emailAddress2.setEditable(false);
         this.add(this._emailAddress2);
         this._emailService2 = (EditField)(new Object(
            ((StringBuffer)(new Object())).append(DiagnosticResources.getString(40)).append(this.emailAddress2).append(" : ").toString(), null
         ));
         this._emailService2.setEditable(false);
         this.add(this._emailService2);
      }

      if (this.hasEmail1 || this.hasEmail2) {
         this.add((Field)(new Object()));
      }

      this._pin = (EditField)(new Object("PIN: ", null));
      this._pin.setEditable(false);
      this.add(this._pin);
      this._msisdn = (EditField)(new Object("MSISDN: ", null));
      this._msisdn.setEditable(false);
      this.add(this._msisdn);
      this._deviceType = (EditField)(new Object(DiagnosticResources.getString(43), null));
      this._deviceType.setEditable(false);
      this.add(this._deviceType);
      this._appVersion = (EditField)(new Object(DiagnosticResources.getString(44), null));
      this._appVersion.setEditable(false);
      this.add(this._appVersion);
      this._platform = (EditField)(new Object(DiagnosticResources.getString(45), null));
      this._platform.setEditable(false);
      this.add(this._platform);
      this._serviceBook = (EditField)(new Object(DiagnosticResources.getString(46), null));
      this._serviceBook.setEditable(false);
      this.add(this._serviceBook);
      this._freeSpace = (EditField)(new Object(DiagnosticResources.getString(47), null));
      this._freeSpace.setEditable(false);
      this.add(this._freeSpace);
      this.add((Field)(new Object()));
      this._startTime = (EditField)(new Object(DiagnosticResources.getString(48), null));
      this._startTime.setEditable(false);
      this.add(this._startTime);
      this._endTime = (EditField)(new Object(DiagnosticResources.getString(49), null));
      this._endTime.setEditable(false);
      this.add(this._endTime);
   }

   void displayReport() {
      SimpleDateFormat formatter = (SimpleDateFormat)(new Object("MMMMM dd, yyyy hh:mm aaa"));
      this._startTime.setText(formatter.format(new Object(this.report.startTimeStamp)));
      if (this.report.endTimeStamp == -1) {
         this._endTime.setText(DiagnosticResources.getString(50));
      } else {
         this._endTime.setText(formatter.format(new Object(this.report.endTimeStamp)));
      }

      this._pin.setText(this.report.getPin());
      this._msisdn.setText(this.report.getMsisdn());
      this._deviceType.setText(((StringBuffer)(new Object("BlackBerry "))).append(this.report.getDeviceType()).toString());
      this._appVersion.setText(this.report.getAppVersion());
      this._platform.setText(this.report.getPlatform());
      this._serviceBook.setText(this.report.getServiceBook());
      this._freeSpace
         .setText(
            this.report.freeSpace < 0
               ? DiagnosticResources.getString(52)
               : ((StringBuffer)(new Object())).append(this.report.freeSpace).append(DiagnosticResources.getString(53)).toString()
         );
      this._signalLevel
         .setText(
            this.report.signalLevel == -99999
               ? DiagnosticResources.getString(52)
               : ((StringBuffer)(new Object())).append(this.report.signalLevel).append(" dBm").toString()
         );
      this._networkType.setText(this.report.getNetworkType());
      this._network.setText(this.report.getNetwork());
      this._ip.setText(this.report.getIp());
      switch (this.report.radioActivation) {
         case -2:
            break;
         case -1:
         default:
            this._radioActivation.setText(DiagnosticResources.getString(50));
            break;
         case 0:
            this._radioActivation.setText(DiagnosticResources.getString(55));
            break;
         case 1:
            this._radioActivation.setText(DiagnosticResources.getString(54));
      }

      switch (this.report.icmpPing) {
         case -2:
            break;
         case -1:
         default:
            this._icmpPing.setText(DiagnosticResources.getString(50));
            break;
         case 0:
            this._icmpPing.setText(DiagnosticResources.getString(55));
            break;
         case 1:
            this._icmpPing.setText(DiagnosticResources.getString(54));
      }

      switch (this.report.bbReg) {
         case -2:
            break;
         case -1:
         default:
            this._bbReg.setText(DiagnosticResources.getString(50));
            break;
         case 0:
            this._bbReg.setText(DiagnosticResources.getString(55));
            break;
         case 1:
            this._bbReg.setText(DiagnosticResources.getString(54));
      }

      switch (this.report.mdpPing) {
         case -2:
            break;
         case -1:
         default:
            this._mdpPing.setText(DiagnosticResources.getString(50));
            break;
         case 0:
            this._mdpPing.setText(DiagnosticResources.getString(55));
            break;
         case 1:
            this._mdpPing.setText(DiagnosticResources.getString(54));
      }

      switch (this.report.pin2pinPing) {
         case -2:
            break;
         case -1:
         default:
            this._pin2pinPing.setText(DiagnosticResources.getString(50));
            break;
         case 0:
            this._pin2pinPing.setText(DiagnosticResources.getString(55));
            break;
         case 1:
            this._pin2pinPing.setText(DiagnosticResources.getString(54));
      }

      if (this.hasEmail1) {
         switch (this.report.emailService1) {
            case -2:
               break;
            case -1:
            default:
               this._emailService1.setText(DiagnosticResources.getString(50));
               break;
            case 0:
               this._emailService1.setText(DiagnosticResources.getString(55));
               break;
            case 1:
               this._emailService1.setText(DiagnosticResources.getString(54));
         }
      }

      if (this.hasEmail2) {
         switch (this.report.emailService2) {
            case -2:
               break;
            case -1:
            default:
               this._emailService2.setText(DiagnosticResources.getString(50));
               return;
            case 0:
               this._emailService2.setText(DiagnosticResources.getString(55));
               return;
            case 1:
               this._emailService2.setText(DiagnosticResources.getString(54));
         }
      }
   }
}
