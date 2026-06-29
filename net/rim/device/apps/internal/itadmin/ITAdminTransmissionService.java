package net.rim.device.apps.internal.itadmin;

import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentInternal;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.AbstractTransmissionService;
import net.rim.device.apps.api.transmission.Packet;
import net.rim.device.apps.api.transmission.PacketReceiver;
import net.rim.device.apps.api.transmission.TransmissionStatusListener;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.cldc.io.gme.GMEDatagram;
import net.rim.device.internal.deviceoptions.Owner;
import net.rim.device.internal.provisioning.ActivationService;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.LockEventLogger;
import net.rim.device.internal.system.NvStore;
import net.rim.device.internal.system.Security;

class ITAdminTransmissionService extends AbstractTransmissionService implements PacketReceiver, GlobalEventListener {
   private IntIntHashtable _ackTable = (IntIntHashtable)(new Object());
   private ApplicationRegistry _appRegistry;
   private Security _security;
   private ServiceRecord[] _overridingServiceRecords = null;
   private ContextObject _transmissionContext = (ContextObject)(new Object());
   private static final byte ITADMIN_VERSION = 16;
   private static final String ITADMIN_CID = "ITADMIN";
   private static final int ITADMIN_COMMAND_TIMEOUT_THRESHOLD = 900000;
   private static final byte MULTIPLE_COMMANDS = 0;
   private static final byte SET_PASSWORD = 1;
   private static final byte GET_PASSWORD_STATUS = 2;
   private static final byte SET_IT_POLICY = 3;
   private static final byte GET_IT_POLICY = 4;
   private static final byte CHANGE_PASWORD = 5;
   private static final byte KILL_HANDHELD = 6;
   private static final byte LOCK_HANDHELD = 7;
   private static final byte SET_OWNER_INFO = 8;
   private static final byte SET_PEER_TO_PEER_KEY = 9;
   private static final byte UPDATE_SERVICE_DATA = 10;
   private static final byte SWITCH_SERVICE = 11;
   private static final byte DELETE_SERVICE = 12;
   private static final byte REQUEST_KEY_REGENERATION = 13;
   private static final byte FLUSH_HANDHELD_AGENT = 16;
   private static final byte REQUEST_UPDATE_SERVICE_DATA = 14;
   private static final byte COMMAND_UNRESOLVED = 15;
   private static final byte REJECT_TIMESTAMP = 1;
   private static final byte REJECT_SECURITY_VIOLATION = 2;
   private static final byte EVENT_AUTHENTICATION_FAILED = 3;
   private static final byte EVENT_MALFORMED_POLICY = 4;
   private static final byte SEND_ACK_FLAG = 64;
   private static final int ACK_SUCCESSFUL = 192;
   private static final int ACK_FAILURE = 128;
   private static final int CURRENT_SERVER_UID = 1;
   private static final int NEW_SERVER_UID = 2;
   private static final int TO_BE_DELETED_SERVER_UID = 1;
   private static final byte PASSWORD = 1;
   private static final byte D_VALUE = 2;
   private static final byte K_VALUE = 3;
   private static final byte H_VALUE = 4;
   private static final byte INVALID_PASSWORD = 65;
   private static final byte UNKNOWN_COMMAND_VERSION = 66;
   private static final byte CONTENT_PROTECTION_ENABLED = 67;
   private static final byte SET_PASSWORD_FAILED = 68;
   private static final byte MISSING_PUBLIC_BES_KEY = 69;
   private static final byte INCOMPATIBLE_BES = 70;
   private static final byte OWNER_NAME = 1;
   private static final byte OWNER_INFO = 2;

   public synchronized void transmitObject(String typeString, Object anObject, Object contextObject) {
   }

   @Override
   public synchronized void receivePacket(DataBuffer packetDataBuffer, Object contextObject) {
      GMEDatagram gmeDatagram = this.verifyGMEDatagram(packetDataBuffer, true);
      if (gmeDatagram != null) {
         if (super._tLogger != null) {
            super._tLogger.bytesReceived(this, 1, null, packetDataBuffer.getLength(), packetDataBuffer.getArray());
         }

         String uid = gmeDatagram.getGMEAddress().getSrc().address;
         this.processDataBuffer(packetDataBuffer, uid);
      }
   }

   @Override
   protected ServiceRecord initServiceRecord() {
      return super._serviceRecord;
   }

   @Override
   protected DatagramConnection createConnection(ServiceRecord serviceRecord) {
      return null;
   }

   @Override
   protected DatagramConnection createSendingConnection(ServiceRecord serviceRecord) {
      String gcfURL;
      if (serviceRecord == null) {
         gcfURL = "gme:ITADMIN";
      } else {
         gcfURL = ((StringBuffer)(new Object("gme:ITADMIN/"))).append(serviceRecord.getUid()).toString();
      }

      DatagramConnection sendingConnection = (DatagramConnection)Connector.open(gcfURL);
      return sendingConnection;
   }

   @Override
   protected DatagramConnection createReceivingConnection(ServiceRecord serviceRecord) {
      return (DatagramConnection)Connector.open("gme:ITADMIN");
   }

   private void init() {
      Proxy.getInstance().addGlobalEventListener(this);
      this._appRegistry = ApplicationRegistry.getApplicationRegistry();
      this._security = Security.getInstance();
      this._overridingServiceRecords = new Object[1];
      this._overridingServiceRecords[0] = (ServiceRecord)(new Object(3));
      this._overridingServiceRecords[0].setCid("ITADMIN");
      this._overridingServiceRecords[0].setType(0);
      this._overridingServiceRecords[0].setEncryptionMode(2);
      this._overridingServiceRecords[0].setName("ITADMIN");
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private synchronized void processDataBuffer(DataBuffer data, String uid) {
      int timeStamp = 0;
      int length = 0;
      int ack = 0;
      byte version = 0;
      byte command = 0;
      byte commandFlag = 0;
      byte commandVersion = 0;
      byte[] buffer = null;
      boolean multipleCommands = false;
      DataBuffer itadminData = (DataBuffer)(new Object(true));

      label435:
      try {
         version = data.readByte();
         itadminData.writeByte(version);
         timeStamp = data.readInt();
         itadminData.writeInt(timeStamp);
         if (ITPolicyInternal.getITAdminTimeStamp() > timeStamp) {
            ITAdminEventLogger.logEvent(1162494804, 0);
            this.sendAcknowledge(version, timeStamp, (byte)15, (byte)1, 128, uid, (byte)1, null);
            ActivationService.getInstance().abortTransaction(uid, (byte)11);
            return;
         }

         this._ackTable.clear();
         if (data.available() > 1) {
            while (!data.eof()) {
               boolean reckognizedCommand = true;
               command = data.readByte();
               commandFlag = data.readByte();
               length = data.readCompressedInt();
               if (length > 0) {
                  commandVersion = data.readByte();
               } else {
                  commandVersion = -1;
               }

               if (command != 3
                  && (
                     !ActivationService.getInstance().isActivationInProgress() && !ITPolicyInternal.verifyBoundITAdminService(uid, true)
                        || command != 0 && !ITPolicyInternal.verifyITAdminService(uid, true)
                  )) {
                  ITAdminEventLogger.logEvent(1397313110, 2);
                  this.sendAcknowledge(version, timeStamp, command, commandVersion, 128, uid, (byte)2, null);
                  ActivationService.getInstance().abortTransaction(uid, (byte)11);
                  return;
               }

               switch (command) {
                  case 0:
                     ITAdminEventLogger.logEvent(1297435732, 0);
                     if ((commandFlag & 64) == 64) {
                        multipleCommands = true;
                     }
                     break;
                  case 1:
                     ITAdminEventLogger.logEvent(1347639108, 0);
                     reckognizedCommand = false;
                     byte[] passwordBytes = null;
                     byte[] K = null;
                     byte[] H = null;
                     if (commandVersion == 16) {
                        passwordBytes = new byte[length - 1];
                        data.readFully(passwordBytes, 0, length - 1);
                     } else {
                        if (commandVersion != 32) {
                           if (length > 1) {
                              data.skipBytes(length - 1);
                           }

                           this.sendAcknowledge(version, timeStamp, command, commandVersion, 128, uid, (byte)66, null);
                           break;
                        }

                        while (data.available() > 2) {
                           byte tag = data.readByte();
                           int len = data.readCompressedInt();
                           if (len < 0) {
                              passwordBytes = null;
                              break;
                           }

                           switch (tag) {
                              case 0:
                              case 2:
                                 data.skipBytes(len);
                                 break;
                              case 1:
                              default:
                                 passwordBytes = new byte[len];
                                 data.readFully(passwordBytes, 0, len);
                                 break;
                              case 3:
                                 K = new byte[len];
                                 data.readFully(K, 0, len);
                                 break;
                              case 4:
                                 H = new byte[len];
                                 data.readFully(H, 0, len);
                           }
                        }
                     }

                     if (passwordBytes == null) {
                        this.sendAcknowledge(version, timeStamp, command, commandVersion, 128, uid, (byte)65, null);
                     } else {
                        this.lockHandheld(1281979504);
                        String password = (String)(new Object(passwordBytes));
                        if (this._security.isPasswordValid(password) == 0 && this._security.verifyPasswordPattern(password) == 0) {
                           Object ticket = PersistentContent.getTicket();
                           if (ticket == null && (K == null || H == null || !PersistentContentInternal.setK(K, H))) {
                              if (commandVersion == 16) {
                                 byte[] D = PersistentContentInternal.getD();
                                 byte[] BChecksum = PersistentContentInternal.getBChecksum();
                                 if (D != null && BChecksum != null && D.length != 0 && BChecksum.length != 0) {
                                    DataBuffer errorData = (DataBuffer)(new Object());
                                    errorData.write(2);
                                    errorData.writeByteArray(D);
                                    errorData.write(4);
                                    errorData.writeByteArray(BChecksum);
                                    this.sendAcknowledge(version, timeStamp, command, commandVersion, 128, uid, (byte)67, errorData.toArray());
                                 } else {
                                    this.sendAcknowledge(version, timeStamp, command, commandVersion, 128, uid, (byte)69, null);
                                 }
                              } else {
                                 this.sendAcknowledge(version, timeStamp, command, commandVersion, 128, uid, (byte)70, null);
                              }
                           } else {
                              boolean var28 = false /* VF: Semaphore variable */;

                              label382: {
                                 try {
                                    var28 = true;
                                    if (!this.setPassword(password)) {
                                       this.sendAcknowledge(version, timeStamp, command, commandVersion, 128, uid, (byte)68, null);
                                       var28 = false;
                                       break label382;
                                    }

                                    var28 = false;
                                 } finally {
                                    if (var28) {
                                       PersistentContentInternal.clearK();
                                    }
                                 }

                                 PersistentContentInternal.clearK();
                                 RIMGlobalMessagePoster.postGlobalEvent(1309561383038111736L);
                                 this.sendAcknowledge(version, timeStamp, command, commandVersion, 192, uid, (byte)0, null);
                                 break;
                              }

                              PersistentContentInternal.clearK();
                           }
                        } else {
                           this.sendAcknowledge(version, timeStamp, command, commandVersion, 128, uid, (byte)65, null);
                        }
                     }
                     break;
                  case 3:
                     ITAdminEventLogger.logEvent(1397772108, 0);
                     buffer = this.getCommandDataWithVersion(data, length, commandVersion);
                     int result = this.setOTAITPolicy(buffer);
                     if (result == 1262834258) {
                        ack = 128;
                        this.sendAcknowledge(version, timeStamp, command, commandVersion, 128, uid, (byte)3, null);
                        ActivationService.getInstance().abortTransaction(uid, (byte)11);
                     } else if (result == 1111573584) {
                        ack = 128;
                        this.sendAcknowledge(version, timeStamp, command, commandVersion, 128, uid, (byte)4, null);
                        ActivationService.getInstance().abortTransaction(uid, (byte)11);
                     } else {
                        ack = 192;
                        TLEUtilities.writeIntegerField(itadminData, 3, 1, false);
                     }
                     break;
                  case 6:
                     ITAdminEventLogger.logEvent(1263094860, 0);
                     long delay = 10000;
                     if (commandVersion == 32) {
                        delay = (long)data.readCompressedInt() * 60 * 60 * 1000;
                     }

                     this.lockHandheld(1281979499);
                     boolean startedDelayedWipeTimer = false;
                     DelayedWipeManager delayedWipeManager = (DelayedWipeManager)ApplicationRegistry.getApplicationRegistry().get(-4224498347513976669L);
                     if (delayedWipeManager != null) {
                        startedDelayedWipeTimer = delayedWipeManager.startDelayedWipe(delay);
                     }

                     if (!startedDelayedWipeTimer) {
                        if (ITPolicy.getBoolean(24, 77, false) && ITPolicyInternal.isITAdminEnabled()) {
                           NvStore.resetToFactoryDefaults();
                           CodeModuleManager.deleteThirdPartyApplications();
                        }

                        Security.getInstance().deviceUnderAttack();
                     }

                     ack = 192;
                     break;
                  case 7:
                     ITAdminEventLogger.logEvent(1280262987, 0);
                     ack = this.lockHandheld(1281979500) ? 192 : 128;
                     break;
                  case 8:
                     ITAdminEventLogger.logEvent(1397708622, 0);
                     buffer = this.getCommandDataWithVersion(data, length, commandVersion);
                     ack = this.setOwnerInfo((DataBuffer)(new Object(buffer, 0, length, false))) ? 192 : 128;
                     break;
                  case 9:
                     ITAdminEventLogger.logEvent(1397764688, 0);
                     buffer = this.getCommandDataWithVersion(data, length, commandVersion);
                     ack = this.setP2P(buffer) ? 192 : 128;
                     break;
                  case 13:
                     ITAdminEventLogger.logEvent(1264142158, 0);
                     ack = ActivationService.getInstance().regenerateKey(uid, false) != -1 ? 192 : 128;
                     if (length > 1) {
                        data.skipBytes(length - 1);
                     }
                     break;
                  case 16:
                     ITAdminEventLogger.logEvent(1179407176, 0);
                     RIMGlobalMessagePoster.postGlobalEvent(978519096100388739L);
                     ack = 192;
                     break;
                  default:
                     ITAdminEventLogger.logEvent(1431194446, 2);
                     reckognizedCommand = false;
                     if (length > 1) {
                        data.skipBytes(length - 1);
                     }
               }

               if (reckognizedCommand && (commandFlag & 64) == 64) {
                  if (!multipleCommands) {
                     this.sendAcknowledge(version, timeStamp, command, commandVersion, ack, uid, (byte)0, null);
                  } else if (command != 0) {
                     int key = commandFlag << 8 | command;
                     int value = commandVersion << 8 | ack;
                     this.addAck(key, value);
                  }
               }
            }

            if (multipleCommands) {
               this.sendAcknowledge(version, timeStamp, (byte)0, (byte)1, 192, uid, (byte)0, null);
            }
         }
      } finally {
         break label435;
      }

      TLEUtilities.writeIntegerField(itadminData, 2, 1, false);
      NvStore.writeData(5, itadminData.getArray());
      ITAdminEventLogger.logEvent(1465010516, 0);
   }

   private void addAck(int command, int ackValue) {
      this._ackTable.put(command, ackValue);
   }

   private byte[] getCommandDataWithVersion(DataBuffer data, int length, byte commandVersion) {
      byte[] buffer = new byte[length];
      data.readFully(buffer, 1, length - 1);
      buffer[0] = commandVersion;
      return buffer;
   }

   private boolean getPasswordStatus() {
      return false;
   }

   private boolean getITPolicy() {
      return false;
   }

   private boolean changePassword() {
      return false;
   }

   private int setOTAITPolicy(byte[] buffer) {
      if (buffer == null) {
         return 128;
      }

      try {
         buffer = ITPolicyInternal.authenticateITPolicy(buffer);
         if (buffer == null) {
            ITAdminEventLogger.logEvent(1262834258, 2);
            return 1262834258;
         }

         DataBuffer otaPolicyBuffer = (DataBuffer)(new Object(buffer, 0, buffer.length, true));
         if (ITPolicyInternal.setOTAITPolicy(otaPolicyBuffer)) {
            this.lockHandheld(1281979503);
         }
      } finally {
         ;
      }

      this.alert(CommonResources.getString(9040));
      return 192;
   }

   private boolean setPassword(String str) {
      boolean result = false;
      String message = CommonResources.getString(this._security.isPasswordEnabled() ? 9163 : 9039);

      label37:
      try {
         this._security.resetPassword();
         result = this._security.setPassword(null, str, null);
      } finally {
         break label37;
      }

      synchronized (Application.getApplication().getAppEventLock()) {
         Dialog dialog = (Dialog)(new Object(0, message, 0, Bitmap.getPredefinedBitmap(0), 33554432));
         UiApplication.getUiApplication().pushGlobalScreen(dialog, -1073741825, 2);
         return result;
      }
   }

   private boolean lockHandheld(int logCode) {
      LockEventLogger.logLockEvent(logCode);
      ApplicationManager.getApplicationManager().lockSystem(true);
      return true;
   }

   private boolean setOwnerInfo(DataBuffer data) {
      label34:
      try {
         data.readByte();

         while (!data.eof()) {
            int command = data.readByte();
            int length = data.readCompressedInt();
            byte[] buffer = new byte[length];
            data.readFully(buffer, 0, length);
            String info = (String)(new Object(buffer));
            switch (command) {
               case 0:
                  break;
               case 1:
               default:
                  Owner.setOwnerName(info, true);
                  break;
               case 2:
                  Owner.setOwnerInfo(info, true);
            }
         }
      } finally {
         break label34;
      }

      this.alert(CommonResources.getString(9038));
      RIMGlobalMessagePoster.postGlobalEvent(-8392006003204551101L);
      return true;
   }

   private boolean setP2P(byte[] data) {
      ITPolicyInternal.setPinKey(data);
      return true;
   }

   private void sendAcknowledge(byte version, int timeStamp, byte command, byte commandVersion, int ack, String uid, byte errorCode, byte[] errorData) {
      label59:
      try {
         this.setSendingConnection((DatagramConnection)Connector.open(((StringBuffer)(new Object("gme:ITADMIN/"))).append(uid).toString()));
      } finally {
         break label59;
      }

      DataBuffer data = (DataBuffer)(new Object());
      data.writeByte(version);
      data.writeInt(timeStamp);
      data.writeByte(command);
      data.writeByte((byte)ack);
      if (command != 0) {
         if (ack == 192) {
            data.writeCompressedInt(1);
            data.writeByte(commandVersion);
         } else if (errorData != null) {
            data.writeCompressedInt(1 + errorData.length);
            data.writeByte(errorCode);
            data.write(errorData);
         } else {
            data.writeCompressedInt(1);
            data.writeByte(errorCode);
         }
      } else {
         DataBuffer singleCommandData = (DataBuffer)(new Object());
         IntEnumeration keys = this._ackTable.keys();

         while (keys.hasMoreElements()) {
            int key = keys.nextElement();
            int value = this._ackTable.get(key);
            byte singleCommand = (byte)(key & 0xFF);
            byte singleCommandFlag = (byte)(key >> 8 & 0xFF);
            byte ackResult = (byte)(value & 0xFF);
            if ((singleCommandFlag & 64) == 64) {
               singleCommandData.writeByte(singleCommand);
               singleCommandData.writeByte(ackResult);
               singleCommandData.writeCompressedInt(0);
            }
         }

         singleCommandData.trim();
         int length = singleCommandData.getLength();
         data.writeCompressedInt(length + 1);
         data.writeByte(commandVersion);
         data.write(singleCommandData.getArray());
      }

      Packet packet = new Packet(data, null, 0, null);
      if (super._serviceRecord == null) {
         this._overridingServiceRecords[0].setUid(uid);
         this._transmissionContext.put(-7050660451800027507L, this._overridingServiceRecords);
         packet.setContextObject(this._transmissionContext);
         ITAdminEventLogger.logEvent(1094929194, 0);
      }

      this.transmitPacket(packet, null);
      ITAdminEventLogger.logEvent(1094929195, 0);
      ITAdminEventLogger.logEvent(1094929195, 5, data.toArray());
      this.closeSendingConnection(true);
   }

   @Override
   public void transmitObject(String typeString, Object anObject, TransmissionStatusListener aTransmissionStatusListener, int tagInt, Object contextObject) {
      super.transmitObject(typeString, anObject, aTransmissionStatusListener, tagInt, contextObject);
   }

   ITAdminTransmissionService() {
      super(7017126881385937825L, 8708180829147027502L, false);
      this.init();
   }

   @Override
   public int getStatus() {
      return 3;
   }

   @Override
   public void setDefaultTransmissionStatusListener(TransmissionStatusListener aTransmissionStatusListener) {
      super.setDefaultTransmissionStatusListener(aTransmissionStatusListener);
   }

   @Override
   protected void transmitPacket(Packet packet, Object contextUsedToFindConverterAndSend) {
      ITAdminEventLogger.logEvent(1397050948, 4, packet.getPayload(), packet.getTag() & 4294967295L);
      super.transmitPacket(packet, contextUsedToFindConverterAndSend);
   }

   private void alert(String message) {
      if (this.debugMode()) {
         Dialog dialog = (Dialog)(new Object(0, message, 0, Bitmap.getPredefinedBitmap(0), 33554432));
         UiApplication.getUiApplication().pushGlobalScreen(dialog, -1073741823, 2);
      }
   }

   private boolean debugMode() {
      Boolean debug = (Boolean)this._appRegistry.get(6058454246005266633L);
      return debug == null ? false : debug;
   }
}
