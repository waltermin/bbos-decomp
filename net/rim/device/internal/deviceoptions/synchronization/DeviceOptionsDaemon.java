package net.rim.device.internal.deviceoptions.synchronization;

import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.TLEFieldController;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.internal.deviceoptions.DeviceOptions;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.rms.RecordStoreSyncCollection;
import net.rim.device.internal.system.FIPSPolicy;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.LockEventLogger;
import net.rim.device.internal.system.NvStore;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.ui.UiOptionsRegistry;

public final class DeviceOptionsDaemon extends Thread implements TLEFieldController {
   private DatagramConnection _connection;
   private DatagramBase _command;
   private DatagramBase _reply;
   private Security _security;
   private DeviceOptionsSyncItem _syncItem;
   private static final int SECURITY_OPTIONS = 1;
   private static final int PAGER_OPTIONS = 2;
   private static final int VERSION_OPTIONS = 3;
   private static final int IT_ADMIN = 4;
   private static final int SET_PASSWORD_CMD = 1;
   private static final int GET_PASSWORD_STATUS_CMD = 2;
   private static final int SET_PASSWORD_FILE = 3;
   private static final int GET_PASSWORD_FILE = 4;
   private static final int SET_DEVICE_OPTIONS_CMD = 1;
   private static final int GET_DEVICE_OPTIONS_CMD = 2;
   private static final int GET_VERSION_OPTIONS_CMD = 1;
   private static final int UNKNOWN_COMMAND = 40962;
   private static final int SUCCESS = 0;
   private static final int SET_PASSWORD = 1;
   private static final int GET_PASSWORD_STATUS = 2;
   private static final int SET_IT_POLICY = 3;
   private static final int GET_IT_POLICY = 4;
   private static final int CHANGE_PASWORD = 5;
   private static final int KILL_HANDHELD = 6;
   private static final int LOCK_HANDHELD = 7;
   private static final int SET_OWNER_INFO = 8;
   private static final int SET_PEER_TO_PEER_KEY = 9;

   private DeviceOptionsDaemon(DeviceOptionsSyncItem syncItem) {
      this._syncItem = syncItem;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      if (FIPSPolicy.getFIPSLevel() < 3) {
         String connectionName = "commlink:Device Options";
         this._security = Security.getInstance();

         try {
            this._connection = (DatagramConnection)Connector.open(connectionName);
            this._command = (DatagramBase)this._connection.newDatagram(0);
            this._reply = (DatagramBase)this._connection.newDatagram(0);
            this._reply.setBigEndian(false);
         } catch (Throwable var7) {
            throw new RuntimeException(connectionName + ":" + ex.getMessage());
         }

         while (true) {
            try {
               this._connection.receive(this._command);
               if (!this._command.isFlagSet(1)) {
                  if (!this._command.isFlagSet(2)) {
                     if (!this._command.isFlagSet(4)) {
                        this.sendReply(this.processCommand());
                     } else {
                        this._command.rewind();
                        this._command.trim();
                     }
                  }
                  continue;
               }
            } finally {
               continue;
            }
         }
      }
   }

   private final int processCommand() {
      try {
         this._command.setBigEndian(false);
         switch (this._command.readByte()) {
            case 0:
               return 40962;
            case 1:
            default:
               return this.securityOptionsCommand();
            case 2:
               return this.pagerOptionsCommand();
            case 3:
               return this.versionOptionsCommand();
            case 4:
               return this.itAdminCommand();
         }
      } finally {
         ;
      }
   }

   private final int securityOptionsCommand() {
      switch (this._command.readByte()) {
         case 1:
            break;
         case 2:
         default: {
            DatagramBase dg = this.resetReply(1);
            dg.writeBoolean(this._security.isPasswordEnabled());
            this.sendReply();
            return 0;
         }
         case 3:
            SyncEventLogger.logEvent(1280331596, 0);
            DatagramBase data = this._command;
            boolean changePasswordNow = data.readBoolean();
            boolean allowPeerToPeer = data.readBoolean();
            int minimumPasswordLength = data.readUnsignedByte();
            data.readBoolean();
            int maximumTimeout = data.readUnsignedByte();
            boolean userCanChangeTimeout = data.readBoolean();
            int maximumAge = data.readUnsignedShort();
            int passwordPattern;
            if (data.available() > 1) {
               passwordPattern = data.readUnsignedByte();
            } else {
               passwordPattern = ITPolicy.getInteger(13, 0);
            }

            boolean longTermTimeoutEnable;
            if (data.available() >= 1) {
               longTermTimeoutEnable = data.readBoolean();
            } else {
               longTermTimeoutEnable = ITPolicy.getBoolean(14, false);
            }

            boolean enableSMS;
            if (data.available() >= 1) {
               enableSMS = data.readBoolean();
            } else {
               enableSMS = SMSPacketHeader.isSendSupported();
            }

            if (RadioInfo.getNetworkType() == 5) {
               enableSMS = false;
            }

            boolean enableBCCRecipients;
            if (data.available() >= 1) {
               enableBCCRecipients = data.readBoolean();
            } else {
               enableBCCRecipients = ITPolicy.getBoolean(16, true);
            }

            DataBuffer tleData = new DataBuffer();
            tleData.write(1);
            TLEUtilities.writeIntegerField(tleData, 6, changePasswordNow ? 1 : 0, false);
            TLEUtilities.writeIntegerField(tleData, 7, allowPeerToPeer ? 1 : 0, false);
            TLEUtilities.writeIntegerField(tleData, 8, minimumPasswordLength, false);
            TLEUtilities.writeIntegerField(tleData, 10, maximumTimeout, false);
            TLEUtilities.writeIntegerField(tleData, 12, userCanChangeTimeout ? 1 : 0, false);
            TLEUtilities.writeIntegerField(tleData, 11, maximumAge, false);
            TLEUtilities.writeIntegerField(tleData, 13, passwordPattern, false);
            TLEUtilities.writeIntegerField(tleData, 14, longTermTimeoutEnable ? 1 : 0, false);
            TLEUtilities.writeIntegerField(tleData, 15, enableSMS ? 1 : 0, false);
            TLEUtilities.writeIntegerField(tleData, 16, enableBCCRecipients ? 1 : 0, false);
            tleData.write(data.getData(), 14, data.getLength() - 14);

            try {
               if (ITPolicyInternal.setITPolicy(tleData)) {
                  LockEventLogger.logLockEvent(1282630768);
                  ApplicationManager.getApplicationManager().lockSystem(true);
                  return 0;
               }
               break;
            } finally {
               ;
            }
         case 4: {
            DatagramBase dg = this.resetReply(44);

            for (int i = 0; i < 20; i++) {
               dg.writeByte(0);
            }

            dg.writeBoolean(this._security.isPasswordEnabled());
            dg.writeByte(this._security.getPasswordFailureCount());
            dg.writeByte(0);
            dg.writeByte(0);

            for (int i = 0; i < 10; i++) {
               dg.writeByte(0);
            }

            dg.writeBoolean(ITPolicy.getBoolean(7, true));
            dg.writeByte(FIPSPolicy.getMaxInteger(8, 4, 5));
            dg.writeBoolean(FIPSPolicy.isDevicePasswordRequired());
            dg.writeByte(ITPolicy.getInteger(10, 60));
            dg.writeBoolean(ITPolicy.getBoolean(12, true));
            dg.writeByte(0);
            dg.writeShort(ITPolicy.getInteger(11, 0));
            dg.writeBoolean(false);
            dg.writeByte(ITPolicy.getInteger(13, 0));
            dg.writeBoolean(ITPolicy.getBoolean(14, false));
            dg.writeBoolean(ITPolicy.getBoolean(15, true));
            dg.writeBoolean(ITPolicy.getBoolean(16, true));
            this.sendReply();
         }
      }

      return 0;
   }

   private final int pagerOptionsCommand() {
      switch (this._command.readByte()) {
         case 0:
            break;
         case 1:
         default:
            if (this._command.readUnsignedShort() >= 290) {
               this._syncItem.setUserInfo(this._command);
               return 0;
            }
            break;
         case 2:
            DatagramBase dg = this.resetReply(290);
            this._syncItem.getUserInfo(dg);
            this.sendReply();
      }

      return 0;
   }

   private final int versionOptionsCommand() {
      switch (this._command.readByte()) {
         default:
            return 0;
      }
   }

   private final int itAdminCommand() {
      this._command.setBigEndian(true);
      if (ITPolicyInternal.isOTAITAdminEnabled()) {
         SyncEventLogger.logEvent(1397051986, 0);
      } else {
         DataBuffer itadminBuffer = new DataBuffer(true);
         itadminBuffer.writeByte(this._command.readByte());
         int curTimeStamp = this._command.readInt();
         itadminBuffer.writeInt(curTimeStamp);
         int timeStamp = ITPolicyInternal.getITAdminTimeStamp();
         if (curTimeStamp <= timeStamp) {
            if (curTimeStamp == timeStamp) {
               SyncEventLogger.logEvent(1414742349, 0);
            } else {
               SyncEventLogger.logEvent(1413829202, 0);
            }
         } else {
            int cmd = this._command.readByte();
            this._command.readByte();
            int cmdLength = this._command.readCompressedInt();
            byte commandVersion = -1;
            if (cmdLength > 0) {
               commandVersion = this._command.readByte();
            }

            byte[] buffer = this.getCommandDataWithVersion(this._command, cmdLength, commandVersion);
            switch (cmd) {
               case 0:
                  break;
               case 1:
               case 2:
               case 4:
               case 5:
               case 6:
               case 7:
               case 8:
                  SyncEventLogger.logEvent(1431194446, 0);
                  break;
               case 3:
               default:
                  SyncEventLogger.logEvent(1397772108, 0);

                  try {
                     if (ITPolicyInternal.setOTAITPolicy(new DataBuffer(buffer, 0, buffer.length, true))) {
                        LockEventLogger.logLockEvent(1282630761);
                        ApplicationManager.getApplicationManager().lockSystem(true);
                     }
                     break;
                  } finally {
                     break;
                  }
               case 9:
                  SyncEventLogger.logEvent(1397764688, 0);
                  ITPolicyInternal.setPinKey(buffer);
            }

            NvStore.writeData(5, itadminBuffer.toArray());
         }
      }

      this._command.setBigEndian(false);
      return 0;
   }

   private final byte[] getCommandDataWithVersion(DatagramBase data, int length, byte commandVersion) {
      byte[] buffer = new byte[length];
      if (length > 0) {
         buffer[0] = commandVersion;
         if (length > 1) {
            data.readFully(buffer, 1, length - 1);
         }
      }

      return buffer;
   }

   private final DatagramBase resetReply(int length) {
      this._reply.simpleReset();
      this._reply.setBigEndian(false);
      this._reply.ensureCapacity(length);
      this._reply.setLength(0);
      return this._reply;
   }

   private final void sendReply() throws IOException {
      this._connection.send(this._reply);
      this._connection.receive(this._reply);
      if (!this._reply.isFlagSet(1) || this._reply.readShort() != 0) {
         throw new IOException("Bad reply");
      }
   }

   private final void sendReply(int reply) {
      DatagramBase rdg = this._reply;
      rdg.rewind();
      rdg.setFlag(1, true);
      rdg.writeShort(reply);
      this._connection.send(rdg);
   }

   @Override
   public final boolean processField(int type, int length, DataBuffer db) {
      return true;
   }

   @Override
   public final void dumpField(int type, DataBuffer db) {
   }

   public static final void DeviceOptionsDaemonMain() {
      OptionsSyncCollection optionsSyncCollection = new OptionsSyncCollection();
      DeviceOptions.addOptionsProvider(new FontOptionsProvider(optionsSyncCollection));
      DeviceOptions.addOptionsProvider(new OwnerInfoOptionsProvider(optionsSyncCollection));
      DeviceOptions.addOptionsProvider(new SecurityOptionsProvider(optionsSyncCollection));
      DeviceOptions.addOptionsProvider(new TimeOptionsProvider(optionsSyncCollection));
      DeviceOptions.addOptionsProvider(new AutoOnOffOptionsProvider(optionsSyncCollection));
      DeviceOptions.addOptionsProvider(new LocalizationOptionsProvider(optionsSyncCollection));
      DeviceOptions.addOptionsProvider(new SpellCheckOptionsProvider(optionsSyncCollection));
      int legacyState = UiOptionsRegistry.getInstance().getLegacyUiOptionsState();
      switch (legacyState) {
         case -1:
         case 2:
            break;
         case 0:
         case 3:
         default:
            optionsSyncCollection.addLegacyOptionsProvider(new UiOptionsProvider(optionsSyncCollection, legacyState));
            break;
         case 1:
            new UiOptionsProvider(optionsSyncCollection, legacyState);
      }

      DeviceOptions.addOptionsProvider(new SMSOptionsProvider(optionsSyncCollection));
      DeviceOptions.addOptionsProvider(new FileSystemOptionsProvider(optionsSyncCollection));
      DeviceOptionsSyncItem deviceOptionsSync = new DeviceOptionsSyncItem();
      Proxy.getInstance().startThread(new DeviceOptionsDaemon(deviceOptionsSync));
      SyncManager syncManager = SyncManager.getInstance();
      syncManager.enableSynchronization(new PolicySyncCollection());
      syncManager.enableSynchronization(deviceOptionsSync);
      syncManager.enableSynchronization(optionsSyncCollection);
      syncManager.enableSynchronization(RecordStoreSyncCollection.getInstance());
      DeviceOptions.setListener(optionsSyncCollection);
   }
}
