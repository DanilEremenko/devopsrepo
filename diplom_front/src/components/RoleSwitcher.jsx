import React, { useEffect, useState } from 'react';
import axiosClient from '../api/AxiosClient';
import specialistGray from '../assets/specialistGray.svg';
import specialistBlue from '../assets/specialistBlue.svg';
import methodologistGray from '../assets/methodologistGray.svg';
import methodologistBlue from '../assets/methodologistBlue.svg';
import mentorGray from '../assets/mentorGray.svg';
import mentorBlue from '../assets/mentorBlue.svg';
import managerGray from '../assets/managerGray.svg';
import managerBlue from '../assets/managerBlue.svg';
import '../styles/RoleSwitcher.css';

const rolesConfig = [
    { name: 'SPECIALIST', grayIcon: specialistGray, blueIcon: specialistBlue },
    { name: 'METHODOLOGIST', grayIcon: methodologistGray, blueIcon: methodologistBlue },
    { name: 'MENTOR', grayIcon: mentorGray, blueIcon: mentorBlue },
    { name: 'MANAGER', grayIcon: managerGray, blueIcon: managerBlue },
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
                    const initialRole = userRoles[0];
                    setAvailableRoles(userRoles);
                    setActiveRole(initialRole);
                    onRoleChange?.(initialRole);
                } else {
                    console.warn('Нет доступных ролей для пользователя');
                }
            } catch (error) {
                console.error('Ошибка при получении текущего пользователя:', error);
            }
        };

        fetchCurrentUser();
    }, []);

    const handleRoleChange = async (newRole) => {
        if (!availableRoles.includes(newRole)) return;
        if (newRole === activeRole) return;

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
            {rolesConfig.map((role) => (
                <div
                    key={role.name}
                    className={`role-item ${activeRole === role.name ? 'active' : 'inactive'}`}
                    onClick={() => availableRoles.includes(role.name) && handleRoleChange(role.name)}
                >
                    <img
                        src={activeRole === role.name ? role.blueIcon : role.grayIcon}
                        alt={role.name}
                        className="role-icon"
                    />
                </div>
            ))}
        </div>
    );
};

export default RoleSwitcher;
