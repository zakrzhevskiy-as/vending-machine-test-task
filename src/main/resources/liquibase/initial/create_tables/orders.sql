-- changeset
-- id:20211211-05 author:zakrzhevskiy-as

-- Create Sequence -----------------------------------------------

CREATE SEQUENCE vending_machine.orders_id_seq START 1;

-- Table Definition ----------------------------------------------

CREATE TABLE vending_machine.orders (
    id bigint DEFAULT nextval('vending_machine.orders_id_seq'::regclass) PRIMARY KEY,
    user_id bigint NOT NULL REFERENCES vending_machine.users(id),
    created timestamp without time zone default current_timestamp NOT NULL,
    modified timestamp without time zone default current_timestamp NOT NULL
);