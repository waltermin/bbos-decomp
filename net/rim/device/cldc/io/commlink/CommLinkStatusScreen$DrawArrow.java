package net.rim.device.cldc.io.commlink;

import net.rim.device.api.ui.Graphics;

class CommLinkStatusScreen$DrawArrow implements Runnable {
   private boolean _left;
   private boolean _draw;
   private boolean _finished;
   private final CommLinkStatusScreen this$0;

   private CommLinkStatusScreen$DrawArrow(CommLinkStatusScreen _1, boolean left, boolean draw) {
      this.this$0 = _1;
      this._left = left;
      this._draw = draw;
      this._finished = true;
   }

   private synchronized void setNewState(boolean left, boolean draw) {
      this._left = left;
      this._draw = draw;
      this._finished = false;
   }

   public void paintArrows(Graphics graphics) {
      if (this._draw) {
         if (this._left) {
            graphics.drawBitmap(this.this$0._leftPos, this.this$0._leftArrow, 0, 0);
            return;
         }

         graphics.drawBitmap(this.this$0._rightPos, this.this$0._rightArrow, 0, 0);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public synchronized void run() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         if (!this.this$0._die) {
            this.this$0.invalidate(this.this$0._arrowPos.x, this.this$0._arrowPos.y, this.this$0._arrowPos.width, this.this$0._arrowPos.height);
            var3 = false;
         } else {
            var3 = false;
         }
      } finally {
         if (var3) {
            this._finished = true;
         }
      }

      this._finished = true;
   }

   CommLinkStatusScreen$DrawArrow(CommLinkStatusScreen x0, boolean x1, boolean x2, CommLinkStatusScreen$1 x3) {
      this(x0, x1, x2);
   }
}
