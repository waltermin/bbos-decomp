package net.rim.device.api.smartcard;

import net.rim.device.api.util.Persistable;

public final class SmartCardID implements Persistable {
   private long _id;
   private String _label;
   private SmartCard _smartCard;

   public SmartCardID(long id, String label, SmartCard smartCard) {
      if (smartCard == null) {
         throw new Object();
      }

      this._id = id;
      this._label = label != null ? label : smartCard.getLabel();
      this._smartCard = smartCard;
   }

   public final long getID() {
      return this._id;
   }

   public final String getLabel() {
      return this._label;
   }

   public final SmartCard getSmartCard() {
      return this._smartCard;
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof SmartCardID) {
         SmartCardID other = (SmartCardID)obj;
         if (other._id == this._id && other._label.equals(this._label) && other._smartCard.getClass().getName().equals(this._smartCard.getClass().getName())) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final int hashCode() {
      return (int)this._id;
   }
}
