package net.rim.device.api.system;

public interface StylusListener {
   boolean stylusDown(int var1, int var2, int var3, int var4);

   boolean stylusUp(int var1, int var2, int var3, int var4);

   boolean stylusDrag(int var1, int var2, int var3, int var4);

   boolean stylusTap(int var1, int var2, int var3, int var4);

   boolean stylusDoubleTap(int var1, int var2, int var3, int var4);

   boolean stylusTapHold(int var1, int var2, int var3, int var4);
}
