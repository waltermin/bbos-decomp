package net.rim.device.apps.api.framework.model;

public interface UIDGeneratorCallback {
   int NULL_BB_UID;

   int generateUID(RIMModel var1);

   boolean canGenerateNextUID();
}
