package net.rim.device.apps.internal.browser.ui;

final class TextFlowCell$AnimationProperties {
   int _animationStyle;
   private int _scrollDelay;
   private int _scrollAmount;
   private int _animationOffset;
   private int _loopCount;
   private boolean _drawText = true;
   private long _nextAnimationTime;
   private int _maxLineWidth;

   static final int access$210(TextFlowCell$AnimationProperties x0) {
      return x0._loopCount--;
   }

   static final int access$520(TextFlowCell$AnimationProperties x0, int x1) {
      return x0._animationOffset -= x1;
   }

   static final int access$512(TextFlowCell$AnimationProperties x0, int x1) {
      return x0._animationOffset += x1;
   }
}
