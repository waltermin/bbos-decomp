package net.rim.device.apps.internal.mms.api;

public interface MMSPresentationModel extends MMSAttachment {
   boolean canAddPresentationElement(int var1, long var2);

   void addPresentationElement(String var1, int var2, boolean var3);

   void addPresentationElement(String var1, int var2, boolean var3, boolean var4);

   void addPresentationElement(MMSAttachment var1, boolean var2);

   void addPresentationElement(MMSAttachment var1, boolean var2, boolean var3);

   void addSlideBreak(int var1, boolean var2);

   void copyTo(MMSPresentationModel var1);
}
