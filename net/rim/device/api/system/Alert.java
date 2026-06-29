package net.rim.device.api.system;

import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.media.MediaNatives;
import net.rim.device.internal.media.MediaStreamingManager;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.TraceBack;

public final class Alert {
   public static final int ALERT_OK = 0;
   public static final int ALERT_ERROR_UNKNOWN = 1;
   public static final int ALERT_ERROR_BAD_DATA = 2;
   public static final int ALERT_ERROR_BAD_STATE = 3;
   public static final int ALERT_ERROR_FILESYSTEM_FULL = 4;
   public static final int ALERT_INTERRUPT_OFF = 0;
   public static final int ALERT_INTERRUPT_ON = 1;
   public static final int ALERT_INTERRUPT_ON_NO_TRACKBALL = 2;

   private Alert() {
   }

   private static final Alert$MidiListener getMidiListener() {
      Alert$MidiListener listener = (Alert$MidiListener)ApplicationRegistry.getApplicationRegistry().getOrWaitFor(2808165152854904955L);
      if (listener == null) {
         listener = new Alert$MidiListener(null);
         ApplicationRegistry.getApplicationRegistry().put(2808165152854904955L, listener);
         Proxy.getInstance().addAlertListener(listener);
      }

      return listener;
   }

   public static final boolean isAudioSupported() {
      return Audio.isSupported();
   }

   public static final void startAudio(short[] tune, int volume) {
      assertMediaPermission();
      stopAudioImpl();
      startAudioImpl(tune, volume);
   }

   private static final native void startAudioImpl(short[] var0, int var1);

   public static final void stopAudio() {
      assertMediaPermission();
      stopAudioImpl();
   }

   private static final native void stopAudioImpl();

   public static final boolean isBuzzerSupported() {
      return InternalServices.isDeviceCapable(9);
   }

   public static final void startBuzzer(short[] tune, int volume) {
      startBuzzer(tune, volume, 2);
   }

   public static final void startBuzzer(short[] tune, int volume, boolean interruptable) {
      startBuzzer(tune, volume, interruptable ? 2 : 0);
   }

   public static final void startBuzzer(short[] tune, int volume, int interruptable) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      assertMediaPermission();
      stopBuzzerImpl();
      startBuzzerImpl(tune, volume, interruptable);
   }

   private static final native void startBuzzerImpl(short[] var0, int var1, int var2);

   public static final void playBuzzer(short[] tune, int volume) {
      startBuzzer(tune, volume, 0);
   }

   public static final void stopBuzzer() {
      assertMediaPermission();
      stopBuzzerImpl();
   }

   private static final native void stopBuzzerImpl();

   public static final boolean isVibrateSupported() {
      return true;
   }

   public static final void startVibrate(int duration) {
      assertMediaPermission();
      startVibrateImpl(duration);
   }

   private static final native void startVibrateImpl(int var0);

   public static final void stopVibrate() {
      assertMediaPermission();
      stopVibrateImpl();
   }

   private static final native void stopVibrateImpl();

   public static final native boolean isMIDISupported();

   public static final int startMIDI(byte[] tune, boolean interruptable) {
      return startMIDI(tune, interruptable ? 1 : 0, null);
   }

   public static final int startMIDI(byte[] tune, int interruptable) {
      return startMIDI(tune, interruptable, null);
   }

   public static final synchronized int startMIDI(byte[] tune, int interruptable, AlertListener2 midiListener) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      assertMediaPermission();
      if (isSingleSharedAudioChannel()) {
         MediaStreamingManager msm = MediaStreamingManager.getInstance();
         if (msm != null) {
            msm.stopSingleChannelAudio();
         }
      }

      stopMIDI();
      Alert$MidiListener listener = getMidiListener();

      try {
         return listener.midiStart(midiListener, startMIDIImpl(tune, interruptable));
      } catch (OutOfMemoryError e) {
         net.rim.vm.Memory.maximizeContiguousRAM();
         return listener.midiStart(midiListener, startMIDIImpl(tune, interruptable));
      }
   }

   public static final boolean isSingleSharedAudioChannel() {
      return MediaNatives.getNumberOfStreamingChannels() == 1;
   }

   private static final native int startMIDIImpl(byte[] var0, int var1);

   public static final synchronized void stopMIDI() {
      if (!getMidiListener().isStopped()) {
         assertMediaPermission();
         stopMIDIImpl();
      }
   }

   private static final native void stopMIDIImpl();

   public static final int pauseMIDI() {
      assertMediaPermission();
      return pauseMIDIImpl();
   }

   private static final native int pauseMIDIImpl();

   public static final int resumeMIDI() {
      assertMediaPermission();
      return resumeMIDIImpl();
   }

   private static final native int resumeMIDIImpl();

   public static final boolean isADPCMSupported() {
      return false;
   }

   public static final int startADPCM(byte[] tune, boolean interruptable) {
      throw new UnsupportedOperationException();
   }

   public static final void stopADPCM() {
   }

   public static final void enablePWMSync(boolean enable) {
   }

   public static final void setADPCMVolume(int volume) {
   }

   public static final void setBuzzerVolume(int volume) {
      assertDeviceSettingsPermission();
      setBuzzerVolumeImpl(volume);
   }

   private static final native void setBuzzerVolumeImpl(int var0);

   public static final void setVolume(int volume) {
      assertDeviceSettingsPermission();
      setVolumeImpl(volume);
   }

   private static final native void setVolumeImpl(int var0);

   public static final native int getVolume();

   public static final void mute(boolean newMuteState) {
      assertDeviceSettingsPermission();
      muteImpl(newMuteState);
   }

   private static final native void muteImpl(boolean var0);

   public static final void enablePowerAmp(boolean enable) {
   }

   private static final void assertMediaPermission() {
      ApplicationControl.assertMediaPermitted(true, CommonResource.getBundle(), 10177);
   }

   private static final void assertDeviceSettingsPermission() {
      ApplicationControl.assertChangeDeviceSettingsPermitted(true, CommonResource.getBundle(), 10133);
   }
}
