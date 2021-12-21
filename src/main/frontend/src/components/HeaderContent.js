import React, {Component} from "react";
import {Button, Col, Row} from "antd";
import Icon from "@ant-design/icons/es";
import logo from "../assets/images/sber-logo.svg";
import * as restApi from "../api/endpoints";
import LogoutOutlined from "@ant-design/icons/es/icons/LogoutOutlined";

export default class HeaderContent extends Component {

    render() {
        return (
            <Row align='middle'>
                <Col id="logo" flex="80px">
                    <Icon id="logo-icon" component={logo}/>
                </Col>
                <Col id="app-title" flex="auto">QA Test Task: Vending Machine</Col>
                <Col id="logout" flex="150px">
                    <Button id="logout-button"
                            type="text"
                            onClick={() => {
                                restApi.logout().done(() => {
                                    window.location.href += 'login';
                                });
                            }}
                    >
                        Logout <LogoutOutlined/>
                    </Button>
                </Col>
            </Row>
        );
    }
}