package net.rim.device.api.ldap;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.util.StringUtilities;

class RIMLDAPEntry implements LDAPEntry {
   private Hashtable _hashtable = new Hashtable();

   public RIMLDAPEntry() {
   }

   public void addAttribute(LDAPAttribute newAttribute) {
      String name = StringUtilities.toLowerCase(newAttribute.getName(), 1701707776);
      if (this._hashtable.get(name) != null) {
         throw new IllegalArgumentException();
      }

      this._hashtable.put(name, newAttribute);
   }

   @Override
   public LDAPAttribute getAttribute(String name) throws LDAPNoSuchAttributeException {
      LDAPAttribute attribute = (LDAPAttribute)this._hashtable.get(StringUtilities.toLowerCase(name, 1701707776));
      if (attribute == null) {
         throw new LDAPNoSuchAttributeException();
      } else {
         return attribute;
      }
   }

   @Override
   public Enumeration getAttributes() {
      return this._hashtable.elements();
   }

   @Override
   public int getSize() {
      return this._hashtable.size();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (obj instanceof LDAPEntry) {
         LDAPEntry entry = (LDAPEntry)obj;

         try {
            if (this.getSize() != entry.getSize()) {
               return false;
            }

            Enumeration enumeration = this.getAttributes();

            while (enumeration.hasMoreElements()) {
               LDAPAttribute attribute1 = (LDAPAttribute)enumeration.nextElement();
               LDAPAttribute attribute2 = entry.getAttribute(attribute1.getName());
               if (!attribute1.equals(attribute2)) {
                  return false;
               }
            }

            return true;
         } catch (LDAPNoSuchAttributeException var6) {
         }
      }

      return false;
   }
}
