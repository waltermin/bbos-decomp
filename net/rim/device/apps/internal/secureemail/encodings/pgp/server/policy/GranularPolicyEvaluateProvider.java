package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

interface GranularPolicyEvaluateProvider {
   boolean evaluate(String var1, ServiceRecord var2, EmailMessageModel var3);
}
