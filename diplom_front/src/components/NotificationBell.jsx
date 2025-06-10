import React, { useState } from 'react';
import { Badge, Dropdown, Button, Spin, message } from 'antd';
import { BellOutlined } from '@ant-design/icons';
import axiosClient from '../api/AxiosClient';
import '../styles/NotificationBell.css';

const NotificationBell = () => {
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(false);
    const [dropdownVisible, setDropdownVisible] = useState(false);

    const fetchNotifications = async () => {
        try {
            setLoading(true);
            const response = await axiosClient.get('/notifications/');
            const items = Array.isArray(response.data?.items) ? response.data.items : [];
            setNotifications(items);
        } catch (error) {
            message.error('Ошибка при загрузке уведомлений');
        } finally {
            setLoading(false);
        }
    };

    const handleVisibleChange = (visible) => {
        setDropdownVisible(visible);
        if (visible && notifications.length === 0) {
            fetchNotifications();
        }
    };

    const dropdownContent = (
        <div className="notification-dropdown">
            {loading ? (
                <div className="notification-loading">
                    <Spin />
                </div>
            ) : notifications.length === 0 ? (
                <div className="notification-empty">Нет новых уведомлений</div>
            ) : (
                notifications.map((item, index) => (
                    <div className="notification-card" key={index}>
                        <div className="notification-title">{item.title || 'Без заголовка'}</div>
                        <div className="notification-description">{item.description || 'Нет описания'}</div>
                    </div>
                ))
            )}
        </div>
    );

    return (
        <Dropdown
            overlay={dropdownContent}
            trigger={['click']}
            onVisibleChange={handleVisibleChange}
            visible={dropdownVisible}
            placement="bottomRight"
        >
            <Badge count={notifications.length}>
                <Button
                    type="text"
                    icon={<BellOutlined style={{ fontSize: '24px' }} />}
                    style={{ padding: 0 }}
                />
            </Badge>
        </Dropdown>
    );
};

export default NotificationBell;
