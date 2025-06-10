import React from 'react';
import { Menu } from 'antd';
import '../styles/SidebarMenu.css';

const SidebarMenu = ({ role = 'SPECIALIST', activeKey = '1', onMenuSelect }) => {
    const menuItems = {
        SPECIALIST: [
            { key: '1', label: 'Дашборд' },
            { key: '2', label: 'Профессии' },
            { key: '3', label: 'Поиск специалистов' },
        ],
        METHODOLOGIST: [
            { key: '1', label: 'Пользователи' },
            { key: '2', label: 'Профессии' },
            { key: '3', label: 'Поиск специалистов' },
            { key: '4', label: 'Статистика' },
        ],
        MENTOR: [
            { key: '1', label: 'Поиск специалистов' },
            { key: '2', label: 'Профессии' },
            { key: '3', label: 'Статистика' },
        ],
        MANAGER: [
            { key: '1', label: 'Поиск специалистов' },
            { key: '2', label: 'Статистика' },
        ],
    };

    const normalizedRole = role?.toUpperCase?.();
    const items = menuItems[normalizedRole] || menuItems.SPECIALIST;

    return (
        <div className="sidebar-menu">
            <Menu
                mode="vertical"
                selectedKeys={[activeKey]}
                onSelect={onMenuSelect}
                items={items.map((item) => ({
                    key: item.key,
                    label: item.label,
                    className: 'menu-item-custom',
                }))}
            />
        </div>
    );
};

export default SidebarMenu;
