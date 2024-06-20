package com.save.savetime.util;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class DesktopTest {

    public static void main(String[] args) {
        //SpringApplication.run(DesktopTestApplication.class, args);
        String urlLink = "https://joytk.tistory.com/";

        try {
            Desktop.getDesktop().browse(new URI(urlLink));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
