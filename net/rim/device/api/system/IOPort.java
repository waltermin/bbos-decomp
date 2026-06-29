package net.rim.device.api.system;

public class IOPort {
   protected IOPort() {
   }

   public void close() {
      throw null;
   }

   public int write(byte[] _1) {
      throw null;
   }

   public int write(byte[] _1, int _2, int _3) {
      throw null;
   }

   public int write(int _1) {
      throw null;
   }

   public int read(byte[] _1) {
      throw null;
   }

   public int read(byte[] _1, int _2, int _3) {
      throw null;
   }

   public int read() {
      throw null;
   }

   public static void registerNotifyPattern(byte[] pattern) {
      SerialPort.registerNotifyPattern(pattern);
   }

   public void cleanup() {
   }
}
