package com.zj.ccIm.core.fecher

enum class FetchMsgChannel(val serializeName: String) {

    OWNER_CLAP_HOUSE("owner_clap_house"),
    OWNER_MESSAGE("owner_message"),
    OWNER_PRIVATE("owner_private"),
    FANS_CLAP_HOUSE("fans_clap_house"),
    FANS_MESSAGE("fans_message"),
    FANS_PRIVATE("fans_private")
}