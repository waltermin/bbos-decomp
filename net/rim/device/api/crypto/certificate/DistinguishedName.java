package net.rim.device.api.crypto.certificate;

import java.util.Enumeration;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.util.Persistable;

public interface DistinguishedName extends Persistable {
   String getString(OID var1);

   String getCommonName();

   String getEmailAddress();

   String getSurname();

   String getCountry();

   String getLocality();

   String getStateOrProvince();

   String getOrganization();

   String getOrganizationalUnit();

   @Override
   String toString();

   Enumeration getOIDs();

   byte[] getEncoding();
}
