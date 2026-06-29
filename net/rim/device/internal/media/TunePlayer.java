package net.rim.device.internal.media;

import java.io.InputStream;
import javax.microedition.media.Control;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.AlertListener2;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.vad.VADUserEventListener;
import net.rim.device.internal.vad.VADUserEvents;
import net.rim.vm.Array;

class TunePlayer extends PlayerImpl implements AlertListener2, RIMTuneControl, VolumeControl, VADUserEventListener {
   private short[] _tune = new short[0];
   private byte[] _midiTune;
   private int _volume = 100;
   private int _interruptable = 0;
   private boolean _alertStarted;
   private boolean _alertStopped;
   private boolean _alertPaused;
   private boolean _mute;
   private boolean _audioRouterAdded;
   private boolean _wasPlaying;
   private boolean _vadListenerAdded;
   private int _updateTimeEventId = -1;
   private DataSource _dataSource;

   protected TunePlayer() {
      super._audioSourceId = 10;
   }

   protected void addNote(int frequency, int duration) {
      int length = this._tune.length;
      Array.resize(this._tune, length + 2);
      this._tune[length] = (short)frequency;
      this._tune[length + 1] = (short)duration;
   }

   protected void setTune(short[] tune, int format) {
      if (super._state == 400) {
         label26:
         try {
            this.stop();
         } finally {
            break label26;
         }
      }

      if (tune.length != this._tune.length) {
         this._tune = new short[tune.length];
      }

      System.arraycopy(tune, 0, this._tune, 0, tune.length);
   }

   protected void setMIDITune(byte[] midiTune) {
      this._midiTune = midiTune;
   }

   public void setInterruptable(int interruptable) {
      this._interruptable = interruptable;
   }

   public int isInterruptable() {
      return this._interruptable;
   }

   private void setVolume(int volume) {
      this._volume = volume;
      if (!this.isMuted()) {
         Alert.setVolume(volume);
      }
   }

   public int getVolume() {
      return this._volume;
   }

   @Override
   public void audioDone(int reason) {
   }

   @Override
   public void buzzerDone(int reason) {
      this.onDone(reason);
   }

   @Override
   public void midiDone(int reason) {
      this.onDone(reason);
   }

   @Override
   public void vibrateDone(int reason) {
   }

   private void playTune() {
      if (!this._audioRouterAdded) {
         AudioRouter.getInstance().addSource(super._audioSourceId);
         this._audioRouterAdded = true;
      }

      if (!this._vadListenerAdded) {
         VADUserEvents.addListener(this.getApplication(), this);
         this._vadListenerAdded = true;
      }

      super._mediaTime = 0;
      int volume = this.isMuted() ? 0 : this._volume;
      if (Alert.isMIDISupported() && this._midiTune != null) {
         Alert.setVolume(volume);
         this._alertStarted = Alert.startMIDI(this._midiTune, this._interruptable, this) == 0;
      } else if (Alert.isBuzzerSupported() && this._tune.length != 0) {
         this._alertStarted = true;
         Alert.setVolume(volume);
         Alert.startBuzzer(this._tune, volume, this._interruptable);
      }

      if (this._alertStarted) {
         if (this._updateTimeEventId != -1) {
            this.getApplication().cancelInvokeLater(this._updateTimeEventId);
         }

         this._updateTimeEventId = this.getApplication().invokeLater(new TunePlayer$MediaTimeEvent(this), 1000, true);
      }

      this.notifyListeners("started", new Object(this.getMediaTime()));
   }

   @Override
   public short[] getBuzzerTune() {
      return this._tune;
   }

   @Override
   protected void doStart() {
      if (Alert.isMIDISupported() && this._alertPaused && this._midiTune != null) {
         this.resumeTune();
      } else {
         this.playTune();
      }

      this._alertPaused = false;
   }

   @Override
   protected void doStop() {
      this.doStop(true);
   }

   private void resumeTune() {
      if (Alert.resumeMIDI() == 0) {
         this._updateTimeEventId = this.getApplication().invokeLater(new TunePlayer$MediaTimeEvent(this), 1000, true);
         this.notifyListeners("started", new Object(this.getMediaTime()));
      } else {
         this.doStop(false);
      }
   }

   private void doStop(boolean pause) {
      if (pause && Alert.isMIDISupported() && this._midiTune != null) {
         this._alertPaused = Alert.pauseMIDI() == 0;
         if (!this._alertPaused) {
            this.doStop(false);
         }

         if (this._updateTimeEventId != -1) {
            this.getApplication().cancelInvokeLater(this._updateTimeEventId);
            this._updateTimeEventId = -1;
            return;
         }
      } else {
         if (this._alertStarted) {
            super._currentLoopIteration = 0;
            this._alertStarted = false;
            if (Alert.isMIDISupported() && this._midiTune != null) {
               Alert.stopMIDI();
            } else if (Alert.isBuzzerSupported() && this._tune.length != 0) {
               Alert.stopBuzzer();
            }

            synchronized (this) {
               long stopRequestTime = System.currentTimeMillis();

               while (!this._alertStopped) {
                  label99:
                  try {
                     this.wait(5000);
                  } finally {
                     break label99;
                  }

                  if (System.currentTimeMillis() - stopRequestTime >= 4000) {
                     break;
                  }
               }
            }

            this._alertStopped = false;
         }

         if (this._audioRouterAdded) {
            AudioRouter.getInstance().removeSource(super._audioSourceId);
            this._audioRouterAdded = false;
         }
      }
   }

   @Override
   protected synchronized void doDeallocate() {
      if (this._alertPaused) {
         this.doStop(false);
      }

      if (this._updateTimeEventId != -1) {
         this.getApplication().cancelInvokeLater(this._updateTimeEventId);
         this._updateTimeEventId = -1;
      }
   }

   private synchronized void onDone(int reason) {
      if (this._audioRouterAdded) {
         AudioRouter.getInstance().removeSource(super._audioSourceId);
         this._audioRouterAdded = false;
      }

      if (this._vadListenerAdded && !this._wasPlaying) {
         VADUserEvents.removeListener(this.getApplication(), this);
      }

      if (this._updateTimeEventId != -1) {
         this.getApplication().cancelInvokeLater(this._updateTimeEventId);
         this._updateTimeEventId = -1;
      }

      if (reason != 3 && this._alertStarted && reason != 1) {
         if (reason == 2 && super._state == 400) {
            super._mediaTime = this.getDuration();
            this.notifyListeners("endOfMedia", new Object(super._mediaTime));
            boolean playAgain = false;
            if (super._state == 400) {
               playAgain = super._loopCount == -1;
               if (!playAgain && super._currentLoopIteration < super._loopCount - 1) {
                  super._currentLoopIteration++;
                  playAgain = true;
               }
            }

            if (playAgain) {
               this.doStart();
               super._mediaTime = 0;
               this.notifyListeners("started", new Object(super._mediaTime));
               return;
            }

            super._currentLoopIteration = 0;
            super._state = 300;
         }
      } else {
         this._alertPaused = false;
         super._currentLoopIteration = 0;
         super._state = 300;
         this.notifyListeners("stopped", new Object(super._mediaTime));
         this._alertStopped = true;
         this.notifyAll();
      }
   }

   @Override
   public void setKeyValue(String key, Object value) {
      if ("interrupt_on_user_input".equals(key)) {
         if (value instanceof Object) {
            this.setInterruptable(value);
            return;
         }
      } else {
         if ("datasource".equals(key)) {
            this._dataSource = (DataSource)value;
            SourceStream[] sources = this._dataSource.getStreams();
            this.read((InputStream)(new Object(sources[0])));
            return;
         }

         if ("audiosource".equals(key)) {
            if (!this._audioRouterAdded) {
               super._audioSourceId = value;
               return;
            }
         } else {
            super.setKeyValue(key, value);
         }
      }
   }

   @Override
   protected void doDataSourceClose() {
      if (this._dataSource != null) {
         this._dataSource.disconnect();
      }
   }

   @Override
   public Control getControl(String controlType) {
      controlType = this.getFullyQualifiedControlType(controlType);
      Control control = super.getControl(controlType);
      if (control == null) {
         if ("javax.microedition.media.control.VolumeControl".equals(controlType)) {
            return this;
         }
      } else if ("net.rim.device.api.media.control.AudioPathControl".equals(controlType)) {
         return this;
      }

      return control;
   }

   @Override
   public Control[] getControls() {
      Control[] controls = super.getControls();
      if (controls == null) {
         return new Object[]{this};
      }

      int tail = controls.length;
      Array.resize(controls, tail + 1);
      controls[tail] = this;
      return controls;
   }

   @Override
   public void vadEvent(int event, int context) {
      if (event == 1) {
         if (super._state == 400) {
            this._wasPlaying = true;
            this.doStop(false);
            return;
         }
      } else if (event == 3 && this._wasPlaying) {
         this._wasPlaying = false;
         this.doStart();
      }
   }

   @Override
   public boolean isMuted() {
      return this._mute;
   }

   @Override
   public void setMute(boolean mute) {
      boolean oldMute = this.isMuted();
      this._mute = mute;
      if (oldMute != this._mute) {
         Alert.setVolume(mute ? 0 : this._volume);
         this.notifyListeners("volumeChanged", this);
      }
   }

   @Override
   public int getLevel() {
      return this.getVolume();
   }

   @Override
   public int setLevel(int level) {
      level = MathUtilities.clamp(0, level, 100);
      int oldLevel = this.getLevel();
      this.setVolume(level);
      if (oldLevel != level) {
         this.notifyListeners("volumeChanged", this);
      }

      return level;
   }
}
