package net.rim.device.apps.internal.diagnostic;

import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.io.Connector;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceListener;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.RIMMessagingIncomingMessage;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailBuilderApi;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.util.CalendarExtensions;

public final class TestBBReg extends Thread implements TransmissionServiceListener {
   private RunningScreen screen;
   boolean timeoutFlag;
   boolean flashFlag;
   boolean responseFlag;
   Diag manager;
   Timer timer;
   TimerTask bbRegTimeout;
   TimerTask pin2pinTimeout;
   TimerTask mdpPingTimeout;
   Calendar cal;
   Report report;
   String subject;
   String bodyStr;
   final String[] regResponseStr = new String[]{
      "ERRs-0x0000000000000004",
      "ERRs 0x0",
      "ERRs 0x1",
      "ERRs 0x2",
      "ERRs 0x3",
      "ERRs 0x4",
      "ERRs 0x5",
      "ERRs 0x6",
      "ERRs 0x7",
      "ERRs 0x8",
      "ERRs 0x9",
      "ERRs 0x10",
      "ERRs 0x11",
      "ERRs 0x12",
      "ERRs 0x13",
      "ERRs 0x14",
      "ERRs 0x15",
      "ERRs 0x16",
      "ERRs 0x17",
      "ERRs 0x18",
      "ERRs 0x19",
      "ERRs 0x20"
   };
   final String regResponseReceiverName = "net.rim.hrtRT";
   final String[] mdpResponseStr = new String[]{"rppi"};
   final String mdpResponseReceiverName = "net.rim.mdp";
   TransmissionService transmissionService;

   TestBBReg(RunningScreen _screen) {
      this.screen = _screen;
      this.report = this.screen.report;
      this.cal = Calendar.getInstance();
      this.manager = this.screen._manager;
   }

   @Override
   public final void run() {
      this.timeoutFlag = false;
      this.screen.state = 1;
      this.bbRegRequest();
      if (this.report.bbReg != 0) {
         int var10000 = this.screen.state;
         this.screen.getClass();
         if (var10000 != 0) {
            this.mdpPing();
            if (this.report.mdpPing != 0) {
               var10000 = this.screen.state;
               this.screen.getClass();
               if (var10000 != 0) {
                  this.pin2pinPing();
               }
            }
         }
      }
   }

   private final void bbRegRequest() {
      try {
         EventLogScanner scanner = null;
         this.responseFlag = false;
         long startTimeStamp = ((CalendarExtensions)this.cal).getTimeLong();
         HRUtils.getThunks().sendRegistrationRequest();
         this.timer = new Timer();
         this.bbRegTimeout = new TestBBReg$bbRegRequestExpired(this);
         this.timer.schedule(this.bbRegTimeout, 30000);

         while (!this.responseFlag) {
            if (this.screen.state == 1) {
               if (scanner == null) {
                  scanner = new EventLogScanner(this, "net.rim.hrtRT", this.regResponseStr, startTimeStamp);
                  scanner.start();
               } else if (scanner != null && !scanner.isAlive()) {
                  scanner = new EventLogScanner(this, "net.rim.hrtRT", this.regResponseStr, startTimeStamp);
                  scanner.start();
               }
            }

            Thread.sleep(1000);
            if (this.timeoutFlag && this.screen.state == 1) {
               this.screen.bbRegFinished = true;
               this.screen.report.bbReg = 0;
               return;
            }

            int var10000 = this.screen.state;
            this.screen.getClass();
            if (var10000 == 0) {
               return;
            }
         }

         this.timer.cancel();
         this.screen.bbRegFinished = true;
         this.screen.report.bbReg = 1;
      } finally {
         return;
      }
   }

   private final void mdpPing() {
      try {
         EventLogScanner scanner = null;
         this.responseFlag = false;
         long startTimeStamp = ((CalendarExtensions)this.cal).getTimeLong();
         this.sendMdpPing();
         this.timer = new Timer();
         this.mdpPingTimeout = new TestBBReg$mdpPingExpired(this);
         this.timer.schedule(this.mdpPingTimeout, 15000);

         while (!this.responseFlag) {
            if (this.screen.state == 1) {
               if (scanner == null) {
                  scanner = new EventLogScanner(this, "net.rim.mdp", this.mdpResponseStr, startTimeStamp);
                  scanner.start();
               } else if (scanner != null && !scanner.isAlive()) {
                  scanner = new EventLogScanner(this, "net.rim.mdp", this.mdpResponseStr, startTimeStamp);
                  scanner.start();
               }
            }

            Thread.sleep(1000);
            if (this.timeoutFlag && this.screen.state == 1) {
               this.screen.mdpPingFinished = true;
               this.screen.report.mdpPing = 0;
               return;
            }

            int var10000 = this.screen.state;
            this.screen.getClass();
            if (var10000 == 0) {
               return;
            }
         }

         this.timer.cancel();
         this.screen.mdpPingFinished = true;
         this.screen.report.mdpPing = 1;
      } finally {
         return;
      }
   }

   private final void pin2pinPing() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: aload 0
      // 01: ldc2_w 8399767144006445082
      // 04: invokestatic net/rim/device/apps/api/transmission/TransmissionServiceManager.get (J)Lnet/rim/device/apps/api/transmission/TransmissionService;
      // 07: putfield net/rim/device/apps/internal/diagnostic/TestBBReg.transmissionService Lnet/rim/device/apps/api/transmission/TransmissionService;
      // 0a: aload 0
      // 0b: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.transmissionService Lnet/rim/device/apps/api/transmission/TransmissionService;
      // 0e: ldc_w "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE"
      // 11: bipush 70
      // 13: aload 0
      // 14: invokeinterface net/rim/device/apps/api/transmission/TransmissionService.addTransmissionServiceListener (Ljava/lang/String;ILnet/rim/device/apps/api/transmission/TransmissionServiceListener;)V 4
      // 19: aload 0
      // 1a: bipush 0
      // 1b: putfield net/rim/device/apps/internal/diagnostic/TestBBReg.responseFlag Z
      // 1e: aload 0
      // 1f: invokespecial net/rim/device/apps/internal/diagnostic/TestBBReg.sendPin2PinPing ()V
      // 22: aload 0
      // 23: new java/util/Timer
      // 26: dup
      // 27: invokespecial java/util/Timer.<init> ()V
      // 2a: putfield net/rim/device/apps/internal/diagnostic/TestBBReg.timer Ljava/util/Timer;
      // 2d: aload 0
      // 2e: new net/rim/device/apps/internal/diagnostic/TestBBReg$pin2pinPingExpired
      // 31: dup
      // 32: aload 0
      // 33: invokespecial net/rim/device/apps/internal/diagnostic/TestBBReg$pin2pinPingExpired.<init> (Lnet/rim/device/apps/internal/diagnostic/TestBBReg;)V
      // 36: putfield net/rim/device/apps/internal/diagnostic/TestBBReg.pin2pinTimeout Ljava/util/TimerTask;
      // 39: aload 0
      // 3a: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.timer Ljava/util/Timer;
      // 3d: aload 0
      // 3e: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.pin2pinTimeout Ljava/util/TimerTask;
      // 41: aload 0
      // 42: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.manager Lnet/rim/device/apps/internal/diagnostic/Diag;
      // 45: pop
      // 46: sipush 15000
      // 49: i2l
      // 4a: invokevirtual java/util/Timer.schedule (Ljava/util/TimerTask;J)V
      // 4d: aload 0
      // 4e: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.responseFlag Z
      // 51: ifne b6
      // 54: sipush 500
      // 57: i2l
      // 58: invokestatic java/lang/Thread.sleep (J)V
      // 5b: aload 0
      // 5c: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.timeoutFlag Z
      // 5f: ifeq 96
      // 62: aload 0
      // 63: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 66: getfield net/rim/device/apps/internal/diagnostic/RunningScreen.state I
      // 69: aload 0
      // 6a: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 6d: invokevirtual java/lang/Object.getClass ()Ljava/lang/Class;
      // 70: pop
      // 71: bipush 1
      // 72: if_icmpne 96
      // 75: aload 0
      // 76: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 79: bipush 1
      // 7a: putfield net/rim/device/apps/internal/diagnostic/RunningScreen.pin2pinPingFinished Z
      // 7d: aload 0
      // 7e: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 81: getfield net/rim/device/apps/internal/diagnostic/ReportScreen.report Lnet/rim/device/apps/internal/diagnostic/Report;
      // 84: bipush 0
      // 85: putfield net/rim/device/apps/internal/diagnostic/Report.pin2pinPing I
      // 88: aload 0
      // 89: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.transmissionService Lnet/rim/device/apps/api/transmission/TransmissionService;
      // 8c: ldc_w "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE"
      // 8f: aload 0
      // 90: invokeinterface net/rim/device/apps/api/transmission/TransmissionService.removeTransmissionServiceListener (Ljava/lang/String;Lnet/rim/device/apps/api/transmission/TransmissionServiceListener;)V 3
      // 95: return
      // 96: aload 0
      // 97: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 9a: getfield net/rim/device/apps/internal/diagnostic/RunningScreen.state I
      // 9d: aload 0
      // 9e: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // a1: invokevirtual java/lang/Object.getClass ()Ljava/lang/Class;
      // a4: pop
      // a5: ifne 4d
      // a8: aload 0
      // a9: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.transmissionService Lnet/rim/device/apps/api/transmission/TransmissionService;
      // ac: ldc_w "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE"
      // af: aload 0
      // b0: invokeinterface net/rim/device/apps/api/transmission/TransmissionService.removeTransmissionServiceListener (Ljava/lang/String;Lnet/rim/device/apps/api/transmission/TransmissionServiceListener;)V 3
      // b5: return
      // b6: aload 0
      // b7: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.timer Ljava/util/Timer;
      // ba: invokevirtual java/util/Timer.cancel ()V
      // bd: aload 0
      // be: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // c1: bipush 1
      // c2: putfield net/rim/device/apps/internal/diagnostic/RunningScreen.pin2pinPingFinished Z
      // c5: aload 0
      // c6: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // c9: getfield net/rim/device/apps/internal/diagnostic/ReportScreen.report Lnet/rim/device/apps/internal/diagnostic/Report;
      // cc: bipush 1
      // cd: putfield net/rim/device/apps/internal/diagnostic/Report.pin2pinPing I
      // d0: aload 0
      // d1: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.transmissionService Lnet/rim/device/apps/api/transmission/TransmissionService;
      // d4: ldc_w "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE"
      // d7: aload 0
      // d8: invokeinterface net/rim/device/apps/api/transmission/TransmissionService.removeTransmissionServiceListener (Ljava/lang/String;Lnet/rim/device/apps/api/transmission/TransmissionServiceListener;)V 3
      // dd: return
      // de: astore 1
      // df: aload 0
      // e0: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.transmissionService Lnet/rim/device/apps/api/transmission/TransmissionService;
      // e3: ldc_w "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE"
      // e6: aload 0
      // e7: invokeinterface net/rim/device/apps/api/transmission/TransmissionService.removeTransmissionServiceListener (Ljava/lang/String;Lnet/rim/device/apps/api/transmission/TransmissionServiceListener;)V 3
      // ec: return
      // ed: astore 2
      // ee: aload 0
      // ef: getfield net/rim/device/apps/internal/diagnostic/TestBBReg.transmissionService Lnet/rim/device/apps/api/transmission/TransmissionService;
      // f2: ldc_w "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE"
      // f5: aload 0
      // f6: invokeinterface net/rim/device/apps/api/transmission/TransmissionService.removeTransmissionServiceListener (Ljava/lang/String;Lnet/rim/device/apps/api/transmission/TransmissionServiceListener;)V 3
      // fb: aload 2
      // fc: athrow
      // try (0 -> 63): 101 null
      // try (69 -> 77): 101 null
      // try (83 -> 95): 101 null
      // try (0 -> 63): 108 null
      // try (69 -> 77): 108 null
      // try (83 -> 95): 108 null
      // try (101 -> 102): 108 null
      // try (108 -> 109): 108 null
   }

   private final void sendPin2PinPing() {
      String to = this.report.getPin();
      this.subject = this.report.getPin();
      long tmpLong = new Random().nextLong();
      this.bodyStr = new Long(tmpLong).toString();
      ContextObject contextObject = new ContextObject();
      contextObject.setFlag(31);
      contextObject.setFlag(85);
      contextObject.setFlag(94);
      EmailMessageModelImpl msg = new EmailMessageModelImpl(contextObject);
      String[] names = new String[]{to, to};
      ContextObject context = new ContextObject();
      ContextObject.put(context, 251, names);
      Object recipient = FactoryUtil.createInstance(-2985347935260258684L, context);
      EmailBuilderApi.addRecipient(msg, 0, (RIMModel)recipient);
      EmailBuilderApi.addSubjectLine(msg, this.subject);
      EmailBuilderApi.addMessageBody(msg, this.bodyStr);
      msg.setType((byte)32);
      RIMMessagingService service = (RIMMessagingService)TransmissionServiceManager.get(8399767144006445082L);
      if (service != null) {
         ServiceRecord sr = service.getOutgoingServiceRecord();
         context.reset();
         EmailSendUtility.sendMessage(msg, sr, new ContextObject());
         EmailHierarchy.removeMessage(msg, msg.getFolderId());
      }
   }

   private final void sendMdpPing() {
      EventLogger.logEvent(4080229686686977759L, 1414557801, 0);

      try {
         String addressBase = HRUtils.getDefaultHRT().getActiveHri().getAddressBase().getAddress();
         String url = "udp:" + addressBase;
         DatagramConnectionBase _subConnection = (DatagramConnectionBase)Connector.open(url);
         DatagramBase dgram = (DatagramBase)_subConnection.newDatagram();
         MdpUtil$DatagramInfo info = MdpUtil.makeDatagramInfo(1);
         info.moreFlag = true;
         MdpUtil.encode(dgram, info);
         dgram.setAddressBase(_subConnection.newDatagramAddressBase(addressBase, false));
         TestBBReg$SendPacketThread _sendThread = new TestBBReg$SendPacketThread();
         ProtocolDaemon.getInstance().startThread(_sendThread);
         _sendThread.addRequest(_subConnection, dgram);
      } finally {
         EventLogger.logEvent(4080229686686977759L, 1413834337, 2);
         return;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean receiveObject(TransmissionService aTransmissionService, Object transmissionObject, Object context) {
      try {
         ContextObject contextObject = ContextObject.castOrCreate(context);
         RIMMessagingIncomingMessage incMsg = null;
         String body = null;
         String sub = null;
         if (!(transmissionObject instanceof RIMMessagingIncomingMessage)) {
            return false;
         } else {
            incMsg = (RIMMessagingIncomingMessage)transmissionObject;
            Object emailbody = incMsg.getText();
            Object emailsub = incMsg.getSubject();
            if (!(emailbody instanceof String)) {
               return false;
            } else {
               body = (String)emailbody;
               sub = (String)emailsub;
               if (body.equals(this.bodyStr) && sub.equals(this.subject)) {
                  this.responseFlag = true;
                  return true;
               } else {
                  return false;
               }
            }
         }
      } catch (Throwable var11) {
         System.out.println(e.toString());
         return false;
      }
   }

   @Override
   public final void statusChanged(TransmissionService aTransmissionService, int statusInt, Object contextObject) {
   }
}
