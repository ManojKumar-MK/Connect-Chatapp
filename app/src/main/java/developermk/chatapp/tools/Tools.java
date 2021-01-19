package developermk.chatapp.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tools {



    public static String toCharacterMonth(int month) {
        if (month == 1) return "Jan";
        else if (month == 2) return "Feb";
        else if (month == 3) return "Mar";
        else if (month == 4) return "Apr";
        else if (month == 5) return "May";
        else if (month == 6) return "Jun";
        else if (month == 7) return "Jul";
        else if (month == 8) return "Aug";
        else if (month == 9) return "Sep";
        else if (month == 10) return "Oct";
        else if (month == 11) return "Nov";
        else return "Dec";
    }

    public static String lastSeenProper(String lastSeenDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
        Date currentDate = new Date();
        String cuurentDateString = dateFormat.format(currentDate);
        Date nw = null;
        Date seen = null;
        try {
            nw = dateFormat.parse(cuurentDateString);
            seen = dateFormat.parse(lastSeenDate);
            long diff = nw.getTime() - seen.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffMinutes = diff / (60 * 1000) % 60;
            if (diffDays > 0) {
                String[] originalDate = lastSeenDate.split(" ");
                return "Last seen " + originalDate[0] + " " + Tools.toCharacterMonth(Integer.parseInt(originalDate[1])) + " " + originalDate[2];
            } else if (diffHours > 0)
                return "Last seen " + diffHours + " hours ago";
            else if (diffMinutes > 0) {
                if (diffMinutes <= 1) {
                    return "Last seen 1 minute ago";
                } else {
                    return "Last seen " + diffMinutes + " minutes ago";
                }
            } else return "Last seen a moment ago";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }


    }

    public static String messageSentDateProper(String sentDate) {
        String properDate = "";
        Calendar cal = Calendar.getInstance();
        Date todayDate = new Date();
        cal.setTime(todayDate);
        String[] date = sentDate.split(" ");
        int todayMonth = cal.get(Calendar.MONTH) + 1;
        int todayDay = cal.get(Calendar.DAY_OF_MONTH);
        if (todayMonth == Integer.parseInt(date[1]) && todayDay == Integer.parseInt(date[0])) {
            properDate = "Today" + " " + date[3] + " " + date[4];
            // 06 11 17 12:28 AM
        } else if (todayMonth == Integer.parseInt(date[1]) && (todayDay - 1) == Integer.parseInt(date[0])) {
            properDate = "Yesterday" + " " + date[3] + " " + date[4];
        } else {
            properDate = date[0] + " " + Tools.toCharacterMonth(Integer.parseInt(date[1])) + " " + date[2] + " " + date[3] + " " + date[4];
        }
        return properDate;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
