package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;

public interface EmailPayloadModel extends PersistableRIMModel, ReadableList, WritableSet {
   void setRecipientType(byte var1);

   byte getRecipientType();

   void setCMIMEReferenceIdentifier(int var1);

   int getCMIMEReferenceIdentifier();

   void setInbound(boolean var1);

   boolean inbound();

   void setCopyInsteadOfReference(boolean var1);

   boolean isCopyInsteadOfReference();

   long getCreationDate();

   void setCreationDate(long var1);

   void prepareForPersisting();

   byte getEncoding();

   void setEncoding(byte var1);

   void setIsNNE(boolean var1);

   boolean isNNE();
}
