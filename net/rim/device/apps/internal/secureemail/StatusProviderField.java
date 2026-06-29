package net.rim.device.apps.internal.secureemail;

public interface StatusProviderField {
   StatusField[] getStatusFields();

   void showShortForm(boolean var1);

   void setSecureEmailMessageManager(SecureEmailMessageManager var1);
}
