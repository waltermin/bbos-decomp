package net.rim.device.apps.internal.docview.gui;

interface ExitScreen {
   boolean canExitScreen();

   void releaseRefs();

   void reInitialize(Object var1);
}
