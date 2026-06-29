package net.rim.device.apps.internal.blackberryemail.email.filters;

public final class EmailFilter {
   public static final EmailFilterCollectionImpl getCollection(String userId) {
      return EmailFilterCollectionImpl.getInstance(userId);
   }

   public static final long getCollectionId() {
      return -7388907038055180696L;
   }

   public static final EmailFilterModelImplClone clone(EmailFilterModelImpl model) {
      return new EmailFilterModelImplClone(model);
   }
}
