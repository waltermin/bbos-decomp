package net.rim.device.apps.internal.messaging.search.criteria;

final class ServiceSearchModel$ServiceChoice {
   private String _name;
   private int _userId;
   private int _uidHash;

   ServiceSearchModel$ServiceChoice(String name, int userId, int uidHash) {
      this._name = name;
      this._userId = userId;
      this._uidHash = uidHash;
   }

   @Override
   public final String toString() {
      return this._name;
   }

   final int getUserId() {
      return this._userId;
   }

   final int getUidHash() {
      return this._uidHash;
   }
}
