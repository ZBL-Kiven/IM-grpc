package com.zj.protocol.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 *
 */
@javax.annotation.Generated(value = "by gRPC proto compiler (version 1.39.0)", comments = "Source: msg_api.proto")
public final class MsgApiGrpc {

    private MsgApiGrpc() {}

    public static final String SERVICE_NAME = "app.MsgApi";

    // Static method descriptors that strictly reflect the proto.
    private static volatile io.grpc.MethodDescriptor<ListenTopicReq, ListenTopicReply> getListenTopicDataMethod;

    @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "ListenTopicData", requestType = ListenTopicReq.class, responseType = ListenTopicReply.class, methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
    public static io.grpc.MethodDescriptor<ListenTopicReq, ListenTopicReply> getListenTopicDataMethod() {
        io.grpc.MethodDescriptor<ListenTopicReq, ListenTopicReply> getListenTopicDataMethod;
        if ((getListenTopicDataMethod = MsgApiGrpc.getListenTopicDataMethod) == null) {
            synchronized (MsgApiGrpc.class) {
                if ((getListenTopicDataMethod = MsgApiGrpc.getListenTopicDataMethod) == null) {
                    MsgApiGrpc.getListenTopicDataMethod = getListenTopicDataMethod = io.grpc.MethodDescriptor.<ListenTopicReq, ListenTopicReply>newBuilder().setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING).setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListenTopicData")).setSampledToLocalTracing(true).setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(ListenTopicReq.getDefaultInstance())).setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(ListenTopicReply.getDefaultInstance())).setSchemaDescriptor(new MsgApiMethodDescriptorSupplier("ListenTopicData")).build();
                }
            }
        }
        return getListenTopicDataMethod;
    }

    private static volatile io.grpc.MethodDescriptor<LeaveImGroupReq, LeaveImGroupReply> getLeaveImGroupMethod;

    @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "LeaveImGroup", requestType = LeaveImGroupReq.class, responseType = LeaveImGroupReply.class, methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
    public static io.grpc.MethodDescriptor<LeaveImGroupReq, LeaveImGroupReply> getLeaveImGroupMethod() {
        io.grpc.MethodDescriptor<LeaveImGroupReq, LeaveImGroupReply> getLeaveImGroupMethod;
        if ((getLeaveImGroupMethod = MsgApiGrpc.getLeaveImGroupMethod) == null) {
            synchronized (MsgApiGrpc.class) {
                if ((getLeaveImGroupMethod = MsgApiGrpc.getLeaveImGroupMethod) == null) {
                    MsgApiGrpc.getLeaveImGroupMethod = getLeaveImGroupMethod = io.grpc.MethodDescriptor.<LeaveImGroupReq, LeaveImGroupReply>newBuilder().setType(io.grpc.MethodDescriptor.MethodType.UNARY).setFullMethodName(generateFullMethodName(SERVICE_NAME, "LeaveImGroup")).setSampledToLocalTracing(true).setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(LeaveImGroupReq.getDefaultInstance())).setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(LeaveImGroupReply.getDefaultInstance())).setSchemaDescriptor(new MsgApiMethodDescriptorSupplier("LeaveImGroup")).build();
                }
            }
        }
        return getLeaveImGroupMethod;
    }

    private static volatile io.grpc.MethodDescriptor<LiveRoomMessageReq, LiveRoomMessageReply> getLiveRoomMessageMethod;

    @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "LiveRoomMessage", requestType = LiveRoomMessageReq.class, responseType = LiveRoomMessageReply.class, methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
    public static io.grpc.MethodDescriptor<LiveRoomMessageReq, LiveRoomMessageReply> getLiveRoomMessageMethod() {
        io.grpc.MethodDescriptor<LiveRoomMessageReq, LiveRoomMessageReply> getLiveRoomMessageMethod;
        if ((getLiveRoomMessageMethod = MsgApiGrpc.getLiveRoomMessageMethod) == null) {
            synchronized (MsgApiGrpc.class) {
                if ((getLiveRoomMessageMethod = MsgApiGrpc.getLiveRoomMessageMethod) == null) {
                    MsgApiGrpc.getLiveRoomMessageMethod = getLiveRoomMessageMethod = io.grpc.MethodDescriptor.<LiveRoomMessageReq, LiveRoomMessageReply>newBuilder().setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING).setFullMethodName(generateFullMethodName(SERVICE_NAME, "LiveRoomMessage")).setSampledToLocalTracing(true).setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(LiveRoomMessageReq.getDefaultInstance())).setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(LiveRoomMessageReply.getDefaultInstance())).setSchemaDescriptor(new MsgApiMethodDescriptorSupplier("LiveRoomMessage")).build();
                }
            }
        }
        return getLiveRoomMessageMethod;
    }

    private static volatile io.grpc.MethodDescriptor<PingReq, Pong> getPingMethod;

    @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "Ping", requestType = PingReq.class, responseType = Pong.class, methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
    public static io.grpc.MethodDescriptor<PingReq, Pong> getPingMethod() {
        io.grpc.MethodDescriptor<PingReq, Pong> getPingMethod;
        if ((getPingMethod = MsgApiGrpc.getPingMethod) == null) {
            synchronized (MsgApiGrpc.class) {
                if ((getPingMethod = MsgApiGrpc.getPingMethod) == null) {
                    MsgApiGrpc.getPingMethod = getPingMethod = io.grpc.MethodDescriptor.<PingReq, Pong>newBuilder().setType(io.grpc.MethodDescriptor.MethodType.UNARY).setFullMethodName(generateFullMethodName(SERVICE_NAME, "Ping")).setSampledToLocalTracing(true).setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(PingReq.getDefaultInstance())).setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(Pong.getDefaultInstance())).setSchemaDescriptor(new MsgApiMethodDescriptorSupplier("Ping")).build();
                }
            }
        }
        return getPingMethod;
    }

    private static volatile io.grpc.MethodDescriptor<ImMessageReq, ImMessageReply> getOnlineImMessageMethod;

    @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "OnlineImMessage", requestType = ImMessageReq.class, responseType = ImMessageReply.class, methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
    public static io.grpc.MethodDescriptor<ImMessageReq, ImMessageReply> getOnlineImMessageMethod() {
        io.grpc.MethodDescriptor<ImMessageReq, ImMessageReply> getOnlineImMessageMethod;
        if ((getOnlineImMessageMethod = MsgApiGrpc.getOnlineImMessageMethod) == null) {
            synchronized (MsgApiGrpc.class) {
                if ((getOnlineImMessageMethod = MsgApiGrpc.getOnlineImMessageMethod) == null) {
                    MsgApiGrpc.getOnlineImMessageMethod = getOnlineImMessageMethod = io.grpc.MethodDescriptor.<ImMessageReq, ImMessageReply>newBuilder().setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING).setFullMethodName(generateFullMethodName(SERVICE_NAME, "OnlineImMessage")).setSampledToLocalTracing(true).setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(ImMessageReq.getDefaultInstance())).setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(ImMessageReply.getDefaultInstance())).setSchemaDescriptor(new MsgApiMethodDescriptorSupplier("OnlineImMessage")).build();
                }
            }
        }
        return getOnlineImMessageMethod;
    }

    /**
     * Creates a new async stub that supports all call types for the service
     */
    public static MsgApiStub newStub(io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<MsgApiStub> factory = new io.grpc.stub.AbstractStub.StubFactory<MsgApiStub>() {
            @java.lang.Override
            public MsgApiStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                return new MsgApiStub(channel, callOptions);
            }
        };
        return MsgApiStub.newStub(factory, channel);
    }

    /**
     * Creates a new blocking-style stub that supports unary and streaming output calls on the service
     */
    public static MsgApiBlockingStub newBlockingStub(io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<MsgApiBlockingStub> factory = new io.grpc.stub.AbstractStub.StubFactory<MsgApiBlockingStub>() {
            @java.lang.Override
            public MsgApiBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                return new MsgApiBlockingStub(channel, callOptions);
            }
        };
        return MsgApiBlockingStub.newStub(factory, channel);
    }

    /**
     * Creates a new ListenableFuture-style stub that supports unary calls on the service
     */
    public static MsgApiFutureStub newFutureStub(io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<MsgApiFutureStub> factory = new io.grpc.stub.AbstractStub.StubFactory<MsgApiFutureStub>() {
            @java.lang.Override
            public MsgApiFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                return new MsgApiFutureStub(channel, callOptions);
            }
        };
        return MsgApiFutureStub.newStub(factory, channel);
    }

    /**
     *
     */
    public static abstract class MsgApiImplBase implements io.grpc.BindableService {

        /**
         *
         */
        public io.grpc.stub.StreamObserver<ListenTopicReq> listenTopicData(io.grpc.stub.StreamObserver<ListenTopicReply> responseObserver) {
            return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getListenTopicDataMethod(), responseObserver);
        }

        /**
         *
         */
        public void leaveImGroup(LeaveImGroupReq request, io.grpc.stub.StreamObserver<LeaveImGroupReply> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLeaveImGroupMethod(), responseObserver);
        }

        /**
         *
         */
        public io.grpc.stub.StreamObserver<LiveRoomMessageReq> liveRoomMessage(io.grpc.stub.StreamObserver<LiveRoomMessageReply> responseObserver) {
            return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getLiveRoomMessageMethod(), responseObserver);
        }

        /**
         *
         */
        public void ping(PingReq request, io.grpc.stub.StreamObserver<Pong> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPingMethod(), responseObserver);
        }

        /**
         *
         */
        public io.grpc.stub.StreamObserver<ImMessageReq> onlineImMessage(io.grpc.stub.StreamObserver<ImMessageReply> responseObserver) {
            return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getOnlineImMessageMethod(), responseObserver);
        }

        @java.lang.Override
        public final io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor()).addMethod(getListenTopicDataMethod(), io.grpc.stub.ServerCalls.asyncBidiStreamingCall(new MethodHandlers<ListenTopicReq, ListenTopicReply>(this, METHODID_LISTEN_TOPIC_DATA))).addMethod(getLeaveImGroupMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(new MethodHandlers<LeaveImGroupReq, LeaveImGroupReply>(this, METHODID_LEAVE_IM_GROUP))).addMethod(getLiveRoomMessageMethod(), io.grpc.stub.ServerCalls.asyncBidiStreamingCall(new MethodHandlers<LiveRoomMessageReq, LiveRoomMessageReply>(this, METHODID_LIVE_ROOM_MESSAGE))).addMethod(getPingMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(new MethodHandlers<PingReq, Pong>(this, METHODID_PING))).addMethod(getOnlineImMessageMethod(), io.grpc.stub.ServerCalls.asyncBidiStreamingCall(new MethodHandlers<ImMessageReq, ImMessageReply>(this, METHODID_ONLINE_IM_MESSAGE))).build();
        }
    }

    /**
     *
     */
    public static final class MsgApiStub extends io.grpc.stub.AbstractAsyncStub<MsgApiStub> {
        private MsgApiStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected MsgApiStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new MsgApiStub(channel, callOptions);
        }

        /**
         *
         */
        public io.grpc.stub.StreamObserver<ListenTopicReq> listenTopicData(io.grpc.stub.StreamObserver<ListenTopicReply> responseObserver) {
            return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(getChannel().newCall(getListenTopicDataMethod(), getCallOptions()), responseObserver);
        }

        /**
         *
         */
        public void leaveImGroup(LeaveImGroupReq request, io.grpc.stub.StreamObserver<LeaveImGroupReply> responseObserver) {
            io.grpc.stub.ClientCalls.asyncUnaryCall(getChannel().newCall(getLeaveImGroupMethod(), getCallOptions()), request, responseObserver);
        }

        /**
         *
         */
        public io.grpc.stub.StreamObserver<LiveRoomMessageReq> liveRoomMessage(io.grpc.stub.StreamObserver<LiveRoomMessageReply> responseObserver) {
            return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(getChannel().newCall(getLiveRoomMessageMethod(), getCallOptions()), responseObserver);
        }

        /**
         *
         */
        public void ping(PingReq request, io.grpc.stub.StreamObserver<Pong> responseObserver) {
            io.grpc.stub.ClientCalls.asyncUnaryCall(getChannel().newCall(getPingMethod(), getCallOptions()), request, responseObserver);
        }

        /**
         *
         */
        public io.grpc.stub.StreamObserver<ImMessageReq> onlineImMessage(io.grpc.stub.StreamObserver<ImMessageReply> responseObserver) {
            return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(getChannel().newCall(getOnlineImMessageMethod(), getCallOptions()), responseObserver);
        }
    }

    /**
     *
     */
    public static final class MsgApiBlockingStub extends io.grpc.stub.AbstractBlockingStub<MsgApiBlockingStub> {
        private MsgApiBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected MsgApiBlockingStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new MsgApiBlockingStub(channel, callOptions);
        }

        /**
         *
         */
        public LeaveImGroupReply leaveImGroup(LeaveImGroupReq request) {
            return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getLeaveImGroupMethod(), getCallOptions(), request);
        }

        /**
         *
         */
        public Pong ping(PingReq request) {
            return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getPingMethod(), getCallOptions(), request);
        }
    }

    /**
     *
     */
    public static final class MsgApiFutureStub extends io.grpc.stub.AbstractFutureStub<MsgApiFutureStub> {
        private MsgApiFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected MsgApiFutureStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new MsgApiFutureStub(channel, callOptions);
        }

        /**
         *
         */
        public com.google.common.util.concurrent.ListenableFuture<LeaveImGroupReply> leaveImGroup(LeaveImGroupReq request) {
            return io.grpc.stub.ClientCalls.futureUnaryCall(getChannel().newCall(getLeaveImGroupMethod(), getCallOptions()), request);
        }

        /**
         *
         */
        public com.google.common.util.concurrent.ListenableFuture<Pong> ping(PingReq request) {
            return io.grpc.stub.ClientCalls.futureUnaryCall(getChannel().newCall(getPingMethod(), getCallOptions()), request);
        }
    }

    private static final int METHODID_LEAVE_IM_GROUP = 0;
    private static final int METHODID_PING = 1;
    private static final int METHODID_LISTEN_TOPIC_DATA = 2;
    private static final int METHODID_LIVE_ROOM_MESSAGE = 3;
    private static final int METHODID_ONLINE_IM_MESSAGE = 4;

    private static final class MethodHandlers<Req, Resp> implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>, io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
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
                case METHODID_LEAVE_IM_GROUP:
                    serviceImpl.leaveImGroup((LeaveImGroupReq) request, (io.grpc.stub.StreamObserver<LeaveImGroupReply>) responseObserver);
                    break;
                case METHODID_PING:
                    serviceImpl.ping((PingReq) request, (io.grpc.stub.StreamObserver<Pong>) responseObserver);
                    break;
                default:
                    throw new AssertionError();
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                case METHODID_LISTEN_TOPIC_DATA:
                    return (io.grpc.stub.StreamObserver<Req>) serviceImpl.listenTopicData((io.grpc.stub.StreamObserver<ListenTopicReply>) responseObserver);
                case METHODID_LIVE_ROOM_MESSAGE:
                    return (io.grpc.stub.StreamObserver<Req>) serviceImpl.liveRoomMessage((io.grpc.stub.StreamObserver<LiveRoomMessageReply>) responseObserver);
                case METHODID_ONLINE_IM_MESSAGE:
                    return (io.grpc.stub.StreamObserver<Req>) serviceImpl.onlineImMessage((io.grpc.stub.StreamObserver<ImMessageReply>) responseObserver);
                default:
                    throw new AssertionError();
            }
        }
    }

    private static abstract class MsgApiBaseDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
        MsgApiBaseDescriptorSupplier() {}

        @java.lang.Override
        public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
            return IMProtoc.getDescriptor();
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
            return getFileDescriptor().findServiceByName("MsgApi");
        }
    }

    private static final class MsgApiFileDescriptorSupplier extends MsgApiBaseDescriptorSupplier {
        MsgApiFileDescriptorSupplier() {}
    }

    private static final class MsgApiMethodDescriptorSupplier extends MsgApiBaseDescriptorSupplier implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
        private final String methodName;

        MsgApiMethodDescriptorSupplier(String methodName) {
            this.methodName = methodName;
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
            return getServiceDescriptor().findMethodByName(methodName);
        }
    }

    private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

    public static io.grpc.ServiceDescriptor getServiceDescriptor() {
        io.grpc.ServiceDescriptor result = serviceDescriptor;
        if (result == null) {
            synchronized (MsgApiGrpc.class) {
                result = serviceDescriptor;
                if (result == null) {
                    serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME).setSchemaDescriptor(new MsgApiFileDescriptorSupplier()).addMethod(getListenTopicDataMethod()).addMethod(getLeaveImGroupMethod()).addMethod(getLiveRoomMessageMethod()).addMethod(getPingMethod()).addMethod(getOnlineImMessageMethod()).build();
                }
            }
        }
        return result;
    }
}
