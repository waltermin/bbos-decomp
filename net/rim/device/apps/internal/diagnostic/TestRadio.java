package net.rim.device.apps.internal.diagnostic;

import java.util.Random;
import java.util.Timer;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.Radio;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.system.ICMPPacketHeader;
import net.rim.device.internal.system.ICMPPacketListener;
import net.rim.device.internal.system.RadioInternal;

public final class TestRadio extends Thread implements RadioStatusListener, ICMPPacketListener {
   private Timer timer;
   int radioState;
   int pdpState;
   boolean timeoutFlag;
   boolean flashFlag;
   boolean icmpPingSentFailed;
   boolean icmpPingResponse;
   int _apnId;
   private TestRadio$timerExpired timeoutAction;
   private RunningScreen screen;
   private Diag manager;
   int _curPacketID;
   byte[] _pingBuffer = new byte[6];
   public static final int PAYLOAD_SIZE = 6;

   public TestRadio(RunningScreen _screen) {
      this.screen = _screen;
      this.flashFlag = true;
      this.pdpState = -1;
      this.manager = this.screen._manager;
      this.manager.addRadioListener(this);
   }

   public final void radioOn() {
      try {
         Radio.requestPowerOn();
         Diag.showMessage(DiagnosticResources.getString(2));

         while (RadioInfo.getState() != 1) {
            Thread.sleep(500);
            if (this.timeoutFlag && this.screen.state == 1) {
               this.screen.report.radioActivation = 0;
               return;
            }

            int var10000 = this.screen.state;
            this.screen.getClass();
            if (var10000 == 0) {
               return;
            }
         }
      } finally {
         return;
      }
   }

   public final void gprsAtt() {
      try {
         RadioInternal.gprsAttach(true);

         while (GPRSInfo.getGPRSState() != 2) {
            Thread.sleep(500);
            if (this.timeoutFlag && this.screen.state == 1) {
               this.screen.report.radioActivation = 0;
               return;
            }

            int var10000 = this.screen.state;
            this.screen.getClass();
            if (var10000 == 0) {
               return;
            }
         }
      } finally {
         return;
      }
   }

   public final void pdpAct() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 2
      // 002: invokestatic net/rim/device/api/system/RadioInfo.getActiveWAFs ()I
      // 005: if_icmpne 01d
      // 008: aload 0
      // 009: ldc_w ""
      // 00c: invokestatic net/rim/device/internal/system/RadioInternal.registerAccessPointNumber (Ljava/lang/String;)I
      // 00f: putfield net/rim/device/apps/internal/diagnostic/TestRadio._apnId I
      // 012: goto 0a6
      // 015: astore 1
      // 016: aload 1
      // 017: invokevirtual java/lang/Throwable.printStackTrace ()V
      // 01a: goto 0a6
      // 01d: invokestatic net/rim/device/cldc/io/tunnel/TunnelApnListFactory.getTunnelApnListFactory ()Lnet/rim/device/cldc/io/tunnel/TunnelApnListFactory;
      // 020: invokevirtual net/rim/device/cldc/io/tunnel/TunnelApnListFactory.createTunnelApnList ()Lnet/rim/device/cldc/io/tunnel/TunnelApnList;
      // 023: astore 2
      // 024: invokestatic net/rim/device/api/hrt/HRUtils.getDefaultHRT ()Lnet/rim/device/api/hrt/HostRoutingTable;
      // 027: astore 3
      // 028: invokestatic net/rim/device/api/hrt/HRUtils.getRegistrationHRT ()Lnet/rim/device/api/hrt/HostRoutingTable;
      // 02b: astore 4
      // 02d: aload 3
      // 02e: ifnull 05e
      // 031: aload 3
      // 032: invokevirtual net/rim/device/api/hrt/HostRoutingTable.getActiveHri ()Lnet/rim/device/api/hrt/HostRoutingInfo;
      // 035: dup
      // 036: astore 5
      // 038: ifnull 05e
      // 03b: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 03e: ldc_w "Diagnostic App: Using default HRT - active HRI information"
      // 041: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 044: aload 2
      // 045: aload 5
      // 047: invokeinterface net/rim/device/cldc/io/tunnel/TunnelApnList.initializeList (Ljava/lang/Object;)V 2
      // 04c: aload 2
      // 04d: invokeinterface net/rim/device/cldc/io/tunnel/TunnelApnList.getFirst ()Ljava/lang/String; 1
      // 052: astore 1
      // 053: aload 0
      // 054: aload 1
      // 055: invokestatic net/rim/device/internal/system/RadioInternal.registerAccessPointNumber (Ljava/lang/String;)I
      // 058: putfield net/rim/device/apps/internal/diagnostic/TestRadio._apnId I
      // 05b: goto 0a6
      // 05e: aload 4
      // 060: ifnull 091
      // 063: aload 4
      // 065: invokevirtual net/rim/device/api/hrt/HostRoutingTable.getActiveHri ()Lnet/rim/device/api/hrt/HostRoutingInfo;
      // 068: dup
      // 069: astore 5
      // 06b: ifnull 091
      // 06e: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 071: ldc_w "Diagnostic App: Using registration HRT - active HRI information"
      // 074: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 077: aload 2
      // 078: aload 5
      // 07a: invokeinterface net/rim/device/cldc/io/tunnel/TunnelApnList.initializeList (Ljava/lang/Object;)V 2
      // 07f: aload 2
      // 080: invokeinterface net/rim/device/cldc/io/tunnel/TunnelApnList.getFirst ()Ljava/lang/String; 1
      // 085: astore 1
      // 086: aload 0
      // 087: aload 1
      // 088: invokestatic net/rim/device/internal/system/RadioInternal.registerAccessPointNumber (Ljava/lang/String;)I
      // 08b: putfield net/rim/device/apps/internal/diagnostic/TestRadio._apnId I
      // 08e: goto 0a6
      // 091: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 094: ldc_w "Diagnostic App: APN list is uninitialized since have nothing to go from?"
      // 097: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 09a: aload 0
      // 09b: getfield net/rim/device/apps/internal/diagnostic/TestRadio.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 09e: getfield net/rim/device/apps/internal/diagnostic/ReportScreen.report Lnet/rim/device/apps/internal/diagnostic/Report;
      // 0a1: bipush 0
      // 0a2: putfield net/rim/device/apps/internal/diagnostic/Report.radioActivation I
      // 0a5: return
      // 0a6: aload 0
      // 0a7: getfield net/rim/device/apps/internal/diagnostic/TestRadio.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 0aa: aload 0
      // 0ab: getfield net/rim/device/apps/internal/diagnostic/TestRadio._apnId I
      // 0ae: putfield net/rim/device/apps/internal/diagnostic/RunningScreen._apnId I
      // 0b1: aload 0
      // 0b2: getfield net/rim/device/apps/internal/diagnostic/TestRadio._apnId I
      // 0b5: invokestatic net/rim/device/api/system/RadioInfo.isPDPContextActive (I)Z
      // 0b8: ifne 137
      // 0bb: aload 0
      // 0bc: getfield net/rim/device/apps/internal/diagnostic/TestRadio.pdpState I
      // 0bf: ifge 102
      // 0c2: sipush 500
      // 0c5: i2l
      // 0c6: invokestatic java/lang/Thread.sleep (J)V
      // 0c9: aload 0
      // 0ca: getfield net/rim/device/apps/internal/diagnostic/TestRadio.timeoutFlag Z
      // 0cd: ifeq 0ef
      // 0d0: aload 0
      // 0d1: getfield net/rim/device/apps/internal/diagnostic/TestRadio.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 0d4: getfield net/rim/device/apps/internal/diagnostic/RunningScreen.state I
      // 0d7: aload 0
      // 0d8: getfield net/rim/device/apps/internal/diagnostic/TestRadio.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 0db: invokevirtual java/lang/Object.getClass ()Ljava/lang/Class;
      // 0de: pop
      // 0df: bipush 1
      // 0e0: if_icmpne 0ef
      // 0e3: aload 0
      // 0e4: getfield net/rim/device/apps/internal/diagnostic/TestRadio.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 0e7: getfield net/rim/device/apps/internal/diagnostic/ReportScreen.report Lnet/rim/device/apps/internal/diagnostic/Report;
      // 0ea: bipush 0
      // 0eb: putfield net/rim/device/apps/internal/diagnostic/Report.radioActivation I
      // 0ee: return
      // 0ef: aload 0
      // 0f0: getfield net/rim/device/apps/internal/diagnostic/TestRadio.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 0f3: getfield net/rim/device/apps/internal/diagnostic/RunningScreen.state I
      // 0f6: aload 0
      // 0f7: getfield net/rim/device/apps/internal/diagnostic/TestRadio.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 0fa: invokevirtual java/lang/Object.getClass ()Ljava/lang/Class;
      // 0fd: pop
      // 0fe: ifne 0bb
      // 101: return
      // 102: aload 0
      // 103: getfield net/rim/device/apps/internal/diagnostic/TestRadio.pdpState I
      // 106: ifne 117
      // 109: aload 0
      // 10a: getfield net/rim/device/apps/internal/diagnostic/TestRadio.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 10d: getfield net/rim/device/apps/internal/diagnostic/ReportScreen.report Lnet/rim/device/apps/internal/diagnostic/Report;
      // 110: bipush 1
      // 111: putfield net/rim/device/apps/internal/diagnostic/Report.radioActivation I
      // 114: goto 159
      // 117: aload 0
      // 118: getfield net/rim/device/apps/internal/diagnostic/TestRadio.pdpState I
      // 11b: bipush 2
      // 11d: if_icmpeq 129
      // 120: aload 0
      // 121: getfield net/rim/device/apps/internal/diagnostic/TestRadio.pdpState I
      // 124: bipush 2
      // 126: if_icmpne 159
      // 129: aload 0
      // 12a: getfield net/rim/device/apps/internal/diagnostic/TestRadio.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 12d: getfield net/rim/device/apps/internal/diagnostic/ReportScreen.report Lnet/rim/device/apps/internal/diagnostic/Report;
      // 130: bipush 0
      // 131: putfield net/rim/device/apps/internal/diagnostic/Report.radioActivation I
      // 134: goto 159
      // 137: aload 0
      // 138: getfield net/rim/device/apps/internal/diagnostic/TestRadio.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 13b: getfield net/rim/device/apps/internal/diagnostic/ReportScreen.report Lnet/rim/device/apps/internal/diagnostic/Report;
      // 13e: bipush 1
      // 13f: putfield net/rim/device/apps/internal/diagnostic/Report.radioActivation I
      // 142: return
      // 143: astore 1
      // 144: aload 1
      // 145: invokevirtual java/lang/Throwable.printStackTrace ()V
      // 148: aload 0
      // 149: getfield net/rim/device/apps/internal/diagnostic/TestRadio.screen Lnet/rim/device/apps/internal/diagnostic/RunningScreen;
      // 14c: getfield net/rim/device/apps/internal/diagnostic/ReportScreen.report Lnet/rim/device/apps/internal/diagnostic/Report;
      // 14f: bipush 0
      // 150: putfield net/rim/device/apps/internal/diagnostic/Report.radioActivation I
      // 153: return
      // 154: astore 1
      // 155: aload 1
      // 156: invokevirtual java/lang/Throwable.printStackTrace ()V
      // 159: return
      // try (3 -> 7): 8 null
      // try (0 -> 69): 141 null
      // try (70 -> 102): 141 null
      // try (103 -> 111): 141 null
      // try (112 -> 140): 141 null
      // try (0 -> 69): 150 null
      // try (70 -> 102): 150 null
      // try (103 -> 111): 150 null
      // try (112 -> 140): 150 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean var5 = false /* VF: Semaphore variable */;

      label403: {
         label402: {
            label401: {
               label400: {
                  label399: {
                     label398: {
                        label397: {
                           label396: {
                              label395: {
                                 label394: {
                                    label393: {
                                       label392: {
                                          label391: {
                                             label390: {
                                                label389: {
                                                   label388: {
                                                      label387: {
                                                         label386: {
                                                            label385: {
                                                               try {
                                                                  try {
                                                                     var5 = true;
                                                                     this.timeoutFlag = false;
                                                                     if ((DeviceInfo.getBatteryStatus() & 16384) > 0) {
                                                                        this.screen.radioFinished = true;
                                                                        this.screen.report.radioActivation = 0;
                                                                        Diag.showMessage(DiagnosticResources.getString(3));
                                                                        var5 = false;
                                                                        break label403;
                                                                     }

                                                                     this.radioState = RadioInfo.getState();
                                                                     if (RadioInfo.getNetworkType() != 4) {
                                                                        if (this.radioState != 0) {
                                                                           if (GPRSInfo.getGPRSState() != 0) {
                                                                              this.screen.state = 1;
                                                                              this.timer = new Timer();
                                                                              this.timeoutAction = new TestRadio$timerExpired(this);
                                                                              this.timer.schedule(this.timeoutAction, 15000);
                                                                              this.pdpAct();
                                                                              if (this.timeoutFlag) {
                                                                                 this.screen.radioFinished = true;
                                                                                 var5 = false;
                                                                                 break label387;
                                                                              }

                                                                              this.timer.cancel();
                                                                              this.screen.radioFinished = true;
                                                                              int var10000 = this.screen.state;
                                                                              this.screen.getClass();
                                                                              if (var10000 == 0) {
                                                                                 var5 = false;
                                                                                 break label386;
                                                                              }

                                                                              this.icmpPing();
                                                                           } else {
                                                                              this.screen.state = 1;
                                                                              this.timer = new Timer();
                                                                              this.timeoutAction = new TestRadio$timerExpired(this);
                                                                              this.timer.schedule(this.timeoutAction, 20000);
                                                                              this.gprsAtt();
                                                                              if (this.timeoutFlag) {
                                                                                 this.screen.radioFinished = true;
                                                                                 var5 = false;
                                                                                 break label391;
                                                                              }

                                                                              int var31 = this.screen.state;
                                                                              this.screen.getClass();
                                                                              if (var31 == 0) {
                                                                                 var5 = false;
                                                                                 break label390;
                                                                              }

                                                                              this.pdpAct();
                                                                              if (this.timeoutFlag || this.screen.report.radioActivation == 0) {
                                                                                 this.screen.radioFinished = true;
                                                                                 var5 = false;
                                                                                 break label389;
                                                                              }

                                                                              this.timer.cancel();
                                                                              this.screen.radioFinished = true;
                                                                              var31 = this.screen.state;
                                                                              this.screen.getClass();
                                                                              if (var31 == 0) {
                                                                                 var5 = false;
                                                                                 break label388;
                                                                              }

                                                                              this.icmpPing();
                                                                           }
                                                                        } else {
                                                                           this.screen.state = 1;
                                                                           this.timer = new Timer();
                                                                           this.timeoutAction = new TestRadio$timerExpired(this);
                                                                           this.timer.schedule(this.timeoutAction, 30000);
                                                                           this.radioOn();
                                                                           if (this.timeoutFlag) {
                                                                              this.screen.radioFinished = true;
                                                                              var5 = false;
                                                                              break label397;
                                                                           }

                                                                           int var33 = this.screen.state;
                                                                           this.screen.getClass();
                                                                           if (var33 == 0) {
                                                                              var5 = false;
                                                                              break label396;
                                                                           }

                                                                           this.gprsAtt();
                                                                           if (this.timeoutFlag) {
                                                                              this.screen.radioFinished = true;
                                                                              var5 = false;
                                                                              break label395;
                                                                           }

                                                                           var33 = this.screen.state;
                                                                           this.screen.getClass();
                                                                           if (var33 == 0) {
                                                                              var5 = false;
                                                                              break label394;
                                                                           }

                                                                           this.pdpAct();
                                                                           if (this.timeoutFlag || this.screen.report.radioActivation == 0) {
                                                                              this.screen.radioFinished = true;
                                                                              var5 = false;
                                                                              break label393;
                                                                           }

                                                                           this.timer.cancel();
                                                                           this.screen.radioFinished = true;
                                                                           var33 = this.screen.state;
                                                                           this.screen.getClass();
                                                                           if (var33 == 0) {
                                                                              var5 = false;
                                                                              break label392;
                                                                           }

                                                                           this.icmpPing();
                                                                        }
                                                                     } else {
                                                                        this.screen.state = 1;
                                                                        this.timer = new Timer();
                                                                        this.timeoutAction = new TestRadio$timerExpired(this);
                                                                        if (this.radioState == 0) {
                                                                           this.timer.schedule(this.timeoutAction, 30000);
                                                                           this.radioOn();
                                                                           if (this.timeoutFlag) {
                                                                              this.screen.radioFinished = true;
                                                                              var5 = false;
                                                                              break label401;
                                                                           }

                                                                           int var36 = this.screen.state;
                                                                           this.screen.getClass();
                                                                           if (var36 == 0) {
                                                                              var5 = false;
                                                                              break label400;
                                                                           }
                                                                        } else {
                                                                           this.timer.schedule(this.timeoutAction, 15000);
                                                                        }

                                                                        this.pdpAct();
                                                                        if (this.timeoutFlag || this.screen.report.radioActivation == 0) {
                                                                           this.screen.radioFinished = true;
                                                                           var5 = false;
                                                                           break label399;
                                                                        }

                                                                        this.timer.cancel();
                                                                        this.screen.radioFinished = true;
                                                                        int var37 = this.screen.state;
                                                                        this.screen.getClass();
                                                                        if (var37 == 0) {
                                                                           var5 = false;
                                                                           break label398;
                                                                        }

                                                                        this.icmpPing();
                                                                     }

                                                                     if (RadioInfo.getSignalLevel() == -256) {
                                                                        this.screen.radioFinished = true;
                                                                        this.screen.report.radioActivation = 0;
                                                                        var5 = false;
                                                                        break label385;
                                                                     }

                                                                     var5 = false;
                                                                  } catch (Throwable var8) {
                                                                     e2.printStackTrace();
                                                                     var5 = false;
                                                                     break label402;
                                                                  }
                                                               } finally {
                                                                  if (var5) {
                                                                     this.manager.removeRadioListener(this);
                                                                  }
                                                               }

                                                               this.manager.removeRadioListener(this);
                                                               return;
                                                            }

                                                            this.manager.removeRadioListener(this);
                                                            return;
                                                         }

                                                         this.manager.removeRadioListener(this);
                                                         return;
                                                      }

                                                      this.manager.removeRadioListener(this);
                                                      return;
                                                   }

                                                   this.manager.removeRadioListener(this);
                                                   return;
                                                }

                                                this.manager.removeRadioListener(this);
                                                return;
                                             }

                                             this.manager.removeRadioListener(this);
                                             return;
                                          }

                                          this.manager.removeRadioListener(this);
                                          return;
                                       }

                                       this.manager.removeRadioListener(this);
                                       return;
                                    }

                                    this.manager.removeRadioListener(this);
                                    return;
                                 }

                                 this.manager.removeRadioListener(this);
                                 return;
                              }

                              this.manager.removeRadioListener(this);
                              return;
                           }

                           this.manager.removeRadioListener(this);
                           return;
                        }

                        this.manager.removeRadioListener(this);
                        return;
                     }

                     this.manager.removeRadioListener(this);
                     return;
                  }

                  this.manager.removeRadioListener(this);
                  return;
               }

               this.manager.removeRadioListener(this);
               return;
            }

            this.manager.removeRadioListener(this);
            return;
         }

         this.manager.removeRadioListener(this);
         return;
      }

      this.manager.removeRadioListener(this);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void icmpPing() {
      try {
         this.icmpPingSentFailed = false;
         this.icmpPingResponse = false;
         Random _rand = new Random();
         Arrays.fill(this._pingBuffer, (byte)115);
         this._pingBuffer[0] = (byte)(_rand.nextInt() & 0xFF);
         this._pingBuffer[1] = (byte)(_rand.nextInt() & 0xFF);
         ICMPPacketHeader head = new ICMPPacketHeader();
         head.setAccessPointNumber(this._apnId);
         head.setDestinationAddress(RadioInfo.getIPAddress(this._apnId));
         head.setType(8);
         head.setCode(0);
         head.setChecksum(0);
         boolean var8 = false /* VF: Semaphore variable */;

         label93:
         try {
            var8 = true;
            this._curPacketID = RadioInternal.sendPacket(head, this._pingBuffer);
            var8 = false;
         } finally {
            if (var8) {
               this._curPacketID = -1;
               System.out.println("radio exception!");
               break label93;
            }
         }

         if (this._curPacketID < 0) {
            this.screen.report.icmpPing = 0;
            this.screen.icmpPingFinished = true;
         } else {
            this.timer = new Timer();
            this.timeoutAction = new TestRadio$timerExpired(this);
            this.timer.schedule(this.timeoutAction, 10000);

            while (!this.icmpPingResponse) {
               if (this.screen.state == 1 && this.icmpPingSentFailed) {
                  this.screen.report.icmpPing = 0;
                  this.screen.icmpPingFinished = true;
                  return;
               }

               Thread.sleep(500);
               if (this.timeoutFlag && this.screen.state == 1) {
                  this.screen.report.icmpPing = 0;
                  this.screen.icmpPingFinished = true;
                  return;
               }

               int var10000 = this.screen.state;
               this.screen.getClass();
               if (var10000 == 0) {
                  return;
               }
            }

            this.timer.cancel();
            this.screen.report.icmpPing = 1;
            this.screen.icmpPingFinished = true;
         }
      } finally {
         return;
      }
   }

   @Override
   public final synchronized void pdpStateChange(int apn, int state, int cause) {
      if (apn == this._apnId) {
         this.pdpState = state;
      }
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
   }

   @Override
   public final void packetSent(int packetID, int networkID) {
      if (this._curPacketID == packetID) {
         System.out.println("ICMP Echo Ping packet sent!");
      }
   }

   @Override
   public final void packetNotSent(int packetID, int networkID) {
      if (this._curPacketID == packetID) {
         this.icmpPingSentFailed = true;
      }
   }

   @Override
   public final void packetStatus(int packetID, int status) {
   }

   @Override
   public final void packetReceived(ICMPPacketHeader header, byte[] data) {
      if (Arrays.equals(header.getSourceAddress(), RadioInfo.getIPAddress(this._apnId))) {
         if (header.getType() == 0) {
            if (Arrays.equals(data, this._pingBuffer)) {
               this.icmpPingResponse = true;
            }
         }
      }
   }
}
