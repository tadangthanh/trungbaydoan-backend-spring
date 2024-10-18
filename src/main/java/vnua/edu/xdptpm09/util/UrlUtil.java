
package vnua.edu.xdptpm09.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class UrlUtil {
    public UrlUtil() {
    }

    public boolean isUrlExists(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();
            return 200 <= responseCode && responseCode <= 399;
        } catch (IOException var5) {
            return false;
        }
    }
}
