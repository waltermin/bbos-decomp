package net.rim.device.apps.internal.mms.api;

import java.util.Enumeration;

public interface AttachmentDataProvider {
   boolean hasAttachments();

   Enumeration attachmentNames();

   boolean hasAttachment(String var1);

   MMSAttachment getAttachment(String var1);

   int getAttachmentType(String var1);

   int getTotalAttachmentDataSize();
}
