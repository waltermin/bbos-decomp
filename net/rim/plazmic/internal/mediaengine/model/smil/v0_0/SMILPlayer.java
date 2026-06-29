package net.rim.plazmic.internal.mediaengine.model.smil.v0_0;

import java.util.Enumeration;
import javax.microedition.media.control.VolumeControl;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.event.EventEngine;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing.MediaObject;
import net.rim.plazmic.internal.mediaengine.util.RIMPlatformImpl;
import net.rim.plazmic.mediaengine.MediaListener;
import net.rim.vm.PersistentInteger;

public class SMILPlayer implements MediaListener {
   private SMILVolumeControl _playerVC;
   private int _state = 0;
   private int _numTimesPaused = 0;
   private SMILModel _model;
   private EventEngine _engine;
   private RegionManager _regionManager;
   private MediaObject _currentAudio;
   private MediaObject _currentVideo;
   private static final long AUDIO_VOLUME_KEY;
   private static final long AUDIO_MUTE_KEY;
   private static final int VOLUME_MUTED;
   private static final int VOLUME_NOT_MUTED;
   private static int _audioVolumeHandle;
   private static int _audioMuteHandle = PersistentInteger.getId(-2226019264938368368L, 0);
   public static final int UNREALIZED;
   public static final int REALIZED;
   public static final int PLAYING;
   public static final int PAUSED;
   public static final int ENDED;

   public void setCurrentAudio(MediaObject mo) {
      this._currentAudio = mo;
   }

   public void setCurrentVideo(MediaObject mo) {
      this._currentVideo = mo;
   }

   MediaObject getCurrentAudio() {
      return this._currentAudio;
   }

   MediaObject getCurrentVideo() {
      return this._currentVideo;
   }

   void setVolumeLevel(int volume) {
      PersistentInteger.set(_audioVolumeHandle, volume);
      if (this._currentAudio != null) {
         VolumeControl vc = this._currentAudio.getVolumeControl();
         if (vc != null) {
            vc.setLevel(volume);
         }
      }

      if (this._currentVideo != null) {
         VolumeControl vc = this._currentVideo.getVolumeControl();
         if (vc != null) {
            vc.setLevel(volume);
         }
      }
   }

   void setMute(boolean mute) {
      PersistentInteger.set(_audioMuteHandle, mute ? 1 : 0);
      if (this._currentAudio != null) {
         VolumeControl vc = this._currentAudio.getVolumeControl();
         if (vc != null) {
            vc.setMute(mute);
         }
      }

      if (this._currentVideo != null) {
         VolumeControl vc = this._currentVideo.getVolumeControl();
         if (vc != null) {
            vc.setMute(mute);
         }
      }
   }

   public SMILVolumeControl getVolumeControl() {
      return this._playerVC;
   }

   public void realize(SMILModel model, EventEngine engine, RegionManager regionManager) {
      this._model = model;
      this._engine = engine;
      this._regionManager = regionManager;
      this._state = 1;
      this._numTimesPaused = 0;
   }

   public int getState() {
      return this._state;
   }

   public void close() {
      this._state = 0;
      if (this._engine != null) {
         this._engine.stop();
         this._engine.cancelAllEvents();
      }

      if (this._model != null) {
         Enumeration timingObjects = this._model.getAllTimingObjects();

         while (timingObjects.hasMoreElements()) {
            Object nextObject = timingObjects.nextElement();
            if (nextObject instanceof MediaObject) {
               MediaObject mo = (MediaObject)nextObject;
               mo.close();
            }
         }
      }
   }

   public void startPlayback() {
      if (this._state == 1 && this._engine != null) {
         this._state = 2;
         this._engine.start();
         Event startEvent = this._model.getStartEvent();
         startEvent._sender = this._engine;
         startEvent._listener = this._regionManager;
         this._engine.postEvent(startEvent, false);
      }
   }

   public void endPlayback() {
      if (this._state != 0 && this._state != 3) {
         this._state = 3;
         this.pause();
         this._engine.cancelAllEvents();
      }
   }

   public void pausePlayback() {
      this._numTimesPaused++;
      if (this._state == 2) {
         this._state = 1;
         this.pause();
      }
   }

   public void resumePlayback() {
      if (this._numTimesPaused > 0) {
         this._numTimesPaused--;
      }

      if (this._numTimesPaused <= 0 && this._state == 1) {
         this._state = 2;
         this.resume();
      }
   }

   public void restartPlayback() {
      if (this._state != 0) {
         this._state = 2;
         this._numTimesPaused = 0;
         if (this._model != null) {
            Enumeration timingObjects = this._model.getAllTimingObjects();

            while (timingObjects.hasMoreElements()) {
               Object nextObject = timingObjects.nextElement();
               if (nextObject instanceof MediaObject) {
                  MediaObject mo = (MediaObject)nextObject;
                  mo.restart();
               }
            }
         }

         if (this._engine != null) {
            this._engine.stop();
            this._engine.setTime(0);
            this._engine.cancelAllEvents();
            Event startEvent = this._model != null ? this._model.getStartEvent() : null;
            if (startEvent != null) {
               startEvent._sender = this._engine;
               startEvent._listener = this._regionManager;
               this._engine.postEvent(startEvent, false);
               this._engine.start();
            } else {
               throw new Object("No start event");
            }
         }
      }
   }

   @Override
   public void mediaEvent(Object sender, int event, int eventParam, Object data) {
      if (event == 4) {
         this._state = 3;
      }
   }

   public SMILPlayer() {
      this._playerVC = new SMILVolumeControl(this);
   }

   public static int getVolumeLevel() {
      return PersistentInteger.get(_audioVolumeHandle);
   }

   public static boolean isMuted() {
      return PersistentInteger.get(_audioMuteHandle) == 1;
   }

   private void pause() {
      if (this._engine != null) {
         this._engine.stop();
      }

      if (this._model != null) {
         Enumeration timingObjects = this._model.getAllTimingObjects();

         while (timingObjects.hasMoreElements()) {
            Object nextObject = timingObjects.nextElement();
            if (nextObject instanceof MediaObject) {
               MediaObject mo = (MediaObject)nextObject;
               if (mo.getState() == 1) {
                  mo.pause();
               }
            }
         }
      }
   }

   private void resume() {
      if (this._engine != null) {
         this._engine.start();
      }

      if (this._model != null) {
         Enumeration timingObjects = this._model.getAllTimingObjects();

         while (timingObjects.hasMoreElements()) {
            Object nextObject = timingObjects.nextElement();
            if (nextObject instanceof MediaObject) {
               MediaObject mo = (MediaObject)nextObject;
               if (mo.getState() == 1) {
                  mo.resume();
               }
            }
         }
      }
   }

   static {
      int defaultVolume = RIMPlatformImpl.getMediumVolume();
      _audioVolumeHandle = PersistentInteger.getId(-7706154300538739807L, defaultVolume);
   }
}
