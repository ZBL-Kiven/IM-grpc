package com.zj.protocol.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.19.0)",
    comments = "Source: msg_api.proto")
public final class MsgApiGrpc {

  private MsgApiGrpc() {}

  public static final String SERVICE_NAME = "app.MsgApi";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ListenTopicReq, ListenTopicReply> getListenTopicDataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListenTopicData",
      requestType = ListenTopicReq.class,
      responseType = ListenTopicReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<ListenTopicReq, ListenTopicReply> getListenTopicDataMethod() {
    io.grpc.MethodDescriptor<ListenTopicReq, ListenTopicReply> getListenTopicDataMethod;
    if ((getListenTopicDataMethod = MsgApiGrpc.getListenTopicDataMethod) == null) {
      synchronized (MsgApiGrpc.class) {
        if ((getListenTopicDataMethod = MsgApiGrpc.getListenTopicDataMethod) == null) {
          MsgApiGrpc.getListenTopicDataMethod = getListenTopicDataMethod = 
              io.grpc.MethodDescriptor.<ListenTopicReq, ListenTopicReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "app.MsgApi", "ListenTopicData"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  ListenTopicReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  ListenTopicReply.getDefaultInstance()))
                  .build();
          }
        }
     }
     return getListenTopicDataMethod;
  }

  private static volatile io.grpc.MethodDescriptor<GetImMessageReq, ImMessage> getGetImMessageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetImMessage",
      requestType = GetImMessageReq.class,
      responseType = ImMessage.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<GetImMessageReq, ImMessage> getGetImMessageMethod() {
    io.grpc.MethodDescriptor<GetImMessageReq, ImMessage> getGetImMessageMethod;
    if ((getGetImMessageMethod = MsgApiGrpc.getGetImMessageMethod) == null) {
      synchronized (MsgApiGrpc.class) {
        if ((getGetImMessageMethod = MsgApiGrpc.getGetImMessageMethod) == null) {
          MsgApiGrpc.getGetImMessageMethod = getGetImMessageMethod = 
              io.grpc.MethodDescriptor.<GetImMessageReq, ImMessage>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "app.MsgApi", "GetImMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  GetImMessageReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  ImMessage.getDefaultInstance()))
                  .build();
          }
        }
     }
     return getGetImMessageMethod;
  }

  private static volatile io.grpc.MethodDescriptor<LeaveImGroupReq, LeaveImGroupReply> getLeaveImGroupMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "LeaveImGroup",
      requestType = LeaveImGroupReq.class,
      responseType = LeaveImGroupReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<LeaveImGroupReq, LeaveImGroupReply> getLeaveImGroupMethod() {
    io.grpc.MethodDescriptor<LeaveImGroupReq, LeaveImGroupReply> getLeaveImGroupMethod;
    if ((getLeaveImGroupMethod = MsgApiGrpc.getLeaveImGroupMethod) == null) {
      synchronized (MsgApiGrpc.class) {
        if ((getLeaveImGroupMethod = MsgApiGrpc.getLeaveImGroupMethod) == null) {
          MsgApiGrpc.getLeaveImGroupMethod = getLeaveImGroupMethod = 
              io.grpc.MethodDescriptor.<LeaveImGroupReq, LeaveImGroupReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "app.MsgApi", "LeaveImGroup"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  LeaveImGroupReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  LeaveImGroupReply.getDefaultInstance()))
                  .build();
          }
        }
     }
     return getLeaveImGroupMethod;
  }

  private static volatile io.grpc.MethodDescriptor<GetImHistoryMsgReq, BatchMsg> getGetGroupHistoryMessageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetGroupHistoryMessage",
      requestType = GetImHistoryMsgReq.class,
      responseType = BatchMsg.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<GetImHistoryMsgReq, BatchMsg> getGetGroupHistoryMessageMethod() {
    io.grpc.MethodDescriptor<GetImHistoryMsgReq, BatchMsg> getGetGroupHistoryMessageMethod;
    if ((getGetGroupHistoryMessageMethod = MsgApiGrpc.getGetGroupHistoryMessageMethod) == null) {
      synchronized (MsgApiGrpc.class) {
        if ((getGetGroupHistoryMessageMethod = MsgApiGrpc.getGetGroupHistoryMessageMethod) == null) {
          MsgApiGrpc.getGetGroupHistoryMessageMethod = getGetGroupHistoryMessageMethod = 
              io.grpc.MethodDescriptor.<GetImHistoryMsgReq, BatchMsg>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "app.MsgApi", "GetGroupHistoryMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  GetImHistoryMsgReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  BatchMsg.getDefaultInstance()))
                  .build();
          }
        }
     }
     return getGetGroupHistoryMessageMethod;
  }

  private static volatile io.grpc.MethodDescriptor<GetImHistoryMsgReq, BatchMsg> getGetChatHistoryMessageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetChatHistoryMessage",
      requestType = GetImHistoryMsgReq.class,
      responseType = BatchMsg.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<GetImHistoryMsgReq, BatchMsg> getGetChatHistoryMessageMethod() {
    io.grpc.MethodDescriptor<GetImHistoryMsgReq, BatchMsg> getGetChatHistoryMessageMethod;
    if ((getGetChatHistoryMessageMethod = MsgApiGrpc.getGetChatHistoryMessageMethod) == null) {
      synchronized (MsgApiGrpc.class) {
        if ((getGetChatHistoryMessageMethod = MsgApiGrpc.getGetChatHistoryMessageMethod) == null) {
          MsgApiGrpc.getGetChatHistoryMessageMethod = getGetChatHistoryMessageMethod = 
              io.grpc.MethodDescriptor.<GetImHistoryMsgReq, BatchMsg>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "app.MsgApi", "GetChatHistoryMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  GetImHistoryMsgReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  BatchMsg.getDefaultInstance()))
                  .build();
          }
        }
     }
     return getGetChatHistoryMessageMethod;
  }

  private static volatile io.grpc.MethodDescriptor<PingReq, Pong> getPingMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Ping",
      requestType = PingReq.class,
      responseType = Pong.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<PingReq, Pong> getPingMethod() {
    io.grpc.MethodDescriptor<PingReq, Pong> getPingMethod;
    if ((getPingMethod = MsgApiGrpc.getPingMethod) == null) {
      synchronized (MsgApiGrpc.class) {
        if ((getPingMethod = MsgApiGrpc.getPingMethod) == null) {
          MsgApiGrpc.getPingMethod = getPingMethod = 
              io.grpc.MethodDescriptor.<PingReq, Pong>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "app.MsgApi", "Ping"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  PingReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  Pong.getDefaultInstance()))
                  .build();
          }
        }
     }
     return getPingMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MsgApiStub newStub(io.grpc.Channel channel) {
    return new MsgApiStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MsgApiBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new MsgApiBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MsgApiFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new MsgApiFutureStub(channel);
  }

  /**
   */
  public static abstract class MsgApiImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     *服务器向客户端推送当前实时变更的信息 比如+1的这种信息
     * </pre>
     */
    public io.grpc.stub.StreamObserver<ListenTopicReq> listenTopicData(
        io.grpc.stub.StreamObserver<ListenTopicReply> responseObserver) {
      return asyncUnimplementedStreamingCall(getListenTopicDataMethod(), responseObserver);
    }

    /**
     * <pre>
     *收取group实时消息
     * </pre>
     */
    public void getImMessage(GetImMessageReq request,
                             io.grpc.stub.StreamObserver<ImMessage> responseObserver) {
      asyncUnimplementedUnaryCall(getGetImMessageMethod(), responseObserver);
    }

    /**
     * <pre>
     *不再收听房间（group）的消息0
     * </pre>
     */
    public void leaveImGroup(LeaveImGroupReq request,
                             io.grpc.stub.StreamObserver<LeaveImGroupReply> responseObserver) {
      asyncUnimplementedUnaryCall(getLeaveImGroupMethod(), responseObserver);
    }

    /**
     * <pre>
     *获取群的历史消息
     * </pre>
     */
    public void getGroupHistoryMessage(GetImHistoryMsgReq request,
                                       io.grpc.stub.StreamObserver<BatchMsg> responseObserver) {
      asyncUnimplementedUnaryCall(getGetGroupHistoryMessageMethod(), responseObserver);
    }

    /**
     * <pre>
     *获取chat的历史消息
     * </pre>
     */
    public void getChatHistoryMessage(GetImHistoryMsgReq request,
                                      io.grpc.stub.StreamObserver<BatchMsg> responseObserver) {
      asyncUnimplementedUnaryCall(getGetChatHistoryMessageMethod(), responseObserver);
    }

    /**
     */
    public void ping(PingReq request,
                     io.grpc.stub.StreamObserver<Pong> responseObserver) {
      asyncUnimplementedUnaryCall(getPingMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getListenTopicDataMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<ListenTopicReq, ListenTopicReply>(
                  this, METHODID_LISTEN_TOPIC_DATA)))
          .addMethod(
            getGetImMessageMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<GetImMessageReq, ImMessage>(
                  this, METHODID_GET_IM_MESSAGE)))
          .addMethod(
            getLeaveImGroupMethod(),
            asyncUnaryCall(
              new MethodHandlers<LeaveImGroupReq, LeaveImGroupReply>(
                  this, METHODID_LEAVE_IM_GROUP)))
          .addMethod(
            getGetGroupHistoryMessageMethod(),
            asyncUnaryCall(
              new MethodHandlers<GetImHistoryMsgReq, BatchMsg>(
                  this, METHODID_GET_GROUP_HISTORY_MESSAGE)))
          .addMethod(
            getGetChatHistoryMessageMethod(),
            asyncUnaryCall(
              new MethodHandlers<GetImHistoryMsgReq, BatchMsg>(
                  this, METHODID_GET_CHAT_HISTORY_MESSAGE)))
          .addMethod(
            getPingMethod(),
            asyncUnaryCall(
              new MethodHandlers<PingReq, Pong>(
                  this, METHODID_PING)))
          .build();
    }
  }

  /**
   */
  public static final class MsgApiStub extends io.grpc.stub.AbstractStub<MsgApiStub> {
    private MsgApiStub(io.grpc.Channel channel) {
      super(channel);
    }

    private MsgApiStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MsgApiStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new MsgApiStub(channel, callOptions);
    }

    /**
     * <pre>
     *服务器向客户端推送当前实时变更的信息 比如+1的这种信息
     * </pre>
     */
    public io.grpc.stub.StreamObserver<ListenTopicReq> listenTopicData(
        io.grpc.stub.StreamObserver<ListenTopicReply> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getListenTopicDataMethod(), getCallOptions()), responseObserver);
    }

    /**
     * <pre>
     *收取group实时消息
     * </pre>
     */
    public void getImMessage(GetImMessageReq request,
                             io.grpc.stub.StreamObserver<ImMessage> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getGetImMessageMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *不再收听房间（group）的消息0
     * </pre>
     */
    public void leaveImGroup(LeaveImGroupReq request,
                             io.grpc.stub.StreamObserver<LeaveImGroupReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getLeaveImGroupMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *获取群的历史消息
     * </pre>
     */
    public void getGroupHistoryMessage(GetImHistoryMsgReq request,
                                       io.grpc.stub.StreamObserver<BatchMsg> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetGroupHistoryMessageMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *获取chat的历史消息
     * </pre>
     */
    public void getChatHistoryMessage(GetImHistoryMsgReq request,
                                      io.grpc.stub.StreamObserver<BatchMsg> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetChatHistoryMessageMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ping(PingReq request,
                     io.grpc.stub.StreamObserver<Pong> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class MsgApiBlockingStub extends io.grpc.stub.AbstractStub<MsgApiBlockingStub> {
    private MsgApiBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private MsgApiBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MsgApiBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new MsgApiBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     *收取group实时消息
     * </pre>
     */
    public java.util.Iterator<ImMessage> getImMessage(
        GetImMessageReq request) {
      return blockingServerStreamingCall(
          getChannel(), getGetImMessageMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *不再收听房间（group）的消息0
     * </pre>
     */
    public LeaveImGroupReply leaveImGroup(LeaveImGroupReq request) {
      return blockingUnaryCall(
          getChannel(), getLeaveImGroupMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *获取群的历史消息
     * </pre>
     */
    public BatchMsg getGroupHistoryMessage(GetImHistoryMsgReq request) {
      return blockingUnaryCall(
          getChannel(), getGetGroupHistoryMessageMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *获取chat的历史消息
     * </pre>
     */
    public BatchMsg getChatHistoryMessage(GetImHistoryMsgReq request) {
      return blockingUnaryCall(
          getChannel(), getGetChatHistoryMessageMethod(), getCallOptions(), request);
    }

    /**
     */
    public Pong ping(PingReq request) {
      return blockingUnaryCall(
          getChannel(), getPingMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class MsgApiFutureStub extends io.grpc.stub.AbstractStub<MsgApiFutureStub> {
    private MsgApiFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private MsgApiFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MsgApiFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new MsgApiFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     *不再收听房间（group）的消息0
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<LeaveImGroupReply> leaveImGroup(
        LeaveImGroupReq request) {
      return futureUnaryCall(
          getChannel().newCall(getLeaveImGroupMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *获取群的历史消息
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<BatchMsg> getGroupHistoryMessage(
        GetImHistoryMsgReq request) {
      return futureUnaryCall(
          getChannel().newCall(getGetGroupHistoryMessageMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *获取chat的历史消息
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<BatchMsg> getChatHistoryMessage(
        GetImHistoryMsgReq request) {
      return futureUnaryCall(
          getChannel().newCall(getGetChatHistoryMessageMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Pong> ping(
        PingReq request) {
      return futureUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_IM_MESSAGE = 0;
  private static final int METHODID_LEAVE_IM_GROUP = 1;
  private static final int METHODID_GET_GROUP_HISTORY_MESSAGE = 2;
  private static final int METHODID_GET_CHAT_HISTORY_MESSAGE = 3;
  private static final int METHODID_PING = 4;
  private static final int METHODID_LISTEN_TOPIC_DATA = 5;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final MsgApiImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(MsgApiImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_IM_MESSAGE:
          serviceImpl.getImMessage((GetImMessageReq) request,
              (io.grpc.stub.StreamObserver<ImMessage>) responseObserver);
          break;
        case METHODID_LEAVE_IM_GROUP:
          serviceImpl.leaveImGroup((LeaveImGroupReq) request,
              (io.grpc.stub.StreamObserver<LeaveImGroupReply>) responseObserver);
          break;
        case METHODID_GET_GROUP_HISTORY_MESSAGE:
          serviceImpl.getGroupHistoryMessage((GetImHistoryMsgReq) request,
              (io.grpc.stub.StreamObserver<BatchMsg>) responseObserver);
          break;
        case METHODID_GET_CHAT_HISTORY_MESSAGE:
          serviceImpl.getChatHistoryMessage((GetImHistoryMsgReq) request,
              (io.grpc.stub.StreamObserver<BatchMsg>) responseObserver);
          break;
        case METHODID_PING:
          serviceImpl.ping((PingReq) request,
              (io.grpc.stub.StreamObserver<Pong>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_LISTEN_TOPIC_DATA:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.listenTopicData(
              (io.grpc.stub.StreamObserver<ListenTopicReply>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (MsgApiGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .addMethod(getListenTopicDataMethod())
              .addMethod(getGetImMessageMethod())
              .addMethod(getLeaveImGroupMethod())
              .addMethod(getGetGroupHistoryMessageMethod())
              .addMethod(getGetChatHistoryMessageMethod())
              .addMethod(getPingMethod())
              .build();
        }
      }
    }
    return result;
  }
}
