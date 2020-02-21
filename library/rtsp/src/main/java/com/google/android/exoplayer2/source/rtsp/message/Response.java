/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.source.rtsp.message;

import androidx.annotation.NonNull;
import java.util.List;
import java.util.Map;

/**
 * An RTSP response
 */
public final class Response extends Message {
    private Protocol protocol;
    private Status status;

    private Response(Builder builder) {
        super(Message.RESPONSE);

        this.protocol = builder.protocol;
        this.status = builder.status;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public Protocol getProtocol() { return protocol; }

    public Status getStatus() { return status; }

    public boolean isSuccess() {
        return (status == Status.OK);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(protocol).append(' ').append(status.code()).append(' ').
                append(status.reason()).append("\r\n");

        for (Map.Entry<String, List<String>> entry : headers.getSnapshot().entrySet()) {
            String header = entry.getKey();
            List<String> values = entry.getValue();
            for (String value : values) {
                str.append(header).append(": ").append(value).append("\r\n");
            }
        }

        if (body != null) {
            str.append(Header.ContentType).append(": ").append(body.getContentType()).append("\r\n");
            str.append(Header.ContentLength).append(": ").append(body.getContentLength()).append("\r\n");
            str.append("\r\n").append(body.getContent());
        } else {
            str.append("\r\n");
        }

        return str.toString();
    }


    public static class Builder implements Message.Builder {
        Protocol protocol;
        Status status;
        Headers headers;
        MessageBody body;

        public Builder() {
            this.protocol = Protocol.RTSP_1_0;
            this.headers = new Headers();
        }

        public Builder(Response response) {
            this.status = response.status;
            this.protocol = response.protocol;
            this.headers = response.headers;
            this.body = response.body;
        }

        public Builder setStatus(Status status) {
            if (status == null) throw new NullPointerException("status is null");

            this.status = status;
            return this;
        }

        @Override
        public Builder setProtocol(Protocol protocol) {
            if (protocol == null) throw new NullPointerException("protocol is null");

            this.protocol = protocol;
            return this;
        }

        @Override
        public Builder setHeader(Header header, Object value) {
            if (header == null) throw new NullPointerException("header is null");

            if (value != null) {
                headers.add(header.toString(), value.toString());
            }

            return this;
        }

        @Override
        public Builder setBody(MessageBody messageBody) {
            if (messageBody == null) throw new NullPointerException("messageBody is null");

            this.body = messageBody;
            return this;
        }

        @Override
        public Response build() {
            if (protocol == null) throw new IllegalStateException("protocol is null");
            if (status == null) throw new IllegalStateException("status is null");
            if (headers.size() == 0) throw new IllegalStateException("headers is null");

            return new Response(this);
        }
    }
}
