import axiosClient from '../../api/AxiosClient';
import tokenService from '../../service/TokenService';

jest.mock('../../api/AxiosClient');
jest.mock('../../service/TokenService');

const loginUser = async (values, onSuccess, onError) => {
    try {
        const response = await axiosClient.post('/auth/login/', {
            login: values.email,
            password: values.password,
        });

        const { accessToken, refreshToken } = response.data;
        tokenService.setTokens({ accessToken, refreshToken });
        onSuccess();
    } catch (error) {
        const msg = error.response?.data || 'Ошибка входа';
        onError(typeof msg === 'string' ? msg : JSON.stringify(msg));
    }
};

describe('loginUser', () => {
    const mockValues = {
        email: 'test@example.com',
        password: 'Qwerty123',
    };

    it('вызов onSuccess и сохраняет токены', async () => {
        const mockTokens = {
            accessToken: 'mock-access-token',
            refreshToken: 'mock-refresh-token',
        };

        axiosClient.post.mockResolvedValueOnce({ data: mockTokens });

        const onSuccess = jest.fn();
        const onError = jest.fn();

        await loginUser(mockValues, onSuccess, onError);

        expect(axiosClient.post).toHaveBeenCalledWith('/auth/login/', {
            login: 'test@example.com',
            password: 'Qwerty123',
        });

        expect(tokenService.setTokens).toHaveBeenCalledWith(mockTokens);
        expect(onSuccess).toHaveBeenCalled();
        expect(onError).not.toHaveBeenCalled();
    });

    it('onError ошибка авторизации', async () => {
        axiosClient.post.mockRejectedValueOnce({
            response: { data: 'Неверный логин или пароль' },
        });

        const onSuccess = jest.fn();
        const onError = jest.fn();

        await loginUser(mockValues, onSuccess, onError);

        expect(onSuccess).not.toHaveBeenCalled();
        expect(onError).toHaveBeenCalledWith('Неверный логин или пароль');
    });
});
