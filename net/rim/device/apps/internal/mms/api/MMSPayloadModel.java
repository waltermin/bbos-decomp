package net.rim.device.apps.internal.mms.api;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.apps.api.framework.model.RIMModel;

public interface MMSPayloadModel {
   int SUBJECT_MAX_LENGTH = 40;

   long getCreationDate();

   RIMModel getSender();

   Vector getRecipients();

   Vector getCcRecipients();

   Vector getBccRecipients();

   String getAttribute(String var1);

   Enumeration attributeNames();

   MMSPresentationModel getPresentationModel();
}
