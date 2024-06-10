-- migrate:up

DROP INDEX idx_user_session_user_id;
DROP TABLE user_session;

CREATE TABLE user_session (
    user_session_id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    created_at_posix BIGINT NOT NULL,
    deleted_at_posix BIGINT
);

CREATE INDEX idx_user_session_user_id ON user_session (user_id);

-- migrate:down


DROP INDEX idx_user_session_user_id;
DROP TABLE user_session;

CREATE TABLE user_session (
    session_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    created_at_posix BIGINT NOT NULL,
    deleted_at_posix BIGINT
);

CREATE INDEX idx_user_session_user_id ON user_session (user_id);