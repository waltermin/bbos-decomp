package net.rim.device.api.ldap;

import java.util.Enumeration;

public interface LDAPAttribute {
   int getSize();

   String getName();

   Object getValue(int var1);

   Enumeration getValues();
}
