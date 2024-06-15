-- migrate:up

CREATE INDEX idx_user_session_session_id ON user_session (session_id);
CREATE INDEX idx_user_session_deleted_at_is_null ON user_session (deleted_at_posix) WHERE deleted_at_posix IS NULL;
CREATE INDEX idx_user_session_session_id_deleted_at ON user_session (session_id, deleted_at_posix) WHERE deleted_at_posix IS NULL;

-- migrate:down

DROP INDEX idx_user_session_session_id;
DROP INDEX idx_user_session_deleted_at_is_null;
DROP INDEX idx_user_session_session_id_deleted_at;

