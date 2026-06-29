package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Memory;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;

public final class RunningScreen$TestSuites extends Thread {
   RunningScreen screen;
   private final RunningScreen this$0;

   public RunningScreen$TestSuites(RunningScreen _1, RunningScreen _screen) {
      this.this$0 = _1;
      this.screen = _screen;
   }

   final void collectDeviceInfo() {
      this.this$0.report.setPin(Integer.toHexString(DeviceInfo.getDeviceId()));
      this.this$0.report.setDeviceType(DeviceInfo.getDeviceName());
      this.this$0.report.setAppVersion(this.getApplicationVersion());
      this.this$0.report.setPlatform(DeviceInfo.getPlatformVersion());
      this.this$0.report.freeSpace = Memory.getFlashFree();
      this.this$0.report.setMsisdn(PhoneUtilities.getDevicePhoneNumber());
      ServiceRecord[] sr = ServiceBook.getSB().getRecords();
      if (sr.length != 0) {
         StringBuffer buf = (StringBuffer)(new Object());

         for (int i = 0; i < sr.length; i++) {
            buf.append(sr[i].getCid());
            if (i != sr.length - 1) {
               buf.append(',');
            }
         }

         this.this$0.report.setServiceBook(buf.toString());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      try {
         this.this$0.state = 1;
         this.this$0._manager.reports.addElement(new DummyItem());
         this.this$0._manager.mainScreen.populate();
         this.updateScreen(this.this$0.RUNNING_TITLE);
         this.this$0.radioFinished = false;
         this.this$0.icmpPingFinished = false;
         this.this$0.bbRegFinished = false;
         this.this$0.mdpPingFinished = false;
         this.this$0.pin2pinPingFinished = false;
         this.this$0.emailServiceFinished = false;
         this.this$0.email1Finished = false;
         this.this$0.report.startTimeStamp = System.currentTimeMillis();
         this.collectDeviceInfo();
         this.updateScreen(this.this$0.RUNNING_TITLE);
         new TestRadio(this.screen).start();

         while (!this.this$0.radioFinished) {
            if (this.this$0.state == 0) {
               this.this$0.saveIncompleteReport();
               this.updateScreen(this.this$0.STOPPED_TITLE);
               return;
            }

            Thread.sleep(100);
         }

         if (this.this$0.report.radioActivation == 0) {
            this.this$0.report.endTimeStamp = System.currentTimeMillis();
            this.this$0.state = 0;
            this.updateScreen(this.this$0.COMPLETED_TITLE);
            this.this$0.saveReport();
         } else {
            this.collectRadioInfo();
            this.updateScreen(this.this$0.RUNNING_TITLE);

            while (!this.this$0.icmpPingFinished) {
               if (this.this$0.state == 0) {
                  this.this$0.saveIncompleteReport();
                  this.updateScreen(this.this$0.STOPPED_TITLE);
                  return;
               }

               Thread.sleep(100);
            }

            this.updateScreen(this.this$0.RUNNING_TITLE);
            new TestBBReg(this.screen).start();

            while (!this.this$0.bbRegFinished) {
               if (this.this$0.state == 0) {
                  this.this$0.saveIncompleteReport();
                  this.updateScreen(this.this$0.STOPPED_TITLE);
                  return;
               }

               Thread.sleep(100);
            }

            if (this.this$0.report.bbReg == 0) {
               this.this$0.report.endTimeStamp = System.currentTimeMillis();
               this.this$0.state = 0;
               this.updateScreen(this.this$0.COMPLETED_TITLE);
               this.this$0.saveReport();
            } else {
               this.updateScreen(this.this$0.RUNNING_TITLE);

               while (!this.this$0.mdpPingFinished) {
                  if (this.this$0.state == 0) {
                     this.this$0.saveIncompleteReport();
                     this.updateScreen(this.this$0.STOPPED_TITLE);
                     return;
                  }

                  Thread.sleep(100);
               }

               if (this.this$0.report.mdpPing == 0) {
                  this.this$0.report.endTimeStamp = System.currentTimeMillis();
                  this.this$0.state = 0;
                  this.updateScreen(this.this$0.COMPLETED_TITLE);
                  this.this$0.saveReport();
               } else {
                  this.updateScreen(this.this$0.RUNNING_TITLE);

                  while (!this.this$0.pin2pinPingFinished) {
                     if (this.this$0.state == 0) {
                        this.this$0.saveIncompleteReport();
                        this.updateScreen(this.this$0.STOPPED_TITLE);
                        return;
                     }

                     Thread.sleep(100);
                  }

                  if (this.this$0.report.pin2pinPing == 0) {
                     this.this$0.report.endTimeStamp = System.currentTimeMillis();
                     this.this$0.state = 0;
                     this.updateScreen(this.this$0.COMPLETED_TITLE);
                     this.this$0.saveReport();
                  } else {
                     this.updateScreen(this.this$0.RUNNING_TITLE);
                     if (this.this$0.hasEmail1) {
                        new TestEmailService(this.screen).start();

                        while (!this.this$0.email1Finished) {
                           if (this.this$0.state == 0) {
                              this.this$0.saveIncompleteReport();
                              this.updateScreen(this.this$0.STOPPED_TITLE);
                              return;
                           }

                           Thread.sleep(100);
                        }

                        this.updateScreen(this.this$0.RUNNING_TITLE);

                        while (!this.this$0.emailServiceFinished) {
                           if (this.this$0.state == 0) {
                              this.this$0.saveIncompleteReport();
                              this.updateScreen(this.this$0.STOPPED_TITLE);
                              return;
                           }

                           Thread.sleep(100);
                        }
                     }

                     this.this$0.report.endTimeStamp = System.currentTimeMillis();
                     this.this$0.state = 0;
                     this.updateScreen(this.this$0.COMPLETED_TITLE);
                     this.this$0.saveReport();
                  }
               }
            }
         }
      } catch (Throwable var3) {
         System.out.println(e.toString());
         return;
      }
   }

   final void collectRadioInfo() {
      int activeWAFs = RadioInfo.getActiveWAFs();
      int networkService = RadioInfo.getNetworkService();
      if ((activeWAFs & 1) != 0) {
         if ((networkService & 1024) != 0) {
            this.this$0.report.setNetworkType("EDGE");
         } else if ((networkService & 4096) != 0) {
            this.this$0.report.setNetworkType("UMTS");
         } else {
            this.this$0.report.setNetworkType("GPRS");
         }
      } else if ((activeWAFs & 2) != 0) {
         if ((networkService & 1024) != 0) {
            this.this$0.report.setNetworkType("EVDO");
         } else if ((networkService & 8192) != 0) {
            this.this$0.report.setNetworkType("EVDO Data Only");
         } else {
            this.this$0.report.setNetworkType("1X");
         }
      } else {
         this.this$0.report.setNetworkType(" Unknown");
      }

      this.screen.report.setNetwork(RadioInfo.getNetworkName(RadioInfo.getCurrentNetworkIndex()));
      this.screen.report.signalLevel = RadioInfo.getSignalLevel();
      byte[] ipAddress = RadioInfo.getIPAddress(this.this$0._apnId);
      StringBuffer buf = (StringBuffer)(new Object());
      buf.append(ipAddress[0] & 255);
      buf.append('.');
      buf.append(ipAddress[1] & 255);
      buf.append('.');
      buf.append(ipAddress[2] & 255);
      buf.append('.');
      buf.append(ipAddress[3] & 255);
      this.screen.report.setIp(buf.toString());
   }

   private final String getApplicationVersion() {
      int handle = CodeModuleManager.getModuleHandle("net_rim_os");
      return handle == 0 ? "" : ((StringBuffer)(new Object("v"))).append(CodeModuleManager.getModuleVersion(handle)).toString();
   }

   final void updateScreen(String title) {
      synchronized (Application.getEventLock()) {
         this.this$0._title.setText(title);
         SimpleDateFormat formatter = (SimpleDateFormat)(new Object("MMMMM dd, yyyy hh:mm aaa"));
         this.this$0._startTime.setText(formatter.format(new Object(this.this$0.report.startTimeStamp)));
         this.this$0._pin.setText(this.this$0.report.getPin());
         this.this$0._msisdn.setText(this.this$0.report.getMsisdn());
         this.this$0._deviceType.setText(((StringBuffer)(new Object("BlackBerry "))).append(this.this$0.report.getDeviceType()).toString());
         this.this$0._appVersion.setText(this.this$0.report.getAppVersion());
         this.this$0._platform.setText(this.this$0.report.getPlatform());
         this.this$0._serviceBook.setText(this.this$0.report.getServiceBook());
         this.this$0
            ._freeSpace
            .setText(
               this.this$0.report.freeSpace < 0 ? "unknown" : ((StringBuffer)(new Object())).append(this.this$0.report.freeSpace).append(" bytes").toString()
            );
         this.this$0
            ._signalLevel
            .setText(
               this.this$0.report.signalLevel == -99999
                  ? "Unknown"
                  : ((StringBuffer)(new Object())).append(this.this$0.report.signalLevel).append(" dBm").toString()
            );
         this.this$0._networkType.setText(this.this$0.report.getNetworkType());
         this.this$0._network.setText(this.this$0.report.getNetwork());
         this.this$0._ip.setText(this.this$0.report.getIp());
         if (this.this$0.state != 1) {
            switch (this.this$0.report.radioActivation) {
               case -2:
                  break;
               case -1:
               default:
                  this.this$0._radioActivation.setText("Incomplete");
                  break;
               case 0:
                  this.this$0._radioActivation.setText("No");
                  break;
               case 1:
                  this.this$0._radioActivation.setText("Yes");
            }

            switch (this.this$0.report.icmpPing) {
               case -2:
                  break;
               case -1:
               default:
                  this.this$0._icmpPing.setText("Incomplete");
                  break;
               case 0:
                  this.this$0._icmpPing.setText("No");
                  break;
               case 1:
                  this.this$0._icmpPing.setText("Yes");
            }

            switch (this.this$0.report.bbReg) {
               case -2:
                  break;
               case -1:
               default:
                  this.this$0._bbReg.setText("Incomplete");
                  break;
               case 0:
                  this.this$0._bbReg.setText("No");
                  break;
               case 1:
                  this.this$0._bbReg.setText("Yes");
            }

            switch (this.this$0.report.mdpPing) {
               case -2:
                  break;
               case -1:
               default:
                  this.this$0._mdpPing.setText("Incomplete");
                  break;
               case 0:
                  this.this$0._mdpPing.setText("No");
                  break;
               case 1:
                  this.this$0._mdpPing.setText("Yes");
            }

            switch (this.this$0.report.pin2pinPing) {
               case -2:
                  break;
               case -1:
               default:
                  this.this$0._pin2pinPing.setText("Incomplete");
                  break;
               case 0:
                  this.this$0._pin2pinPing.setText("No");
                  break;
               case 1:
                  this.this$0._pin2pinPing.setText("Yes");
            }

            if (this.this$0.hasEmail1) {
               switch (this.this$0.report.emailService1) {
                  case -2:
                     break;
                  case -1:
                  default:
                     this.this$0._emailService1.setText("Incomplete");
                     break;
                  case 0:
                     this.this$0._emailService1.setText("No");
                     break;
                  case 1:
                     this.this$0._emailService1.setText("Yes");
               }
            }

            if (this.this$0.hasEmail2) {
               switch (this.this$0.report.emailService2) {
                  case -2:
                     break;
                  case -1:
                  default:
                     this.this$0._emailService2.setText("Incomplete");
                     break;
                  case 0:
                     this.this$0._emailService2.setText("No");
                     break;
                  case 1:
                     this.this$0._emailService2.setText("Yes");
               }
            }

            if (this.this$0.report.endTimeStamp > 0) {
               this.this$0._endTime.setText(formatter.format(new Object(this.this$0.report.endTimeStamp)));
            } else {
               this.this$0._endTime.setText("Incomplete");
            }
         } else {
            this.this$0._radioActivation.setText(this.this$0.report.radioActivation == 1 ? "Yes" : " ");
            if (!this.this$0.icmpPingFinished) {
               this.this$0._icmpPing.setText(this.this$0.report.icmpPing == 1 ? "Yes" : " ");
            } else {
               this.this$0._icmpPing.setText(this.this$0.report.icmpPing == 1 ? "Yes" : "No");
            }

            this.this$0._bbReg.setText(this.this$0.report.bbReg == 1 ? "Yes" : "");
            this.this$0._mdpPing.setText(this.this$0.report.mdpPing == 1 ? "Yes" : " ");
            this.this$0._pin2pinPing.setText(this.this$0.report.pin2pinPing == 1 ? "Yes" : " ");
            if (this.this$0.hasEmail1) {
               if (!this.this$0.email1Finished) {
                  this.this$0._emailService1.setText(this.this$0.report.emailService1 == 1 ? "Yes" : " ");
               } else {
                  this.this$0._emailService1.setText(this.this$0.report.emailService1 == 1 ? "Yes" : "No");
               }
            }

            if (this.this$0.hasEmail2) {
               this.this$0._emailService2.setText(this.this$0.report.emailService2 == 1 ? "Yes" : " ");
            }
         }
      }
   }
}
