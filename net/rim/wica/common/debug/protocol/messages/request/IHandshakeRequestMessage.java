package net.rim.wica.common.debug.protocol.messages.request;

public interface IHandshakeRequestMessage extends IRequestMessage {
   int FIELD_HANDSHAKE_PATTERN;

   int getHandshakePattern();
}
