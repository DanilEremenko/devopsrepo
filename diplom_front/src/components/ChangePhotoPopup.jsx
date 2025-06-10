import React, { useState, useRef } from 'react';
import { Button, message } from 'antd';
import axiosClient from '../api/AxiosClient';
import '../styles/ProfileModal.css';

const ChangePhotoPopup = ({ visible, onOk, onCancel, onPhotoChange }) => {
    const [previewImage, setPreviewImage] = useState(null);
    const [file, setFile] = useState(null);
    const fileInputRef = useRef(null);

    if (!visible) return null;

    const handleUploadClick = () => {
        fileInputRef.current.click();
    };

    const handleFileChange = (event) => {
        const selectedFile = event.target.files[0];
        if (selectedFile) {
            const reader = new FileReader();
            reader.onload = (e) => {
                const newPhotoUrl = e.target.result;
                setPreviewImage(newPhotoUrl);
                onPhotoChange(newPhotoUrl);
            };
            reader.readAsDataURL(selectedFile);
            setFile(selectedFile);
        }
    };

    const handleConfirm = async () => {
        if (!file) {
            message.warning('Сначала выберите файл');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await axiosClient.post('/files/', formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
            });

            const uploadedFileData = response.data;
            const fileResponse = await axiosClient.get(`/files/${uploadedFileData.guid}/`);
            const fullUrl = fileResponse.data.reference;

            onPhotoChange(fullUrl);
            localStorage.setItem('pendingProfilePhoto', fullUrl);
            onOk();
        } catch (error) {
            console.error('Ошибка загрузки файла:', error);
            message.error('Не удалось загрузить фото');
            onCancel();
        }
    };

    const handleCancel = () => {
        setPreviewImage(null);
        setFile(null);
        onCancel();
    };

    return (
        <div className="change-photo-popup">
            <div className="change-photo-title">Изменение фотографии профиля</div>
            <div className="photo-upload-area" onClick={handleUploadClick}>
                {previewImage ? (
                    <img
                        src={previewImage}
                        alt="Preview"
                        style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '8px' }}
                    />
                ) : (
                    'Перетащите сюда фото или кликните для выбора'
                )}
            </div>
            <input
                type="file"
                accept="image/*"
                ref={fileInputRef}
                style={{ display: 'none' }}
                onChange={handleFileChange}
            />
            <Button className="cancel-button" onClick={handleCancel}>Отмена</Button>
            <Button className="confirm-button" onClick={handleConfirm}>Подтвердить</Button>
        </div>
    );
};

export default ChangePhotoPopup;
