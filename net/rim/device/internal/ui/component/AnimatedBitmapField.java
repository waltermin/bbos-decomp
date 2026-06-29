package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.ui.Animation;
import net.rim.device.internal.ui.AnimationListener;
import net.rim.device.internal.ui.AnimationThread;
import net.rim.vm.WeakReference;

public class AnimatedBitmapField extends BitmapField implements Animation {
   private EncodedImage _image;
   private int _frameCount;
   private int _currentFrame;
   private int _backgroundColour;
   private int _maxIterations;
   private int _maxImageIterations;
   private int _iterationsLeft;
   private boolean _running;
   private int _backgroundColourBelow = -1;
   private boolean _addedToQueue;
   private boolean _visible;
   private long _timeToExecuteAt;
   WeakReference[] _listeners = new WeakReference[0];
   private static final int GIF_DELAY_FACTOR;
   private static final int MAX_FRAMES_PER_SECOND;
   private static final int MIN_DELAY_PER_FRAME;
   private static final int ANIMATION_STARTED;
   private static final int ANIMATION_STOPPED;
   private static final int ANIMATION_PAUSED;
   private static final int ANIMATION_RESUMED;
   private static final int MAX_MESSAGE_INDEX;

   public AnimatedBitmapField() {
      this((Bitmap)null, 0);
   }

   public AnimatedBitmapField(Bitmap bitmap) {
      this(bitmap, 0);
   }

   public AnimatedBitmapField(Bitmap image, long style) {
      super(image, style);
   }

   public AnimatedBitmapField(EncodedImage image, int maxLoopIterations, long style) {
      super((Bitmap)null, style);
      this.setImage(image);
      this._maxIterations = maxLoopIterations;
      this._iterationsLeft = this._maxIterations;
      this.startAnimation();
   }

   @Override
   public void setBitmap(Bitmap bitmap) {
      super.setBitmap(bitmap);
      AnimationThread.removeAnimation(this);
      this._frameCount = 1;
      this._image = null;
   }

   @Override
   public void setImage(EncodedImage image) {
      super.setImage(image);
      this._image = null;
      this._backgroundColour = -1;
      if (image != null) {
         this._frameCount = image.getFrameCount();
         if (this._frameCount >= 2) {
            this._image = image;
            int imageType = image.getImageType();
            switch (imageType) {
               case 1:
                  GIFEncodedImage gif = (GIFEncodedImage)image;
                  this._backgroundColour = gif.getBackgroundColor();
                  this._maxImageIterations = gif.getIterations();
                  if (this._maxImageIterations == 0) {
                     this._maxImageIterations = Integer.MAX_VALUE;
                  }

                  this._maxIterations = Math.min(this._maxImageIterations, this._maxIterations);
               default:
                  this._currentFrame = -1;
                  if (this._running) {
                     this.notify(1);
                  }

                  this._running = false;
            }
         }
      }
   }

   public void setMaximumLoopIterations(int iterations) {
      if (this._image != null) {
         this._maxIterations = Math.min(this._maxImageIterations, iterations);
      }
   }

   public void setUnderlyingBackgroundColor(int color) {
      this._backgroundColourBelow = color;
   }

   public void startAnimation() {
      if (this._maxIterations > 0) {
         this._iterationsLeft = this._maxIterations;
         if (!this._running && this._image != null) {
            this._running = true;
            AnimationThread.addAnimation(this);
            this._addedToQueue = true;
            this.notify(0);
         }
      }
   }

   public void stopAnimation() {
      if (this._running) {
         this.notify(1);
      }

      this._running = false;
      AnimationThread.removeAnimation(this);
      this._addedToQueue = false;
   }

   public void pauseAnimation() {
      if (this._running && this._addedToQueue) {
         this.notify(2);
         this.toggleAnimation(false);
      }
   }

   public void resumeAnimation() {
      if (this._running && !this._addedToQueue) {
         this.notify(3);
         this.toggleAnimation(true);
      }
   }

   public boolean isAnimated() {
      return this._frameCount > 1;
   }

   public boolean isAnimationRunning() {
      return this._running;
   }

   public void reset() {
      this._currentFrame = -1;
      if (!this._running && this._image != null) {
         this.invalidate();
      }
   }

   private int pushContext(Screen screen, Graphics g, Manager mgr) {
      if (mgr == screen) {
         return 0;
      }

      int count = this.pushContext(screen, g, mgr.getManager());
      g.pushRegion(mgr.getExtent(), -mgr.getHorizontalScroll(), -mgr.getVerticalScroll());
      return count + 1;
   }

   private boolean paintFrame() {
      boolean result = false;
      if (this._currentFrame < this._frameCount) {
         int frame = this._currentFrame;
         if (frame == -1) {
            frame = 0;
         }

         if (this._image.getImageType() == 1) {
            GIFEncodedImage gif = (GIFEncodedImage)this._image;
            if (this._backgroundColourBelow == -1
               && gif.isBackgroundTransparent()
               && (gif.getFrameTransition((frame + this._frameCount - 1) % this._frameCount) == 2 || frame == 0)) {
               this.invalidate();
               return true;
            }
         }

         Screen screen = this.getScreen();
         if (screen != null) {
            Graphics graphics = screen.getGraphics();
            int numToPop = this.pushContext(screen, graphics, this.getManager());
            if (graphics.pushRegion(this.getContentRect())) {
               this.paintFrame(frame, graphics);
               screen.updateDisplay();
               result = true;
            }

            for (int i = 0; i <= numToPop; i++) {
               graphics.popContext();
            }
         }
      }

      return result;
   }

   private void paintFrame(int frame, Graphics graphics) {
      int transition = 2;
      int left = 0;
      int top = 0;
      int xPos = this.getXPos();
      int yPos = this.getYPos();
      boolean trans = false;
      if (this._image.getImageType() == 1) {
         GIFEncodedImage gif = (GIFEncodedImage)this._image;
         transition = gif.getFrameTransition((frame + this._frameCount - 1) % this._frameCount);
         left = gif.getScaledFrameLeft(frame);
         top = gif.getScaledFrameTop(frame);
         trans = gif.isBackgroundTransparent();
      }

      if (frame == 0 || transition == 2) {
         int colour = trans ? this._backgroundColourBelow : this._backgroundColour;
         if (colour != -1) {
            graphics.setColor(colour);
            graphics.fillRect(xPos, yPos, this._image.getScaledWidth(), this._image.getScaledHeight());
         }
      }

      int scaledHeight = this._image.getScaledFrameHeight(frame);
      if (scaledHeight > 0) {
         int scaledWidth = this._image.getScaledFrameWidth(frame);
         if (scaledWidth > 0) {
            this.paintImage(graphics, left + xPos, top + yPos, scaledWidth, scaledHeight, this._image, frame, 0, 0);
         }
      }
   }

   @Override
   protected void paint(Graphics graphics) {
      if (this._image == null) {
         super.paint(graphics);
      } else {
         if (this._visible && !this._addedToQueue && this._running) {
            this.toggleAnimation(true);
         }

         if (this._currentFrame >= this._frameCount) {
            boolean paintFirst = true;
            if (this._frameCount >= 2 && this._image.getImageType() == 1) {
               GIFEncodedImage gif = (GIFEncodedImage)this._image;
               if (gif.getFrameTransition(this._frameCount - 2) == 2) {
                  paintFirst = false;
               }
            }

            if (paintFirst) {
               this.paintFrame(0, graphics);
               if (this._frameCount > 20) {
                  int numFrames = this._frameCount / 10;
                  numFrames -= 2;

                  for (int i = 1; i < numFrames; i++) {
                     this.paintFrame(i, graphics);
                  }

                  for (int i = numFrames; i > 1; i--) {
                     this.paintFrame(this._frameCount - i, graphics);
                  }
               } else {
                  for (int i = 1; i < this._frameCount - 1; i++) {
                     this.paintFrame(i, graphics);
                  }
               }
            }

            this.paintFrame(this._frameCount - 1, graphics);
            return;
         }

         int endFrame = this._currentFrame;
         if (endFrame == -1) {
            endFrame = 0;
         }

         int i = 0;
         if (endFrame > 0 && this._image.getImageType() == 1) {
            GIFEncodedImage gif = (GIFEncodedImage)this._image;
            if (gif.getFrameTransition((endFrame + this._frameCount - 1) % this._frameCount) == 2) {
               i = endFrame;
            }
         }

         while (i <= endFrame) {
            this.paintFrame(i, graphics);
            i++;
         }
      }
   }

   @Override
   protected void onVisibilityChange(boolean visible) {
      super.onVisibilityChange(visible);
      this.toggleAnimation(visible);
      this._visible = visible;
   }

   @Override
   protected void onExposed() {
      super.onExposed();
      this.toggleAnimation(true);
      this._visible = true;
   }

   @Override
   protected void onUndisplay() {
      super.onUndisplay();
      this.toggleAnimation(false);
      this._visible = false;
   }

   @Override
   protected void onObscured() {
      super.onObscured();
      this.toggleAnimation(false);
      this._visible = false;
   }

   private void toggleAnimation(boolean on) {
      if (on && this._running) {
         AnimationThread.addAnimation(this);
         this._addedToQueue = true;
      } else {
         AnimationThread.removeAnimation(this);
         this._addedToQueue = false;
      }
   }

   @Override
   public boolean animate() {
      Field field = this;

      while (field != null) {
         XYRect e = field.getExtent();
         Manager mgr = field.getManager();
         if (mgr != null) {
            int xLeft = mgr.getHorizontalScroll();
            int xRight = xLeft + mgr.getWidth();
            int yTop = mgr.getVerticalScroll();
            int yBottom = yTop + mgr.getHeight();
            if (e.y <= yBottom && e.Y2() >= yTop && e.x <= xRight && e.X2() >= xLeft) {
               field = mgr;
               continue;
            }

            this._addedToQueue = false;
            return false;
         }

         if (field instanceof Screen) {
            Screen s = (Screen)field;
            if (!s.isDisplayed()) {
               return false;
            }
         }
         break;
      }

      boolean result = true;
      if (this._currentFrame == this._frameCount - 1) {
         this._iterationsLeft--;
         if (this._iterationsLeft <= 0) {
            result = false;
            if (this._running) {
               this.notify(1);
            }

            this._running = false;
         }
      }

      synchronized (Application.getEventLock()) {
         this._currentFrame++;
         if (this._iterationsLeft > 0) {
            this._currentFrame = this._currentFrame % this._frameCount;
         }

         if (this._currentFrame < this._frameCount && !this.paintFrame()) {
            this._currentFrame = -1;
         }
      }

      if (this._currentFrame != -1 && this._currentFrame < this._frameCount) {
         int delay = 66;
         if (this._image.getImageType() == 1) {
            delay = Math.max(((GIFEncodedImage)this._image).getFrameDelay(this._currentFrame) * 10, 66);
         }

         this._timeToExecuteAt = System.currentTimeMillis() + delay;
      }

      this._addedToQueue = result;
      return result;
   }

   @Override
   public long getExecutionTime() {
      return this._timeToExecuteAt;
   }

   @Override
   public void addAnimationListener(AnimationListener listener) {
      synchronized (this) {
         int previous = this.findListener(listener, this._listeners);
         if (previous != -1) {
            throw new IllegalStateException("addAnimationListener():  already a listener");
         }

         Arrays.add(this._listeners, new WeakReference(listener));
      }
   }

   @Override
   public void removeAnimationListener(AnimationListener listener) {
      synchronized (this) {
         int index = this.findListener(listener, this._listeners);
         if (index != -1) {
            Arrays.removeAt(this._listeners, index);
         } else {
            throw new IllegalStateException("removeAnimationListener():  listener not found");
         }
      }
   }

   private int findListener(Object search, WeakReference[] listenerList) {
      for (int lv = listenerList.length - 1; lv >= 0; lv--) {
         Object listener = listenerList[lv].get();
         if (listener == search) {
            return lv;
         }
      }

      return -1;
   }

   private void notify(int message) {
      if (message >= 0 && message <= 3) {
         synchronized (this) {
            switch (message) {
               case -1:
                  break;
               case 0:
               default:
                  int count = this._listeners.length;

                  for (int i = 0; i < count; i++) {
                     try {
                        AnimationListener listener = (AnimationListener)this._listeners[i].get();
                        if (listener != null) {
                           listener.animationStarted(this);
                        }
                     } catch (Exception e) {
                        System.err.println("Exception while notifying AnimationListener:  " + e);
                        e.printStackTrace();
                     }
                  }
                  break;
               case 1:
                  int count = this._listeners.length;

                  for (int i = 0; i < count; i++) {
                     try {
                        AnimationListener listener = (AnimationListener)this._listeners[i].get();
                        if (listener != null) {
                           listener.animationStopped(this);
                        }
                     } catch (Exception e) {
                        System.err.println("Exception while notifying AnimationListener:  " + e);
                        e.printStackTrace();
                     }
                  }
            }
         }
      } else {
         throw new IllegalArgumentException("Invalid message to be sent to AnimationListeners:  " + message);
      }
   }
}
