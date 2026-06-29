package net.rim.device.internal.system;

import java.io.IOException;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.IOPort;
import net.rim.device.internal.applicationcontrol.ApplicationControl;

public final class SmartCardPort extends IOPort {
   private boolean _opened;
   public int SC_PROTOCOL_DEFAULT = -1;
   public int SC_PROTOCOL_T0 = 0;
   public int SC_PROTOCOL_T1 = 1;

   private static final void assertPermission() {
      ApplicationControl.assertLocalConnectionAllowed(true);
   }

   public SmartCardPort() {
      this._opened = false;
   }

   public static final boolean isSupported() {
      return InternalServices.isDeviceCapable(5);
   }

   public final void reset() throws IOException {
      assertPermission();
      if (!this._opened) {
         throw new IOException("Device not open");
      }

      this.resetDevice(true);

      try {
         synchronized (this) {
            this.wait(200);
         }
      } catch (InterruptedException var4) {
      }

      this.resetDevice(false);
   }

   @Override
   public final int write(byte[] data) {
      return this.write(data, 0, data.length);
   }

   @Override
   public final int write(int b) {
      assertPermission();
      return this.write((byte)b);
   }

   @Override
   public final int read(byte[] data) {
      return this.read(data, 0, data.length);
   }

   @Override
   public final void close() {
      assertPermission();
      this.closeDevice();
      this._opened = false;
   }

   public final boolean open(int protocol) throws IOException {
      assertPermission();
      if (this._opened) {
         throw new IOException("Device Already Open");
      }

      if (!this.openDevice(protocol)) {
         return false;
      }

      this._opened = true;
      return true;
   }

   public final boolean setProtocol(int protocol) {
      assertPermission();
      return protocol != this.SC_PROTOCOL_T1 && protocol != this.SC_PROTOCOL_T0 ? false : this.setProtocolImpl(protocol);
   }

   private final native boolean setProtocolImpl(int var1);

   public static final boolean isCardPresent() {
      assertPermission();
      return isCardPresentImpl();
   }

   private static final native boolean isCardPresentImpl();

   @Override
   public final int write(byte[] data, int offset, int length) {
      assertPermission();
      return this.writeImpl(data, offset, length);
   }

   private final native int writeImpl(byte[] var1, int var2, int var3);

   @Override
   public final int read(byte[] data, int offset, int length) {
      assertPermission();
      return this.readImpl(data, offset, length);
   }

   private final native int readImpl(byte[] var1, int var2, int var3);

   @Override
   public final int read() {
      assertPermission();
      return this.readImpl();
   }

   private final native int readImpl();

   public final void flushRead() {
      assertPermission();
      this.flushReadImpl();
   }

   private final native void flushReadImpl();

   private final native boolean openDevice(int var1);

   private final native boolean closeDevice();

   private final native boolean resetDevice(boolean var1);

   private final native int write(byte var1);

   public static final void addListener(Application app, SmartCardPortListener listener) {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(16) == null) {
            dispatchManager.setDispatcher(16, new SmartCardPortEventDispatcher());
         }
      }

      app.addListener(16, listener);
   }

   public static final void removeListener(Application app, SmartCardPortListener listener) {
      app.removeListener(16, listener);
   }
}
