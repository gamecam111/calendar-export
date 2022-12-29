/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package eu.gamecam.calendar.export;

import eu.gamecam.calendar.export.frames.ExportFrame;
import eu.gamecam.calendar.export.frames.GoogleLoginFrame;
import eu.gamecam.calendar.export.frames.LoginFrame;
import eu.gamecam.calendar.export.google.GoogleCalendar;
import eu.gamecam.calendar.export.web.ConnectionManager;

/**
 *
 * @author Erik Juríček
 */
public class ApplicationMain {

    private static ExportFrame exportFrame;
    private static LoginFrame loginFrame;
    private static GoogleLoginFrame googleLoginFrame;
    private static ConnectionManager cm;
    private static GoogleCalendar gc;

    public static void main(String[] args) {
        googleLoginFrame = new GoogleLoginFrame();
        googleLoginFrame.setTitle("Calendar Export");
        googleLoginFrame.setLocationRelativeTo(null);
        googleLoginFrame.setVisible(true);

        cm = new ConnectionManager();
        gc = new GoogleCalendar();
        googleLoginFrame.setVisible(false);

        //Export Frame
        exportFrame = new ExportFrame();
        exportFrame.setTitle("Calendar Export");
        exportFrame.setGoogleCalendar(gc);
        exportFrame.setLocationRelativeTo(null);

        //Login Frame
        loginFrame = new LoginFrame();
        loginFrame.setConnectionManager(cm);
        loginFrame.setTitle("Calendar Export");
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setCalendarList(gc.listCalendarsNames());
        loginFrame.setGoogleCalendar(gc);

        showLoginFrame();

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            }
        });
    }

    public static void showBasic() {
        exportFrame.setVisible(true);
        exportFrame.setConnectionManager(cm);
        loginFrame.setVisible(false);
    }

    public static void showLoginFrame() {
        loginFrame.setVisible(true);
    }

    public static void hideLoginFrame() {
        loginFrame.setVisible(false);
    }

    public static void showExportFrame() {
        exportFrame.setVisible(true);
    }

    public static void hideExportFrame() {
        exportFrame.setVisible(false);
    }
}
