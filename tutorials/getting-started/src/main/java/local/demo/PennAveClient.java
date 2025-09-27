package local.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public class PennAveClient {

  // Hard-coded credentials for demo purposes
  private static final String DATA_FILE = "sample_data.json";
  private static final String HOST = "api.pennaveiam.local";
  private static final String USERNAME = "admin";
  private static final String PASSWORD = "Password1";

  @Getter
  private boolean connected = false;

  private final String host;

  private final String username;

  private final String password;

  public PennAveClient(String host, String username, String password) {
    this.host = host;
    this.username = username;
    this.password = password;
  }

  public void connect() {
    if (!StringUtils.equals(host, HOST)) {
      throw new IllegalArgumentException("Unknown host");
    }
    if (!StringUtils.equals(username, USERNAME)) {
      throw new IllegalArgumentException("Invalid username");
    }
    if (!StringUtils.equals(password, PASSWORD)) {
      throw new IllegalArgumentException("Invalid password");
    }

    connected = true;
  }

  public String getAllUsers() {
    return loadData().toString();
  }

  public @Nullable String getUser(final String username) {
    assert StringUtils.isNotBlank(username);
    JsonNode users = loadData();
    for (JsonNode user : users) {
      if (StringUtils.equalsIgnoreCase(username, user.get("username").asText(StringUtils.EMPTY))) {
        return user.toString();
      }
    }
    return null;
  }

  private JsonNode loadData() {
    if (!connected) {
      throw new IllegalStateException("Not connected");
    }

    try (InputStream inputStream = PennAveClient.class.getClassLoader().getResourceAsStream(DATA_FILE)) {
      return new ObjectMapper().readTree(inputStream);
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to load sample data.");
    }
  }
}
