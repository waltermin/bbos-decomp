package net.rim.device.apps.api.tts;

public interface TTSListener {
   boolean playNext();

   boolean hasSomethingToSay();

   int getPriority();

   void notifyTTSEngineListener(TTSEngineEvent var1);

   int getVolume();

   int getSpeakingRate();

   void clearQueueTop(SynthesizerQueueItem var1);
}
