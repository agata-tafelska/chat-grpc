package atafelska.chat.client.net;

import atafelska.chat.client.core.Logger;
import io.grpc.*;

public class NetworkLoggingInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                channel.newCall(methodDescriptor, callOptions.withoutWaitForReady())) {

            @Override
            public void sendMessage(ReqT message) {
                Logger.print("**gRPC** Request: " + methodDescriptor.getFullMethodName() + ": " +
                        message.toString());
                super.sendMessage(message);
            }

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {

                ClientCall.Listener<RespT> listener = new ClientCall.Listener<RespT>() {
                    @Override
                    public void onMessage(RespT message) {
                        Logger.print("**gRPC** Response: " + methodDescriptor.getFullMethodName() + ": " +
                                message.toString());
                        super.onMessage(message);
                    }
                };
                super.start(listener, headers);
            }
        };
    }
}
