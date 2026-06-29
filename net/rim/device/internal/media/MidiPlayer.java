package net.rim.device.internal.media;

import java.io.InputStream;
import javax.microedition.media.Control;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.AlertListener2;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.system.ActiveMedia;
import net.rim.device.internal.vad.VADUserEventListener;
import net.rim.device.internal.vad.VADUserEvents;
import net.rim.vm.Array;

class MidiPlayer extends PlayerImpl implements AlertListener2, RIMTuneControl, VolumeControl, VADUserEventListener {
   private byte[] _midiData;
   private MidiModel _model;
   private boolean _mute;
   private boolean _alertPaused;
   private boolean _wasPlaying;
   private int _interruptable = 0;
   private int _updateTimeEventId = -1;
   private boolean _alertStarted;
   private boolean _alertStopped;
   private boolean _restartAtZero;
   private int _volume = 100;
   private boolean _vadListenerAdded;
   private DataSource _dataSource;

   public boolean isPolyphonic() {
      return this._model.isPolyphonic();
   }

   @Override
   public int getLevel() {
      return this._volume;
   }

   @Override
   public boolean isMuted() {
      return this._mute;
   }

   @Override
   public short[] getBuzzerTune() {
      this.chkClosed(true);
      return this._model.getBuzzerTune();
   }

   @Override
   public int setLevel(int level) {
      level = MathUtilities.clamp(0, level, 100);
      int oldLevel = this.getLevel();
      this._volume = level;
      if (!this.isMuted()) {
         Alert.setVolume(level);
      }

      if (oldLevel != level) {
         this.notifyListeners("volumeChanged", this);
      }

      return level;
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
   public void setKeyValue(String key, Object value) {
      if ("interrupt_on_user_input".equals(key)) {
         if (value instanceof Integer) {
            this._interruptable = (Integer)value;
            return;
         }
      } else {
         if ("datasource".equals(key)) {
            this._dataSource = (DataSource)value;
            SourceStream[] sources = this._dataSource.getStreams();
            this.read(new DataSourceInputStream(sources[0]));
            return;
         }

         if ("audiosource".equals(key)) {
            super._audioSourceId = (Integer)value;
            return;
         }

         super.setKeyValue(key, value);
      }
   }

   @Override
   public long getDuration() {
      this.chkClosed(true);
      return this._model.getDuration();
   }

   @Override
   protected void doDataSourceClose() {
      if (this._dataSource != null) {
         this._dataSource.disconnect();
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

   public MidiPlayer() {
      if (!Alert.isMIDISupported()) {
         throw new IllegalStateException("MIDI not supported");
      }

      this._interruptable = this._interruptable;
      this._dataSource = this._dataSource;
      super._audioSourceId = 10;
   }

   private synchronized void onDone(int reason) {
      if (reason == 3 && this._restartAtZero) {
         this._restartAtZero = false;
         this._alertStarted = Alert.startMIDI(this._midiData, this._interruptable, this) == 0;
         if (this._alertStarted) {
            this.notifyAll();
            return;
         }

         this.notifyListeners("error", "Error while setting media time to 0");
      }

      this.stopped();
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
            this.notifyListeners("endOfMedia", new Long(super._mediaTime));
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
               this.notifyListeners("started", new Long(super._mediaTime));
               return;
            }

            super._currentLoopIteration = 0;
            super._state = 300;
         }
      } else {
         super._currentLoopIteration = 0;
         this._alertPaused = false;
         super._state = 300;
         this.notifyListeners("stopped", new Long(super._mediaTime));
         this._alertStopped = true;
         this.notifyAll();
      }
   }

   private void playTune() {
      this.starting();
      if (!this._vadListenerAdded) {
         VADUserEvents.addListener(this.getApplication(), this);
         this._vadListenerAdded = true;
      }

      super._mediaTime = 0;
      int volume = this.isMuted() ? 0 : this._volume;
      Alert.setVolume(volume);
      this._alertStarted = Alert.startMIDI(this._midiData, this._interruptable, this) == 0;
      if (this._alertStarted) {
         if (this._updateTimeEventId != -1) {
            this.getApplication().cancelInvokeLater(this._updateTimeEventId);
         }

         this._updateTimeEventId = this.getApplication().invokeLater(new MidiPlayer$MediaTimeEvent(this), 1000, true);
      }

      this.notifyListeners("started", new Long(this.getMediaTime()));
   }

   @Override
   public void read(InputStream stream) {
      try {
         this._midiData = IOUtilities.streamToBytes(stream);
         this._model = new MidiModel(this._midiData);
      } finally {
         return;
      }
   }

   private void resumeTune() {
      if (Alert.resumeMIDI() == 0) {
         this._updateTimeEventId = this.getApplication().invokeLater(new MidiPlayer$MediaTimeEvent(this), 1000, true);
         this.notifyListeners("started", new Long(this.getMediaTime()));
      } else {
         this.doStop(false);
      }
   }

   @Override
   protected void doMakeMediaUnavailable(ActiveMedia media) {
      if (Alert.isSingleSharedAudioChannel()) {
         this.notifyListeners("deviceUnavailable", "");
         if (super._state == 400) {
            this.doStop(false);
            return;
         }
      } else {
         super.doMakeMediaUnavailable(media);
      }
   }

   @Override
   public long setMediaTime(long mediaTime) {
      this.chkClosed(true);
      if (!Alert.isMIDISupported()) {
         mediaTime *= 1000;
         super._mediaTime = mediaTime;
         return mediaTime;
      }

      if (mediaTime == 0) {
         if (super._state == 400) {
            this._restartAtZero = true;
            Alert.stopMIDI();
            synchronized (this) {
               long stopRequestTime = System.currentTimeMillis();

               while (this._restartAtZero) {
                  label55:
                  try {
                     this.wait(5000);
                  } finally {
                     break label55;
                  }

                  if (System.currentTimeMillis() - stopRequestTime >= 4000) {
                     break;
                  }
               }

               super._mediaTime = 0;
            }
         } else {
            this._alertPaused = false;
            this.doStop(false);
            super._mediaTime = 0;
         }
      }

      return super._mediaTime;
   }

   @Override
   protected void doStop() {
      this.doStop(true);
   }

   private void doStop(boolean pause) {
      if (pause) {
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
            Alert.stopMIDI();
            synchronized (this) {
               long stopRequestTime = System.currentTimeMillis();

               while (!this._alertStopped) {
                  label73:
                  try {
                     this.wait(5000);
                  } finally {
                     break label73;
                  }

                  if (System.currentTimeMillis() - stopRequestTime >= 4000) {
                     break;
                  }
               }
            }

            this._alertStopped = false;
         }

         this.stopped();
      }
   }

   @Override
   protected void doStart() {
      if (this._alertPaused) {
         this.resumeTune();
      } else {
         this.playTune();
      }

      this._alertPaused = false;
   }

   @Override
   public String getContentType() {
      this.chkClosed(true);
      return "audio/midi";
   }

   @Override
   public Control getControl(String controlType) {
      controlType = this.getFullyQualifiedControlType(controlType);
      Control control = super.getControl(controlType);
      if (control != null) {
         return control;
      } else {
         this.chkClosed(true);
         if ("javax.microedition.media.control.MetaDataControl".equals(controlType)) {
            return this._model;
         } else if ("javax.microedition.media.control.VolumeControl".equals(controlType)) {
            return this;
         } else {
            return "net.rim.device.api.media.control.AudioPathControl".equals(controlType) ? this : null;
         }
      }
   }

   @Override
   public Control[] getControls() {
      this.chkClosed(true);
      Control[] controls = super.getControls();
      if (controls == null) {
         return new Control[]{this, this._model};
      }

      int tail = controls.length;
      Array.resize(controls, tail + 2);
      controls[tail] = this;
      controls[tail + 1] = this._model;
      return controls;
   }
}
