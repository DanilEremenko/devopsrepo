import React from 'react';
import { Form, Input, Button, Typography, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import SidebarLayout from '../components/SidebarLayout';
import axios from '../api/AxiosClient';
import styles from '../styles/ResetPassword.module.scss';

const { Title, Text } = Typography;

const ResetPassword = () => {
    const navigate = useNavigate();
    const [messageApi, contextHolder] = message.useMessage();

    const onFinish = async (values) => {
        try {
            const response = await axios.post('/auth/reset-password/', {
                login: values.login,
                secret: values.secret,
                newPassword: values.newPassword,
            });

            if (response.status === 200) {
                message.success('Пароль успешно изменён');
                navigate('/login');
            }
        } catch (error) {
            messageApi.error(error.response?.data?.errors[0] || 'Ошибка при смене пароля');
            console.error(error);
        }
    };

    return (
        <SidebarLayout>
            {contextHolder}
            <div className={styles.formHeader}>
                <Title level={1} className={styles.logoText}>BE BETTER</Title>
            </div>
            <Form
                name="reset-password"
                layout="vertical"
                onFinish={onFinish}
                autoComplete="off"
                className={styles.resetForm}
            >
                <div className={styles.description}>
                    <Text type="secondary">
                        Введите логин, секретный код из уведомления и новый пароль.
                    </Text>
                </div>

                <Form.Item
                    name="login"
                    rules={[{ required: true, message: 'Пожалуйста, введите логин!' }]}
                >
                    <Input placeholder="Логин" className={styles.input} />
                </Form.Item>

                <Form.Item
                    name="secret"
                    rules={[{ required: true, message: 'Введите секретный код!' }]}
                >
                    <Input placeholder="Секретный код" className={styles.input} />
                </Form.Item>

                <Form.Item
                    name="newPassword"
                    rules={[{ required: true, message: 'Введите новый пароль!' }]}
                >
                    <Input.Password placeholder="Новый пароль" className={styles.input} />
                </Form.Item>

                <Form.Item>
                    <Button type="primary" htmlType="submit" className={styles.submitBtn}>
                        СМЕНИТЬ ПАРОЛЬ
                    </Button>
                </Form.Item>
            </Form>
        </SidebarLayout>
    );
};

export default ResetPassword;
