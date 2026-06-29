package net.rim.device.api.ui.component;

import java.util.Vector;
import net.rim.device.api.ui.MenuItem;

public interface ActiveFieldCookie {
   MenuItem getFocusVerbs(CookieProvider var1, Object var2, Vector var3);

   boolean invokeApplicationKeyVerb();
}
