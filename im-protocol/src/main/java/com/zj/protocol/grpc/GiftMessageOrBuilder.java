// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: msg_api.proto

package com.zj.protocol.grpc;

public interface GiftMessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:app.GiftMessage)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>uint32 giftId = 1;</code>
   * @return The giftId.
   */
  int getGiftId();

  /**
   * <code>string giftImage = 2;</code>
   * @return The giftImage.
   */
  java.lang.String getGiftImage();
  /**
   * <code>string giftImage = 2;</code>
   * @return The bytes for giftImage.
   */
  com.google.protobuf.ByteString
      getGiftImageBytes();

  /**
   * <code>.app.MultiLanguage multiLanguage = 3;</code>
   * @return Whether the multiLanguage field is set.
   */
  boolean hasMultiLanguage();
  /**
   * <code>.app.MultiLanguage multiLanguage = 3;</code>
   * @return The multiLanguage.
   */
  MultiLanguage getMultiLanguage();
  /**
   * <code>.app.MultiLanguage multiLanguage = 3;</code>
   */
  MultiLanguageOrBuilder getMultiLanguageOrBuilder();

  /**
   * <code>uint32 amount = 4;</code>
   * @return The amount.
   */
  int getAmount();

  /**
   * <code>string bundle = 5;</code>
   * @return The bundle.
   */
  java.lang.String getBundle();
  /**
   * <code>string bundle = 5;</code>
   * @return The bytes for bundle.
   */
  com.google.protobuf.ByteString
      getBundleBytes();

  /**
   * <code>string receiveUserName = 6;</code>
   * @return The receiveUserName.
   */
  java.lang.String getReceiveUserName();
  /**
   * <code>string receiveUserName = 6;</code>
   * @return The bytes for receiveUserName.
   */
  com.google.protobuf.ByteString
      getReceiveUserNameBytes();

  /**
   * <code>uint32 receiveUserId = 7;</code>
   * @return The receiveUserId.
   */
  int getReceiveUserId();
}
