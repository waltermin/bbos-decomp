package net.rim.device.internal.provisioning;

public interface ActivationStatusListener {
   String getCollectionName();

   void activationComplete(boolean var1);
}
