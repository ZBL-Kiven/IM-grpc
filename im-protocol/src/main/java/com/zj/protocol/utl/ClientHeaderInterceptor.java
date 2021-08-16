package com.zj.protocol.utl;


import java.util.Map;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

public class ClientHeaderInterceptor implements ClientInterceptor {

    private final Map<String, String> mHeaderMap;

    public ClientHeaderInterceptor(Map<String, String> headerMap) {
        mHeaderMap = headerMap;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                if (mHeaderMap != null) {
                    for (String key : mHeaderMap.keySet()) {
                        Metadata.Key<String> customHeadKey = Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER);
                        String value = mHeaderMap.get(key);
                        if (value != null) headers.put(customHeadKey, value);
                    }
                }

                // Log.e("------ ", "header send to server:" + headers.toString());

                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {

                        // Log.e("------ ", "header received from server:" + headers.toString());

                        super.onHeaders(headers);
                    }
                }, headers);
            }
        };
    }
}