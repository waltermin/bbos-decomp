package net.rim.device.apps.api.tts;

public interface TTSManager {
   boolean playTTS(SynthesizerQueueItem var1);

   void setTTSVolume(int var1);

   void setTTSSpeed(int var1);

   boolean isIdleForTTS();

   void cancelTTS();

   boolean startTTSEngine(TTSListener var1);

   boolean stopTTSEngine(TTSListener var1);
}
