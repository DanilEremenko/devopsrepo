import React from 'react';
import { Layout, Typography, Row, Col } from 'antd';
import '../styles/SmallFooter.css';
import footerLogo from '../assets/footer-logo.svg';

const { Footer } = Layout;
const { Text } = Typography;

const SmallFooter = () => {
    return (
        <Footer className="small-footer">
            <Row justify="space-between" align="middle" style={{ height: '100%' }}>
                <Col>
                    <div className="footer-left">
                        <Text className="footer-year">2025</Text>
                        <br />
                        <Text className="footer-copy">© Work Solutions</Text>
                    </div>
                </Col>
                <Col>
                    <img
                        src={footerLogo}
                        alt="Logo"
                        className="footer-logo"
                    />
                </Col>
            </Row>
        </Footer>
    );
};

export default SmallFooter;