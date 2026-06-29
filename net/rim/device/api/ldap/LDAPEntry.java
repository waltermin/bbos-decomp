package net.rim.device.api.ldap;

import java.util.Enumeration;

public interface LDAPEntry {
   LDAPAttribute getAttribute(String var1);

   Enumeration getAttributes();

   int getSize();
}
