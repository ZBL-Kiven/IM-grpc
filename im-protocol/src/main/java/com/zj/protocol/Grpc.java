package com.zj.protocol;

import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;

import com.zj.protocol.grpc.MsgApiGrpc;
import com.zj.protocol.utl.ClientHeaderInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.grpc.ClientInterceptor;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;


@SuppressWarnings("unused")
public final class Grpc {

    private volatile static Grpc current;
    private volatile CachedChannel<MsgApiGrpc.MsgApiStub> cache;

    public static boolean isAlive() {
        return current != null && current.cache != null && current.cache.isAlive();
    }

    public static void reset() {
        if (current != null && current.cache != null) current.cache.reset();
    }

    public static void shutdown() {
        if (current != null && current.cache != null) current.cache.shutdown();
    }

    public static MsgApiGrpc.MsgApiStub stub(Map<String, String> header) {
        if (current == null || current.cache == null) return null;
        Function<ManagedChannel, MsgApiGrpc.MsgApiStub> func = MsgApiGrpc::newStub;
        return current.cache.stub(header, func);
    }

    public static void build(GrpcConfig config, Consumer<Boolean> consumer) {
        if (current == null) {
            synchronized (Grpc.class) {
                if (current == null) current = new Grpc();
            }
        }
        current.createChannel(config, consumer);
    }

    private void createChannel(GrpcConfig config, Consumer<Boolean> consumer) {
        String key = config.getHost() + config.getPort();
        if (cache == null || !cache.isAlive() || !cache.key.equals(key)) {
            if (cache != null) {
                cache.shutdown();
            }
            cache = new CachedChannel<>(key, config, consumer);
        } else {
            consumer.accept(cache.isAlive());
        }
    }

    private static class CachedChannel<T extends AbstractStub<T>> {

        private final ManagedChannel channel;
        private final String key;
        private boolean inConnection;
        private T cachedStub;

        private CachedChannel(String key, GrpcConfig config, Consumer<Boolean> consumer) {
            this.key = key;
            inConnection = true;
            this.channel = ManagedChannelBuilder.forAddress(config.getHost(), config.getPort()).keepAliveTimeout(config.getKeepAliveTimeOut(), TimeUnit.MILLISECONDS).idleTimeout(config.getIdleTimeOut(), TimeUnit.MILLISECONDS).usePlaintext().build();
            channel.notifyWhenStateChanged(ConnectivityState.READY, () -> {
                cachedStub = null;
                inConnection = false;
                consumer.accept(true);
            });
        }

        public T stub(Map<String, String> header, Function<ManagedChannel, T> f) {
            if (cachedStub == null) cachedStub = new RequestBuilder<>(channel, f, header).build();
            return cachedStub;
        }

        public boolean isAlive() {
            return inConnection || (!channel.isShutdown() && !channel.isTerminated());
        }

        public void shutdown() {
            reset();
            channel.shutdownNow();
        }

        public void reset() {
            channel.enterIdle();
            channel.resetConnectBackoff();
        }

        public boolean isTerminated() {
            return channel.isShutdown() || channel.isTerminated();
        }

        public void notifyWhenStateChanged(ConnectivityState source, Runnable callback) {
            channel.notifyWhenStateChanged(source, callback);
        }

    }


    public static class RequestBuilder<T extends AbstractStub<T>> {

        private final Map<String, String> headerParams;
        private final List<ClientInterceptor> interceptors = new ArrayList<>();

        private final T stub;

        public RequestBuilder(ManagedChannel channel, Function<ManagedChannel, T> f, Map<String, String> header) {
            this.headerParams = new HashMap<>(header);
            stub = f.apply(channel);
        }

        public RequestBuilder<T> header(Map<String, String> map) {
            headerParams.putAll(map);
            return this;
        }

        @SafeVarargs
        public final RequestBuilder<T> header(final Pair<String, String>... headers) {
            Map<String, String> params = new HashMap<>();
            for (Pair<String, String> p : headers) {
                params.put(p.first, p.second);
            }
            return header(params);
        }

        public RequestBuilder<T> addInterceptor(ClientInterceptor interceptor) {
            this.interceptors.add(interceptor);
            return this;
        }

        public T build() {
            interceptors.add(new ClientHeaderInterceptor(headerParams));
            ClientInterceptor[] i = new ClientInterceptor[interceptors.size()];
            interceptors.toArray(i);
            return stub.withInterceptors(i);
        }
    }
}