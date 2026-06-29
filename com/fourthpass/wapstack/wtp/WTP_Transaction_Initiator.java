package com.fourthpass.wapstack.wtp;

import java.io.InputStream;

public class WTP_Transaction_Initiator extends WTP_Transaction {
   public WTP_Transaction_Initiator(WTPLayer wtpLayer, int TID, boolean TIDnew, byte cID) {
      super(wtpLayer, TID, TIDnew, cID);
   }

   public boolean sendInvokeRequest(byte[] _1) {
      throw null;
   }

   public InputStream getResultIndication(int _1) {
      throw null;
   }
}
