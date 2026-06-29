package net.rim.device.apps.internal.explorer.file.render;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Backlight;
import net.rim.device.apps.internal.explorer.file.FileItemField;
import net.rim.device.apps.internal.explorer.file.options.ExplorerOptions;

final class SlideShowThread extends Thread {
   private boolean _done;
   private boolean _paused;
   private boolean _obscured;
   private long _startTime;
   private RenderScreen _screen;
   private FileItemField _initialSlide;
   private FileItemField _nextSlide;

   SlideShowThread(RenderScreen screen, FileItemField fileItem) {
      this._initialSlide = fileItem;
      this._screen = screen;
   }

   @Override
   public final void run() {
      while (!this._done) {
         long waitTime = (long)ExplorerOptions.getOptions().getSlideShowDisplayTime() * 1000;
         this._screen.loadContent();
         FileItemField nextSlide = null;

         while (!this._done && waitTime > 0) {
            synchronized (this) {
               boolean wasPaused = this._paused;
               boolean wasObscured = this._obscured;
               this.setSlideStartTime(System.currentTimeMillis());

               label176:
               try {
                  if (!wasPaused && !wasObscured) {
                     Backlight.enable(true, 10);
                     this.wait(Math.min(waitTime, 10000));
                  } else {
                     this.wait();
                  }
               } finally {
                  break label176;
               }

               if (!wasPaused && !wasObscured) {
                  waitTime -= System.currentTimeMillis() - this._startTime;
               }

               if (wasObscured && !this._obscured) {
                  waitTime = Math.max(waitTime, 1000);
               } else {
                  nextSlide = this._nextSlide;
                  this._nextSlide = null;
                  if (nextSlide != null) {
                     break;
                  }
               }
            }
         }

         if (!this._screen.isDisplayed()) {
            this._done = true;
         } else {
            FileItemField searchItem = null;

            while (this._screen.isDisplayed() && !this._done) {
               if (nextSlide == null) {
                  nextSlide = this._screen.getNextViewableItem(searchItem, true, true);
                  if (nextSlide == null || nextSlide == this._initialSlide) {
                     synchronized (Application.getEventLock()) {
                        if (this._screen.isDisplayed()) {
                           this._screen.close();
                        }
                     }

                     this._done = true;
                  }
               }

               if (nextSlide == null || nextSlide.getMediaType() == 1 && this._screen.setItem(nextSlide)) {
                  break;
               }

               searchItem = nextSlide;
               nextSlide = null;
            }
         }
      }
   }

   final synchronized void pause(boolean enable) {
      this._paused = enable;
   }

   final boolean isPaused() {
      return this._paused;
   }

   final synchronized void setSlideStartTime(long time) {
      this._startTime = time;
   }

   final synchronized void setNextSlide(FileItemField item) {
      this._nextSlide = item;
   }

   final void setObscured(boolean obscured) {
      synchronized (this) {
         this._obscured = obscured;
      }

      if (this.isAlive()) {
         this.interrupt();
      }
   }
}
