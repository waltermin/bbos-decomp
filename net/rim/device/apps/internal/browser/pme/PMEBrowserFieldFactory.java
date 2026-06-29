package net.rim.device.apps.internal.browser.pme;

import java.io.InputStream;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.ui.Field;

public interface PMEBrowserFieldFactory {
   long PME_BROWSER_FIELD_FACTORY_ID = -4027123499468556973L;

   Field create(InputConnection var1, InputStream var2, BrowserContentBaseImpl var3, long var4);
}
