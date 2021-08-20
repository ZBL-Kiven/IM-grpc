// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: msg_api.proto

package com.zj.protocol.grpc;

public interface QuestionContentOrBuilder extends
    // @@protoc_insertion_point(interface_extends:app.QuestionContent)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.app.TextContent textContent = 1;</code>
   * @return Whether the textContent field is set.
   */
  boolean hasTextContent();
  /**
   * <code>.app.TextContent textContent = 1;</code>
   * @return The textContent.
   */
  TextContent getTextContent();
  /**
   * <code>.app.TextContent textContent = 1;</code>
   */
  TextContentOrBuilder getTextContentOrBuilder();

  /**
   * <code>uint32 questionStatus = 2;</code>
   * @return The questionStatus.
   */
  int getQuestionStatus();

  /**
   * <code>uint64 questionId = 3;</code>
   * @return The questionId.
   */
  long getQuestionId();

  /**
   * <code>uint64 spark = 4;</code>
   * @return The spark.
   */
  long getSpark();

  /**
   * <code>uint64 diamond = 5;</code>
   * @return The diamond.
   */
  long getDiamond();

  /**
   * <code>bool isPublic = 6;</code>
   * @return The isPublic.
   */
  boolean getIsPublic();

  /**
   * <code>uint64 sendTime = 7;</code>
   * @return The sendTime.
   */
  long getSendTime();
}
