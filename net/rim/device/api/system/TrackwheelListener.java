package net.rim.device.api.system;

public interface TrackwheelListener extends KeypadListener {
   boolean trackwheelClick(int var1, int var2);

   boolean trackwheelUnclick(int var1, int var2);

   boolean trackwheelRoll(int var1, int var2, int var3);
}
