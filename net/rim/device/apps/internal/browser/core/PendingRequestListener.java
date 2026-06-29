package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.util.Persistable;
import net.rim.device.apps.internal.browser.page.PageModel;

public interface PendingRequestListener extends Persistable {
   void notify(PageModel var1);
}
