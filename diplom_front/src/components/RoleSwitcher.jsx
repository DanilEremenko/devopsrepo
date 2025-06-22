import React, { useEffect, useState } from 'react';
import {
    UserOutlined,
    UserSwitchOutlined,
    TeamOutlined,
    SolutionOutlined,
} from '@ant-design/icons';
import { Tooltip } from 'antd';
import axiosClient from '../api/AxiosClient';
import '../styles/RoleSwitcher.css';

const rolesConfig = [
    { name: 'SPECIALIST', Icon: UserOutlined, tooltip: 'Специалист' },
    { name: 'METHODOLOGIST', Icon: UserSwitchOutlined, tooltip: 'Методолог' },
    { name: 'MENTOR', Icon: TeamOutlined, tooltip: 'Ментор' },
    { name: 'MANAGER', Icon: SolutionOutlined, tooltip: 'Менеджер' },
];

const RoleSwitcher = ({ onRoleChange }) => {
    const [activeRole, setActiveRole] = useState('');
    const [availableRoles, setAvailableRoles] = useState([]);

    useEffect(() => {
        const fetchCurrentUser = async () => {
            try {
                const response = await axiosClient.get('/auth/current-user/');
                const userRoles = response.data.userRoles.map(role => role.roleType);

                if (userRoles.length > 0) {
                    setAvailableRoles(userRoles);
                    const initialRole = userRoles[0];
                    setActiveRole(initialRole);
                    onRoleChange?.(initialRole);
                }
            } catch (error) {
                console.error('Ошибка при получении текущего пользователя:', error);
            }
        };

        fetchCurrentUser();
    }, []);

    const handleRoleChange = async (newRole) => {
        if (!availableRoles.includes(newRole) || newRole === activeRole) return;

        try {
            await axiosClient.post('/user-profiles/set-active-role/', { activeRole: newRole });
            setActiveRole(newRole);
            onRoleChange?.(newRole);
        } catch (error) {
            console.error('Ошибка при установке роли:', error);
            alert('Не удалось изменить активную роль');
        }
    };

    return (
        <div className="role-switcher">
            {rolesConfig.map(({ name, Icon, tooltip }, index) => {
                const isDisabled = !availableRoles.includes(name);
                const isActive = activeRole === name;
                const color = isActive ? '#1890ff' : '#bfbfbf';

                return (
                    <Tooltip title={tooltip} key={tooltip}>
                        <div
                            className={`role-item ${isActive ? 'active' : 'inactive'} ${isDisabled ? 'disabled' : ''}`}
                            onClick={() => {
                                if (!isDisabled && availableRoles.includes(name)) {
                                    handleRoleChange(name);
                                }
                            }}
                        >
                            <Icon style={{ fontSize: 28, color }} />
                        </div>
                    </Tooltip>
                );
            })}
        </div>
    );
};

export default RoleSwitcher;
