package net.rim.device.api.itpolicy;

public interface MDSPolicy {
   int MDS_POLICY = 44;
   int DISABLE_MDS_RE = 1;
   int SECURITY_MIN_VERSION = 2;
   int SECURITY_VERIFY_CERTIFICATE = 3;
   int DISABLE_PUBLIC_MDSS = 4;
   int DISABLE_USER_INITIATED_ACTIVATION = 5;
   boolean DISABLE_MDS_RE_DEFAULT = false;
   int SECURITY_MIN_VERSION_DEFAULT = 1;
   boolean SECURITY_VERIFY_CERTIFICATE_DEFAULT = false;
   boolean DISABLE_PUBLIC_MDSS_DEFAULT = false;
   boolean DISABLE_USER_INITIATED_ACTIVATION_DEFAULT = false;
}
