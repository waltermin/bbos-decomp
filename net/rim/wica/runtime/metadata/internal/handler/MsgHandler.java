package net.rim.wica.runtime.metadata.internal.handler;

import net.rim.wica.common.ReservedWicletConstants;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.lifecycle.LifecycleService;
import net.rim.wica.runtime.messaging.InboundQueueConnection;
import net.rim.wica.runtime.messaging.InboundQueueListener;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.MessagingService;
import net.rim.wica.runtime.messaging.OutboundQueueConnection;
import net.rim.wica.runtime.messaging.ReadableDataStream;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.WicletContext;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.metadata.component.Msg;
import net.rim.wica.runtime.metadata.component.REError;
import net.rim.wica.runtime.metadata.component.REErrorDetails;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.metadata.internal.component.MsgImpl;
import net.rim.wica.runtime.metadata.internal.transaction.TransactionManager;
import net.rim.wica.runtime.metadata.internal.util.DataDecoder;
import net.rim.wica.runtime.metadata.internal.util.DataEncoder;
import net.rim.wica.runtime.metadata.internal.util.MsgMapper;
import net.rim.wica.runtime.metadata.internal.util.PersistenceListener;
import net.rim.wica.runtime.script.ScriptEngine;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;

public class MsgHandler implements Serviceable, InboundQueueListener {
   private WicletRuntime _runtime;
   private MessagingService _messaging;
   private ScriptEngine _scriptEngine;
   private TransactionManager _transactions;
   private LifecycleService _lifecycle;
   private EventService _eventService;
   private PersistenceListener _persistenceListener;
   private DataEncoder _encoder;
   private DataDecoder _decoder;
   private MsgMapper _msgMapper;
   private OutboundQueueConnection _outboundQueue;
   private InboundQueueConnection _queue;
   private boolean _active;
   private boolean _scheduled;
   static Class class$net$rim$wica$runtime$metadata$WicletRuntime;
   static Class class$net$rim$wica$runtime$messaging$MessagingService;
   static Class class$net$rim$wica$runtime$script$ScriptEngine;
   static Class class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager;
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$lifecycle$LifecycleService;
   static Class class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener;

   public void processMsg() {
      this._scheduled = false;
      if (this._queue != null) {
         Message msg = this._queue.getNextMessage();
         if (msg != null) {
            if (this._active) {
               this.processMsgActive(msg);
            } else {
               this.processMsgDeactive(msg);
            }

            if (!this._queue.isEmpty()) {
               this.schedule();
            }
         }
      }
   }

   public void activate() {
      if (this._queue == null) {
         this._queue = this._messaging.getInboundQueueConnection(this._runtime.getWiclet().getId());
         if (this._queue == null) {
            return;
         }

         this._queue.open(this);
      } else if (!this._queue.isOpen()) {
         this._queue.open(this);
      }

      this._active = true;
   }

   public void deactivate() {
      WicletContext context = ((WicletEx)this._runtime.getWiclet()).getContext();
      if (this._queue != null && !context.getProcessMsgsInBackground()) {
         this._active = false;
         this._queue.deactivate();
      }
   }

   public void shutdown() {
      if (this._queue != null) {
         this._queue.close();
         this._queue = null;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void postMsg(Msg msgData, String destination) {
      if (this._outboundQueue == null) {
         this._outboundQueue = this._messaging.getOutboundQueueConnection(this._runtime.getWiclet().getId());
      }

      try {
         switch (this._outboundQueue.getQueueStatus()) {
            case -1:
               break;
            case 1:
               this._eventService.dispatchEvent(this, 605, 102);
            case 0:
               int numFields = msgData.getDef().getNumFields();
               if (numFields > 0) {
                  this._msgMapper.applyOutMapping(msgData);
               }

               Message msg = this._messaging.createMessageInstance();
               msg.setWicletID(this._runtime.getWiclet().getId());
               msg.setMessageCode(msgData.getMsgCode());
               msg.setMessageName(msgData.getMsgName());
               this.setMsgDestination(msg, destination);
               if (msgData.isSecure()) {
                  msg.setSecurityMode(1);
               }

               if (numFields > 0) {
                  this._encoder.encode(msgData, msg.openWritableDataStream());
               }

               Message response = this._messaging.sendMessage(msg);
               this._eventService.dispatchEvent(900, msgData);
               if (response != null) {
                  this.processMsgActive(response);
                  return;
               }
               break;
            case 2:
            default:
               this._eventService.dispatchEvent(this, 605, 101);
         }
      } catch (Throwable var7) {
         this._eventService.dispatchEvent(this, 605, 105, e.getMessage());
         return;
      }
   }

   @Override
   public void noteMessageAvailable() {
      this.schedule();
   }

   @Override
   public void setServices(ServiceProvider provider) {
      this._runtime = (WicletRuntime)provider.getService(
         class$net$rim$wica$runtime$metadata$WicletRuntime == null
            ? (class$net$rim$wica$runtime$metadata$WicletRuntime = class$("net.rim.wica.runtime.metadata.WicletRuntime"))
            : class$net$rim$wica$runtime$metadata$WicletRuntime
      );
      this._messaging = (MessagingService)provider.getService(
         class$net$rim$wica$runtime$messaging$MessagingService == null
            ? (class$net$rim$wica$runtime$messaging$MessagingService = class$("net.rim.wica.runtime.messaging.MessagingService"))
            : class$net$rim$wica$runtime$messaging$MessagingService
      );
      this._scriptEngine = (ScriptEngine)provider.getService(
         class$net$rim$wica$runtime$script$ScriptEngine == null
            ? (class$net$rim$wica$runtime$script$ScriptEngine = class$("net.rim.wica.runtime.script.ScriptEngine"))
            : class$net$rim$wica$runtime$script$ScriptEngine
      );
      this._transactions = (TransactionManager)provider.getService(
         class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager == null
            ? (
               class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager = class$(
                  "net.rim.wica.runtime.metadata.internal.transaction.TransactionManager"
               )
            )
            : class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager
      );
      this._eventService = (EventService)provider.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
      this._lifecycle = (LifecycleService)provider.getService(
         class$net$rim$wica$runtime$lifecycle$LifecycleService == null
            ? (class$net$rim$wica$runtime$lifecycle$LifecycleService = class$("net.rim.wica.runtime.lifecycle.LifecycleService"))
            : class$net$rim$wica$runtime$lifecycle$LifecycleService
      );
      this._persistenceListener = (PersistenceListener)provider.getService(
         class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener == null
            ? (
               class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener = class$("net.rim.wica.runtime.metadata.internal.util.PersistenceListener")
            )
            : class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener
      );
      WicletEx wiclet = (WicletEx)this._runtime.getWiclet();
      this._encoder = new DataEncoder(wiclet);
      this._msgMapper = new MsgMapper(wiclet);
      this._decoder = new DataDecoder(wiclet);
      this._active = true;
   }

   private void setMsgDestination(Message msg, String destination) {
      if (destination == null || !ReservedWicletConstants.isReservedWiclet(this._runtime.getWiclet().getContext().getUri())) {
         msg.setDestinationType(0);
      } else if (destination.startsWith("local:")) {
         msg.setDestinationType(2);
         msg.setServiceID(destination);
      } else {
         msg.setDestinationType(1);
         msg.setWicletID(this._lifecycle.getWiclet(destination).getId());
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void processMsgActive(Message msg) {
      int transactionId = -1;
      Wiclet wiclet = this._runtime.getWiclet();
      Msg wicletMsg = null;
      boolean var18 = false /* VF: Semaphore variable */;

      label199: {
         try {
            try {
               var18 = true;
               String e = msg.getMessageName();
               int msgCode = msg.getMessageCode();
               if (e == null && 0 == msgCode) {
                  ReadableDataStream in = msg.openReadableDataStream();
                  int requestMsgCode = in.readInt();
                  int category = in.readInt();
                  int code = in.readInt();
                  String name = in.readString();
                  String data = in.readString();
                  wicletMsg = wiclet.getMsgFromCode(requestMsgCode);
                  String requestMsgName = null;
                  if (wicletMsg != null) {
                     requestMsgName = wicletMsg.getMsgName() != null ? wicletMsg.getMsgName() : "";
                  }

                  REError error = new REError(code, data, new REErrorDetails(requestMsgName, category, name, null));
                  this._eventService.dispatchEvent(this, 605, code, error);
               } else {
                  wicletMsg = e != null ? wiclet.getMsg(wiclet.getDefHandle(e)) : wiclet.getMsgFromCode(msgCode);
                  this._eventService.dispatchEvent(901, wicletMsg);
                  this._eventService.dispatchEvent(this, 600, wicletMsg.getDef().getId());
                  if (this._transactions != null) {
                     transactionId = this._transactions.startTransaction();
                  }

                  if (wicletMsg.getDef().getNumFields() > 0) {
                     this.decodeAndMap(wiclet, wicletMsg, msg);
                  }

                  int script = wicletMsg.getScript();
                  if (script != -1) {
                     this._scriptEngine.call(script, wicletMsg);
                  }

                  this._eventService.dispatchEvent(this, 601, wicletMsg.getDef().getId());
                  if (wicletMsg instanceof MsgImpl) {
                     ((MsgImpl)wicletMsg).clean();
                  }
               }

               if (transactionId != -1) {
                  this._transactions.complete(false, transactionId, true, true);
                  var18 = false;
               } else {
                  var18 = false;
               }
               break label199;
            } catch (Throwable var21) {
               label190: {
                  if (wicletMsg instanceof MsgImpl) {
                     ((MsgImpl)wicletMsg).clean();
                  }

                  if (transactionId != -1) {
                     this._transactions.undo(false, transactionId, true);
                  }

                  this._eventService.dispatchEvent(this, 605, 104, e);
                  var18 = false;
                  break label190;
               }
            }
         } finally {
            if (var18) {
               if (transactionId != -1) {
                  this._transactions.removeTrans(transactionId);
               }
            }
         }

         if (transactionId != -1) {
            this._transactions.removeTrans(transactionId);
            return;
         }

         return;
      }

      if (transactionId != -1) {
         this._transactions.removeTrans(transactionId);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void processMsgDeactive(Message msg) {
      Wiclet wiclet = this._runtime.getWiclet();
      Msg wicletMsg = null;
      int transactionId = -1;
      boolean var10 = false /* VF: Semaphore variable */;
      boolean var13 = false /* VF: Semaphore variable */;

      label168: {
         try {
            try {
               var13 = true;
               var10 = true;
               String e = msg.getMessageName();
               int msgCode = msg.getMessageCode();
               wicletMsg = e != null ? wiclet.getMsg(wiclet.getDefHandle(e)) : wiclet.getMsgFromCode(msgCode);
               this._eventService.dispatchEvent(901, wicletMsg);
               if (this._msgMapper.hasMapping(wicletMsg)) {
                  if (this._transactions != null) {
                     transactionId = this._transactions.startTransaction();
                  }

                  this._persistenceListener.startTransaction();
                  this.decodeAndMap(wiclet, wicletMsg, msg);
                  if (wicletMsg instanceof MsgImpl) {
                     ((MsgImpl)wicletMsg).clean();
                  }

                  if (transactionId != -1) {
                     this._transactions.complete(false, transactionId, true, true);
                  }

                  this._persistenceListener.commitTransaction();
               }

               this._eventService.dispatchEvent(this, 601, wicletMsg.getDef().getId());
               var10 = false;
               var13 = false;
               break label168;
            } finally {
               if (var13) {
                  label162: {
                     if (wicletMsg instanceof MsgImpl) {
                        ((MsgImpl)wicletMsg).clean();
                     }

                     if (transactionId != -1) {
                        this._transactions.undo(false, transactionId, true);
                     }

                     this._persistenceListener.undoTransaction();
                     var10 = false;
                     break label162;
                  }
               }
            }
         } finally {
            if (var10) {
               if (transactionId != -1) {
                  this._transactions.removeTrans(transactionId);
               }
            }
         }

         if (transactionId != -1) {
            this._transactions.removeTrans(transactionId);
            return;
         }

         return;
      }

      if (transactionId != -1) {
         this._transactions.removeTrans(transactionId);
      }
   }

   private void decodeAndMap(Wiclet wiclet, Msg wicletMsg, Message msg) {
      ReadableDataStream in = msg.openReadableDataStream();
      this._decoder.decode(wicletMsg, in);
      this._msgMapper.applyInMapping(wicletMsg);
   }

   private synchronized void schedule() {
      if (!this._scheduled) {
         this._scheduled = true;
         this._runtime.requestProcessMessage();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
