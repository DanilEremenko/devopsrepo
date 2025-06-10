import React from 'react';
import { Form, Input, Checkbox, Button, Typography, message } from 'antd';
import SidebarLayout from '../components/SidebarLayout';
import styles from '../styles/Registration.module.scss';
import axiosClient from "../api/AxiosClient";
import { useNavigate } from "react-router-dom";

const { Title, Text } = Typography;

const Registration = () => {
    const [messageApi, contextHolder] = message.useMessage();
    const navigate = useNavigate();

    const onFinish = async (values) => {
        const requestData = {
            email: values.email,
            password: values.password,
            firstName: values.firstName,
            lastName: values.lastName,
            middleName: values.middleName || '',
            company: {
                companyTitle: values.company,
                inn: values.inn,
            },
        };

        try {
            await axiosClient.post('/auth/register/', requestData);
            navigate("/login");
        } catch (error) {
            messageApi.error(error.response?.data?.errors[0] || 'Ошибка при регистрации');
        }
    };

    return (
        <SidebarLayout>
            {contextHolder}
            <div className={styles.formHeader}>
                <Title level={1} className={styles.logoText}>BE BETTER</Title>
                <Text className={styles.subtitle}>Регистрация</Text>
            </div>
            <Form
                name="registration"
                layout="vertical"
                onFinish={onFinish}
                autoComplete="off"
                className={styles.registrationForm}
            >
                <Form.Item
                    name="lastName"
                    rules={[{ required: true, message: 'Пожалуйста, введите фамилию!' }]}
                >
                    <Input placeholder="Введите фамилию" className={styles.inputCustom} />
                </Form.Item>

                <Form.Item
                    name="firstName"
                    rules={[{ required: true, message: 'Пожалуйста, введите имя!' }]}
                >
                    <Input placeholder="Введите имя" className={styles.inputCustom} />
                </Form.Item>

                <Form.Item name="middleName">
                    <Input placeholder="Введите отчество" className={styles.inputCustom} />
                </Form.Item>

                <Form.Item
                    name="company"
                    rules={[{ required: true, message: 'Пожалуйста, введите название компании!' }]}
                >
                    <Input placeholder="Введите название компании" className={styles.inputCustom} />
                </Form.Item>

                <Form.Item
                    name="inn"
                    rules={[{ required: true, message: 'Пожалуйста, введите ИНН!' }]}
                >
                    <Input placeholder="Введите ИНН" className={styles.inputCustom} />
                </Form.Item>

                <Form.Item
                    name="email"
                    rules={[
                        { required: true, message: 'Пожалуйста, введите email!' },
                        { type: 'email', message: 'Введите корректный email!' },
                    ]}
                >
                    <Input placeholder="Введите email" className={styles.inputCustom} />
                </Form.Item>

                <Form.Item
                    name="password"
                    rules={[{ required: true, message: 'Пожалуйста, введите пароль!' }]}
                >
                    <Input.Password placeholder="Введите пароль" className={styles.inputCustom} />
                </Form.Item>

                <Form.Item
                    name="consent"
                    valuePropName="checked"
                    initialValue={true}
                    rules={[
                        {
                            validator: (_, value) =>
                                value ? Promise.resolve() : Promise.reject('Необходимо согласие'),
                        },
                    ]}
                >
                    <Checkbox className={styles.checkboxCustom}>
                        Я ознакомлен(-а) с <a href="/agreement.pdf">политикой конфиденциальности</a>
                    </Checkbox>
                </Form.Item>

                <Form.Item>
                    <Button type="primary" htmlType="submit" block className={styles.submitBtn}>
                        ЗАРЕГИСТРИРОВАТЬСЯ
                    </Button>
                </Form.Item>

                <Form.Item style={{ textAlign: 'center' }}>
                    <Text type="secondary">
                        Уже зарегистрированы? <a href="/login">Войти</a>
                    </Text>
                </Form.Item>
            </Form>
        </SidebarLayout>
    );
};

export default Registration;
