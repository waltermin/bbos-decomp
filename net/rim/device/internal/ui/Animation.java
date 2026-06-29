package net.rim.device.internal.ui;

public interface Animation {
   long getExecutionTime();

   boolean animate();

   void addAnimationListener(AnimationListener var1);

   void removeAnimationListener(AnimationListener var1);
}
