import React from 'react';
import { Form, Input, Button, Typography, message } from 'antd';
import BigFooter from '../components/Bigfooter';
import axiosClient from '../api/AxiosClient';
import { useNavigate } from 'react-router-dom';
import '../styles/ChangePassword.css';

const { Title } = Typography;

const ChangePassword = () => {
    const navigate = useNavigate();
    const [messageApi, contextHolder] = message.useMessage();

    const onFinish = async (values) => {
        const { oldPassword, newPassword, confirmPassword } = values;

        if (newPassword !== confirmPassword) {
            message.error('Пароли не совпадают!');
            return;
        }

        try {
            const response = await axiosClient.post('auth/change-password/', {
                oldPassword,
                newPassword,
            });


            if (response.status === 200) {
                message.success('Пароль успешно изменён!');
                navigate('/login');
            } else {
                message.error('Что-то пошло не так при смене пароля');
            }
        } catch (error) {
            messageApi.error(error.response?.data?.errors[0] || 'Ошибка при изменении пароля');
            console.error('Ошибка:', error);
            message.error('Ошибка при смене пароля');
        }
    };

    return (
        <div className="password-recovery-container">
            {contextHolder}
            <div className="form-section">
                <div className="form-header">
                    <Title level={1} className="logo-text">
                        BE BETTER
                    </Title>
                </div>
                <Form
                    name="password-recovery"
                    layout="vertical"
                    onFinish={onFinish}
                    autoComplete="off"
                    className="password-recovery-form"
                >
                    <Form.Item
                        name="oldPassword"
                        rules={[{ required: true, message: 'Пожалуйста, введите старый пароль!' }]}
                    >
                        <Input.Password placeholder="Старый пароль" className="input-custom" />
                    </Form.Item>

                    <Form.Item
                        name="newPassword"
                        rules={[{ required: true, message: 'Пожалуйста, введите новый пароль!' }]}
                    >
                        <Input.Password placeholder="Новый пароль" className="input-custom" />
                    </Form.Item>

                    <Form.Item
                        name="confirmPassword"
                        rules={[{ required: true, message: 'Пожалуйста, повторите новый пароль!' }]}
                    >
                        <Input.Password placeholder="Повторите новый пароль" className="input-custom" />
                    </Form.Item>

                    <Form.Item>
                        <Button type="primary" htmlType="submit" block className="submit-btn">
                            ОТПРАВИТЬ
                        </Button>
                    </Form.Item>
                </Form>
            </div>
            <BigFooter />
        </div>
    );
};

export default ChangePassword;
