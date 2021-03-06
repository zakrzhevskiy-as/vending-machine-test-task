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
        // key: Math.random(),
        collapsedOrders: true,
        orders: []
    };

    constructor(props) {
        super(props);

        // this.rerender = this.rerender.bind(this);
        this.getOrders = this.getOrders.bind(this);
        this.deleteOrder = this.deleteOrder.bind(this);
    }

    componentDidMount() {
        this.getOrders();
    }

    getOrders() {
        orders.getOrders(false)
            .then(
                response => this.setState({ orders: response.entity }),
                error => showErrorMessage(error)
            ).done(() => this.setState({ loading: false }));
    }

    rerender() {
        // this.setState({ key: Math.random() });
        this.getOrders();
    }

    onCollapse = collapsed => {
        this.setState({ collapsedOrders: collapsed });
    };

    deleteOrder(id) {
        this.setState({loading: true});
        orders.deleteOrder(id)
            .then(() => this.getOrders(), error => showErrorMessage(error))
            .done(() => this.setState({loading: false}));
    }

    render() {
        return (
            <Layout id="app" //key={this.state.key}
            >
                <Header id="header">
                    <HeaderContent/>
                </Header>
                <Layout>
                    <Sider id="sidebar" collapsed={true}>
                        <SidebarContent/>
                    </Sider>
                    <Content id="content">
                        <OperatingContent getOrders={this.getOrders}/>
                    </Content>
                    <Sider id="orders-list-sider"
                           width={320}
                           collapsible
                           collapsed={this.state.collapsedOrders}
                           onCollapse={this.onCollapse}
                           collapsedWidth={0}
                           reverseArrow={true}
                           style={{
                               backgroundColor: '#e6e6e6',
                               padding: '15px 10px 5px 10px',
                               height: 'calc(100vh - 64px)',
                               position: "absolute",
                               right: 0
                           }}
                           zeroWidthTriggerStyle={{backgroundColor: "#363f47"}}
                           trigger={this.state.collapsedOrders ? <CaretLeftOutlined/> : <CaretRightOutlined/>}
                           breakpoint="xxl"
                           onBreakpoint={this.onCollapse}
                    >
                        {
                            this.state.collapsedOrders ||
                            <OrdersList deleteOrder={this.deleteOrder}
                                        rerender={this.getOrders}
                                        orders={this.state.orders}
                            />
                        }
                    </Sider>
                </Layout>
            </Layout>
        )
    }
}