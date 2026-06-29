package net.rim.device.apps.internal.browser.stack;

import net.rim.device.cldc.io.utility.SessionStats;

interface NetworkPageFetcher extends PageFetcher {
   void reinitialize();

   SessionStats getSessionStats();
}
