package net.rim.device.internal.system;

import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.IOPort;
import net.rim.vm.Process;

public final class USBPortInternal extends IOPort {
   private int _channel;
   private Runnable _cleanupRunnable;
   private static final String PORT_NOT_OPEN = "Port not open";

   private USBPortInternal() {
   }

   public static final boolean isSupported() {
      return InternalServices.isDeviceCapable(1);
   }

   public USBPortInternal(int channel) {
      openChannel(channel);
      this._channel = channel;
      this._cleanupRunnable = new USBPortInternal$USBPortCleanupRunnable(this);
      ((ApplicationProcess)Process.currentProcess()).addCleanupRunnable(this._cleanupRunnable);
   }

   @Override
   public final void close() {
      closeChannel(this._channel);
      ((ApplicationProcess)Process.currentProcess()).removeCleanupRunnable(this._cleanupRunnable);
   }

   @Override
   public final int write(byte[] data) {
      return this.write(data, 0, data.length);
   }

   @Override
   public final synchronized int write(byte[] data, int offset, int length) {
      return write(this._channel, data, offset, length);
   }

   @Override
   public final synchronized int write(int b) {
      return write(this._channel, b);
   }

   @Override
   public final int read(byte[] data) {
      return this.read(data, 0, data == null ? 0 : data.length);
   }

   @Override
   public final int read(byte[] data, int offset, int length) {
      return read(this._channel, data, offset, length);
   }

   @Override
   public final int read() {
      return read(this._channel);
   }

   public static final int registerChannel(String name, int maxRxSize, int maxTxSize) {
      return registerChannelImpl(name, maxRxSize, maxTxSize);
   }

   private static final native int registerChannelImpl(String var0, int var1, int var2);

   public static final void deregisterChannel(int channel) {
      deregisterChannelImpl(channel);
   }

   private static final native void deregisterChannelImpl(int var0);

   public static final native int getConnectionState();

   public static final int getMaximumRxSize() {
      return getMaximumTransferSize(true);
   }

   public static final int getMaximumTxSize() {
      return getMaximumTransferSize(false);
   }

   private static final native int getMaximumTransferSize(boolean var0);

   private static final native void openChannel(int var0);

   private static final native void closeChannel(int var0);

   private static final native int write(int var0, byte[] var1, int var2, int var3);

   private static final native int write(int var0, int var1);

   private static final native int read(int var0, byte[] var1, int var2, int var3);

   private static final native int read(int var0);
}
