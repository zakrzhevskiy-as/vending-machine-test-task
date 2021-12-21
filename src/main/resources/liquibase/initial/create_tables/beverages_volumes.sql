-- changeset
-- id:20211211-03 author:zakrzhevskiy-as

-- Create Sequence -----------------------------------------------

CREATE SEQUENCE beverage_volumes_id_seq START 1;

-- Table Definition ----------------------------------------------

CREATE TABLE beverage_volumes (
    id bigint DEFAULT nextval('beverage_volumes_id_seq'::regclass) PRIMARY KEY,
    price integer NOT NULL,
    volume double precision NOT NULL,
    beverage_id bigint REFERENCES vending_machine.beverages(id),
    created timestamp without time zone default current_timestamp NOT NULL,
    modified timestamp without time zone default current_timestamp NOT NULL
);
