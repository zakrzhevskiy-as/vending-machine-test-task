import React, {Component} from "react";
import {Col, Row, Space} from "antd";

const coinOpeningStyle = {
    height: 60,
    width: 10,
    backgroundColor: "#4F4F4F"
};

export default class Coins extends Component {

    render() {
        return (
            <Row style={{backgroundColor: "#C8C8C8", borderRadius: 5, padding: 10, width: 100, height: 185}}
                 align="middle" justify="center"
            >
                <Col>
                    <Space direction="vertical" size={30}>
                        <div style={coinOpeningStyle}/>
                        <div style={coinOpeningStyle}/>
                    </Space>
                </Col>
            </Row>
        );
    }
}