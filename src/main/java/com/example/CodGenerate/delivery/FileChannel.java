package com.example.CodGenerate.delivery;

import com.example.CodGenerate.dao.entity.OtpCodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Component
public class FileChannel implements OtpDeliveryChannel {

  private static final Logger logger = LoggerFactory.getLogger(FileChannel.class);

  @Value("${otp.file.directory:./otp-codes}")
  private String fileDirectory;

  @Override
  public boolean send(OtpCodeEntity otpCode, String filename) {
    try {
      Path dir = Paths.get(fileDirectory);
      if (!Files.exists(dir)) {
        Files.createDirectories(dir);
      }

      Path filePath = dir.resolve(filename + ".txt");
      String content = String.format(
              "Operation ID: %s\nCode: %s\nExpires at: %s\nCreated at: %s\n",
              otpCode.getOperationId(),
              otpCode.getCode(),
              otpCode.getExpiresAt(),
              otpCode.getCreatedAt()
      );

      Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      logger.info("[File] Код сохранён в файл: {}", filePath.toAbsolutePath());
      return true;

    } catch (IOException e) {
      logger.error("Ошибка записи в файл: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public String getChannelName() {
    return "file";
  }
}