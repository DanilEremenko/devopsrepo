import React from 'react';
import { Layout, Divider } from 'antd';
import SmallFooter from '../components/SmallFooter';
import styles from '../styles/SidebarLayout.module.scss';
import logo from '../assets/ws_logo.svg';
import background from '../assets/bg.png';

const { Sider, Content } = Layout;

const SidebarLayout = ({ children }) => {
    return (
        <Layout className={styles.layout}>
            <Sider
                width={400}
                className={styles.customSidebar}
                theme="light"
                trigger={null}
            >
                <div className={styles.sidebarWrapper}>
                    <div className={styles.sidebarLogo}>
                        <img src={logo} alt="Logo" className={styles.logoImg} />
                    </div>
                    <div className={styles.sidebarContent}>
                        {children}
                    </div>
                    <div className={styles.sidebarFooter}>
                        <Divider style={{ margin: '8px 0' }} />
                        <SmallFooter />
                    </div>
                </div>
            </Sider>
            <img src={background} alt="Background" className={styles.backgroundImg} />
        </Layout>
    );
};

export default SidebarLayout;
