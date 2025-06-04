-- Создание таблицы пользователей
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(50) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE user_devices (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    device_id VARCHAR(50) REFERENCES devices(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, device_id)
);

-- Создание таблицы устройств
CREATE TABLE devices (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    status BOOLEAN DEFAULT false,
    type VARCHAR(50) NOT NULL,
    last_update TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    room_id VARCHAR(50) REFERENCES rooms(id) ON DELETE SET NULL,
    relay_id INTEGER,
    mac VARCHAR(17)
);

-- Создание таблицы комнат
CREATE TABLE rooms (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    color VARCHAR(50),
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE
);

-- Создание таблицы сценариев
CREATE TABLE scenarios (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    color VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE
);

-- Создание таблицы связи сценариев с устройствами
CREATE TABLE scenario_devices (
    scenario_id VARCHAR(50) REFERENCES scenarios(id) ON DELETE CASCADE,
    device_id VARCHAR(50) REFERENCES devices(id) ON DELETE CASCADE,
    PRIMARY KEY (scenario_id, device_id)
);