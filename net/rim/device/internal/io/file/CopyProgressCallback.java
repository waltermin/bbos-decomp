package net.rim.device.internal.io.file;

public interface CopyProgressCallback {
   void copyStarted();

   void segmentCopied(long var1, long var3);

   void copyCompleted();
}
