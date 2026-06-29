package net.rim.device.internal.system;

public interface ActiveMedia {
   boolean isAudioInUse();

   int codecUsed();

   boolean isForce();

   boolean isAlert();
}
