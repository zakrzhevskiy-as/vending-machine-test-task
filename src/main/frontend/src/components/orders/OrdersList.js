import React, {Component} from "react";
import {orders, showErrorMessage} from "../../api/endpoints";
import {Button, Col, Divider, Modal, Popconfirm, Row, Space} from "antd";
import Title from "antd/es/typography/Title";
import DeleteOutlined from "@ant-design/icons/es/icons/DeleteOutlined";
import ExclamationCircleOutlined from "@ant-design/icons/es/icons/ExclamationCircleOutlined";
import Icon from "@ant-design/icons/lib";
import logo from "../../assets/images/sber-logo.svg";

export default class OrdersList extends Component {

    state = {
        showingBill: false,
        order: undefined
    };

    showBill = (order) => {
        this.setState({showingBill: true, order: order});
    };

    deleteOrders = () => {
        this.setState({loading: true});
        orders.clearFinishedOrders()
            .then(
                () => this.setState({orders: []}),
                error => showErrorMessage(error)
            ).done(this.props.rerender);
    };

    render() {
        return (
            <Space direction="vertical">
                <Row justify="space-between">
                    <Col flex="auto">
                        <Title level={3}>Orders history</Title>
                    </Col>
                    <Col flex="87px">
                        <Popconfirm
                            placement="bottomRight"
                            icon={<ExclamationCircleOutlined/>}
                            title="Are you sure delete all orders?"
                            onConfirm={this.deleteOrders}
                            okText="Yes"
                            okType="danger"
                            cancelText="No"
                        >
                            <Button type="text"
                                    disabled={this.props.orders.length === 0}>Clear <DeleteOutlined/></Button>
                        </Popconfirm>
                    </Col>
                </Row>
                <Divider style={{marginTop: 0}}/>
                {
                    this.props.orders.map(order =>
                        <OrderCard key={order.id} order={order} showBill={this.showBill}/>)
                }
                {
                    this.state.order &&
                    <Modal centered
                           closable
                           forceRender
                           maskClosable
                           width={455}
                           visible={this.state.showingBill}
                           footer={null}
                           onCancel={() => this.setState({showingBill: false})}
                    >
                        <Bill order={this.state.order}/>
                    </Modal>
                }
            </Space>

        );
    }
}

class OrderCard extends Component {

    render() {
        let {order} = this.props;

        return order
            ? <>
                <Button className="order-card" type="text" onClick={() => this.props.showBill(order)}>
                    <div>{`Beverages count: ${order.orderBeverages.length}`}</div>
                    <div>{`Total: ${order.totalCost}₽`}</div>
                </Button>
            </>
            : <></>;
    }
}

class Bill extends Component {

    render() {
        let {order} = this.props;

        return (
            <Space className="bill"
                   direction="vertical"
                   align="center"
                   split={<Divider dashed style={{borderColor: "#5B5B5B", borderWidth: "3px 0 0"}}/>}
                   style={{width: 415, padding: "20px 10px", backgroundColor: "#F2F2F2", color: "#5B5B5B !important"}}
            >
                <Space direction="vertical" align="center" style={{width: 395}}>
                    <Icon component={logo} style={{fontSize: 36, color: "#5B5B5B"}}/>
                    <Title level={4}>SberVending</Title>
                    <Title level={5}>{`Чек #1001`}</Title>
                </Space>
                <Space direction="vertical" align="center" style={{width: 395}}>
                    {order.orderBeverages.map(beverage => {
                        return <Row key={beverage.id}>
                            <Col span={beverage.selectedIce ? 8 : 11}>{beverage.beverageType}</Col>
                            {beverage.selectedIce && <Col span={3}>{beverage.selectedIce ? "(Ice)" : ""}</Col>}
                            <Col span={beverage.selectedIce ? 8 : 8}>{`1 * ${beverage.beverageVolume.volume}`}</Col>
                            <Col span={beverage.selectedIce ? 5 : 5}>{beverage.beverageVolume.price}</Col>
                        </Row>;
                    })}
                </Space>
                <Space direction="vertical" align="center" style={{width: 395}}>
                    <Row justify="space-between">
                        <Col>Итог:</Col>
                        <Col>{order.totalCost}</Col>
                    </Row>
                    {
                        order.balance - order.totalCost > 0 &&
                        <Row justify="space-between">
                            <Col>Сдача:</Col>
                            <Col>{order.balance - order.totalCost}</Col>
                        </Row>
                    }
                </Space>
                <Space direction="vertical" align="center" size={0} style={{width: 395}}>
                    <p>Спасибо за покупку!</p>
                    <p>Будем рады видеть вас снова!</p>
                </Space>
            </Space>
        );
    }
}