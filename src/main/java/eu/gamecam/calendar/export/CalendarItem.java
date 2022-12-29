/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.gamecam.calendar.export;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Erik Juríček
 */
public class CalendarItem {

    private String htmlString;
    private String date;
    private String timeStart;
    private String timeEnd;
    private String description;

    private String googleStartTime;
    private String googleEndTime;

    public CalendarItem() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHtmlString() {
        return htmlString;
    }

    public void setHtmlString(String htmlString) {
        this.htmlString = htmlString.trim().replaceAll("\\s{2,}", " ");
    }

    public void calcEventInfo() {
        this.timeEnd = getEndTime();
        this.timeStart = getStartTimeTravel();
        this.description = getOthers();
        fillGoogleTimes();
    }

    public String getStartTimeTravel() {
        try {
            String regex = "<td class=\"green inv .+ start\" .+?>(.|\n)*?</td>";

            Pattern p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
            Matcher m = p.matcher(htmlString);
            m.find();

            String something = m.group(0);

            if (!something.isBlank()) {
                String regexTime = "<span class=\"withTravel.+\" .+?>(.*?)</span>";
                Pattern p1 = Pattern.compile(regexTime, Pattern.MULTILINE | Pattern.DOTALL);
                Matcher m1 = p1.matcher(m.group(0));
                m1.find();

                if (m1.group(1).isBlank() || m1.group(1).isEmpty()) {
                    String regexTime1 = "<span class=\"withoutTravel.+\" .+?>(.*?)</span>";
                    Pattern p2 = Pattern.compile(regexTime1, Pattern.MULTILINE | Pattern.DOTALL);
                    Matcher m2 = p2.matcher(m.group(0));
                    m2.find();
                    return m2.group(1).substring(1);
                } else {
                    return m1.group(1).substring(1);
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String getEndTime() {
        try {
            String regex = "<td class=\"green inv .+ end\" .+?>(.|\n)*?</td>";

            Pattern p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
            Matcher m = p.matcher(htmlString);
            m.find();

            String something = m.group(0);

            if (!something.isBlank()) {
                String regexTime = "<span class=\"withTravel.+\" .+?>(.*?)</span>";
                Pattern p1 = Pattern.compile(regexTime, Pattern.MULTILINE | Pattern.DOTALL);
                Matcher m1 = p1.matcher(m.group(0));
                m1.find();

                if (m1.group(1).isBlank() || m1.group(1).isEmpty()) {
                    String regexTime1 = "<span class=\"withoutTravel.+\" .+?>(.*?)</span>";
                    Pattern p2 = Pattern.compile(regexTime1, Pattern.MULTILINE | Pattern.DOTALL);
                    Matcher m2 = p2.matcher(m.group(0));
                    m2.find();
                    return m2.group(1);
                } else {
                    return m1.group(1);
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String getOthers() {
        try {
            String regex = "<td class=\"green inv .+?\" .+?>(.*?)</td>";

            Pattern p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
            Matcher m = p.matcher(htmlString);
            m.find();

            int count = 0;
            StringBuilder sb = new StringBuilder();

            while (m.find()) {
                if (count != 0 && count != 1 && count != 4 && count != 5 && count != 0 && count != 6 && count != 7) {
                    sb.append(m.group(1) + " - ");
                }
                count++;
            }

            sb.append(", TimeEnd: " + timeEnd);
            return sb.toString();

        } catch (Exception e) {
            return null;
        }
    }

    public void fillGoogleTimes() {
        if (timeEnd != null && timeStart != null && date != null) {
            String[] dateSplit = date.split("\\.");
            String[] timeStartSplit = timeStart.split(":");
            String[] timeEndSplit = timeEnd.split(":");

            googleStartTime = dateSplit[2] + "-" + dateSplit[1] + "-" + dateSplit[0] + "T" + timeStartSplit[0] + ":" + timeStartSplit[1] + ":00+01:00";
            googleEndTime = dateSplit[2] + "-" + dateSplit[1] + "-" + dateSplit[0] + "T23:59:00+01:00";
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getGoogleStartTime() {
        return googleStartTime;
    }

    public void setGoogleStartTime(String googleStartTime) {
        this.googleStartTime = googleStartTime;
    }

    public String getGoogleEndTime() {
        return googleEndTime;
    }

    public void setGoogleEndTime(String googleEndTime) {
        this.googleEndTime = googleEndTime;
    }

}
