package net.rim.device.api.system;

import net.rim.device.api.media.control.AudioPathControl;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.device.internal.system.AudioInternal;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.TTY;
import net.rim.vm.Message;

public final class AudioRouter implements AudioHeadsetListener {
   private boolean _masterVolumeChangeSupported;
   private int _audioSink;
   private int[] _numAudioSources;
   private AudioPathControlImpl[] _audioControlsForSource;
   private boolean _bluetoothSCOSinkAvailable;
   private boolean _bluetoothSCORemoteVolumeControl;
   private boolean _bluetoothSCONREC;
   private boolean _bluetoothSCOHasVoiceRecognition;
   private String _bluetoothSCODeviceName;
   private boolean _bluetoothA2DPSinkAvailable;
   private String _bluetoothA2DPDeviceName;
   private PersistentObject _masterVolumePersistentObject;
   private byte[] _masterVolume;
   private byte[] _volumeBoostData;
   private byte[] _eqPresetData;
   private IntVector _feedbackEnabledSources;
   private AudioSinkCallback _callback = null;
   private static final long GUID;
   private static final long AUDIO_VOLUME_PERSISTENT_KEY;
   static final int EVENT_AUDIO_VOLUME_CHANGED;
   static final int EVENT_AUDIO_SINK_CHANGED;
   static final int EVENT_AUDIO_SOURCE_CHANGED;
   private static final int DEFAULT_TONE_VOLUME;
   private static final boolean DEBUG;
   private static final boolean _a2dpSupportedForVideo;
   public static final int AUDIO_SOURCE_PHONE;
   public static final int AUDIO_SOURCE_VAD;
   public static final int AUDIO_SOURCE_VOICE_NOTES;
   public static final int AUDIO_SOURCE_MEDIA_PLAYER_VIDEO;
   public static final int AUDIO_SOURCE_MEDIA_PLAYER_AUDIO;
   public static final int AUDIO_SOURCE_ATTACHMENT_VIEWER;
   public static final int AUDIO_SOURCE_ALERTS;
   public static final int AUDIO_SOURCE_VIDEO_RECORDING;
   public static final int AUDIO_SOURCE_AUDIO_RECORDING;
   public static final int AUDIO_SOURCE_VIDEO_PLAYBACK;
   public static final int AUDIO_SOURCE_AUDIO_PLAYBACK;
   public static final int AUDIO_SOURCE_DEFAULT;
   public static final int NUM_AUDIO_SOURCES;
   public static final int AUDIO_SINK_HANDSET;
   public static final int AUDIO_SINK_HANDSFREE;
   public static final int AUDIO_SINK_BLUETOOTH_SCO;
   public static final int AUDIO_SINK_HEADSET;
   public static final int AUDIO_SINK_HEADSET_HANDSFREE;
   public static final int AUDIO_SINK_BLUETOOTH_A2DP;
   public static final int NUM_AUDIO_SINKS;
   public static final int FEEDBACK_TRACKBALL_CLICK;
   public static final int FEEDBACK_KEYPAD_BEEP;
   public static final int EQ_PRESET_NORMAL;
   public static final int EQ_PRESET_BOOST_BASS;
   public static final int EQ_PRESET_BOOST_TREBLE;
   private static final int[] _validSourceSinks = new int[]{
      15,
      14,
      15,
      11,
      43,
      47,
      11,
      15,
      15,
      15,
      47,
      11,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      -804650991,
      25701,
      25966,
      26996,
      26226,
      25971,
      28268,
      29558,
      25697,
      28788,
      26217,
      28271,
      25964,
      29810,
      26741,
      28780,
      65535,
      25459,
      -805044096,
      -2119164771,
      701964907,
      -1164754652,
      955341807,
      -847833802,
      943449308,
      -1793643343,
      2112113261,
      -1649273728,
      -427103605,
      877887930,
      1289267590
   };

   public final void setAudioSinkCallback(AudioSinkCallback callback) {
      this._callback = callback;
   }

   public final void updateMasterVolume() {
      this.updateMasterVolume(false);
   }

   public final boolean canChangeMasterVolume() {
      switch (this.getActiveSource()) {
         case -1:
            return false;
         case 0:
         case 1:
         case 2:
         default:
            return true;
      }
   }

   public final synchronized int getSink() {
      return this._audioSink;
   }

   public final synchronized boolean setSink(int sink) {
      int activeSource = this.getActiveSource();
      int newMode = this.getAudioMode(activeSource, sink, true);
      if (newMode == -1) {
         return false;
      }

      String name = null;
      this._masterVolumeChangeSupported = true;
      if (sink == 2) {
         this._masterVolumeChangeSupported = this._bluetoothSCORemoteVolumeControl;
         name = this._bluetoothSCODeviceName;
      } else if (sink == 5) {
         name = this._bluetoothA2DPDeviceName;
      }

      byte[] data = null;
      if (isAudioModeParamSupported(newMode, 0) && name != null) {
         byte[] nameBytes = name.getBytes();
         int length = nameBytes.length;
         if (length > 127) {
            length = 127;
         }

         data = new byte[length + 2];
         data[0] = 0;
         data[1] = (byte)length;
         System.arraycopy(nameBytes, 0, data, 2, length);
      }

      if (isAudioModeParamSupported(newMode, 3) && this._volumeBoostData != null) {
         if (data == null) {
            int length = this._volumeBoostData.length;
            data = new byte[length];
            System.arraycopy(this._volumeBoostData, 0, data, 0, length);
         } else {
            Arrays.append(data, this._volumeBoostData);
         }
      }

      if (isAudioModeParamSupported(newMode, 1) && this._eqPresetData != null) {
         if (data == null) {
            int length = this._eqPresetData.length;
            data = new byte[length];
            System.arraycopy(this._eqPresetData, 0, data, 0, length);
         } else {
            Arrays.append(data, this._eqPresetData);
         }
      }

      System.out.println("AR: setAudioMode " + newMode);
      setAudioMode(newMode, data);
      if (this._callback != null) {
         try {
            this._callback.updateMediaSourceVolume(sink);
         } catch (Throwable var8) {
         }
      }

      if (this._audioSink != sink) {
         this._audioSink = sink;
         if (activeSource < 11) {
            this._audioControlsForSource[activeSource].sinkChanged(sink);
         }

         this.postEvent(2);
      }

      this.updateMasterVolume(false);
      this.postEvent(1);
      return true;
   }

   public final boolean canEnableSink(int sink) {
      return this.canEnableSink(this.getActiveSource(), sink, true);
   }

   public final boolean canEnableSink(int source, int sink, boolean validate) {
      return this.getAudioMode(source, sink, validate) != -1;
   }

   public final boolean isVolumeChangeSupported() {
      return this._masterVolumeChangeSupported;
   }

   public final int getMasterVolume() {
      int index = this.getMasterVolumeIndex();
      return this._masterVolume[index];
   }

   public final void setMasterVolume(int volume) {
      this.setMasterVolume(volume, false);
   }

   public final boolean startTone(int tone) {
      if (!this.anyActiveSources() && Audio.isHeadsetConnected()) {
         this.setSink(3);
      }

      return AudioInternal.startTone(tone);
   }

   public final boolean stopTone() {
      boolean rc = AudioInternal.stopTone();
      if (!this.anyActiveSources() && Audio.isHeadsetConnected()) {
         this.resetSink();
      }

      return rc;
   }

   public final void setMasterVolume(int volume, boolean remote) {
      if (this._masterVolumeChangeSupported) {
         int index = this.getMasterVolumeIndex();
         this.setMasterVolume(volume, remote, index);
      }
   }

   public final void setMasterVolume(int volume, boolean remote, int index) {
      int lowerLimit = 10;
      if (index == 2) {
         lowerLimit = 0;
      }

      volume = MathUtilities.clamp(lowerLimit, volume, 100);
      if (volume != this._masterVolume[index]) {
         this._masterVolume[index] = (byte)volume;
         this.updateMasterVolume(true);
         this.postEvent(1, remote ? 1 : 0);
      }
   }

   public final int incrementMasterVolume(int amount) {
      int index = this.getMasterVolumeIndex();
      if (!this._masterVolumeChangeSupported) {
         return this._masterVolume[index];
      }

      int lowerLimit = 10;
      if (index == 2) {
         lowerLimit = 0;
      }

      int volume = MathUtilities.clamp(lowerLimit, this._masterVolume[index] + amount, 100);
      if (volume == this._masterVolume[index]) {
         return this._masterVolume[index];
      }

      this._masterVolume[index] = (byte)volume;
      this.updateMasterVolume(true);
      this.postEvent(1);
      return this._masterVolume[index];
   }

   public final synchronized void setBluetoothSCOSinkProperties(
      boolean available, boolean remoteVolumeControl, boolean nrec, String deviceName, boolean hasVoiceRecognition
   ) {
      this._bluetoothSCOSinkAvailable = available;
      this._bluetoothSCORemoteVolumeControl = remoteVolumeControl;
      this._bluetoothSCONREC = nrec;
      this._bluetoothSCODeviceName = deviceName;
      this._bluetoothSCOHasVoiceRecognition = hasVoiceRecognition;
      if (available && this.isValidSink(2) || !available && this.getSink() == 2) {
         this.resetSink();
      }
   }

   public final synchronized void setBluetoothA2DPSinkProperties(boolean available, String deviceName) {
      this._bluetoothA2DPSinkAvailable = available;
      this._bluetoothA2DPDeviceName = deviceName;
      if (available && this.isValidSink(5) || !available && this.getSink() == 5) {
         this.resetSink();
      }
   }

   public final boolean isVolumeBoostModeSupported() {
      byte[] b = Branding.getData(23);
      return b != null && b.length == 1 && b[0] != 0 ? false : isAudioModeParamSupported(33, 3);
   }

   public final synchronized void setVolumeBoostMode(boolean available) {
      byte[] b = Branding.getData(23);
      if (b == null || b.length != 1 || b[0] == 0) {
         this._volumeBoostData = new byte[3];
         this._volumeBoostData[0] = 3;
         this._volumeBoostData[1] = 1;
         this._volumeBoostData[2] = (byte)(available ? 1 : 0);
         this.setSink(this.getSink());
      }
   }

   public final boolean isEQPresetSupported() {
      return isAudioModeParamSupported(9, 1);
   }

   public final synchronized void setEQPreset(int eqPreset) {
      if (eqPreset >= 0 && eqPreset <= 2) {
         this._eqPresetData = new byte[3];
         this._eqPresetData[0] = 1;
         this._eqPresetData[1] = 1;
         this._eqPresetData[2] = (byte)eqPreset;
         this.setSink(this.getSink());
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final synchronized int getEQPreset() {
      return this._eqPresetData != null ? this._eqPresetData[2] : 0;
   }

   public final synchronized void addSource(int source) {
      if (source >= 0 && source < 11) {
         this._numAudioSources[source]++;
         if (this._numAudioSources[source] == 1) {
            System.out.println("AR: add source " + source);
            boolean switchSink = true;

            for (int i = 0; i < source; i++) {
               if (this._numAudioSources[i] != 0) {
                  switchSink = false;
               }
            }

            if (switchSink) {
               int newSink = this._audioControlsForSource[source].getSink();
               if (!this.canEnableSink(source, newSink, true)) {
                  newSink = this.getDefaultSink(source);
               }

               this.setSink(newSink);
            }

            this.updateInputFeedback();
            if (this._numAudioSources[source] == 1) {
               this.postEvent(3);
            }
         }
      } else {
         throw new IllegalArgumentException("invalid source");
      }
   }

   public final synchronized void removeSource(int source) {
      if (source < 0 || source >= 11) {
         throw new IllegalArgumentException("invalid source");
      }

      if (this._numAudioSources[source] != 0) {
         this._numAudioSources[source]--;
         if (this._numAudioSources[source] == 0) {
            System.out.println("AR: remove source " + source);
            boolean resetSink = true;

            for (int i = 0; i < source; i++) {
               if (this._numAudioSources[i] != 0) {
                  resetSink = false;
               }
            }

            if (resetSink) {
               this.resetSink();
            }

            this.updateInputFeedback();
            if (this._numAudioSources[source] == 0) {
               this.postEvent(3);
            }
         }
      }
   }

   public final synchronized int getActiveSource() {
      int i;
      for (i = 0; i < 11; i++) {
         if (this._numAudioSources[i] != 0) {
            return i;
         }
      }

      return i;
   }

   public final synchronized boolean isSourceAdded(int source) {
      if (source < 0 || source >= 11) {
         throw new IllegalArgumentException("invalid source");
      } else {
         return this._numAudioSources[source] != 0;
      }
   }

   public final int getDefaultSink(int source) {
      boolean headsetConnected = Audio.isHeadsetConnected();
      switch (source) {
         case -1:
            return 1;
         case 0:
         default:
            if (headsetConnected && this.headsetHasMic()) {
               return 3;
            } else {
               if (this._bluetoothSCOSinkAvailable) {
                  return 2;
               }

               return 0;
            }
         case 1:
            if (headsetConnected && this.headsetHasMic()) {
               return 3;
            } else {
               if (this._bluetoothSCOSinkAvailable && this._bluetoothSCOHasVoiceRecognition) {
                  return 2;
               }

               return 1;
            }
         case 2:
            if (headsetConnected && this.headsetHasMic()) {
               return 3;
            } else {
               if (this._bluetoothSCOSinkAvailable) {
                  return 2;
               }

               return 1;
            }
         case 3:
            if (headsetConnected) {
               return 3;
            }

            return 1;
         case 4:
            if (headsetConnected) {
               return 3;
            } else {
               if (this._bluetoothA2DPSinkAvailable) {
                  return 5;
               }

               return 1;
            }
         case 5:
            if (headsetConnected) {
               return 3;
            } else if (this._bluetoothA2DPSinkAvailable) {
               return 5;
            } else {
               if (this._bluetoothSCOSinkAvailable) {
                  return 2;
               }

               return 0;
            }
         case 6:
            return 1;
         case 7:
         case 8:
            if (headsetConnected && this.headsetHasMic()) {
               return 3;
            }

            return 1;
         case 9:
            if (headsetConnected) {
               return 3;
            }

            return 1;
         case 10:
            if (headsetConnected) {
               return 3;
            } else {
               return this._bluetoothA2DPSinkAvailable ? 5 : 1;
            }
      }
   }

   public final synchronized void resetSink() {
      for (int i = 0; i < 11; i++) {
         if (this._numAudioSources[i] != 0) {
            this.setSink(this.getDefaultSink(i));
            return;
         }
      }

      this.setSink(1);
   }

   public final synchronized boolean isValidSink(int sink) {
      for (int i = 0; i < 11; i++) {
         if (this._numAudioSources[i] != 0) {
            if ((_validSourceSinks[i] & 1 << sink) != 0) {
               return true;
            }

            return false;
         }
      }

      return (_validSourceSinks[11] & 1 << sink) != 0;
   }

   public final synchronized void enableInputFeedback(int src, boolean enable) {
      if (enable) {
         if (!this._feedbackEnabledSources.contains(src)) {
            this._feedbackEnabledSources.addElement(src);
            this.updateInputFeedback();
            return;
         }
      } else {
         this._feedbackEnabledSources.removeElement(src);
         enableInputFeedback0(src, false);
      }
   }

   public final AudioPathControl getAudioPathControl(int source) {
      if (source >= 0 && source < 11) {
         return this._audioControlsForSource[source];
      } else {
         throw new IllegalArgumentException("invalid source");
      }
   }

   public final synchronized void fastReset() {
      Arrays.zero(this._numAudioSources);
      this.updateInputFeedback();
      this.resetSink();
   }

   @Override
   public final void headsetButtonUnclick(int button, int time) {
   }

   @Override
   public final synchronized void headsetInserted(int type) {
      this.resetSink();
   }

   @Override
   public final synchronized void headsetRemoved() {
      if (this.getSink() == 3 || this.getSink() == 4) {
         this.resetSink();
      }
   }

   @Override
   public final void headsetButtonClick(int button, int time) {
   }

   private final boolean anyActiveSources() {
      for (int i = 0; i < 11; i++) {
         if (this._numAudioSources[i] != 0) {
            return true;
         }
      }

      return false;
   }

   private final int getAudioMode(int source, int sink, boolean validate) {
      if (isAudioModeSupported(16) && source != 6 && TTY.getSource() == 0) {
         switch (TTY.getMode()) {
            case -1:
               break;
            case 0:
            default:
               return 16;
            case 1:
               return 17;
            case 2:
               return 18;
         }
      }

      switch (sink) {
         case -1:
            return -1;
         case 0:
         default:
            if (!this.checkHeadset(source)) {
               return -1;
            } else {
               switch (source) {
                  case -1:
                  case 1:
                  case 3:
                  case 4:
                     return 34;
                  case 0:
                  case 2:
                  case 5:
                  case 7:
                  case 8:
                  default:
                     return 9;
                  case 6:
                     return 3;
               }
            }
         case 1:
            if (!this.checkHeadset(source)) {
               return -1;
            } else {
               switch (source) {
                  case -1:
                  case 3:
                  case 4:
                     return 32;
                  case 0:
                  case 5:
                  default:
                     return 6;
                  case 1:
                     return 20;
                  case 2:
                  case 7:
                  case 8:
                     return 41;
                  case 6:
                     return 0;
               }
            }
         case 2:
            if (!this.checkHeadset(source)) {
               return -1;
            } else if (validate && !this._bluetoothSCOSinkAvailable) {
               return -1;
            } else {
               switch (source) {
                  case -1:
                  case 3:
                  case 4:
                  case 6:
                     return -1;
                  case 0:
                  case 2:
                  case 5:
                  case 7:
                  case 8:
                  case 9:
                  case 10:
                  default:
                     if (this._bluetoothSCONREC) {
                        return 11;
                     }

                     return 10;
                  case 1:
                     if (!this._bluetoothSCOHasVoiceRecognition) {
                        return -1;
                     } else {
                        if (this._bluetoothSCONREC) {
                           return 22;
                        }

                        return 21;
                     }
               }
            }
         case 3:
            if (!Audio.isHeadsetConnected()) {
               return -1;
            } else {
               switch (source) {
                  case -1:
                  case 3:
                  case 4:
                     return 33;
                  case 0:
                  default:
                     switch (TTY.getMode()) {
                        case -1:
                           break;
                        case 0:
                        default:
                           return 13;
                        case 1:
                           return 14;
                        case 2:
                           return 15;
                     }
                  case 2:
                  case 5:
                  case 7:
                  case 8:
                     return 7;
                  case 1:
                     return 19;
                  case 6:
                     return 1;
               }
            }
         case 4:
            if (!Audio.isHeadsetConnected()) {
               return -1;
            }

            return 4;
         case 5:
            if (!this.checkHeadset(source)) {
               return -1;
            } else if (validate && !this._bluetoothA2DPSinkAvailable) {
               return -1;
            } else {
               switch (source) {
                  case 4:
                  case 5:
                  case 10:
                     return 36;
                  default:
                     return -1;
               }
            }
      }
   }

   private final void updateMasterVolume(boolean persist) {
      if (this.canChangeMasterVolume()) {
         if (this.getSink() == 2) {
            AudioInternal.setVolume(50);
            return;
         }

         AudioInternal.setVolume(this.getMasterVolume());
         AudioInternal.setToneVolume(100);
         if (persist) {
            this._masterVolumePersistentObject.commit();
            return;
         }
      } else {
         AudioInternal.setVolume(100);
         AudioInternal.setToneVolume(75);
      }
   }

   public static final AudioRouter getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      AudioRouter router = (AudioRouter)ar.getOrWaitFor(-672359094277821107L);
      if (router == null) {
         router = new AudioRouter();
         ar.put(-672359094277821107L, router);
      }

      return router;
   }

   private final synchronized void updateInputFeedback() {
      boolean enable = true;
      if (this._numAudioSources[0] != 0
         || this._numAudioSources[1] != 0
         || this._numAudioSources[2] != 0
         || this._numAudioSources[3] != 0
         || this._numAudioSources[4] != 0
         || this._numAudioSources[5] != 0
         || this._numAudioSources[7] != 0
         || this._numAudioSources[8] != 0
         || this._numAudioSources[9] != 0
         || this._numAudioSources[10] != 0) {
         enable = false;
      }

      for (int i = this._feedbackEnabledSources.size() - 1; i >= 0; i--) {
         enableInputFeedback0(this._feedbackEnabledSources.elementAt(i), enable);
      }
   }

   private AudioRouter() {
      this._numAudioSources = new int[11];
      this._audioControlsForSource = new AudioPathControlImpl[11];

      for (int i = 0; i < 11; i++) {
         this._audioControlsForSource[i] = new AudioPathControlImpl(this, i);
      }

      this._masterVolumePersistentObject = RIMPersistentStore.getPersistentObject(1154807502384803209L);
      synchronized (this._masterVolumePersistentObject) {
         this._masterVolume = (byte[])this._masterVolumePersistentObject.getContents();
         if (this._masterVolume == null) {
            this._masterVolume = new byte[6];
            Arrays.fill(this._masterVolume, (byte)50);
            this._masterVolumePersistentObject.setContents(this._masterVolume, 51, false);
            this._masterVolumePersistentObject.commit();
         }
      }

      this._masterVolumeChangeSupported = true;
      this._feedbackEnabledSources = new IntVector();
      this.resetSink();
   }

   private final boolean checkHeadset(int source) {
      if (Audio.isHeadsetConnected()) {
         switch (source) {
            case -1:
               return false;
            case 0:
            case 1:
            case 2:
               if (!this.headsetHasMic()) {
                  return true;
               }

               return false;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            default:
               return true;
         }
      } else {
         return true;
      }
   }

   private final int getMasterVolumeIndex() {
      return this.getSink();
   }

   private final void postEvent(int event, int subMessage) {
      Message msg = new Message(8, event, subMessage);
      ApplicationManagerInternal appManager = (ApplicationManagerInternal)ApplicationManager.getApplicationManager();
      appManager.postMessage(msg);
   }

   private final void postEvent(int event) {
      this.postEvent(event, 0);
   }

   private final boolean headsetHasMic() {
      switch (AudioInternal.getHeadsetType()) {
         case 2:
            return true;
         case 3:
         case 4:
         default:
            return false;
      }
   }

   public static final void addListener(Application app, AudioRouterListener listener) {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(8) == null) {
            dispatchManager.setDispatcher(8, new AudioRouterEventDispatcher());
         }
      }

      app.addListener(8, listener);
   }

   public static final void removeListener(Application app, AudioRouterListener listener) {
      app.removeListener(8, listener);
   }

   public static final native boolean isAudioModeSupported(int var0);

   private static final native boolean isAudioModeParamSupported(int var0, int var1);

   private static final native void setAudioMode(int var0, byte[] var1);

   private static final native int getAudioMode();

   private static final native void enableInputFeedback0(int var0, boolean var1);
}
