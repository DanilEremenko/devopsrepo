import React, { useState, useCallback } from 'react';
import { Layout } from 'antd';
import '../styles/Dashboard.css';
import wsLogo from '../assets/ws_logo.svg';
import Header from '../components/Header';
import SidebarMenu from '../components/SidebarMenu';
import MethodologistDashboard from '../components/MethodologistDashboard';
import MentorDashboard from '../components/MentorDashboard';
import ManagerDashboard from "../components/ManagerDashboard";

const { Content, Footer } = Layout;

const Dashboard = () => {
    const [role, setRole] = useState('');
    const [activeMenuKey, setActiveMenuKey] = useState('1');
    const [isMainContentVisible, setIsMainContentVisible] = useState(true);

    const handleRoleChange = useCallback((newRole) => {
        setRole(newRole);
        setActiveMenuKey('1');
    }, []);

    const handleMenuSelect = ({ key }) => {
        setActiveMenuKey(key);
    };

    const handleToggleMainContent = (visible) => {
        setIsMainContentVisible(visible);
    };

    return (
        <Layout className="dashboard-layout">
            <div className="top-bar">
                <img src={wsLogo} alt="Logo" className="logo" />
                <Header
                    onRoleChange={handleRoleChange}
                    onToggleMainContent={handleToggleMainContent}
                />
            </div>

            <SidebarMenu
                role={role}
                activeKey={activeMenuKey}
                onMenuSelect={handleMenuSelect}
            />

            <Layout className="main-layout">
                <Content className="dashboard-content">
                    {isMainContentVisible && (
                        <div className="main-container">
                            {role === 'METHODOLOGIST' ? (
                                <MethodologistDashboard />
                            ) : role === 'MENTOR' ? (
                                <MentorDashboard />
                            ) : role === 'MANAGER' ? (
                                <ManagerDashboard />
                            ) : (
                                <>
                                    <div className="info-card">
                                        <p className="job-title">
                                            Профессия: <span>PHP backend разработчик</span>
                                        </p>
                                        <p className="job-grade">Грейд: Middle</p>
                                        <p className="job-status">
                                            Текущий грейд не подтвержден. Необходимо пройти калибровку.
                                        </p>
                                    </div>
                                    <h3 className="history-title">История достижений</h3>
                                    <p className="no-events">Пока нет событий.</p>
                                </>
                            )}
                        </div>
                    )}
                </Content>

                <Footer className="dashboard-footer">
                    <div className="footer-container">
                        <p className="footer-text">2025 © Work Solutions</p>
                        <p className="footer-text" href="/agreement.pdf">Политика конфиденциальности</p>
                        <img src={wsLogo} alt="Logo" className="footer-logo" />
                    </div>
                </Footer>
            </Layout>
        </Layout>
    );
};

export default Dashboard;