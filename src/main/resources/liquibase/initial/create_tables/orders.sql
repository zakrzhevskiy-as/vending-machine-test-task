-- changeset
-- id:20211211-05 author:zakrzhevskiy-as

-- Create Sequence -----------------------------------------------

CREATE SEQUENCE orders_id_seq START 1;

-- Table Definition ----------------------------------------------

CREATE TABLE orders (
    id bigint DEFAULT nextval('orders_id_seq'::regclass) PRIMARY KEY,
    user_id bigint NOT NULL REFERENCES users(id),
    created timestamp without time zone default current_timestamp NOT NULL,
    modified timestamp without time zone default current_timestamp NOT NULL
);