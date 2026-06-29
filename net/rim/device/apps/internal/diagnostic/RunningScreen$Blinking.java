package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.system.Application;

public final class RunningScreen$Blinking extends Thread {
   RunningScreen screen;
   boolean flashFlag;
   private final RunningScreen this$0;

   public RunningScreen$Blinking(RunningScreen _1, RunningScreen _screen) {
      this.this$0 = _1;
      this.screen = _screen;
      this.flashFlag = false;
   }

   @Override
   public final void run() {
      while (this.screen.state == 1) {
         if (!this.this$0.radioFinished) {
            synchronized (Application.getEventLock()) {
               if (this.flashFlag) {
                  this.screen._radioActivation.setText("  ...");
                  this.flashFlag = false;
               } else {
                  this.screen._radioActivation.setText(" ");
                  this.flashFlag = true;
               }
            }
         } else if (!this.this$0.icmpPingFinished) {
            synchronized (Application.getEventLock()) {
               if (this.flashFlag) {
                  this.screen._icmpPing.setText("  ...");
                  this.flashFlag = false;
               } else {
                  this.screen._icmpPing.setText(" ");
                  this.flashFlag = true;
               }
            }
         } else if (!this.this$0.bbRegFinished) {
            synchronized (Application.getEventLock()) {
               if (this.flashFlag) {
                  this.screen._bbReg.setText("  ...");
                  this.flashFlag = false;
               } else {
                  this.screen._bbReg.setText(" ");
                  this.flashFlag = true;
               }
            }
         } else if (!this.this$0.mdpPingFinished) {
            synchronized (Application.getEventLock()) {
               if (this.flashFlag) {
                  this.screen._mdpPing.setText("  ...");
                  this.flashFlag = false;
               } else {
                  this.screen._mdpPing.setText(" ");
                  this.flashFlag = true;
               }
            }
         } else if (!this.this$0.pin2pinPingFinished) {
            synchronized (Application.getEventLock()) {
               if (this.flashFlag) {
                  this.screen._pin2pinPing.setText("  ...");
                  this.flashFlag = false;
               } else {
                  this.screen._pin2pinPing.setText(" ");
                  this.flashFlag = true;
               }
            }
         } else if (this.this$0.hasEmail1 && !this.this$0.email1Finished) {
            synchronized (Application.getEventLock()) {
               if (this.flashFlag) {
                  this.screen._emailService1.setText("  ...");
                  this.flashFlag = false;
               } else {
                  this.screen._emailService1.setText(" ");
                  this.flashFlag = true;
               }
            }
         } else if (this.this$0.hasEmail2 && !this.this$0.emailServiceFinished) {
            synchronized (Application.getEventLock()) {
               if (this.flashFlag) {
                  this.screen._emailService2.setText("  ...");
                  this.flashFlag = false;
               } else {
                  this.screen._emailService2.setText(" ");
                  this.flashFlag = true;
               }
            }
         }

         try {
            Thread.sleep(300);
         } finally {
            continue;
         }
      }
   }
}
