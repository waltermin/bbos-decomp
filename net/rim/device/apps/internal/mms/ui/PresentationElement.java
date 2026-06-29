package net.rim.device.apps.internal.mms.ui;

import net.rim.device.apps.internal.mms.api.MMSPresentationModel;

interface PresentationElement {
   void copyTo(MMSPresentationModel var1);

   boolean canMove();

   void move(boolean var1);
}
