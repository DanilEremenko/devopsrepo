import React, { useState } from 'react';
import '../styles/AddUserModal.css';

const AddUserWithRolesModal = ({ isVisible, onClose, onSave, onInputChange, formData, roles, onRoleToggle }) => {
    const [isAssignRolesVisible, setAssignRolesVisible] = useState(false);

    if (!isVisible) return null;

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="add-user-modal" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">
                    <span className="modal-title">Добавление пользователя</span>
                    <span className="modal-close" onClick={onClose}>х</span>
                </div>

                <div className="modal-content">
                    <div className="input-container">
                        <label className="input-label">Фамилия *</label>
                        <input
                            type="text"
                            className="input-field"
                            value={formData.lastName}
                            onChange={(e) => onInputChange('lastName', e.target.value)}
                            required
                        />
                    </div>
                    <div className="input-container">
                        <label className="input-label">Имя *</label>
                        <input
                            type="text"
                            className="input-field"
                            value={formData.firstName}
                            onChange={(e) => onInputChange('firstName', e.target.value)}
                            required
                        />
                    </div>
                    <div className="input-container">
                        <label className="input-label">Отчество</label>
                        <input
                            type="text"
                            className="input-field"
                            value={formData.middleName}
                            onChange={(e) => onInputChange('middleName', e.target.value)}
                        />
                    </div>
                    <div className="input-container">
                        <label className="input-label">Электронная почта *</label>
                        <input
                            type="email"
                            className="input-field"
                            value={formData.login}
                            onChange={(e) => onInputChange('login', e.target.value)}
                            required
                        />
                    </div>

                    <div className="button-group-aum full-width">
                        <button className="assign-role-button-aum" onClick={() => setAssignRolesVisible(true)}>
                            Назначить роли
                        </button>
                        <button className="cancel-button-aum" onClick={onClose}>Отмена</button>
                        <button className="save-button-aum" onClick={onSave}>Сохранить</button>
                    </div>
                </div>

                {isAssignRolesVisible && (
                    <div className="modal-overlay inner" onClick={() => setAssignRolesVisible(false)}>
                        <div className="add-user-modal" onClick={(e) => e.stopPropagation()}>
                            <div className="modal-header">
                                <span className="modal-title">Назначение ролей</span>
                                <span className="modal-close" onClick={() => setAssignRolesVisible(false)}>х</span>
                            </div>
                            <div className="modal-content">
                                <div className="role-container">
                                    {['SPECIALIST', 'MANAGER', 'METHODOLOGIST', 'MENTOR'].map((roleKey) => (
                                        <div key={roleKey} className="role-switcher">
                                            <label className="role-label">
                                                {{
                                                    SPECIALIST: 'Специалист',
                                                    MANAGER: 'Менеджер',
                                                    METHODOLOGIST: 'Методолог',
                                                    MENTOR: 'Ментор',
                                                }[roleKey]}
                                            </label>
                                            <label className="switch">
                                                <input
                                                    type="checkbox"
                                                    checked={roles[roleKey]}
                                                    onChange={() => onRoleToggle(roleKey)}
                                                />
                                                <span className="slider round"></span>
                                            </label>
                                        </div>
                                    ))}
                                </div>
                                <div className="button-group-aum">
                                    <button className="cancel-button-aum" onClick={() => setAssignRolesVisible(false)}>Готово</button>
                                </div>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default AddUserWithRolesModal;
