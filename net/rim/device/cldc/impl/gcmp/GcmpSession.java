package net.rim.device.cldc.impl.gcmp;

import javax.microedition.io.DatagramConnection;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.RegistrationUtilities;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.UdpAddress;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.datarecovery.DataRecovery;
import net.rim.device.cldc.io.srp.SrpListener;
import net.rim.device.cldc.io.srp.SrpUtils;
import net.rim.device.internal.system.NvStore;
import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.PersistentInteger;

final class GcmpSession extends Thread implements GcmpEvents, RadioStatusListener, SrpListener {
   private DatagramConnection _conn;
   private UdpAddress _address;
   private String _name;
   private int _apnId;
   private int _mask;
   private byte _hashAScheme;
   private byte[] _hashA;
   private byte[] _randB;
   private int _nextAckRequestId;
   private int _ackResponseId;
   private int _currentBackoff;
   private int _waitState;
   private int _nextAction;
   private long _lastSentTime;
   private long _coverageLossTime;
   private int _lastChallengeTime;
   private DatagramBase _dgram;
   private boolean _coverage;
   private boolean _suspendFlag;
   private int _inactivityTmo;
   private boolean _conditionalPinging;
   private int _tunnelHoldTmo;
   private int _defaultMask;
   private DataRecovery _dataRecovery;
   private static final int DEF_INACTIVITY_TMO = 900000;
   private static final boolean DEF_CONDITIONAL_PINGING = false;
   private static final int DEF_TUNNEL_HOLD_TMO = 0;
   private static final int MAX_INACTIVITY_TMO = 2147483;
   private static final int MIN_INACTIVITY_TMO = 1000;
   private static final int DELAY_SEND_TMO = 15000;
   private static final int ACK_RESPONSE_TMO = 15000;
   private static final int START_BACKOFF_TMO = 120000;
   private static final int MAX_BACKOFF_TMO = 1920000;
   private static final int COVERAGE_LOSS_TMO = !RadioInfo.areWAFsSupported(2)
         && (!RadioInfo.areWAFsSupported(1) || (RadioInternal.get3GPPSupportedRats() & 2) == 0)
      ? 15000
      : 300000;
   private static final int WAIT_DELAY_SEND = 0;
   private static final int WAIT_ACK_RESPONSE = 1;
   private static final int WAIT_BACKOFF = 2;
   private static final int WAIT_INACTIVITY = 3;
   private static final int WAIT_OUT_OF_COVERAGE = 4;
   private static final int ACTION_NONE = 0;
   private static final int ACTION_DELAY_SEND = 1;
   private static final int ACTION_KICK = 2;
   private static final int ACTION_RETRY = 3;
   private static final int ACTION_BACKOFF = 4;
   private static final int ACTION_DEACTIVATE = 5;
   public static final int DEF_MASK = 5504;
   private static final int PER_INACTIVITY_TMO = PersistentInteger.getId(-1673931206114386243L, 900000);
   private static final int PER_CONDITIONAL_PINGING = PersistentInteger.getId(-1673931206114386242L, 0);
   private static final int PER_TUNNEL_HOLD_TMO = PersistentInteger.getId(-1673931206114386241L, 0);
   private static final int PER_MASK = PersistentInteger.getId(-1673931206114386240L, 5504);

   final boolean isAddressed(UdpAddress address) {
      return (this._address.getIpAddressInt() == -1 || address.getIpAddressInt() == -1 || this._address.getIpAddressInt() == address.getIpAddressInt())
         && (this._address.getDestPort() == -1 || address.getDestPort() == -1 || this._address.getDestPort() == address.getDestPort())
         && (this._address.getSrcPort() == -1 || address.getSrcPort() == -1 || this._address.getSrcPort() == address.getSrcPort())
         && (this._address.getApn() == null || address.getApn() == null || this._address.getApn().equalsIgnoreCase(address.getApn()));
   }

   final void deactivate() {
      synchronized (this._address) {
         this.setNextAction(5);
         this._address.notify();
      }
   }

   final void packetSent() {
      synchronized (this._address) {
         this._lastSentTime = System.currentTimeMillis();
         if (this._waitState == 0 || this._waitState == 3) {
            this.setNextAction(0);
            this._waitState = 3;
            this._address.notify();
         }
      }
   }

   final void kick(int mask, int ackResponseId, byte hashAScheme, byte[] hashA) {
      synchronized (this._address) {
         if ((mask & 16) != 0) {
            int time = (int)System.currentTimeMillis();
            if (time - this._lastChallengeTime < 60000) {
               return;
            }

            this._lastChallengeTime = time;
         }

         this._mask = mask != 0 ? mask : this._defaultMask;
         this._ackResponseId = ackResponseId;
         this._hashAScheme = hashAScheme;
         this._hashA = hashA;
         if (this._waitState == 0 || this._waitState == 1 || this._waitState == 2 || this._waitState == 3) {
            this.setNextAction(2);
            this._waitState = (this._mask & 1044) != 0 ? 1 : 3;
            this._address.notify();
         }
      }
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
      if (apn == this._apnId) {
         synchronized (this._address) {
            if (state != 0 && state != 3) {
               if (state == 1) {
                  EventLogger.logEvent(-1673931206114386243L, 1195593836, 5);
               }
            } else if (this._waitState != 0 && this._waitState != 1 && this._waitState != 2 && this._waitState != 3) {
               this._mask = this._defaultMask;
               EventLogger.logEvent(-1673931206114386243L, 1195593831, 5);
            } else {
               EventLogger.logEvent(-1673931206114386243L, 1195593843, 0);
               this.setNextAction(0);
               this._waitState = 0;
               this._address.notify();
            }
         }
      }
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
   public final void srpRouteStateChanged(int linkType, int connectionType, boolean routeState) {
      if (linkType == 0 && connectionType == 1) {
         synchronized (this._address) {
            if (this._suspendFlag != routeState) {
               this._suspendFlag = routeState;
               if (!routeState) {
                  if (this._waitState != 0 && this._waitState != 1 && this._waitState != 2 && this._waitState != 3) {
                     EventLogger.logEvent(-1673931206114386243L, 1195595628, 5);
                  } else {
                     EventLogger.logEvent(-1673931206114386243L, 1195595635, 0);
                     this.setNextAction(0);
                     this._waitState = 0;
                     this._address.notify();
                  }
               } else {
                  EventLogger.logEvent(-1673931206114386243L, 1195595623, 5);
               }
            }
         }
      }
   }

   @Override
   public final int getLinkType() {
      return 0;
   }

   @Override
   public final int getConnectionType() {
      return 1;
   }

   @Override
   public final void srpServiceStateChanged(String service, int capabilities, boolean serviceState) {
   }

   @Override
   public final void signalLevel(int level) {
      boolean coverage = level != -256;
      if (coverage != this._coverage) {
         this._coverage = coverage;
         synchronized (this._address) {
            if (!coverage) {
               this._coverageLossTime = System.currentTimeMillis();
               if (this._waitState != 4) {
                  if (this._waitState == 0 || this._waitState == 1 || this._waitState == 2) {
                     this._mask = this._defaultMask;
                  }

                  EventLogger.logEvent(-1673931206114386243L, 1195590510, 0);
                  this.setNextAction(0);
                  this._waitState = 4;
                  this._address.notify();
               } else {
                  EventLogger.logEvent(-1673931206114386243L, 1195590508, 5);
               }
            } else {
               int time = (int)(System.currentTimeMillis() - this._coverageLossTime);
               if (this._waitState == 0 || time > COVERAGE_LOSS_TMO || this._mask != 0) {
                  EventLogger.logEvent(-1673931206114386243L, 1195590515, 0);
                  this.setNextAction(0);
                  this._waitState = 0;
                  this._address.notify();
               } else if (this._waitState != 1 && this._waitState != 2) {
                  EventLogger.logEvent(-1673931206114386243L, 1195590505, 0);
                  this.setNextAction(0);
                  this._waitState = 3;
                  this._address.notify();
               } else {
                  EventLogger.logEvent(-1673931206114386243L, 1195590503, 5);
               }
            }
         }
      }
   }

   private final void pingTmoChange(int pingTmo, boolean conditionalPinging) {
      if (pingTmo > 0 && pingTmo <= 2147483) {
         synchronized (this._address) {
            this._inactivityTmo = pingTmo * 1000;
            if (this._waitState == 3) {
               this.setNextAction(0);
               this._address.notify();
            }
         }
      }
   }

   private final void tunnelHoldTmoChange(int tunnelHoldTmo) {
   }

   private final void changeAck(boolean ack) {
      synchronized (this._address) {
         if (ack) {
            this._defaultMask |= 1024;
         } else {
            this._defaultMask &= -1025;
         }
      }
   }

   GcmpSession(DatagramConnection conn, UdpAddress address, String name) {
      this._conn = conn;
      this._address = address;
      this._name = name;

      label27:
      try {
         this._apnId = RadioInternal.registerAccessPointNumber(address.getApn());
      } finally {
         break label27;
      }

      this._dgram = (DatagramBase)(new Object(
         null, 0, 0, (DatagramAddressBase)(new Object(address.getIpAddressInt(), address.getDestPort(), address.getSrcPort(), address.getApn(), 4))
      ));
      this._currentBackoff = 120000;
      this._inactivityTmo = PersistentInteger.get(PER_INACTIVITY_TMO);
      this._conditionalPinging = PersistentInteger.get(PER_CONDITIONAL_PINGING) != 0;
      this._tunnelHoldTmo = PersistentInteger.get(PER_TUNNEL_HOLD_TMO);
      this._defaultMask = PersistentInteger.get(PER_MASK);
      this._dataRecovery = DataRecovery.getInstance();
      this._lastChallengeTime = (int)System.currentTimeMillis() - 120000;
      ProtocolDaemon.getInstance().startThread(this);
   }

   private final void ackResponse(int sequence) {
      synchronized (this._address) {
         this._currentBackoff = 120000;
         if (this._waitState == 1 || this._waitState == 2) {
            this.setNextAction(0);
            this._waitState = 3;
            this._address.notify();
         }
      }

      this._dataRecovery.fileReport(0);
   }

   private final void resultResponse(byte challengeResult, byte hashBScheme, byte[] hashB) {
      if (challengeResult != 1) {
         EventLogger.logEvent(-1673931206114386243L, 1397778034, 3);
      } else if (this._randB == null) {
         EventLogger.logEvent(-1673931206114386243L, 1397780067, 3);
      } else if (hashB != null) {
         if (hashBScheme == 2) {
            if (RegistrationUtilities.checkMAC(this._randB, NvStore.readData(36), hashB)) {
               if (this._hashA == null) {
                  EventLogger.logEvent(-1673931206114386243L, 1397780075, 3);
               }
            } else {
               EventLogger.logEvent(-1673931206114386243L, 1397782118, 3);
            }
         } else {
            EventLogger.logEvent(-1673931206114386243L, 1397781875, 3);
         }
      } else {
         EventLogger.logEvent(-1673931206114386243L, 1397780082, 0);
      }

      this._hashAScheme = 0;
      this._hashA = null;
      this._randB = null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      int lastMask = 0;
      GcmpPacket packet = new GcmpPacket();
      this._coverage = RadioInfo.getSignalLevel() != -256;
      ProtocolDaemon.getInstance().addRadioListener(this);
      if (StringUtilities.strEqual(this._name, "Relay")) {
         this._suspendFlag = SrpUtils.getInstance().getRouteState(0, 1);
         SrpUtils.getInstance().addListener(this);
      }

      this._lastSentTime = this._coverageLossTime = System.currentTimeMillis();

      while (true) {
         if (packet.mask != 0) {
            if (this._suspendFlag) {
               this._lastSentTime = System.currentTimeMillis();
               this._waitState = 3;
               this.setNextAction(0);
            } else {
               packet.encode(this._dgram);
               EventLogger.logEvent(-1673931206114386243L, (packet.mask & 22) == 0 ? 1195594608 : 1195594595, 0);
               boolean var14 = false /* VF: Semaphore variable */;

               label243:
               try {
                  var14 = true;
                  this._conn.send(this._dgram);
                  var14 = false;
               } finally {
                  if (var14) {
                     EventLogger.logEvent(-1673931206114386243L, 1195594598, 2);
                     break label243;
                  }
               }

               lastMask = packet.mask;
               packet.reset();
            }
         }

         synchronized (this._address) {
            switch (this._nextAction) {
               case 0:
                  break;
               case 1:
               default:
                  this._waitState = 0;
                  break;
               case 3:
                  if (this._currentBackoff < 1920000) {
                     this._currentBackoff <<= 1;
                  }

                  this._lastChallengeTime -= 120000;
                  this._mask = lastMask;
               case 2:
                  packet.mask = this._mask != 0 ? this._mask : this._defaultMask;
                  if ((packet.mask & 16) != 0) {
                     this._lastChallengeTime = (int)System.currentTimeMillis();
                     packet.mask |= 2;
                     this._randB = RandomSource.getBytes(32);
                  }

                  if ((packet.mask & 4) != 0) {
                     this._lastChallengeTime = (int)System.currentTimeMillis();
                     packet.challengeResponseScheme = this._hashAScheme;
                     packet.challengeResponse = this._hashA;
                  }

                  if ((packet.mask & 2) != 0) {
                     if (this._randB == null) {
                        this._randB = RandomSource.getBytes(32);
                     }

                     packet.challengeMessage = this._randB;
                  }

                  if ((packet.mask & 128) != 0) {
                     packet.pingTmo = this._inactivityTmo / 1000;
                     packet.conditionalPinging = this._conditionalPinging;
                  }

                  if ((packet.mask & 256) != 0) {
                     packet.tunnelHoldTmo = this._tunnelHoldTmo;
                  }

                  if ((packet.mask & 1024) != 0) {
                     packet.ackRequestId = ++this._nextAckRequestId;
                  }

                  if ((packet.mask & 2048) != 0) {
                     packet.ackResponseId = this._ackResponseId;
                  }

                  this._mask = 0;
                  this._ackResponseId = 0;
                  this.setNextAction(0);
                  this._waitState = (packet.mask & 1044) != 0 ? 1 : 3;
                  continue;
               case 4:
                  EventLogger.logEvent(-1673931206114386243L, 1195594606, 3);
                  this._dataRecovery.fileReport(1);
                  this._waitState = 2;
                  break;
               case 5:
                  ProtocolDaemon.getInstance().removeRadioListener(this);
                  if (StringUtilities.strEqual(this._name, "Relay")) {
                     SrpUtils.getInstance().removeListener(this);
                  }

                  return;
            }

            int tmo;
            switch (this._waitState) {
               case -1:
               case 3:
                  tmo = this._inactivityTmo - (int)(System.currentTimeMillis() - this._lastSentTime);
                  if (tmo < 1000) {
                     tmo = 1000;
                  }

                  this.setNextAction(2);
                  break;
               case 0:
               default:
                  tmo = 15000;
                  this.setNextAction(2);
                  break;
               case 1:
                  tmo = 15000;
                  this.setNextAction(4);
                  break;
               case 2:
                  tmo = this._currentBackoff;
                  this.setNextAction(3);
                  break;
               case 4:
                  tmo = 0;
                  this.setNextAction(0);
            }

            boolean var10 = false /* VF: Semaphore variable */;

            try {
               var10 = true;
               this._address.wait(tmo);
               var10 = false;
            } finally {
               if (var10) {
                  EventLogger.logEvent(-1673931206114386243L, 1195595625, 2);
                  continue;
               }
            }
         }
      }
   }

   private final void setNextAction(int nextAction) {
      if (this._nextAction != 5) {
         this._nextAction = nextAction;
      }
   }

   static final void processReceivedPacket(DatagramAddressBase addressBase, DataBuffer buf) {
      GcmpPacket packet = new GcmpPacket();
      if (addressBase instanceof Object) {
         UdpAddress addr = (UdpAddress)addressBase;
         packet.ipAddress = addr.getIpAddress();
         packet.destinationPort = addr.getDestPort();
         packet.sourcePort = addr.getSrcPort();
         packet.apn = addr.getApn();
      }

      packet.decode(buf);
      UdpAddress addr = (UdpAddress)(new Object(packet.ipAddress, packet.sourcePort, packet.destinationPort, packet.apn, 4));
      GcmpSession session = Gcmp.getInstance().makeSession(addr);
      if (session == null) {
         EventLogger.logEvent(-1673931206114386243L, 1195594292, 3);
      } else {
         EventLogger.logEvent(-1673931206114386243L, (packet.mask & 22) == 0 ? 1195594352 : 1195594339, 0);
         session.ackResponse(packet.ackResponseId);
         if ((packet.mask & 128) != 0) {
            session.pingTmoChange(packet.pingTmo, packet.conditionalPinging);
         }

         if ((packet.mask & 256) != 0) {
            session.tunnelHoldTmoChange(packet.tunnelHoldTmo);
         }

         if ((packet.mask & 4096) != 0) {
            session.changeAck(packet.ackInfo);
         }

         if ((packet.mask & 8) != 0) {
            byte hashBScheme = 0;
            byte[] hashB = null;
            if ((packet.mask & 4) != 0) {
               hashBScheme = packet.challengeResponseScheme;
               hashB = packet.challengeResponse;
            }

            session.resultResponse(packet.challengeResult, hashBScheme, hashB);
         }

         if ((packet.mask & 1027) != 0) {
            int mask = 0;
            int ackResponseId = 0;
            byte hashAScheme = 0;
            byte[] hashA = null;
            if ((packet.mask & 1) != 0) {
               mask |= 4480;
            }

            if ((packet.mask & 1024) != 0) {
               mask |= 2048;
               ackResponseId = packet.ackRequestId;
            }

            if ((packet.mask & 2) != 0) {
               mask |= 4;
               byte[] challengeResponse = new byte[20];
               RegistrationUtilities.mac(packet.challengeMessage, NvStore.readData(36), challengeResponse, 0);
               hashAScheme = 2;
               hashA = challengeResponse;
               mask |= 2;
            }

            session.kick(mask, ackResponseId, hashAScheme, hashA);
         }
      }
   }
}
