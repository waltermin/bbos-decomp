package net.rim.device.internal.compress;

public final class YKEncode {
   private YKCleaner _cleaningHelper = new YKCleaner(this);
   private boolean yk_mixedMode;
   private boolean yk_passthroughMode;
   private byte[] yk_context;

   public YKEncode(boolean mixedMode) {
      this.yk_mixedMode = mixedMode;
      this.yk_passthroughMode = false;
   }

   public static final native int yk_get_codec_version();

   public final byte[] yk_encode(byte[] input, int inputOffset, int inputSize) {
      this._cleaningHelper.register();
      return this.yk_encode0(input, inputOffset, inputSize);
   }

   private final native byte[] yk_encode0(byte[] var1, int var2, int var3);

   public final native byte[] flush();

   public final void loadContextMap(byte[] contextMap, boolean bEncodeMap) {
      this._cleaningHelper.register();
      this.loadContextMap0(contextMap, bEncodeMap);
   }

   public final native void loadContextMap0(byte[] var1, boolean var2);

   public final void yk_load_side_data(byte[] input, int inputOffset, int inputSize) {
      this._cleaningHelper.register();
      this.yk_load_side_data0(input, inputOffset, inputSize);
   }

   private final native void yk_load_side_data0(byte[] var1, int var2, int var3);

   public final byte[] yk_uninit() {
      this._cleaningHelper.unregister();
      return this.yk_uninit0();
   }

   private final native byte[] yk_uninit0();

   public final boolean getPassthroughMode() {
      return this.yk_passthroughMode;
   }

   public final void setPassthroughMode(boolean value) {
      this.yk_passthroughMode = value;
   }

   public static final native boolean isSupported();
}
