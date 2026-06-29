package net.rim.device.apps.internal.browser.stack;

import javax.microedition.io.InputConnection;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;

interface PageFetcher {
   InputConnection fetchPage(FetchRequest var1, BrowserConfigRecord var2);
}
