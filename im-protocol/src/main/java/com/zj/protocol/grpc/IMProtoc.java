// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: msg_api.proto

package com.zj.protocol.grpc;

public final class IMProtoc {
  private IMProtoc() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_ListenTopicReq_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_ListenTopicReq_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_ListenTopicReply_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_ListenTopicReply_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_GetImMessageReq_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_GetImMessageReq_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_ImMessage_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_ImMessage_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_ImgContent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_ImgContent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_VideoContent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_VideoContent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_TextContent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_TextContent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_AudioContent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_AudioContent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_CCVideoContent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_CCVideoContent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_QuestionContent_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_QuestionContent_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_SenderInfo_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_SenderInfo_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_LeaveImGroupReq_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_LeaveImGroupReq_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_LeaveImGroupReply_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_LeaveImGroupReply_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_PingReq_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_PingReq_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_Pong_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_Pong_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_LiveRoomMessageReq_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_LiveRoomMessageReq_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_app_LiveRoomMessageReply_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_app_LiveRoomMessageReply_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rmsg_api.proto\022\003app\"u\n\016ListenTopicReq\022\r" +
      "\n\005topic\030\001 \003(\t\022*\n\006method\030\002 \001(\0162\032.app.List" +
      "enTopicReq.Method\"(\n\006Method\022\r\n\tSubscribe" +
      "\020\000\022\017\n\013UnSubscribe\020\001\"/\n\020ListenTopicReply\022" +
      "\r\n\005topic\030\001 \001(\t\022\014\n\004data\030\002 \001(\t\"Z\n\017GetImMes" +
      "sageReq\022\017\n\007groupId\030\001 \001(\004\022\017\n\007ownerId\030\002 \001(" +
      "\004\022\017\n\007channel\030\003 \003(\t\022\024\n\014targetUserid\030\004 \001(\004" +
      "\"\325\003\n\tImMessage\022\017\n\007groupId\030\001 \001(\004\022\017\n\007owner" +
      "Id\030\002 \001(\004\022\020\n\010sendTime\030\003 \001(\004\022\r\n\005msgId\030\004 \001(" +
      "\004\022\023\n\013clientMsgId\030\005 \001(\t\022\017\n\007msgType\030\006 \001(\t\022" +
      "#\n\nimgContent\030\007 \001(\0132\017.app.ImgContent\022\'\n\014" +
      "videoContent\030\010 \001(\0132\021.app.VideoContent\022%\n" +
      "\013textContent\030\t \001(\0132\020.app.TextContent\022\'\n\014" +
      "audioContent\030\n \001(\0132\021.app.AudioContent\022+\n" +
      "\016ccVideoContent\030\013 \001(\0132\023.app.CCVideoConte" +
      "nt\022-\n\017questionContent\030\014 \001(\0132\024.app.Questi" +
      "onContent\022\037\n\006sender\030\r \001(\0132\017.app.SenderIn" +
      "fo\022\022\n\nreplyMsgId\030\016 \001(\004\022 \n\010replyMsg\030\017 \001(\013" +
      "2\016.app.ImMessage\022\016\n\006status\030\020 \001(\005\"8\n\nImgC" +
      "ontent\022\013\n\003url\030\001 \001(\t\022\016\n\006height\030\002 \001(\r\022\r\n\005w" +
      "idth\030\003 \001(\r\"L\n\014VideoContent\022\013\n\003url\030\001 \001(\t\022" +
      "\016\n\006height\030\002 \001(\t\022\r\n\005width\030\003 \001(\t\022\020\n\010durati" +
      "on\030\004 \001(\004\"\033\n\013TextContent\022\014\n\004text\030\001 \001(\t\"-\n" +
      "\014AudioContent\022\013\n\003url\030\001 \001(\t\022\020\n\010duration\030\002" +
      " \001(\004\"\241\001\n\016CCVideoContent\022\017\n\007videoId\030\001 \001(\t" +
      "\022\022\n\nvideoTitle\030\002 \001(\t\022\025\n\rvideoDescribe\030\003 " +
      "\001(\t\022\020\n\010duration\030\004 \001(\001\022\r\n\005width\030\005 \001(\r\022\016\n\006" +
      "height\030\006 \001(\r\022\"\n\032imgPreviewRemoteStorageU" +
      "rl\030\007 \001(\t\"\351\001\n\017QuestionContent\022%\n\013textCont" +
      "ent\030\001 \001(\0132\020.app.TextContent\022\026\n\016questionS" +
      "tatus\030\002 \001(\r\022\022\n\nquestionId\030\003 \001(\004\022\r\n\005spark" +
      "\030\004 \001(\004\022\017\n\007diamond\030\005 \001(\004\022\021\n\tpublished\030\006 \001" +
      "(\010\022\020\n\010sendTime\030\007 \001(\004\022\022\n\nexpireTime\030\010 \001(\004" +
      "\022\025\n\ranswerMsgType\030\t \001(\t\022\023\n\013contentType\030\n" +
      " \001(\t\"`\n\nSenderInfo\022\020\n\010senderId\030\001 \001(\004\022\022\n\n" +
      "senderName\030\002 \001(\t\022\024\n\014senderAvatar\030\003 \001(\t\022\026" +
      "\n\016senderPlatform\030\004 \001(\t\"Z\n\017LeaveImGroupRe" +
      "q\022\017\n\007groupId\030\001 \001(\004\022\017\n\007ownerId\030\002 \001(\004\022\017\n\007c" +
      "hannel\030\003 \003(\t\022\024\n\014targetUserid\030\004 \001(\004\"$\n\021Le" +
      "aveImGroupReply\022\017\n\007success\030\001 \001(\010\"\t\n\007Ping" +
      "Req\"\006\n\004Pong\"\232\001\n\022LiveRoomMessageReq\022&\n\002op" +
      "\030\001 \001(\0162\032.app.LiveRoomMessageReq.Op\022\016\n\006ro" +
      "omId\030\002 \001(\005\022\016\n\006liveId\030\003 \001(\003\022\021\n\tliverIsMe\030" +
      "\004 \001(\010\022\016\n\006userId\030\005 \001(\004\"\031\n\002Op\022\010\n\004JOIN\020\000\022\t\n" +
      "\005LEAVE\020\001\"X\n\024LiveRoomMessageReply\022\016\n\006room" +
      "Id\030\001 \001(\005\022\016\n\006liveId\030\002 \001(\003\022\017\n\007msgType\030\003 \001(" +
      "\t\022\017\n\007content\030\004 \001(\t2\267\002\n\006MsgApi\022C\n\017ListenT" +
      "opicData\022\023.app.ListenTopicReq\032\025.app.List" +
      "enTopicReply\"\000(\0010\001\0228\n\014GetImMessage\022\024.app" +
      ".GetImMessageReq\032\016.app.ImMessage\"\0000\001\022>\n\014" +
      "LeaveImGroup\022\024.app.LeaveImGroupReq\032\026.app" +
      ".LeaveImGroupReply\"\000\022K\n\017LiveRoomMessage\022" +
      "\027.app.LiveRoomMessageReq\032\031.app.LiveRoomM" +
      "essageReply\"\000(\0010\001\022!\n\004Ping\022\014.app.PingReq\032" +
      "\t.app.Pong\"\000B\033\n\rcom.zj.im.genB\010IMProtocP" +
      "\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_app_ListenTopicReq_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_app_ListenTopicReq_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_ListenTopicReq_descriptor,
        new java.lang.String[] { "Topic", "Method", });
    internal_static_app_ListenTopicReply_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_app_ListenTopicReply_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_ListenTopicReply_descriptor,
        new java.lang.String[] { "Topic", "Data", });
    internal_static_app_GetImMessageReq_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_app_GetImMessageReq_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_GetImMessageReq_descriptor,
        new java.lang.String[] { "GroupId", "OwnerId", "Channel", "TargetUserid", });
    internal_static_app_ImMessage_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_app_ImMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_ImMessage_descriptor,
        new java.lang.String[] { "GroupId", "OwnerId", "SendTime", "MsgId", "ClientMsgId", "MsgType", "ImgContent", "VideoContent", "TextContent", "AudioContent", "CcVideoContent", "QuestionContent", "Sender", "ReplyMsgId", "ReplyMsg", "Status", });
    internal_static_app_ImgContent_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_app_ImgContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_ImgContent_descriptor,
        new java.lang.String[] { "Url", "Height", "Width", });
    internal_static_app_VideoContent_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_app_VideoContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_VideoContent_descriptor,
        new java.lang.String[] { "Url", "Height", "Width", "Duration", });
    internal_static_app_TextContent_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_app_TextContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_TextContent_descriptor,
        new java.lang.String[] { "Text", });
    internal_static_app_AudioContent_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_app_AudioContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_AudioContent_descriptor,
        new java.lang.String[] { "Url", "Duration", });
    internal_static_app_CCVideoContent_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_app_CCVideoContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_CCVideoContent_descriptor,
        new java.lang.String[] { "VideoId", "VideoTitle", "VideoDescribe", "Duration", "Width", "Height", "ImgPreviewRemoteStorageUrl", });
    internal_static_app_QuestionContent_descriptor =
      getDescriptor().getMessageTypes().get(9);
    internal_static_app_QuestionContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_QuestionContent_descriptor,
        new java.lang.String[] { "TextContent", "QuestionStatus", "QuestionId", "Spark", "Diamond", "Published", "SendTime", "ExpireTime", "AnswerMsgType", "ContentType", });
    internal_static_app_SenderInfo_descriptor =
      getDescriptor().getMessageTypes().get(10);
    internal_static_app_SenderInfo_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_SenderInfo_descriptor,
        new java.lang.String[] { "SenderId", "SenderName", "SenderAvatar", "SenderPlatform", });
    internal_static_app_LeaveImGroupReq_descriptor =
      getDescriptor().getMessageTypes().get(11);
    internal_static_app_LeaveImGroupReq_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_LeaveImGroupReq_descriptor,
        new java.lang.String[] { "GroupId", "OwnerId", "Channel", "TargetUserid", });
    internal_static_app_LeaveImGroupReply_descriptor =
      getDescriptor().getMessageTypes().get(12);
    internal_static_app_LeaveImGroupReply_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_LeaveImGroupReply_descriptor,
        new java.lang.String[] { "Success", });
    internal_static_app_PingReq_descriptor =
      getDescriptor().getMessageTypes().get(13);
    internal_static_app_PingReq_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_PingReq_descriptor,
        new java.lang.String[] { });
    internal_static_app_Pong_descriptor =
      getDescriptor().getMessageTypes().get(14);
    internal_static_app_Pong_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_Pong_descriptor,
        new java.lang.String[] { });
    internal_static_app_LiveRoomMessageReq_descriptor =
      getDescriptor().getMessageTypes().get(15);
    internal_static_app_LiveRoomMessageReq_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_LiveRoomMessageReq_descriptor,
        new java.lang.String[] { "Op", "RoomId", "LiveId", "LiverIsMe", "UserId", });
    internal_static_app_LiveRoomMessageReply_descriptor =
      getDescriptor().getMessageTypes().get(16);
    internal_static_app_LiveRoomMessageReply_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_app_LiveRoomMessageReply_descriptor,
        new java.lang.String[] { "RoomId", "LiveId", "MsgType", "Content", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
