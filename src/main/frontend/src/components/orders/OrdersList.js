import React, {Component} from "react";
import {orders, showErrorMessage} from "../../api/endpoints";
import {Button, Col, Divider, Modal, Popconfirm, Row, Space, Tooltip} from "antd";
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
        orders.deleteOrders(false)
            .then(
                () => this.setState({orders: []}),
                error => showErrorMessage(error)
            ).done(this.props.rerender);
    };

    render() {
        return (
            <Space direction="vertical">
                <Row>
                    <Col flex="auto">
                        <Title level={3}>История заказов</Title>
                    </Col>
                    <Col flex="46px">
                        <Popconfirm
                            placement="bottomRight"
                            icon={<ExclamationCircleOutlined/>}
                            title="Вы точно хотите очистить историю заказов?"
                            onConfirm={this.deleteOrders}
                            okText="Да"
                            okType="danger"
                            cancelText="Нет"
                        >
                            <Tooltip placement="bottomRight"
                                     title="Очистить историю заказов"
                                     trigger={["hover", "click"]}
                            >
                                <Button type="primary"
                                        danger
                                        disabled={this.props.orders.length === 0}
                                >
                                    <DeleteOutlined/>
                                </Button>
                            </Tooltip>
                        </Popconfirm>
                    </Col>
                </Row>
                <Divider style={{marginTop: 0}}/>
                <div id="orders-list-scrollable">
                    {
                        this.props.orders.map(order =>
                            <OrderCard key={order.id}
                                       order={order}
                                       showBill={this.showBill}
                                       deleteOrder={this.props.deleteOrder}
                            />
                        )
                    }
                </div>
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

        return (
            <Row align="top" gutter={16}>
                <Col flex="auto">
                    <Button className="order-card"
                            type="text"
                            onClick={() => this.props.showBill(order)}
                    >
                        <Space direction="vertical" align="start">
                            <span>{`Заказ #${order.orderNumber}`}</span>
                            <span>{`Дата: ${order.created}`}</span>
                            {/* ОЖИДАЕМАЯ ОШИБКА - отображается некорректная итоговая стоимость */}
                            <span>{`Итого: ${order.balance - order.totalCost}₽`}</span>
                        </Space>
                    </Button>
                </Col>
                <Col flex="24px">
                    <Popconfirm
                        placement="bottomRight"
                        icon={<ExclamationCircleOutlined/>}
                        title={`Вы точно хотите удалить заказ #${order.orderNumber}?`}
                        onConfirm={() => this.props.deleteOrder(order.id)}
                        okText="Да"
                        okType="danger"
                        cancelText="Нет"
                    >
                        <Button type="primary"
                                shape="circle"
                                icon={<DeleteOutlined/>}
                                danger
                                size="small"
                        />
                    </Popconfirm>
                </Col>
            </Row>
        );
    }
}

class Bill extends Component {

    render() {
        let {order} = this.props;

        return (
            <div className="bill">
                <Row justify="center">
                    <Col>
                        <Space direction="vertical" align="center">
                            <Icon component={logo} style={{fontSize: 36, color: "#5B5B5B"}}/>
                            <span style={{fontWeight: 600, fontSize: 28}}>SberVending</span>
                            <span style={{fontWeight: 300, fontSize: 20}}>{`Чек #${order.orderNumber}`}</span>
                        </Space>
                    </Col>
                </Row>
                <Divider dashed style={{borderColor: "#5B5B5B", borderWidth: "3px 0 0"}}/>
                <Row justify="center">
                    <Col style={{width: '100%'}}>
                        {order.orderBeverages.map(beverage => {
                            return <Row key={beverage.id} className="beverage">
                                <Col span={beverage.selectedIce ? 8 : 11}>{beverage.beverageType}</Col>
                                {beverage.selectedIce && <Col span={3}>{beverage.selectedIce ? "(Ice)" : ""}</Col>}
                                <Col span={beverage.selectedIce ? 8 : 8}>{`1 * ${beverage.beverageVolume.volume}`}</Col>
                                <Col span={beverage.selectedIce ? 5 : 5}>{`${beverage.beverageVolume.price}₽`}</Col>
                            </Row>;
                        })}
                    </Col>
                </Row>
                <Divider dashed style={{borderColor: "#5B5B5B", borderWidth: "3px 0 0"}}/>
                <Row justify="center">
                    <Col flex="auto">
                        <Row>
                            <Col span={19}>Итог:</Col>
                            <Col span={5}>{`${order.totalCost}₽`}</Col>
                        </Row>
                        <Row>
                            <Col span={19}>Внесено:</Col>
                            <Col span={5}>{`${order.balance}₽`}</Col>
                        </Row>
                        {
                            order.balance - order.totalCost > 0 &&
                            <Row>
                                <Col span={19}>Сдача:</Col>
                                <Col span={5}>{`${order.balance - order.totalCost}₽`}</Col>
                            </Row>
                        }
                    </Col>
                </Row>
                <Divider dashed style={{borderColor: "#5B5B5B", borderWidth: "3px 0 0"}}/>
                <Row justify="center">
                    <Col>
                        <Space direction="vertical" align="center" size={0}>
                            <span>Спасибо за покупку!</span>
                            <span>Будем рады видеть вас снова!</span>
                        </Space>
                    </Col>
                </Row>
            </div>
        );
    }
}