/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.gamecam.calendar.export.web;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.List;

/**
 *
 * @author Erik Juríček
 */
public class ConnectionManager {

    private String username;
    private String password;
    private String cookie;
    private String downloadedHtml;

    public ConnectionManager() {
    }

    public ConnectionManager(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String downloadFullHtml() {
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS) // follow redirects
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .header("Cookie", this.cookie)
                    .uri(new URI("https://moznosti.dantem.net/options/")).build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            System.out.println("some error");
            return null;
        }
    }

    public boolean tryConnect() {
        try {
            String urlParameters = "username=" + this.username + "&password=" + this.password + "&_do=signInForm-submit";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://dap.dantem.net/auth/login"))
                    .POST(BodyPublishers.ofString(urlParameters))
                    .headers("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 303) {
                List<String> cookies = response.headers().allValues("set-cookie");

                String cookieEnd = cookies.get(cookies.size() - 1);

                if (!cookieEnd.isBlank()) {
                    String[] cookiesSplit = cookieEnd.split(";");
                    this.cookie = cookiesSplit[0];
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("some error");
        }

        return false;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getDownloadedHtml() {
        return downloadedHtml;
    }

    public void setDownloadedHtml(String downloadedHtml) {
        this.downloadedHtml = downloadedHtml;
    }

}
