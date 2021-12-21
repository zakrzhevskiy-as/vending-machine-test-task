import React, {Component} from "react";
import {Col, Row} from "antd";
import OrdersList from "./orders/OrdersList";
import OperatingContent from "./OperatingContent";

const columnStyle = {backgroundColor: '#e6e6e6', borderRadius: 5, padding: '15px 10px 5px 10px'};

export default class MainContent extends Component {

    state = {
        key: Math.random()
    };

    constructor(props) {
        super(props);

        this.rerender = this.rerender.bind(this);
    }

    rerender() {
        this.setState({key: Math.random()});
    }

    render() {
        return (
            <Row gutter={[10]} key={this.state.key}>
                <Col flex="auto" style={columnStyle}>
                    <OperatingContent rerender={this.rerender}/>
                </Col>
                <Col span={5} style={columnStyle}>
                    <OrdersList rerender={this.rerender}/>
                </Col>
            </Row>
        );
    }


}