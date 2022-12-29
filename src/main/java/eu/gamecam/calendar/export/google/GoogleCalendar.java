/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.gamecam.calendar.export.google;

/**
 *
 * @author Erik Juríček
 */
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import eu.gamecam.calendar.export.CalendarItem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoogleCalendar {

    private String defaultCalendarName;
    private String defaultCalendarId;

    private Calendar service;

    private static final String APPLICATION_NAME = "Calendar Export";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public GoogleCalendar() {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

        } catch (Exception e) {
            System.out.println("Google service start error");
        }
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleCalendar.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets
                = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
    }

    public List<String> listCalendarsNames() {
        try {
            List<String> names = new ArrayList<>();
            CalendarList calendarList = service.calendarList().list().execute();
            for (CalendarListEntry item : calendarList.getItems()) {
                if (!item.getAccessRole().equals("reader")) {
                    names.add(item.getSummary());
                }
            }
            return names;
        } catch (Exception e) {
            System.out.println("Google - listCalendarsNames error");
        }
        return null;
    }

    public String getCalendarId(String calendarName) {
        try {
            CalendarList calendarList = service.calendarList().list().execute();
            for (CalendarListEntry item : calendarList.getItems()) {
                if (item.getSummary().equals(calendarName)) {
                    return item.getId();
                }
            }
        } catch (Exception e) {
            System.out.println("Google - getCalendarId error");
        }
        return null;
    }

    public void addToCalendar(CalendarItem item) {
        try {

            Event event = new Event()
                    .setSummary("Dantem - " + item.getTimeStart() + "-" + item.getEndTime())
                    .setDescription(item.getDescription());

            DateTime startDateTime = new DateTime(item.getGoogleStartTime());
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Europe/Prague");
            event.setStart(start);

            DateTime endDateTime = new DateTime(item.getGoogleEndTime());
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Europe/Prague");
            event.setEnd(end);

            event = service.events().insert(defaultCalendarId, event).execute();
            System.out.printf("Event created: %s\n", event.getHtmlLink());
        } catch (Exception e) {
            System.out.println("chyba");
            e.printStackTrace();
        }
    }

    public void deleteAllInCalendar() {
        try {
            Events events = service.events().list(defaultCalendarId).execute();
            for (Event item : events.getItems()) {
                service.events().delete(defaultCalendarId, item.getId()).execute();
            }
        } catch (Exception e) {
            System.out.println("chyba");
            e.printStackTrace();
        }
    }

//    public static void main(String... args) throws IOException, GeneralSecurityException {
//
//
//        // List the next 10 events from the primary calendar.
//        DateTime now = new DateTime(System.currentTimeMillis());
//        Events events = service.events().list("primary")
//                .setMaxResults(10)
//                .setTimeMin(now)
//                .setOrderBy("startTime")
//                .setSingleEvents(true)
//                .execute();
//        List<Event> items = events.getItems();
//        if (items.isEmpty()) {
//            System.out.println("No upcoming events found.");
//        } else {
//            System.out.println("Upcoming events");
//            for (Event event : items) {
//                DateTime start = event.getStart().getDateTime();
//                if (start == null) {
//                    start = event.getStart().getDate();
//                }
//                System.out.printf("%s (%s)\n", event.getSummary(), start);
//            }
//        }
//    }
    public String getDefaultCalendarName() {
        return defaultCalendarName;
    }

    public void setDefaultCalendarName(String defaultCalendarName) {
        this.defaultCalendarName = defaultCalendarName;
        this.defaultCalendarId = getCalendarId(defaultCalendarName);
    }

    public String getDefaultCalendarId() {
        return defaultCalendarId;
    }

    public void setDefaultCalendarId(String defaultCalendarId) {
        this.defaultCalendarId = defaultCalendarId;
    }

}
