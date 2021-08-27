// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: msg_api.proto

package com.zj.protocol.grpc;

/**
 * Protobuf type {@code app.QuestionContent}
 */
public final class QuestionContent extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:app.QuestionContent)
    QuestionContentOrBuilder {
private static final long serialVersionUID = 0L;
  // Use QuestionContent.newBuilder() to construct.
  private QuestionContent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private QuestionContent() {
    answerMsgType_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new QuestionContent();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private QuestionContent(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            TextContent.Builder subBuilder = null;
            if (textContent_ != null) {
              subBuilder = textContent_.toBuilder();
            }
            textContent_ = input.readMessage(TextContent.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(textContent_);
              textContent_ = subBuilder.buildPartial();
            }

            break;
          }
          case 16: {

            questionStatus_ = input.readUInt32();
            break;
          }
          case 24: {

            questionId_ = input.readUInt64();
            break;
          }
          case 32: {

            spark_ = input.readUInt64();
            break;
          }
          case 40: {

            diamond_ = input.readUInt64();
            break;
          }
          case 48: {

            isPublic_ = input.readBool();
            break;
          }
          case 56: {

            sendTime_ = input.readUInt64();
            break;
          }
          case 64: {

            expireTime_ = input.readUInt64();
            break;
          }
          case 74: {
            java.lang.String s = input.readStringRequireUtf8();

            answerMsgType_ = s;
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return IMProtoc.internal_static_app_QuestionContent_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return IMProtoc.internal_static_app_QuestionContent_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            QuestionContent.class, QuestionContent.Builder.class);
  }

  public static final int TEXTCONTENT_FIELD_NUMBER = 1;
  private TextContent textContent_;
  /**
   * <code>.app.TextContent textContent = 1;</code>
   * @return Whether the textContent field is set.
   */
  @java.lang.Override
  public boolean hasTextContent() {
    return textContent_ != null;
  }
  /**
   * <code>.app.TextContent textContent = 1;</code>
   * @return The textContent.
   */
  @java.lang.Override
  public TextContent getTextContent() {
    return textContent_ == null ? TextContent.getDefaultInstance() : textContent_;
  }
  /**
   * <code>.app.TextContent textContent = 1;</code>
   */
  @java.lang.Override
  public TextContentOrBuilder getTextContentOrBuilder() {
    return getTextContent();
  }

  public static final int QUESTIONSTATUS_FIELD_NUMBER = 2;
  private int questionStatus_;
  /**
   * <code>uint32 questionStatus = 2;</code>
   * @return The questionStatus.
   */
  @java.lang.Override
  public int getQuestionStatus() {
    return questionStatus_;
  }

  public static final int QUESTIONID_FIELD_NUMBER = 3;
  private long questionId_;
  /**
   * <code>uint64 questionId = 3;</code>
   * @return The questionId.
   */
  @java.lang.Override
  public long getQuestionId() {
    return questionId_;
  }

  public static final int SPARK_FIELD_NUMBER = 4;
  private long spark_;
  /**
   * <code>uint64 spark = 4;</code>
   * @return The spark.
   */
  @java.lang.Override
  public long getSpark() {
    return spark_;
  }

  public static final int DIAMOND_FIELD_NUMBER = 5;
  private long diamond_;
  /**
   * <code>uint64 diamond = 5;</code>
   * @return The diamond.
   */
  @java.lang.Override
  public long getDiamond() {
    return diamond_;
  }

  public static final int ISPUBLIC_FIELD_NUMBER = 6;
  private boolean isPublic_;
  /**
   * <code>bool isPublic = 6;</code>
   * @return The isPublic.
   */
  @java.lang.Override
  public boolean getIsPublic() {
    return isPublic_;
  }

  public static final int SENDTIME_FIELD_NUMBER = 7;
  private long sendTime_;
  /**
   * <code>uint64 sendTime = 7;</code>
   * @return The sendTime.
   */
  @java.lang.Override
  public long getSendTime() {
    return sendTime_;
  }

  public static final int EXPIRETIME_FIELD_NUMBER = 8;
  private long expireTime_;
  /**
   * <code>uint64 expireTime = 8;</code>
   * @return The expireTime.
   */
  @java.lang.Override
  public long getExpireTime() {
    return expireTime_;
  }

  public static final int ANSWERMSGTYPE_FIELD_NUMBER = 9;
  private volatile java.lang.Object answerMsgType_;
  /**
   * <code>string answerMsgType = 9;</code>
   * @return The answerMsgType.
   */
  @java.lang.Override
  public java.lang.String getAnswerMsgType() {
    java.lang.Object ref = answerMsgType_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      answerMsgType_ = s;
      return s;
    }
  }
  /**
   * <code>string answerMsgType = 9;</code>
   * @return The bytes for answerMsgType.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getAnswerMsgTypeBytes() {
    java.lang.Object ref = answerMsgType_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      answerMsgType_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (textContent_ != null) {
      output.writeMessage(1, getTextContent());
    }
    if (questionStatus_ != 0) {
      output.writeUInt32(2, questionStatus_);
    }
    if (questionId_ != 0L) {
      output.writeUInt64(3, questionId_);
    }
    if (spark_ != 0L) {
      output.writeUInt64(4, spark_);
    }
    if (diamond_ != 0L) {
      output.writeUInt64(5, diamond_);
    }
    if (isPublic_ != false) {
      output.writeBool(6, isPublic_);
    }
    if (sendTime_ != 0L) {
      output.writeUInt64(7, sendTime_);
    }
    if (expireTime_ != 0L) {
      output.writeUInt64(8, expireTime_);
    }
    if (!getAnswerMsgTypeBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 9, answerMsgType_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (textContent_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, getTextContent());
    }
    if (questionStatus_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(2, questionStatus_);
    }
    if (questionId_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt64Size(3, questionId_);
    }
    if (spark_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt64Size(4, spark_);
    }
    if (diamond_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt64Size(5, diamond_);
    }
    if (isPublic_ != false) {
      size += com.google.protobuf.CodedOutputStream
        .computeBoolSize(6, isPublic_);
    }
    if (sendTime_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt64Size(7, sendTime_);
    }
    if (expireTime_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt64Size(8, expireTime_);
    }
    if (!getAnswerMsgTypeBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(9, answerMsgType_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof QuestionContent)) {
      return super.equals(obj);
    }
    QuestionContent other = (QuestionContent) obj;

    if (hasTextContent() != other.hasTextContent()) return false;
    if (hasTextContent()) {
      if (!getTextContent()
          .equals(other.getTextContent())) return false;
    }
    if (getQuestionStatus()
        != other.getQuestionStatus()) return false;
    if (getQuestionId()
        != other.getQuestionId()) return false;
    if (getSpark()
        != other.getSpark()) return false;
    if (getDiamond()
        != other.getDiamond()) return false;
    if (getIsPublic()
        != other.getIsPublic()) return false;
    if (getSendTime()
        != other.getSendTime()) return false;
    if (getExpireTime()
        != other.getExpireTime()) return false;
    if (!getAnswerMsgType()
        .equals(other.getAnswerMsgType())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasTextContent()) {
      hash = (37 * hash) + TEXTCONTENT_FIELD_NUMBER;
      hash = (53 * hash) + getTextContent().hashCode();
    }
    hash = (37 * hash) + QUESTIONSTATUS_FIELD_NUMBER;
    hash = (53 * hash) + getQuestionStatus();
    hash = (37 * hash) + QUESTIONID_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getQuestionId());
    hash = (37 * hash) + SPARK_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getSpark());
    hash = (37 * hash) + DIAMOND_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getDiamond());
    hash = (37 * hash) + ISPUBLIC_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
        getIsPublic());
    hash = (37 * hash) + SENDTIME_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getSendTime());
    hash = (37 * hash) + EXPIRETIME_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getExpireTime());
    hash = (37 * hash) + ANSWERMSGTYPE_FIELD_NUMBER;
    hash = (53 * hash) + getAnswerMsgType().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static QuestionContent parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static QuestionContent parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static QuestionContent parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static QuestionContent parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static QuestionContent parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static QuestionContent parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static QuestionContent parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static QuestionContent parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static QuestionContent parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static QuestionContent parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static QuestionContent parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static QuestionContent parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(QuestionContent prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code app.QuestionContent}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:app.QuestionContent)
          QuestionContentOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return IMProtoc.internal_static_app_QuestionContent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return IMProtoc.internal_static_app_QuestionContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              QuestionContent.class, QuestionContent.Builder.class);
    }

    // Construct using com.zj.protocol.grpc.QuestionContent.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      if (textContentBuilder_ == null) {
        textContent_ = null;
      } else {
        textContent_ = null;
        textContentBuilder_ = null;
      }
      questionStatus_ = 0;

      questionId_ = 0L;

      spark_ = 0L;

      diamond_ = 0L;

      isPublic_ = false;

      sendTime_ = 0L;

      expireTime_ = 0L;

      answerMsgType_ = "";

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return IMProtoc.internal_static_app_QuestionContent_descriptor;
    }

    @java.lang.Override
    public QuestionContent getDefaultInstanceForType() {
      return QuestionContent.getDefaultInstance();
    }

    @java.lang.Override
    public QuestionContent build() {
      QuestionContent result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public QuestionContent buildPartial() {
      QuestionContent result = new QuestionContent(this);
      if (textContentBuilder_ == null) {
        result.textContent_ = textContent_;
      } else {
        result.textContent_ = textContentBuilder_.build();
      }
      result.questionStatus_ = questionStatus_;
      result.questionId_ = questionId_;
      result.spark_ = spark_;
      result.diamond_ = diamond_;
      result.isPublic_ = isPublic_;
      result.sendTime_ = sendTime_;
      result.expireTime_ = expireTime_;
      result.answerMsgType_ = answerMsgType_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof QuestionContent) {
        return mergeFrom((QuestionContent)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(QuestionContent other) {
      if (other == QuestionContent.getDefaultInstance()) return this;
      if (other.hasTextContent()) {
        mergeTextContent(other.getTextContent());
      }
      if (other.getQuestionStatus() != 0) {
        setQuestionStatus(other.getQuestionStatus());
      }
      if (other.getQuestionId() != 0L) {
        setQuestionId(other.getQuestionId());
      }
      if (other.getSpark() != 0L) {
        setSpark(other.getSpark());
      }
      if (other.getDiamond() != 0L) {
        setDiamond(other.getDiamond());
      }
      if (other.getIsPublic() != false) {
        setIsPublic(other.getIsPublic());
      }
      if (other.getSendTime() != 0L) {
        setSendTime(other.getSendTime());
      }
      if (other.getExpireTime() != 0L) {
        setExpireTime(other.getExpireTime());
      }
      if (!other.getAnswerMsgType().isEmpty()) {
        answerMsgType_ = other.answerMsgType_;
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      QuestionContent parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (QuestionContent) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private TextContent textContent_;
    private com.google.protobuf.SingleFieldBuilderV3<TextContent, TextContent.Builder, TextContentOrBuilder> textContentBuilder_;
    /**
     * <code>.app.TextContent textContent = 1;</code>
     * @return Whether the textContent field is set.
     */
    public boolean hasTextContent() {
      return textContentBuilder_ != null || textContent_ != null;
    }
    /**
     * <code>.app.TextContent textContent = 1;</code>
     * @return The textContent.
     */
    public TextContent getTextContent() {
      if (textContentBuilder_ == null) {
        return textContent_ == null ? TextContent.getDefaultInstance() : textContent_;
      } else {
        return textContentBuilder_.getMessage();
      }
    }
    /**
     * <code>.app.TextContent textContent = 1;</code>
     */
    public Builder setTextContent(TextContent value) {
      if (textContentBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        textContent_ = value;
        onChanged();
      } else {
        textContentBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.app.TextContent textContent = 1;</code>
     */
    public Builder setTextContent(
        TextContent.Builder builderForValue) {
      if (textContentBuilder_ == null) {
        textContent_ = builderForValue.build();
        onChanged();
      } else {
        textContentBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.app.TextContent textContent = 1;</code>
     */
    public Builder mergeTextContent(TextContent value) {
      if (textContentBuilder_ == null) {
        if (textContent_ != null) {
          textContent_ =
            TextContent.newBuilder(textContent_).mergeFrom(value).buildPartial();
        } else {
          textContent_ = value;
        }
        onChanged();
      } else {
        textContentBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.app.TextContent textContent = 1;</code>
     */
    public Builder clearTextContent() {
      if (textContentBuilder_ == null) {
        textContent_ = null;
        onChanged();
      } else {
        textContent_ = null;
        textContentBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.app.TextContent textContent = 1;</code>
     */
    public TextContent.Builder getTextContentBuilder() {
      
      onChanged();
      return getTextContentFieldBuilder().getBuilder();
    }
    /**
     * <code>.app.TextContent textContent = 1;</code>
     */
    public TextContentOrBuilder getTextContentOrBuilder() {
      if (textContentBuilder_ != null) {
        return textContentBuilder_.getMessageOrBuilder();
      } else {
        return textContent_ == null ?
            TextContent.getDefaultInstance() : textContent_;
      }
    }
    /**
     * <code>.app.TextContent textContent = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<TextContent, TextContent.Builder, TextContentOrBuilder>
        getTextContentFieldBuilder() {
      if (textContentBuilder_ == null) {
        textContentBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<TextContent, TextContent.Builder, TextContentOrBuilder>(
                getTextContent(),
                getParentForChildren(),
                isClean());
        textContent_ = null;
      }
      return textContentBuilder_;
    }

    private int questionStatus_ ;
    /**
     * <code>uint32 questionStatus = 2;</code>
     * @return The questionStatus.
     */
    @java.lang.Override
    public int getQuestionStatus() {
      return questionStatus_;
    }
    /**
     * <code>uint32 questionStatus = 2;</code>
     * @param value The questionStatus to set.
     * @return This builder for chaining.
     */
    public Builder setQuestionStatus(int value) {
      
      questionStatus_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>uint32 questionStatus = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearQuestionStatus() {
      
      questionStatus_ = 0;
      onChanged();
      return this;
    }

    private long questionId_ ;
    /**
     * <code>uint64 questionId = 3;</code>
     * @return The questionId.
     */
    @java.lang.Override
    public long getQuestionId() {
      return questionId_;
    }
    /**
     * <code>uint64 questionId = 3;</code>
     * @param value The questionId to set.
     * @return This builder for chaining.
     */
    public Builder setQuestionId(long value) {
      
      questionId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>uint64 questionId = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearQuestionId() {
      
      questionId_ = 0L;
      onChanged();
      return this;
    }

    private long spark_ ;
    /**
     * <code>uint64 spark = 4;</code>
     * @return The spark.
     */
    @java.lang.Override
    public long getSpark() {
      return spark_;
    }
    /**
     * <code>uint64 spark = 4;</code>
     * @param value The spark to set.
     * @return This builder for chaining.
     */
    public Builder setSpark(long value) {
      
      spark_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>uint64 spark = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearSpark() {
      
      spark_ = 0L;
      onChanged();
      return this;
    }

    private long diamond_ ;
    /**
     * <code>uint64 diamond = 5;</code>
     * @return The diamond.
     */
    @java.lang.Override
    public long getDiamond() {
      return diamond_;
    }
    /**
     * <code>uint64 diamond = 5;</code>
     * @param value The diamond to set.
     * @return This builder for chaining.
     */
    public Builder setDiamond(long value) {
      
      diamond_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>uint64 diamond = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearDiamond() {
      
      diamond_ = 0L;
      onChanged();
      return this;
    }

    private boolean isPublic_ ;
    /**
     * <code>bool isPublic = 6;</code>
     * @return The isPublic.
     */
    @java.lang.Override
    public boolean getIsPublic() {
      return isPublic_;
    }
    /**
     * <code>bool isPublic = 6;</code>
     * @param value The isPublic to set.
     * @return This builder for chaining.
     */
    public Builder setIsPublic(boolean value) {
      
      isPublic_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bool isPublic = 6;</code>
     * @return This builder for chaining.
     */
    public Builder clearIsPublic() {
      
      isPublic_ = false;
      onChanged();
      return this;
    }

    private long sendTime_ ;
    /**
     * <code>uint64 sendTime = 7;</code>
     * @return The sendTime.
     */
    @java.lang.Override
    public long getSendTime() {
      return sendTime_;
    }
    /**
     * <code>uint64 sendTime = 7;</code>
     * @param value The sendTime to set.
     * @return This builder for chaining.
     */
    public Builder setSendTime(long value) {
      
      sendTime_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>uint64 sendTime = 7;</code>
     * @return This builder for chaining.
     */
    public Builder clearSendTime() {
      
      sendTime_ = 0L;
      onChanged();
      return this;
    }

    private long expireTime_ ;
    /**
     * <code>uint64 expireTime = 8;</code>
     * @return The expireTime.
     */
    @java.lang.Override
    public long getExpireTime() {
      return expireTime_;
    }
    /**
     * <code>uint64 expireTime = 8;</code>
     * @param value The expireTime to set.
     * @return This builder for chaining.
     */
    public Builder setExpireTime(long value) {
      
      expireTime_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>uint64 expireTime = 8;</code>
     * @return This builder for chaining.
     */
    public Builder clearExpireTime() {
      
      expireTime_ = 0L;
      onChanged();
      return this;
    }

    private java.lang.Object answerMsgType_ = "";
    /**
     * <code>string answerMsgType = 9;</code>
     * @return The answerMsgType.
     */
    public java.lang.String getAnswerMsgType() {
      java.lang.Object ref = answerMsgType_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        answerMsgType_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string answerMsgType = 9;</code>
     * @return The bytes for answerMsgType.
     */
    public com.google.protobuf.ByteString
        getAnswerMsgTypeBytes() {
      java.lang.Object ref = answerMsgType_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        answerMsgType_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string answerMsgType = 9;</code>
     * @param value The answerMsgType to set.
     * @return This builder for chaining.
     */
    public Builder setAnswerMsgType(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      answerMsgType_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string answerMsgType = 9;</code>
     * @return This builder for chaining.
     */
    public Builder clearAnswerMsgType() {
      
      answerMsgType_ = getDefaultInstance().getAnswerMsgType();
      onChanged();
      return this;
    }
    /**
     * <code>string answerMsgType = 9;</code>
     * @param value The bytes for answerMsgType to set.
     * @return This builder for chaining.
     */
    public Builder setAnswerMsgTypeBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      answerMsgType_ = value;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:app.QuestionContent)
  }

  // @@protoc_insertion_point(class_scope:app.QuestionContent)
  private static final QuestionContent DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new QuestionContent();
  }

  public static QuestionContent getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<QuestionContent>
      PARSER = new com.google.protobuf.AbstractParser<QuestionContent>() {
    @java.lang.Override
    public QuestionContent parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new QuestionContent(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<QuestionContent> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<QuestionContent> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public QuestionContent getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

