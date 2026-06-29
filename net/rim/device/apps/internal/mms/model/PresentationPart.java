package net.rim.device.apps.internal.mms.model;

import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;

interface PresentationPart {
   void copyTo(MMSPresentationModel var1);

   void writeData(SyncBuffer var1);

   int getTaggedFieldSize();
}
