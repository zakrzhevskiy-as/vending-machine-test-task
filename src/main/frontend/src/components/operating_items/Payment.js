import React, {Component} from "react";
import {Button, Col, Row, Space} from "antd";
import Cash from "./payment/Cash";
import Coins from "./payment/Coins";
import PayPass from "./payment/PayPass";

const buttonWidth = 100;
const cash = [50, 100, 200];
const coins = [1, 2, 5, 10];

export default class Payment extends Component {

    render() {
        return (
            <Space align="center" direction="vertical">
                <Row>
                    <Col>
                        <Space direction="vertical" align="center">
                            {cash.map((cost, index) => {
                                return <Button key={index}
                                               type="primary"
                                               shape="round"
                                               size="large"
                                               disabled={this.props.orderConfirmed}
                                               style={{width: buttonWidth}}
                                               onClick={() => this.props.add(cost)}
                                >
                                    {cost}₽
                                </Button>
                            })}
                            <Button type="primary"
                                    shape="round"
                                    size="large"
                                    danger
                                    disabled={this.props.orderConfirmed}
                                    style={{width: buttonWidth}}
                                    onClick={() => this.props.reset()}
                            >
                                Возврат
                            </Button>
                        </Space>
                    </Col>
                    <Col>
                        <Cash balance={this.props.balance} totalCost={this.props.totalCost}/>
                    </Col>
                    <Col>
                        <Coins/>
                    </Col>
                    <Col>
                        <Space direction="vertical" align="center">
                            {coins.map((cost, index) => {
                                return <Button key={index}
                                               type="primary"
                                               shape="round"
                                               size="large"
                                               disabled={this.props.orderConfirmed}
                                               style={{width: buttonWidth}}
                                               onClick={() => this.props.add(cost)}
                                >
                                    {cost}₽
                                </Button>
                            })}
                        </Space>
                    </Col>
                </Row>
                <PayPass orderSize={this.props.orderSize}
                         balance={this.props.balance}
                         totalCost={this.props.totalCost}
                         payPass={this.props.add}
                />
            </Space>
        );
    }
}