package net.rim.device.api.system;

import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.system.USBPortInternal;

public final class USBPort extends IOPort {
   private USBPortInternal _usbPortInternal;

   private USBPort() {
   }

   private static final void assertPermission() {
      ApplicationControl.assertLocalConnectionAllowed(true);
   }

   public static final boolean isSupported() {
      return USBPortInternal.isSupported();
   }

   public USBPort(int channel) {
      assertPermission();
      this._usbPortInternal = new USBPortInternal(channel);
   }

   @Override
   public final void close() {
      assertPermission();
      this._usbPortInternal.close();
   }

   @Override
   public final int write(byte[] data) {
      assertPermission();
      return this._usbPortInternal.write(data);
   }

   @Override
   public final synchronized int write(byte[] data, int offset, int length) {
      assertPermission();
      return this._usbPortInternal.write(data, offset, length);
   }

   @Override
   public final synchronized int write(int b) {
      assertPermission();
      return this._usbPortInternal.write(b);
   }

   @Override
   public final int read(byte[] data) {
      assertPermission();
      return this._usbPortInternal.read(data);
   }

   @Override
   public final int read(byte[] data, int offset, int length) {
      assertPermission();
      return this._usbPortInternal.read(data, offset, length);
   }

   @Override
   public final int read() {
      assertPermission();
      return this._usbPortInternal.read();
   }

   public static final int registerChannel(String name, int maxRxSize, int maxTxSize) {
      assertPermission();
      if (name != null && name.startsWith("RIM")) {
         ControlledAccess.assertRRISignatures(true);
      }

      return USBPortInternal.registerChannel(name, maxRxSize, maxTxSize);
   }

   public static final void deregisterChannel(int channel) {
      assertPermission();
      String channelName = USBPort$Internal.getChannelName(channel);
      if (channelName != null && channelName.startsWith("RIM")) {
         ControlledAccess.assertRRISignatures(true);
      }

      USBPortInternal.deregisterChannel(channel);
   }

   public static final int getConnectionState() {
      return USBPortInternal.getConnectionState();
   }

   public static final int getMaximumRxSize() {
      assertPermission();
      return USBPortInternal.getMaximumRxSize();
   }

   public static final int getMaximumTxSize() {
      assertPermission();
      return USBPortInternal.getMaximumTxSize();
   }
}
