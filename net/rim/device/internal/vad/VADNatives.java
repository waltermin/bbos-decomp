package net.rim.device.internal.vad;

public final class VADNatives {
   public static final native int initialize();

   public static final native int start(int var0);

   public static final native int stop();

   public static final native void writeInt(int var0, int var1);

   public static final native void writeData(int var0, Object[] var1, int var2, int var3);

   public static final native void writeNameData(int var0, byte[][] var1);

   public static final native void writePhonebookData(int var0, byte[] var1, byte[][] var2);

   public static final native void writeLabelData(int var0, byte[] var1);

   public static final native void writePhoneInfo(int var0, VADPhoneInfo var1);

   public static final native int memoryMapData(int var0, Object[] var1, int var2);

   public static final native int operationComplete(int var0);

   public static final native int sendEvent(int var0);

   public static final native int setParameters(VADParameters var0);

   public static final native Object[] getVersionInfo();

   public static final native int playTTS(byte[] var0);
}
