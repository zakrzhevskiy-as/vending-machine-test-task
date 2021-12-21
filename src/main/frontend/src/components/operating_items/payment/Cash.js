import React, {Component} from "react";
import {Col, Row, Space} from "antd";

export default class Cash extends Component {

    render() {
        return (
            <div id="balance" style={{
                backgroundColor: "#C8C8C8", borderRadius: 10, color: "white",
                padding: 10, width: 270, fontSize: 22, fontFamily: "VideoTerminalScreen, serif"
            }}>
                <Space style={{backgroundColor: "#92A88A", borderRadius: 10, padding: 10, marginBottom: 10, height: 95}}
                       direction="vertical" size={0}
                >
                    <Row justify="space-between">
                        <Col span={14} style={{marginBottom: 0}}>Balance:</Col>
                        <Col span={10} style={{marginBottom: 0}}>{this.props.balance} p.</Col>
                    </Row>
                    <Row justify="space-between">
                        <Col span={14} style={{marginTop: 0}}>Total cost:</Col>
                        <Col span={10} style={{marginTop: 0}}>{this.props.totalCost} p.</Col>
                    </Row>
                </Space>
                <div style={{backgroundColor: "#333333", borderRadius: 10, padding: 10, height: 60}}>
                    <Row align="middle" justify="center">
                        <Col span={24}>
                            <div style={{
                                height: 20,
                                border: "#4D4D4D solid 3px"
                            }}>
                                <div style={{
                                    height: 8,
                                    backgroundColor: "#262626",
                                    margin: 3
                                }}/>
                            </div>
                        </Col>
                    </Row>
                </div>
            </div>
        );
    }
}