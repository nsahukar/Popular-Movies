package android.nsahukar.com.popularmovies.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nikhil on 07/02/17.
 */

public final class MoviesDateUtils {

    public static Date getDateFromString(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(dateStr);
        } catch (NullPointerException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFriendlyDateString(Date date) {
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
            return formatter.format(date);
        }
        return "-- --- ----";
    }
}
