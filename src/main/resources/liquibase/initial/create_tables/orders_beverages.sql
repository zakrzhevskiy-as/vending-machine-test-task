-- changeset
-- id:20211211-06 author:zakrzhevskiy-as

-- Create Sequence -----------------------------------------------

CREATE SEQUENCE vending_machine.orders_beverages_id_seq START 1;

-- Table Definition ----------------------------------------------
CREATE TABLE vending_machine.orders_beverages (
    id bigint DEFAULT nextval('vending_machine.orders_beverages_id_seq'::regclass) PRIMARY KEY,
    selected_ice boolean,
    beverage_volume_id bigint REFERENCES vending_machine.beverage_volumes(id),
    order_id bigint NOT NULL REFERENCES vending_machine.orders(id),
    created timestamp without time zone default current_timestamp NOT NULL,
    modified timestamp without time zone default current_timestamp NOT NULL
);
