package net.rim.device.api.system;

import javax.microedition.media.MediaException;
import net.rim.device.api.media.control.AudioPathControl;

final class AudioPathControlImpl implements AudioPathControl {
   private int _source;
   private int _sink;
   private AudioRouter _audioRouter;
   private int _previousSink;

   final void sinkChanged(int sink) {
      synchronized (this._audioRouter) {
         if (sink == this._audioRouter.getDefaultSink(this._source)) {
            this._sink = -1;
         } else {
            this._sink = sink;
         }
      }
   }

   final int getSink() {
      synchronized (this._audioRouter) {
         return this._sink == -1 ? this._audioRouter.getDefaultSink(this._source) : this._sink;
      }
   }

   @Override
   public final int getAudioPath() {
      synchronized (this._audioRouter) {
         if (this._audioRouter.getActiveSource() == this._source) {
            return this._audioRouter.getSink();
         }

         int sink;
         if (this._sink == -1) {
            sink = this._audioRouter.getDefaultSink(this._source);
         } else if (!this._audioRouter.canEnableSink(this._source, this._sink, true)) {
            this._sink = -1;
            sink = this._audioRouter.getDefaultSink(this._source);
         } else {
            sink = this._sink;
         }

         return sink;
      }
   }

   @Override
   public final boolean canSwitchToPath(int path) {
      boolean rc = this._audioRouter.canEnableSink(this._source, path, false);
      return rc && path == this.getAudioPath() ? false : rc;
   }

   @Override
   public final synchronized void setAudioPath(int newPath) {
      if (newPath >= 0 && newPath < 6) {
         if (this._audioRouter.getActiveSource() == this._source) {
            if (!this._audioRouter.setSink(newPath)) {
               throw new MediaException("Failed to change audio path");
            }
         } else if (!this._audioRouter.canEnableSink(this._source, newPath, true)) {
            throw new MediaException("Failed to change audio path");
         }

         this._sink = newPath;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final boolean isPathExplicitlySet() {
      return this._sink != -1;
   }

   @Override
   public final void toggleSpeakerphone() {
      synchronized (this._audioRouter) {
         try {
            if (this._audioRouter.getActiveSource() == this._source) {
               switch (this._audioRouter.getSink()) {
                  case -1:
                  case 4:
                     break;
                  case 0:
                  case 2:
                  case 3:
                  case 5:
                  default:
                     this._previousSink = this._audioRouter.getSink();
                     this.setAudioPath(1);
                     break;
                  case 1:
                     if (this._previousSink == 2 && this._audioRouter.canEnableSink(this._source, 2, true)) {
                        this.setAudioPath(2);
                     } else if (this._previousSink == 5 && this._audioRouter.canEnableSink(this._source, 5, true)) {
                        this.setAudioPath(5);
                     } else if (this._previousSink == 3 && this._audioRouter.canEnableSink(this._source, 3, true)) {
                        this.setAudioPath(3);
                     } else {
                        this.setAudioPath(0);
                     }
               }
            }
         } catch (MediaException var4) {
         }
      }
   }

   @Override
   public final void resetAudioPath() {
      synchronized (this._audioRouter) {
         this._sink = -1;
         if (this._audioRouter.getActiveSource() == this._source) {
            this._audioRouter.resetSink();
         }
      }
   }

   @Override
   public final void forceActive(boolean active) {
   }

   AudioPathControlImpl(AudioRouter audioRouter, int source) {
      this._audioRouter = audioRouter;
      this._source = source;
      this._sink = -1;
   }
}
