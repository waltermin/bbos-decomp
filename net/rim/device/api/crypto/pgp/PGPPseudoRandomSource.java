package net.rim.device.api.crypto.pgp;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.PseudoRandomSource;

public interface PGPPseudoRandomSource extends PseudoRandomSource {
   Digest getDigest();
}
