package net.rim.device.internal.diagnostics;

public interface StateTrackerListener {
   void itemChanged(int var1, long var2, long var4);

   void listReset();
}
