package com.betfair.cougar.modules.zipkin.impl;

import com.betfair.cougar.modules.zipkin.api.ZipkinData;
import com.betfair.cougar.modules.zipkin.api.ZipkinDataBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class ZipkinDataImpl implements ZipkinData {

    private final long traceId;
    private final long spanId;
    private final Long parentSpanId;

    private final String spanName;
    private final short port;
    private final Long flags;


    private ZipkinDataImpl(@Nonnull Builder builder) {
        Objects.requireNonNull(builder);

        traceId = builder.traceId;
        spanId = builder.spanId;
        parentSpanId = builder.parentSpanId;
        spanName = builder.spanName;
        port = builder.port;
        flags = builder.flags;

        Objects.requireNonNull(spanName);
    }

    @Override
    public long getTraceId() {
        return traceId;
    }

    @Override
    public long getSpanId() {
        return spanId;
    }

    @Nullable
    @Override
    public Long getParentSpanId() {
        return parentSpanId;
    }

    @Nonnull
    @Override
    public String getSpanName() {
        return spanName;
    }

    @Override
    public short getPort() {
        return port;
    }

    @Nullable
    @Override
    public Long getFlags() {
        return flags;
    }

    @Override
    public String toString() {
        return "ZipkinDataImpl{" +
                "traceId=" + traceId +
                ", spanId=" + spanId +
                ", parentSpanId=" + parentSpanId +
                ", spanName='" + spanName + '\'' +
                ", port=" + port +
                ", flags=" + flags +
                '}';
    }

    public static final class Builder implements ZipkinDataBuilder {
        private long traceId;
        private long spanId;
        private Long parentSpanId;

        private String spanName;
        private short port;
        private Long flags;

        @Nonnull
        @Override
        public Builder traceId(long traceId) {
            this.traceId = traceId;
            return this;
        }

        @Nonnull
        @Override
        public Builder spanId(long spanId) {
            this.spanId = spanId;
            return this;
        }

        @Nonnull
        @Override
        public Builder parentSpanId(@Nullable Long parentSpanId) {
            this.parentSpanId = parentSpanId;
            return this;
        }

        @Nonnull
        @Override
        public Builder spanName(@Nonnull String spanName) {
            this.spanName = spanName;
            return this;
        }

        @Nonnull
        @Override
        public Builder port(short port) {
            this.port = port;
            return this;
        }

        @Nonnull
        @Override
        public Builder flags(@Nullable Long flags) {
            this.flags = flags;
            return this;
        }

        @Nonnull
        @Override
        public ZipkinDataImpl build() {
            return new ZipkinDataImpl(this);
        }
    }
}
