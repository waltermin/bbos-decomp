package net.rim.device.api.browser.wapprovisioning;

import org.w3c.dom.Document;

public interface WAPProvisioningListener {
   int SOURCE_SIM_CARD = 0;
   int SOURCE_CELL_BROADCAST = 1;
   int SOURCE_WAP_PUSH = 2;
   int SOURCE_MDS_PUSH = 3;

   void processProvisioningDocument(Document var1, int var2, String[] var3, String[] var4);
}
