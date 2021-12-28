import {notification} from "antd";
import {notificationPlacement} from "../components/constants";

const client = require('./client');
const rootOrdersUrl = 'api/orders';
const rootBeveragesUrl = 'api/beverages';

export const orders = {
    getOrders: (active) => client({method: 'GET', path: rootOrdersUrl, params: {active}}),
    deleteOrders: (all) => client({method: 'DELETE', path: rootOrdersUrl, params: {"deleteAll": all}}),
    create: () => client({method: 'POST', path: rootOrdersUrl}),
    addBeverage: (id, beverage) => client({method: 'PUT', path: `${rootOrdersUrl}/${id}/beverages`, entity: beverage}),
    submit: (id) => client({method: 'POST', path: `${rootOrdersUrl}/${id}/submit`}),
    removeBeverage: (id) => client({method: 'DELETE', path: `${rootOrdersUrl}/beverages/${id}`}),
    addBalance: (id, amount) => client({method: 'PUT', path: `${rootOrdersUrl}/${id}/add-balance`, params: {'amount': amount}}),
    resetBalance: (id) => client({method: 'PATCH', path: `${rootOrdersUrl}/${id}/reset-balance`}),
    selectIce: (beverageId, value) => client({method: 'PATCH', path: `${rootOrdersUrl}/beverages/${beverageId}/select-ice`, params: {'withIce': value}})
};

export const beverages = {
    getAll: () => client({method: 'GET', path: rootBeveragesUrl}),
    getById: (id) => client({method: 'GET', path: `${rootBeveragesUrl}/${id}`}),
    getBeverageVolumes: (id) => client({method: 'GET', path: `${rootBeveragesUrl}/${id}/volumes`}),
    getAllVolumes: () => client({method: 'GET', path: `${rootBeveragesUrl}/volumes`}),
    getVolume: (id) => client({method: 'GET', path: `${rootBeveragesUrl}/volumes/${id}`})
};

export const info = {
    db: () => client({method: 'GET', path: 'api/app-info/database'}),
    rest: () => client({method: 'GET', path: 'api/app-info/rest'}),
    app: () => client({method: 'GET', path: 'api/app-info'})
};

export const logout = () => client({method: 'POST', path: 'logout'});

export function showErrorMessage(error) {
    notification.error({
        message: `${error.entity.status} ${error.entity.error}`,
        description: error.entity.message,
        placement: notificationPlacement
    });
}
