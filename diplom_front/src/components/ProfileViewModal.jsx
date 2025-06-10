import React, { useState, useEffect } from 'react';
import { Input, message, Tooltip, Modal } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import axiosClient from '../api/AxiosClient';
import RoleEditModal from './RoleEditModal';
import pencilIcon from '../assets/pencil.svg';
import '../styles/ProfileViewModal.css';

const { TextArea } = Input;

const ProfileViewModal = ({ visible, onClose, activeRole, userId }) => {
    const [profileData, setProfileData] = useState({
        userId: '',
        lastName: '',
        firstName: '',
        middleName: '',
        login: '',
        dateOfBirth: '',
        workExperience: '',
        photo: null,
        roles: '',
        activeRole: '',
        activeStatus: true
    });
    const [photoUrl, setPhotoUrl] = useState(null);
    const [userInitials, setUserInitials] = useState('');
    const [isEditRoleModalVisible, setIsEditRoleModalVisible] = useState(false);

    const [feedbackData, setFeedbackData] = useState(null);
    const [feedbackText, setFeedbackText] = useState('');
    const [isFeedbackModalVisible, setIsFeedbackModalVisible] = useState(false);

    const roleTranslation = {
        METHODOLOGIST: 'Методолог',
        MANAGER: 'Менеджер',
        MENTOR: 'Ментор',
        SPECIALIST: 'Специалист',
    };

    const fetchUserData = () => {
        axiosClient
            .get(`/users/${userId}/`)
            .then((response) => {
                const data = response.data;
                const initials = `${data.firstName?.[0] || ''}${data.lastName?.[0] || ''}`.toUpperCase();
                setUserInitials(initials);

                setProfileData({
                    userId: userId,
                    lastName: data.lastName || '',
                    firstName: data.firstName || '',
                    middleName: data.middleName || '',
                    login: data.login || '',
                    dateOfBirth: data.dateOfBirth ? data.dateOfBirth.split('-').reverse().join('.') : '',
                    workExperience: data.workExperience || '',
                    photo: data.photo || null,
                    roles: data.roles || [],
                    activeRole: data.activeRole || '',
                    activeStatus: data.activeStatus ?? true,
                });

                if (data.photo?.guid) {
                    axiosClient
                        .get(`/files/${data.photo.guid}/`)
                        .then((photoResponse) => {
                            setPhotoUrl(photoResponse.data.reference);
                        })
                        .catch(() => {
                            message.warning('Не удалось загрузить фотографию');
                            setPhotoUrl(null);
                        });
                } else {
                    setPhotoUrl(null);
                }
            })
            .catch(() => {
                message.error('Не удалось загрузить данные профиля');
            });
    };

    const fetchFeedback = () => {
        axiosClient
            .get(`/feedback/${userId}/`)
            .then(res => {
                const items = res.data?.items || [];
                setFeedbackData(items);
            })
            .catch((err) => {
                console.error('Ошибка при получении обратной связи', err);
                setFeedbackData([]);
            });
    };



    useEffect(() => {
        if (visible && userId) {
            fetchUserData();
            if (activeRole === 'MANAGER') {
                fetchFeedback();
            } else {
                setFeedbackData(null);
                setFeedbackText('');
            }
        }
    }, [visible, userId, activeRole]);

    const handleSaveFeedback = () => {
        if (!feedbackText.trim()) {
            message.warning('Пожалуйста, заполните текст обратной связи');
            return;
        }

        const today = new Date();
        const createdAt = today.toISOString().slice(0, 10);

        axiosClient.post(`/feedback/${userId}/`, {
            message: feedbackText.trim(),
            createdAt: createdAt
        })
            .then(() => {
                message.success('Обратная связь успешно сохранена');
                fetchFeedback();
                setIsFeedbackModalVisible(false);
            })
            .catch(() => {
                message.error('Ошибка при сохранении обратной связи');
            });
    };

    if (!visible) return null;

    return (
        <div className="profile-modal">
            <button className="close-button" onClick={onClose}>Х</button>

            <div className="profile-avatar-container">
                {photoUrl ? (
                    <img src={photoUrl} alt="Profile" className="profile-avatar" />
                ) : (
                    <div className="profile-avatar-fallback">{userInitials}</div>
                )}
            </div>

            <div className="profile-field-container surname">
                <label style={{ color: 'black' }}>Фамилия</label>
                <Input value={profileData.lastName} disabled style={{ color: 'black' }} />
            </div>

            <div className="profile-field-container birthdate">
                <label style={{ color: 'black' }}>Дата рождения</label>
                <Input value={profileData.dateOfBirth} disabled style={{ color: 'black' }} />
            </div>

            <div className="profile-field-container name">
                <label style={{ color: 'black' }}>Имя</label>
                <Input value={profileData.firstName} disabled style={{ color: 'black' }} />
            </div>

            <div className="profile-field-container login">
                <label style={{ color: 'black' }}>Логин</label>
                <Input value={profileData.login} disabled style={{ color: 'black' }} />
            </div>

            <div className="profile-field-container patronymic">
                <label style={{ color: 'black' }}>Отчество</label>
                <Input value={profileData.middleName} disabled style={{ color: 'black' }} />
            </div>

            <div className="work-experience-container">
                <label className="work-experience-label">Опыт работы</label>
                <TextArea
                    className="work-experience-textarea"
                    value={profileData.workExperience}
                    disabled
                    style={{ color: 'black' }}
                />
            </div>

            <div className="profile-roles-container">
                <label className="profile-roles-label">Роли пользователя</label>
                <div className="textarea-wrapper">
                    <TextArea
                        value={
                            Array.isArray(profileData.roles)
                                ? profileData.roles
                                    .map((role) => roleTranslation[role.roleType] || role.roleType)
                                    .join(', ')
                                : ''
                        }
                        autoSize={{minRows: 2, maxRows: 4}}
                        disabled
                        style={{color: 'black'}}
                    />
                    {activeRole === 'METHODOLOGIST' && (
                        <Tooltip title="Редактировать роли">
                            <img
                                src={pencilIcon}
                                alt="Редактировать"
                                className="edit-icon"
                                onClick={() => setIsEditRoleModalVisible(true)}
                            />
                        </Tooltip>
                    )}
                </div>
            </div>

            {activeRole === 'MANAGER' && (
                <div className="feedback-section">
                    <label style={{ color: 'black' }}>Обратная связь</label>

                    {feedbackData && feedbackData.length > 0 ? (
                        <div className="feedback-list">
                            {feedbackData.map((fb) => {
                                const author = fb.author;
                                const formattedDate = new Date(fb.createdAt)
                                    .toLocaleDateString('ru-RU', { day: '2-digit', month: '2-digit', year: 'numeric' });

                                return (
                                    <div key={fb.id} className="feedback-card">
                                        <div className="feedback-meta">
                                <span className="feedback-author">
                                    {author.lastname} {author.firstname} {author.middlename}
                                </span>
                                            <span className="feedback-date">{formattedDate}</span>
                                        </div>
                                        <div className="feedback-message">{fb.message}</div>
                                    </div>
                                );
                            })}
                        </div>
                    ) : (
                        <div style={{ color: 'gray', marginBottom: 8 }}>Обратная связь отсутствует</div>
                    )}

                    <button className="feedback-button" onClick={() => setIsFeedbackModalVisible(true)}>
                        Оставить обратную связь
                    </button>
                </div>
            )}

            <Modal
                title="Оставьте обратную связь"
                visible={isFeedbackModalVisible}
                onCancel={() => setIsFeedbackModalVisible(false)}
                footer={[
                    <button  key="cancel" className="modal-btn cancel" style={{marginRight: 15}} onClick={() => setIsFeedbackModalVisible(false)}>
                        Отмена
                    </button>,
                    <button key="save" className="modal-btn save" onClick={handleSaveFeedback}>
                        Сохранить
                    </button>
                ]}
                closeIcon={<CloseOutlined />}
                destroyOnClose
                className="custom-feedback-modal"
            >
                <TextArea
                    rows={4}
                    value={feedbackText}
                    onChange={(e) => setFeedbackText(e.target.value)}
                    placeholder="Введите текст обратной связи"
                />
            </Modal>

            <RoleEditModal
                visible={isEditRoleModalVisible}
                onClose={() => setIsEditRoleModalVisible(false)}
                userData={profileData}
                onRolesUpdated={fetchUserData}
            />
        </div>
    );
};

export default ProfileViewModal;
