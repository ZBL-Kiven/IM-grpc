// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: msg_api.proto

package com.zj.protocol.grpc;

public interface VideoContentOrBuilder extends
    // @@protoc_insertion_point(interface_extends:app.VideoContent)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string url = 1;</code>
   * @return The url.
   */
  java.lang.String getUrl();
  /**
   * <code>string url = 1;</code>
   * @return The bytes for url.
   */
  com.google.protobuf.ByteString
      getUrlBytes();

  /**
   * <code>uint64 duration = 4;</code>
   * @return The duration.
   */
  long getDuration();

  /**
   * <code>uint32 height = 5;</code>
   * @return The height.
   */
  int getHeight();

  /**
   * <code>uint32 width = 6;</code>
   * @return The width.
   */
  int getWidth();
}
