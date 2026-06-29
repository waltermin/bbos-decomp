package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.util.Frame;

public final class FrameComparator {
   private boolean _framesDifferByMoreThanOneURL;
   private Frame _differentFrame1;
   private Frame _differentFrame2;

   public FrameComparator() {
      this.reset();
   }

   public final void reset() {
      this._framesDifferByMoreThanOneURL = false;
      this._differentFrame1 = null;
      this._differentFrame2 = null;
   }

   public final boolean equals(Frame frame1, Frame frame2) {
      if (frame1 == frame2) {
         return true;
      }

      if (frame1 != null && frame2 != null) {
         boolean framesEqual = true;
         Frame[] children1 = frame1.getChildren();
         Frame[] children2 = frame2.getChildren();
         if ((children1 != null || children2 == null)
            && (children1 == null || children2 != null)
            && (children1 == null || children2 == null || children1.length == children2.length)
            && StringUtilities.strEqualIgnoreCase(frame1.getName(), frame2.getName(), 1701707776)) {
            if (!StringUtilities.strEqual(frame1.getUrl(), frame2.getUrl())) {
               if (this._differentFrame1 != null || this._framesDifferByMoreThanOneURL) {
                  this._framesDifferByMoreThanOneURL = true;
                  return false;
               }

               framesEqual = false;
               this._differentFrame1 = frame1;
               this._differentFrame2 = frame2;
            }

            if (children1 != null && children2 != null) {
               for (int child = 0; child < children1.length; child++) {
                  if (!this.equals(children1[child], children2[child])) {
                     framesEqual = false;
                     if (this._framesDifferByMoreThanOneURL) {
                        return framesEqual;
                     }
                  }
               }
            }

            return framesEqual;
         } else {
            this._framesDifferByMoreThanOneURL = true;
            return false;
         }
      } else {
         this._framesDifferByMoreThanOneURL = true;
         return false;
      }
   }

   public final Frame getDifferentFrame1() {
      return this._framesDifferByMoreThanOneURL ? null : this._differentFrame1;
   }

   public final Frame getDifferentFrame2() {
      return this._framesDifferByMoreThanOneURL ? null : this._differentFrame2;
   }
}
