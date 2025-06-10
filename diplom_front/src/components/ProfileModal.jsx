import React, { useState, useEffect } from 'react';
import { Button, Input, Upload, message } from 'antd';
import axiosClient from '../api/AxiosClient';
import '../styles/ProfileModal.css';
import { useNavigate } from "react-router-dom";


const { TextArea } = Input;


const ProfileModal = ({ visible, onClose, onOpenChangePhoto, profilePhoto, onPhotoChange, setUserInitials }) => {
    const [profileData, setProfileData] = useState({
        lastName: '',
        firstName: '',
        middleName: '',
        login: '',
        dateOfBirth: '',
        workExperience: '',
        photoGuid: '',
    });

    const [localPhotoUrl, setLocalPhotoUrl] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (!visible) return;

        axiosClient.get('/user-profiles/')
            .then((response) => {
                const data = response.data;

                setProfileData({
                    lastName: data.lastName || '',
                    firstName: data.firstName || '',
                    middleName: data.middleName || '',
                    login: data.login || '',
                    dateOfBirth: data.dateOfBirth ? data.dateOfBirth.split('-').reverse().join('.') : '',
                    workExperience: data.workExperience || '',
                    photoGuid: data.photo?.guid || '',
                });

                setUserInitials(data.firstName, data.lastName);

                if (data.photo?.guid) {
                    axiosClient.get(`/files/${data.photo.guid}/`)
                        .then((photoResponse) => {
                            const photoUrl = photoResponse.data.reference;
                            setLocalPhotoUrl(photoUrl);
                            onPhotoChange(photoUrl);
                            localStorage.removeItem('pendingProfilePhoto');
                        })
                        .catch(() => {
                            message.warning('Не удалось загрузить фотографию');
                        });
                } else {
                    const cached = localStorage.getItem('pendingProfilePhoto');
                    if (cached) {
                        setLocalPhotoUrl(cached);
                        onPhotoChange(cached);
                    } else {
                        setLocalPhotoUrl(null);
                        onPhotoChange(null);
                    }
                }
            })
            .catch(() => {
                message.error('Не удалось загрузить данные профиля');
            });
    }, [visible]);

    const handleChange = (field) => (e) => {
        setProfileData((prev) => ({ ...prev, [field]: e.target.value }));
    };

    const handleSave = () => {
        const updatedData = { ...profileData };

        if (updatedData.dateOfBirth) {
            updatedData.dateOfBirth = updatedData.dateOfBirth.replace(/(\d{2})\.(\d{2})\.(\d{4})/, '$3-$2-$1');
        }

        axiosClient.post('/user-profiles/update-user-profile/', updatedData)
            .then(() => {
                message.success('Профиль успешно обновлён');
                onClose();
            })
            .catch(() => {
                message.error('Не удалось сохранить профиль');
            });
    };

    if (!visible) return null;

    const initials = `${profileData.firstName?.[0] || ''}${profileData.lastName?.[0] || ''}`.toUpperCase();

    return (
        <div className="profile-modal">
            <div className="profile-avatar-container">
                {localPhotoUrl ? (
                    <img src={localPhotoUrl} alt="Profile" className="profile-avatar" />
                ) : (
                    <div className="profile-avatar-fallback">{initials}</div>
                )}
                <Upload beforeUpload={() => false} showUploadList={false}>
                    <Button className="change-photo-button" onClick={onOpenChangePhoto}>
                        Изменить фото
                    </Button>
                </Upload>
            </div>

            <div className="profile-field-container surname">
                <label>Фамилия <span className="required-star">*</span></label>
                <Input value={profileData.lastName} onChange={handleChange('lastName')} />
            </div>

            <div className="profile-field-container birthdate">
                <label>Дата рождения <span className="required-star">*</span></label>
                <Input value={profileData.dateOfBirth} onChange={handleChange('dateOfBirth')} />
            </div>

            <div className="profile-field-container name">
                <label>Имя <span className="required-star">*</span></label>
                <Input value={profileData.firstName} onChange={handleChange('firstName')} />
            </div>

            <div className="profile-field-container login">
                <label>Логин <span className="required-star">*</span></label>
                <Input value={profileData.login} onChange={handleChange('login')} />
            </div>

            <div className="profile-field-container patronymic">
                <label>Отчество</label>
                <Input value={profileData.middleName} onChange={handleChange('middleName')} />
            </div>

            <Button className="change-password-button" onClick={()=>navigate("/change-password")}>Сменить пароль</Button>

            <div className="work-experience-container">
                <label>Опыт работы <span className="required-star">*</span></label>
                <TextArea value={profileData.workExperience} onChange={handleChange('workExperience')} />
            </div>

            <Button className="save-button" onClick={handleSave}>Сохранить</Button>
        </div>
    );
};

export default ProfileModal;
