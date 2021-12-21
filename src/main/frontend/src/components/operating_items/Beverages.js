import React, {Component} from "react";
import {Button, Row, Space, Switch, Tooltip} from "antd";
import {beverages, showErrorMessage} from "../../api/endpoints";
import Col from "antd/es/grid/col";
import DeleteOutlined from "@ant-design/icons/es/icons/DeleteOutlined";

const emptyContent = (<span style={{color: "rgba(0, 0, 0, 0)"}}>&nbsp;</span>);
const displayHeader = [emptyContent, "Beverage", "Volume", "Ice", "Status"];

export default class Beverages extends Component {

    state = {
        beverages: [],
        loading: true
    };

    componentDidMount() {
        beverages.getAllVolumes()
            .then(
                response => this.setState({beverages: response.entity}),
                error => showErrorMessage(error)
            ).done(() => this.setState({loading: false}));
    }

    render() {
        return (
            <Row align="middle" justify="center">
                <Col style={{backgroundColor: "#C8C8C8", borderRadius: 5, padding: 10}}>
                    <Space direction="vertical">
                        <Row>
                            <Col>
                                <Display id="#display"
                                         content={this.props.order}
                                         onSelectIce={this.props.selectIce}
                                         onRemoveVolume={this.props.removeBeverageVolume}
                                />
                            </Col>
                        </Row>
                        {this.state.beverages.map(beverage =>
                            <Beverage key={beverage.id} item={beverage}
                                      maxSize={this.props.order.length === 5}
                                      onSelectVolume={this.props.addBeverageVolume}
                            />)
                        }
                        <Row align="middle" justify="center">
                            <Col>
                                <Tooltip
                                    title={this.props.totalCost === 0 || this.props.balance < this.props.totalCost
                                        ? "Enter balance equal to order cost"
                                        : ""}
                                >
                                    <Button type="primary"
                                            shape="round"
                                            size="large"
                                            loading={false}
                                            disabled={this.props.totalCost === 0 || this.props.balance < this.props.totalCost}
                                            style={{width: 200, height: 50, fontSize: 24}}
                                            onClick={this.props.submit}
                                    >
                                        Submit
                                    </Button>
                                </Tooltip>
                            </Col>
                        </Row>
                    </Space>
                </Col>
            </Row>
        );
    }
}

class Display extends Component {

    render() {
        return (
            <div id="display" className="resp-table">
                <div className="resp-table-header">
                    {displayHeader.map((column, key) => <div key={key} className="table-header-cell">{column}</div>)}
                </div>
                <div className="resp-table-body">
                    {this.tableRow(0)}
                    {this.tableRow(1)}
                    {this.tableRow(2)}
                    {this.tableRow(3)}
                    {this.tableRow(4)}
                </div>
            </div>
        );
    }

    tableRow(index) {
        let volume = this.props.content[index];

        return (
            <div className="resp-table-row">
                <div className="table-body-cell">
                    {volume
                        ? <Tooltip title={`Delete '${volume.beverageType}' (${volume.beverageVolume.volume})`}>
                            <Button type="text" icon={<DeleteOutlined/>} style={{color: "white"}}
                                    onClick={() => this.props.onRemoveVolume(index)}
                            />
                        </Tooltip>
                        : emptyContent}
                </div>
                <div className="table-body-cell">
                    {volume ? volume.beverageType : emptyContent}
                </div>
                <div className="table-body-cell">
                    {volume ? volume.beverageVolume.volume : emptyContent}
                </div>
                <div className="table-body-cell">
                    {volume
                        ? <Switch checked={volume.selectedIce}
                                  onChange={checked => this.props.onSelectIce(index, checked)}/>
                        : emptyContent}
                </div>
                <div className="table-body-cell">
                    {volume ? volume.ready : emptyContent}
                </div>
            </div>
        );
    }
}

class Beverage extends Component {

    render() {
        return (
            <Row key={this.props.item.id} align="middle" justify="center" style={{color: "white"}}>
                <Col style={{
                    backgroundColor: "#B0B0B0",
                    fontSize: 24,
                    width: 220,
                    textAlign: "center"
                }}>
                    <span>{`${this.props.item.beverageType} (${this.props.item.availableVolume.toFixed(2)}L)`}</span>
                </Col>
                {this.props.item.beverageVolumes.map(beverageVolume =>
                    <Col key={beverageVolume.id}>
                        <Tooltip title={`${beverageVolume.price}₽`}>
                            <Button shape="circle" size="large" disabled={this.props.maxSize}
                                    onClick={() => this.props.onSelectVolume(this.props.item.beverageType, beverageVolume)}
                            >
                                {beverageVolume.volume}
                            </Button>
                        </Tooltip>
                    </Col>
                )}
            </Row>
        );
    }
}