// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: msg_api.proto

package com.zj.protocol.grpc;

public interface LiveContentOrBuilder extends
    // @@protoc_insertion_point(interface_extends:app.LiveContent)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>uint64 id = 1;</code>
   * @return The id.
   */
  long getId();

  /**
   * <code>bool status = 2;</code>
   * @return The status.
   */
  boolean getStatus();

  /**
   * <code>uint32 roomId = 3;</code>
   * @return The roomId.
   */
  int getRoomId();

  /**
   * <code>string name = 4;</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <code>string name = 4;</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <code>string area = 5;</code>
   * @return The area.
   */
  java.lang.String getArea();
  /**
   * <code>string area = 5;</code>
   * @return The bytes for area.
   */
  com.google.protobuf.ByteString
      getAreaBytes();

  /**
   * <code>string introduce = 6;</code>
   * @return The introduce.
   */
  java.lang.String getIntroduce();
  /**
   * <code>string introduce = 6;</code>
   * @return The bytes for introduce.
   */
  com.google.protobuf.ByteString
      getIntroduceBytes();

  /**
   * <code>string cover = 7;</code>
   * @return The cover.
   */
  java.lang.String getCover();
  /**
   * <code>string cover = 7;</code>
   * @return The bytes for cover.
   */
  com.google.protobuf.ByteString
      getCoverBytes();

  /**
   * <code>uint32 userId = 8;</code>
   * @return The userId.
   */
  int getUserId();

  /**
   * <code>string channelId = 9;</code>
   * @return The channelId.
   */
  java.lang.String getChannelId();
  /**
   * <code>string channelId = 9;</code>
   * @return The bytes for channelId.
   */
  com.google.protobuf.ByteString
      getChannelIdBytes();
}