package net.rim.wica.common.debug.session;

import net.rim.wica.common.debug.io.IInputByteStreamAdapter;
import net.rim.wica.common.debug.io.IOutputByteStreamAdapter;
import net.rim.wica.common.debug.msgmgt.MessageReceiver;
import net.rim.wica.common.debug.msgmgt.MessageSender;
import net.rim.wica.common.debug.protocol.messages.IMessageEnvelope;
import net.rim.wica.common.debug.protocol.messages.MessageFactory;
import net.rim.wica.common.debug.protocol.messages.response.IResponseMessage;
import net.rim.wica.common.debug.protocol.messages.targetevents.ITargetEventMessage;
import net.rim.wica.common.debug.util.ByteStreamUtil;

public final class TargetSessionManager extends AbstractSessionManager implements ThreadListener {
   private MessageSender _msgSender;
   private MessageSendThread _msgSenderThread;
   private MessageReceiver _msgReceiver;
   private MessageReceiveThread _msgReceiverThread;
   private RequestMessageDispatcher _messageDispatcher;
   private RequestMessageDispatcherThread _messageHandlerThread;
   private IRequestMessageHandler _messageHandler;

   public final void init(IInputByteStreamAdapter inStream, IOutputByteStreamAdapter outStream) {
      if (super._initHasBeenCalled) {
         throw new Object("init() should only be called once; to reuse, call reinit().");
      }

      super._inStream = inStream;
      super._outStream = outStream;
      this.initStateMachine();
      this.initIOThreads();
      super._initHasBeenCalled = true;
   }

   private final void initStateMachine() {
      super._fsm = new ProtocolStateMachine();
      super._fsm.init();

      try {
         super._fsm.eventConnected();
      } catch (ProtocolStateMachine$InvalidStateException e) {
         throw new Object("FSM should be in valid state for connect.");
      }
   }

   private final void initIOThreads() {
      this._msgSender = new MessageSender(super._outStream);
      this._msgSenderThread = new MessageSendThread(this._msgSender);
      this._msgReceiver = new MessageReceiver(super._inStream);
      this._msgReceiverThread = new MessageReceiveThread(this._msgReceiver);
      this._messageDispatcher = new RequestMessageDispatcher(this._msgReceiver, new TargetSessionManager$RequestMessageHandler(this, null));
      this._messageHandlerThread = new RequestMessageDispatcherThread(this._messageDispatcher);
   }

   public final boolean start(int timeoutMs) {
      this.checkInitHasBeenCalled();
      this._msgSenderThread.start();
      this._msgSenderThread.setListener(this);
      this._msgReceiverThread.start();
      this._msgReceiverThread.setListener(this);
      this._messageHandlerThread.start();
      this._messageHandlerThread.setListener(this);
      return super._fsm.waitForState(2, timeoutMs);
   }

   public final void stop() {
      this.checkInitHasBeenCalled();
      this._msgSenderThread.stopLoopWithInterrupt();
      this._msgReceiverThread.stopLoopWithInterrupt();
      this._messageHandlerThread.stopLoopWithInterrupt();
   }

   @Override
   public final void notifyError(Thread t) {
      super._fsm.eventError();
   }

   public final boolean waitForNewSession(int timeoutMs) {
      return super._fsm.waitForState(3, timeoutMs);
   }

   public final void waitForDetach(int timeoutMs) {
      this.checkInitHasBeenCalled();
      super._fsm.waitForState(4, timeoutMs);
   }

   public final void sendTargetEventMessage(int messageType, ITargetEventMessage messageBody) {
      byte[] serializedBody = ByteStreamUtil.serialize(messageBody);
      if (serializedBody == null) {
         throw new Object("messageBody");
      }

      IMessageEnvelope message = MessageFactory.createTargetEventMessage(messageType, serializedBody);
      message.setSessionId(-1);
      this._msgSender.send(message);
   }

   public final void registerRequestMessageHandler(IRequestMessageHandler messageHandler) {
      this._messageHandler = messageHandler;
   }

   public final void sendResponseMessage(int messageType, int messageId, IResponseMessage messageBody) {
      byte[] serialMessage = ByteStreamUtil.serialize(messageBody);
      if (serialMessage == null) {
         throw new Object("messageBody");
      }

      IMessageEnvelope message = MessageFactory.createResponseMessage(messageType, serialMessage);
      message.setSessionId(-1);
      message.setMessageId(messageId);
      this._msgSender.send(message);
   }
}
