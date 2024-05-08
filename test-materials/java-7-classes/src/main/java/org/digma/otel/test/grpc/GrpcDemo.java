package org.digma.otel.test.grpc;

import io.grpc.*;

import java.io.File;
import java.util.concurrent.Executor;

@SuppressWarnings("ReturnOfNull")
public class GrpcDemo extends ServerBuilder {

    @Override
    public ServerBuilder directExecutor() {
        return null;
    }

    @Override
    public ServerBuilder executor(Executor executor) {
        return null;
    }

    @Override
    public ServerBuilder addService(ServerServiceDefinition service) {
        return null;
    }

    @Override
    public ServerBuilder addService(BindableService bindableService) {
        return null;
    }

    @Override
    public ServerBuilder fallbackHandlerRegistry(HandlerRegistry fallbackRegistry) {
        return null;
    }

    @Override
    public ServerBuilder useTransportSecurity(File certChain, File privateKey) {
        return null;
    }

    @Override
    public ServerBuilder decompressorRegistry(DecompressorRegistry registry) {
        return null;
    }

    @Override
    public ServerBuilder compressorRegistry(CompressorRegistry registry) {
        return null;
    }

    @Override
    public Server build() {
        return null;
    }
}
