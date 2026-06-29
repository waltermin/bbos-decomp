package net.rim.wica.common.debug.protocol.messages.response;

public interface IHandshakeResponseMessage extends IResponseMessage {
   int FIELD_HANDSHAKE_PATTERN;

   void setHandshakePattern(int var1);
}
