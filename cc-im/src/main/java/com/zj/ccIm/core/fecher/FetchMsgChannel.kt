package com.zj.ccIm.core.fecher

enum class FetchMsgChannel(val serializeName: String, val classification: Int) {

    /**
     * @property serializeName Subscription type agreed with the server
     * @property classification The type of Badge to be cleared is tab-SessionLastMsgInfo
     * */

    OWNER_CLAP_HOUSE("owner_clap_house", 0),
    OWNER_MESSAGE("owner_message", 0),
    FANS_CLAP_HOUSE("fans_clap_house", 0),
    FANS_MESSAGE("fans_message", 0),
    FANS_PRIVATE("fans_private", 1),
    OWNER_PRIVATE("owner_private", 2);

}