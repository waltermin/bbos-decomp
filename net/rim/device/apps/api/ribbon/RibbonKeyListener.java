package net.rim.device.apps.api.ribbon;

public interface RibbonKeyListener extends RibbonListener {
   boolean keyChar(char var1, int var2, int var3);

   boolean keyDown(int var1, int var2);

   boolean keyUp(int var1, int var2);

   boolean keyRepeat(int var1, int var2);
}
