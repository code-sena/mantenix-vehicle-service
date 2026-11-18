-- V1: vehicles table
-- Owner of this schema: vehicle-service (ADR-004). Never edit this file once applied;
-- create a new migration (V2__...) instead.

CREATE TABLE vehicles (
    id          UUID         PRIMARY KEY,
    owner_id    UUID         NOT NULL,             -- user id from auth-service (no FK: another database)
    plate       VARCHAR(10)  NOT NULL,
    type        VARCHAR(20)  NOT NULL,
    brand       VARCHAR(50)  NOT NULL,
    model       VARCHAR(50)  NOT NULL,
    model_year  INTEGER      NOT NULL,
    mileage     INTEGER      NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_vehicles_plate UNIQUE (plate),
    CONSTRAINT ck_vehicles_type CHECK (type IN ('CAR', 'MOTORCYCLE')),
    CONSTRAINT ck_vehicles_mileage CHECK (mileage >= 0)
);

CREATE INDEX idx_vehicles_owner_id ON vehicles (owner_id);
