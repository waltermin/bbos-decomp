package net.rim.device.cldc.io.mdp;

final class MFHRetransmissionConfiguration {
   long _lostPacketTimeout;
   long _maxPacketTimeout;
   long _maxRelayPacketRTT;
   int _maxAggressivePacketRetry;
   int _maxImmediatePacketRetry;
   int _maxBackoffPacketRetry;
}
