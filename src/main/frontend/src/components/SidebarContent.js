import React, {Component} from "react";
import {Popover, Space, Tooltip} from "antd";
import Icon from "@ant-design/icons/es";
import dbSettingsSvg from "../assets/images/database-settings.svg";
import restApiSvg from "../assets/images/rest-api.svg";
import newTabIcon from "../assets/images/new-tab.svg";
import appInfoSvg from "../assets/images/info.svg";
import {info, showErrorMessage} from "../api/endpoints";

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
                                     <span>{`URL: ${this.state.db.url}`}</span>
                                     <span>{`Username: ${this.state.db.username}`}</span>
                                     <span>{`Password: ${this.state.db.password}`}</span>
                                 </Space>
                             }
                             trigger="click"
                             overlayStyle={{width: 600}}
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
                                         <span style={{fontWeight: 400}}>Documentation:</span>
                                         <a href={this.state.rest.documentation} target="_blank">
                                             <span>Swagger UI </span><Icon component={newTabIcon}
                                                                           style={{fontSize: 10}}/>
                                         </a>
                                     </Space>
                                     <span>{`Auth type: ${this.state.rest.auth_type}`}</span>
                                     <span>{`Credentials: ${this.state.rest.credentials}`}</span>
                                 </Space>
                             }
                             trigger="click"
                             overlayStyle={{width: 280}}
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
                             content={JSON.stringify(this.state.app)}
                             trigger="click"
                             overlayStyle={{width: 300}}
                    >
                        <Icon component={appInfoSvg}/>
                    </Popover>
                </Tooltip>
            </Space>
        );
    }
}