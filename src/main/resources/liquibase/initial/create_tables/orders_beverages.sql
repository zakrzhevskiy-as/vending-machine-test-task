-- changeset
-- id:20211211-06 author:zakrzhevskiy-as

-- Create Sequence -----------------------------------------------

CREATE SEQUENCE orders_beverages_id_seq START 1;

-- Table Definition ----------------------------------------------
CREATE TABLE orders_beverages (
    id bigint DEFAULT nextval('orders_beverages_id_seq'::regclass) PRIMARY KEY,
    selected_ice boolean,
    beverage_volume_id bigint REFERENCES beverage_volumes(id),
    order_id bigint NOT NULL REFERENCES orders(id),
    created timestamp without time zone default current_timestamp NOT NULL,
    modified timestamp without time zone default current_timestamp NOT NULL
);
