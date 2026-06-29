package net.rim.wica.common.debug.protocol.messages.request;

public interface IHandshakeRequestMessage extends IRequestMessage {
   int FIELD_HANDSHAKE_PATTERN = 1;

   int getHandshakePattern();
}
