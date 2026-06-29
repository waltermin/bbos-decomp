package net.rim.device.api.crypto.certificate.pgp;

import java.util.Enumeration;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.util.EmptyEnumeration;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.internal.crypto.pgp.PGPUserIDPacket;

public class PGPDistinguishedName implements DistinguishedName {
   private PGPUserIDPacket _userIDPacket;

   public PGPDistinguishedName(byte[] encoding) {
      this._userIDPacket = new PGPUserIDPacket(13, encoding);
   }

   @Override
   public String getCommonName() {
      return this._userIDPacket.getName();
   }

   @Override
   public String getCountry() {
      return null;
   }

   @Override
   public String getEmailAddress() {
      return this._userIDPacket.getEmailAddress();
   }

   @Override
   public String getLocality() {
      return null;
   }

   @Override
   public Enumeration getOIDs() {
      return new EmptyEnumeration();
   }

   @Override
   public String getOrganization() {
      return null;
   }

   @Override
   public String getOrganizationalUnit() {
      return null;
   }

   @Override
   public String getStateOrProvince() {
      return null;
   }

   @Override
   public String getString(OID oid) {
      return null;
   }

   @Override
   public String getSurname() {
      return this._userIDPacket.getName();
   }

   @Override
   public byte[] getEncoding() {
      return this._userIDPacket.getEncoding();
   }

   @Override
   public String toString() {
      return this._userIDPacket.getUserID();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof PGPDistinguishedName)) {
         return false;
      }

      PGPDistinguishedName pgpDistinguishedName = (PGPDistinguishedName)obj;
      return ObjectUtilities.objEqual(this._userIDPacket, pgpDistinguishedName._userIDPacket);
   }
}
