package com.example.CodGenerate.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Component
public class TelegramService {

  private static final Logger log = LoggerFactory.getLogger(TelegramService.class);
  private final String botToken;
  private final String chatId;
  private final String apiUrl;

  public TelegramService() {
    Properties config = loadConfig();
    this.botToken = config.getProperty("telegram.bot.token");
    this.chatId = config.getProperty("telegram.chat.id");
    this.apiUrl = config.getProperty("telegram.api.url") + botToken + "/sendMessage";
  }

  private Properties loadConfig() {
    try (InputStream input = getClass().getClassLoader().getResourceAsStream("telegram.properties")) {
      Properties props = new Properties();
      props.load(input);
      return props;
    } catch (IOException e) {
      log.error("Failed to load Telegram configuration", e);
      throw new RuntimeException("Failed to load Telegram configuration", e);
    }
  }

  public void sendCode(String username, String code) {
    String message = String.format("%s, your verification code is: %s", username, code);
    String url = String.format("%s?chat_id=%s&text=%s", apiUrl, chatId, urlEncode(message));
    sendTelegramRequest(url);
  }

  private void sendTelegramRequest(String url) {
    HttpClient httpClient = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() != 200) {
        log.error("Telegram API error. Status code: {}, Response: {}", response.statusCode(), response.body());
      } else {
        log.info("Telegram message sent successfully");
      }
    } catch (InterruptedException e) {
      log.error("Interrupted while sending Telegram message: {}", e.getMessage());
      Thread.currentThread().interrupt();
    } catch (IOException e) {
      log.error("IO error while sending Telegram message: {}", e.getMessage());
    }
  }

  private static String urlEncode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}