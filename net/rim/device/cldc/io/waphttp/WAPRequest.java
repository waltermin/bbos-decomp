package net.rim.device.cldc.io.waphttp;

import javax.microedition.io.HttpConnection;
import javax.microedition.io.SecurityInfo;
import net.rim.device.cldc.io.utility.SessionStats;
import net.rim.device.internal.browser.util.Pipe;

public interface WAPRequest extends HttpConnection {
   long WAP_CONNECTION_CLOSED = 3729075673904713354L;

   WAPConnectionParams getParameters();

   Pipe getPipe();

   SecurityInfo getRIMSecurityInfo();

   SessionStats getSessionStats();
}
