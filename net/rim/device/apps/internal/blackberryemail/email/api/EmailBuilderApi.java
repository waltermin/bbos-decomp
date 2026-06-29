package net.rim.device.apps.internal.blackberryemail.email.api;

import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.header.HeaderTypes;

public final class EmailBuilderApi {
   private static ContextObjectWR _contextObjectWR = (ContextObjectWR)(new Object());

   private EmailBuilderApi() {
   }

   private static final void addObject(EmailMessageModel message, long objectId, ContextObject context) {
      message.add(FactoryUtil.createInstance(objectId, context));
      context.reset();
   }

   public static final synchronized void addSubjectLine(EmailMessageModel message, String subject) {
      ContextObject contextObject = _contextObjectWR.getContextObject();
      contextObject.put(253, subject);
      addObject(message, 3928489455534245796L, contextObject);
   }

   public static final synchronized void addOriginalMessageReferenceIdentifierStub(EmailMessageModel message, int referenceId) {
      ContextObject contextObject = _contextObjectWR.getContextObject();
      contextObject.putIntegerData(referenceId);
      addObject(message, 7954277133629574293L, contextObject);
   }

   public static final synchronized void addRecipient(EmailMessageModel message, int type, RIMModel recipient) {
      ContextObject contextObject = _contextObjectWR.getContextObject();
      contextObject.put(-4054673099568009991L, HeaderTypes._typesAsInteger[type]);
      contextObject.put(254, recipient);
      addObject(message, -8034039608019345282L, contextObject);
   }

   public static final synchronized void addMessageBody(EmailMessageModel message, String text) {
      ContextObject contextObject = _contextObjectWR.getContextObject();
      contextObject.put(253, text);
      addObject(message, 5987399499453925075L, contextObject);
   }
}
