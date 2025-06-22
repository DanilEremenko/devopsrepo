import React, { useState, useEffect } from 'react';
import { Button, Input, message, Form, Row, Col } from 'antd';
import axiosClient from '../api/AxiosClient';
import { useNavigate } from "react-router-dom";
import '../styles/ProfileModal.css';

const { TextArea } = Input;

const ProfileModal = ({ visible, onClose, onOpenChangePhoto, profilePhoto, onPhotoChange, setUserInitials }) => {
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(false);
    const [localPhotoUrl, setLocalPhotoUrl] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (!visible) return;

        setLoading(true);
        axiosClient.get('/user-profiles/')
            .then((response) => {
                const data = response.data;
                const formattedDate = data.dateOfBirth ? data.dateOfBirth.split('-').reverse().join('.') : '';

                form.setFieldsValue({
                    lastName: data.lastName || '',
                    firstName: data.firstName || '',
                    middleName: data.middleName || '',
                    login: data.login || '',
                    dateOfBirth: formattedDate,
                    workExperience: data.workExperience || ''
                });

                setUserInitials(data.firstName, data.lastName);

                if (data.photo?.guid) {
                    axiosClient.get(`/files/${data.photo.guid}/`)
                        .then((photoResponse) => {
                            setLocalPhotoUrl(photoResponse.data.reference);
                            onPhotoChange(photoResponse.data.reference);
                            localStorage.removeItem('pendingProfilePhoto');
                        })
                        .catch(() => message.warning('Не удалось загрузить фотографию'));
                } else {
                    const cached = localStorage.getItem('pendingProfilePhoto');
                    if (cached) {
                        setLocalPhotoUrl(cached);
                        onPhotoChange(cached);
                    }
                }
            })
            .catch(() => message.error('Не удалось загрузить данные профиля'))
            .finally(() => setLoading(false));
    }, [visible, form]);

    const handleSave = async () => {
        try {
            const values = await form.validateFields();
            const updatedData = { ...values };

            if (updatedData.dateOfBirth) {
                updatedData.dateOfBirth = updatedData.dateOfBirth.replace(/(\d{2})\.(\d{2})\.(\d{4})/, '$3-$2-$1');
            }

            await axiosClient.post('/user-profiles/update-user-profile/', updatedData);
            message.success('Профиль успешно обновлён');
            onClose();
        } catch (error) {
            message.error('Не удалось сохранить профиль');
        }
    };

    if (!visible) return null;

    const initials = `${form.getFieldValue('firstName')?.[0] || ''}${form.getFieldValue('lastName')?.[0] || ''}`.toUpperCase();

    return (
        <div className="profile-modal-overlay">
            <div className="profile-modal">
                <button className="close-button" onClick={onClose}>×</button>

                <div className="profile-content">
                    <div className="avatar-section">
                        {localPhotoUrl ? (
                            <img src={localPhotoUrl} alt="Profile" className="profile-avatar" />
                        ) : (
                            <div className="profile-avatar-fallback">{initials}</div>
                        )}
                            <Button
                                variant="outlined"
                                color="primary"
                                className="change-photo-button"
                                onClick={onOpenChangePhoto}
                            >
                                Изменить фото
                            </Button>
                    </div>

                    <Form form={form} layout="vertical" className="profile-form">
                        <Row gutter={16}>
                            <Col span={12}>
                                <Form.Item
                                    name="lastName"
                                    label="Фамилия"
                                    rules={[{ required: true, message: 'Пожалуйста, введите фамилию' }]}
                                >
                                    <Input placeholder="Введите фамилию" disabled={true}/>
                                </Form.Item>
                            </Col>
                            <Col span={12}>
                                <Form.Item
                                    name="dateOfBirth"
                                    label="Дата рождения"
                                    rules={[{ required: false, message: 'Пожалуйста, введите дату рождения' }]}
                                >
                                    <Input placeholder="ДД.ММ.ГГГГ" />
                                </Form.Item>
                            </Col>
                        </Row>

                        <Row gutter={16}>
                            <Col span={12}>
                                <Form.Item
                                    name="firstName"
                                    label="Имя"
                                    rules={[{ required: true, message: 'Пожалуйста, введите имя' }]}
                                >
                                    <Input placeholder="Введите имя" disabled={true}/>
                                </Form.Item>
                            </Col>
                            <Col span={12}>
                                <Form.Item
                                    name="login"
                                    label="Логин"
                                    rules={[{ required: true, message: 'Пожалуйста, введите логин' }]}
                                >
                                    <Input placeholder="Введите логин" disabled={true}/>
                                </Form.Item>
                            </Col>
                        </Row>

                        <Row gutter={16}>
                            <Col span={24}>
                                <Form.Item name="middleName" label="Отчество">
                                    <Input placeholder="Введите отчество" />
                                </Form.Item>
                            </Col>
                        </Row>

                        <Button className="change-password-button" onClick={()=>navigate("/change-password")}>Сменить пароль</Button>

                        <Row gutter={16}>
                            <Col span={24}>
                                <Form.Item
                                    name="workExperience"
                                    label="Опыт работы"
                                    rules={[{ required: false, message: 'Пожалуйста, укажите опыт работы' }]}
                                >
                                    <TextArea rows={6} placeholder="Опишите ваш опыт работы" />
                                </Form.Item>
                            </Col>
                        </Row>

                        <Row gutter={16}>
                            <Col span={24} style={{ textAlign: 'left' }}>
                                <Button
                                    variant="outlined"
                                    color="primary"
                                    onClick={handleSave}
                                    loading={loading}
                                    className="save-button"
                                >
                                    Сохранить
                                </Button>
                            </Col>
                        </Row>
                    </Form>
                </div>
            </div>
        </div>
    );
};

export default ProfileModal;