package net.rim.device.apps.internal.phone;

interface SSQueryListener {
   void queryStarted();

   void queryFinished(boolean var1, boolean var2);

   void queryTimedOut();
}
