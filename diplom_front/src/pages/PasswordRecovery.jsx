import React from 'react';
import { Form, Input, Button, Typography, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import SidebarLayout from '../components/SidebarLayout';
import axios from '../api/AxiosClient';
import '../styles/ForgotPassword.css';

const { Title, Text } = Typography;

const PasswordRecovery = () => {
    const navigate = useNavigate();
    const [messageApi, contextHolder] = message.useMessage();

    const onFinish = async (values) => {
        try {
            const response = await axios.post('/auth/password-recovery/', {
                login: values.email,
            });
            message.success('Ссылка для восстановления пароля отправлена!');
            navigate('/reset-password');
        } catch (error) {
            messageApi.error(error.response?.data?.errors[0] || 'Ошибка при отправке ссылки');
            console.error(error);
            message.error('Ошибка при отправке ссылки');
        }
    };

    return (
        <SidebarLayout>
            {contextHolder}
            <div className="form-header">
                <Title level={1} className="logo-text">
                    BE BETTER
                </Title>
            </div>
            <Form
                name="forgot-password"
                layout="vertical"
                onFinish={onFinish}
                autoComplete="off"
                className="forgot-password-form"
            >
                <div className="description">
                    <Text type="secondary">
                        Введите адрес электронной почты, привязанный к аккаунту, и мы отправим вам ссылку для восстановления пароля
                    </Text>
                </div>

                <Form.Item
                    name="email"
                    rules={[
                        { required: true, message: 'Пожалуйста, введите email!' },
                        { type: 'email', message: 'Введите корректный email!' },
                    ]}
                >
                    <Input placeholder="  Электронная почта" className="input-custom" />
                </Form.Item>

                <Form.Item>
                    <Button type="primary" htmlType="submit" block className="submit-btn">
                        ОТПРАВИТЬ
                    </Button>
                </Form.Item>

                <Form.Item>
                    <Text style={{ textAlign: 'left', marginRight: 220 }} type="secondary">
                        <a href="/registration">Регистрация</a>
                    </Text>
                    <Text style={{ textAlign: 'right' }} type="secondary">
                        <a href="/login">Войти</a>
                    </Text>
                </Form.Item>
            </Form>
        </SidebarLayout>
    );
};

export default PasswordRecovery;
