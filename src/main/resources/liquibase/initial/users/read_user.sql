-- changeset
-- id:20211211-07 author:zakrzhevskiy-as
CREATE USER reader WITH PASSWORD '$uper$ecret';
GRANT USAGE ON SCHEMA vending_machine TO reader;
GRANT SELECT ON vending_machine.orders TO reader;
GRANT SELECT ON vending_machine.users TO reader;
GRANT SELECT ON vending_machine.orders_beverages TO reader;
GRANT SELECT ON vending_machine.beverages TO reader;
GRANT SELECT ON vending_machine.beverage_volumes TO reader;
