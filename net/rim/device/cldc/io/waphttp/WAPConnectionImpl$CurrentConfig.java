package net.rim.device.cldc.io.waphttp;

import com.fourthpass.wapstack.bearer.RIM_UDP_Bearer;
import com.fourthpass.wapstack.wsp.WSPAddress;
import com.fourthpass.wapstack.wsp.WSPClientSession;
import com.fourthpass.wapstack.wsp.WSPConnectionless;
import com.fourthpass.wapstack.wsp.WSPContextObject;
import com.fourthpass.wapstack.wsp.WSPMethod;
import com.fourthpass.wapstack.wsp.pdu.WSP_ReplyPDU;
import com.fourthpass.wapstack.wtls.RIMWTLSLayer;
import com.fourthpass.wapstack.wtp.WTPLayer;

class WAPConnectionImpl$CurrentConfig {
   private boolean _suspendSession;
   private WTPLayer _wtpLayer;
   private RIMWTLSLayer _wtlsLayer;
   private RIM_UDP_Bearer _bearer;
   private WSPClientSession _wspSession;
   private WSPAddress _redirectAddress;
   private boolean _reuseSecurity;
   private WSP_ReplyPDU _connectAbortReply;
   private WSPConnectionless _connectionless;
   private WSPMethod _method;
   private WAPSession _currentWapSession;
   private WSPContextObject _wspContext;
   private int _currentTimer = -1;
   private int _currentPid = -1;
}
