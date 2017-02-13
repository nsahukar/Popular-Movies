package android.nsahukar.com.popularmovies.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nikhil on 07/02/17.
 */

public final class MoviesDateUtils {

    public static String getFriendlyDateString(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = formatter.parse(dateStr);
            if (date != null) {
                formatter = new SimpleDateFormat("dd MMM yyyy");
                return formatter.format(date);
            }
        } catch (NullPointerException | ParseException e) {
            e.printStackTrace();
            return null;
        }
        return dateStr;
    }
}
