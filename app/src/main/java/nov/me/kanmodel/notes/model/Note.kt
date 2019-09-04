package nov.me.kanmodel.notes.model

/**
 * 存储每个便签内容的数据类
 * Created by KanModel on 2017/11/26.
 */

class Note
/**
 * @param title   便签标题
 * @param content 便签内容
 * @param logTime 便签时间
 * @param time    时间戳
 */
(var title: String?, var content: String?, var logTime: String?, var time: Long, var lastChangedTime: Long) {
    var dstTime: Long = 0

    constructor(title: String, content: String, logTime: String, time: Long, lastChangedTime: Long, dstTime: Long) : this(title, content, logTime, time, lastChangedTime) {
        this.dstTime = dstTime
    }

    constructor(title: String, content: String, time: Long) : this(title, content, "", time, 0) {}
}
