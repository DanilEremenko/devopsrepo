import React from 'react';
import { Form, Input, Button, Typography, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import SidebarLayout from '../components/SidebarLayout';
import '../styles/Login.css';
import axiosClient from '../api/AxiosClient';
import tokenService from '../service/TokenService';

const { Title, Text } = Typography;

const Login = () => {
    const navigate = useNavigate();

    const [messageApi, contextHolder] = message.useMessage();

    const onFinish = async (values) => {
        try {
            const response = await axiosClient.post('/auth/login/', {
                login: values.email,
                password: values.password,
            });

            const { accessToken, refreshToken } = response.data;

            tokenService.setTokens({ accessToken, refreshToken });
            message.success('Вы успешно вошли!');
            navigate('/dashboard');
        } catch (error) {
            messageApi.error(error.response?.data?.errors[0] || 'Ошибка при входе');
            const msg = error.response?.data || 'Ошибка входа';
            message.error(typeof msg === 'string' ? msg : JSON.stringify(msg));
        }
    };

    return (
        <SidebarLayout>
            {contextHolder}
            <div className="form-header">
                <Title level={1} className="logo-text">BE BETTER</Title>
            </div>
            <Form
                name="login"
                layout="vertical"
                onFinish={onFinish}
                autoComplete="off"
                className="login-form"
            >
                <Form.Item
                    name="email"
                    rules={[{ required: true, message: 'Пожалуйста, введите email!' },
                        { type: 'email', message: 'Введите корректный email!' }]}
                >
                    <Input placeholder="Электронная почта" className="input-custom-login" />
                </Form.Item>

                <Form.Item
                    name="password"
                    rules={[{ required: true, message: 'Пожалуйста, введите пароль!' }]}
                >
                    <Input.Password className="input-custom-login" placeholder="Пароль" />
                </Form.Item>

                <Form.Item>
                    <Button type="primary" htmlType="submit" block className="submit-btn">
                        ВОЙТИ
                    </Button>
                </Form.Item>

                <Form.Item>
                    <div className="link-row">
                        <Text type="secondary">
                            <a href="/password-recovery">Забыли пароль?</a>
                        </Text>
                        <Text type="secondary">
                            <a href="/registration">Регистрация</a>
                        </Text>
                    </div>
                </Form.Item>

            </Form>
        </SidebarLayout>
    );
};

export default Login;
