package net.rim.device.cldc.io.commlink;

import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Datagram;
import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramTransportBase;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.USBPortInternal;
import net.rim.vm.Array;

public final class Transport extends DatagramTransportBase implements Runnable {
   private boolean _started;
   private ToIntHashtable _registry = new ToIntHashtable();
   private Vector _reverseRegistry = new Vector();
   private int _txMTU;
   int _rxDestination;
   byte[] _rxBuffer;
   int _rxLength;
   int _rxBase;
   private int _reply;
   private Object _replyFlag;
   private Object _replyLock = new Object();
   private CommLinkTransport _commLink;
   private boolean _linkUp;
   private IntHashtable _receivingDatagrams;
   CommLinkStatusScreen _statusScreen;
   private ProfileString _profile;

   final synchronized boolean startThread(CommLinkTransport commLink) {
      RIMGlobalMessagePoster.postGlobalEvent(-4783788168994715579L);
      if (this._started) {
         if (commLink != this._commLink) {
            return false;
         }
      } else {
         this._started = true;
         this._commLink = commLink;
         new Thread(this).start();
      }

      return true;
   }

   final void die() {
      this._commLink.die();
   }

   final void registerName(String name) throws IOException {
      if (name != null && name.length() != 0) {
         synchronized (this._registry) {
            if (!this._registry.containsKey(name)) {
               int id = this._reverseRegistry.size() + 1;
               this._registry.put(name, id);
               this._reverseRegistry.addElement(name);
            }
         }
      } else {
         throw new IOException("Invalid transport address");
      }
   }

   final void setMtu(int newmtu) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final int sendDataPacket(int id, byte[] data, int off, int len, boolean more, boolean onInternalThread) {
      this._profile.add(80, 115);
      int result = this._commLink.sendDataPacket(id, data, off, len, more, onInternalThread);
      if (result != 0) {
         this._profile.add(80, 33);
         return result;
      }

      if (!more) {
         if (onInternalThread) {
            this._commLink.readFrame();
            result = this.getReplyValue();
         } else {
            result = this.readReply();
         }
      }

      return result;
   }

   final void setReply(int reply) {
      synchronized (this._replyLock) {
         this._reply = reply;
         this._replyFlag = this._replyLock;
         this._replyLock.notify();
         this._profile.add(82, 105);
      }
   }

   final ProfileString getProfileString() {
      return this._profile;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         this._profile.add(84, 49);
         this.mainLoop();
         var3 = false;
      } finally {
         if (var3) {
            this._started = false;
            this._commLink.close();
            this._profile.add(84, 48);
         }
      }

      this._started = false;
      this._commLink.close();
      this._profile.add(84, 48);
   }

   @Override
   public final void send(Datagram datagram) throws EOFException {
      DatagramBase dg = (DatagramBase)datagram;
      if (!this._linkUp) {
         throw new EOFException("Link down");
      }

      dg.rewind();
      if (dg.isFlagSet(1)) {
         this._commLink.sendReply(dg.readShort());
      } else {
         this.sendDatagram(dg);
      }
   }

   private final void sendDatagram(DatagramBase dg) throws IOException {
      this._profile.add(68, 111);
      String name = dg.getAddress();
      int id = this.lookupName(name);
      if (id <= 0) {
         throw new IOException("Unknown sender");
      }

      this._statusScreen.advanceAnimation(true);
      byte[] data = dg.getArray();
      int fragment_offset = dg.getArrayStart();
      int len = dg.getLength();
      boolean more = dg.isFlagSet(8);
      int result = 0;

      while (len > 0) {
         int fragment_size;
         boolean frag;
         if (len > this._txMTU) {
            fragment_size = this._txMTU;
            frag = true;
         } else {
            fragment_size = len;
            frag = more;
         }

         result = this.sendDataPacket(id, data, fragment_offset, fragment_size, frag, false);
         if (result != 0) {
            this.passUpReply(name, result);
            return;
         }

         len -= fragment_size;
         fragment_offset += fragment_size;
      }

      if (dg.getLength() == 0) {
         result = this.sendDataPacket(id, null, 0, 0, more, false);
      }

      if (!more) {
         this.passUpReply(name, result);
      }
   }

   private final int sendAppInfo() {
      byte[] array = new byte[this._txMTU];
      int len = 0;
      int size = this._reverseRegistry.size();

      for (int i = 0; i < size; i++) {
         String name = (String)this._reverseRegistry.elementAt(i);
         int namelen = name.length();
         if (len + namelen + 2 <= this._txMTU) {
            array[len++] = (byte)namelen;
            array[len++] = (byte)(i + 1);

            for (int j = 0; j < namelen; j++) {
               array[len++] = (byte)name.charAt(j);
            }
         }
      }

      return this.sendDataPacket(0, array, 0, len, false, true);
   }

   private final boolean doFinalConnectionSteps() {
      if (this.sendAppInfo() == 0) {
         this._commLink.sendReply(0);
         this._commLink.checkSpeed();
         this._linkUp = true;
         this._statusScreen.connected();
         this.broadcastStartStop(true);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final Datagram newDatagram(byte[] buf, int offset, int length, String addr) {
      return new CommLinkDatagram(buf, offset, length, addr);
   }

   private final void passUpFrame(String recipient, byte[] data, int length) {
      DatagramBase frameDatagram = (DatagramBase)this.newDatagram(data, 0, length, recipient);
      this.passUpDatagram(frameDatagram);
   }

   private final void passUpReply(String recipient, int reply) {
      DatagramBase replyDatagram = (DatagramBase)this.newDatagram(null, 0, 2, recipient);
      replyDatagram.writeShort(reply);
      replyDatagram.setFlag(1, true);
      this.passUpDatagram(replyDatagram);
   }

   private final void broadcastStartStop(boolean start) {
      DatagramBase datagram = (DatagramBase)this.newDatagram(null, 0, 0, "");
      if (start) {
         datagram.setFlag(2, true);
      } else {
         datagram.setFlag(4, true);
      }

      this.passUpDatagram(datagram);
   }

   private final int readReply() {
      synchronized (this._replyLock) {
         this._profile.add(82, 119);

         while (this._replyFlag == null) {
            try {
               this._replyLock.wait();
            } finally {
               continue;
            }
         }

         int result = this._reply;
         this._replyFlag = null;
         this._profile.add(82, 103);
         return result;
      }
   }

   private final int lookupName(String name) {
      if (name != null && name.length() != 0) {
         synchronized (this._registry) {
            return this._registry.get(name);
         }
      } else {
         return 0;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void mainLoop() {
      boolean var67 = false /* VF: Semaphore variable */;
      boolean var76 = false /* VF: Semaphore variable */;

      label1137: {
         label1138: {
            label1139: {
               label1140: {
                  try {
                     try {
                        var76 = true;
                        var67 = true;
                        long e = 0;
                        if (!this._commLink.open()) {
                           var67 = false;
                           var76 = false;
                           break label1138;
                        }

                        this._replyFlag = null;
                        this._receivingDatagrams = new IntHashtable();
                        this._rxBuffer = new byte[1036];
                        this._statusScreen = new CommLinkStatusScreen();
                        this._linkUp = false;
                        int SCState = 1;
                        this._txMTU = 128;

                        label1125:
                        while (true) {
                           int rxType = this._commLink.readFrame(e);
                           if ((rxType & 67) == 64) {
                              e = 0;
                              switch (SCState) {
                                 case 0:
                                    break;
                                 case 1:
                                 default:
                                    if ((rxType & 32) == 0) {
                                       if (this._commLink.sendChallenge(this._rxBuffer[this._rxBase]) != 0) {
                                          var67 = false;
                                          var76 = false;
                                          break label1140;
                                       }

                                       if (!this._commLink.needsResponse()) {
                                          this._commLink.checkResponse(null, 0, 0);
                                          if (!this.doFinalConnectionSteps()) {
                                             var67 = false;
                                             var76 = false;
                                             break label1125;
                                          }

                                          SCState = 3;
                                       } else {
                                          this._commLink.sendReply(0);
                                          SCState = 2;
                                       }
                                    }
                                    break;
                                 case 2:
                                    if ((rxType & 32) == 0) {
                                       if (this._rxDestination == 0) {
                                          if (this._commLink.checkResponse(this._rxBuffer, this._rxBase, this._rxLength)) {
                                             if (!this.doFinalConnectionSteps()) {
                                                var67 = false;
                                                var76 = false;
                                                break label1139;
                                             }

                                             SCState = 3;
                                          } else {
                                             this._commLink.sendReply(32774);
                                             SCState = 1;
                                          }
                                       } else {
                                          SCState = 1;
                                       }
                                    }
                                    break;
                                 case 3:
                                    this._statusScreen.advanceAnimation(false);
                                    if (this._rxDestination == 0) {
                                       if (this._rxLength == 2 && this._rxBuffer[0] == 1) {
                                          this._commLink.sendReply(0);
                                          this._commLink.standbyMode();
                                       } else {
                                          this._commLink.sendReply(32768);
                                       }
                                    } else {
                                       this._rxDestination--;
                                       if (this._rxDestination >= 0 && this._rxDestination <= this._reverseRegistry.size()) {
                                          String name = (String)this._reverseRegistry.elementAt(this._rxDestination);
                                          if (name == null) {
                                             this._commLink.sendReply(32773);
                                          } else {
                                             byte[] data = (byte[])this._receivingDatagrams.get(this._rxDestination);
                                             int offset;
                                             if (data == null) {
                                                data = new byte[this._rxLength];
                                                Array.setSectionSize(data, 4096);
                                                offset = 0;
                                                this._receivingDatagrams.put(this._rxDestination, data);
                                             } else {
                                                offset = data.length;
                                                Array.resize(data, offset + this._rxLength);
                                             }

                                             System.arraycopy(this._rxBuffer, this._rxBase, data, offset, this._rxLength);
                                             if ((rxType & 32) == 0) {
                                                this.passUpFrame(name, data, data.length);
                                                this._receivingDatagrams.remove(this._rxDestination);
                                             }
                                          }
                                       } else {
                                          this._commLink.sendReply(32773);
                                       }
                                    }
                              }
                           } else {
                              this.setReply(this.getReplyValue());
                           }
                        }
                     } finally {
                        if (var76) {
                           this._profile.add(69, 33);
                           var67 = false;
                           break label1137;
                        }
                     }
                  } finally {
                     if (var67) {
                        this.setReply(32770);
                        this._commLink.close();
                        if (this._linkUp) {
                           this._linkUp = false;

                           label1048:
                           try {
                              this.broadcastStartStop(false);
                           } finally {
                              break label1048;
                           }
                        }

                        this._receivingDatagrams = null;
                        this._rxBuffer = null;
                        if (this._statusScreen != null) {
                           this._statusScreen.stop();
                           this._statusScreen = null;
                        }
                     }
                  }

                  this.setReply(32770);
                  this._commLink.close();
                  if (this._linkUp) {
                     this._linkUp = false;

                     label1075:
                     try {
                        this.broadcastStartStop(false);
                     } finally {
                        break label1075;
                     }
                  }

                  this._receivingDatagrams = null;
                  this._rxBuffer = null;
                  if (this._statusScreen != null) {
                     this._statusScreen.stop();
                     this._statusScreen = null;
                  }

                  return;
               }

               this.setReply(32770);
               this._commLink.close();
               if (this._linkUp) {
                  this._linkUp = false;

                  label1070:
                  try {
                     this.broadcastStartStop(false);
                  } finally {
                     break label1070;
                  }
               }

               this._receivingDatagrams = null;
               this._rxBuffer = null;
               if (this._statusScreen != null) {
                  this._statusScreen.stop();
                  this._statusScreen = null;
               }

               return;
            }

            this.setReply(32770);
            this._commLink.close();
            if (this._linkUp) {
               this._linkUp = false;

               label1065:
               try {
                  this.broadcastStartStop(false);
               } finally {
                  break label1065;
               }
            }

            this._receivingDatagrams = null;
            this._rxBuffer = null;
            if (this._statusScreen != null) {
               this._statusScreen.stop();
               this._statusScreen = null;
            }

            return;
         }

         this.setReply(32770);
         this._commLink.close();
         if (this._linkUp) {
            this._linkUp = false;

            label1060:
            try {
               this.broadcastStartStop(false);
            } finally {
               break label1060;
            }
         }

         this._receivingDatagrams = null;
         this._rxBuffer = null;
         if (this._statusScreen != null) {
            this._statusScreen.stop();
            this._statusScreen = null;
         }

         return;
      }

      this.setReply(32770);
      this._commLink.close();
      if (this._linkUp) {
         this._linkUp = false;

         label1055:
         try {
            this.broadcastStartStop(false);
         } finally {
            break label1055;
         }
      }

      this._receivingDatagrams = null;
      this._rxBuffer = null;
      if (this._statusScreen != null) {
         this._statusScreen.stop();
         this._statusScreen = null;
      }
   }

   @Override
   protected final void processReceivedDatagram(Datagram packet) {
   }

   private final short readBigShort(byte[] b, int offset) {
      return (short)((b[offset] & 255) << 8 | b[offset + 1] & 0xFF);
   }

   private final int getReplyValue() {
      return this.readBigShort(this._rxBuffer, this._rxBase) & 65535;
   }

   @Override
   public final int getMaximumLength() {
      return 1024;
   }

   @Override
   public final void init() {
      super.init(null);
      this._profile = new ProfileString();
      if (USBPortInternal.isSupported()) {
         new USBTransport(this, this._profile);
      }

      if (BluetoothSerialPort.isSupported() && !InternalServices.isFermion()) {
         new BluetoothTransport(this, this._profile);
      }
   }
}
