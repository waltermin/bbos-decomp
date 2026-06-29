package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.Graphics;

interface SSOptionProfile {
   void paint(Graphics var1, int var2, int var3, int var4);

   void enable();

   void disable();

   boolean isEnabled();
}
