package net.rim.device.apps.internal.browser.plugin.media.field;

import java.lang.ref.WeakReference;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.text.TextRect;
import net.rim.device.internal.ui.Animation;
import net.rim.device.internal.ui.AnimationListener;
import net.rim.device.internal.ui.AnimationThread;

public final class MarqueeDetailsField extends Field implements Animation {
   private String _text;
   private boolean _marquee;
   private boolean _ellipsis;
   private boolean _marqueeNeeded;
   private boolean _ellipsisNeeded;
   private boolean _running;
   private boolean _wasRunning;
   private boolean _dontDraw;
   private int _orientation = 0;
   private int _x;
   private int _x2;
   private int _tempX;
   private int _spacerWidth;
   private int _textWidth;
   private int _direction = 2;
   private long _animationTime;
   private boolean _stopForever;
   private TextRect _textRect;
   private TextRect _textRect2;
   private WeakReference _listener;
   private static final int ALL = 0;
   private static final int TRAILING = 1;
   private static final int PAUSED = 2;
   private static final int LEFT_TO_RIGHT = 0;
   private static final int RIGHT_TO_LEFT = 1;
   private static final int INCREMENT = 1;
   private static final int INTERVAL = 35;
   private static final int BACKWARD_DELAY = 171;
   private static final int START_DELAY = 85;

   public final void reset(boolean repaint) {
      if (this._orientation == 0) {
         this._x = 0;
         this._x2 = this._x + this._textWidth + this._spacerWidth;
      } else {
         this._x = this.getWidth() - this._textWidth;
         this._x2 = this._x - this._textWidth - this._spacerWidth;
      }

      this._tempX = 171;
      this._direction = 2;
      if (repaint) {
         synchronized (Application.getEventLock()) {
            this.invalidate();
         }
      }
   }

   public final void stopAnimation(boolean forever) {
      this._stopForever = forever;
      AnimationThread.removeAnimation(this);
      this._running = false;
      if (this._listener != null) {
         Object listener = this._listener.get();
         if (listener instanceof Object) {
            ((AnimationListener)listener).animationStopped(this);
         }
      }
   }

   @Override
   public final void addAnimationListener(AnimationListener listener) {
      this._listener = (WeakReference)(new Object(listener));
   }

   @Override
   public final void removeAnimationListener(AnimationListener listener) {
      if (this._listener != null) {
         Object listenerRef = this._listener.get();
         if (listenerRef == listener) {
            this._listener.clear();
            this._listener = null;
         }
      }
   }

   @Override
   public final long getExecutionTime() {
      return this._animationTime;
   }

   @Override
   public final boolean animate() {
      if (this._running) {
         synchronized (Application.getEventLock()) {
            this.incrementX();
            this.invalidate();
            this._animationTime = System.currentTimeMillis() + 35;
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   protected final void onUndisplay() {
      this.onVisibilityChange(false);
   }

   @Override
   protected final void onDisplay() {
      this.onVisibilityChange(true);
   }

   @Override
   protected final void paint(Graphics g) {
      if (!this._dontDraw) {
         int availWidth = this.getWidth();
         int availHeight = this.getHeight();
         int layoutWidth = availWidth;
         int posX = 0;
         int posY = 0;
         if (this._ellipsisNeeded) {
            if (this._marqueeNeeded) {
               layoutWidth = availWidth + Math.abs(this._x);
               posX = this._x;
            }
         } else if (this._marqueeNeeded) {
            layoutWidth = Integer.MAX_VALUE;
            posX = this._x;
         }

         this._textRect.layout(layoutWidth, availHeight);
         this._textRect.setPosition(posX, posY);
         this._textRect.paintSelf(g);
         if (this._marqueeNeeded) {
            this._textRect2.layout(layoutWidth, availHeight);
            this._textRect2.setPosition(this._x2, posY);
            this._textRect2.paintSelf(g);
         }

         if (this._marqueeNeeded && !this._running) {
            this.internalStartAnimation();
         }
      }
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (!visible) {
         if (this._running && !Backlight.isEnabled()) {
            this._wasRunning = true;
         }

         this.stopAnimation(false);
      } else {
         if (this._wasRunning && this._marqueeNeeded && !this._running) {
            this.internalStartAnimation();
         }
      }
   }

   @Override
   protected final void layout(int width, int height) {
      Font font = this.getFont();
      TextMetrics metrics = Ui.getTmpTextMetrics();
      font.measureText(this._text, 0, this._text.length(), null, metrics);
      this._textWidth = metrics.iBoundsBrX - metrics.iBoundsTlX;
      Ui.returnTmpTextMetrics(metrics);
      this._textRect = (TextRect)(new Object(this, this._text, 6));
      this._textRect.layout(Integer.MAX_VALUE, Integer.MAX_VALUE);
      int textHeight = this._textRect.getExtent().height;
      this._orientation = this.calculateTextOrientation();
      int drawStyle = 4;
      if (this._textWidth > width) {
         this._spacerWidth = width / 4;
         this._direction = 2;
         if (this._marquee) {
            this._marqueeNeeded = true;
            drawStyle = 6;
         }

         if (this._ellipsis) {
            this._ellipsisNeeded = true;
            drawStyle |= 64;
         }
      }

      this._textRect.setStyle(drawStyle);
      if (this._marqueeNeeded) {
         this._textRect2 = (TextRect)(new Object(this, this._text, drawStyle));
      }

      if (textHeight > height) {
         this._dontDraw = true;
         this.setExtent(0, 0);
      } else {
         this.setExtent(width, textHeight);
      }

      this.reset(false);
      this._tempX = 85;
   }

   private final void internalStartAnimation() {
      if (!this._stopForever) {
         if (this._marqueeNeeded) {
            this._animationTime = 0;
            AnimationThread.addAnimation(this);
            this._running = true;
            if (this._listener != null) {
               Object listener = this._listener.get();
               if (listener instanceof Object) {
                  ((AnimationListener)listener).animationStarted(this);
               }
            }
         }
      }
   }

   public MarqueeDetailsField(String text, long style, boolean marquee, boolean ellipsis) {
      super(style);
      this._text = text;
      this._marquee = marquee;
      this._ellipsis = ellipsis;
   }

   private final int calculateTextOrientation() {
      return this._textRect != null && (this._textRect.getLineInfoForDocPos(0, true)._line._flags & 16) != 0 ? 1 : 0;
   }

   private final void incrementX() {
      if (this._direction == 2) {
         this._tempX--;
         if (this._tempX <= 0) {
            this._direction = 0;
         }
      } else {
         int width = -1;
         if (this._orientation == 1) {
            width = this.getWidth();
         }

         if (this._direction == 0) {
            if (this._orientation == 0) {
               if (this._x <= -this._textWidth) {
                  this._direction = 1;
               }
            } else if (this._x >= width) {
               this._direction = 1;
            }
         } else if (this._direction == 1) {
            if (this._orientation == 0) {
               if (this._x2 <= 0 || this._textWidth - this._x2 < 1) {
                  this.reset(false);
                  return;
               }
            } else {
               int pos = this.getWidth() - this._textWidth;
               if (this._x2 >= pos || pos - this._x2 < 1) {
                  this.reset(false);
                  return;
               }
            }
         }

         if (this._orientation == 0) {
            this._x--;
            this._x2--;
         } else {
            this._x++;
            this._x2++;
         }
      }
   }
}
