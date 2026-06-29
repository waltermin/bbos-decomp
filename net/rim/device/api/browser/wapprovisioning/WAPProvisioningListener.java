package net.rim.device.api.browser.wapprovisioning;

import org.w3c.dom.Document;

public interface WAPProvisioningListener {
   int SOURCE_SIM_CARD;
   int SOURCE_CELL_BROADCAST;
   int SOURCE_WAP_PUSH;
   int SOURCE_MDS_PUSH;

   void processProvisioningDocument(Document var1, int var2, String[] var3, String[] var4);
}
