import React, {Component} from "react";
import Icon from "@ant-design/icons/es";
import nfc from "../../../assets/images/hand-with-nfc.svg";
import {Button, Col, Row} from "antd";

export default class PayPass extends Component {

    render() {
        return (
            <Button id="pay-pass"
                    type="text"
                    disabled={this.props.orderSize === 0 || this.props.balance > 0}
                    onClick={() => this.props.payPass(this.props.totalCost)}
                    style={{height: 150, padding: 0}}
            >
                <div style={{
                    backgroundColor: "#333333",
                    borderRadius: 5,
                    padding: 10,
                    width: 245,
                    height: 148
                }}
                >
                    <Row align="middle" justify="center"
                         style={{
                             width: "100%", height: "100%", border: "#808080 solid 3px",
                             borderRadius: 10, color: "white", fontSize: 80
                         }}
                    >
                        <Col>
                            <Icon component={nfc}/>
                        </Col>
                    </Row>
                </div>
            </Button>
        );
    }
}