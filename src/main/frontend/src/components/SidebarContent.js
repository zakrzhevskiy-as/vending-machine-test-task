import React, {Component} from "react";
import {Popover, Space, Tooltip, Typography} from "antd";
import Icon from "@ant-design/icons/es";
import dbSettingsSvg from "../assets/images/database-settings.svg";
import restApiSvg from "../assets/images/rest-api.svg";
import newTabIcon from "../assets/images/new-tab.svg";
import appInfoSvg from "../assets/images/info.svg";
import {info, showErrorMessage} from "../api/endpoints";

const {Title, Text} = Typography;

export default class SidebarContent extends Component {

    state = {
        db: {},
        rest: {},
        app: {}
    };

    componentDidMount() {
        info.db()
            .then(
                response => this.setState({db: response.entity}),
                error => showErrorMessage(error)
            );
        info.rest()
            .then(
                response => this.setState({rest: response.entity}),
                error => showErrorMessage(error)
            );
        info.app()
            .then(
                response => this.setState({app: response.entity}),
                error => showErrorMessage(error)
            );
    }

    render() {
        return (
            <Space direction="vertical"
                   align="center"
                   size={35}
            >
                <Tooltip className="menu-button"
                         placement="right"
                         title="Database connection"
                         trigger={["hover", "click"]}
                >
                    <Popover placement="rightTop"
                             title="Database connection"
                             content={
                                 <Space direction="vertical">
                                     <Space>
                                         <Text strong>URL:</Text>
                                         <Text copyable={{ tooltips: false }}>{this.state.db.url}</Text>
                                     </Space>
                                     <Space>
                                         <Text strong>Schema:</Text>
                                         <Text copyable={{ tooltips: false }}>{this.state.db.schema}</Text>
                                     </Space>
                                     <Space>
                                         <Text strong>Username:</Text>
                                         <Text copyable={{ tooltips: false }}>{this.state.db.username}</Text>
                                     </Space>
                                     <Space>
                                         <Text strong>Password:</Text>
                                         <Text copyable={{ tooltips: false }}>{this.state.db.password}</Text>
                                     </Space>
                                 </Space>
                             }
                             trigger="click"
                    >
                        <Icon component={dbSettingsSvg}/>
                    </Popover>
                </Tooltip>
                <Tooltip className="menu-button"
                         placement="right"
                         title="API connection"
                         trigger={["hover", "click"]}
                >
                    <Popover placement="rightTop"
                             title="API connection"
                             content={
                                 <Space direction="vertical">
                                     <Space>
                                         <Text strong>Documentation:</Text>
                                         <a href={this.state.rest.documentation} target="_blank">
                                             <Text>Swagger UI </Text><Icon component={newTabIcon}
                                                                           style={{fontSize: 10}}/>
                                         </a>
                                     </Space>
                                     <Space>
                                         <Text strong>Auth type:</Text>
                                         <Text>{this.state.rest.auth_type}</Text>
                                     </Space>
                                     <Space>
                                         <Text strong>Credentials:</Text>
                                         <Text>{this.state.rest.credentials}</Text>
                                     </Space>
                                 </Space>
                             }
                             trigger="click"
                    >
                        <Icon component={restApiSvg}/>
                    </Popover>
                </Tooltip>
                <Tooltip className="menu-button"
                         placement="right"
                         title="Application info"
                         trigger={["hover", "click"]}
                >
                    <Popover placement="rightTop"
                             title="Application info"
                             content={
                                 <Space direction="vertical">
                                     <Space>
                                         <Text strong>Current user:</Text>
                                         <Text>{this.state.app.username}</Text>
                                     </Space>
                                 </Space>
                             }
                             trigger="click"
                    >
                        <Icon component={appInfoSvg}/>
                    </Popover>
                </Tooltip>
            </Space>
        );
    }
}