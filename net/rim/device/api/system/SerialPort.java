package net.rim.device.api.system;

public final class SerialPort extends IOPort {
   public static final int PARITY_NONE;
   public static final int PARITY_EVEN;
   public static final int PARITY_ODD;
   public static final int DEFAULT_PORT;

   private SerialPort() {
   }

   public static final boolean isSupported() {
      return false;
   }

   public SerialPort(int baud, int dataBits, int parity, int stopBits, int rxBufferSize, int txBufferSize) {
      throw new UnsupportedOperationException();
   }

   public final void setProperties(int baud, int dataBits, int parity, int stopBits) {
   }

   public final void setDsr(boolean state) {
   }

   public final void standbyMode(boolean state) {
   }

   public final boolean getDtr() {
      throw new UnsupportedOperationException();
   }

   public final int getTxCount() {
      throw new UnsupportedOperationException();
   }

   @Override
   public final void close() {
   }

   @Override
   public final int write(byte[] data) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final int write(byte[] data, int offset, int length) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final int write(int b) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final int read(byte[] data) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final int read(byte[] data, int offset, int length) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final int read() {
      throw new UnsupportedOperationException();
   }

   public static final void registerNotifyPattern(byte[] pattern) {
      throw new UnsupportedOperationException();
   }

   public static final void enableDtrFix() {
   }
}
