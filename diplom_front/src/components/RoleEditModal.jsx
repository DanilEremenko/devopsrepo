import React, { useState, useEffect } from 'react';
import { Modal, message } from 'antd';
import axiosClient from '../api/AxiosClient';
import '../styles/RoleEditModal.css';

const ALL_ROLES = ['SPECIALIST', 'MANAGER', 'METHODOLOGIST', 'MENTOR'];

const roleLabels = {
    SPECIALIST: 'Специалист',
    MANAGER: 'Менеджер',
    METHODOLOGIST: 'Методолог',
    MENTOR: 'Ментор',
};

const RoleEditModal = ({ visible, onClose, userData, onRolesUpdated }) => {
    const [roles, setRoles] = useState({});

    useEffect(() => {
        if (visible && userData?.roles) {
            const userRolesSet = new Set(userData.roles.map(roleObj => roleObj.roleType));
            const roleState = {};
            ALL_ROLES.forEach(role => {
                roleState[role] = userRolesSet.has(role);
            });
            setRoles(roleState);
        }
    }, [visible, userData.roles]);

    const onRoleToggle = (roleKey) => {
        setRoles(prev => ({
            ...prev,
            [roleKey]: !prev[roleKey],
        }));
    };

    const handleSave = async () => {
        const updatedRoles = Object.entries(roles)
            .filter(([_, isActive]) => isActive)
            .map(([role]) => role);

        const oldActive = typeof userData.activeRole === 'object'
            ? userData.activeRole.roleType
            : userData.activeRole;
        const newActive = updatedRoles.includes(oldActive)
            ? oldActive : (updatedRoles.length > 0 ? updatedRoles[0] : null);

        const rawPayload = {
            userId: userData.userId,
            lastName: userData.lastName,
            firstName: userData.firstName,
            middleName: userData.middleName,
            roles: updatedRoles,
            activeRole: newActive,
            activeStatus: true,
            login: userData.login,
            photo: userData.photo,
            dateOfBirth: userData.dateOfBirth.replace(/(\d{2})\.(\d{2})\.(\d{4})/, '$3-$2-$1'),
            workExperience: userData.workExperience,
        };

        const payload = Object.fromEntries(
            Object.entries(rawPayload).filter(([_, value]) => value !== null && value !== undefined)
        );

        try {
            await axiosClient.patch('/users/update-user-profile/', payload);
            message.success('Роли успешно обновлены');
            onRolesUpdated();
            onClose();
        } catch (error) {
            message.error('Ошибка при обновлении ролей');
        }
    };

    return (
        <Modal
            title="Редактировать роли пользователя"
            open={visible}
            onCancel={onClose}
            onOk={handleSave}
            okText="Сохранить"
            cancelButtonProps={{ style: { display: 'none' } }}
        >
            <div className="role-container">
                {ALL_ROLES.map((roleKey) => (
                    <div key={roleKey} className="role-switcher">
                        <label className="role-label">{roleLabels[roleKey]}</label>
                        <label className="switch">
                            <input
                                type="checkbox"
                                checked={roles[roleKey] || false}
                                onChange={() => onRoleToggle(roleKey)}
                            />
                            <span className="slider round"></span>
                        </label>
                    </div>
                ))}
            </div>
        </Modal>
    );
};

export default RoleEditModal;
