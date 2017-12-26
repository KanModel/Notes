package nov.me.kanmodel.notes.utils;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by KanModel on 2017/12/26.
 */

public class DateFormats {
    @NonNull
    public static SimpleDateFormat fromSkeleton(@NonNull String skeleton, @NonNull Locale locale) {
        SimpleDateFormat df = new SimpleDateFormat(skeleton, locale);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df;
    }

    public static SimpleDateFormat getBackupDateFormat() {
        return fromSkeleton("yyyy-MM-dd HHmmss", Locale.US);
    }

    public static SimpleDateFormat getCSVDateFormat() {
        return fromSkeleton("yyyy-MM-dd", Locale.US);
    }
}
