package net.rim.device.apps.internal.supl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import javax.microedition.io.SocketConnection;
import net.rim.device.api.crypto.HMAC;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.tls.tls10.TLS10Connection;
import net.rim.device.api.gps.GPS;
import net.rim.device.api.gps.GPSLocationExtended;
import net.rim.device.api.gps.LCS;
import net.rim.device.api.gps.LCSListener;
import net.rim.device.api.system.Application;

public final class SuplSession extends Thread implements LCSListener {
   private SuplOptions options;
   private SessionId sessionId;
   private byte[] suplInit = null;
   private byte[] ver;
   private byte state;
   SuplSession$TLSReceiveThread receiveThread = null;
   private TLS10Connection tlsConnection;
   private SocketConnection socketConnection;
   private OutputStream os;
   private InputStream is;
   private SuplEventMonitor eventMonitor;
   private SuplUtilities utils;
   private Timer timer;
   static final boolean SUPL_DEBUG = true;
   static final byte STATE_ESTABLISH_CONNECTION = 0;
   static final byte STATE_SUPL_INIT_RECEIVED = 1;
   static final byte STATE_SEND_SUPL_START = 2;
   static final byte STATE_WAIT_FOR_SUPL_RESP = 3;
   static final byte STATE_WAIT_FOR_SUPL_POS = 4;
   static final byte STATE_CLOSING = 5;
   static final long TIMER_CONN_EST_GUARD_DELAY = 30000L;
   static final long TIMER_UT1_DELAY = 10000L;
   static final long TIMER_UT2_DELAY = 10000L;
   static final long TIMER_UT3_DELAY = 10000L;
   static int sessionIdCounter = 0;
   static final byte[] SUPPORTED_POS_METHODS = new byte[]{0, 2, 1, 3, 4};

   final void establishConnection() throws SuplException {
      this.timer = new Timer();
      System.out.println("Starting Connection guard timer");
      this.timer.schedule(new SuplSession$UTimerTask(this, 0), 30000);
      this.receiveThread = new SuplSession$TLSReceiveThread(this, this);
      this.receiveThread.start();
      SuplEvent receivedEvent = this.eventMonitor.getEvent();
      switch (receivedEvent.getEventType()) {
         case 1:
            System.out.println("Unexpected event");
            throw new SuplException();
         case 2:
            throw new SuplException();
         case 3:
         default:
            Boolean wrapper = (Boolean)receivedEvent.getData();
            if (wrapper) {
               this.timer.cancel();
            } else {
               throw new SuplException();
            }
      }
   }

   final void sendUlpMsg(UlpMessage msg) {
      Ulp pdu = new Ulp(msg, this.sessionId);
      System.out.println("Send ULP: ");
      pdu.print();
      byte[] payload = pdu.encode();
      System.out.println("Sending ULP message of length: " + payload.length);

      try {
         this.os.write(payload);
         this.os.flush();
      } finally {
         System.out.println("Error sending data...");
         return;
      }
   }

   public final void handleReceivedSuplInit(byte[] init) throws SuplException {
      Ulp ulpPdu = new Ulp();
      this.calcVer(init);
      ulpPdu.decode(init);
      ulpPdu.print();
      if (ulpPdu.sessionId.optionals != 1) {
         this.sessionId = ulpPdu.sessionId;
         throw new SuplException((byte)12);
      }

      this.sessionId.addSlpSessionId(ulpPdu.sessionId.slpSessionId);
      if (ulpPdu.version.major > 1) {
         System.out.println("SUPL: Status Version Not Supported!");
         throw new SuplException((byte)10);
      }

      if (!(ulpPdu.ulpMessage instanceof SuplInit)) {
         throw new SuplException((byte)2);
      }

      SuplInit suplInitMessage = (SuplInit)ulpPdu.ulpMessage;
      if (!suplInitMessage.slpMode.mode) {
         if ((suplInitMessage.optionals & 16) == 16) {
            boolean userAccepted = this.utils.notifyAndVerify(suplInitMessage.notification.notificationType.type);
            if (!userAccepted) {
               throw new SuplException((byte)18);
            }

            if (suplInitMessage.posMethod.method == 9) {
               throw new SuplException((byte)19);
            }
         }

         Position storedPosition = null;
         if ((suplInitMessage.optionals & 4) == 4) {
            boolean QOPSatisfied = true;
            GPSLocationExtended cachedLocation = new GPSLocationExtended();
            GPS.getLocation(cachedLocation, 1);
            if (this.utils.cachedPositionAcceptable(cachedLocation, suplInitMessage.qop)) {
            }
         }

         this.sendSuplPosInit(suplInitMessage.posMethod, storedPosition);
         this.ver = null;
      } else {
         throw new SuplException((byte)13);
      }
   }

   public final void connectionStatusEvent(boolean connectionUp) {
      System.out.println("Issuing CONNECTION_STATUS_EVENT");
      this.eventMonitor.issueEvent(new SuplEvent(3, new Boolean(connectionUp)));
   }

   public final void receivedUlpPduEvent(Ulp ulpPdu) {
      System.out.println("Issuing RECEIVED_ULP_MESSAGE_EVENT");
      this.eventMonitor.issueEvent(new SuplEvent(1, ulpPdu));
   }

   @Override
   public final void RRLPPayloadIndicationEvent(int sessionId, int length) {
      if (sessionId == this.sessionId.setSessionId.sessionId) {
         System.out.println("Issuing RRLP_PAYLOAD_INDICATION_EVENT");
         this.eventMonitor.issueEvent(new SuplEvent(0, new Integer(length)));
      }
   }

   @Override
   public final void reqAssistDataEvent() {
      System.out.println("SUPL Session: LCS requested assistance data over SUPL");
   }

   @Override
   public final void notificationRequest(int type, int length) {
   }

   @Override
   public final void verificationTimerExpiry() {
   }

   private final void handleReceivedSuplResponse(SuplResponse suplResponse) throws SuplException {
      System.out.println("Stopping UT1");
      this.timer.cancel();
      if (this.state != 3) {
         throw new SuplException((byte)2);
      }

      boolean supportedPosMethod = false;

      for (int i = 0; i < SUPPORTED_POS_METHODS.length; i++) {
         if (suplResponse.posMethod.method == SUPPORTED_POS_METHODS[i]) {
            supportedPosMethod = true;
            break;
         }
      }

      if (!supportedPosMethod) {
         throw new SuplException((byte)7);
      }

      this.sendSuplPosInit(suplResponse.posMethod, null);
   }

   private final void handleReceivedSuplPos(SuplPos suplPos) throws SuplException {
      System.out.println("Stopping UT2 or UT3");
      this.timer.cancel();
      if (this.state != 4) {
         throw new SuplException((byte)2);
      }

      System.out.println("Sending RRLP payload of " + suplPos.posPayload.payload.length + " bytes to LCS thread");
      if (!LCS.processRrlp(suplPos.posPayload.payload, this.sessionId.setSessionId.sessionId)) {
         System.out.println("Error in RimLcsProcessRrlp");
         throw new SuplException((byte)3);
      }
   }

   private final void handleReceivedSuplEnd(SuplEnd suplEnd) {
      this.timer.cancel();
      if ((suplEnd.optionals & 2) == 2) {
      }

      this.state = 5;
   }

   private final void sendSuplEnd(byte status) {
      this.timer.cancel();
      SuplEnd suplEnd;
      if (this.ver != null) {
         suplEnd = new SuplEnd(status, this.ver);
      } else {
         suplEnd = new SuplEnd(status);
      }

      System.out.println("Sending SUPL END");
      this.sendUlpMsg(suplEnd);
      this.state = 5;
   }

   private final void sendSuplPos(byte[] payload) {
      SuplPos suplPos = new SuplPos(payload);
      System.out.println("Sending SUPL POS");
      this.sendUlpMsg(suplPos);
      this.state = 4;
      this.timer = new Timer();
      System.out.println("Starting UT3");
      this.timer.schedule(new SuplSession$UTimerTask(this, 3), 10000);
   }

   private final void sendSuplStart() {
      SuplStart suplStart = new SuplStart(this.options.getSetCapabilities());
      System.out.println("Sending SUPL START");
      this.sendUlpMsg(suplStart);
      this.timer = new Timer();
      System.out.println("Starting UT1");
      this.timer.schedule(new SuplSession$UTimerTask(this, 1), 10000);
      this.state = 3;
   }

   private final void sendSuplPosInit(PosMethod posMethod, Position storedPosition) {
      SuplPosInit suplPosInit = new SuplPosInit(posMethod, storedPosition, this.ver, this.options.getSetCapabilities());
      System.out.println("Sending SUPL POS INIT");
      this.sendUlpMsg(suplPosInit);
      this.state = 4;
      this.timer = new Timer();
      System.out.println("Starting UT2");
      this.timer.schedule(new SuplSession$UTimerTask(this, 2), 10000);
   }

   private final void timerExpired(int timerId) {
      System.out.println("Issuing UTIMER_EXPIRY_EVENT");
      this.eventMonitor.issueEvent(new SuplEvent(2, new Integer(timerId)));
   }

   private final void bindLCSListener() {
      LCS.addListener(Application.getApplication(), this);
   }

   private final void unbindLCSListener() {
      LCS.removeListener(Application.getApplication(), this);
   }

   public SuplSession(byte[] _suplInit) {
      this.reset();
      this.suplInit = _suplInit;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      int prevState = -1;

      label359:
      try {
         Thread.sleep(1500);
      } finally {
         break label359;
      }

      while (true) {
         if (this.state != prevState) {
            System.out.println("Switching to state: " + this.state);
         }

         prevState = this.state;
         switch (this.state) {
            case -1:
               break;
            case 0:
            default:
               try {
                  this.establishConnection();
                  System.out.println("TLS connection established");
                  if (this.suplInit == null) {
                     this.state = 2;
                  } else {
                     this.state = 1;
                  }
               } catch (SuplException e) {
                  System.out.println(e.toString());
                  if (this.suplInit == null) {
                     LCS.assistDataRequestFailed();
                  }

                  this.state = 5;
               }
               break;
            case 1:
               SuplException var68;
               try {
                  try {
                     this.handleReceivedSuplInit(this.suplInit);
                     break;
                  } catch (SuplException var65) {
                     var68 = var65;
                  }
               } catch (Throwable var66) {
                  this.state = 5;
                  System.out.println(e.getMessage());
                  e.printStackTrace();
                  System.out.println("handleRcvSuplInit:" + e);
                  break;
               }

               System.out.println("SUPL Exception:" + var68);
               this.sendSuplEnd(var68.error.status);
               break;
            case 2:
               this.sendSuplStart();
               break;
            case 3:
            case 4:
               SuplEvent receivedEvent = this.eventMonitor.getEvent();
               switch (receivedEvent.getEventType()) {
                  case -1:
                     System.out.println("Unhandled SUPL event.");
                     this.sendSuplEnd((byte)3);
                     continue;
                  case 0:
                  default: {
                     Integer wrapper = (Integer)receivedEvent.getData();
                     byte[] txRrlpPayload = new byte[wrapper];
                     System.out.println("Processing RRLP Payload");
                     if (LCS.getTxRrlp(txRrlpPayload, this.sessionId.setSessionId.sessionId)) {
                        this.sendSuplPos(txRrlpPayload);
                     } else {
                        System.out.println("Error getting RRLP message from LCS");
                        this.sendSuplEnd((byte)6);
                     }
                     continue;
                  }
                  case 1:
                     try {
                        this.handleReceivedUlpMsg((Ulp)receivedEvent.getData());
                     } catch (SuplException e) {
                        this.sendSuplEnd(e.error.status);
                     }
                     continue;
                  case 2: {
                     Integer wrapper = (Integer)receivedEvent.getData();
                     System.out.println("UTimer " + wrapper.toString() + " expired.");
                     this.sendSuplEnd((byte)0);
                     continue;
                  }
               }
            case 5:
               if (this.options.isTLSEnabled()) {
                  System.out.println("Closing TLS connection.");

                  label337:
                  try {
                     this.tlsConnection.close();
                  } catch (Throwable var63) {
                     System.out.println("ERROR CLOSING TLS CONNECTION!");
                     System.out.println(e.toString());
                     break label337;
                  }
               }

               label334:
               try {
                  System.out.println("Closing socket connection.");
                  this.socketConnection.close();
                  this.socketConnection = null;
               } catch (Throwable var62) {
                  System.out.println("ERROR CLOSING SOCKET CONNECTION!");
                  System.out.println(e.toString());
                  break label334;
               }

               label331:
               try {
                  System.out.println("Closing input stream.");
                  this.is.close();
               } catch (Throwable var61) {
                  System.out.println("ERROR CLOSING INPUT STREAM!");
                  System.out.println(e.toString());
                  break label331;
               }

               label328:
               try {
                  System.out.println("Closing output stream.");
                  this.os.close();
               } catch (Throwable var60) {
                  System.out.println("ERROR CLOSING OUTPUT STREAM!");
                  System.out.println(e.toString());
                  break label328;
               }

               this.unbindLCSListener();
               return;
         }
      }
   }

   public SuplSession() {
      this.reset();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void calcVer(byte[] suplInitMsg) {
      Fqdn slpAddress = this.options.getSlpAddress();
      HMACKey key = new HMACKey(slpAddress.domainName.getBytes());
      SHA1Digest digest = new SHA1Digest();
      HMAC hmac = new HMAC(key, digest);
      int verLength = hmac.getLength();
      hmac.update(suplInitMsg, 0, suplInitMsg.length);
      this.ver = new byte[verLength];

      label35:
      try {
         hmac.getMAC(this.ver, 0);
      } catch (Throwable var9) {
         System.out.println(e.toString());
         e.printStackTrace();
         break label35;
      }

      System.out.println("VER is: ");

      for (int i = 0; i < verLength; i++) {
         System.out.print(Integer.toHexString(255 & this.ver[i]) + " ");
      }
   }

   private final void reset() {
      this.utils = new SuplUtilities();
      this.options = new SuplOptions();
      this.sessionId = new SessionId(new SetSessionId(sessionIdCounter++));
      this.bindLCSListener();
      this.eventMonitor = new SuplEventMonitor();
      SuplEventMonitor.initialize();
      this.state = 0;
   }

   private final void handleReceivedUlpMsg(Ulp ulp) throws SuplException {
      System.out.println("Received ULP: ");
      if (ulp == null) {
         throw new SuplException((byte)4);
      }

      if (ulp.version.major > 1) {
         throw new SuplException((byte)10);
      }

      if (!this.sessionId.equals(ulp.sessionId)) {
         this.sessionId = ulp.sessionId;
         throw new SuplException((byte)12);
      }

      if (ulp.ulpMessage instanceof SuplResponse) {
         System.out.println("Received SUPL RESP");
         this.sessionId.addSlpSessionId(ulp.sessionId.slpSessionId);
         this.handleReceivedSuplResponse((SuplResponse)ulp.ulpMessage);
      } else if (ulp.ulpMessage instanceof SuplPos) {
         System.out.println("Received SUPL POS");
         this.handleReceivedSuplPos((SuplPos)ulp.ulpMessage);
      } else if (ulp.ulpMessage instanceof SuplEnd) {
         System.out.println("Received SUPL END");
         this.handleReceivedSuplEnd((SuplEnd)ulp.ulpMessage);
      } else {
         throw new SuplException((byte)2);
      }
   }
}
