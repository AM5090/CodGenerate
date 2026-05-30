-- Добавил внешний ключ от otp_codes к users
ALTER TABLE otp_codes
DROP CONSTRAINT IF EXISTS fk_otp_codes_user;

ALTER TABLE otp_codes
ADD CONSTRAINT fk_otp_codes_user
FOREIGN KEY (user_id) REFERENCES users(id)
ON DELETE CASCADE;