package net.rim.device.cldc.io.stp;

import net.rim.device.api.io.DatagramBase;

public final class StpDatagram extends DatagramBase {
   private byte _version = 1;
   private byte _command;
   private int _result;
   private int _acceptableVersion;
   private StpUtil$ServiceInfo[] _services;
   public static final byte COMMAND_NONE;
   public static final byte COMMAND_DATA;
   public static final byte COMMAND_STATUS;
   public static final byte COMMAND_CONNECT;
   public static final byte COMMAND_STATE;
   public static final byte COMMAND_CONFIG;
   public static final int RESULT_NONE;
   public static final int RESULT_DELIVERED;
   public static final int RESULT_REFUSED;
   public static final int RESULT_NOT_ROUTABLE;
   public static final int RESULT_FAILED;

   @Override
   public final void reset() {
      super.reset();
      this._version = 1;
      this._command = 0;
      this._result = 0;
      this._acceptableVersion = 0;
      this._services = null;
   }

   public final byte getVersion() {
      return this._version;
   }

   public final void setVersion(byte version) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final byte getCommand() {
      return this._command;
   }

   public final void setCommand(byte command) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getResult() {
      return this._result;
   }

   public final void setResult(int result) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getAcceptableVersion() {
      return this._acceptableVersion;
   }

   public final void setAcceptableVersion(int acceptableVersion) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final StpUtil$ServiceInfo[] getServices() {
      return this._services;
   }

   public final void setServices(StpUtil$ServiceInfo[] services) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
