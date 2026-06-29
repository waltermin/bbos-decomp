package net.rim.device.api.system;

public interface AudioRouterListener {
   void audioVolumeChanged(boolean var1);

   void audioSinkChanged();

   void audioSourceChanged();
}
