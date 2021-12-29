import React, {Component} from "react";
import {Button, Modal, Space, Spin} from "antd";
import Payment from "./operating_items/Payment";
import Beverages from "./operating_items/Beverages";
import {orders, showErrorMessage} from "../api/endpoints";
import {spinner} from "./constants";

export default class OperatingContent extends Component {

    state = {
        loading: true,
        creatingOrder: false,
        newOrder: false,
        balance: 0,
        totalCost: 0,
        order: [],
        orderId: undefined,
        orderConfirmed: false,
        processingBeverage: undefined,
        brewing: false,
        progress: 0
    };

    constructor(props) {
        super(props);

        this.add = this.add.bind(this);
        this.resetBalance = this.resetBalance.bind(this);
        this.addBeverageVolume = this.addBeverageVolume.bind(this);
        this.removeBeverageVolume = this.removeBeverageVolume.bind(this);
        this.selectIce = this.selectIce.bind(this);
        this.submitOrder = this.submitOrder.bind(this);
        this.processOrderBeverage = this.processOrderBeverage.bind(this);
        this.updateProgressBar = this.updateProgressBar.bind(this);
    }

    componentDidMount() {
        this.getActiveOrder();
    }

    getActiveOrder = () => {
        this.setState({loading: true});
        orders.getOrders(true)
            .then(
                response => {
                    let order = response.entity;

                    this.setState({
                        order: order.orderBeverages,
                        orderId: order.id,
                        totalCost: order.totalCost,
                        balance: order.balance,
                        orderConfirmed: order.orderBeverages.filter(item => item.status !== 'SELECTED').length > 0
                    });
                },
                error => {
                    if (error.status.code === 404) {
                        this.setState({newOrder: true});
                    } else {
                        showErrorMessage(error);
                    }
                }
            ).done(() => this.setState({loading: false}));
    };

    async add(amount) {
        this.setState({loading: true});
        await orders.addBalance(this.state.orderId, amount)
            .then(
                response => {
                    this.setState({balance: response.entity.balance});
                },
                error => showErrorMessage(error)
            ).done(() => this.setState({loading: false}));
    }

    resetBalance() {
        this.setState({loading: true});
        orders.resetBalance(this.state.orderId)
            .then(
                response => {
                    this.setState({balance: response.entity.balance});
                },
                error => showErrorMessage(error)
            ).done(() => this.setState({loading: false}));
    }

    addBeverageVolume(name, volume) {
        this.setState({loading: true});
        orders.addBeverage(this.state.orderId, {"beverageVolume": {"id": volume.id}})
            .then(
                response => {
                    this.setState(prevState => ({
                        order: [...prevState.order, response.entity],
                        totalCost: prevState.totalCost + volume.price
                    }));
                },
                error => showErrorMessage(error)
            ).done(() => this.setState({loading: false}));
    }

    removeBeverageVolume(index) {
        let order = [...this.state.order];
        let {totalCost} = this.state;

        let orderBeverage = order[index];

        this.setState({loading: true});
        orders.removeBeverage(orderBeverage.id)
            .then(
                () => {
                    totalCost -= orderBeverage.beverageVolume.price;
                    order.splice(index, 1);

                    this.setState({
                        order: order,
                        totalCost: totalCost
                    });
                },
                error => showErrorMessage(error)
            ).done(() => this.setState({loading: false}));
    }

    selectIce(index, withIce) {
        let {order} = this.state;
        let orderBeverage = order[index];

        this.setState({loading: true});
        orders.selectIce(orderBeverage.id, withIce)
            .then(
                () => {
                    orderBeverage['selectedIce'] = withIce;
                    this.setState({order: order});
                },
                error => showErrorMessage(error)
            ).done(() => this.setState({loading: false}));
    }

    submitOrder() {
        this.setState({loading: true});
        orders.submit(this.state.orderId)
            .then(
                response => this.setState({order: response.entity, orderConfirmed: true}),
                error => showErrorMessage(error)
            ).done(() => this.setState({loading: false}));
    }

    processOrderBeverage(beverageId, action) {
        let {order} = this.state;
        let readyCount = order.filter(item => item.status === 'READY').length;
        let takenCount = order.filter(item => item.status === 'TAKEN').length;

        let last = (order.length - (readyCount + takenCount)) === 0;

        if (last) {
            orders.finishOrder(this.state.orderId)
                .then(() => {
                }, error => showErrorMessage(error))
                .done(() => {
                    this.setState({brewing: false, progress: 0});
                    this.props.rerender();
                })
        } else {
            this.setState({processingBeverage: beverageId});
            orders.processOrderBeverage(this.state.orderId, beverageId, action)
                .then(
                    response => this.setState({order: response.entity, orderConfirmed: true}),
                    error => showErrorMessage(error)
                ).done(() => this.setState({processingBeverage: undefined, brewing: false, progress: 0}));
        }

        if (action === 'PROCESS') {
            this.setState({brewing: true});
            Array.from(Array(20).keys()).forEach((item, i) => setTimeout(
                () => this.setState({progress: 100 / 20 * (i + 1)}),
                (i + 1) * 1000,
            ));
        }
    }

    updateProgressBar(progress) {
        this.setState({progress: progress})
    }

    render() {
        return (
            <>
                <Spin spinning={this.state.loading}
                      size="large"
                      indicator={spinner}
                >
                    <Space align="start" size={30} wrap>
                        <Payment orderSize={this.state.order.length}
                                 balance={this.state.balance}
                                 totalCost={this.state.totalCost}
                                 add={this.add}
                                 reset={this.resetBalance}
                                 orderConfirmed={this.state.orderConfirmed}
                        />
                        <Beverages balance={this.state.balance}
                                   totalCost={this.state.totalCost}
                                   orderId={this.state.orderId}
                                   order={this.state.order}
                                   orderConfirmed={this.state.orderConfirmed}
                                   selectIce={this.selectIce}
                                   removeBeverageVolume={this.removeBeverageVolume}
                                   addBeverageVolume={this.addBeverageVolume}
                                   submit={this.submitOrder}
                                   processBeverage={this.processOrderBeverage}
                                   processingBeverage={this.state.processingBeverage}
                                   brewing={this.state.brewing}
                                   progress={this.state.progress}
                        />
                    </Space>
                </Spin>
                <Modal title="No active order found"
                       centered
                       closable={false}
                       visible={this.state.newOrder}
                       afterClose={this.props.rerender}
                       footer={[
                           <Button key="create"
                                   type="primary"
                                   loading={this.state.creatingOrder}
                                   onClick={() => {
                                       this.setState({creatingOrder: true});
                                       orders.create()
                                           .then(
                                               response => this.setState({orderId: response.entity.id}),
                                               error => showErrorMessage(error)
                                           )
                                           .done(() => this.setState({creatingOrder: false, newOrder: false}));
                                   }}
                           >
                               New order
                           </Button>
                       ]}
                >
                    In order to proceed you need to create new order
                </Modal>
            </>
        );
    }
}