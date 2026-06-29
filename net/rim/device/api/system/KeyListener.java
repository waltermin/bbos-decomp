package net.rim.device.api.system;

public interface KeyListener extends KeypadListener {
   boolean keyChar(char var1, int var2, int var3);

   boolean keyDown(int var1, int var2);

   boolean keyUp(int var1, int var2);

   boolean keyRepeat(int var1, int var2);

   boolean keyStatus(int var1, int var2);
}
