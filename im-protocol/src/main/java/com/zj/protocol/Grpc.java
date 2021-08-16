package com.zj.protocol;

import androidx.arch.core.util.Function;
import androidx.core.util.Pair;

import com.zj.protocol.utl.ClientHeaderInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.grpc.ClientInterceptor;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;


@SuppressWarnings("unused")
public final class Grpc {

    private static Map<String, CachedChannel> cachedChannels;
    private volatile static Grpc current;

    private Grpc() {
        if (cachedChannels != null) cachedChannels.clear();
        cachedChannels = new HashMap<>();
    }

    public static CachedChannel get(String host, int port) {
        if (current == null) {
            synchronized (Grpc.class) {
                if (current == null) current = new Grpc();
            }
        }
        return current.create(host, port);
    }

    private CachedChannel create(String host, int port) {
        String key = host + port;
        CachedChannel c = cachedChannels.get(key);
        if (c == null) {
            c = new CachedChannel(key, host, port);
            cachedChannels.put(key, c);
        }
        return c;
    }

    public static class CachedChannel {

        private final ManagedChannel channel;
        private final String key;
        private Map<String, String> headerDefault;

        private CachedChannel(String key, String host, int port) {
            this.key = key;
            channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        }

        public CachedChannel defaultHeader(Map<String, String> h) {
            headerDefault = h;
            return this;
        }

        public <T extends AbstractStub<T>> RequestBuilder<T> stub(Function<ManagedChannel, T> f) {
            return new RequestBuilder<>(channel, f, headerDefault);
        }

        public void shutdown() {
            clear();
            channel.shutdown();
        }

        public void shutdownNow() {
            clear();
            channel.shutdownNow();
        }

        public boolean isTerminated() {
            return channel.isTerminated();
        }

        public void resetConnectBackoff() {
            channel.resetConnectBackoff();
        }

        public void notifyWhenStateChanged(ConnectivityState source, Runnable callback) {
            channel.notifyWhenStateChanged(source, callback);
        }

        private void clear() {
            cachedChannels.remove(key);
            headerDefault.clear();
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