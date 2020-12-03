package org.schabi.newpipe.util.stream_dialog

import org.schabi.newpipe.extractor.stream.StreamInfoItem

object StreamDialogEntryObject {

    var title: String = "EMPTY"
    var additionalDetail: String? = null
    var infoItem: StreamInfoItem? = null
    var actions: ArrayList<StreamDialogEntry> = arrayListOf()

    fun clear() {
        infoItem = null
        actions.clear()
    }
}
