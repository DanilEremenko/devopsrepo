import React, { useState, useEffect } from 'react';
import { Input, message, Tooltip, Modal, Button, Form, Row, Col, Tag } from 'antd';
import { CloseOutlined, EditOutlined } from '@ant-design/icons';
import axiosClient from '../api/AxiosClient';
import RoleEditModal from './RoleEditModal';
import '../styles/ProfileModal.css';

const { TextArea } = Input;

const ProfileViewModal = ({ visible, onClose, activeRole, userId }) => {
    const [form] = Form.useForm();
    const [profileData, setProfileData] = useState({
        userId: '',
        lastName: '',
        firstName: '',
        middleName: '',
        login: '',
        dateOfBirth: '',
        workExperience: '',
        photo: null,
        roles: [],
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

    const roleColors = {
        METHODOLOGIST: 'blue',
        MANAGER: 'green',
        MENTOR: 'orange',
        SPECIALIST: 'purple'
    };

    const fetchUserData = () => {
        axiosClient
            .get(`/users/${userId}/`)
            .then((response) => {
                const data = response.data;
                const initials = `${data.firstName?.[0] || ''}${data.lastName?.[0] || ''}`.toUpperCase();
                setUserInitials(initials);

                const formattedData = {
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
                };

                setProfileData(formattedData);
                form.setFieldsValue(formattedData);

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
        <div className="profile-modal-overlay">
            <div className="profile-modal">
                <button className="close-button" onClick={onClose}>×</button>

                <div className="profile-content">
                    <div className="avatar-section">
                        {photoUrl ? (
                            <img src={photoUrl} alt="Profile" className="profile-avatar" />
                        ) : (
                            <div className="profile-avatar-fallback">{userInitials}</div>
                        )}
                    </div>

                    <Form form={form} layout="vertical" className="profile-form">
                        <Row gutter={16}>
                            <Col span={12}>
                                <Form.Item name="lastName" label="Фамилия">
                                    <Input disabled />
                                </Form.Item>
                            </Col>
                            <Col span={12}>
                                <Form.Item name="dateOfBirth" label="Дата рождения">
                                    <Input disabled />
                                </Form.Item>
                            </Col>
                        </Row>

                        <Row gutter={16}>
                            <Col span={12}>
                                <Form.Item name="firstName" label="Имя">
                                    <Input disabled />
                                </Form.Item>
                            </Col>
                            <Col span={12}>
                                <Form.Item name="login" label="Логин">
                                    <Input disabled />
                                </Form.Item>
                            </Col>
                        </Row>

                        <Row gutter={16}>
                            <Col span={24}>
                                <Form.Item name="middleName" label="Отчество">
                                    <Input disabled />
                                </Form.Item>
                            </Col>
                        </Row>

                        <Row gutter={16}>
                            <Col span={24}>
                                <Form.Item name="workExperience" label="Опыт работы">
                                    <TextArea rows={6} disabled />
                                </Form.Item>
                            </Col>
                        </Row>

                        <Row gutter={16}>
                            <Col span={24}>
                                <Form.Item label="Роли пользователя">
                                    {activeRole === 'METHODOLOGIST' && (
                                        <Tooltip title="Редактировать роли">
                                            <Button
                                                type="text"
                                                icon={<EditOutlined />}
                                                className="edit-roles-button"
                                                onClick={() => setIsEditRoleModalVisible(true)}
                                            />
                                        </Tooltip>
                                    )}
                                    <div className="roles-container">
                                        <div className="roles-list">

                                            {profileData.roles.length > 0 ? (
                                                profileData.roles.map((role) => (
                                                    <Tag
                                                        key={role.roleType}
                                                        color={roleColors[role.roleType] || 'default'}
                                                        className="role-tag"
                                                    >
                                                        {roleTranslation[role.roleType] || role.roleType}
                                                    </Tag>
                                                ))
                                            ) : (
                                                <span className="no-roles-text">Роли не назначены</span>
                                            )}
                                        </div>

                                    </div>
                                </Form.Item>
                            </Col>
                        </Row>

                        {activeRole === 'MANAGER' && (
                            <Row gutter={16}>
                                <Col span={24}>
                                    <Form.Item label="Обратная связь">
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
                                        <Button
                                            type="primary"
                                            className="feedback-button"
                                            onClick={() => setIsFeedbackModalVisible(true)}
                                        >
                                            Оставить обратную связь
                                        </Button>
                                    </Form.Item>
                                </Col>
                            </Row>
                        )}
                    </Form>
                </div>
            </div>

            <Modal
                title="Оставьте обратную связь"
                visible={isFeedbackModalVisible}
                onCancel={() => setIsFeedbackModalVisible(false)}
                footer={[
                    <Button key="cancel" className="modal-btn cancel" onClick={() => setIsFeedbackModalVisible(false)}>
                        Отмена
                    </Button>,
                    <Button key="save" type="primary" onClick={handleSaveFeedback}>
                        Сохранить
                    </Button>
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
                key={userId + isEditRoleModalVisible}
                visible={isEditRoleModalVisible}
                onClose={() => setIsEditRoleModalVisible(false)}
                userData={profileData}
                onRolesUpdated={fetchUserData}
            />
        </div>
    );
};

export default ProfileViewModal;