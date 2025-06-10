import React from 'react';
import { Typography } from 'antd';
import '../styles/BigFooter.css';

const { Text, Link } = Typography;

const BigFooter = () => {
    return (
        <footer className="big-footer">
            <div className="footer-content">
                <Text type="secondary">2024 © Work Solutions</Text>
                <Link href="/privacy-policy" className="footer-link">
                    Политика конфиденциальности
                </Link>
            </div>
        </footer>
    );
};

export default BigFooter;