package nov.me.kanmodel.notes.utils

import android.content.Context
import android.content.Intent
import android.support.v4.content.FileProvider.getUriForFile
import nov.me.kanmodel.notes.activity.SettingsActivity
import java.io.*

/**
 * Created by KanModel on 2017/12/26.
 * 文件操作相关函数集
 */
object FileUtils {
    @Throws(IOException::class)
    private fun copy(src: File, dst: File) {
        val inStream = FileInputStream(src)
        val outStream = FileOutputStream(dst)
        copy(inStream, outStream)
    }

    @Throws(IOException::class)
    private fun copy(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var numBytes: Int = `in`.read(buffer)

        while (numBytes != -1){
            out.write(buffer, 0, numBytes)
            numBytes = `in`.read(buffer)
        }
    }

    @Throws(IOException::class)
    fun saveDatabaseCopy(context: Context, dir: File): String {
        val filename = String.format("%s/Note Backup %s.db", dir.absolutePath,
                TimeAid.backupDateFormat.format(TimeAid.nowTime))

        val db = getDatabaseFile(context)
        val dbCopy = File(filename)
        copy(db, dbCopy)

        return dbCopy.absolutePath
    }

    private fun getDatabaseFile(context: Context): File {
        val databaseFilename = "Note.db"
        val root = context.filesDir.path
        val filename = String.format("%s/../databases/%s", root, databaseFilename)

        return File(filename)
    }

    fun showSendFileScreen(archiveFilename: String, activity: SettingsActivity) {
        val file = File(archiveFilename)
        val fileUri = getUriForFile(activity, "nov.me.kanmodel.notes", file)

        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "application/zip"
        intent.putExtra(Intent.EXTRA_STREAM, fileUri)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        activity.startActivity(intent)
    }
}
