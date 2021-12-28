import React, {Component} from "react";
import {Layout} from "antd";
import HeaderContent from "./components/HeaderContent";
import SidebarContent from "./components/SidebarContent";
import OrdersList from "./components/orders/OrdersList";
import OperatingContent from "./components/OperatingContent";
import CaretLeftOutlined from "@ant-design/icons/es/icons/CaretLeftOutlined";
import CaretRightOutlined from "@ant-design/icons/es/icons/CaretRightOutlined";
import {orders, showErrorMessage} from "./api/endpoints";

const {Header, Sider, Content} = Layout;

export default class App extends Component {

    state = {
        key: Math.random(),
        collapsedOrders: true,
        orders: []
    };

    constructor(props) {
        super(props);

        this.rerender = this.rerender.bind(this);
    }

    componentDidMount() {
        orders.getFinishedOrders()
            .then(
                response => this.setState({ orders: response.entity }),
                error => showErrorMessage(error)
            ).done(() => this.setState({ loading: false }));
    }

    rerender() {
        this.setState({ key: Math.random() });
    }

    onCollapse = collapsed => {
        this.setState({ collapsedOrders: collapsed });
    };

    render() {
        return (
            <Layout id="app" key={this.state.key}>
                <Header id="header">
                    <HeaderContent/>
                </Header>
                <Layout>
                    <Sider id="sidebar" collapsed={true}>
                        <SidebarContent/>
                    </Sider>
                    <Content id="content">
                        <OperatingContent rerender={this.rerender}/>
                    </Content>
                    <Sider id="orders-list-sider"
                           key={this.state.key}
                           width={350}
                           collapsible
                           collapsed={this.state.collapsedOrders}
                           onCollapse={this.onCollapse}
                           collapsedWidth={0}
                           reverseArrow={true}
                           style={{
                               backgroundColor: '#e6e6e6',
                               padding: '15px 10px 5px 10px',
                               height: 'calc(100vh - 64px)'
                           }}
                           zeroWidthTriggerStyle={{backgroundColor: "#363f47"}}
                           trigger={this.state.collapsedOrders ? <CaretLeftOutlined/> : <CaretRightOutlined/>}
                           breakpoint="xxl"
                           onBreakpoint={this.onCollapse}
                    >
                        {this.state.collapsedOrders || <OrdersList rerender={this.rerender} orders={this.state.orders}/>}
                    </Sider>
                </Layout>
            </Layout>
        )
    }
}