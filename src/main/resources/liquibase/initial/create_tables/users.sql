-- changeset
-- id:20211211-04 author:zakrzhevskiy-as

-- Create Sequence -----------------------------------------------

CREATE SEQUENCE vending_machine.users_id_seq START 1;

-- Table Definition ----------------------------------------------

CREATE TABLE vending_machine.users (
    id bigint DEFAULT nextval('vending_machine.users_id_seq'::regclass) PRIMARY KEY,
    password character varying(255) NOT NULL,
    username character varying(32) NOT NULL UNIQUE,
    created timestamp without time zone default current_timestamp NOT NULL,
    modified timestamp without time zone default current_timestamp NOT NULL
);

-- Indices -------------------------------------------------------

CREATE UNIQUE INDEX uk_username ON vending_machine.users(username text_ops);
