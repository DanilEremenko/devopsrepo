import axiosClient from '../../api/AxiosClient';

jest.mock('../../api/AxiosClient');

const registerUser = async (values, onSuccess, onError) => {
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
        onSuccess();
    } catch (error) {
        const message = error?.response?.data?.errors?.[0] || 'Ошибка при регистрации';
        onError(message);
    }
};

describe('registerUser', () => {
    const mockValues = {
        email: 'test@example.com',
        password: 'Qwerty123**!',
        firstName: 'Иван',
        lastName: 'Иванов',
        middleName: '',
        company: 'ООО Рога и Копыта',
        inn: '1234567890',
    };

    it('onSuccess', async () => {
        axiosClient.post.mockResolvedValueOnce({});

        const onSuccess = jest.fn();
        const onError = jest.fn();

        await registerUser(mockValues, onSuccess, onError);

        expect(axiosClient.post).toHaveBeenCalledWith('/auth/register/', {
            email: 'test@example.com',
            password: 'Qwerty123**!',
            firstName: 'Иван',
            lastName: 'Иванов',
            middleName: '',
            company: {
                companyTitle: 'ООО Рога и Копыта',
                inn: '1234567890',
            },
        });

        expect(onSuccess).toHaveBeenCalled();
        expect(onError).not.toHaveBeenCalled();
    });

    it('onError с сообщением при ошибке регистрации', async () => {
        axiosClient.post.mockRejectedValueOnce({
            response: {
                data: {
                    errors: ['Email уже зарегистрирован'],
                },
            },
        });

        const onSuccess = jest.fn();
        const onError = jest.fn();

        await registerUser(mockValues, onSuccess, onError);

        expect(onSuccess).not.toHaveBeenCalled();
        expect(onError).toHaveBeenCalledWith('Email уже зарегистрирован');
    });
});
