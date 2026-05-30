-- Удаляем существующие записи с id != 1 (если есть)
DELETE FROM otp_config WHERE id <> 1;

-- Создаём или обновляем запись с id = 1
INSERT INTO otp_config (id, code_length, code_expiry_minutes, max_attempts, is_active)
VALUES (1, 6, 5, 3, true)
ON CONFLICT (id) DO UPDATE SET
    code_length = EXCLUDED.code_length,
    code_expiry_minutes = EXCLUDED.code_expiry_minutes,
    max_attempts = EXCLUDED.max_attempts,
    is_active = EXCLUDED.is_active;

-- Добавляем CHECK-ограничение
ALTER TABLE otp_config DROP CONSTRAINT IF EXISTS single_config_check;
ALTER TABLE otp_config ADD CONSTRAINT single_config_check CHECK (id = 1);

-- Сбрасываем последовательность (чтобы следующий id был 2, но CHECK его не пропустит)
SELECT setval('otp_config_id_seq', 1, false);